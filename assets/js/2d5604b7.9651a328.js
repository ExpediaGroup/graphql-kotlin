"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[6313],{2562:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>c,contentTitle:()=>i,default:()=>p,frontMatter:()=>o,metadata:()=>d,toc:()=>s});var r=n(74848),a=n(28453);const o={id:"federation-tracing",title:"Federation Tracing"},i=void 0,d={id:"schema-generator/federation/federation-tracing",title:"Federation Tracing",description:"graphql-kotlin-federation module relies on apollographql/federation-jvm",source:"@site/versioned_docs/version-6.x.x/schema-generator/federation/federation-tracing.md",sourceDirName:"schema-generator/federation",slug:"/schema-generator/federation/federation-tracing",permalink:"/graphql-kotlin/docs/6.x.x/schema-generator/federation/federation-tracing",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-6.x.x/schema-generator/federation/federation-tracing.md",tags:[],version:"6.x.x",lastUpdatedBy:"Dariusz Kuc",lastUpdatedAt:1708623265,formattedLastUpdatedAt:"Feb 22, 2024",frontMatter:{id:"federation-tracing",title:"Federation Tracing"},sidebar:"docs",previous:{title:"Federated Type Resolution",permalink:"/graphql-kotlin/docs/6.x.x/schema-generator/federation/type-resolution"},next:{title:"GraphQLServer",permalink:"/graphql-kotlin/docs/6.x.x/server/graphql-server"}},c={},s=[{value:"GraphQL Context Map",id:"graphql-context-map",level:3},{value:"<code>FederatedGraphQLContext</code> (Deprecated)",id:"federatedgraphqlcontext-deprecated",level:3}];function l(e){const t={a:"a",admonition:"admonition",code:"code",h3:"h3",p:"p",pre:"pre",...(0,a.R)(),...e.components};return(0,r.jsxs)(r.Fragment,{children:[(0,r.jsxs)(t.p,{children:[(0,r.jsx)(t.code,{children:"graphql-kotlin-federation"})," module relies on ",(0,r.jsx)(t.a,{href:"https://github.com/apollographql/federation-jvm",children:"apollographql/federation-jvm"}),"\npackage to provide support for Apollo Federation tracing. Tracing is turned on by including ",(0,r.jsx)(t.code,{children:"FederatedTracingInstrumentation"}),"\nin your GraphQL instance. In order for the ",(0,r.jsx)(t.code,{children:"FederatedTracingInstrumentation"})," to know whether incoming request should be\ntraced, we need to provide it a ",(0,r.jsx)(t.code,{children:"apollo-federation-include-trace"})," header value."]}),"\n",(0,r.jsx)(t.pre,{children:(0,r.jsx)(t.code,{className:"language-kotlin",children:"val schema = toFederatedSchema(myFederatedConfig, listOf(TopLevelObject(MyFederatedQuery())))\nval graphQL = GraphQL.newGraphQL(schema)\n    .instrumentation(FederatedTracingInstrumentation())\n    .build()\n"})}),"\n",(0,r.jsx)(t.h3,{id:"graphql-context-map",children:"GraphQL Context Map"}),"\n",(0,r.jsx)(t.admonition,{type:"note",children:(0,r.jsxs)(t.p,{children:["Default ",(0,r.jsx)(t.code,{children:"GraphQLContextFactory"})," provided by ",(0,r.jsx)(t.code,{children:"graphql-kotlin-spring-server"})," populates this header information automatically."]})}),"\n",(0,r.jsx)(t.p,{children:"Tracing header information can be provided by populating info directly on the GraphQL context map."}),"\n",(0,r.jsx)(t.pre,{children:(0,r.jsx)(t.code,{className:"language-kotlin",children:"val contextMap = mutableMapOf<Any, Any>()\n    .also { map ->\n        request.headers().firstHeader(FEDERATED_TRACING_HEADER_NAME)?.let { headerValue ->\n            map[FEDERATED_TRACING_HEADER_NAME] = headerValue\n        }\n    }\n\nval executionInput = ExecutionInput.newExecutionInput()\n    .graphQLContext(contextMap)\n    .query(queryString)\n    .build()\ngraphql.executeAsync(executionInput)\n"})}),"\n",(0,r.jsxs)(t.h3,{id:"federatedgraphqlcontext-deprecated",children:[(0,r.jsx)(t.code,{children:"FederatedGraphQLContext"})," (Deprecated)"]}),"\n",(0,r.jsx)(t.admonition,{type:"danger",children:(0,r.jsx)(t.p,{children:"Support for custom GraphQL context object is deprecated and will be removed in future releases. Please migrate to use\ngeneric GraphQL context map."})}),"\n",(0,r.jsxs)(t.p,{children:["To best support tracing, the context must implement a specific method to get the HTTP headers from the request.\nThis is done by implementing the ",(0,r.jsx)(t.code,{children:"FederatedGraphQLContext"})," interface instead of just the ",(0,r.jsx)(t.code,{children:"GraphQLContext"})," interface\nfrom ",(0,r.jsx)(t.code,{children:"graphql-kotlin-schema-generator"}),"."]}),"\n",(0,r.jsx)(t.pre,{children:(0,r.jsx)(t.code,{className:"language-kotlin",children:"class MyFederatedGraphQLContext(private val request: ServerRequest) : FederatedGraphQLContext {\n    override fun getHTTPRequestHeader(caseInsensitiveHeaderName: String): String? =\n        request.headers().firstHeader(caseInsensitiveHeaderName)\n}\n\nval executionInput = ExecutionInput.newExecutionInput()\n    .context(MyFederatedGraphQLContext(httpRequest))\n    .query(queryString)\n    .build()\ngraphql.executeAsync(executionInput)\n"})})]})}function p(e={}){const{wrapper:t}={...(0,a.R)(),...e.components};return t?(0,r.jsx)(t,{...e,children:(0,r.jsx)(l,{...e})}):l(e)}},28453:(e,t,n)=>{n.d(t,{R:()=>i,x:()=>d});var r=n(96540);const a={},o=r.createContext(a);function i(e){const t=r.useContext(o);return r.useMemo((function(){return"function"==typeof e?e(t):{...t,...e}}),[t,e])}function d(e){let t;return t=e.disableParentContext?"function"==typeof e.components?e.components(a):e.components||a:i(e.components),r.createElement(o.Provider,{value:t},e.children)}}}]);