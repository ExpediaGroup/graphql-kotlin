"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[8948],{56585:(e,n,t)=>{t.r(n),t.d(n,{assets:()=>c,contentTitle:()=>i,default:()=>p,frontMatter:()=>o,metadata:()=>d,toc:()=>l});var r=t(74848),a=t(28453);const o={id:"federation-tracing",title:"Federation Tracing"},i=void 0,d={id:"schema-generator/federation/federation-tracing",title:"Federation Tracing",description:"graphql-kotlin-federation module relies on apollographql/federation-jvm",source:"@site/versioned_docs/version-8.x.x/schema-generator/federation/federation-tracing.md",sourceDirName:"schema-generator/federation",slug:"/schema-generator/federation/federation-tracing",permalink:"/graphql-kotlin/docs/schema-generator/federation/federation-tracing",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-8.x.x/schema-generator/federation/federation-tracing.md",tags:[],version:"8.x.x",lastUpdatedBy:"Samuel Vazquez",lastUpdatedAt:1744404742e3,frontMatter:{id:"federation-tracing",title:"Federation Tracing"},sidebar:"docs",previous:{title:"Federated Type Resolution",permalink:"/graphql-kotlin/docs/schema-generator/federation/type-resolution"},next:{title:"GraphQLServer",permalink:"/graphql-kotlin/docs/server/graphql-server"}},c={},l=[{value:"GraphQL Context Map",id:"graphql-context-map",level:3}];function s(e){const n={a:"a",admonition:"admonition",code:"code",h3:"h3",p:"p",pre:"pre",...(0,a.R)(),...e.components};return(0,r.jsxs)(r.Fragment,{children:[(0,r.jsxs)(n.p,{children:[(0,r.jsx)(n.code,{children:"graphql-kotlin-federation"})," module relies on ",(0,r.jsx)(n.a,{href:"https://github.com/apollographql/federation-jvm",children:"apollographql/federation-jvm"}),"\npackage to provide support for Apollo Federation tracing. Tracing is turned on by including ",(0,r.jsx)(n.code,{children:"FederatedTracingInstrumentation"}),"\nin your GraphQL instance. In order for the ",(0,r.jsx)(n.code,{children:"FederatedTracingInstrumentation"})," to know whether incoming request should be\ntraced, we need to provide it a ",(0,r.jsx)(n.code,{children:"apollo-federation-include-trace"})," header value."]}),"\n",(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-kotlin",children:"val schema = toFederatedSchema(myFederatedConfig, listOf(TopLevelObject(MyFederatedQuery())))\nval graphQL = GraphQL.newGraphQL(schema)\n    .instrumentation(FederatedTracingInstrumentation())\n    .build()\n"})}),"\n",(0,r.jsx)(n.h3,{id:"graphql-context-map",children:"GraphQL Context Map"}),"\n",(0,r.jsx)(n.admonition,{type:"note",children:(0,r.jsxs)(n.p,{children:["Default ",(0,r.jsx)(n.code,{children:"GraphQLContextFactory"})," provided by ",(0,r.jsx)(n.code,{children:"graphql-kotlin-spring-server"})," populates this header information automatically."]})}),"\n",(0,r.jsx)(n.p,{children:"Tracing header information can be provided by populating info directly on the GraphQL context map."}),"\n",(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-kotlin",children:"val contextMap = mutableMapOf<Any, Any>()\n    .also { map ->\n        request.headers().firstHeader(FEDERATED_TRACING_HEADER_NAME)?.let { headerValue ->\n            map[FEDERATED_TRACING_HEADER_NAME] = headerValue\n        }\n    }\n\nval executionInput = ExecutionInput.newExecutionInput()\n    .graphQLContext(contextMap)\n    .query(queryString)\n    .build()\n\ngraphql.executeAsync(executionInput)\n"})})]})}function p(e={}){const{wrapper:n}={...(0,a.R)(),...e.components};return n?(0,r.jsx)(n,{...e,children:(0,r.jsx)(s,{...e})}):s(e)}},28453:(e,n,t)=>{t.d(n,{R:()=>i,x:()=>d});var r=t(96540);const a={},o=r.createContext(a);function i(e){const n=r.useContext(o);return r.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function d(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(a):e.components||a:i(e.components),r.createElement(o.Provider,{value:n},e.children)}}}]);