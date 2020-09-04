---
id: graphql-java-comparison
title: GraphQL Java Comparison
---

[graphql-java](https://graphql-java.com/) is one of the more popular JVM based GraphQL implemenations. GraphQL Kotlin is
built on top of `grahpql-java` as there are many parts of the GraphQL specification that are trivial and this implemenatation
has been used and tested by many users.

### GraphQL Java Schema

The most common way to create the schema in `graphql-java` is to first manually write the SDL file:

```graphql
schema {
    query: Query
}

type Query {
    bookById(id: ID): Book
}

type Book {
    id: ID!
    name: String!
    pageCount: Int!
    author: Author
}

type Author {
    id: ID!
    firstName: String!
    lastName: String!
}
```

Then write the runtime code that matches this schema to build the `GraphQLSchema` object.

```java
public class Schema {

    public static void main(String[] args) {
        SchemaParser schemaParser = new SchemaParser();
        SchemaGenerator schemaGenerator = new SchemaGenerator();

        File schemaFile = loadSchema("schema.graphqls");
        TypeDefinitionRegistry typeRegistry = schemaParser.parse(schemaFile);

        RuntimeWiring runtimeWiring = buildRuntimeWiring();

        GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);
    }

    private RuntimeWiring buildRuntimeWiring() {
        return RuntimeWiring.newRuntimeWiring()
            .type(
                newTypeWiring("Query")
                    .dataFetcher("bookById", graphQLDataFetchers.getBookByIdDataFetcher())
            )
            .type(
                newTypeWiring("Book")
                    .dataFetcher("author", graphQLDataFetchers.getAuthorDataFetcher())
                    .dataFetcher("pageCount", graphQLDataFetchers.getPageCountDataFetcher())
            )
            .build();
    }
}

public class GraphQLDataFetchers {

    private static List<Map<String, String>> books = booksFromDB();
    private static List<Map<String, String>> authors = authorsFromDB();

    public DataFetcher getBookByIdDataFetcher() {
        return dataFetchingEnvironment -> {
            String bookId = dataFetchingEnvironment.getArgument("id");
            return books
                    .stream()
                    .filter(book -> book.get("id").equals(bookId))
                    .findFirst()
                    .orElse(null);
        };
    }

    public DataFetcher getAuthorDataFetcher() {
        return dataFetchingEnvironment -> {
            Map<String,String> book = dataFetchingEnvironment.getSource();
            String authorId = book.get("authorId");
            return authors
                    .stream()
                    .filter(author -> author.get("id").equals(authorId))
                    .findFirst()
                    .orElse(null);
        };
    }

    public DataFetcher getPageCountDataFetcher() {
        return dataFetchingEnvironment -> {
            Map<String,String> book = dataFetchingEnvironment.getSource();
            return book.get("totalPages");
        };
    }
}
```

This means that there are two sources-of-truth for your schema and changes in either have to be reflected in both locations.
As your schema scales to hundreds of types and many different resolvers, it can get more difficult to track what code needs to be changed if you want to add a new field,
deprecated or delete an existing one, or fix a bug in the resolver code.

These errors will most likely be caught by your build or automated tests, but it is another layer your have to be worried about when creating your API.

### GraphQL Kotlin Schema

`graphql-kotlin-schema-generator` aims to simplify this process by using Kotlin reflection to generate the schema for you.
All you need to do is write your schema code in a Kotlin class with public functions or properties.

```kotlin
class Query {
    private val books: List<Book> = booksFromDB()

    fun bookById(id: ID): Book? = books.find { it.id == id }
}

class Book(
    val id: ID,
    val name: String,
    val pageCount: Int,
    private val authorId: ID
) {
    private val authors: List<Author> = authorsFromDB()

    fun author(): Author? = authors.find { it.id == authorId }
}

class Author(
    val id: ID,
    val firstName: String,
    val lastName: String
)

val config = SchemaGeneratorConfig(supportedPackages = "com.example")
val queries = listOf(TopLevelObject(Query()))
val schema: GraphQLSchema = toSchema(config, queries)
```

This makes changes in code directly reflect to your schema and you can still produce the `GraphQLSchema` to print and export an SDL file.
