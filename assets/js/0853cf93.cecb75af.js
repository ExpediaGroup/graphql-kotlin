"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[9240],{8616:(t,e,a)=>{a.r(e),a.d(e,{assets:()=>s,contentTitle:()=>o,default:()=>k,frontMatter:()=>p,metadata:()=>d,toc:()=>g});var r=a(87462),n=a(63366),l=(a(67294),a(3905)),i=(a(95657),["components"]),p={id:"spring-properties",title:"Configuration Properties"},o=void 0,d={unversionedId:"server/spring-server/spring-properties",id:"server/spring-server/spring-properties",title:"Configuration Properties",description:"graphql-kotlin-spring-server relies on GraphQLConfigurationProperties",source:"@site/docs/server/spring-server/spring-properties.md",sourceDirName:"server/spring-server",slug:"/server/spring-server/spring-properties",permalink:"/graphql-kotlin/docs/7.x.x/server/spring-server/spring-properties",draft:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/docs/server/spring-server/spring-properties.md",tags:[],version:"current",lastUpdatedBy:"Samuel Vazquez",lastUpdatedAt:1685659104,formattedLastUpdatedAt:"Jun 1, 2023",frontMatter:{id:"spring-properties",title:"Configuration Properties"},sidebar:"docs",previous:{title:"Automatically Created Beans",permalink:"/graphql-kotlin/docs/7.x.x/server/spring-server/spring-beans"},next:{title:"Subscriptions",permalink:"/graphql-kotlin/docs/7.x.x/server/spring-server/spring-subscriptions"}},s={},g=[],u={toc:g},m="wrapper";function k(t){var e=t.components,a=(0,n.Z)(t,i);return(0,l.kt)(m,(0,r.Z)({},u,a,{components:e,mdxType:"MDXLayout"}),(0,l.kt)("p",null,(0,l.kt)("inlineCode",{parentName:"p"},"graphql-kotlin-spring-server")," relies on ",(0,l.kt)("a",{parentName:"p",href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/master/servers/graphql-kotlin-spring-server/src/main/kotlin/com/expediagroup/graphql/server/spring/GraphQLConfigurationProperties.kt"},"GraphQLConfigurationProperties"),"\nto provide various customizations of the auto-configuration library. All applicable configuration properties expose ",(0,l.kt)("a",{parentName:"p",href:"https://docs.spring.io/spring-boot/docs/current/reference/html/configuration-metadata.html"},"configuration\nmetadata")," that provide\ndetails on the supported configuration properties."),(0,l.kt)("table",null,(0,l.kt)("thead",{parentName:"table"},(0,l.kt)("tr",{parentName:"thead"},(0,l.kt)("th",{parentName:"tr",align:null},"Property"),(0,l.kt)("th",{parentName:"tr",align:null},"Description"),(0,l.kt)("th",{parentName:"tr",align:null},"Default Value"))),(0,l.kt)("tbody",{parentName:"table"},(0,l.kt)("tr",{parentName:"tbody"},(0,l.kt)("td",{parentName:"tr",align:null},"graphql.endpoint"),(0,l.kt)("td",{parentName:"tr",align:null},"GraphQL server endpoint"),(0,l.kt)("td",{parentName:"tr",align:null},"graphql")),(0,l.kt)("tr",{parentName:"tbody"},(0,l.kt)("td",{parentName:"tr",align:null},"graphql.packages"),(0,l.kt)("td",{parentName:"tr",align:null},"List of supported packages that can contain GraphQL schema type definitions"),(0,l.kt)("td",{parentName:"tr",align:null})),(0,l.kt)("tr",{parentName:"tbody"},(0,l.kt)("td",{parentName:"tr",align:null},"graphql.printSchema"),(0,l.kt)("td",{parentName:"tr",align:null},"Boolean flag indicating whether to print the schema after generator creates it"),(0,l.kt)("td",{parentName:"tr",align:null},"false")),(0,l.kt)("tr",{parentName:"tbody"},(0,l.kt)("td",{parentName:"tr",align:null},"graphql.federation.enabled"),(0,l.kt)("td",{parentName:"tr",align:null},"Boolean flag indicating whether to generate federated GraphQL model"),(0,l.kt)("td",{parentName:"tr",align:null},"false")),(0,l.kt)("tr",{parentName:"tbody"},(0,l.kt)("td",{parentName:"tr",align:null},"graphql.federation.optInV2"),(0,l.kt)("td",{parentName:"tr",align:null},"Boolean flag indicating whether to generate Federation v2 GraphQL model"),(0,l.kt)("td",{parentName:"tr",align:null},"false")),(0,l.kt)("tr",{parentName:"tbody"},(0,l.kt)("td",{parentName:"tr",align:null},"graphql.federation.tracing.enabled"),(0,l.kt)("td",{parentName:"tr",align:null},"Boolean flag indicating whether add federated tracing data to the extensions"),(0,l.kt)("td",{parentName:"tr",align:null},"true (if federation enabled)")),(0,l.kt)("tr",{parentName:"tbody"},(0,l.kt)("td",{parentName:"tr",align:null},"graphql.federation.tracing.debug"),(0,l.kt)("td",{parentName:"tr",align:null},"Boolean flag to log debug info in the federated tracing"),(0,l.kt)("td",{parentName:"tr",align:null},"false (if federation enabled)")),(0,l.kt)("tr",{parentName:"tbody"},(0,l.kt)("td",{parentName:"tr",align:null},"graphql.introspection.enabled"),(0,l.kt)("td",{parentName:"tr",align:null},"Boolean flag indicating whether introspection queries are enabled"),(0,l.kt)("td",{parentName:"tr",align:null},"true")),(0,l.kt)("tr",{parentName:"tbody"},(0,l.kt)("td",{parentName:"tr",align:null},"graphql.playground.enabled"),(0,l.kt)("td",{parentName:"tr",align:null},"Boolean flag indicating whether to enable Prisma Labs Playground GraphQL IDE"),(0,l.kt)("td",{parentName:"tr",align:null},"false")),(0,l.kt)("tr",{parentName:"tbody"},(0,l.kt)("td",{parentName:"tr",align:null},"graphql.playground.endpoint"),(0,l.kt)("td",{parentName:"tr",align:null},"Prisma Labs Playground GraphQL IDE endpoint"),(0,l.kt)("td",{parentName:"tr",align:null},"playground")),(0,l.kt)("tr",{parentName:"tbody"},(0,l.kt)("td",{parentName:"tr",align:null},"graphql.graphiql.enabled"),(0,l.kt)("td",{parentName:"tr",align:null},"Boolean flag indicating whether to enable GraphiQL GraphQL IDE"),(0,l.kt)("td",{parentName:"tr",align:null},"true")),(0,l.kt)("tr",{parentName:"tbody"},(0,l.kt)("td",{parentName:"tr",align:null},"graphql.graphiql.endpoint"),(0,l.kt)("td",{parentName:"tr",align:null},"Prisma Labs Playground GraphQL IDE endpoint"),(0,l.kt)("td",{parentName:"tr",align:null},"graphiql")),(0,l.kt)("tr",{parentName:"tbody"},(0,l.kt)("td",{parentName:"tr",align:null},"graphql.sdl.enabled"),(0,l.kt)("td",{parentName:"tr",align:null},"Boolean flag indicating whether to expose SDL endpoint"),(0,l.kt)("td",{parentName:"tr",align:null},"true")),(0,l.kt)("tr",{parentName:"tbody"},(0,l.kt)("td",{parentName:"tr",align:null},"graphql.sdl.endpoint"),(0,l.kt)("td",{parentName:"tr",align:null},"GraphQL SDL endpoint"),(0,l.kt)("td",{parentName:"tr",align:null},"sdl")),(0,l.kt)("tr",{parentName:"tbody"},(0,l.kt)("td",{parentName:"tr",align:null},"graphql.subscriptions.endpoint"),(0,l.kt)("td",{parentName:"tr",align:null},"GraphQL subscriptions endpoint"),(0,l.kt)("td",{parentName:"tr",align:null},"subscriptions")),(0,l.kt)("tr",{parentName:"tbody"},(0,l.kt)("td",{parentName:"tr",align:null},"graphql.subscriptions.keepAliveInterval"),(0,l.kt)("td",{parentName:"tr",align:null},"Keep the websocket alive and send a message to the client every interval in ms. Defaults to not sending messages"),(0,l.kt)("td",{parentName:"tr",align:null},"null")),(0,l.kt)("tr",{parentName:"tbody"},(0,l.kt)("td",{parentName:"tr",align:null},"graphql.batching.enabled"),(0,l.kt)("td",{parentName:"tr",align:null},"Boolean flag indicating whether to enable custom dataloader instrumentations for 1 or more GraphQL Operations"),(0,l.kt)("td",{parentName:"tr",align:null},"false")),(0,l.kt)("tr",{parentName:"tbody"},(0,l.kt)("td",{parentName:"tr",align:null},"graphql.batching.strategy"),(0,l.kt)("td",{parentName:"tr",align:null},"Configure which custom dataloader instrumentation will be used (LEVEL_DISPATCHED or SYNC_EXHAUSTION)"),(0,l.kt)("td",{parentName:"tr",align:null},"LEVEL_DISPATCHED")))))}k.isMDXComponent=!0}}]);