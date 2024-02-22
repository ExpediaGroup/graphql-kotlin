"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[4008],{69304:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>i,contentTitle:()=>s,default:()=>h,frontMatter:()=>a,metadata:()=>c,toc:()=>l});var r=n(74848),o=n(28453);const a={id:"graphql-context-factory",title:"GraphQLContextFactory"},s=void 0,c={id:"server/graphql-context-factory",title:"GraphQLContextFactory",description:"If you are using graphql-kotlin-spring-server, see the Spring specific documentation.",source:"@site/versioned_docs/version-4.x.x/server/graphql-context-factory.md",sourceDirName:"server",slug:"/server/graphql-context-factory",permalink:"/graphql-kotlin/docs/4.x.x/server/graphql-context-factory",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-4.x.x/server/graphql-context-factory.md",tags:[],version:"4.x.x",lastUpdatedBy:"Dariusz Kuc",lastUpdatedAt:1708623265,formattedLastUpdatedAt:"Feb 22, 2024",frontMatter:{id:"graphql-context-factory",title:"GraphQLContextFactory"},sidebar:"docs",previous:{title:"GraphQLRequestParser",permalink:"/graphql-kotlin/docs/4.x.x/server/graphql-request-parser"},next:{title:"GraphQLRequestHandler",permalink:"/graphql-kotlin/docs/4.x.x/server/graphql-request-handler"}},i={},l=[{value:"Nullable Context",id:"nullable-context",level:2},{value:"Suspendable Factory",id:"suspendable-factory",level:2},{value:"Server-Specific Abstractions",id:"server-specific-abstractions",level:2},{value:"HTTP Headers and Cookies",id:"http-headers-and-cookies",level:2},{value:"Federated Tracing",id:"federated-tracing",level:2}];function d(e){const t={a:"a",admonition:"admonition",code:"code",h2:"h2",p:"p",pre:"pre",...(0,o.R)(),...e.components};return(0,r.jsxs)(r.Fragment,{children:[(0,r.jsx)(t.admonition,{type:"note",children:(0,r.jsxs)(t.p,{children:["If you are using ",(0,r.jsx)(t.code,{children:"graphql-kotlin-spring-server"}),", see the ",(0,r.jsx)(t.a,{href:"/graphql-kotlin/docs/4.x.x/server/spring-server/spring-graphql-context",children:"Spring specific documentation"}),"."]})}),"\n",(0,r.jsxs)(t.p,{children:[(0,r.jsx)(t.code,{children:"GraphQLContextFactory"})," is a generic method for generating a ",(0,r.jsx)(t.code,{children:"GraphQLContext"})," for each request."]}),"\n",(0,r.jsx)(t.pre,{children:(0,r.jsx)(t.code,{className:"language-kotlin",children:"interface GraphQLContextFactory<out Context : GraphQLContext, Request> {\n    suspend fun generateContext(request: Request): Context?\n}\n"})}),"\n",(0,r.jsxs)(t.p,{children:["Given the generic server request, the interface should create a ",(0,r.jsx)(t.code,{children:"GraphQLContext"})," class to be used for every new operation.\nThe context must implement the ",(0,r.jsx)(t.code,{children:"GraphQLContext"})," interface from ",(0,r.jsx)(t.code,{children:"graphql-kotlin-schema-generator"}),".\nSee ",(0,r.jsx)(t.a,{href:"/graphql-kotlin/docs/4.x.x/schema-generator/execution/contextual-data",children:"execution context"})," for more info on how the context can be used in the schema functions."]}),"\n",(0,r.jsx)(t.h2,{id:"nullable-context",children:"Nullable Context"}),"\n",(0,r.jsxs)(t.p,{children:["The factory may return ",(0,r.jsx)(t.code,{children:"null"})," if a context is not required for execution. This allows the library to have a default factory that just returns ",(0,r.jsx)(t.code,{children:"null"}),".\nIf your custom factory never returns ",(0,r.jsx)(t.code,{children:"null"}),", then there is no need to use nullable arguments.\nHowever, if your custom factory may return ",(0,r.jsx)(t.code,{children:"null"}),", you must define the context argument as nullable in the schema functions or a runtime exception will be thrown."]}),"\n",(0,r.jsx)(t.pre,{children:(0,r.jsx)(t.code,{className:"language-kotlin",children:"data class CustomContext(val value: String) : GraphQLContext\n\nclass CustomFactory : GraphQLContextFactory<CustomContext, ServerRequest> {\n    suspend fun generateContext(request: Request): Context? {\n        if (isSpecialRequest(request)) {\n            return null\n        }\n\n        val value = callSomeSuspendableService(request)\n        return CustomContext(value)\n    }\n}\n\nclass MyQuery : Query {\n\n    fun getResults(context: CustomContext?, input: String): String {\n        if (context != null) {\n            return getDataWithContext(input, context)\n        }\n\n        return getBasicData(input)\n    }\n}\n"})}),"\n",(0,r.jsx)(t.h2,{id:"suspendable-factory",children:"Suspendable Factory"}),"\n",(0,r.jsxs)(t.p,{children:["The interface is marked as a ",(0,r.jsx)(t.code,{children:"suspend"})," function to allow the asynchronous fetching of context data.\nThis may be helpful if you need to call some other services to calculate a context value."]}),"\n",(0,r.jsx)(t.h2,{id:"server-specific-abstractions",children:"Server-Specific Abstractions"}),"\n",(0,r.jsxs)(t.p,{children:["A specific ",(0,r.jsx)(t.code,{children:"graphql-kotlin-*-server"})," library may provide an abstract class on top of this interface so users only have to be concerned with the context class and not the server class type.\nFor example the ",(0,r.jsx)(t.code,{children:"graphql-kotlin-spring-server"})," provides the following class, which sets the request type:"]}),"\n",(0,r.jsx)(t.pre,{children:(0,r.jsx)(t.code,{className:"language-kotlin",children:"abstract class SpringGraphQLContextFactory<out T : GraphQLContext> : GraphQLContextFactory<T, ServerRequest>\n"})}),"\n",(0,r.jsx)(t.h2,{id:"http-headers-and-cookies",children:"HTTP Headers and Cookies"}),"\n",(0,r.jsxs)(t.p,{children:["For common use cases around authorization, authentication, or tracing you may need to read HTTP headers and cookies.\nThis should be done in the ",(0,r.jsx)(t.code,{children:"GraphQLContextFactory"})," and relevant data should be added to the context to be accessible during schema exectuion."]}),"\n",(0,r.jsx)(t.h2,{id:"federated-tracing",children:"Federated Tracing"}),"\n",(0,r.jsxs)(t.p,{children:["If you need ",(0,r.jsx)(t.a,{href:"/graphql-kotlin/docs/4.x.x/schema-generator/federation/federation-tracing",children:"federation tracing support"}),", the context must implement the separate ",(0,r.jsx)(t.code,{children:"FederatedGraphQLContext"})," interface from ",(0,r.jsx)(t.code,{children:"graphql-kotlin-federation"}),"."]}),"\n",(0,r.jsxs)(t.p,{children:["The reference server implementation ",(0,r.jsx)(t.code,{children:"graphql-kotlin-spring-server"})," ",(0,r.jsx)(t.a,{href:"/graphql-kotlin/docs/4.x.x/server/spring-server/spring-graphql-context",children:"supports federated tracing in the context"}),"."]})]})}function h(e={}){const{wrapper:t}={...(0,o.R)(),...e.components};return t?(0,r.jsx)(t,{...e,children:(0,r.jsx)(d,{...e})}):d(e)}},28453:(e,t,n)=>{n.d(t,{R:()=>s,x:()=>c});var r=n(96540);const o={},a=r.createContext(o);function s(e){const t=r.useContext(a);return r.useMemo((function(){return"function"==typeof e?e(t):{...t,...e}}),[t,e])}function c(e){let t;return t=e.disableParentContext?"function"==typeof e.components?e.components(o):e.components||o:s(e.components),r.createElement(a.Provider,{value:t},e.children)}}}]);