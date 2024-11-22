"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[2062],{64743:(e,r,s)=>{s.r(r),s.d(r,{assets:()=>l,contentTitle:()=>o,default:()=>p,frontMatter:()=>a,metadata:()=>i,toc:()=>c});var t=s(74848),n=s(28453);const a={id:"graphql-request-parser",title:"GraphQLRequestParser"},o=void 0,i={id:"server/graphql-request-parser",title:"GraphQLRequestParser",description:"The GraphQLRequestParser interface is required to parse the library-specific HTTP request object into the common GraphQLServerRequest class.",source:"@site/versioned_docs/version-7.x.x/server/graphql-request-parser.md",sourceDirName:"server",slug:"/server/graphql-request-parser",permalink:"/graphql-kotlin/docs/7.x.x/server/graphql-request-parser",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-7.x.x/server/graphql-request-parser.md",tags:[],version:"7.x.x",lastUpdatedBy:"Dale Seo",lastUpdatedAt:1732301091e3,frontMatter:{id:"graphql-request-parser",title:"GraphQLRequestParser"},sidebar:"docs",previous:{title:"GraphQLServer",permalink:"/graphql-kotlin/docs/7.x.x/server/graphql-server"},next:{title:"GraphQLContextFactory",permalink:"/graphql-kotlin/docs/7.x.x/server/graphql-context-factory"}},l={},c=[];function h(e){const r={a:"a",code:"code",li:"li",p:"p",pre:"pre",ul:"ul",...(0,n.R)(),...e.components};return(0,t.jsxs)(t.Fragment,{children:[(0,t.jsxs)(r.p,{children:["The ",(0,t.jsx)(r.code,{children:"GraphQLRequestParser"})," interface is required to parse the library-specific HTTP request object into the common ",(0,t.jsx)(r.code,{children:"GraphQLServerRequest"})," class."]}),"\n",(0,t.jsx)(r.pre,{children:(0,t.jsx)(r.code,{className:"language-kotlin",children:"interface GraphQLRequestParser<Request> {\n    suspend fun parseRequest(request: Request): GraphQLServerRequest?\n}\n"})}),"\n",(0,t.jsxs)(r.p,{children:["While not officially part of the spec, there is a standard format used by most GraphQL clients and servers for ",(0,t.jsx)(r.a,{href:"https://graphql.org/learn/serving-over-http/",children:"serving GraphQL over HTTP"}),".\nFollowing the above convention, GraphQL clients should generally use HTTP POST requests with the following body structure"]}),"\n",(0,t.jsx)(r.pre,{children:(0,t.jsx)(r.code,{className:"language-json",children:'{\n  "query": "...",\n  "operationName": "...",\n  "variables": { "myVariable": "someValue" }\n}\n'})}),"\n",(0,t.jsx)(r.p,{children:"where"}),"\n",(0,t.jsxs)(r.ul,{children:["\n",(0,t.jsxs)(r.li,{children:[(0,t.jsx)(r.code,{children:"query"})," is a required field and contains the operation (query, mutation, or subscription) to be executed"]}),"\n",(0,t.jsxs)(r.li,{children:[(0,t.jsx)(r.code,{children:"operationName"})," is an optional string, only required if multiple operations are specified in the ",(0,t.jsx)(r.code,{children:"query"})," string."]}),"\n",(0,t.jsxs)(r.li,{children:[(0,t.jsx)(r.code,{children:"variables"})," is an optional map of JSON objects that are referenced as input arguments in the ",(0,t.jsx)(r.code,{children:"query"})," string"]}),"\n"]}),"\n",(0,t.jsx)(r.p,{children:"GraphQL Kotlin server supports both single and batch GraphQL requests. Batch requests are represented as a list of individual\nGraphQL requests. When processing batch requests, the same context will be used for processing all requests and the server will\nrespond with a list of GraphQL responses."}),"\n",(0,t.jsxs)(r.p,{children:["If the request is not a valid GraphQL format, the interface should return ",(0,t.jsx)(r.code,{children:"null"})," and let the server specific code return a bad request status to the client.\nThis is not the same as a GraphQL error or an exception thrown by the schema.\nThose types of errors should still parse the request and return a valid response with errors set via the ",(0,t.jsx)(r.a,{href:"/graphql-kotlin/docs/7.x.x/server/graphql-request-handler",children:"GraphQLRequestHandler"}),"."]}),"\n",(0,t.jsxs)(r.p,{children:["This interface should only be concerned with parsing the request, not about forwarding info to the context or execution.\nThat is handled by the ",(0,t.jsx)(r.a,{href:"/graphql-kotlin/docs/7.x.x/server/graphql-context-factory",children:"GraphQLContextFactory"}),"."]})]})}function p(e={}){const{wrapper:r}={...(0,n.R)(),...e.components};return r?(0,t.jsx)(r,{...e,children:(0,t.jsx)(h,{...e})}):h(e)}},28453:(e,r,s)=>{s.d(r,{R:()=>o,x:()=>i});var t=s(96540);const n={},a=t.createContext(n);function o(e){const r=t.useContext(a);return t.useMemo((function(){return"function"==typeof e?e(r):{...r,...e}}),[r,e])}function i(e){let r;return r=e.disableParentContext?"function"==typeof e.components?e.components(n):e.components||n:o(e.components),t.createElement(a.Provider,{value:r},e.children)}}}]);