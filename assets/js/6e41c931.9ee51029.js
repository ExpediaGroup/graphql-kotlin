"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[8801],{48569:(e,t,r)=>{r.r(t),r.d(t,{assets:()=>d,contentTitle:()=>l,default:()=>h,frontMatter:()=>i,metadata:()=>a,toc:()=>o});var s=r(74848),n=r(28453);const i={id:"spring-beans",title:"Automatically Created Beans"},l=void 0,a={id:"server/spring-server/spring-beans",title:"Automatically Created Beans",description:"graphql-kotlin-spring-server automatically creates all the necessary beans to start a GraphQL server.",source:"@site/docs/server/spring-server/spring-beans.md",sourceDirName:"server/spring-server",slug:"/server/spring-server/spring-beans",permalink:"/graphql-kotlin/docs/8.x.x/server/spring-server/spring-beans",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/docs/server/spring-server/spring-beans.md",tags:[],version:"current",lastUpdatedBy:"Curtis Cook",lastUpdatedAt:1712948770,formattedLastUpdatedAt:"Apr 12, 2024",frontMatter:{id:"spring-beans",title:"Automatically Created Beans"},sidebar:"docs",previous:{title:"HTTP Request and Response",permalink:"/graphql-kotlin/docs/8.x.x/server/spring-server/spring-http-request-response"},next:{title:"Configuration Properties",permalink:"/graphql-kotlin/docs/8.x.x/server/spring-server/spring-properties"}},d={},o=[{value:"Execution",id:"execution",level:2},{value:"Non-Federated Schema",id:"non-federated-schema",level:2},{value:"Federated Schema",id:"federated-schema",level:2},{value:"GraphQL Configuration",id:"graphql-configuration",level:2},{value:"Subscriptions",id:"subscriptions",level:2},{value:"graphql-transport-ws",id:"graphql-transport-ws",level:3},{value:"(deprecated) subscription-transport-ws",id:"deprecated-subscription-transport-ws",level:3},{value:"Fixed Beans",id:"fixed-beans",level:2}];function c(e){const t={a:"a",admonition:"admonition",code:"code",em:"em",h2:"h2",h3:"h3",li:"li",p:"p",strong:"strong",table:"table",tbody:"tbody",td:"td",th:"th",thead:"thead",tr:"tr",ul:"ul",...(0,n.R)(),...e.components};return(0,s.jsxs)(s.Fragment,{children:[(0,s.jsxs)(t.p,{children:[(0,s.jsx)(t.code,{children:"graphql-kotlin-spring-server"})," automatically creates all the necessary beans to start a GraphQL server.\nMany of the beans are conditionally created and the default behavior can be customized by providing custom overriding beans in your application context."]}),"\n",(0,s.jsx)(t.h2,{id:"execution",children:"Execution"}),"\n",(0,s.jsxs)(t.table,{children:[(0,s.jsx)(t.thead,{children:(0,s.jsxs)(t.tr,{children:[(0,s.jsx)(t.th,{style:{textAlign:"left"},children:"Bean"}),(0,s.jsx)(t.th,{style:{textAlign:"left"},children:"Description"})]})}),(0,s.jsxs)(t.tbody,{children:[(0,s.jsxs)(t.tr,{children:[(0,s.jsx)(t.td,{style:{textAlign:"left"},children:"DataFetcherExceptionHandler"}),(0,s.jsxs)(t.td,{style:{textAlign:"left"},children:["GraphQL exception handler used from the various execution strategies, defaults to ",(0,s.jsx)(t.a,{href:"https://www.graphql-java.com/documentation/v16/execution/",children:"SimpleDataFetcherExceptionHandler"})," from graphql-java."]})]}),(0,s.jsxs)(t.tr,{children:[(0,s.jsx)(t.td,{style:{textAlign:"left"},children:"KotlinDataFetcherFactoryProvider"}),(0,s.jsxs)(t.td,{style:{textAlign:"left"},children:["Factory used during schema construction to obtain ",(0,s.jsx)(t.code,{children:"DataFetcherFactory"})," that should be used for target function (using Spring aware ",(0,s.jsx)(t.code,{children:"SpringDataFetcher"}),") and property resolution."]})]}),(0,s.jsxs)(t.tr,{children:[(0,s.jsx)(t.td,{style:{textAlign:"left"},children:"KotlinDataLoader (optional)"}),(0,s.jsxs)(t.td,{style:{textAlign:"left"},children:["Any number of beans created that implement ",(0,s.jsx)(t.code,{children:"KotlinDataLoader"}),". See ",(0,s.jsx)(t.a,{href:"/graphql-kotlin/docs/8.x.x/server/data-loader/",children:"Data Loaders"})," for more details."]})]}),(0,s.jsxs)(t.tr,{children:[(0,s.jsx)(t.td,{style:{textAlign:"left"},children:"KotlinDataLoaderRegistryFactory"}),(0,s.jsxs)(t.td,{style:{textAlign:"left"},children:["A factory class that creates a ",(0,s.jsx)(t.code,{children:"KotlinDataLoaderRegistry"})," of all the ",(0,s.jsx)(t.code,{children:"KotlinDataLoaders"}),". Defaults to empty registry."]})]})]})]}),"\n",(0,s.jsx)(t.h2,{id:"non-federated-schema",children:"Non-Federated Schema"}),"\n",(0,s.jsx)(t.admonition,{type:"note",children:(0,s.jsx)(t.p,{children:(0,s.jsxs)(t.em,{children:["Created only if federation is ",(0,s.jsx)(t.strong,{children:"disabled"})]})})}),"\n",(0,s.jsxs)(t.table,{children:[(0,s.jsx)(t.thead,{children:(0,s.jsxs)(t.tr,{children:[(0,s.jsx)(t.th,{style:{textAlign:"left"},children:"Bean"}),(0,s.jsx)(t.th,{style:{textAlign:"left"},children:"Description"})]})}),(0,s.jsxs)(t.tbody,{children:[(0,s.jsxs)(t.tr,{children:[(0,s.jsx)(t.td,{style:{textAlign:"left"},children:"GraphQLSchema"}),(0,s.jsxs)(t.td,{style:{textAlign:"left"},children:["GraphQL schema generated based on the schema generator configuration and  ",(0,s.jsx)(t.code,{children:"Query"}),", ",(0,s.jsx)(t.code,{children:"Mutation"})," and ",(0,s.jsx)(t.code,{children:"Subscription"})," objects available in the application context."]})]}),(0,s.jsxs)(t.tr,{children:[(0,s.jsx)(t.td,{style:{textAlign:"left"},children:"SchemaGeneratorConfig"}),(0,s.jsxs)(t.td,{style:{textAlign:"left"},children:["Schema generator configuration information, see ",(0,s.jsx)(t.a,{href:"/graphql-kotlin/docs/8.x.x/schema-generator/customizing-schemas/generator-config",children:"Schema Generator Configuration"})," for details. Can be customized by providing ",(0,s.jsx)(t.code,{children:"TopLevelNames"}),", ",(0,s.jsx)(t.a,{href:"/graphql-kotlin/docs/8.x.x/schema-generator/customizing-schemas/generator-config",children:"SchemaGeneratorHooks"})," and ",(0,s.jsx)(t.code,{children:"KotlinDataFetcherFactoryProvider"})," beans."]})]}),(0,s.jsxs)(t.tr,{children:[(0,s.jsx)(t.td,{style:{textAlign:"left"},children:"GraphQLTypeResolver"}),(0,s.jsxs)(t.td,{style:{textAlign:"left"},children:["GraphQL type resolver that is used to lookup polymorphic type hierarchy. Defaults to use ",(0,s.jsx)(t.code,{children:"ClassGraph"})," to load polymorphic information directly from classpath.",(0,s.jsx)("br",{}),(0,s.jsx)("br",{}),(0,s.jsx)(t.strong,{children:"NOTE: When generating GraalVM Native Images you cannot use classpath scanning and have to explicitly provide this information."})]})]})]})]}),"\n",(0,s.jsx)(t.h2,{id:"federated-schema",children:"Federated Schema"}),"\n",(0,s.jsx)(t.admonition,{type:"note",children:(0,s.jsx)(t.p,{children:(0,s.jsxs)(t.em,{children:["Created only if federation is ",(0,s.jsx)(t.strong,{children:"enabled"})]})})}),"\n",(0,s.jsxs)(t.table,{children:[(0,s.jsx)(t.thead,{children:(0,s.jsxs)(t.tr,{children:[(0,s.jsx)(t.th,{style:{textAlign:"left"},children:"Bean"}),(0,s.jsx)(t.th,{style:{textAlign:"left"},children:"Description"})]})}),(0,s.jsxs)(t.tbody,{children:[(0,s.jsxs)(t.tr,{children:[(0,s.jsx)(t.td,{style:{textAlign:"left"},children:"FederatedGraphQLTypeResolver"}),(0,s.jsxs)(t.td,{style:{textAlign:"left"},children:["GraphQL type resolver that is used to lookup polymorphic type hierarchy and locate federated entities. Defaults to use ",(0,s.jsx)(t.code,{children:"ClassGraph"})," to load information directly from classpath.",(0,s.jsx)("br",{}),(0,s.jsx)("br",{}),(0,s.jsx)(t.strong,{children:"NOTE: When generating GraalVM Native Images you cannot use classpath scanning and have to explicitly provide this information."})]})]}),(0,s.jsxs)(t.tr,{children:[(0,s.jsx)(t.td,{style:{textAlign:"left"},children:"FederatedTypeResolvers"}),(0,s.jsxs)(t.td,{style:{textAlign:"left"},children:["List of ",(0,s.jsx)(t.code,{children:"FederatedTypeResolvers"})," marked as beans that should be added to hooks. See ",(0,s.jsx)(t.a,{href:"/graphql-kotlin/docs/8.x.x/schema-generator/federation/type-resolution",children:"Federated Type Resolution"})," for more details"]})]}),(0,s.jsxs)(t.tr,{children:[(0,s.jsx)(t.td,{style:{textAlign:"left"},children:"FederatedSchemaGeneratorHooks"}),(0,s.jsx)(t.td,{style:{textAlign:"left"},children:"Schema generator hooks used to build federated schema"})]}),(0,s.jsxs)(t.tr,{children:[(0,s.jsx)(t.td,{style:{textAlign:"left"},children:"FederatedSchemaGeneratorConfig"}),(0,s.jsxs)(t.td,{style:{textAlign:"left"},children:["Federated schema generator configuration information. You can customize the configuration by providing ",(0,s.jsx)(t.code,{children:"TopLevelNames"}),", ",(0,s.jsx)(t.code,{children:"FederatedSchemaGeneratorHooks"})," and ",(0,s.jsx)(t.code,{children:"KotlinDataFetcherFactoryProvider"})," beans"]})]}),(0,s.jsxs)(t.tr,{children:[(0,s.jsx)(t.td,{style:{textAlign:"left"},children:"FederatedTracingInstrumentation"}),(0,s.jsxs)(t.td,{style:{textAlign:"left"},children:["If ",(0,s.jsx)(t.code,{children:"graphql.federation.tracing.enabled"})," is true, it adds tracing info to the response via the ",(0,s.jsx)(t.a,{href:"https://github.com/apollographql/federation-jvm",children:"apollo federation-jvm"})," library."]})]}),(0,s.jsxs)(t.tr,{children:[(0,s.jsx)(t.td,{style:{textAlign:"left"},children:"GraphQLSchema"}),(0,s.jsxs)(t.td,{style:{textAlign:"left"},children:["GraphQL schema generated based on the federated schema generator configuration and  ",(0,s.jsx)(t.code,{children:"Query"}),", ",(0,s.jsx)(t.code,{children:"Mutation"})," and ",(0,s.jsx)(t.code,{children:"Subscription"})," objects available in the application context."]})]})]})]}),"\n",(0,s.jsx)(t.h2,{id:"graphql-configuration",children:"GraphQL Configuration"}),"\n",(0,s.jsxs)(t.table,{children:[(0,s.jsx)(t.thead,{children:(0,s.jsxs)(t.tr,{children:[(0,s.jsx)(t.th,{style:{textAlign:"left"},children:"Bean"}),(0,s.jsx)(t.th,{style:{textAlign:"left"},children:"Description"})]})}),(0,s.jsxs)(t.tbody,{children:[(0,s.jsxs)(t.tr,{children:[(0,s.jsx)(t.td,{style:{textAlign:"left"},children:"Instrumentation (optional)"}),(0,s.jsxs)(t.td,{style:{textAlign:"left"},children:["Any number of beans created that implement ",(0,s.jsx)(t.code,{children:"graphql-java"})," ",(0,s.jsx)(t.a,{href:"https://www.graphql-java.com/documentation/v16/instrumentation/",children:"Instrumentation"})," will be pulled in. The beans can be ordered by implementing the Spring ",(0,s.jsx)(t.code,{children:"Ordered"})," interface."]})]}),(0,s.jsxs)(t.tr,{children:[(0,s.jsx)(t.td,{style:{textAlign:"left"},children:"ExecutionIdProvider (optional)"}),(0,s.jsxs)(t.td,{style:{textAlign:"left"},children:["Any number of beans created that implement ",(0,s.jsx)(t.code,{children:"graphql-java"})," ",(0,s.jsx)(t.a,{href:"https://github.com/graphql-java/graphql-java/blob/master/src/main/java/graphql/execution/ExecutionIdProvider.java",children:"ExecutionIdProvider"})," will be pulled in."]})]}),(0,s.jsxs)(t.tr,{children:[(0,s.jsx)(t.td,{style:{textAlign:"left"},children:"PreparsedDocumentProvider (optional)"}),(0,s.jsxs)(t.td,{style:{textAlign:"left"},children:["Any number of beans created that implement ",(0,s.jsx)(t.code,{children:"graphql-java"})," ",(0,s.jsx)(t.a,{href:"https://github.com/graphql-java/graphql-java/blob/master/src/main/java/graphql/execution/preparsed/PreparsedDocumentProvider.java",children:"PreparsedDocumentProvider"})," will be pulled in."]})]}),(0,s.jsxs)(t.tr,{children:[(0,s.jsx)(t.td,{style:{textAlign:"left"},children:"GraphQL"}),(0,s.jsxs)(t.td,{style:{textAlign:"left"},children:["GraphQL execution object generated using ",(0,s.jsx)(t.code,{children:"GraphQLSchema"})," with default async execution strategies. The GraphQL object can be customized by optionally providing the above beans in the application context."]})]}),(0,s.jsxs)(t.tr,{children:[(0,s.jsx)(t.td,{style:{textAlign:"left"},children:"SpringGraphQLRequestParser"}),(0,s.jsxs)(t.td,{style:{textAlign:"left"},children:["Provides the Spring specific logic for parsing the HTTP request into a common GraphQLRequest. See ",(0,s.jsx)(t.a,{href:"/graphql-kotlin/docs/8.x.x/server/graphql-request-parser",children:"GraphQLRequestParser"})]})]}),(0,s.jsxs)(t.tr,{children:[(0,s.jsx)(t.td,{style:{textAlign:"left"},children:"SpringGraphQLContextFactory"}),(0,s.jsxs)(t.td,{style:{textAlign:"left"},children:["Spring specific factory that uses the ",(0,s.jsx)(t.code,{children:"ServerRequest"}),". The ",(0,s.jsx)(t.code,{children:"GraphQLContext"})," generated can be any object. See ",(0,s.jsx)(t.a,{href:"/graphql-kotlin/docs/8.x.x/server/graphql-context-factory",children:"GraphQLContextFactory"}),"."]})]}),(0,s.jsxs)(t.tr,{children:[(0,s.jsx)(t.td,{style:{textAlign:"left"},children:"GraphQLRequestHandler"}),(0,s.jsxs)(t.td,{style:{textAlign:"left"},children:["Handler invoked from ",(0,s.jsx)(t.code,{children:"GraphQLServer"})," that executes the incoming request, defaults to ",(0,s.jsx)(t.a,{href:"/graphql-kotlin/docs/8.x.x/server/graphql-request-handler",children:"GraphQLRequestHandler"}),"."]})]}),(0,s.jsxs)(t.tr,{children:[(0,s.jsx)(t.td,{style:{textAlign:"left"},children:"SpringGraphQLServer"}),(0,s.jsxs)(t.td,{style:{textAlign:"left"},children:["Spring specific object that takes in a ",(0,s.jsx)(t.code,{children:"ServerRequest"})," and returns a ",(0,s.jsx)(t.code,{children:"GraphQLResponse"})," using all the above implementations. See ",(0,s.jsx)(t.a,{href:"/graphql-kotlin/docs/8.x.x/server/graphql-server",children:"GraphQLServer"})]})]}),(0,s.jsxs)(t.tr,{children:[(0,s.jsx)(t.td,{style:{textAlign:"left"},children:"IDValueUnboxer"}),(0,s.jsx)(t.td,{style:{textAlign:"left"},children:"Value unboxer that provides support for handling ID value class"})]})]})]}),"\n",(0,s.jsx)(t.h2,{id:"subscriptions",children:"Subscriptions"}),"\n",(0,s.jsx)(t.admonition,{type:"note",children:(0,s.jsx)(t.p,{children:(0,s.jsxs)(t.em,{children:["Created only if the ",(0,s.jsx)(t.code,{children:"Subscription"})," marker interface is used"]})})}),"\n",(0,s.jsxs)(t.table,{children:[(0,s.jsx)(t.thead,{children:(0,s.jsxs)(t.tr,{children:[(0,s.jsx)(t.th,{style:{textAlign:"left"},children:"Bean"}),(0,s.jsx)(t.th,{style:{textAlign:"left"},children:"Description"})]})}),(0,s.jsxs)(t.tbody,{children:[(0,s.jsxs)(t.tr,{children:[(0,s.jsx)(t.td,{style:{textAlign:"left"},children:"FlowSubscriptionSchemaGeneratorHooks"}),(0,s.jsxs)(t.td,{style:{textAlign:"left"},children:["Schema generator hooks that provide support for using ",(0,s.jsx)(t.code,{children:"Flow"})," in your subscriptions"]})]}),(0,s.jsxs)(t.tr,{children:[(0,s.jsx)(t.td,{style:{textAlign:"left"},children:"WebSocketHandlerAdapter"}),(0,s.jsxs)(t.td,{style:{textAlign:"left"},children:["Spring class for handling web socket http requests. See ",(0,s.jsx)(t.a,{href:"https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/reactive/socket/server/support/WebSocketHandlerAdapter.html",children:"Spring documentation"})]})]}),(0,s.jsxs)(t.tr,{children:[(0,s.jsx)(t.td,{style:{textAlign:"left"},children:"HandlerMapping"}),(0,s.jsx)(t.td,{style:{textAlign:"left"},children:"Maps websocket URL to the corresponding web socket handler"})]})]})]}),"\n",(0,s.jsx)(t.h3,{id:"graphql-transport-ws",children:"graphql-transport-ws"}),"\n",(0,s.jsxs)(t.table,{children:[(0,s.jsx)(t.thead,{children:(0,s.jsxs)(t.tr,{children:[(0,s.jsx)(t.th,{style:{textAlign:"left"},children:"Bean"}),(0,s.jsx)(t.th,{style:{textAlign:"left"},children:"Description"})]})}),(0,s.jsxs)(t.tbody,{children:[(0,s.jsxs)(t.tr,{children:[(0,s.jsx)(t.td,{style:{textAlign:"left"},children:"SpringSubscriptionGraphQLContextFactory"}),(0,s.jsx)(t.td,{style:{textAlign:"left"},children:"Generates GraphQL subscription context based on the WebSocket session information"})]}),(0,s.jsxs)(t.tr,{children:[(0,s.jsx)(t.td,{style:{textAlign:"left"},children:"SpringGraphQLSubscriptionRequestParser"}),(0,s.jsx)(t.td,{style:{textAlign:"left"},children:"Parses incoming WebSocket messages"})]}),(0,s.jsxs)(t.tr,{children:[(0,s.jsx)(t.td,{style:{textAlign:"left"},children:"SpringGraphQLSubscriptionHooks"}),(0,s.jsx)(t.td,{style:{textAlign:"left"},children:"Provides hooks into the subscription request lifecycle"})]}),(0,s.jsxs)(t.tr,{children:[(0,s.jsx)(t.td,{style:{textAlign:"left"},children:"SubscriptionWebSocketHandler"}),(0,s.jsxs)(t.td,{style:{textAlign:"left"},children:["WebSocketHandler that implements the ",(0,s.jsx)(t.code,{children:"graphql-transport-ws"})," subscription protocol"]})]})]})]}),"\n",(0,s.jsx)(t.h3,{id:"deprecated-subscription-transport-ws",children:"(deprecated) subscription-transport-ws"}),"\n",(0,s.jsxs)(t.table,{children:[(0,s.jsx)(t.thead,{children:(0,s.jsxs)(t.tr,{children:[(0,s.jsx)(t.th,{style:{textAlign:"left"},children:"Bean"}),(0,s.jsx)(t.th,{style:{textAlign:"left"},children:"Description"})]})}),(0,s.jsxs)(t.tbody,{children:[(0,s.jsxs)(t.tr,{children:[(0,s.jsx)(t.td,{style:{textAlign:"left"},children:"ApolloSubscriptionHooks"}),(0,s.jsxs)(t.td,{style:{textAlign:"left"},children:["Provides hooks into the subscription request lifecycle. See ",(0,s.jsx)(t.a,{href:"/graphql-kotlin/docs/8.x.x/server/spring-server/spring-subscriptions",children:"the subscription docs"})]})]}),(0,s.jsxs)(t.tr,{children:[(0,s.jsx)(t.td,{style:{textAlign:"left"},children:"SpringSubscriptionGraphQLContextFactory"}),(0,s.jsxs)(t.td,{style:{textAlign:"left"},children:["Spring specific factory that uses the ",(0,s.jsx)(t.code,{children:"WebSocketSession"}),". See ",(0,s.jsx)(t.a,{href:"/graphql-kotlin/docs/8.x.x/server/graphql-context-factory",children:"GraphQLContextFactory"}),"."]})]}),(0,s.jsxs)(t.tr,{children:[(0,s.jsx)(t.td,{style:{textAlign:"left"},children:"ApolloSubscriptionProtocolHandler"}),(0,s.jsxs)(t.td,{style:{textAlign:"left"},children:["Implementation of the ",(0,s.jsx)(t.code,{children:"subscription-transport-ws"})," subscription protocol"]})]}),(0,s.jsxs)(t.tr,{children:[(0,s.jsx)(t.td,{style:{textAlign:"left"},children:"ApolloSubscriptionWebSocketHandler"}),(0,s.jsxs)(t.td,{style:{textAlign:"left"},children:["WebSocketHandler that delegates handling of the messages to the ",(0,s.jsx)(t.code,{children:"ApolloSubscriptionProtocolHandler"})," bean"]})]})]})]}),"\n",(0,s.jsx)(t.h2,{id:"fixed-beans",children:"Fixed Beans"}),"\n",(0,s.jsx)(t.p,{children:"The following beans cannot be overridden, but may have options to disable them:"}),"\n",(0,s.jsxs)(t.ul,{children:["\n",(0,s.jsx)(t.li,{children:"Route handler for GraphQL queries and mutations endpoint."}),"\n",(0,s.jsx)(t.li,{children:"Route handler for the SDL endpoint. Created only if sdl route is enabled."}),"\n",(0,s.jsxs)(t.li,{children:["Route handler for ",(0,s.jsx)(t.a,{href:"https://github.com/graphql/graphiql",children:"GraphQL graphiql browser IDE"}),". Created only if graphiql is enabled."]}),"\n",(0,s.jsx)(t.li,{children:"Route handler for the subscriptions endpoint. Created only if subscriptions are used."}),"\n",(0,s.jsxs)(t.li,{children:[(0,s.jsx)(t.code,{children:"ApolloSubscriptionProtocolHandler"})," for handling GraphQL subscriptions using the ",(0,s.jsxs)(t.a,{href:"https://github.com/apollographql/subscriptions-transport-ws/blob/master/PROTOCOL.md",children:[(0,s.jsx)(t.code,{children:"graphql-ws"})," protocol"]}),". Created only if subscriptions are used."]}),"\n",(0,s.jsxs)(t.li,{children:[(0,s.jsx)(t.code,{children:"SubscriptionWebSocketHandler"})," that utilizes above subscription protocol handler. Created only if subscriptions are used."]}),"\n"]})]})}function h(e={}){const{wrapper:t}={...(0,n.R)(),...e.components};return t?(0,s.jsx)(t,{...e,children:(0,s.jsx)(c,{...e})}):c(e)}},28453:(e,t,r)=>{r.d(t,{R:()=>l,x:()=>a});var s=r(96540);const n={},i=s.createContext(n);function l(e){const t=s.useContext(i);return s.useMemo((function(){return"function"==typeof e?e(t):{...t,...e}}),[t,e])}function a(e){let t;return t=e.disableParentContext?"function"==typeof e.components?e.components(n):e.components||n:l(e.components),s.createElement(i.Provider,{value:t},e.children)}}}]);