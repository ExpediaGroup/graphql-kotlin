"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[4760],{17952:(e,n,t)=>{t.r(n),t.d(n,{assets:()=>s,contentTitle:()=>r,default:()=>u,frontMatter:()=>a,metadata:()=>o,toc:()=>c});var i=t(74848),l=t(28453);const a={id:"client-customization",title:"Client Customization",original_id:"client-customization"},r=void 0,o={id:"client/client-customization",title:"Client Customization",description:"Ktor HTTP Client Customization",source:"@site/versioned_docs/version-3.x.x/client/client-customization.md",sourceDirName:"client",slug:"/client/client-customization",permalink:"/graphql-kotlin/docs/3.x.x/client/client-customization",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-3.x.x/client/client-customization.md",tags:[],version:"3.x.x",lastUpdatedBy:"Dariusz Kuc",lastUpdatedAt:1713385577,formattedLastUpdatedAt:"Apr 17, 2024",frontMatter:{id:"client-customization",title:"Client Customization",original_id:"client-customization"},sidebar:"docs",previous:{title:"Client Features",permalink:"/graphql-kotlin/docs/3.x.x/client/client-features"},next:{title:"Gradle Plugin",permalink:"/graphql-kotlin/docs/3.x.x/plugins/gradle-plugin"}},s={},c=[{value:"Ktor HTTP Client Customization",id:"ktor-http-client-customization",level:2},{value:"Global Client Customization",id:"global-client-customization",level:3},{value:"Per Request Customization",id:"per-request-customization",level:3},{value:"Custom GraphQL client",id:"custom-graphql-client",level:3},{value:"Jackson Customization",id:"jackson-customization",level:2},{value:"Deprecated Field\xa0Usage",id:"deprecated-fieldusage",level:2},{value:"Custom GraphQL\xa0Scalars",id:"custom-graphqlscalars",level:2}];function d(e){const n={a:"a",code:"code",h2:"h2",h3:"h3",li:"li",p:"p",pre:"pre",ul:"ul",...(0,l.R)(),...e.components};return(0,i.jsxs)(i.Fragment,{children:[(0,i.jsx)(n.h2,{id:"ktor-http-client-customization",children:"Ktor HTTP Client Customization"}),"\n",(0,i.jsxs)(n.p,{children:[(0,i.jsx)(n.code,{children:"GraphQLClient"})," uses the Ktor HTTP Client to execute the underlying queries. Clients can be customized with different\nengines (defaults to Coroutine-based IO) and HTTP client features. Custom configurations can be applied through Ktor DSL\nstyle builders."]}),"\n",(0,i.jsxs)(n.p,{children:["See ",(0,i.jsx)(n.a,{href:"https://ktor.io/clients/index.html",children:"Ktor HTTP Client documentation"})," for additional details."]}),"\n",(0,i.jsx)(n.h3,{id:"global-client-customization",children:"Global Client Customization"}),"\n",(0,i.jsxs)(n.p,{children:["A single instance of ",(0,i.jsx)(n.code,{children:"GraphQLClient"})," can be used to power many GraphQL operations. You can specify a target engine factory and\nconfigure it through the corresponding ",(0,i.jsx)(n.a,{href:"https://api.ktor.io/1.3.2/io.ktor.client/-http-client-config/index.html",children:"HttpClientConfig"}),".\nKtor also provides a number of ",(0,i.jsx)(n.a,{href:"https://ktor.io/clients/http-client/features.html",children:"standard HTTP features"})," and\nallows you to easily create custom ones that can be configured globally."]}),"\n",(0,i.jsxs)(n.p,{children:["The below example configures a new ",(0,i.jsx)(n.code,{children:"GraphQLClient"})," to use the ",(0,i.jsx)(n.code,{children:"OkHttp"})," engine with custom timeouts, adds a default ",(0,i.jsx)(n.code,{children:"X-MY-API-KEY"}),"\nheader to all requests, and enables basic logging of the requests."]}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-kotlin",children:'\nval client = GraphQLClient(\n        url = URL("http://localhost:8080/graphql"),\n        engineFactory = OkHttp\n) {\n    engine {\n        config {\n            connectTimeout(10, TimeUnit.SECONDS)\n            readTimeout(60, TimeUnit.SECONDS)\n            writeTimeout(60, TimeUnit.SECONDS)\n        }\n    }\n    defaultRequest {\n        header("X-MY-API-KEY", "someSecretApiKey")\n    }\n    install(Logging) {\n        logger = Logger.DEFAULT\n        level = LogLevel.INFO\n    }\n}\n\n'})}),"\n",(0,i.jsx)(n.h3,{id:"per-request-customization",children:"Per Request Customization"}),"\n",(0,i.jsxs)(n.p,{children:["Individual GraphQL requests can be customized through ",(0,i.jsx)(n.a,{href:"https://api.ktor.io/1.3.2/io.ktor.client.request/-http-request-builder/",children:"HttpRequestBuilder"}),".\nYou can use this mechanism to specify custom headers, update target url to include custom query parameters, configure\nattributes that can be accessed from the pipeline features as well specify timeouts per request."]}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-kotlin",children:'\nval helloWorldQuery = HelloWorldQuery(client)\nval result = helloWorldQuery.execute(variables = HelloWorldQuery.Variables(name = null)) {\n    header("X-B3-TraceId", "0123456789abcdef")\n}\n\n'})}),"\n",(0,i.jsx)(n.h3,{id:"custom-graphql-client",children:"Custom GraphQL client"}),"\n",(0,i.jsxs)(n.p,{children:[(0,i.jsx)(n.code,{children:"GraphQLClient"})," is an open class which means you can also extend it to provide custom ",(0,i.jsx)(n.code,{children:"execute"})," logic."]}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-kotlin",children:"\nclass CustomGraphQLClient(url: URL) : GraphQLClient<CIOEngineConfig>(url = url, engineFactory = CIO) {\n\n    override suspend fun <T> execute(query: String, operationName: String?, variables: Any?, resultType: Class<T>, requestBuilder: HttpRequestBuilder.() -> Unit): GraphQLResponse<T> {\n        // custom init logic\n        val result = super.execute(query, operationName, variables, resultType, requestBuilder)\n        // custom finalize logic\n        return result\n    }\n}\n\n"})}),"\n",(0,i.jsx)(n.h2,{id:"jackson-customization",children:"Jackson Customization"}),"\n",(0,i.jsxs)(n.p,{children:[(0,i.jsx)(n.code,{children:"GraphQLClient"})," relies on Jackson to handle polymorphic types and default enum values. You can specify your own custom\nobject mapper configured with some additional serialization/deserialization features but due to the necessary logic to\nhandle the above, currently we don't support other JSON libraries."]}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-kotlin",children:'\nval customObjectMapper = jacksonObjectMapper()\nval client = GraphQLClient(url = URL("http://localhost:8080/graphql"), mapper = customObjectMapper)\n\n'})}),"\n",(0,i.jsx)(n.h2,{id:"deprecated-fieldusage",children:"Deprecated Field\xa0Usage"}),"\n",(0,i.jsxs)(n.p,{children:["Build plugins will automatically fail generation of a client if any of the specified query files are referencing\ndeprecated fields. This ensures that your clients have to explicitly opt-in into deprecated usage by specifying\n",(0,i.jsx)(n.code,{children:"allowDeprecatedFields"})," configuration option."]}),"\n",(0,i.jsx)(n.h2,{id:"custom-graphqlscalars",children:"Custom GraphQL\xa0Scalars"}),"\n",(0,i.jsxs)(n.p,{children:["By default, custom GraphQL scalars are serialized and ",(0,i.jsx)(n.a,{href:"https://kotlinlang.org/docs/reference/type-aliases.html",children:"type-aliased"}),"\nto a String. GraphQL Kotlin plugins also support custom serialization based on provided configuration."]}),"\n",(0,i.jsxs)(n.p,{children:["In order to automatically convert between custom GraphQL ",(0,i.jsx)(n.code,{children:"UUID"})," scalar type and ",(0,i.jsx)(n.code,{children:"java.util.UUID"}),", we first need to create\nour custom ",(0,i.jsx)(n.code,{children:"ScalarConverter"}),"."]}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-kotlin",children:"\npackage com.example.client\n\nimport com.expediagroup.graphql.client.converter.ScalarConverter\nimport java.util.UUID\n\nclass UUIDScalarConverter : ScalarConverter<UUID> {\n    override fun toScalar(rawValue: String): UUID = UUID.fromString(rawValue)\n    override fun toJson(value: UUID): String = value.toString()\n}\n\n"})}),"\n",(0,i.jsx)(n.p,{children:"And then configure build plugin by specifying"}),"\n",(0,i.jsxs)(n.ul,{children:["\n",(0,i.jsx)(n.li,{children:"Custom GraphQL scalar name"}),"\n",(0,i.jsx)(n.li,{children:"Target class name"}),"\n",(0,i.jsx)(n.li,{children:"Converter that provides logic to map between GraphQL and Kotlin type"}),"\n"]}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-kotlin",children:'\ngraphql {\n    packageName = "com.example.generated"\n    endpoint = "http://localhost:8080/graphql"\n    converters.put("UUID", ScalarConverterMapping("java.util.UUID", "com.example.UUIDScalarConverter"))\n}\n\n'})}),"\n",(0,i.jsxs)(n.p,{children:["See ",(0,i.jsx)(n.a,{href:"/graphql-kotlin/docs/3.x.x/plugins/gradle-plugin",children:"Gradle"}),"\nand ",(0,i.jsx)(n.a,{href:"/graphql-kotlin/docs/3.x.x/plugins/maven-plugin",children:"Maven"}),"\nplugin documentation for additional details."]})]})}function u(e={}){const{wrapper:n}={...(0,l.R)(),...e.components};return n?(0,i.jsx)(n,{...e,children:(0,i.jsx)(d,{...e})}):d(e)}},28453:(e,n,t)=>{t.d(n,{R:()=>r,x:()=>o});var i=t(96540);const l={},a=i.createContext(l);function r(e){const n=i.useContext(a);return i.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function o(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(l):e.components||l:r(e.components),i.createElement(a.Provider,{value:n},e.children)}}}]);