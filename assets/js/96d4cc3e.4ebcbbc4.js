"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[8662],{60659:(e,r,t)=>{t.r(r),t.d(r,{assets:()=>h,contentTitle:()=>l,default:()=>q,frontMatter:()=>o,metadata:()=>p,toc:()=>u});var a=t(87462),s=t(63366),n=(t(67294),t(3905)),i=(t(95657),["components"]),o={id:"graphql-request-parser",title:"GraphQLRequestParser"},l=void 0,p={unversionedId:"server/graphql-request-parser",id:"version-6.x.x/server/graphql-request-parser",title:"GraphQLRequestParser",description:"The GraphQLRequestParser interface is required to parse the library-specific HTTP request object into the common GraphQLServerRequest class.",source:"@site/versioned_docs/version-6.x.x/server/graphql-request-parser.md",sourceDirName:"server",slug:"/server/graphql-request-parser",permalink:"/graphql-kotlin/docs/server/graphql-request-parser",draft:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-6.x.x/server/graphql-request-parser.md",tags:[],version:"6.x.x",lastUpdatedBy:"Shane Myrick",lastUpdatedAt:1685995381,formattedLastUpdatedAt:"Jun 5, 2023",frontMatter:{id:"graphql-request-parser",title:"GraphQLRequestParser"},sidebar:"docs",previous:{title:"GraphQLServer",permalink:"/graphql-kotlin/docs/server/graphql-server"},next:{title:"GraphQLContextFactory",permalink:"/graphql-kotlin/docs/server/graphql-context-factory"}},h={},u=[],d={toc:u},c="wrapper";function q(e){var r=e.components,t=(0,s.Z)(e,i);return(0,n.kt)(c,(0,a.Z)({},d,t,{components:r,mdxType:"MDXLayout"}),(0,n.kt)("p",null,"The ",(0,n.kt)("inlineCode",{parentName:"p"},"GraphQLRequestParser")," interface is required to parse the library-specific HTTP request object into the common ",(0,n.kt)("inlineCode",{parentName:"p"},"GraphQLServerRequest")," class."),(0,n.kt)("pre",null,(0,n.kt)("code",{parentName:"pre",className:"language-kotlin"},"interface GraphQLRequestParser<Request> {\n    suspend fun parseRequest(request: Request): GraphQLServerRequest?\n}\n")),(0,n.kt)("p",null,"While not officially part of the spec, there is a standard format used by most GraphQL clients and servers for ",(0,n.kt)("a",{parentName:"p",href:"https://graphql.org/learn/serving-over-http/"},"serving GraphQL over HTTP"),".\nFollowing the above convention, GraphQL clients should generally use HTTP POST requests with the following body structure"),(0,n.kt)("pre",null,(0,n.kt)("code",{parentName:"pre",className:"language-json"},'{\n  "query": "...",\n  "operationName": "...",\n  "variables": { "myVariable": "someValue" }\n}\n')),(0,n.kt)("p",null,"where"),(0,n.kt)("ul",null,(0,n.kt)("li",{parentName:"ul"},(0,n.kt)("inlineCode",{parentName:"li"},"query")," is a required field and contains the operation (query, mutation, or subscription) to be executed"),(0,n.kt)("li",{parentName:"ul"},(0,n.kt)("inlineCode",{parentName:"li"},"operationName")," is an optional string, only required if multiple operations are specified in the ",(0,n.kt)("inlineCode",{parentName:"li"},"query")," string."),(0,n.kt)("li",{parentName:"ul"},(0,n.kt)("inlineCode",{parentName:"li"},"variables")," is an optional map of JSON objects that are referenced as input arguments in the ",(0,n.kt)("inlineCode",{parentName:"li"},"query")," string")),(0,n.kt)("p",null,"GraphQL Kotlin server supports both single and batch GraphQL requests. Batch requests are represented as a list of individual\nGraphQL requests. When processing batch requests, the same context will be used for processing all requests and the server will\nrespond with a list of GraphQL responses."),(0,n.kt)("p",null,"If the request is not a valid GraphQL format, the interface should return ",(0,n.kt)("inlineCode",{parentName:"p"},"null")," and let the server specific code return a bad request status to the client.\nThis is not the same as a GraphQL error or an exception thrown by the schema.\nThose types of errors should still parse the request and return a valid response with errors set via the ",(0,n.kt)("a",{parentName:"p",href:"/graphql-kotlin/docs/server/graphql-request-handler"},"GraphQLRequestHandler"),"."),(0,n.kt)("p",null,"This interface should only be concerned with parsing the request, not about forwarding info to the context or execution.\nThat is handled by the ",(0,n.kt)("a",{parentName:"p",href:"/graphql-kotlin/docs/server/graphql-context-factory"},"GraphQLContextFactory"),"."))}q.isMDXComponent=!0}}]);