"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[7446],{60378:(e,n,t)=>{t.r(n),t.d(n,{assets:()=>l,contentTitle:()=>i,default:()=>u,frontMatter:()=>o,metadata:()=>s,toc:()=>c});var r=t(74848),a=t(28453);const o={id:"exceptions",title:"Exceptions and Partial Data",original_id:"exceptions"},i=void 0,s={id:"schema-generator/execution/exceptions",title:"Exceptions and Partial Data",description:"Returning GraphQL Errors",source:"@site/versioned_docs/version-3.x.x/schema-generator/execution/exceptions.md",sourceDirName:"schema-generator/execution",slug:"/schema-generator/execution/exceptions",permalink:"/graphql-kotlin/docs/3.x.x/schema-generator/execution/exceptions",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-3.x.x/schema-generator/execution/exceptions.md",tags:[],version:"3.x.x",lastUpdatedBy:"Tasuku Nakagawa",lastUpdatedAt:1714237421,formattedLastUpdatedAt:"Apr 27, 2024",frontMatter:{id:"exceptions",title:"Exceptions and Partial Data",original_id:"exceptions"},sidebar:"docs",previous:{title:"Async Models",permalink:"/graphql-kotlin/docs/3.x.x/schema-generator/execution/async-models"},next:{title:"Data Fetching Environment",permalink:"/graphql-kotlin/docs/3.x.x/schema-generator/execution/data-fetching-environment"}},l={},c=[{value:"Returning GraphQL Errors",id:"returning-graphql-errors",level:2},{value:"Returning Data and Errors",id:"returning-data-and-errors",level:2}];function d(e){const n={a:"a",code:"code",h2:"h2",p:"p",pre:"pre",...(0,a.R)(),...e.components};return(0,r.jsxs)(r.Fragment,{children:[(0,r.jsx)(n.h2,{id:"returning-graphql-errors",children:"Returning GraphQL Errors"}),"\n",(0,r.jsxs)(n.p,{children:["Exceptions thrown during execution of an operation will result in an empty data response and a GraphQLError that is added to a list of errors of the result.\nSee ",(0,r.jsx)(n.a,{href:"https://www.graphql-java.com/documentation/v14/execution/",children:"graphql-java documentation"})," for more details on how to customize your exception handling."]}),"\n",(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-kotlin",children:'\nfun getRandomNumberOrError(): Int {\n    val num = Random().nextInt(100)\n    return if (num <= 50) num else throw Exception("number is greater than 50")\n}\n\n'})}),"\n",(0,r.jsx)(n.h2,{id:"returning-data-and-errors",children:"Returning Data and Errors"}),"\n",(0,r.jsxs)(n.p,{children:["GraphQL allows you to return both data and errors in a single response, as long as the data returned still matches the schema. Depending on the criticality of the encountered error, instead of throwing an exception, you may want to return\ndefault data or use a nullable field, but still include more information in the ",(0,r.jsx)(n.code,{children:"errors"})," block. In Kotlin, functions return only a single value, which means that in order to return both data\nand errors you have to explicitly return them wrapped in a ",(0,r.jsx)(n.code,{children:"DataFetcherResult"})," object."]}),"\n",(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-kotlin",children:"\nclass DataAndErrorsQuery {\n  fun returnDataAndErrors(): DataFetcherResult<String?> {\n    val data: String? = getData()\n    val error = if (data == null) MyError() else null\n\n    return DataFetcherResult.newResult<String?>()\n      .data(data)\n      .error(error)\n      .build()\n  }\n}\n\n"})}),"\n",(0,r.jsxs)(n.p,{children:["An example of a query returning partial data is available in our ",(0,r.jsx)(n.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/3.x.x/examples/spring/src/main/kotlin/com/expediagroup/graphql/examples/query/DataAndErrorsQuery.kt",children:"spring example app"}),"."]})]})}function u(e={}){const{wrapper:n}={...(0,a.R)(),...e.components};return n?(0,r.jsx)(n,{...e,children:(0,r.jsx)(d,{...e})}):d(e)}},28453:(e,n,t)=>{t.d(n,{R:()=>i,x:()=>s});var r=t(96540);const a={},o=r.createContext(a);function i(e){const n=r.useContext(o);return r.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function s(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(a):e.components||a:i(e.components),r.createElement(o.Provider,{value:n},e.children)}}}]);