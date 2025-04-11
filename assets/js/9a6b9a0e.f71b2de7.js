"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[5962],{76541:(e,t,r)=>{r.r(t),r.d(t,{assets:()=>d,contentTitle:()=>o,default:()=>h,frontMatter:()=>s,metadata:()=>c,toc:()=>l});var a=r(74848),n=r(28453);const s={id:"graphql-request-handler",title:"GraphQLRequestHandler"},o=void 0,c={id:"server/graphql-request-handler",title:"GraphQLRequestHandler",description:"The GraphQLRequestHandler is an open and extendable class that contains the basic logic to get a GraphQLResponse.",source:"@site/versioned_docs/version-4.x.x/server/graphql-request-handler.md",sourceDirName:"server",slug:"/server/graphql-request-handler",permalink:"/graphql-kotlin/docs/4.x.x/server/graphql-request-handler",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-4.x.x/server/graphql-request-handler.md",tags:[],version:"4.x.x",lastUpdatedBy:"Samuel Vazquez",lastUpdatedAt:174440151e4,frontMatter:{id:"graphql-request-handler",title:"GraphQLRequestHandler"},sidebar:"docs",previous:{title:"GraphQLContextFactory",permalink:"/graphql-kotlin/docs/4.x.x/server/graphql-context-factory"},next:{title:"Data Loaders",permalink:"/graphql-kotlin/docs/4.x.x/server/data-loaders"}},d={},l=[];function i(e){const t={a:"a",code:"code",p:"p",...(0,n.R)(),...e.components};return(0,a.jsxs)(a.Fragment,{children:[(0,a.jsxs)(t.p,{children:["The ",(0,a.jsx)(t.code,{children:"GraphQLRequestHandler"})," is an open and extendable class that contains the basic logic to get a ",(0,a.jsx)(t.code,{children:"GraphQLResponse"}),"."]}),"\n",(0,a.jsxs)(t.p,{children:["It requires a ",(0,a.jsx)(t.code,{children:"GraphQLSchema"})," and a ",(0,a.jsx)(t.a,{href:"/graphql-kotlin/docs/4.x.x/server/data-loaders",children:"DataLoaderRegistryFactory"})," in the constructor.\nFor each request, it accepts a ",(0,a.jsx)(t.code,{children:"GraphQLRequest"})," and an optional ",(0,a.jsx)(t.a,{href:"/graphql-kotlin/docs/4.x.x/server/graphql-context-factory",children:"GraphQLContext"}),", and calls the ",(0,a.jsx)(t.code,{children:"DataLoaderRegistryFactory"})," to generate a new ",(0,a.jsx)(t.code,{children:"DataLoaderRegistry"}),".\nThen all of these objects are sent to the schema for execution and the result is mapped to a ",(0,a.jsx)(t.code,{children:"GraphQLResponse"}),"."]}),"\n",(0,a.jsx)(t.p,{children:"There shouldn't be much need to change this class but if you wanted to add custom logic or logging it is possible to override it or just create your own."})]})}function h(e={}){const{wrapper:t}={...(0,n.R)(),...e.components};return t?(0,a.jsx)(t,{...e,children:(0,a.jsx)(i,{...e})}):i(e)}},28453:(e,t,r)=>{r.d(t,{R:()=>o,x:()=>c});var a=r(96540);const n={},s=a.createContext(n);function o(e){const t=a.useContext(s);return a.useMemo((function(){return"function"==typeof e?e(t):{...t,...e}}),[t,e])}function c(e){let t;return t=e.disableParentContext?"function"==typeof e.components?e.components(n):e.components||n:o(e.components),a.createElement(s.Provider,{value:t},e.children)}}}]);