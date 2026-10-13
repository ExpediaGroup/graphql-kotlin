---
id: framework-comparison
title: GraphQL Frameworks Comparison
---

## GraphQL Java
[graphql-java](https://graphql-java.com/) is one of the most popular JVM based GraphQL implemenations. GraphQL Kotlin is
built on top of `grahpql-java` as it can be easily extended with additional functionality and this implementation
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

```kotlin
// Internal DB class, not schema class
class Book(
    val id: ID,
    val name: String,
    val totalPages: Int, // This needs to be renamed to pageCount
    val authorId: ID // This is not in the schema
)

// Internal DB class, not schema class
class Author(
    val id: ID,
    val firstName: String,
    val lastName: String
)

class GraphQLDataFetchers {
    private val books: List<Book> = booksFromDB()
    private val authors: List<Author> = authorsFromDB()

    fun getBookByIdDataFetcher() = DataFetcher { dataFetchingEnvironment ->
        val bookId: String = dataFetchingEnvironment.getArgument("id")
        return books.firstOrNull { it.id == bookId }
    }

    fun getAuthorDataFetcher() = DataFetcher { dataFetchingEnvironment ->
        val book: Book = dataFetchingEnvironment.getSource() as Book
        return authors.firstOrNull { it.id == book.authorId }
    }

    fun getPageCountDataFetcher() = DataFetcher { dataFetchingEnvironment ->
        val book: Book = dataFetchingEnvironment.getSource() as Book
        return book.totalPages
    }
}

val schemaParser = SchemaParser()
val schemaGenerator = SchemaGenerator()
val schemaFile = loadSchema("schema.graphqls")
val typeRegistry = schemaParser.parse(schemaFile)
val graphQLDataFetchers = GraphQLDataFetchers()

val runtimeWiring = RuntimeWiring.newRuntimeWiring()
    .type(
        newTypeWiring("Query")
            .dataFetcher("bookById", graphQLDataFetchers.getBookByIdDataFetcher())
    )
    .type(
        newTypeWiring("Book")
            .dataFetcher("author", graphQLDataFetchers.getAuthorDataFetcher())
            .dataFetcher("pageCount", graphQLDataFetchers.getPageCountDataFetcher())
    )
    .build()

// Combine the types and runtime code together to make a schema
val graphQLSchema: GraphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring)
```

This means that there are two sources of truth for your schema and changes in either have to be reflected in both locations.
As your schema scales to hundreds of types and many different resolvers, it can get more difficult to track what code needs to be changed if you want to add a new field,
deprecate or delete an existing one, or fix a bug in the resolver code.

These errors will hopefully be caught by your build or automated tests, but it is another layer your have to be worried about when creating your API.

### GraphQL Kotlin Schema

`graphql-kotlin-schema-generator` aims to simplify this process by using Kotlin reflection to generate the schema for you.
All you need to do is write your schema code in a Kotlin class with public functions or properties.

```kotlin
private val books: List<Book> = booksFromDB()
private val authors: List<Author> = authorsFromDB()

class Query {
    fun bookById(id: ID): Book? = books.find { it.id == id }
}

class Book(
    val id: ID,
    val name: String,
    private val totalPages: Int,
    private val authorId: ID
) {
    fun author(): Author? = authors.find { it.id == authorId }
    fun pageCount(): Int = totalPages
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


## DGS
[DGS](https://netflix.github.io/dgs/) is a GraphQL server framework for Spring Boot. It works with both Java and Kotlin.
DGS is also built on top of `graphql-java` and implements many similar features to `graphql-kotlin` and [graphql-java-kickstart/graphql-spring-boot](https://github.com/graphql-java-kickstart/graphql-spring-boot).

* Auto-configuration of server routes and request handling
* Auto-wiring of data fetchers (resolvers) to the `GraphQLSchema`
* Apollo Federation support
* Subscriptions support
* Client schema-code generation

While both libraries do very similar things, there are some minor differences which may serve different usecases better.
As with open source library, you can compare and use the right tool for the job.

### Extra Features of DGS

* Support for a SDL-First (Schema-First) approach
* Ability to autogenerate code stubs from the schema
* Includes [JsonPath](https://github.com/json-path/JsonPath) testing library
* Build on top of Spring MVC

### Extra Features of graphql-kotlin

* Supports code-first approach (generates schema from source code - does not require duplicate implementation of data fetchers, schema classes, and SDL files)
* Abstract server logic can be used in any framework, e.g. Ktor
* Reference server implementation build on top of [Spring Webflux](https://spring.io/reactive) for a reactive server stack
* Simple nesting of data fetchers
* Client code generation for Ktor and Spring
* Client plugin support for both Maven and Gradle
