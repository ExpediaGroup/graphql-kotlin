"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[353],{8379:(e,o,n)=>{n.r(o),n.d(o,{assets:()=>l,contentTitle:()=>i,default:()=>p,frontMatter:()=>r,metadata:()=>c,toc:()=>a});var s=n(74848),t=n(28453);const r={id:"ktor-subscriptions",title:"Subscriptions"},i=void 0,c={id:"server/ktor-server/ktor-subscriptions",title:"Subscriptions",description:"_To see more details on how to implement subscriptions in your schema, see the schema generator docs on executing subscriptions.",source:"@site/versioned_docs/version-8.x.x/server/ktor-server/ktor-subscriptions.md",sourceDirName:"server/ktor-server",slug:"/server/ktor-server/ktor-subscriptions",permalink:"/graphql-kotlin/docs/server/ktor-server/ktor-subscriptions",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-8.x.x/server/ktor-server/ktor-subscriptions.md",tags:[],version:"8.x.x",lastUpdatedBy:"Samuel Vazquez",lastUpdatedAt:174440151e4,frontMatter:{id:"ktor-subscriptions",title:"Subscriptions"},sidebar:"docs",previous:{title:"Ktor Plugin Configuration",permalink:"/graphql-kotlin/docs/server/ktor-server/ktor-configuration"},next:{title:"Client Overview",permalink:"/graphql-kotlin/docs/client/client-overview"}},l={},a=[{value:"Prerequisites",id:"prerequisites",level:2},{value:"Flow Support",id:"flow-support",level:2},{value:"Subscription Protocols",id:"subscription-protocols",level:2},{value:"<code>graphql-transport-ws</code> subprotocol",id:"graphql-transport-ws-subprotocol",level:3},{value:"Subscription Execution Hooks",id:"subscription-execution-hooks",level:2},{value:"<code>onConnect</code>",id:"onconnect",level:3},{value:"<code>onOperation</code>",id:"onoperation",level:3},{value:"<code>onOperationComplete</code>",id:"onoperationcomplete",level:3},{value:"<code>onDisconnect</code>",id:"ondisconnect",level:3},{value:"Example",id:"example",level:2}];function d(e){const o={a:"a",admonition:"admonition",code:"code",em:"em",h2:"h2",h3:"h3",p:"p",pre:"pre",...(0,t.R)(),...e.components};return(0,s.jsxs)(s.Fragment,{children:[(0,s.jsx)(o.p,{children:(0,s.jsxs)(o.em,{children:["To see more details on how to implement subscriptions in your schema, see the schema generator docs on ",(0,s.jsx)(o.a,{href:"/graphql-kotlin/docs/schema-generator/execution/subscriptions",children:"executing subscriptions"}),".\nThis page lists the ",(0,s.jsx)(o.code,{children:"graphql-kotlin-ktor-server"})," specific features."]})}),"\n",(0,s.jsx)(o.h2,{id:"prerequisites",children:"Prerequisites"}),"\n",(0,s.jsxs)(o.p,{children:["To start using Subscriptions, you may need install ",(0,s.jsx)(o.a,{href:"https://ktor.io/docs/websocket.html",children:"WebSockets"})," plugin to your Ktor server config."]}),"\n",(0,s.jsx)(o.pre,{children:(0,s.jsx)(o.code,{className:"language-kotlin",children:"install(WebSockets)\n"})}),"\n",(0,s.jsxs)(o.p,{children:["See ",(0,s.jsx)(o.a,{href:"https://ktor.io/docs/websocket.html#configure",children:"plugin docs"})," to get more info about the ",(0,s.jsx)(o.code,{children:"WebSocketOptions"})," configuration."]}),"\n",(0,s.jsx)(o.h2,{id:"flow-support",children:"Flow Support"}),"\n",(0,s.jsxs)(o.p,{children:[(0,s.jsx)(o.code,{children:"graphql-kotlin-ktor-server"})," provides support for Kotlin ",(0,s.jsx)(o.code,{children:"Flow"})," by automatically configuring schema generation process with ",(0,s.jsx)(o.code,{children:"FlowSubscriptionSchemaGeneratorHooks"}),"\nand GraphQL execution with ",(0,s.jsx)(o.code,{children:"FlowSubscriptionExecutionStrategy"}),"."]}),"\n",(0,s.jsx)(o.admonition,{type:"info",children:(0,s.jsxs)(o.p,{children:["If you define your subscriptions using Kotlin ",(0,s.jsx)(o.code,{children:"Flow"}),", make sure to extend ",(0,s.jsx)(o.code,{children:"FlowSubscriptionSchemaGeneratorHooks"})," whenever you need to provide some custom hooks."]})}),"\n",(0,s.jsx)(o.h2,{id:"subscription-protocols",children:"Subscription Protocols"}),"\n",(0,s.jsxs)(o.h3,{id:"graphql-transport-ws-subprotocol",children:[(0,s.jsx)(o.code,{children:"graphql-transport-ws"})," subprotocol"]}),"\n",(0,s.jsxs)(o.p,{children:["We have implemented subscriptions in Ktor WebSockets following the ",(0,s.jsx)(o.a,{href:"https://github.com/enisdenjo/graphql-ws/blob/master/PROTOCOL.md",children:(0,s.jsx)(o.code,{children:"graphql-transport-ws"})})," sub-protocol\nfrom ",(0,s.jsx)(o.a,{href:"https://the-guild.dev/",children:"The Guild"}),". This requires that your client send and parse messages in a specific format.\nSee protocol documentation for expected messages."]}),"\n",(0,s.jsx)(o.pre,{children:(0,s.jsx)(o.code,{className:"language-kotlin",children:"install(Routing) {\n    graphQLSubscriptionsRoute()\n}\n"})}),"\n",(0,s.jsx)(o.h2,{id:"subscription-execution-hooks",children:"Subscription Execution Hooks"}),"\n",(0,s.jsxs)(o.p,{children:['Subscription execution hooks allow you to "hook-in" to the various stages of the connection lifecycle and execute custom logic based on the event. By default, all subscription execution hooks are no-op.\nIf you would like to provide some custom hooks, you can do so by providing your own implementation of ',(0,s.jsx)(o.code,{children:"KtorGraphQLSubscriptionHooks"}),"."]}),"\n",(0,s.jsx)(o.h3,{id:"onconnect",children:(0,s.jsx)(o.code,{children:"onConnect"})}),"\n",(0,s.jsxs)(o.p,{children:["Allows validation of connectionParams prior to starting the connection.\nYou can reject the connection by throwing an exception.\nA ",(0,s.jsx)(o.code,{children:"GraphQLContext"})," returned from this hook will be later passed to subsequent hooks."]}),"\n",(0,s.jsx)(o.h3,{id:"onoperation",children:(0,s.jsx)(o.code,{children:"onOperation"})}),"\n",(0,s.jsx)(o.p,{children:"Called when the client executes a GraphQL operation."}),"\n",(0,s.jsx)(o.h3,{id:"onoperationcomplete",children:(0,s.jsx)(o.code,{children:"onOperationComplete"})}),"\n",(0,s.jsx)(o.p,{children:"Called when client's unsubscribes"}),"\n",(0,s.jsx)(o.h3,{id:"ondisconnect",children:(0,s.jsx)(o.code,{children:"onDisconnect"})}),"\n",(0,s.jsx)(o.p,{children:"Called when the client disconnects"}),"\n",(0,s.jsx)(o.h2,{id:"example",children:"Example"}),"\n",(0,s.jsxs)(o.p,{children:["You can see an example implementation of a ",(0,s.jsx)(o.code,{children:"Subscription"})," in the ",(0,s.jsx)(o.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/master/examples/server/ktor-server/src/main/kotlin/com/expediagroup/graphql/examples/server/ktor/schema/ExampleSubscriptionService.kt",children:"example app"}),"."]})]})}function p(e={}){const{wrapper:o}={...(0,t.R)(),...e.components};return o?(0,s.jsx)(o,{...e,children:(0,s.jsx)(d,{...e})}):d(e)}},28453:(e,o,n)=>{n.d(o,{R:()=>i,x:()=>c});var s=n(96540);const t={},r=s.createContext(t);function i(e){const o=s.useContext(r);return s.useMemo((function(){return"function"==typeof e?e(o):{...o,...e}}),[o,e])}function c(e){let o;return o=e.disableParentContext?"function"==typeof e.components?e.components(t):e.components||t:i(e.components),s.createElement(r.Provider,{value:o},e.children)}}}]);