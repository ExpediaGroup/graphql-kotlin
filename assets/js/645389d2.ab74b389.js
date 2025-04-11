"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[3474],{14466:(e,r,n)=>{n.r(r),n.d(r,{assets:()=>i,contentTitle:()=>l,default:()=>h,frontMatter:()=>s,metadata:()=>o,toc:()=>p});var a=n(74848),t=n(28453);const s={id:"examples",title:"Examples"},l=void 0,o={id:"examples",title:"Examples",description:"A collection of example apps that use graphql-kotlin libraries to test and demonstrate usages can be found in the examples module.",source:"@site/versioned_docs/version-4.x.x/examples.md",sourceDirName:".",slug:"/examples",permalink:"/graphql-kotlin/docs/4.x.x/examples",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-4.x.x/examples.md",tags:[],version:"4.x.x",lastUpdatedBy:"Samuel Vazquez",lastUpdatedAt:174440151e4,frontMatter:{id:"examples",title:"Examples"},sidebar:"docs",previous:{title:"Getting Started",permalink:"/graphql-kotlin/docs/4.x.x/"},next:{title:"GraphQL Frameworks Comparison",permalink:"/graphql-kotlin/docs/4.x.x/framework-comparison"}},i={},p=[{value:"Client Example",id:"client-example",level:2},{value:"Federation Example",id:"federation-example",level:2},{value:"Server Examples",id:"server-examples",level:2},{value:"Ktor Server Example",id:"ktor-server-example",level:3},{value:"Spring Server Example",id:"spring-server-example",level:3}];function d(e){const r={a:"a",code:"code",h2:"h2",h3:"h3",p:"p",pre:"pre",...(0,t.R)(),...e.components};return(0,a.jsxs)(a.Fragment,{children:[(0,a.jsxs)(r.p,{children:["A collection of example apps that use graphql-kotlin libraries to test and demonstrate usages can be found in the ",(0,a.jsx)(r.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/examples",children:"examples module"}),"."]}),"\n",(0,a.jsx)(r.h2,{id:"client-example",children:"Client Example"}),"\n",(0,a.jsxs)(r.p,{children:["A ",(0,a.jsx)(r.code,{children:"graphql-kotlin-client"})," can be generated by using the provided Maven or Gradle. Example integration using Maven and\nGradle plugins can be found under the ",(0,a.jsx)(r.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/examples/client",children:"examples/client"}),"\nfolder."]}),"\n",(0,a.jsx)(r.h2,{id:"federation-example",children:"Federation Example"}),"\n",(0,a.jsxs)(r.p,{children:["There is also an example of ",(0,a.jsx)(r.a,{href:"https://www.apollographql.com/docs/apollo-server/federation/introduction/",children:"Apollo Federation"}),"\nwith two Spring Boot apps using ",(0,a.jsx)(r.code,{children:"graphql-kotlin-federation"})," and an Apollo Gateway app in Nodejs that exposes a single\nfederated schema in ",(0,a.jsx)(r.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/examples/federation",children:"examples/federation"}),"\nproject. Please refer to the README files for details on how to run each application."]}),"\n",(0,a.jsx)(r.h2,{id:"server-examples",children:"Server Examples"}),"\n",(0,a.jsxs)(r.p,{children:["Example integrations of ",(0,a.jsx)(r.code,{children:"graphql-kotlin-schema-generator"})," with number of popular application frameworks can be found under\n",(0,a.jsx)(r.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/examples/server",children:"examples/server"})," folder."]}),"\n",(0,a.jsxs)(r.p,{children:["These examples also demonstrates how to include ",(0,a.jsx)(r.a,{href:"https://github.com/graphql-java/java-dataloader",children:(0,a.jsx)(r.code,{children:"DataLoaders"})})," in your query execution."]}),"\n",(0,a.jsx)(r.h3,{id:"ktor-server-example",children:"Ktor Server Example"}),"\n",(0,a.jsxs)(r.p,{children:[(0,a.jsx)(r.a,{href:"http://ktor.io/",children:"Ktor"})," is an asynchronous framework for creating microservices, web applications, and more. Example\nintegration can be found at ",(0,a.jsx)(r.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/examples/server/ktor-server",children:"examples/server/ktor-server"})]}),"\n",(0,a.jsx)(r.h3,{id:"spring-server-example",children:"Spring Server Example"}),"\n",(0,a.jsxs)(r.p,{children:["One way to run a GraphQL server is with ",(0,a.jsx)(r.a,{href:"https://github.com/spring-projects/spring-boot",children:"Spring Boot"}),". A sample Spring\nBoot app that uses ",(0,a.jsx)(r.a,{href:"https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html",children:"Spring\nWebflux"})," together with\n",(0,a.jsx)(r.code,{children:"graphql-kotlin-schema-generator"})," and ",(0,a.jsx)(r.a,{href:"https://github.com/prisma/graphql-playground",children:"graphql-playground"})," is provided as\na ",(0,a.jsx)(r.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/examples/server/spring-server",children:"examples/server/spring-server"}),".\nAll the examples used in this documentation should be available in this sample app."]}),"\n",(0,a.jsxs)(r.p,{children:["In order to run it you can run\n",(0,a.jsx)(r.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/master/examples/spring/src/main/kotlin/com/expediagroup/graphql/examples/Application.kt",children:"Application.kt"}),"\ndirectly from your IDE. Alternatively you can also use the Spring Boot plugin from the command line."]}),"\n",(0,a.jsx)(r.pre,{children:(0,a.jsx)(r.code,{className:"language-shell",metastring:"script",children:"\n./gradlew bootRun\n\n"})}),"\n",(0,a.jsx)(r.p,{children:"Once the app has started you can explore the example schema by opening Playground endpoint at\nhttp:."})]})}function h(e={}){const{wrapper:r}={...(0,t.R)(),...e.components};return r?(0,a.jsx)(r,{...e,children:(0,a.jsx)(d,{...e})}):d(e)}},28453:(e,r,n)=>{n.d(r,{R:()=>l,x:()=>o});var a=n(96540);const t={},s=a.createContext(t);function l(e){const r=a.useContext(s);return a.useMemo((function(){return"function"==typeof e?e(r):{...r,...e}}),[r,e])}function o(e){let r;return r=e.disableParentContext?"function"==typeof e.components?e.components(t):e.components||t:l(e.components),a.createElement(s.Provider,{value:r},e.children)}}}]);