"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[669],{85311:(e,t,r)=>{r.r(t),r.d(t,{assets:()=>p,contentTitle:()=>i,default:()=>d,frontMatter:()=>s,metadata:()=>a,toc:()=>c});var n=r(74848),o=r(28453);const s={id:"spring-graphql-context",title:"Generating GraphQL Context"},i=void 0,a={id:"server/spring-server/spring-graphql-context",title:"Generating GraphQL Context",description:"graphql-kotlin-spring-server provides a Spring specific implementation of GraphQLContextFactory",source:"@site/versioned_docs/version-7.x.x/server/spring-server/spring-graphql-context.md",sourceDirName:"server/spring-server",slug:"/server/spring-server/spring-graphql-context",permalink:"/graphql-kotlin/docs/7.x.x/server/spring-server/spring-graphql-context",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-7.x.x/server/spring-server/spring-graphql-context.md",tags:[],version:"7.x.x",lastUpdatedBy:"Samuel Vazquez",lastUpdatedAt:174440151e4,frontMatter:{id:"spring-graphql-context",title:"Generating GraphQL Context"},sidebar:"docs",previous:{title:"Writing Schemas with Spring",permalink:"/graphql-kotlin/docs/7.x.x/server/spring-server/spring-schema"},next:{title:"HTTP Request and Response",permalink:"/graphql-kotlin/docs/7.x.x/server/spring-server/spring-http-request-response"}},p={},c=[{value:"Federated Context",id:"federated-context",level:2}];function l(e){const t={a:"a",code:"code",h2:"h2",li:"li",p:"p",pre:"pre",ul:"ul",...(0,o.R)(),...e.components};return(0,n.jsxs)(n.Fragment,{children:[(0,n.jsxs)(t.p,{children:[(0,n.jsx)(t.code,{children:"graphql-kotlin-spring-server"})," provides a Spring specific implementation of ",(0,n.jsx)(t.a,{href:"/graphql-kotlin/docs/7.x.x/server/graphql-context-factory",children:"GraphQLContextFactory"}),"\nand the context."]}),"\n",(0,n.jsxs)(t.ul,{children:["\n",(0,n.jsxs)(t.li,{children:[(0,n.jsx)(t.code,{children:"SpringGraphQLContextFactory"})," - Generates GraphQL context map with federated tracing information per request"]}),"\n"]}),"\n",(0,n.jsxs)(t.p,{children:["If you are using ",(0,n.jsx)(t.code,{children:"graphql-kotlin-spring-server"}),", you should extend ",(0,n.jsx)(t.code,{children:"DefaultSpringGraphQLContextFactory"})," to automatically\nsupport federated tracing."]}),"\n",(0,n.jsx)(t.pre,{children:(0,n.jsx)(t.code,{className:"language-kotlin",children:'@Component\nclass MyGraphQLContextFactory : DefaultSpringGraphQLContextFactory() {\n    override suspend fun generateContext(request: ServerRequest): GraphQLContext =\n        super.generateContext(request) + mapOf(\n            "myCustomValue" to (request.headers().firstHeader("MyHeader") ?: "defaultContext")\n        )\n}\n'})}),"\n",(0,n.jsx)(t.p,{children:"Once your application is configured to build your custom GraphQL context, you can then access it through a data fetching\nenvironment argument. While executing the query, data fetching environment will be automatically injected to the function input arguments.\nThis argument will not appear in the GraphQL schema."}),"\n",(0,n.jsxs)(t.p,{children:["For more details, see the ",(0,n.jsx)(t.a,{href:"/graphql-kotlin/docs/7.x.x/schema-generator/execution/contextual-data",children:"Contextual Data Documentation"}),"."]}),"\n",(0,n.jsx)(t.h2,{id:"federated-context",children:"Federated Context"}),"\n",(0,n.jsxs)(t.p,{children:["If you need ",(0,n.jsx)(t.a,{href:"/graphql-kotlin/docs/7.x.x/schema-generator/federation/federation-tracing",children:"federation tracing support"}),", you can set the appropriate ",(0,n.jsx)(t.a,{href:"/graphql-kotlin/docs/7.x.x/server/spring-server/spring-properties",children:"configuration properties"}),".\nThe provided ",(0,n.jsx)(t.code,{children:"DefaultSpringGraphQLContextFactory"})," populates the required information for federated tracing, so as long as\nyou extend this context class you will maintain feature support."]})]})}function d(e={}){const{wrapper:t}={...(0,o.R)(),...e.components};return t?(0,n.jsx)(t,{...e,children:(0,n.jsx)(l,{...e})}):l(e)}},28453:(e,t,r)=>{r.d(t,{R:()=>i,x:()=>a});var n=r(96540);const o={},s=n.createContext(o);function i(e){const t=n.useContext(s);return n.useMemo((function(){return"function"==typeof e?e(t):{...t,...e}}),[t,e])}function a(e){let t;return t=e.disableParentContext?"function"==typeof e.components?e.components(o):e.components||o:i(e.components),n.createElement(s.Provider,{value:t},e.children)}}}]);