"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[7981],{47244:(e,r,t)=>{t.r(r),t.d(r,{assets:()=>p,contentTitle:()=>i,default:()=>d,frontMatter:()=>o,metadata:()=>a,toc:()=>c});var n=t(74848),s=t(28453);const o={id:"spring-graphql-context",title:"Generating GraphQL Context"},i=void 0,a={id:"server/spring-server/spring-graphql-context",title:"Generating GraphQL Context",description:"graphql-kotlin-spring-server provides a Spring specific implementation of GraphQLContextFactory",source:"@site/versioned_docs/version-6.x.x/server/spring-server/spring-graphql-context.md",sourceDirName:"server/spring-server",slug:"/server/spring-server/spring-graphql-context",permalink:"/graphql-kotlin/docs/6.x.x/server/spring-server/spring-graphql-context",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-6.x.x/server/spring-server/spring-graphql-context.md",tags:[],version:"6.x.x",lastUpdatedBy:"Samuel Vazquez",lastUpdatedAt:172791071e4,frontMatter:{id:"spring-graphql-context",title:"Generating GraphQL Context"},sidebar:"docs",previous:{title:"Writing Schemas with Spring",permalink:"/graphql-kotlin/docs/6.x.x/server/spring-server/spring-schema"},next:{title:"HTTP Request and Response",permalink:"/graphql-kotlin/docs/6.x.x/server/spring-server/spring-http-request-response"}},p={},c=[{value:"Federated Context",id:"federated-context",level:2}];function l(e){const r={a:"a",code:"code",h2:"h2",li:"li",p:"p",pre:"pre",strong:"strong",ul:"ul",...(0,s.R)(),...e.components};return(0,n.jsxs)(n.Fragment,{children:[(0,n.jsxs)(r.p,{children:[(0,n.jsx)(r.code,{children:"graphql-kotlin-spring-server"})," provides a Spring specific implementation of ",(0,n.jsx)(r.a,{href:"/graphql-kotlin/docs/6.x.x/server/graphql-context-factory",children:"GraphQLContextFactory"}),"\nand the context."]}),"\n",(0,n.jsxs)(r.ul,{children:["\n",(0,n.jsxs)(r.li,{children:[(0,n.jsx)(r.code,{children:"SpringGraphQLContext"})," ",(0,n.jsx)(r.strong,{children:"(deprecated)"})," - Implements the Spring ",(0,n.jsx)(r.code,{children:"ServerRequest"})," and federation tracing ",(0,n.jsx)(r.code,{children:"HTTPRequestHeaders"})]}),"\n",(0,n.jsxs)(r.li,{children:[(0,n.jsx)(r.code,{children:"SpringGraphQLContextFactory"})," - Generates GraphQL context map with federated tracing information per request"]}),"\n"]}),"\n",(0,n.jsxs)(r.p,{children:["If you are using ",(0,n.jsx)(r.code,{children:"graphql-kotlin-spring-server"}),", you should extend ",(0,n.jsx)(r.code,{children:"DefaultSpringGraphQLContextFactory"})," to automatically\nsupport federated tracing."]}),"\n",(0,n.jsx)(r.pre,{children:(0,n.jsx)(r.code,{className:"language-kotlin",children:'@Component\nclass MyGraphQLContextFactory : DefaultSpringGraphQLContextFactory() {\n    override suspend fun generateContextMap(request: ServerRequest): Map<*, Any> = super.generateContextMap(request) + mapOf(\n        "myCustomValue" to (request.headers().firstHeader("MyHeader") ?: "defaultContext")\n    )\n}\n'})}),"\n",(0,n.jsx)(r.p,{children:"Once your application is configured to build your custom GraphQL context map, you can then access it through a data fetching\nenvironment argument. While executing the query, data fetching environment will be automatically injected to the function input arguments.\nThis argument will not appear in the GraphQL schema."}),"\n",(0,n.jsxs)(r.p,{children:["For more details, see the ",(0,n.jsx)(r.a,{href:"/graphql-kotlin/docs/6.x.x/schema-generator/execution/contextual-data",children:"Contextual Data Documentation"}),"."]}),"\n",(0,n.jsx)(r.h2,{id:"federated-context",children:"Federated Context"}),"\n",(0,n.jsxs)(r.p,{children:["If you need ",(0,n.jsx)(r.a,{href:"/graphql-kotlin/docs/6.x.x/schema-generator/federation/federation-tracing",children:"federation tracing support"}),", you can set the appropriate ",(0,n.jsx)(r.a,{href:"/graphql-kotlin/docs/6.x.x/server/spring-server/spring-properties",children:"configuration properties"}),".\nThe provided ",(0,n.jsx)(r.code,{children:"DefaultSpringGraphQLContextFactory"})," populates the required information for federated tracing, so as long as\nyou extend this context class you will maintain feature support."]})]})}function d(e={}){const{wrapper:r}={...(0,s.R)(),...e.components};return r?(0,n.jsx)(r,{...e,children:(0,n.jsx)(l,{...e})}):l(e)}},28453:(e,r,t)=>{t.d(r,{R:()=>i,x:()=>a});var n=t(96540);const s={},o=n.createContext(s);function i(e){const r=n.useContext(o);return n.useMemo((function(){return"function"==typeof e?e(r):{...r,...e}}),[r,e])}function a(e){let r;return r=e.disableParentContext?"function"==typeof e.components?e.components(s):e.components||s:i(e.components),n.createElement(o.Provider,{value:r},e.children)}}}]);