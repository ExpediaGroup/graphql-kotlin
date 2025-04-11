"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[3008],{25669:(e,t,r)=>{r.r(t),r.d(t,{assets:()=>d,contentTitle:()=>l,default:()=>h,frontMatter:()=>i,metadata:()=>a,toc:()=>o});var n=r(74848),s=r(28453);const i={id:"spring-beans",title:"Automatically Created Beans"},l=void 0,a={id:"server/spring-server/spring-beans",title:"Automatically Created Beans",description:"graphql-kotlin-spring-server automatically creates all the necessary beans to start a GraphQL server.",source:"@site/versioned_docs/version-5.x.x/server/spring-server/spring-beans.md",sourceDirName:"server/spring-server",slug:"/server/spring-server/spring-beans",permalink:"/graphql-kotlin/docs/5.x.x/server/spring-server/spring-beans",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-5.x.x/server/spring-server/spring-beans.md",tags:[],version:"5.x.x",lastUpdatedBy:"Samuel Vazquez",lastUpdatedAt:1744404742e3,frontMatter:{id:"spring-beans",title:"Automatically Created Beans"},sidebar:"docs",previous:{title:"HTTP Request and Response",permalink:"/graphql-kotlin/docs/5.x.x/server/spring-server/spring-http-request-response"},next:{title:"Configuration Properties",permalink:"/graphql-kotlin/docs/5.x.x/server/spring-server/spring-properties"}},d={},o=[{value:"Execution",id:"execution",level:2},{value:"Non-Federated Schema",id:"non-federated-schema",level:2},{value:"Federated Schema",id:"federated-schema",level:2},{value:"GraphQL Configuration",id:"graphql-configuration",level:2},{value:"Subscriptions",id:"subscriptions",level:2},{value:"Fixed Beans",id:"fixed-beans",level:2}];function c(e){const t={a:"a",admonition:"admonition",code:"code",em:"em",h2:"h2",li:"li",p:"p",strong:"strong",table:"table",tbody:"tbody",td:"td",th:"th",thead:"thead",tr:"tr",ul:"ul",...(0,s.R)(),...e.components};return(0,n.jsxs)(n.Fragment,{children:[(0,n.jsxs)(t.p,{children:[(0,n.jsx)(t.code,{children:"graphql-kotlin-spring-server"})," automatically creates all the necessary beans to start a GraphQL server.\nMany of the beans are conditionally created and the default behavior can be customized by providing custom overriding beans in your application context."]}),"\n",(0,n.jsx)(t.h2,{id:"execution",children:"Execution"}),"\n",(0,n.jsxs)(t.table,{children:[(0,n.jsx)(t.thead,{children:(0,n.jsxs)(t.tr,{children:[(0,n.jsx)(t.th,{style:{textAlign:"left"},children:"Bean"}),(0,n.jsx)(t.th,{style:{textAlign:"left"},children:"Description"})]})}),(0,n.jsxs)(t.tbody,{children:[(0,n.jsxs)(t.tr,{children:[(0,n.jsx)(t.td,{style:{textAlign:"left"},children:"DataFetcherExceptionHandler"}),(0,n.jsxs)(t.td,{style:{textAlign:"left"},children:["GraphQL exception handler used from the various execution strategies, defaults to ",(0,n.jsx)(t.a,{href:"https://www.graphql-java.com/documentation/v16/execution/",children:"SimpleDataFetcherExceptionHandler"})," from graphql-java."]})]}),(0,n.jsxs)(t.tr,{children:[(0,n.jsx)(t.td,{style:{textAlign:"left"},children:"KotlinDataFetcherFactoryProvider"}),(0,n.jsxs)(t.td,{style:{textAlign:"left"},children:["Factory used during schema construction to obtain ",(0,n.jsx)(t.code,{children:"DataFetcherFactory"})," that should be used for target function (using Spring aware ",(0,n.jsx)(t.code,{children:"SpringDataFetcher"}),") and property resolution."]})]}),(0,n.jsxs)(t.tr,{children:[(0,n.jsx)(t.td,{style:{textAlign:"left"},children:"KotlinDataLoader (optional)"}),(0,n.jsxs)(t.td,{style:{textAlign:"left"},children:["Any number of beans created that implement ",(0,n.jsx)(t.code,{children:"KotlinDataLoader"}),". See ",(0,n.jsx)(t.a,{href:"/graphql-kotlin/docs/5.x.x/server/data-loaders",children:"Data Loaders"})," for more details."]})]}),(0,n.jsxs)(t.tr,{children:[(0,n.jsx)(t.td,{style:{textAlign:"left"},children:"DataLoaderRegistryFactory"}),(0,n.jsxs)(t.td,{style:{textAlign:"left"},children:["A factory class that creates a ",(0,n.jsx)(t.code,{children:"DataLoaderRegistry"})," of all the ",(0,n.jsx)(t.code,{children:"KotlinDataLoaders"}),". Defaults to empty registry."]})]})]})]}),"\n",(0,n.jsx)(t.h2,{id:"non-federated-schema",children:"Non-Federated Schema"}),"\n",(0,n.jsx)(t.admonition,{type:"note",children:(0,n.jsx)(t.p,{children:(0,n.jsxs)(t.em,{children:["Created only if federation is ",(0,n.jsx)(t.strong,{children:"disabled"})]})})}),"\n",(0,n.jsxs)(t.table,{children:[(0,n.jsx)(t.thead,{children:(0,n.jsxs)(t.tr,{children:[(0,n.jsx)(t.th,{style:{textAlign:"left"},children:"Bean"}),(0,n.jsx)(t.th,{style:{textAlign:"left"},children:"Description"})]})}),(0,n.jsxs)(t.tbody,{children:[(0,n.jsxs)(t.tr,{children:[(0,n.jsx)(t.td,{style:{textAlign:"left"},children:"SchemaGeneratorConfig"}),(0,n.jsxs)(t.td,{style:{textAlign:"left"},children:["Schema generator configuration information, see ",(0,n.jsx)(t.a,{href:"/graphql-kotlin/docs/5.x.x/schema-generator/customizing-schemas/generator-config",children:"Schema Generator Configuration"})," for details. Can be customized by providing ",(0,n.jsx)(t.code,{children:"TopLevelNames"}),", ",(0,n.jsx)(t.a,{href:"/graphql-kotlin/docs/5.x.x/schema-generator/customizing-schemas/generator-config",children:"SchemaGeneratorHooks"})," and ",(0,n.jsx)(t.code,{children:"KotlinDataFetcherFactoryProvider"})," beans."]})]}),(0,n.jsxs)(t.tr,{children:[(0,n.jsx)(t.td,{style:{textAlign:"left"},children:"GraphQLSchema"}),(0,n.jsxs)(t.td,{style:{textAlign:"left"},children:["GraphQL schema generated based on the schema generator configuration and  ",(0,n.jsx)(t.code,{children:"Query"}),", ",(0,n.jsx)(t.code,{children:"Mutation"})," and ",(0,n.jsx)(t.code,{children:"Subscription"})," objects available in the application context."]})]})]})]}),"\n",(0,n.jsx)(t.h2,{id:"federated-schema",children:"Federated Schema"}),"\n",(0,n.jsx)(t.admonition,{type:"note",children:(0,n.jsx)(t.p,{children:(0,n.jsxs)(t.em,{children:["Created only if federation is ",(0,n.jsx)(t.strong,{children:"enabled"})]})})}),"\n",(0,n.jsxs)(t.table,{children:[(0,n.jsx)(t.thead,{children:(0,n.jsxs)(t.tr,{children:[(0,n.jsx)(t.th,{style:{textAlign:"left"},children:"Bean"}),(0,n.jsx)(t.th,{style:{textAlign:"left"},children:"Description"})]})}),(0,n.jsxs)(t.tbody,{children:[(0,n.jsxs)(t.tr,{children:[(0,n.jsx)(t.td,{style:{textAlign:"left"},children:"FederatedTypeResolvers"}),(0,n.jsxs)(t.td,{style:{textAlign:"left"},children:["List of ",(0,n.jsx)(t.code,{children:"FederatedTypeResolvers"})," marked as beans that should be added to hooks. See ",(0,n.jsx)(t.a,{href:"/graphql-kotlin/docs/5.x.x/schema-generator/federation/type-resolution",children:"Federated Type Resolution"})," for more details"]})]}),(0,n.jsxs)(t.tr,{children:[(0,n.jsx)(t.td,{style:{textAlign:"left"},children:"FederatedSchemaGeneratorHooks"}),(0,n.jsx)(t.td,{style:{textAlign:"left"},children:"Schema generator hooks used to build federated schema"})]}),(0,n.jsxs)(t.tr,{children:[(0,n.jsx)(t.td,{style:{textAlign:"left"},children:"FederatedSchemaGeneratorConfig"}),(0,n.jsxs)(t.td,{style:{textAlign:"left"},children:["Federated schema generator configuration information. You can customize the configuration by providing ",(0,n.jsx)(t.code,{children:"TopLevelNames"}),", ",(0,n.jsx)(t.code,{children:"FederatedSchemaGeneratorHooks"})," and ",(0,n.jsx)(t.code,{children:"KotlinDataFetcherFactoryProvider"})," beans"]})]}),(0,n.jsxs)(t.tr,{children:[(0,n.jsx)(t.td,{style:{textAlign:"left"},children:"FederatedTracingInstrumentation"}),(0,n.jsxs)(t.td,{style:{textAlign:"left"},children:["If ",(0,n.jsx)(t.code,{children:"graphql.federation.tracing.enabled"})," is true, it adds tracing info to the response via the ",(0,n.jsx)(t.a,{href:"https://github.com/apollographql/federation-jvm",children:"apollo federation-jvm"})," library."]})]}),(0,n.jsxs)(t.tr,{children:[(0,n.jsx)(t.td,{style:{textAlign:"left"},children:"GraphQLSchema"}),(0,n.jsxs)(t.td,{style:{textAlign:"left"},children:["GraphQL schema generated based on the federated schema generator configuration and  ",(0,n.jsx)(t.code,{children:"Query"}),", ",(0,n.jsx)(t.code,{children:"Mutation"})," and ",(0,n.jsx)(t.code,{children:"Subscription"})," objects available in the application context."]})]})]})]}),"\n",(0,n.jsx)(t.h2,{id:"graphql-configuration",children:"GraphQL Configuration"}),"\n",(0,n.jsxs)(t.table,{children:[(0,n.jsx)(t.thead,{children:(0,n.jsxs)(t.tr,{children:[(0,n.jsx)(t.th,{style:{textAlign:"left"},children:"Bean"}),(0,n.jsx)(t.th,{style:{textAlign:"left"},children:"Description"})]})}),(0,n.jsxs)(t.tbody,{children:[(0,n.jsxs)(t.tr,{children:[(0,n.jsx)(t.td,{style:{textAlign:"left"},children:"Instrumentation (optional)"}),(0,n.jsxs)(t.td,{style:{textAlign:"left"},children:["Any number of beans created that implement ",(0,n.jsx)(t.code,{children:"graphql-java"})," ",(0,n.jsx)(t.a,{href:"https://www.graphql-java.com/documentation/v16/instrumentation/",children:"Instrumentation"})," will be pulled in. The beans can be ordered by implementing the Spring ",(0,n.jsx)(t.code,{children:"Ordered"})," interface."]})]}),(0,n.jsxs)(t.tr,{children:[(0,n.jsx)(t.td,{style:{textAlign:"left"},children:"ExecutionIdProvider (optional)"}),(0,n.jsxs)(t.td,{style:{textAlign:"left"},children:["Any number of beans created that implement ",(0,n.jsx)(t.code,{children:"graphql-java"})," ",(0,n.jsx)(t.a,{href:"https://github.com/graphql-java/graphql-java/blob/master/src/main/java/graphql/execution/ExecutionIdProvider.java",children:"ExecutionIdProvider"})," will be pulled in."]})]}),(0,n.jsxs)(t.tr,{children:[(0,n.jsx)(t.td,{style:{textAlign:"left"},children:"PreparsedDocumentProvider (optional)"}),(0,n.jsxs)(t.td,{style:{textAlign:"left"},children:["Any number of beans created that implement ",(0,n.jsx)(t.code,{children:"graphql-java"})," ",(0,n.jsx)(t.a,{href:"https://github.com/graphql-java/graphql-java/blob/master/src/main/java/graphql/execution/preparsed/PreparsedDocumentProvider.java",children:"PreparsedDocumentProvider"})," will be pulled in."]})]}),(0,n.jsxs)(t.tr,{children:[(0,n.jsx)(t.td,{style:{textAlign:"left"},children:"GraphQL"}),(0,n.jsxs)(t.td,{style:{textAlign:"left"},children:["GraphQL execution object generated using ",(0,n.jsx)(t.code,{children:"GraphQLSchema"})," with default async execution strategies. The GraphQL object can be customized by optionally providing the above beans in the application context."]})]}),(0,n.jsxs)(t.tr,{children:[(0,n.jsx)(t.td,{style:{textAlign:"left"},children:"SpringGraphQLRequestParser"}),(0,n.jsxs)(t.td,{style:{textAlign:"left"},children:["Provides the Spring specific logic for parsing the HTTP request into a common GraphQLRequest. See ",(0,n.jsx)(t.a,{href:"/graphql-kotlin/docs/5.x.x/server/graphql-request-parser",children:"GraphQLRequestParser"})]})]}),(0,n.jsxs)(t.tr,{children:[(0,n.jsx)(t.td,{style:{textAlign:"left"},children:"SpringGraphQLContextFactory"}),(0,n.jsxs)(t.td,{style:{textAlign:"left"},children:["Spring specific factory that uses the ",(0,n.jsx)(t.code,{children:"ServerRequest"}),". The ",(0,n.jsx)(t.code,{children:"GraphQLContext"})," generated can be any object. See ",(0,n.jsx)(t.a,{href:"/graphql-kotlin/docs/5.x.x/server/graphql-context-factory",children:"GraphQLContextFactory"}),"."]})]}),(0,n.jsxs)(t.tr,{children:[(0,n.jsx)(t.td,{style:{textAlign:"left"},children:"GraphQLRequestHandler"}),(0,n.jsxs)(t.td,{style:{textAlign:"left"},children:["Handler invoked from ",(0,n.jsx)(t.code,{children:"GraphQLServer"})," that executes the incoming request, defaults to ",(0,n.jsx)(t.a,{href:"/graphql-kotlin/docs/5.x.x/server/graphql-request-handler",children:"GraphQLRequestHandler"}),"."]})]}),(0,n.jsxs)(t.tr,{children:[(0,n.jsx)(t.td,{style:{textAlign:"left"},children:"SpringGraphQLServer"}),(0,n.jsxs)(t.td,{style:{textAlign:"left"},children:["Spring specific object that takes in a ",(0,n.jsx)(t.code,{children:"ServerRequest"})," and returns a ",(0,n.jsx)(t.code,{children:"GraphQLResponse"})," using all the above implementations. See ",(0,n.jsx)(t.a,{href:"/graphql-kotlin/docs/5.x.x/server/graphql-server",children:"GraphQLServer"})]})]})]})]}),"\n",(0,n.jsx)(t.h2,{id:"subscriptions",children:"Subscriptions"}),"\n",(0,n.jsx)(t.admonition,{type:"note",children:(0,n.jsx)(t.p,{children:(0,n.jsxs)(t.em,{children:["Created only if the ",(0,n.jsx)(t.code,{children:"Subscription"})," marker interface is used"]})})}),"\n",(0,n.jsxs)(t.table,{children:[(0,n.jsx)(t.thead,{children:(0,n.jsxs)(t.tr,{children:[(0,n.jsx)(t.th,{style:{textAlign:"left"},children:"Bean"}),(0,n.jsx)(t.th,{style:{textAlign:"left"},children:"Description"})]})}),(0,n.jsxs)(t.tbody,{children:[(0,n.jsxs)(t.tr,{children:[(0,n.jsx)(t.td,{style:{textAlign:"left"},children:"SpringGraphQLSubscriptionHandler"}),(0,n.jsx)(t.td,{style:{textAlign:"left"},children:"Spring reactor code for executing GraphQL subscriptions requests"})]}),(0,n.jsxs)(t.tr,{children:[(0,n.jsx)(t.td,{style:{textAlign:"left"},children:"WebSocketHandlerAdapter"}),(0,n.jsxs)(t.td,{style:{textAlign:"left"},children:["Spring class for handling web socket http requests. See ",(0,n.jsx)(t.a,{href:"https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/reactive/socket/server/support/WebSocketHandlerAdapter.html",children:"Spring documentation"})]})]}),(0,n.jsxs)(t.tr,{children:[(0,n.jsx)(t.td,{style:{textAlign:"left"},children:"ApolloSubscriptionHooks"}),(0,n.jsxs)(t.td,{style:{textAlign:"left"},children:["Provides hooks into the subscription request lifecycle. See ",(0,n.jsx)(t.a,{href:"/graphql-kotlin/docs/5.x.x/server/spring-server/spring-subscriptions",children:"the subscription docs"})]})]}),(0,n.jsxs)(t.tr,{children:[(0,n.jsx)(t.td,{style:{textAlign:"left"},children:"SpringSubscriptionGraphQLContextFactory"}),(0,n.jsxs)(t.td,{style:{textAlign:"left"},children:["Spring specific factory that uses the ",(0,n.jsx)(t.code,{children:"WebSocketSession"}),". See ",(0,n.jsx)(t.a,{href:"/graphql-kotlin/docs/5.x.x/server/graphql-context-factory",children:"GraphQLContextFactory"}),"."]})]})]})]}),"\n",(0,n.jsx)(t.h2,{id:"fixed-beans",children:"Fixed Beans"}),"\n",(0,n.jsx)(t.p,{children:"The following beans cannot be overridden, but may have options to disable them:"}),"\n",(0,n.jsxs)(t.ul,{children:["\n",(0,n.jsx)(t.li,{children:"Route handler for GraphQL queries and mutations endpoint."}),"\n",(0,n.jsx)(t.li,{children:"Route handler for the SDL endpoint. Created only if sdl route is enabled."}),"\n",(0,n.jsxs)(t.li,{children:["Route handler for ",(0,n.jsx)(t.a,{href:"https://github.com/prisma-labs/graphql-playground",children:"Prisma Labs Playground"}),". Created only if playground is enabled"]}),"\n",(0,n.jsx)(t.li,{children:"Route handler for the subscriptions endpoint. Created only if subscriptions are used."}),"\n",(0,n.jsxs)(t.li,{children:[(0,n.jsx)(t.code,{children:"ApolloSubscriptionProtocolHandler"})," for handling GraphQL subscriptions using the ",(0,n.jsxs)(t.a,{href:"https://github.com/apollographql/subscriptions-transport-ws/blob/master/PROTOCOL.md",children:[(0,n.jsx)(t.code,{children:"graphql-ws"})," protocol"]}),". Created only if subscriptions are used."]}),"\n",(0,n.jsxs)(t.li,{children:[(0,n.jsx)(t.code,{children:"SubscriptionWebSocketHandler"})," that utilizes above subscription protocol handler. Created only if subscriptions are used."]}),"\n"]})]})}function h(e={}){const{wrapper:t}={...(0,s.R)(),...e.components};return t?(0,n.jsx)(t,{...e,children:(0,n.jsx)(c,{...e})}):c(e)}},28453:(e,t,r)=>{r.d(t,{R:()=>l,x:()=>a});var n=r(96540);const s={},i=n.createContext(s);function l(e){const t=n.useContext(i);return n.useMemo((function(){return"function"==typeof e?e(t):{...t,...e}}),[t,e])}function a(e){let t;return t=e.disableParentContext?"function"==typeof e.components?e.components(s):e.components||s:l(e.components),n.createElement(i.Provider,{value:t},e.children)}}}]);