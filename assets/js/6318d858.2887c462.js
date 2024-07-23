"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[642],{63155:(e,n,t)=>{t.r(n),t.d(n,{assets:()=>s,contentTitle:()=>o,default:()=>h,frontMatter:()=>r,metadata:()=>c,toc:()=>l});var a=t(74848),i=t(28453);const r={id:"data-fetching-environment",title:"Data Fetching Environment"},o=void 0,c={id:"schema-generator/execution/data-fetching-environment",title:"Data Fetching Environment",description:"Each resolver has access to a DataFetchingEnvironment that provides additional information about the currently executed query including information about what data is requested",source:"@site/docs/schema-generator/execution/data-fetching-environment.md",sourceDirName:"schema-generator/execution",slug:"/schema-generator/execution/data-fetching-environment",permalink:"/graphql-kotlin/docs/8.x.x/schema-generator/execution/data-fetching-environment",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/docs/schema-generator/execution/data-fetching-environment.md",tags:[],version:"current",lastUpdatedBy:"mykevinjung",lastUpdatedAt:1721694712e3,frontMatter:{id:"data-fetching-environment",title:"Data Fetching Environment"},sidebar:"docs",previous:{title:"Exceptions and Partial Data",permalink:"/graphql-kotlin/docs/8.x.x/schema-generator/execution/exceptions"},next:{title:"Contextual Data",permalink:"/graphql-kotlin/docs/8.x.x/schema-generator/execution/contextual-data"}},s={},l=[];function d(e){const n={a:"a",code:"code",p:"p",pre:"pre",...(0,i.R)(),...e.components};return(0,a.jsxs)(a.Fragment,{children:[(0,a.jsxs)(n.p,{children:["Each resolver has access to a ",(0,a.jsx)(n.code,{children:"DataFetchingEnvironment"})," that provides additional information about the currently executed query including information about what data is requested\nas well as details about current execution state. For more details on the ",(0,a.jsx)(n.code,{children:"DataFetchingEnvironment"})," please refer to ",(0,a.jsx)(n.a,{href:"https://www.graphql-java.com/documentation/data-fetching/",children:"graphql-java documentation"})]}),"\n",(0,a.jsxs)(n.p,{children:["You can access this info by including the ",(0,a.jsx)(n.code,{children:"DataFetchingEnvironment"})," as one of the arguments to a Kotlin function. This argument will be automatically populated and injected\nduring the query execution but will not be included in the schema definition."]}),"\n",(0,a.jsx)(n.pre,{children:(0,a.jsx)(n.code,{className:"language-kotlin",children:'class Query {\n    fun printEnvironmentInfo(parentField: String): MyObject = MyObject()\n}\n\nclass MyObject {\n  fun printParentField(childField: String, environment: DataFetchingEnvironment): String {\n    val parentField = environment.executionStepInfo.parent.getArgument("parentField")\n    return "The parentField was $parentField and the childField was $childField"\n  }\n}\n'})}),"\n",(0,a.jsx)(n.p,{children:"This will produce the following schema"}),"\n",(0,a.jsx)(n.pre,{children:(0,a.jsx)(n.code,{className:"language-graphql",children:"type Query {\n  printEnvironmentInfo(parentField: String!): MyObject!\n}\n\ntype MyObject {\n  printParentField(childField: String!): String!\n}\n"})}),"\n",(0,a.jsxs)(n.p,{children:["Then the following query would return ",(0,a.jsx)(n.code,{children:'"The parentField was foo and the childField was bar"'})]}),"\n",(0,a.jsx)(n.pre,{children:(0,a.jsx)(n.code,{className:"language-graphql",children:'{\n  printEnvironmentInfo(parentField: "foo") {\n    printParentField(childField: "bar")\n  }\n}\n'})}),"\n",(0,a.jsxs)(n.p,{children:["You can also use this to retrieve arguments and query information from higher up the query chain. You can see a working\nexample in the ",(0,a.jsx)(n.code,{children:"graphql-kotlin-spring-example"})," module [",(0,a.jsx)(n.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/master/examples/server/spring-server/src/main/kotlin/com/expediagroup/graphql/examples/server/spring/query/EnvironmentQuery.kt",children:"link"}),"]."]}),"\n",(0,a.jsx)(n.pre,{children:(0,a.jsx)(n.code,{className:"language-kotlin",children:'class ProductQueryService : Query {\n\n  fun products(environment: DataFetchingEnvironment): Product {\n      environment.selectionSet.fields.forEach { println("field: ${it.name}") }\n\n    return Product(1, "Product title", 100)\n  }\n}\n\n'})}),"\n",(0,a.jsx)(n.pre,{children:(0,a.jsx)(n.code,{className:"language-graphql",children:"{\n  product {\n    id\n    title\n    price\n  }\n}\n"})}),"\n",(0,a.jsxs)(n.p,{children:["You can also use ",(0,a.jsx)(n.code,{children:"selectionSet"})," to access the selected fields of the current field. It can be useful to know which selections have been requested so the data fetcher can optimize the data access queries. For example, in an SQL-backed system, the data fetcher can access the database and use the field selection criteria to specifically retrieve only the columns that have been requested by the client.\nwhat selection has been asked for so the data fetcher can optimise the data access queries.\nFor example an SQL backed system may be able to use the field selection to only retrieve the columns that have been asked for."]})]})}function h(e={}){const{wrapper:n}={...(0,i.R)(),...e.components};return n?(0,a.jsx)(n,{...e,children:(0,a.jsx)(d,{...e})}):d(e)}},28453:(e,n,t)=>{t.d(n,{R:()=>o,x:()=>c});var a=t(96540);const i={},r=a.createContext(i);function o(e){const n=a.useContext(r);return a.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function c(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(i):e.components||i:o(e.components),a.createElement(r.Provider,{value:n},e.children)}}}]);