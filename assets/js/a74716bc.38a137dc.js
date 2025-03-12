"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[764],{5357:(e,n,t)=>{t.r(n),t.d(n,{assets:()=>a,contentTitle:()=>o,default:()=>u,frontMatter:()=>i,metadata:()=>l,toc:()=>p});var s=t(74848),r=t(28453);const i={id:"ktor-http-request-response",title:"HTTP request and response"},o=void 0,l={id:"server/ktor-server/ktor-http-request-response",title:"HTTP request and response",description:"Ktor HTTP request/response can be intercepted by installing various plugins to your module or by intercepting specific",source:"@site/versioned_docs/version-8.x.x/server/ktor-server/ktor-http-request-response.md",sourceDirName:"server/ktor-server",slug:"/server/ktor-server/ktor-http-request-response",permalink:"/graphql-kotlin/docs/server/ktor-server/ktor-http-request-response",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-8.x.x/server/ktor-server/ktor-http-request-response.md",tags:[],version:"8.x.x",lastUpdatedBy:"Daniel",lastUpdatedAt:1741819577e3,frontMatter:{id:"ktor-http-request-response",title:"HTTP request and response"},sidebar:"docs",previous:{title:"Generating GraphQL Context",permalink:"/graphql-kotlin/docs/server/ktor-server/ktor-graphql-context"},next:{title:"Ktor Plugin Configuration",permalink:"/graphql-kotlin/docs/server/ktor-server/ktor-configuration"}},a={},p=[{value:"Installing Additional Plugins",id:"installing-additional-plugins",level:2},{value:"Intercepting Pipeline Phases",id:"intercepting-pipeline-phases",level:2}];function c(e){const n={a:"a",code:"code",h2:"h2",mermaid:"mermaid",p:"p",pre:"pre",...(0,r.R)(),...e.components};return(0,s.jsxs)(s.Fragment,{children:[(0,s.jsxs)(n.p,{children:["Ktor HTTP request/response can be intercepted by installing various plugins to your module or by intercepting specific\nphases of application call pipeline. By installing ",(0,s.jsx)(n.code,{children:"graphql-kotlin-ktor-server"})," plugin you will configure following pipeline"]}),"\n",(0,s.jsx)(n.mermaid,{value:"flowchart LR\n    A(Request) --\x3e B(ContentNegotiation)\n    B --\x3e C(Routing)\n    C --\x3e D(GraphQL)\n    D --\x3e E(Response)"}),"\n",(0,s.jsx)(n.h2,{id:"installing-additional-plugins",children:"Installing Additional Plugins"}),"\n",(0,s.jsxs)(n.p,{children:["You can install additional plugins in your module next to the ",(0,s.jsx)(n.code,{children:"GraphQL"})," module. See ",(0,s.jsx)(n.a,{href:"https://ktor.io/docs/plugins.html",children:"Ktor docs"}),"\nfor details."]}),"\n",(0,s.jsx)(n.pre,{children:(0,s.jsx)(n.code,{className:"language-kotlin",children:'fun Application.myModule() {\n    // install additional plugins\n    install(CORS) { ... }\n    install(Authentication) { ... }\n    install(StatusPages) { ... }\n\n    // install graphql plugin\n    install(GraphQL) {\n        schema {\n            packages = listOf("com.example")\n            queries = listOf(TestQuery())\n        }\n    }\n    // install authenticated GraphQL routes\n    install(Routing) {\n        authenticate("auth-basic") {\n            graphQLPostRoute()\n        }\n    }\n}\n'})}),"\n",(0,s.jsx)(n.h2,{id:"intercepting-pipeline-phases",children:"Intercepting Pipeline Phases"}),"\n",(0,s.jsxs)(n.p,{children:["You can intercept requests/responses in various phases of application call pipeline by specifying an interceptor. See\n",(0,s.jsx)(n.a,{href:"https://ktor.io/docs/custom-plugins-base-api.html#call-handling",children:"Ktor docs"})," for details."]}),"\n",(0,s.jsx)(n.pre,{children:(0,s.jsx)(n.code,{className:"language-kotlin",children:'fun Application.myModule() {\n    install(GraphQL) {\n        schema {\n            packages = listOf("com.example")\n            queries = listOf(TestQuery())\n        }\n    }\n    install(Routing) {\n        graphQLPostRoute()\n    }\n\n    intercept(ApplicationCallPipeline.Monitoring) {\n        call.request.origin.apply {\n            println("Request URL: $scheme://$localHost:$localPort$uri")\n        }\n    }\n}\n'})})]})}function u(e={}){const{wrapper:n}={...(0,r.R)(),...e.components};return n?(0,s.jsx)(n,{...e,children:(0,s.jsx)(c,{...e})}):c(e)}},28453:(e,n,t)=>{t.d(n,{R:()=>o,x:()=>l});var s=t(96540);const r={},i=s.createContext(r);function o(e){const n=s.useContext(i);return s.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function l(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(r):e.components||r:o(e.components),s.createElement(i.Provider,{value:n},e.children)}}}]);