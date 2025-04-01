"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[6144],{65305:(e,t,r)=>{r.r(t),r.d(t,{assets:()=>i,contentTitle:()=>o,default:()=>h,frontMatter:()=>a,metadata:()=>c,toc:()=>l});var n=r(74848),s=r(28453);const a={id:"graphql-request-handler",title:"GraphQLRequestHandler"},o=void 0,c={id:"server/graphql-request-handler",title:"GraphQLRequestHandler",description:"The GraphQLRequestHandler is an open and extendable class that contains the basic logic to get a GraphQLResponse.",source:"@site/versioned_docs/version-8.x.x/server/graphql-request-handler.md",sourceDirName:"server",slug:"/server/graphql-request-handler",permalink:"/graphql-kotlin/docs/server/graphql-request-handler",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-8.x.x/server/graphql-request-handler.md",tags:[],version:"8.x.x",lastUpdatedBy:"Templeton Peck",lastUpdatedAt:1743524928e3,frontMatter:{id:"graphql-request-handler",title:"GraphQLRequestHandler"},sidebar:"docs",previous:{title:"GraphQLContextFactory",permalink:"/graphql-kotlin/docs/server/graphql-context-factory"},next:{title:"Subscriptions",permalink:"/graphql-kotlin/docs/server/server-subscriptions"}},i={},l=[];function d(e){const t={a:"a",code:"code",p:"p",...(0,s.R)(),...e.components};return(0,n.jsxs)(n.Fragment,{children:[(0,n.jsxs)(t.p,{children:["The ",(0,n.jsx)(t.code,{children:"GraphQLRequestHandler"})," is an open and extendable class that contains the basic logic to get a ",(0,n.jsx)(t.code,{children:"GraphQLResponse"}),"."]}),"\n",(0,n.jsxs)(t.p,{children:["It requires a ",(0,n.jsx)(t.code,{children:"GraphQLSchema"})," and a ",(0,n.jsx)(t.a,{href:"/graphql-kotlin/docs/server/data-loader/",children:"KotlinDataLoaderRegistryFactory"})," in the constructor.\nFor each request, it accepts a ",(0,n.jsx)(t.code,{children:"GraphQLRequest"})," and an optional ",(0,n.jsx)(t.a,{href:"/graphql-kotlin/docs/server/graphql-context-factory",children:"GraphQLContext"}),",\nand calls the ",(0,n.jsx)(t.code,{children:"KotlinDataLoaderRegistryFactory"})," to generate a new ",(0,n.jsx)(t.code,{children:"KotlinDataLoaderRegistry"}),". Then all of these objects are sent to the schema for\nexecution and the result is mapped to a ",(0,n.jsx)(t.code,{children:"GraphQLResponse"}),"."]}),"\n",(0,n.jsx)(t.p,{children:"There shouldn't be much need to change this class but if you wanted to add custom logic\nor logging it is possible to override it or just create your own."})]})}function h(e={}){const{wrapper:t}={...(0,s.R)(),...e.components};return t?(0,n.jsx)(t,{...e,children:(0,n.jsx)(d,{...e})}):d(e)}},28453:(e,t,r)=>{r.d(t,{R:()=>o,x:()=>c});var n=r(96540);const s={},a=n.createContext(s);function o(e){const t=n.useContext(a);return n.useMemo((function(){return"function"==typeof e?e(t):{...t,...e}}),[t,e])}function c(e){let t;return t=e.disableParentContext?"function"==typeof e.components?e.components(s):e.components||s:o(e.components),n.createElement(a.Provider,{value:t},e.children)}}}]);