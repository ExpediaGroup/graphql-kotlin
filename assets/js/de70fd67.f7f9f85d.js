"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[9036],{53483:(e,n,r)=>{r.r(n),r.d(n,{assets:()=>l,contentTitle:()=>a,default:()=>d,frontMatter:()=>t,metadata:()=>s,toc:()=>c});var i=r(74848),o=r(28453);const t={id:"ktor-configuration",title:"Ktor Plugin Configuration"},a=void 0,s={id:"server/ktor-server/ktor-configuration",title:"Ktor Plugin Configuration",description:"graphql-kotlin-ktor-server plugin can be configured by using DSL when installing the plugin. Configuration is broken into multiple",source:"@site/docs/server/ktor-server/ktor-configuration.md",sourceDirName:"server/ktor-server",slug:"/server/ktor-server/ktor-configuration",permalink:"/graphql-kotlin/docs/9.x.x/server/ktor-server/ktor-configuration",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/docs/server/ktor-server/ktor-configuration.md",tags:[],version:"current",lastUpdatedBy:"Samuel Vazquez",lastUpdatedAt:172791071e4,frontMatter:{id:"ktor-configuration",title:"Ktor Plugin Configuration"},sidebar:"docs",previous:{title:"HTTP request and response",permalink:"/graphql-kotlin/docs/9.x.x/server/ktor-server/ktor-http-request-response"},next:{title:"Subscriptions",permalink:"/graphql-kotlin/docs/9.x.x/server/ktor-server/ktor-subscriptions"}},l={},c=[{value:"Configuration Files",id:"configuration-files",level:2},{value:"Schema Configuration",id:"schema-configuration",level:2},{value:"GraphQL Execution Engine Configuration",id:"graphql-execution-engine-configuration",level:2},{value:"Server Configuration",id:"server-configuration",level:2},{value:"Routes Configuration",id:"routes-configuration",level:2},{value:"GraphQL POST route",id:"graphql-post-route",level:3},{value:"GraphQL GET route",id:"graphql-get-route",level:3},{value:"GraphQL Subscriptions route",id:"graphql-subscriptions-route",level:3},{value:"GraphQL SDL route",id:"graphql-sdl-route",level:3},{value:"GraphiQL IDE route",id:"graphiql-ide-route",level:3}];function u(e){const n={a:"a",admonition:"admonition",code:"code",h2:"h2",h3:"h3",p:"p",pre:"pre",strong:"strong",...(0,o.R)(),...e.components};return(0,i.jsxs)(i.Fragment,{children:[(0,i.jsxs)(n.p,{children:[(0,i.jsx)(n.code,{children:"graphql-kotlin-ktor-server"})," plugin can be configured by using DSL when installing the plugin. Configuration is broken into multiple\ngroups related to specific functionality. See sections below for details."]}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-kotlin",children:"install(GraphQL) {\n    schema {\n        // configuration that controls schema generation logic\n    }\n    engine {\n        // configurations that control GraphQL execution engine\n    }\n    server {\n        // configurations that control GraphQL HTTP server\n    }\n}\n"})}),"\n",(0,i.jsx)(n.h2,{id:"configuration-files",children:"Configuration Files"}),"\n",(0,i.jsxs)(n.p,{children:["Ktor supports specifying configurations in ",(0,i.jsx)(n.code,{children:"application.conf"})," (HOCON) or ",(0,i.jsx)(n.code,{children:"application.yaml"})," file. By default, only HOCON format\nis supported. To use a YAML configuration file, you need to add the ",(0,i.jsx)(n.code,{children:"ktor-server-config-yaml"})," dependency to your project dependencies.\nSee ",(0,i.jsx)(n.a,{href:"https://ktor.io/docs/configuration-file.html",children:"Ktor documentation"})," for details."]}),"\n",(0,i.jsx)(n.admonition,{type:"caution",children:(0,i.jsx)(n.p,{children:"Not all configuration properties can be specified in your configuration file. You will need to use DSL to configure more advanced features\nthat cannot be represented in the property file syntax (e.g. any instantiated objects)."})}),"\n",(0,i.jsxs)(n.p,{children:["All configuration options in ",(0,i.jsx)(n.code,{children:"application.conf"})," format, with their default values are provided below."]}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-kotlin",children:'graphql {\n    schema {\n        // this is a required property that you need to set to appropriate value\n        // example value is just provided for illustration purposes\n        packages = [\n            "com.example"\n        ]\n        federation {\n            enabled = false\n            tracing {\n                enabled = true\n                debug = false\n            }\n        }\n    }\n    engine {\n        automaticPersistedQueries {\n            enabled = false\n        }\n        batching {\n            enabled = false\n            strategy = LEVEL_DISPATCHED\n        }\n        introspection {\n            enabled = true\n        }\n    }\n}\n'})}),"\n",(0,i.jsx)(n.h2,{id:"schema-configuration",children:"Schema Configuration"}),"\n",(0,i.jsxs)(n.p,{children:["This section configures ",(0,i.jsx)(n.code,{children:"graphql-kotlin-schema-generation"})," logic and is the ",(0,i.jsx)(n.strong,{children:"only"})," section that has to be configured.\nAt a minimum you need to configure the list of packages that can contain your GraphQL schema definitions and a list of queries."]}),"\n",(0,i.jsx)(n.p,{children:"All configuration options, with their default values are provided below."}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-kotlin",children:'schema {\n    // this is a required property that you need to set to appropriate value\n    // example value is just provided for illustration purposes\n    packages = listOf("com.example")\n    // non-federated schemas, require at least a single query\n    queries = listOf()\n    mutations = listOf()\n    subscriptions = listOf()\n    schemaObject = null\n    // federated schemas require federated hooks\n    hooks = NoopSchemaGeneratorHooks\n    topLevelNames = TopLevelNames()\n    federation {\n        enabled = false\n        tracing {\n            enabled = true\n            debug = false\n        }\n    }\n    // required for GraalVM native servers\n    typeHierarchy = null\n}\n'})}),"\n",(0,i.jsx)(n.h2,{id:"graphql-execution-engine-configuration",children:"GraphQL Execution Engine Configuration"}),"\n",(0,i.jsxs)(n.p,{children:["This section configures ",(0,i.jsx)(n.code,{children:"graphql-java"})," execution engine that will be used to process your GraphQL requests."]}),"\n",(0,i.jsx)(n.p,{children:"All configuration options, with their default values are provided below."}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-kotlin",children:"engine {\n    automaticPersistedQueries {\n        enabled = false\n    }\n    // DO NOT enable default batching logic if specifying custom provider\n    batching {\n        enabled = false\n        strategy = SYNC_EXHAUSTION\n    }\n    introspection {\n        enabled = true\n    }\n    dataFetcherFactoryProvider = SimpleKotlinDataFetcherFactoryProvider()\n    dataLoaderRegistryFactory = KotlinDataLoaderRegistryFactory()\n    exceptionHandler = SimpleDataFetcherExceptionHandler()\n    executionIdProvider = null\n    idValueUnboxer = IDValueUnboxer()\n    instrumentations = emptyList()\n    // DO NOT specify custom provider if enabling default batching logic\n    preparsedDocumentProvider = null\n}\n"})}),"\n",(0,i.jsx)(n.h2,{id:"server-configuration",children:"Server Configuration"}),"\n",(0,i.jsx)(n.p,{children:"This section configures your GraphQL HTTP server."}),"\n",(0,i.jsx)(n.p,{children:"All configuration options, with their default values are provided below."}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-kotlin",children:"server {\n    contextFactory = DefaultKtorGraphQLContextFactory()\n    jacksonConfiguration = { }\n    requestParser = KtorGraphQLRequestParser(jacksonObjectMapper().apply(jacksonConfiguration))\n}\n"})}),"\n",(0,i.jsx)(n.h2,{id:"routes-configuration",children:"Routes Configuration"}),"\n",(0,i.jsxs)(n.p,{children:["GraphQL Kotlin Ktor Plugin DOES NOT automatically configure any routes. You need to explicitly configure ",(0,i.jsx)(n.code,{children:"Routing"}),"\nplugin with GraphQL routes. This allows you to selectively enable routes and wrap them in some additional logic (e.g. ",(0,i.jsx)(n.code,{children:"Authentication"}),")."]}),"\n",(0,i.jsxs)(n.p,{children:["GraphQL Kotlin Ktor Plugin provides following ",(0,i.jsx)(n.code,{children:"Route"})," extensions that can be called when configuring ",(0,i.jsx)(n.code,{children:"Routing"})," plugin."]}),"\n",(0,i.jsx)(n.h3,{id:"graphql-post-route",children:"GraphQL POST route"}),"\n",(0,i.jsxs)(n.p,{children:["This is the main route for processing your GraphQL requests. By default, it will use ",(0,i.jsx)(n.code,{children:"/graphql"})," endpoint and respond\nusing chunked encoding."]}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-kotlin",children:'fun Route.graphQLPostRoute(endpoint: String = "graphql", streamingResponse: Boolean = true, jacksonConfiguration: ObjectMapper.() -> Unit = {}): Route\n'})}),"\n",(0,i.jsx)(n.h3,{id:"graphql-get-route",children:"GraphQL GET route"}),"\n",(0,i.jsx)(n.admonition,{type:"caution",children:(0,i.jsxs)(n.p,{children:["Only ",(0,i.jsx)(n.code,{children:"Query"})," operations are supported by the GET route."]})}),"\n",(0,i.jsxs)(n.p,{children:["GraphQL route for processing GET requests. By default, it will use ",(0,i.jsx)(n.code,{children:"/graphql"})," endpoint and respond using chunked encoding."]}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-kotlin",children:'fun Route.graphQLGetRoute(endpoint: String = "graphql", streamingResponse: Boolean = true, jacksonConfiguration: ObjectMapper.() -> Unit = {}): Route\n'})}),"\n",(0,i.jsx)(n.h3,{id:"graphql-subscriptions-route",children:"GraphQL Subscriptions route"}),"\n",(0,i.jsxs)(n.p,{children:["GraphQL route for processing subscriptions. By default, it will use ",(0,i.jsx)(n.code,{children:"/subscriptions"})," endpoint and handle\nrequests using ",(0,i.jsx)(n.a,{href:"https://github.com/enisdenjo/graphql-ws/blob/master/PROTOCOL.md",children:"graphql-transport-ws"})," protocol handler."]}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-kotlin",children:'fun Route.graphQLSubscriptionsRoute(\n    endpoint: String = "subscriptions",\n    protocol: String? = null,\n    handlerOverride: KtorGraphQLSubscriptionHandler? = null,\n)\n'})}),"\n",(0,i.jsxs)(n.p,{children:["See related ",(0,i.jsx)(n.a,{href:"/graphql-kotlin/docs/9.x.x/server/ktor-server/ktor-subscriptions",children:"Subscriptions"})," document for more info."]}),"\n",(0,i.jsx)(n.h3,{id:"graphql-sdl-route",children:"GraphQL SDL route"}),"\n",(0,i.jsx)(n.p,{children:"Convenience route to expose endpoint that returns your GraphQL schema in SDL format."}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-kotlin",children:'fun Route.graphQLSDLRoute(endpoint: String = "sdl"): Route\n'})}),"\n",(0,i.jsx)(n.h3,{id:"graphiql-ide-route",children:"GraphiQL IDE route"}),"\n",(0,i.jsxs)(n.p,{children:[(0,i.jsx)(n.a,{href:"https://github.com/graphql/graphiql",children:"GraphiQL IDE"})," is a convenient tool that helps you to easily interact\nwith your GraphQL server."]}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-kotlin",children:'fun Route.graphiQLRoute(endpoint: String = "graphiql", graphQLEndpoint: String = "graphql"): Route\n'})})]})}function d(e={}){const{wrapper:n}={...(0,o.R)(),...e.components};return n?(0,i.jsx)(n,{...e,children:(0,i.jsx)(u,{...e})}):u(e)}},28453:(e,n,r)=>{r.d(n,{R:()=>a,x:()=>s});var i=r(96540);const o={},t=i.createContext(o);function a(e){const n=i.useContext(t);return i.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function s(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(o):e.components||o:a(e.components),i.createElement(t.Provider,{value:n},e.children)}}}]);