"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[7583],{65985:(e,a,n)=>{n.r(a),n.d(a,{assets:()=>h,contentTitle:()=>s,default:()=>d,frontMatter:()=>o,metadata:()=>i,toc:()=>c});var t=n(74848),r=n(28453);const o={id:"graphql-java-comparison",title:"GraphQL Java Comparison"},s=void 0,i={id:"graphql-java-comparison",title:"GraphQL Java Comparison",description:"graphql-java is one of the most popular JVM based GraphQL implemenations. GraphQL Kotlin is",source:"@site/versioned_docs/version-3.x.x/graphql-java-comparison.md",sourceDirName:".",slug:"/graphql-java-comparison",permalink:"/graphql-kotlin/docs/3.x.x/graphql-java-comparison",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-3.x.x/graphql-java-comparison.md",tags:[],version:"3.x.x",lastUpdatedBy:"Dariusz Kuc",lastUpdatedAt:1708623265,formattedLastUpdatedAt:"Feb 22, 2024",frontMatter:{id:"graphql-java-comparison",title:"GraphQL Java Comparison"}},h={},c=[{value:"GraphQL Java Schema",id:"graphql-java-schema",level:3},{value:"GraphQL Kotlin Schema",id:"graphql-kotlin-schema",level:3}];function l(e){const a={a:"a",code:"code",h3:"h3",p:"p",pre:"pre",...(0,r.R)(),...e.components};return(0,t.jsxs)(t.Fragment,{children:[(0,t.jsxs)(a.p,{children:[(0,t.jsx)(a.a,{href:"https://graphql-java.com/",children:"graphql-java"})," is one of the most popular JVM based GraphQL implemenations. GraphQL Kotlin is\nbuilt on top of ",(0,t.jsx)(a.code,{children:"grahpql-java"})," as it can be easily extended with additional functionality and this implementation\nhas been used and tested by many users."]}),"\n",(0,t.jsx)(a.h3,{id:"graphql-java-schema",children:"GraphQL Java Schema"}),"\n",(0,t.jsxs)(a.p,{children:["The most common way to create the schema in ",(0,t.jsx)(a.code,{children:"graphql-java"})," is to first manually write the SDL file:"]}),"\n",(0,t.jsx)(a.pre,{children:(0,t.jsx)(a.code,{className:"language-graphql",children:"\nschema {\n    query: Query\n}\n\ntype Query {\n    bookById(id: ID): Book\n}\n\ntype Book {\n    id: ID!\n    name: String!\n    pageCount: Int!\n    author: Author\n}\n\ntype Author {\n    id: ID!\n    firstName: String!\n    lastName: String!\n}\n\n"})}),"\n",(0,t.jsxs)(a.p,{children:["Then write the runtime code that matches this schema to build the ",(0,t.jsx)(a.code,{children:"GraphQLSchema"})," object."]}),"\n",(0,t.jsx)(a.pre,{children:(0,t.jsx)(a.code,{className:"language-kotlin",children:'\n// Internal DB class, not schema class\nclass Book(\n    val id: ID,\n    val name: String,\n    val totalPages: Int, // This needs to be renamed to pageCount\n    val authorId: ID // This is not in the schema\n)\n\n// Internal DB class, not schema class\nclass Author(\n    val id: ID,\n    val firstName: String,\n    val lastName: String\n)\n\nclass GraphQLDataFetchers {\n    private val books: List<Book> = booksFromDB()\n    private val authors: List<Author> = authorsFromDB()\n\n    fun getBookByIdDataFetcher() = DataFetcher { dataFetchingEnvironment ->\n        val bookId: String = dataFetchingEnvironment.getArgument("id")\n        return books.firstOrNull { it.id == bookId }\n    }\n\n    fun getAuthorDataFetcher() = DataFetcher { dataFetchingEnvironment ->\n        val book: Book = dataFetchingEnvironment.getSource() as Book\n        return authors.firstOrNull { it.id == book.authorId }\n    }\n\n    fun getPageCountDataFetcher() = DataFetcher { dataFetchingEnvironment ->\n        val book: Book = dataFetchingEnvironment.getSource() as Book\n        return book.totalPages\n    }\n}\n\nval schemaParser = SchemaParser()\nval schemaGenerator = SchemaGenerator()\nval schemaFile = loadSchema("schema.graphqls")\nval typeRegistry = schemaParser.parse(schemaFile)\nval graphQLDataFetchers = GraphQLDataFetchers()\n\nval runtimeWiring = RuntimeWiring.newRuntimeWiring()\n    .type(\n        newTypeWiring("Query")\n            .dataFetcher("bookById", graphQLDataFetchers.getBookByIdDataFetcher())\n    )\n    .type(\n        newTypeWiring("Book")\n            .dataFetcher("author", graphQLDataFetchers.getAuthorDataFetcher())\n            .dataFetcher("pageCount", graphQLDataFetchers.getPageCountDataFetcher())\n    )\n    .build()\n\n// Combine the types and runtime code together to make a schema\nval graphQLSchema: GraphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring)\n\n'})}),"\n",(0,t.jsx)(a.p,{children:"This means that there are two sources of truth for your schema and changes in either have to be reflected in both locations.\nAs your schema scales to hundreds of types and many different resolvers, it can get more difficult to track what code needs to be changed if you want to add a new field,\ndeprecate or delete an existing one, or fix a bug in the resolver code."}),"\n",(0,t.jsx)(a.p,{children:"These errors will hopefully be caught by your build or automated tests, but it is another layer your have to be worried about when creating your API."}),"\n",(0,t.jsx)(a.h3,{id:"graphql-kotlin-schema",children:"GraphQL Kotlin Schema"}),"\n",(0,t.jsxs)(a.p,{children:[(0,t.jsx)(a.code,{children:"graphql-kotlin-schema-generator"})," aims to simplify this process by using Kotlin reflection to generate the schema for you.\nAll you need to do is write your schema code in a Kotlin class with public functions or properties."]}),"\n",(0,t.jsx)(a.pre,{children:(0,t.jsx)(a.code,{className:"language-kotlin",children:'\nprivate val books: List<Book> = booksFromDB()\nprivate val authors: List<Author> = authorsFromDB()\n\nclass Query {\n    fun bookById(id: ID): Book? = books.find { it.id == id }\n}\n\nclass Book(\n    val id: ID,\n    val name: String,\n    private val totalPages: Int,\n    private val authorId: ID\n) {\n    fun author(): Author? = authors.find { it.id == authorId }\n    fun pageCount(): Int = totalPages\n}\n\nclass Author(\n    val id: ID,\n    val firstName: String,\n    val lastName: String\n)\n\nval config = SchemaGeneratorConfig(supportedPackages = "com.example")\nval queries = listOf(TopLevelObject(Query()))\nval schema: GraphQLSchema = toSchema(config, queries)\n\n'})}),"\n",(0,t.jsxs)(a.p,{children:["This makes changes in code directly reflect to your schema and you can still produce the ",(0,t.jsx)(a.code,{children:"GraphQLSchema"})," to print and export an SDL file."]})]})}function d(e={}){const{wrapper:a}={...(0,r.R)(),...e.components};return a?(0,t.jsx)(a,{...e,children:(0,t.jsx)(l,{...e})}):l(e)}},28453:(e,a,n)=>{n.d(a,{R:()=>s,x:()=>i});var t=n(96540);const r={},o=t.createContext(r);function s(e){const a=t.useContext(o);return t.useMemo((function(){return"function"==typeof e?e(a):{...a,...e}}),[a,e])}function i(e){let a;return a=e.disableParentContext?"function"==typeof e.components?e.components(r):e.components||r:s(e.components),t.createElement(o.Provider,{value:a},e.children)}}}]);