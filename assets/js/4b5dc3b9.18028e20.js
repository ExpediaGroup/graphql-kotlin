"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[6567],{79141:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>c,contentTitle:()=>i,default:()=>h,frontMatter:()=>o,metadata:()=>s,toc:()=>l});var a=n(74848),r=n(28453);const o={id:"fetching-data",title:"Fetching Data",original_id:"fetching-data"},i=void 0,s={id:"schema-generator/execution/fetching-data",title:"Fetching Data",description:"Each field exposed through a GraphQL query has a corresponding resolver (aka data fetcher) associated with it. graphql-kotlin-schema-generator generates GraphQL schema",source:"@site/versioned_docs/version-3.x.x/schema-generator/execution/fetching-data.md",sourceDirName:"schema-generator/execution",slug:"/schema-generator/execution/fetching-data",permalink:"/graphql-kotlin/docs/3.x.x/schema-generator/execution/fetching-data",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-3.x.x/schema-generator/execution/fetching-data.md",tags:[],version:"3.x.x",lastUpdatedBy:"Tasuku Nakagawa",lastUpdatedAt:1714237421,formattedLastUpdatedAt:"Apr 27, 2024",frontMatter:{id:"fetching-data",title:"Fetching Data",original_id:"fetching-data"},sidebar:"docs",previous:{title:"Advanced Features",permalink:"/graphql-kotlin/docs/3.x.x/schema-generator/customizing-schemas/advanced-features"},next:{title:"Async Models",permalink:"/graphql-kotlin/docs/3.x.x/schema-generator/execution/async-models"}},c={},l=[{value:"Customizing Default Behavior",id:"customizing-default-behavior",level:3}];function d(e){const t={a:"a",code:"code",h3:"h3",p:"p",pre:"pre",...(0,r.R)(),...e.components};return(0,a.jsxs)(a.Fragment,{children:[(0,a.jsxs)(t.p,{children:["Each field exposed through a GraphQL query has a corresponding resolver (aka data fetcher) associated with it. ",(0,a.jsx)(t.code,{children:"graphql-kotlin-schema-generator"})," generates GraphQL schema\ndirectly from the source code automatically mapping all the fields either to use\n",(0,a.jsx)(t.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/3.x.x/graphql-kotlin-schema-generator/src/main/kotlin/com/expediagroup/graphql/execution/FunctionDataFetcher.kt",children:"FunctionDataFetcher"}),"\nto resolve underlying functions or a ",(0,a.jsx)(t.a,{href:"https://www.graphql-java.com/documentation/v14/data-fetching/",children:"PropertyDataFetcher"})," to read a value from an underlying Kotlin property."]}),"\n",(0,a.jsx)(t.p,{children:"While all the fields in a GraphQL query are resolved independently to produce a final result, whether field is backed by a function or a property can have significant\nperformance repercussions. For example, given the following schema:"}),"\n",(0,a.jsx)(t.pre,{children:(0,a.jsx)(t.code,{className:"language-graphql",children:"\ntype Query {\n  product(id: Int!): Product\n}\n\ntype Product {\n  id: Int!\n  name: String!\n  reviews: [Review!]!\n}\n\ntype Review {\n  id: Int!\n  text: String!\n}\n\n"})}),"\n",(0,a.jsxs)(t.p,{children:["We can define ",(0,a.jsx)(t.code,{children:"Product"})," as"]}),"\n",(0,a.jsx)(t.pre,{children:(0,a.jsx)(t.code,{className:"language-kotlin",children:"\ndata class Product(val id: Int, val name: String, reviews: List<Review>)\n\n"})}),"\n",(0,a.jsx)(t.p,{children:"or"}),"\n",(0,a.jsx)(t.pre,{children:(0,a.jsx)(t.code,{className:"language-kotlin",children:"\nclass Product(val id: Int, val name: String) {\n  suspend fun reviews(): List<Reviews> {\n     // logic to fetch reviews here\n  }\n}\n\n"})}),"\n",(0,a.jsxs)(t.p,{children:["If we expose the ",(0,a.jsx)(t.code,{children:"reviews"})," field as a property it will always be populated regardless whether or not your client actually asks for it. On the other hand if ",(0,a.jsx)(t.code,{children:"reviews"})," is backed\nby a function, it will only be called if your client asks for this data. In order to minimize the over-fetching of data from your underlying data sources we recommend to\nexpose all your GraphQL fields that require some additional computations through functions."]}),"\n",(0,a.jsx)(t.h3,{id:"customizing-default-behavior",children:"Customizing Default Behavior"}),"\n",(0,a.jsxs)(t.p,{children:["You can provide your own custom data fetchers to resolve functions and properties by providing an instance of\n",(0,a.jsx)(t.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/3.x.x/graphql-kotlin-schema-generator/src/main/kotlin/com/expediagroup/graphql/execution/KotlinDataFetcherFactoryProvider.kt#L31",children:"KotlinDataFetcherFactoryProvider"}),"\nto your ",(0,a.jsx)(t.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/3.x.x/graphql-kotlin-schema-generator/src/main/kotlin/com/expediagroup/graphql/SchemaGeneratorConfig.kt",children:"SchemaGeneratorConfig"}),".\nSee our ",(0,a.jsx)(t.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/tree/3.x.x/examples/spring",children:"spring example app"})," for an example of ",(0,a.jsx)(t.code,{children:"CustomDataFetcherFactoryProvider"}),"."]})]})}function h(e={}){const{wrapper:t}={...(0,r.R)(),...e.components};return t?(0,a.jsx)(t,{...e,children:(0,a.jsx)(d,{...e})}):d(e)}},28453:(e,t,n)=>{n.d(t,{R:()=>i,x:()=>s});var a=n(96540);const r={},o=a.createContext(r);function i(e){const t=a.useContext(o);return a.useMemo((function(){return"function"==typeof e?e(t):{...t,...e}}),[t,e])}function s(e){let t;return t=e.disableParentContext?"function"==typeof e.components?e.components(r):e.components||r:i(e.components),a.createElement(o.Provider,{value:t},e.children)}}}]);