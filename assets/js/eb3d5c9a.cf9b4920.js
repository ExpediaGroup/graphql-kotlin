"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[5797],{54257:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>c,contentTitle:()=>i,default:()=>d,frontMatter:()=>o,metadata:()=>s,toc:()=>l});var a=n(74848),r=n(28453);const o={id:"contextual-data",title:"Contextual Data",original_id:"contextual-data"},i=void 0,s={id:"schema-generator/execution/contextual-data",title:"Contextual Data",description:'All GraphQL servers have a concept of a "context". A GraphQL context contains metadata that is useful to the GraphQL',source:"@site/versioned_docs/version-3.x.x/schema-generator/execution/contextual-data.md",sourceDirName:"schema-generator/execution",slug:"/schema-generator/execution/contextual-data",permalink:"/graphql-kotlin/docs/3.x.x/schema-generator/execution/contextual-data",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-3.x.x/schema-generator/execution/contextual-data.md",tags:[],version:"3.x.x",lastUpdatedBy:"Samuel Vazquez",lastUpdatedAt:1744404742e3,frontMatter:{id:"contextual-data",title:"Contextual Data",original_id:"contextual-data"},sidebar:"docs",previous:{title:"Data Fetching Environment",permalink:"/graphql-kotlin/docs/3.x.x/schema-generator/execution/data-fetching-environment"},next:{title:"Optional Undefined Arguments",permalink:"/graphql-kotlin/docs/3.x.x/schema-generator/execution/optional-undefined-arguments"}},c={},l=[{value:"GraphQLContext Interface",id:"graphqlcontext-interface",level:2},{value:"Spring Server",id:"spring-server",level:2},{value:"Customization",id:"customization",level:3}];function h(e){const t={a:"a",code:"code",h2:"h2",h3:"h3",p:"p",pre:"pre",...(0,r.R)(),...e.components};return(0,a.jsxs)(a.Fragment,{children:[(0,a.jsxs)(t.p,{children:['All GraphQL servers have a concept of a "context". A GraphQL context contains metadata that is useful to the GraphQL\nserver, but shouldn\'t necessarily be part of the GraphQL schema. A prime example of something that is appropriate\nfor the GraphQL context would be trace headers for an OpenTracing system such as\n',(0,a.jsx)(t.a,{href:"https://expediadotcom.github.io/haystack",children:"Haystack"}),". The GraphQL query does not need the information to perform\nits function, but the server needs the information to ensure observability."]}),"\n",(0,a.jsxs)(t.p,{children:["The contents of the GraphQL context vary across applications and it is up to the GraphQL server developers to decide\nwhat it should contain. For Spring based applications, ",(0,a.jsx)(t.code,{children:"graphql-kotlin-spring-server"})," provides a simple mechanism to\nbuild context per query execution through\n",(0,a.jsx)(t.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/3.x.x/graphql-kotlin-spring-server/src/main/kotlin/com/expediagroup/graphql/spring/execution/GraphQLContextFactory.kt",children:"GraphQLContextFactory"}),".\nOnce context factory bean is available in the Spring application context it will then be used in a corresponding\n",(0,a.jsx)(t.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/3.x.x/graphql-kotlin-spring-server/src/main/kotlin/com/expediagroup/graphql/spring/execution/ContextWebFilter.kt",children:"ContextWebFilter"}),"\nto populate GraphQL context based on the incoming request and make it available during query execution. See ",(0,a.jsx)(t.a,{href:"../../spring-server/spring-graphql-context",children:"graphql-kotlin-spring-server documentation"}),"\nfor additional details"]}),"\n",(0,a.jsx)(t.h2,{id:"graphqlcontext-interface",children:"GraphQLContext Interface"}),"\n",(0,a.jsxs)(t.p,{children:["The easiest way to specify a context class is to use the ",(0,a.jsx)(t.code,{children:"GraphQLContext"})," marker interface. This interface does not require any implementations,\nit is just used to inform the schema generator that this is the class that should be used as the context for every request."]}),"\n",(0,a.jsx)(t.pre,{children:(0,a.jsx)(t.code,{className:"language-kotlin",children:"\nclass MyGraphQLContext(val customValue: String) : GraphQLContext\n\n"})}),"\n",(0,a.jsx)(t.p,{children:"Then you can just use the class as an argument and it will be automatically injected during execution time."}),"\n",(0,a.jsx)(t.pre,{children:(0,a.jsx)(t.code,{className:"language-kotlin",children:'\nclass ContextualQuery {\n    fun contextualQuery(\n        context: MyGraphQLContext,\n        value: Int\n    ): String = "The custom value was ${context.customValue} and the value was $value"\n}\n\n'})}),"\n",(0,a.jsx)(t.p,{children:"The above query would produce the following GraphQL schema:"}),"\n",(0,a.jsx)(t.pre,{children:(0,a.jsx)(t.code,{className:"language-graphql",children:"\nschema {\n  query: Query\n}\n\ntype Query {\n  contextualQuery(value: Int!): String!\n}\n\n"})}),"\n",(0,a.jsxs)(t.p,{children:["Note that the argument that implements ",(0,a.jsx)(t.code,{children:"GraphQLContext"})," is not reflected in the GraphQL schema."]}),"\n",(0,a.jsx)(t.h2,{id:"spring-server",children:"Spring Server"}),"\n",(0,a.jsxs)(t.p,{children:["For more details on how to create the context while using ",(0,a.jsx)(t.code,{children:"graphql-kotlin-spring-server"})," see the ",(0,a.jsx)(t.a,{href:"/graphql-kotlin/docs/3.x.x/spring-server/spring-graphql-context",children:"spring graphql context page"}),"."]}),"\n",(0,a.jsx)(t.h3,{id:"customization",children:"Customization"}),"\n",(0,a.jsxs)(t.p,{children:["The context is injected into the execution through the ",(0,a.jsx)(t.code,{children:"FunctionDataFetcher"})," class. If you want to customize the logic on how the context is determined, that is possible to override. See more details on the ",(0,a.jsx)(t.a,{href:"./fetching-data",children:"Fetching Data documentation"})]})]})}function d(e={}){const{wrapper:t}={...(0,r.R)(),...e.components};return t?(0,a.jsx)(t,{...e,children:(0,a.jsx)(h,{...e})}):h(e)}},28453:(e,t,n)=>{n.d(t,{R:()=>i,x:()=>s});var a=n(96540);const r={},o=a.createContext(r);function i(e){const t=a.useContext(o);return a.useMemo((function(){return"function"==typeof e?e(t):{...t,...e}}),[t,e])}function s(e){let t;return t=e.disableParentContext?"function"==typeof e.components?e.components(r):e.components||r:i(e.components),a.createElement(o.Provider,{value:t},e.children)}}}]);