"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[989],{16570:(e,o,i)=>{i.r(o),i.d(o,{assets:()=>u,contentTitle:()=>l,default:()=>m,frontMatter:()=>p,metadata:()=>a,toc:()=>c});var t=i(87462),r=i(63366),s=(i(67294),i(3905)),n=(i(95657),["components"]),p={id:"subscriptions",title:"Subscriptions",original_id:"subscriptions"},l=void 0,a={unversionedId:"spring-server/subscriptions",id:"version-3.x.x/spring-server/subscriptions",title:"Subscriptions",description:"Schema",source:"@site/versioned_docs/version-3.x.x/spring-server/subscriptions.md",sourceDirName:"spring-server",slug:"/spring-server/subscriptions",permalink:"/graphql-kotlin/docs/3.x.x/spring-server/subscriptions",draft:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-3.x.x/spring-server/subscriptions.md",tags:[],version:"3.x.x",lastUpdatedBy:"Samuel Vazquez",lastUpdatedAt:1685659104,formattedLastUpdatedAt:"Jun 1, 2023",frontMatter:{id:"subscriptions",title:"Subscriptions",original_id:"subscriptions"},sidebar:"version-3.x.x/docs",previous:{title:"Access the HTTP Request-Response",permalink:"/graphql-kotlin/docs/3.x.x/spring-server/http-request-response"},next:{title:"Client Overview",permalink:"/graphql-kotlin/docs/3.x.x/client/client-overview"}},u={},c=[{value:"Schema",id:"schema",level:2},{value:"<code>graphql-ws</code> subprotocol",id:"graphql-ws-subprotocol",level:2},{value:"Overview",id:"overview",level:3},{value:"Subscription Hooks",id:"subscription-hooks",level:3},{value:"Example",id:"example",level:2}],h={toc:c},d="wrapper";function m(e){var o=e.components,i=(0,r.Z)(e,n);return(0,s.kt)(d,(0,t.Z)({},h,i,{components:o,mdxType:"MDXLayout"}),(0,s.kt)("h2",{id:"schema"},"Schema"),(0,s.kt)("p",null,"To see more details of how to implement subscriptions in your schema, see ",(0,s.kt)("a",{parentName:"p",href:"/graphql-kotlin/docs/3.x.x/schema-generator/execution/subscriptions"},"executing subscriptions"),"."),(0,s.kt)("h2",{id:"graphql-ws-subprotocol"},(0,s.kt)("inlineCode",{parentName:"h2"},"graphql-ws")," subprotocol"),(0,s.kt)("h3",{id:"overview"},"Overview"),(0,s.kt)("p",null,"We have implemented subscriptions in Spring WebSockets following the ",(0,s.kt)("a",{parentName:"p",href:"https://github.com/apollographql/subscriptions-transport-ws/blob/3.x.x/PROTOCOL.md"},(0,s.kt)("inlineCode",{parentName:"a"},"graphql-ws")," subprotocol")," defined by Apollo. This requires that your client send and parse messages in a specific format."),(0,s.kt)("p",null,"You can see more details in the file ",(0,s.kt)("a",{parentName:"p",href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/3.x.x/graphql-kotlin-spring-server/src/main/kotlin/com/expediagroup/graphql/spring/execution/ApolloSubscriptionProtocolHandler.kt"},"ApolloSubscriptionProtocolHandler"),"."),(0,s.kt)("p",null,"If you would like to implement your own subscription handler, you can provide a primary spring bean for ",(0,s.kt)("inlineCode",{parentName:"p"},"HandlerMapping")," that overrides the ",(0,s.kt)("a",{parentName:"p",href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/3.x.x/graphql-kotlin-spring-server/src/main/kotlin/com/expediagroup/graphql/spring/SubscriptionAutoConfiguration.kt"},"default one")," which sets the url for subscriptions to the Apollo subscription handler."),(0,s.kt)("h3",{id:"subscription-hooks"},"Subscription Hooks"),(0,s.kt)("p",null,"In line with the protocol, we have implemented hooks to execute functions at different stages of the connection lifecycle:"),(0,s.kt)("ul",null,(0,s.kt)("li",{parentName:"ul"},"onConnect"),(0,s.kt)("li",{parentName:"ul"},"onOperation"),(0,s.kt)("li",{parentName:"ul"},"onOperationComplete"),(0,s.kt)("li",{parentName:"ul"},"onDisconnect")),(0,s.kt)("p",null,"You can see more details in the file ",(0,s.kt)("a",{parentName:"p",href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/3.x.x/graphql-kotlin-spring-server/src/main/kotlin/com/expediagroup/graphql/spring/execution/ApolloSubscriptionHooks.kt"},"ApolloSubscriptionHooks"),"."),(0,s.kt)("p",null,"If you would like to implement your own subscription hooks, you can provide a primary spring bean for ",(0,s.kt)("inlineCode",{parentName:"p"},"ApolloSubscriptionHooks")," that overrides the ",(0,s.kt)("a",{parentName:"p",href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/3.x.x/graphql-kotlin-spring-server/src/main/kotlin/com/expediagroup/graphql/spring/SubscriptionAutoConfiguration.kt"},"default one")," which do not perform any actions."),(0,s.kt)("h2",{id:"example"},"Example"),(0,s.kt)("p",null,"You can see an example implementation of a ",(0,s.kt)("inlineCode",{parentName:"p"},"Subscription")," in the ",(0,s.kt)("a",{parentName:"p",href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/3.x.x/examples/spring/src/main/kotlin/com/expediagroup/graphql/examples/subscriptions/SimpleSubscription.kt"},"example app"),"."))}m.isMDXComponent=!0}}]);