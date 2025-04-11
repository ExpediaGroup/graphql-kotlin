"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[1034],{91777:(e,i,o)=>{o.r(i),o.d(i,{assets:()=>p,contentTitle:()=>t,default:()=>h,frontMatter:()=>r,metadata:()=>l,toc:()=>c});var s=o(74848),n=o(28453);const r={id:"subscriptions",title:"Subscriptions",original_id:"subscriptions"},t=void 0,l={id:"spring-server/subscriptions",title:"Subscriptions",description:"Schema",source:"@site/versioned_docs/version-3.x.x/spring-server/subscriptions.md",sourceDirName:"spring-server",slug:"/spring-server/subscriptions",permalink:"/graphql-kotlin/docs/3.x.x/spring-server/subscriptions",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-3.x.x/spring-server/subscriptions.md",tags:[],version:"3.x.x",lastUpdatedBy:"Samuel Vazquez",lastUpdatedAt:1744404742e3,frontMatter:{id:"subscriptions",title:"Subscriptions",original_id:"subscriptions"},sidebar:"docs",previous:{title:"Access the HTTP Request-Response",permalink:"/graphql-kotlin/docs/3.x.x/spring-server/http-request-response"},next:{title:"Client Overview",permalink:"/graphql-kotlin/docs/3.x.x/client/client-overview"}},p={},c=[{value:"Schema",id:"schema",level:2},{value:"<code>graphql-ws</code> subprotocol",id:"graphql-ws-subprotocol",level:2},{value:"Overview",id:"overview",level:3},{value:"Subscription Hooks",id:"subscription-hooks",level:3},{value:"Example",id:"example",level:2}];function a(e){const i={a:"a",code:"code",h2:"h2",h3:"h3",li:"li",p:"p",ul:"ul",...(0,n.R)(),...e.components};return(0,s.jsxs)(s.Fragment,{children:[(0,s.jsx)(i.h2,{id:"schema",children:"Schema"}),"\n",(0,s.jsxs)(i.p,{children:["To see more details of how to implement subscriptions in your schema, see ",(0,s.jsx)(i.a,{href:"/graphql-kotlin/docs/3.x.x/schema-generator/execution/subscriptions",children:"executing subscriptions"}),"."]}),"\n",(0,s.jsxs)(i.h2,{id:"graphql-ws-subprotocol",children:[(0,s.jsx)(i.code,{children:"graphql-ws"})," subprotocol"]}),"\n",(0,s.jsx)(i.h3,{id:"overview",children:"Overview"}),"\n",(0,s.jsxs)(i.p,{children:["We have implemented subscriptions in Spring WebSockets following the ",(0,s.jsxs)(i.a,{href:"https://github.com/apollographql/subscriptions-transport-ws/blob/3.x.x/PROTOCOL.md",children:[(0,s.jsx)(i.code,{children:"graphql-ws"})," subprotocol"]})," defined by Apollo. This requires that your client send and parse messages in a specific format."]}),"\n",(0,s.jsxs)(i.p,{children:["You can see more details in the file ",(0,s.jsx)(i.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/3.x.x/graphql-kotlin-spring-server/src/main/kotlin/com/expediagroup/graphql/spring/execution/ApolloSubscriptionProtocolHandler.kt",children:"ApolloSubscriptionProtocolHandler"}),"."]}),"\n",(0,s.jsxs)(i.p,{children:["If you would like to implement your own subscription handler, you can provide a primary spring bean for ",(0,s.jsx)(i.code,{children:"HandlerMapping"})," that overrides the ",(0,s.jsx)(i.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/3.x.x/graphql-kotlin-spring-server/src/main/kotlin/com/expediagroup/graphql/spring/SubscriptionAutoConfiguration.kt",children:"default one"})," which sets the url for subscriptions to the Apollo subscription handler."]}),"\n",(0,s.jsx)(i.h3,{id:"subscription-hooks",children:"Subscription Hooks"}),"\n",(0,s.jsx)(i.p,{children:"In line with the protocol, we have implemented hooks to execute functions at different stages of the connection lifecycle:"}),"\n",(0,s.jsxs)(i.ul,{children:["\n",(0,s.jsx)(i.li,{children:"onConnect"}),"\n",(0,s.jsx)(i.li,{children:"onOperation"}),"\n",(0,s.jsx)(i.li,{children:"onOperationComplete"}),"\n",(0,s.jsx)(i.li,{children:"onDisconnect"}),"\n"]}),"\n",(0,s.jsxs)(i.p,{children:["You can see more details in the file ",(0,s.jsx)(i.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/3.x.x/graphql-kotlin-spring-server/src/main/kotlin/com/expediagroup/graphql/spring/execution/ApolloSubscriptionHooks.kt",children:"ApolloSubscriptionHooks"}),"."]}),"\n",(0,s.jsxs)(i.p,{children:["If you would like to implement your own subscription hooks, you can provide a primary spring bean for ",(0,s.jsx)(i.code,{children:"ApolloSubscriptionHooks"})," that overrides the ",(0,s.jsx)(i.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/3.x.x/graphql-kotlin-spring-server/src/main/kotlin/com/expediagroup/graphql/spring/SubscriptionAutoConfiguration.kt",children:"default one"})," which do not perform any actions."]}),"\n",(0,s.jsx)(i.h2,{id:"example",children:"Example"}),"\n",(0,s.jsxs)(i.p,{children:["You can see an example implementation of a ",(0,s.jsx)(i.code,{children:"Subscription"})," in the ",(0,s.jsx)(i.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/3.x.x/examples/spring/src/main/kotlin/com/expediagroup/graphql/examples/subscriptions/SimpleSubscription.kt",children:"example app"}),"."]})]})}function h(e={}){const{wrapper:i}={...(0,n.R)(),...e.components};return i?(0,s.jsx)(i,{...e,children:(0,s.jsx)(a,{...e})}):a(e)}},28453:(e,i,o)=>{o.d(i,{R:()=>t,x:()=>l});var s=o(96540);const n={},r=s.createContext(n);function t(e){const i=s.useContext(r);return s.useMemo((function(){return"function"==typeof e?e(i):{...i,...e}}),[i,e])}function l(e){let i;return i=e.disableParentContext?"function"==typeof e.components?e.components(n):e.components||n:t(e.components),s.createElement(r.Provider,{value:i},e.children)}}}]);