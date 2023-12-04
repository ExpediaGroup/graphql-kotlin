"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[7655],{3905:(e,t,o)=>{o.d(t,{Zo:()=>c,kt:()=>k});var n=o(67294);function r(e,t,o){return t in e?Object.defineProperty(e,t,{value:o,enumerable:!0,configurable:!0,writable:!0}):e[t]=o,e}function i(e,t){var o=Object.keys(e);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);t&&(n=n.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),o.push.apply(o,n)}return o}function s(e){for(var t=1;t<arguments.length;t++){var o=null!=arguments[t]?arguments[t]:{};t%2?i(Object(o),!0).forEach((function(t){r(e,t,o[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(o)):i(Object(o)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(o,t))}))}return e}function a(e,t){if(null==e)return{};var o,n,r=function(e,t){if(null==e)return{};var o,n,r={},i=Object.keys(e);for(n=0;n<i.length;n++)o=i[n],t.indexOf(o)>=0||(r[o]=e[o]);return r}(e,t);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(n=0;n<i.length;n++)o=i[n],t.indexOf(o)>=0||Object.prototype.propertyIsEnumerable.call(e,o)&&(r[o]=e[o])}return r}var p=n.createContext({}),l=function(e){var t=n.useContext(p),o=t;return e&&(o="function"==typeof e?e(t):s(s({},t),e)),o},c=function(e){var t=l(e.components);return n.createElement(p.Provider,{value:t},e.children)},u="mdxType",d={inlineCode:"code",wrapper:function(e){var t=e.children;return n.createElement(n.Fragment,{},t)}},m=n.forwardRef((function(e,t){var o=e.components,r=e.mdxType,i=e.originalType,p=e.parentName,c=a(e,["components","mdxType","originalType","parentName"]),u=l(o),m=r,k=u["".concat(p,".").concat(m)]||u[m]||d[m]||i;return o?n.createElement(k,s(s({ref:t},c),{},{components:o})):n.createElement(k,s({ref:t},c))}));function k(e,t){var o=arguments,r=t&&t.mdxType;if("string"==typeof e||r){var i=o.length,s=new Array(i);s[0]=m;var a={};for(var p in t)hasOwnProperty.call(t,p)&&(a[p]=t[p]);a.originalType=e,a[u]="string"==typeof e?e:r,s[1]=a;for(var l=2;l<i;l++)s[l]=o[l];return n.createElement.apply(null,s)}return n.createElement.apply(null,o)}m.displayName="MDXCreateElement"},1318:(e,t,o)=>{o.r(t),o.d(t,{assets:()=>c,contentTitle:()=>p,default:()=>k,frontMatter:()=>a,metadata:()=>l,toc:()=>u});var n=o(87462),r=o(63366),i=(o(67294),o(3905)),s=["components"],a={id:"ktor-subscriptions",title:"Subscriptions"},p=void 0,l={unversionedId:"server/ktor-server/ktor-subscriptions",id:"server/ktor-server/ktor-subscriptions",title:"Subscriptions",description:"_To see more details on how to implement subscriptions in your schema, see the schema generator docs on executing subscriptions.",source:"@site/docs/server/ktor-server/ktor-subscriptions.md",sourceDirName:"server/ktor-server",slug:"/server/ktor-server/ktor-subscriptions",permalink:"/graphql-kotlin/docs/server/ktor-server/ktor-subscriptions",draft:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/docs/server/ktor-server/ktor-subscriptions.md",tags:[],version:"current",lastUpdatedBy:"Simon B\xf6lscher",lastUpdatedAt:1701733027,formattedLastUpdatedAt:"Dec 4, 2023",frontMatter:{id:"ktor-subscriptions",title:"Subscriptions"},sidebar:"docs",previous:{title:"Ktor Plugin Configuration",permalink:"/graphql-kotlin/docs/server/ktor-server/ktor-configuration"},next:{title:"Client Overview",permalink:"/graphql-kotlin/docs/client/client-overview"}},c={},u=[{value:"Prerequisites",id:"prerequisites",level:2},{value:"Flow Support",id:"flow-support",level:2},{value:"Subscription Protocols",id:"subscription-protocols",level:2},{value:"<code>graphql-transport-ws</code> subprotocol",id:"graphql-transport-ws-subprotocol",level:3},{value:"Subscription Execution Hooks",id:"subscription-execution-hooks",level:2},{value:"<code>onConnect</code>",id:"onconnect",level:3},{value:"<code>onOperation</code>",id:"onoperation",level:3},{value:"<code>onOperationComplete</code>",id:"onoperationcomplete",level:3},{value:"<code>onDisconnect</code>",id:"ondisconnect",level:3},{value:"Example",id:"example",level:2}],d={toc:u},m="wrapper";function k(e){var t=e.components,o=(0,r.Z)(e,s);return(0,i.kt)(m,(0,n.Z)({},d,o,{components:t,mdxType:"MDXLayout"}),(0,i.kt)("p",null,(0,i.kt)("em",{parentName:"p"},"To see more details on how to implement subscriptions in your schema, see the schema generator docs on ",(0,i.kt)("a",{parentName:"em",href:"/graphql-kotlin/docs/schema-generator/execution/subscriptions"},"executing subscriptions"),".\nThis page lists the ",(0,i.kt)("inlineCode",{parentName:"em"},"graphql-kotlin-ktor-server")," specific features.")),(0,i.kt)("h2",{id:"prerequisites"},"Prerequisites"),(0,i.kt)("p",null,"To start using Subscriptions, you may need install ",(0,i.kt)("a",{parentName:"p",href:"https://ktor.io/docs/websocket.html"},"WebSockets")," plugin to your Ktor server config."),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-kotlin"},"install(WebSockets)\n")),(0,i.kt)("p",null,"See ",(0,i.kt)("a",{parentName:"p",href:"https://ktor.io/docs/websocket.html#configure"},"plugin docs")," to get more info about the ",(0,i.kt)("inlineCode",{parentName:"p"},"WebSocketOptions")," configuration."),(0,i.kt)("h2",{id:"flow-support"},"Flow Support"),(0,i.kt)("p",null,(0,i.kt)("inlineCode",{parentName:"p"},"graphql-kotlin-ktor-server")," provides support for Kotlin ",(0,i.kt)("inlineCode",{parentName:"p"},"Flow")," by automatically configuring schema generation process with ",(0,i.kt)("inlineCode",{parentName:"p"},"FlowSubscriptionSchemaGeneratorHooks"),"\nand GraphQL execution with ",(0,i.kt)("inlineCode",{parentName:"p"},"FlowSubscriptionExecutionStrategy"),"."),(0,i.kt)("admonition",{type:"info"},(0,i.kt)("p",{parentName:"admonition"},"If you define your subscriptions using Kotlin ",(0,i.kt)("inlineCode",{parentName:"p"},"Flow"),", make sure to extend ",(0,i.kt)("inlineCode",{parentName:"p"},"FlowSubscriptionSchemaGeneratorHooks")," whenever you need to provide some custom hooks.")),(0,i.kt)("h2",{id:"subscription-protocols"},"Subscription Protocols"),(0,i.kt)("h3",{id:"graphql-transport-ws-subprotocol"},(0,i.kt)("inlineCode",{parentName:"h3"},"graphql-transport-ws")," subprotocol"),(0,i.kt)("p",null,"We have implemented subscriptions in Ktor WebSockets following the ",(0,i.kt)("a",{parentName:"p",href:"https://github.com/enisdenjo/graphql-ws/blob/master/PROTOCOL.md"},(0,i.kt)("inlineCode",{parentName:"a"},"graphql-transport-ws"))," sub-protocol\nfrom ",(0,i.kt)("a",{parentName:"p",href:"https://the-guild.dev/"},"The Guild"),". This requires that your client send and parse messages in a specific format.\nSee protocol documentation for expected messages."),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-kotlin"},"install(Routing) {\n    graphQLSubscriptionsRoute()\n}\n")),(0,i.kt)("h2",{id:"subscription-execution-hooks"},"Subscription Execution Hooks"),(0,i.kt)("p",null,'Subscription execution hooks allow you to "hook-in" to the various stages of the connection lifecycle and execute custom logic based on the event. By default, all subscription execution hooks are no-op.\nIf you would like to provide some custom hooks, you can do so by providing your own implementation of ',(0,i.kt)("inlineCode",{parentName:"p"},"KtorGraphQLSubscriptionHooks"),"."),(0,i.kt)("h3",{id:"onconnect"},(0,i.kt)("inlineCode",{parentName:"h3"},"onConnect")),(0,i.kt)("p",null,"Allows validation of connectionParams prior to starting the connection.\nYou can reject the connection by throwing an exception.\nA ",(0,i.kt)("inlineCode",{parentName:"p"},"GraphQLContext")," returned from this hook will be later passed to subsequent hooks."),(0,i.kt)("h3",{id:"onoperation"},(0,i.kt)("inlineCode",{parentName:"h3"},"onOperation")),(0,i.kt)("p",null,"Called when the client executes a GraphQL operation."),(0,i.kt)("h3",{id:"onoperationcomplete"},(0,i.kt)("inlineCode",{parentName:"h3"},"onOperationComplete")),(0,i.kt)("p",null,"Called when client's unsubscribes"),(0,i.kt)("h3",{id:"ondisconnect"},(0,i.kt)("inlineCode",{parentName:"h3"},"onDisconnect")),(0,i.kt)("p",null,"Called when the client disconnects"),(0,i.kt)("h2",{id:"example"},"Example"),(0,i.kt)("p",null,"You can see an example implementation of a ",(0,i.kt)("inlineCode",{parentName:"p"},"Subscription")," in the ",(0,i.kt)("a",{parentName:"p",href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/master/examples/server/ktor-server/src/main/kotlin/com/expediagroup/graphql/examples/server/ktor/schema/ExampleSubscriptionService.kt"},"example app"),"."))}k.isMDXComponent=!0}}]);