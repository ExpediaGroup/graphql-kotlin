"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[7419],{3905:(e,t,a)=>{a.d(t,{Zo:()=>s,kt:()=>g});var r=a(67294);function n(e,t,a){return t in e?Object.defineProperty(e,t,{value:a,enumerable:!0,configurable:!0,writable:!0}):e[t]=a,e}function i(e,t){var a=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),a.push.apply(a,r)}return a}function o(e){for(var t=1;t<arguments.length;t++){var a=null!=arguments[t]?arguments[t]:{};t%2?i(Object(a),!0).forEach((function(t){n(e,t,a[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(a)):i(Object(a)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(a,t))}))}return e}function l(e,t){if(null==e)return{};var a,r,n=function(e,t){if(null==e)return{};var a,r,n={},i=Object.keys(e);for(r=0;r<i.length;r++)a=i[r],t.indexOf(a)>=0||(n[a]=e[a]);return n}(e,t);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(r=0;r<i.length;r++)a=i[r],t.indexOf(a)>=0||Object.prototype.propertyIsEnumerable.call(e,a)&&(n[a]=e[a])}return n}var p=r.createContext({}),d=function(e){var t=r.useContext(p),a=t;return e&&(a="function"==typeof e?e(t):o(o({},t),e)),a},s=function(e){var t=d(e.components);return r.createElement(p.Provider,{value:t},e.children)},m="mdxType",c={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},h=r.forwardRef((function(e,t){var a=e.components,n=e.mdxType,i=e.originalType,p=e.parentName,s=l(e,["components","mdxType","originalType","parentName"]),m=d(a),h=n,g=m["".concat(p,".").concat(h)]||m[h]||c[h]||i;return a?r.createElement(g,o(o({ref:t},s),{},{components:a})):r.createElement(g,o({ref:t},s))}));function g(e,t){var a=arguments,n=t&&t.mdxType;if("string"==typeof e||n){var i=a.length,o=new Array(i);o[0]=h;var l={};for(var p in t)hasOwnProperty.call(t,p)&&(l[p]=t[p]);l.originalType=e,l[m]="string"==typeof e?e:n,o[1]=l;for(var d=2;d<i;d++)o[d]=a[d];return r.createElement.apply(null,o)}return r.createElement.apply(null,a)}h.displayName="MDXCreateElement"},1995:(e,t,a)=>{a.r(t),a.d(t,{assets:()=>s,contentTitle:()=>p,default:()=>g,frontMatter:()=>l,metadata:()=>d,toc:()=>m});var r=a(87462),n=a(63366),i=(a(67294),a(3905)),o=["components"],l={id:"spring-beans",title:"Automatically Created Beans"},p=void 0,d={unversionedId:"server/spring-server/spring-beans",id:"version-6.x.x/server/spring-server/spring-beans",title:"Automatically Created Beans",description:"graphql-kotlin-spring-server automatically creates all the necessary beans to start a GraphQL server.",source:"@site/versioned_docs/version-6.x.x/server/spring-server/spring-beans.md",sourceDirName:"server/spring-server",slug:"/server/spring-server/spring-beans",permalink:"/graphql-kotlin/docs/6.x.x/server/spring-server/spring-beans",draft:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-6.x.x/server/spring-server/spring-beans.md",tags:[],version:"6.x.x",lastUpdatedBy:"eocantu",lastUpdatedAt:1697752782,formattedLastUpdatedAt:"Oct 19, 2023",frontMatter:{id:"spring-beans",title:"Automatically Created Beans"},sidebar:"docs",previous:{title:"HTTP Request and Response",permalink:"/graphql-kotlin/docs/6.x.x/server/spring-server/spring-http-request-response"},next:{title:"Configuration Properties",permalink:"/graphql-kotlin/docs/6.x.x/server/spring-server/spring-properties"}},s={},m=[{value:"Execution",id:"execution",level:2},{value:"Non-Federated Schema",id:"non-federated-schema",level:2},{value:"Federated Schema",id:"federated-schema",level:2},{value:"GraphQL Configuration",id:"graphql-configuration",level:2},{value:"Subscriptions",id:"subscriptions",level:2},{value:"Fixed Beans",id:"fixed-beans",level:2}],c={toc:m},h="wrapper";function g(e){var t=e.components,a=(0,n.Z)(e,o);return(0,i.kt)(h,(0,r.Z)({},c,a,{components:t,mdxType:"MDXLayout"}),(0,i.kt)("p",null,(0,i.kt)("inlineCode",{parentName:"p"},"graphql-kotlin-spring-server")," automatically creates all the necessary beans to start a GraphQL server.\nMany of the beans are conditionally created and the default behavior can be customized by providing custom overriding beans in your application context."),(0,i.kt)("h2",{id:"execution"},"Execution"),(0,i.kt)("table",null,(0,i.kt)("thead",{parentName:"table"},(0,i.kt)("tr",{parentName:"thead"},(0,i.kt)("th",{parentName:"tr",align:"left"},"Bean"),(0,i.kt)("th",{parentName:"tr",align:"left"},"Description"))),(0,i.kt)("tbody",{parentName:"table"},(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:"left"},"DataFetcherExceptionHandler"),(0,i.kt)("td",{parentName:"tr",align:"left"},"GraphQL exception handler used from the various execution strategies, defaults to ",(0,i.kt)("a",{parentName:"td",href:"https://www.graphql-java.com/documentation/v16/execution/"},"SimpleDataFetcherExceptionHandler")," from graphql-java.")),(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:"left"},"KotlinDataFetcherFactoryProvider"),(0,i.kt)("td",{parentName:"tr",align:"left"},"Factory used during schema construction to obtain ",(0,i.kt)("inlineCode",{parentName:"td"},"DataFetcherFactory")," that should be used for target function (using Spring aware ",(0,i.kt)("inlineCode",{parentName:"td"},"SpringDataFetcher"),") and property resolution.")),(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:"left"},"KotlinDataLoader (optional)"),(0,i.kt)("td",{parentName:"tr",align:"left"},"Any number of beans created that implement ",(0,i.kt)("inlineCode",{parentName:"td"},"KotlinDataLoader"),". See ",(0,i.kt)("a",{parentName:"td",href:"/graphql-kotlin/docs/6.x.x/server/data-loader/"},"Data Loaders")," for more details.")),(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:"left"},"KotlinDataLoaderRegistryFactory"),(0,i.kt)("td",{parentName:"tr",align:"left"},"A factory class that creates a ",(0,i.kt)("inlineCode",{parentName:"td"},"KotlinDataLoaderRegistry")," of all the ",(0,i.kt)("inlineCode",{parentName:"td"},"KotlinDataLoaders"),". Defaults to empty registry.")))),(0,i.kt)("h2",{id:"non-federated-schema"},"Non-Federated Schema"),(0,i.kt)("admonition",{type:"note"},(0,i.kt)("p",{parentName:"admonition"},(0,i.kt)("em",{parentName:"p"},"Created only if federation is ",(0,i.kt)("strong",{parentName:"em"},"disabled")))),(0,i.kt)("table",null,(0,i.kt)("thead",{parentName:"table"},(0,i.kt)("tr",{parentName:"thead"},(0,i.kt)("th",{parentName:"tr",align:"left"},"Bean"),(0,i.kt)("th",{parentName:"tr",align:"left"},"Description"))),(0,i.kt)("tbody",{parentName:"table"},(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:"left"},"SchemaGeneratorConfig"),(0,i.kt)("td",{parentName:"tr",align:"left"},"Schema generator configuration information, see ",(0,i.kt)("a",{parentName:"td",href:"/graphql-kotlin/docs/6.x.x/schema-generator/customizing-schemas/generator-config"},"Schema Generator Configuration")," for details. Can be customized by providing ",(0,i.kt)("inlineCode",{parentName:"td"},"TopLevelNames"),", ",(0,i.kt)("a",{parentName:"td",href:"/graphql-kotlin/docs/6.x.x/schema-generator/customizing-schemas/generator-config"},"SchemaGeneratorHooks")," and ",(0,i.kt)("inlineCode",{parentName:"td"},"KotlinDataFetcherFactoryProvider")," beans.")),(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:"left"},"GraphQLSchema"),(0,i.kt)("td",{parentName:"tr",align:"left"},"GraphQL schema generated based on the schema generator configuration and  ",(0,i.kt)("inlineCode",{parentName:"td"},"Query"),", ",(0,i.kt)("inlineCode",{parentName:"td"},"Mutation")," and ",(0,i.kt)("inlineCode",{parentName:"td"},"Subscription")," objects available in the application context.")))),(0,i.kt)("h2",{id:"federated-schema"},"Federated Schema"),(0,i.kt)("admonition",{type:"note"},(0,i.kt)("p",{parentName:"admonition"},(0,i.kt)("em",{parentName:"p"},"Created only if federation is ",(0,i.kt)("strong",{parentName:"em"},"enabled")))),(0,i.kt)("table",null,(0,i.kt)("thead",{parentName:"table"},(0,i.kt)("tr",{parentName:"thead"},(0,i.kt)("th",{parentName:"tr",align:"left"},"Bean"),(0,i.kt)("th",{parentName:"tr",align:"left"},"Description"))),(0,i.kt)("tbody",{parentName:"table"},(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:"left"},"FederatedTypeResolvers"),(0,i.kt)("td",{parentName:"tr",align:"left"},"List of ",(0,i.kt)("inlineCode",{parentName:"td"},"FederatedTypeResolvers")," marked as beans that should be added to hooks. See ",(0,i.kt)("a",{parentName:"td",href:"/graphql-kotlin/docs/6.x.x/schema-generator/federation/type-resolution"},"Federated Type Resolution")," for more details")),(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:"left"},"FederatedSchemaGeneratorHooks"),(0,i.kt)("td",{parentName:"tr",align:"left"},"Schema generator hooks used to build federated schema")),(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:"left"},"FederatedSchemaGeneratorConfig"),(0,i.kt)("td",{parentName:"tr",align:"left"},"Federated schema generator configuration information. You can customize the configuration by providing ",(0,i.kt)("inlineCode",{parentName:"td"},"TopLevelNames"),", ",(0,i.kt)("inlineCode",{parentName:"td"},"FederatedSchemaGeneratorHooks")," and ",(0,i.kt)("inlineCode",{parentName:"td"},"KotlinDataFetcherFactoryProvider")," beans")),(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:"left"},"FederatedTracingInstrumentation"),(0,i.kt)("td",{parentName:"tr",align:"left"},"If ",(0,i.kt)("inlineCode",{parentName:"td"},"graphql.federation.tracing.enabled")," is true, it adds tracing info to the response via the ",(0,i.kt)("a",{parentName:"td",href:"https://github.com/apollographql/federation-jvm"},"apollo federation-jvm")," library.")),(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:"left"},"GraphQLSchema"),(0,i.kt)("td",{parentName:"tr",align:"left"},"GraphQL schema generated based on the federated schema generator configuration and  ",(0,i.kt)("inlineCode",{parentName:"td"},"Query"),", ",(0,i.kt)("inlineCode",{parentName:"td"},"Mutation")," and ",(0,i.kt)("inlineCode",{parentName:"td"},"Subscription")," objects available in the application context.")))),(0,i.kt)("h2",{id:"graphql-configuration"},"GraphQL Configuration"),(0,i.kt)("table",null,(0,i.kt)("thead",{parentName:"table"},(0,i.kt)("tr",{parentName:"thead"},(0,i.kt)("th",{parentName:"tr",align:"left"},"Bean"),(0,i.kt)("th",{parentName:"tr",align:"left"},"Description"))),(0,i.kt)("tbody",{parentName:"table"},(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:"left"},"Instrumentation (optional)"),(0,i.kt)("td",{parentName:"tr",align:"left"},"Any number of beans created that implement ",(0,i.kt)("inlineCode",{parentName:"td"},"graphql-java")," ",(0,i.kt)("a",{parentName:"td",href:"https://www.graphql-java.com/documentation/v16/instrumentation/"},"Instrumentation")," will be pulled in. The beans can be ordered by implementing the Spring ",(0,i.kt)("inlineCode",{parentName:"td"},"Ordered")," interface.")),(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:"left"},"ExecutionIdProvider (optional)"),(0,i.kt)("td",{parentName:"tr",align:"left"},"Any number of beans created that implement ",(0,i.kt)("inlineCode",{parentName:"td"},"graphql-java")," ",(0,i.kt)("a",{parentName:"td",href:"https://github.com/graphql-java/graphql-java/blob/master/src/main/java/graphql/execution/ExecutionIdProvider.java"},"ExecutionIdProvider")," will be pulled in.")),(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:"left"},"PreparsedDocumentProvider (optional)"),(0,i.kt)("td",{parentName:"tr",align:"left"},"Any number of beans created that implement ",(0,i.kt)("inlineCode",{parentName:"td"},"graphql-java")," ",(0,i.kt)("a",{parentName:"td",href:"https://github.com/graphql-java/graphql-java/blob/master/src/main/java/graphql/execution/preparsed/PreparsedDocumentProvider.java"},"PreparsedDocumentProvider")," will be pulled in.")),(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:"left"},"GraphQL"),(0,i.kt)("td",{parentName:"tr",align:"left"},"GraphQL execution object generated using ",(0,i.kt)("inlineCode",{parentName:"td"},"GraphQLSchema")," with default async execution strategies. The GraphQL object can be customized by optionally providing the above beans in the application context.")),(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:"left"},"SpringGraphQLRequestParser"),(0,i.kt)("td",{parentName:"tr",align:"left"},"Provides the Spring specific logic for parsing the HTTP request into a common GraphQLRequest. See ",(0,i.kt)("a",{parentName:"td",href:"/graphql-kotlin/docs/6.x.x/server/graphql-request-parser"},"GraphQLRequestParser"))),(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:"left"},"SpringGraphQLContextFactory"),(0,i.kt)("td",{parentName:"tr",align:"left"},"Spring specific factory that uses the ",(0,i.kt)("inlineCode",{parentName:"td"},"ServerRequest"),". The ",(0,i.kt)("inlineCode",{parentName:"td"},"GraphQLContext")," generated can be any object. See ",(0,i.kt)("a",{parentName:"td",href:"/graphql-kotlin/docs/6.x.x/server/graphql-context-factory"},"GraphQLContextFactory"),".")),(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:"left"},"GraphQLRequestHandler"),(0,i.kt)("td",{parentName:"tr",align:"left"},"Handler invoked from ",(0,i.kt)("inlineCode",{parentName:"td"},"GraphQLServer")," that executes the incoming request, defaults to ",(0,i.kt)("a",{parentName:"td",href:"/graphql-kotlin/docs/6.x.x/server/graphql-request-handler"},"GraphQLRequestHandler"),".")),(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:"left"},"SpringGraphQLServer"),(0,i.kt)("td",{parentName:"tr",align:"left"},"Spring specific object that takes in a ",(0,i.kt)("inlineCode",{parentName:"td"},"ServerRequest")," and returns a ",(0,i.kt)("inlineCode",{parentName:"td"},"GraphQLResponse")," using all the above implementations. See ",(0,i.kt)("a",{parentName:"td",href:"/graphql-kotlin/docs/6.x.x/server/graphql-server"},"GraphQLServer"))),(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:"left"},"IDValueUnboxer"),(0,i.kt)("td",{parentName:"tr",align:"left"},"Value unboxer that provides support for handling ID value class")))),(0,i.kt)("h2",{id:"subscriptions"},"Subscriptions"),(0,i.kt)("admonition",{type:"note"},(0,i.kt)("p",{parentName:"admonition"},(0,i.kt)("em",{parentName:"p"},"Created only if the ",(0,i.kt)("inlineCode",{parentName:"em"},"Subscription")," marker interface is used"))),(0,i.kt)("table",null,(0,i.kt)("thead",{parentName:"table"},(0,i.kt)("tr",{parentName:"thead"},(0,i.kt)("th",{parentName:"tr",align:"left"},"Bean"),(0,i.kt)("th",{parentName:"tr",align:"left"},"Description"))),(0,i.kt)("tbody",{parentName:"table"},(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:"left"},"SpringGraphQLSubscriptionHandler"),(0,i.kt)("td",{parentName:"tr",align:"left"},"Spring reactor code for executing GraphQL subscriptions requests")),(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:"left"},"WebSocketHandlerAdapter"),(0,i.kt)("td",{parentName:"tr",align:"left"},"Spring class for handling web socket http requests. See ",(0,i.kt)("a",{parentName:"td",href:"https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/reactive/socket/server/support/WebSocketHandlerAdapter.html"},"Spring documentation"))),(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:"left"},"ApolloSubscriptionHooks"),(0,i.kt)("td",{parentName:"tr",align:"left"},"Provides hooks into the subscription request lifecycle. See ",(0,i.kt)("a",{parentName:"td",href:"/graphql-kotlin/docs/6.x.x/server/spring-server/spring-subscriptions"},"the subscription docs"))),(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:"left"},"SpringSubscriptionGraphQLContextFactory"),(0,i.kt)("td",{parentName:"tr",align:"left"},"Spring specific factory that uses the ",(0,i.kt)("inlineCode",{parentName:"td"},"WebSocketSession"),". See ",(0,i.kt)("a",{parentName:"td",href:"/graphql-kotlin/docs/6.x.x/server/graphql-context-factory"},"GraphQLContextFactory"),".")))),(0,i.kt)("h2",{id:"fixed-beans"},"Fixed Beans"),(0,i.kt)("p",null,"The following beans cannot be overridden, but may have options to disable them:"),(0,i.kt)("ul",null,(0,i.kt)("li",{parentName:"ul"},"Route handler for GraphQL queries and mutations endpoint."),(0,i.kt)("li",{parentName:"ul"},"Route handler for the SDL endpoint. Created only if sdl route is enabled."),(0,i.kt)("li",{parentName:"ul"},"Route handler for ",(0,i.kt)("a",{parentName:"li",href:"https://github.com/prisma-labs/graphql-playground"},"Prisma Labs Playground"),". Created only if playground is enabled"),(0,i.kt)("li",{parentName:"ul"},"Route handler for the subscriptions endpoint. Created only if subscriptions are used."),(0,i.kt)("li",{parentName:"ul"},(0,i.kt)("inlineCode",{parentName:"li"},"ApolloSubscriptionProtocolHandler")," for handling GraphQL subscriptions using the ",(0,i.kt)("a",{parentName:"li",href:"https://github.com/apollographql/subscriptions-transport-ws/blob/master/PROTOCOL.md"},(0,i.kt)("inlineCode",{parentName:"a"},"graphql-ws")," protocol"),". Created only if subscriptions are used."),(0,i.kt)("li",{parentName:"ul"},(0,i.kt)("inlineCode",{parentName:"li"},"SubscriptionWebSocketHandler")," that utilizes above subscription protocol handler. Created only if subscriptions are used.")))}g.isMDXComponent=!0}}]);