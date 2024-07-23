"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[3532],{84274:(e,n,a)=>{a.r(n),a.d(n,{assets:()=>l,contentTitle:()=>i,default:()=>d,frontMatter:()=>o,metadata:()=>s,toc:()=>h});var t=a(74848),r=a(28453);const o={id:"framework-comparison",title:"GraphQL Frameworks Comparison"},i=void 0,s={id:"framework-comparison",title:"GraphQL Frameworks Comparison",description:"GraphQL Java",source:"@site/versioned_docs/version-5.x.x/framework-comparison.md",sourceDirName:".",slug:"/framework-comparison",permalink:"/graphql-kotlin/docs/5.x.x/framework-comparison",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-5.x.x/framework-comparison.md",tags:[],version:"5.x.x",lastUpdatedBy:"mykevinjung",lastUpdatedAt:1721694712e3,frontMatter:{id:"framework-comparison",title:"GraphQL Frameworks Comparison"},sidebar:"docs",previous:{title:"Examples",permalink:"/graphql-kotlin/docs/5.x.x/examples"},next:{title:"Blogs & Videos",permalink:"/graphql-kotlin/docs/5.x.x/blogs-and-videos"}},l={},h=[{value:"GraphQL Java",id:"graphql-java",level:2},{value:"GraphQL Java Schema",id:"graphql-java-schema",level:3},{value:"GraphQL Kotlin Schema",id:"graphql-kotlin-schema",level:3},{value:"DGS",id:"dgs",level:2},{value:"Extra Features of DGS",id:"extra-features-of-dgs",level:3},{value:"Extra Features of graphql-kotlin",id:"extra-features-of-graphql-kotlin",level:3}];function c(e){const n={a:"a",code:"code",h2:"h2",h3:"h3",li:"li",p:"p",pre:"pre",ul:"ul",...(0,r.R)(),...e.components};return(0,t.jsxs)(t.Fragment,{children:[(0,t.jsx)(n.h2,{id:"graphql-java",children:"GraphQL Java"}),"\n",(0,t.jsxs)(n.p,{children:[(0,t.jsx)(n.a,{href:"https://graphql-java.com/",children:"graphql-java"})," is one of the most popular JVM based GraphQL implemenations. GraphQL Kotlin is\nbuilt on top of ",(0,t.jsx)(n.code,{children:"grahpql-java"})," as it can be easily extended with additional functionality and this implementation\nhas been used and tested by many users."]}),"\n",(0,t.jsx)(n.h3,{id:"graphql-java-schema",children:"GraphQL Java Schema"}),"\n",(0,t.jsxs)(n.p,{children:["The most common way to create the schema in ",(0,t.jsx)(n.code,{children:"graphql-java"})," is to first manually write the SDL file:"]}),"\n",(0,t.jsx)(n.pre,{children:(0,t.jsx)(n.code,{className:"language-graphql",children:"schema {\n    query: Query\n}\n\ntype Query {\n    bookById(id: ID): Book\n}\n\ntype Book {\n    id: ID!\n    name: String!\n    pageCount: Int!\n    author: Author\n}\n\ntype Author {\n    id: ID!\n    firstName: String!\n    lastName: String!\n}\n"})}),"\n",(0,t.jsxs)(n.p,{children:["Then write the runtime code that matches this schema to build the ",(0,t.jsx)(n.code,{children:"GraphQLSchema"})," object."]}),"\n",(0,t.jsx)(n.pre,{children:(0,t.jsx)(n.code,{className:"language-kotlin",children:'// Internal DB class, not schema class\nclass Book(\n    val id: ID,\n    val name: String,\n    val totalPages: Int, // This needs to be renamed to pageCount\n    val authorId: ID // This is not in the schema\n)\n\n// Internal DB class, not schema class\nclass Author(\n    val id: ID,\n    val firstName: String,\n    val lastName: String\n)\n\nclass GraphQLDataFetchers {\n    private val books: List<Book> = booksFromDB()\n    private val authors: List<Author> = authorsFromDB()\n\n    fun getBookByIdDataFetcher() = DataFetcher { dataFetchingEnvironment ->\n        val bookId: String = dataFetchingEnvironment.getArgument("id")\n        return books.firstOrNull { it.id == bookId }\n    }\n\n    fun getAuthorDataFetcher() = DataFetcher { dataFetchingEnvironment ->\n        val book: Book = dataFetchingEnvironment.getSource() as Book\n        return authors.firstOrNull { it.id == book.authorId }\n    }\n\n    fun getPageCountDataFetcher() = DataFetcher { dataFetchingEnvironment ->\n        val book: Book = dataFetchingEnvironment.getSource() as Book\n        return book.totalPages\n    }\n}\n\nval schemaParser = SchemaParser()\nval schemaGenerator = SchemaGenerator()\nval schemaFile = loadSchema("schema.graphqls")\nval typeRegistry = schemaParser.parse(schemaFile)\nval graphQLDataFetchers = GraphQLDataFetchers()\n\nval runtimeWiring = RuntimeWiring.newRuntimeWiring()\n    .type(\n        newTypeWiring("Query")\n            .dataFetcher("bookById", graphQLDataFetchers.getBookByIdDataFetcher())\n    )\n    .type(\n        newTypeWiring("Book")\n            .dataFetcher("author", graphQLDataFetchers.getAuthorDataFetcher())\n            .dataFetcher("pageCount", graphQLDataFetchers.getPageCountDataFetcher())\n    )\n    .build()\n\n// Combine the types and runtime code together to make a schema\nval graphQLSchema: GraphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring)\n'})}),"\n",(0,t.jsx)(n.p,{children:"This means that there are two sources of truth for your schema and changes in either have to be reflected in both locations.\nAs your schema scales to hundreds of types and many different resolvers, it can get more difficult to track what code needs to be changed if you want to add a new field,\ndeprecate or delete an existing one, or fix a bug in the resolver code."}),"\n",(0,t.jsx)(n.p,{children:"These errors will hopefully be caught by your build or automated tests, but it is another layer your have to be worried about when creating your API."}),"\n",(0,t.jsx)(n.h3,{id:"graphql-kotlin-schema",children:"GraphQL Kotlin Schema"}),"\n",(0,t.jsxs)(n.p,{children:[(0,t.jsx)(n.code,{children:"graphql-kotlin-schema-generator"})," aims to simplify this process by using Kotlin reflection to generate the schema for you.\nAll you need to do is write your schema code in a Kotlin class with public functions or properties."]}),"\n",(0,t.jsx)(n.pre,{children:(0,t.jsx)(n.code,{className:"language-kotlin",children:'private val books: List<Book> = booksFromDB()\nprivate val authors: List<Author> = authorsFromDB()\n\nclass Query {\n    fun bookById(id: ID): Book? = books.find { it.id == id }\n}\n\nclass Book(\n    val id: ID,\n    val name: String,\n    private val totalPages: Int,\n    private val authorId: ID\n) {\n    fun author(): Author? = authors.find { it.id == authorId }\n    fun pageCount(): Int = totalPages\n}\n\nclass Author(\n    val id: ID,\n    val firstName: String,\n    val lastName: String\n)\n\nval config = SchemaGeneratorConfig(supportedPackages = "com.example")\nval queries = listOf(TopLevelObject(Query()))\nval schema: GraphQLSchema = toSchema(config, queries)\n'})}),"\n",(0,t.jsxs)(n.p,{children:["This makes changes in code directly reflect to your schema and you can still produce the ",(0,t.jsx)(n.code,{children:"GraphQLSchema"})," to print and export an SDL file."]}),"\n",(0,t.jsx)(n.h2,{id:"dgs",children:"DGS"}),"\n",(0,t.jsxs)(n.p,{children:[(0,t.jsx)(n.a,{href:"https://netflix.github.io/dgs/",children:"DGS"})," is a GraphQL server framework for Spring Boot. It works with both Java and Kotlin.\nDGS is also built on top of ",(0,t.jsx)(n.code,{children:"graphql-java"})," and implements many similar features to ",(0,t.jsx)(n.code,{children:"graphql-kotlin"})," and ",(0,t.jsx)(n.a,{href:"https://github.com/graphql-java-kickstart/graphql-spring-boot",children:"graphql-java-kickstart/graphql-spring-boot"}),"."]}),"\n",(0,t.jsxs)(n.ul,{children:["\n",(0,t.jsx)(n.li,{children:"Auto-configuration of server routes and request handling"}),"\n",(0,t.jsxs)(n.li,{children:["Auto-wiring of data fetchers (resolvers) to the ",(0,t.jsx)(n.code,{children:"GraphQLSchema"})]}),"\n",(0,t.jsx)(n.li,{children:"Apollo Federation support"}),"\n",(0,t.jsx)(n.li,{children:"Subscriptions support"}),"\n",(0,t.jsx)(n.li,{children:"Client schema-code generation"}),"\n"]}),"\n",(0,t.jsx)(n.p,{children:"While both libraries do very similar things, there are some minor differences which may serve different usecases better.\nAs with open source library, you can compare and use the right tool for the job."}),"\n",(0,t.jsx)(n.h3,{id:"extra-features-of-dgs",children:"Extra Features of DGS"}),"\n",(0,t.jsxs)(n.ul,{children:["\n",(0,t.jsx)(n.li,{children:"Support for a SDL-First (Schema-First) approach"}),"\n",(0,t.jsx)(n.li,{children:"Ability to autogenerate code stubs from the schema"}),"\n",(0,t.jsxs)(n.li,{children:["Includes ",(0,t.jsx)(n.a,{href:"https://github.com/json-path/JsonPath",children:"JsonPath"})," testing library"]}),"\n",(0,t.jsx)(n.li,{children:"Build on top of Spring MVC"}),"\n"]}),"\n",(0,t.jsx)(n.h3,{id:"extra-features-of-graphql-kotlin",children:"Extra Features of graphql-kotlin"}),"\n",(0,t.jsxs)(n.ul,{children:["\n",(0,t.jsx)(n.li,{children:"Supports code-first approach (generates schema from source code - does not require duplicate implementation of data fetchers, schema classes, and SDL files)"}),"\n",(0,t.jsx)(n.li,{children:"Abstract server logic can be used in any framework, e.g. Ktor"}),"\n",(0,t.jsxs)(n.li,{children:["Reference server implementation build on top of ",(0,t.jsx)(n.a,{href:"https://spring.io/reactive",children:"Spring Webflux"})," for a reactive server stack"]}),"\n",(0,t.jsx)(n.li,{children:"Simple nesting of data fetchers"}),"\n",(0,t.jsx)(n.li,{children:"Client code generation for Ktor and Spring"}),"\n",(0,t.jsx)(n.li,{children:"Client plugin support for both Maven and Gradle"}),"\n"]})]})}function d(e={}){const{wrapper:n}={...(0,r.R)(),...e.components};return n?(0,t.jsx)(n,{...e,children:(0,t.jsx)(c,{...e})}):c(e)}},28453:(e,n,a)=>{a.d(n,{R:()=>i,x:()=>s});var t=a(96540);const r={},o=t.createContext(r);function i(e){const n=t.useContext(o);return t.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function s(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(r):e.components||r:i(e.components),t.createElement(o.Provider,{value:n},e.children)}}}]);