"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[708],{48073:(e,r,t)=>{t.r(r),t.d(r,{assets:()=>p,contentTitle:()=>i,default:()=>d,frontMatter:()=>o,metadata:()=>a,toc:()=>c});var n=t(74848),s=t(28453);const o={id:"spring-graphql-context",title:"Generating GraphQL Context"},i=void 0,a={id:"server/spring-server/spring-graphql-context",title:"Generating GraphQL Context",description:"graphql-kotlin-spring-server provides a Spring specific implementation of GraphQLContextFactory and the context.",source:"@site/versioned_docs/version-4.x.x/server/spring-server/spring-graphql-context.md",sourceDirName:"server/spring-server",slug:"/server/spring-server/spring-graphql-context",permalink:"/graphql-kotlin/docs/4.x.x/server/spring-server/spring-graphql-context",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-4.x.x/server/spring-server/spring-graphql-context.md",tags:[],version:"4.x.x",lastUpdatedBy:"Dariusz Kuc",lastUpdatedAt:1708623265,formattedLastUpdatedAt:"Feb 22, 2024",frontMatter:{id:"spring-graphql-context",title:"Generating GraphQL Context"},sidebar:"docs",previous:{title:"Writing Schemas with Spring",permalink:"/graphql-kotlin/docs/4.x.x/server/spring-server/spring-schema"},next:{title:"HTTP Request and Response",permalink:"/graphql-kotlin/docs/4.x.x/server/spring-server/spring-http-request-response"}},p={},c=[{value:"Federated Context",id:"federated-context",level:2}];function l(e){const r={a:"a",code:"code",h2:"h2",li:"li",p:"p",pre:"pre",ul:"ul",...(0,s.R)(),...e.components};return(0,n.jsxs)(n.Fragment,{children:[(0,n.jsxs)(r.p,{children:[(0,n.jsx)(r.code,{children:"graphql-kotlin-spring-server"})," provides a Spring specific implementation of ",(0,n.jsx)(r.a,{href:"/graphql-kotlin/docs/4.x.x/server/graphql-context-factory",children:"GraphQLContextFactory"})," and the context."]}),"\n",(0,n.jsxs)(r.ul,{children:["\n",(0,n.jsxs)(r.li,{children:[(0,n.jsx)(r.code,{children:"SpringGraphQLContext"})," - Implements the Spring ",(0,n.jsx)(r.code,{children:"ServerRequest"})," and federation tracing ",(0,n.jsx)(r.code,{children:"HTTPRequestHeaders"})]}),"\n",(0,n.jsxs)(r.li,{children:[(0,n.jsx)(r.code,{children:"SpringGraphQLContextFactory"})," - Generates a ",(0,n.jsx)(r.code,{children:"SpringGraphQLContext"})," per request"]}),"\n"]}),"\n",(0,n.jsxs)(r.p,{children:["If you are using ",(0,n.jsx)(r.code,{children:"graphql-kotlin-spring-server"}),", you should extend ",(0,n.jsx)(r.code,{children:"SpringGraphQLContext"})," and ",(0,n.jsx)(r.code,{children:"SpringGraphQLContextFactory"})," to maintain support with all the other features."]}),"\n",(0,n.jsx)(r.pre,{children:(0,n.jsx)(r.code,{className:"language-kotlin",children:'class MyGraphQLContext(val myCustomValue: String, request: ServerRequest) : SpringGraphQLContext(request)\n\n@Component\nclass MyGraphQLContextFactory : SpringGraphQLContextFactory<MyGraphQLContext>() {\n    override suspend fun generateContext(request: ServerRequest): MyGraphQLContext {\n        val customVal = request.headers().firstHeader("MyHeader") ?: "defaultValue"\n        return MyGraphQLContext(customVal, request)\n    }\n}\n'})}),"\n",(0,n.jsxs)(r.p,{children:["Once your application is configured to build your custom ",(0,n.jsx)(r.code,{children:"MyGraphQLContext"}),", you can then specify it as function argument.\nWhile executing the query, the corresponding GraphQL context will be read from the environment and automatically injected to the function input arguments.\nThis argument will not appear in the GraphQL schema."]}),"\n",(0,n.jsxs)(r.p,{children:["For more details, see the ",(0,n.jsx)(r.a,{href:"/graphql-kotlin/docs/4.x.x/schema-generator/execution/contextual-data",children:"Contextual Data Documentation"}),"."]}),"\n",(0,n.jsx)(r.h2,{id:"federated-context",children:"Federated Context"}),"\n",(0,n.jsxs)(r.p,{children:["If you need ",(0,n.jsx)(r.a,{href:"/graphql-kotlin/docs/4.x.x/schema-generator/federation/federation-tracing",children:"federation tracing support"}),", you can set the appropiate ",(0,n.jsx)(r.a,{href:"/graphql-kotlin/docs/4.x.x/server/spring-server/spring-properties",children:"configuration properties"}),".\nThe provided ",(0,n.jsx)(r.code,{children:"SpringGraphQLContext"})," implements the required federation methods for tracing, so as long as you extend this context class you will maintain feature support."]})]})}function d(e={}){const{wrapper:r}={...(0,s.R)(),...e.components};return r?(0,n.jsx)(r,{...e,children:(0,n.jsx)(l,{...e})}):l(e)}},28453:(e,r,t)=>{t.d(r,{R:()=>i,x:()=>a});var n=t(96540);const s={},o=n.createContext(s);function i(e){const r=n.useContext(o);return n.useMemo((function(){return"function"==typeof e?e(r):{...r,...e}}),[r,e])}function a(e){let r;return r=e.disableParentContext?"function"==typeof e.components?e.components(s):e.components||s:i(e.components),n.createElement(o.Provider,{value:r},e.children)}}}]);