"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[6431],{82918:(e,n,i)=>{i.r(n),i.d(n,{assets:()=>s,contentTitle:()=>a,default:()=>d,frontMatter:()=>t,metadata:()=>o,toc:()=>c});var r=i(74848),l=i(28453);const t={id:"client-overview",title:"Client Overview",original_id:"client-overview"},a=void 0,o={id:"client/client-overview",title:"Client Overview",description:"graphql-kotlin-client is a lightweight type-safe GraphQL HTTP client. Type-safe data models are generated at build time",source:"@site/versioned_docs/version-3.x.x/client/client-overview.md",sourceDirName:"client",slug:"/client/client-overview",permalink:"/graphql-kotlin/docs/3.x.x/client/client-overview",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-3.x.x/client/client-overview.md",tags:[],version:"3.x.x",lastUpdatedBy:"Curtis Cook",lastUpdatedAt:1712948770,formattedLastUpdatedAt:"Apr 12, 2024",frontMatter:{id:"client-overview",title:"Client Overview",original_id:"client-overview"},sidebar:"docs",previous:{title:"Subscriptions",permalink:"/graphql-kotlin/docs/3.x.x/spring-server/subscriptions"},next:{title:"Client Features",permalink:"/graphql-kotlin/docs/3.x.x/client/client-features"}},s={},c=[{value:"Project Configuration",id:"project-configuration",level:2},{value:"Generating GraphQL Client",id:"generating-graphql-client",level:2},{value:"Executing Queries",id:"executing-queries",level:2}];function p(e){const n={a:"a",code:"code",h2:"h2",p:"p",pre:"pre",...(0,l.R)(),...e.components};return(0,r.jsxs)(r.Fragment,{children:[(0,r.jsxs)(n.p,{children:[(0,r.jsx)(n.code,{children:"graphql-kotlin-client"})," is a lightweight type-safe GraphQL HTTP client. Type-safe data models are generated at build time\nby the GraphQL Kotlin ",(0,r.jsx)(n.a,{href:"/graphql-kotlin/docs/3.x.x/plugins/gradle-plugin",children:"Gradle"})," and\n",(0,r.jsx)(n.a,{href:"/graphql-kotlin/docs/3.x.x/plugins/maven-plugin",children:"Maven"})," plugins."]}),"\n",(0,r.jsxs)(n.p,{children:[(0,r.jsx)(n.code,{children:"GraphQLClient"})," is a thin wrapper on top of ",(0,r.jsx)(n.a,{href:"https://ktor.io/clients/index.html",children:"Ktor HTTP Client"})," and supports fully\nasynchronous non-blocking communication. It is highly customizable and can be configured with any supported Ktor HTTP\n",(0,r.jsx)(n.a,{href:"https://ktor.io/clients/http-client/engines.html",children:"engine"})," and ",(0,r.jsx)(n.a,{href:"https://ktor.io/clients/http-client/features.html",children:"features"}),"."]}),"\n",(0,r.jsx)(n.h2,{id:"project-configuration",children:"Project Configuration"}),"\n",(0,r.jsxs)(n.p,{children:["GraphQL Kotlin provides both Gradle and Maven plugins to automatically generate your client code at build time. Once\nyour data classes are generated, you can then execute their underlying GraphQL operations using ",(0,r.jsx)(n.code,{children:"graphql-kotlin-client"}),"\nruntime dependency."]}),"\n",(0,r.jsxs)(n.p,{children:["Basic ",(0,r.jsx)(n.code,{children:"build.gradle.kts"})," Gradle configuration:"]}),"\n",(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-kotlin",children:'\nimport com.expediagroup.graphql.plugin.gradle.graphql\n\nplugins {\n    id("com.expediagroup.graphql") version $latestGraphQLKotlinVersion\n}\n\ndependencies {\n  implementation("com.expediagroup:graphql-kotlin-client:$latestGraphQLKotlinVersion")\n}\n\ngraphql {\n    client {\n        endpoint = "http://localhost:8080/graphql"\n        packageName = "com.example.generated"\n    }\n}\n\n'})}),"\n",(0,r.jsxs)(n.p,{children:["Equivalent ",(0,r.jsx)(n.code,{children:"pom.xml"})," Maven configuration"]}),"\n",(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-xml",children:'\n<?xml version="1.0" encoding="UTF-8"?>\n<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">\n    <modelVersion>4.0.0</modelVersion>\n\n    <groupId>com.example</groupId>\n    <artifactId>graphql-kotlin-maven-client-example</artifactId>\n    <version>1.0.0-SNAPSHOT</version>\n\n    <properties>\n        <graphql-kotlin.version>$latestGraphQLKotlinVersion</graphql-kotlin.version>\n    </properties>\n\n    <dependencies>\n        <dependency>\n            <groupId>com.expediagroup</groupId>\n            <artifactId>graphql-kotlin-client</artifactId>\n            <version>${graphql-kotlin.version}</version>\n        </dependency>\n    </dependencies>\n\n    <build>\n        <plugins>\n            <plugin>\n                <groupId>com.expediagroup</groupId>\n                <artifactId>graphql-kotlin-maven-plugin</artifactId>\n                <version>${graphql-kotlin.version}</version>\n                <executions>\n                    <execution>\n                        <id>generate-graphql-client</id>\n                        <goals>\n                            <goal>introspect-schema</goal>\n                            <goal>generate-client</goal>\n                        </goals>\n                        <configuration>\n                            <endpoint>http://localhost:8080/graphql</endpoint>\n                            <packageName>com.example.generated</packageName>\n                            <schemaFile>${project.build.directory}/schema.graphql</schemaFile>\n                        </configuration>\n                    </execution>\n                </executions>\n            </plugin>\n        </plugins>\n    </build>\n</project>\n\n'})}),"\n",(0,r.jsxs)(n.p,{children:["See ",(0,r.jsx)(n.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/tree/3.x.x/examples/client",children:"graphql-kotlin-client-example"})," project for complete\nworking examples of Gradle and Maven based projects."]}),"\n",(0,r.jsx)(n.h2,{id:"generating-graphql-client",children:"Generating GraphQL Client"}),"\n",(0,r.jsxs)(n.p,{children:["By default, GraphQL Kotlin build plugins will attempt to generate GraphQL clients from all ",(0,r.jsx)(n.code,{children:"*.graphql"})," files located under\n",(0,r.jsx)(n.code,{children:"src/main/resources"}),". Queries are validated against the target GraphQL schema, which can be manually provided, retrieved by\nthe plugins through introspection (as configured in examples above) or downloaded directly from a custom SDL endpoint.\nSee our documentation for more details on supported ",(0,r.jsx)(n.a,{href:"/graphql-kotlin/docs/3.x.x/plugins/gradle-plugin",children:"Gradle tasks"}),"\nand ",(0,r.jsx)(n.a,{href:"/graphql-kotlin/docs/3.x.x/plugins/maven-plugin",children:"Maven Mojos"}),"."]}),"\n",(0,r.jsxs)(n.p,{children:["When creating your GraphQL queries make sure to always specify an operation name and name the files accordingly. Each\none of your query files will generate a corresponding Kotlin file with a class matching your operation\nname that will act as a wrapper for all corresponding data classes. For example, given ",(0,r.jsx)(n.code,{children:"HelloWorldQuery.graphql"})," with\n",(0,r.jsx)(n.code,{children:"HelloWorldQuery"})," as the operation name, GraphQL Kotlin plugins will generate a corresponding ",(0,r.jsx)(n.code,{children:"HelloWorldQuery.kt"})," file\nwith a ",(0,r.jsx)(n.code,{children:"HelloWorldQuery"})," class under the configured package."]}),"\n",(0,r.jsx)(n.p,{children:"For example, given a simple schema"}),"\n",(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-graphql",children:"\ntype Query {\n  helloWorld: String\n}\n\n"})}),"\n",(0,r.jsxs)(n.p,{children:["And a corresponding ",(0,r.jsx)(n.code,{children:"HelloWorldQuery.graphql"})," query"]}),"\n",(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-graphql",children:"\nquery HelloWorldQuery {\n  helloWorld\n}\n\n"})}),"\n",(0,r.jsx)(n.p,{children:"Plugins will generate following client code"}),"\n",(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-kotlin",children:'\npackage com.example.generated\n\nimport com.expediagroup.graphql.client.GraphQLClient\nimport com.expediagroup.graphql.types.GraphQLResponse\nimport kotlin.String\n\nconst val HELLO_WORLD_QUERY: String = "query HelloWorldQuery {\\n    helloWorld\\n}"\n\nclass HelloWorldQuery(\n  private val graphQLClient: GraphQLClient\n) {\n  suspend fun execute(): GraphQLResponse<HelloWorldQuery.Result> =\n      graphQLClient.execute(HELLO_WORLD_QUERY, "HelloWorldQuery", null)\n\n  data class Result(\n    val helloWorld: String\n  )\n}\n\n'})}),"\n",(0,r.jsxs)(n.p,{children:["Generated classes requires an instance of ",(0,r.jsx)(n.code,{children:"GraphQLClient"})," and exposes a single ",(0,r.jsx)(n.code,{children:"execute"})," suspendable method that executes\nthe underlying GraphQL operation using the provided client."]}),"\n",(0,r.jsx)(n.h2,{id:"executing-queries",children:"Executing Queries"}),"\n",(0,r.jsxs)(n.p,{children:["Your auto generated classes accept an instance of ",(0,r.jsx)(n.code,{children:"GraphQLClient"})," which is a thin wrapper around Ktor HTTP client that\nensures proper serialization and deserialization of your GraphQL objects. ",(0,r.jsx)(n.code,{children:"GraphQLClient"})," requires target URL to be\nspecified and defaults to fully asynchronous non-blocking ",(0,r.jsx)(n.a,{href:"https://ktor.io/clients/http-client/engines.html#cio",children:"Coroutine-based IO engine"}),"."]}),"\n",(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-kotlin",children:'\npackage com.example.client\n\nimport com.expediagroup.graphql.client.GraphQLClient\nimport com.expediagroup.graphql.generated.HelloWorldQuery\nimport kotlinx.coroutines.runBlocking\nimport java.net.URL\n\nfun main() {\n    val client = GraphQLClient(url = URL("http://localhost:8080/graphql"))\n    val helloWorldQuery = HelloWorldQuery(client)\n    runBlocking {\n        val result = helloWorldQuery.execute()\n        println("hello world query result: ${result.data?.helloWorld}")\n    }\n    client.close()\n}\n\n'})})]})}function d(e={}){const{wrapper:n}={...(0,l.R)(),...e.components};return n?(0,r.jsx)(n,{...e,children:(0,r.jsx)(p,{...e})}):p(e)}},28453:(e,n,i)=>{i.d(n,{R:()=>a,x:()=>o});var r=i(96540);const l={},t=r.createContext(l);function a(e){const n=r.useContext(t);return r.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function o(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(l):e.components||l:a(e.components),r.createElement(t.Provider,{value:n},e.children)}}}]);