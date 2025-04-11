"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[6905],{88939:(e,t,r)=>{r.r(t),r.d(t,{assets:()=>c,contentTitle:()=>i,default:()=>d,frontMatter:()=>o,metadata:()=>s,toc:()=>l});var a=r(74848),n=r(28453);const o={id:"fetching-data",title:"Fetching Data"},i=void 0,s={id:"schema-generator/execution/fetching-data",title:"Fetching Data",description:"Each field exposed in the GraphQL schema has a corresponding resolver (aka data fetcher) associated with it. graphql-kotlin-schema-generator generates the GraphQL schema",source:"@site/versioned_docs/version-4.x.x/schema-generator/execution/fetching-data.md",sourceDirName:"schema-generator/execution",slug:"/schema-generator/execution/fetching-data",permalink:"/graphql-kotlin/docs/4.x.x/schema-generator/execution/fetching-data",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-4.x.x/schema-generator/execution/fetching-data.md",tags:[],version:"4.x.x",lastUpdatedBy:"Samuel Vazquez",lastUpdatedAt:174440151e4,frontMatter:{id:"fetching-data",title:"Fetching Data"},sidebar:"docs",previous:{title:"Advanced Features",permalink:"/graphql-kotlin/docs/4.x.x/schema-generator/customizing-schemas/advanced-features"},next:{title:"Async Models",permalink:"/graphql-kotlin/docs/4.x.x/schema-generator/execution/async-models"}},c={},l=[{value:"Customizing Default Behavior",id:"customizing-default-behavior",level:3}];function h(e){const t={a:"a",code:"code",h3:"h3",p:"p",pre:"pre",...(0,n.R)(),...e.components};return(0,a.jsxs)(a.Fragment,{children:[(0,a.jsxs)(t.p,{children:["Each field exposed in the GraphQL schema has a corresponding resolver (aka data fetcher) associated with it. ",(0,a.jsx)(t.code,{children:"graphql-kotlin-schema-generator"})," generates the GraphQL schema\ndirectly from the source code, automatically mapping all the fields either to use\n",(0,a.jsx)(t.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/master/generator/graphql-kotlin-schema-generator/src/main/kotlin/com/expediagroup/graphql/generator/execution/FunctionDataFetcher.kt",children:"FunctionDataFetcher"}),"\nto resolve underlying functions or the ",(0,a.jsx)(t.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/master/generator/graphql-kotlin-schema-generator/src/main/kotlin/com/expediagroup/graphql/generator/execution/PropertyDataFetcher.kt",children:"PropertyDataFetcher"}),"\nto read a value from an underlying Kotlin property."]}),"\n",(0,a.jsx)(t.p,{children:"While all the fields in a GraphQL schema are resolved independently to produce a final result, whether a field is backed by a function or a property can have significant\nperformance repercussions. For example, given the following schema:"}),"\n",(0,a.jsx)(t.pre,{children:(0,a.jsx)(t.code,{className:"language-graphql",children:"type Query {\n  product(id: ID!): Product\n}\n\ntype Product {\n  id: ID!\n  name: String!\n  reviews: [Review!]!\n}\n\ntype Review {\n  id: ID!\n  text: String!\n}\n"})}),"\n",(0,a.jsxs)(t.p,{children:["We can define ",(0,a.jsx)(t.code,{children:"Product"})," as"]}),"\n",(0,a.jsx)(t.pre,{children:(0,a.jsx)(t.code,{className:"language-kotlin",children:"data class Product(val id: ID, val name: String, reviews: List<Review>)\n"})}),"\n",(0,a.jsx)(t.p,{children:"or"}),"\n",(0,a.jsx)(t.pre,{children:(0,a.jsx)(t.code,{className:"language-kotlin",children:"class Product(val id: ID, val name: String) {\n  suspend fun reviews(): List<Reviews> {\n     // logic to fetch reviews here\n  }\n}\n"})}),"\n",(0,a.jsxs)(t.p,{children:["If we expose the ",(0,a.jsx)(t.code,{children:"reviews"})," field as a property it will always be populated regardless whether or not your client actually asks for it. On the other hand if ",(0,a.jsx)(t.code,{children:"reviews"})," is backed\nby a function, it will only be called if your client asks for this data. In order to minimize the over-fetching of data from your underlying data sources we recommend to\nexpose all your GraphQL fields that require some additional computations through functions."]}),"\n",(0,a.jsx)(t.h3,{id:"customizing-default-behavior",children:"Customizing Default Behavior"}),"\n",(0,a.jsxs)(t.p,{children:["You can provide your own custom data fetchers to resolve functions and properties by providing an instance of\n",(0,a.jsx)(t.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/master/generator/graphql-kotlin-schema-generator/src/main/kotlin/com/expediagroup/graphql/generator/execution/KotlinDataFetcherFactoryProvider.kt#L31",children:"KotlinDataFetcherFactoryProvider"}),"\nto your ",(0,a.jsx)(t.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/master/generator/graphql-kotlin-schema-generator/src/main/kotlin/com/expediagroup/graphql/generator/SchemaGeneratorConfig.kt",children:"SchemaGeneratorConfig"}),"."]}),"\n",(0,a.jsxs)(t.p,{children:["See our ",(0,a.jsx)(t.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/examples/server/spring-server",children:"spring example app"})," for an example of ",(0,a.jsx)(t.code,{children:"CustomDataFetcherFactoryProvider"}),"."]})]})}function d(e={}){const{wrapper:t}={...(0,n.R)(),...e.components};return t?(0,a.jsx)(t,{...e,children:(0,a.jsx)(h,{...e})}):h(e)}},28453:(e,t,r)=>{r.d(t,{R:()=>i,x:()=>s});var a=r(96540);const n={},o=a.createContext(n);function i(e){const t=a.useContext(o);return a.useMemo((function(){return"function"==typeof e?e(t):{...t,...e}}),[t,e])}function s(e){let t;return t=e.disableParentContext?"function"==typeof e.components?e.components(n):e.components||n:i(e.components),a.createElement(o.Provider,{value:t},e.children)}}}]);