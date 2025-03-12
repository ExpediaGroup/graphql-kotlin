"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[2053],{7328:(e,n,t)=>{t.r(n),t.d(n,{assets:()=>s,contentTitle:()=>o,default:()=>h,frontMatter:()=>r,metadata:()=>c,toc:()=>l});var i=t(74848),a=t(28453);const r={id:"data-fetching-environment",title:"Data Fetching Environment"},o=void 0,c={id:"schema-generator/execution/data-fetching-environment",title:"Data Fetching Environment",description:"Each resolver has access to a DataFetchingEnvironment that provides additional information about the currently executed query including information about what data is requested",source:"@site/versioned_docs/version-7.x.x/schema-generator/execution/data-fetching-environment.md",sourceDirName:"schema-generator/execution",slug:"/schema-generator/execution/data-fetching-environment",permalink:"/graphql-kotlin/docs/7.x.x/schema-generator/execution/data-fetching-environment",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-7.x.x/schema-generator/execution/data-fetching-environment.md",tags:[],version:"7.x.x",lastUpdatedBy:"Daniel",lastUpdatedAt:1741819577e3,frontMatter:{id:"data-fetching-environment",title:"Data Fetching Environment"},sidebar:"docs",previous:{title:"Exceptions and Partial Data",permalink:"/graphql-kotlin/docs/7.x.x/schema-generator/execution/exceptions"},next:{title:"Contextual Data",permalink:"/graphql-kotlin/docs/7.x.x/schema-generator/execution/contextual-data"}},s={},l=[];function d(e){const n={a:"a",code:"code",p:"p",pre:"pre",...(0,a.R)(),...e.components};return(0,i.jsxs)(i.Fragment,{children:[(0,i.jsxs)(n.p,{children:["Each resolver has access to a ",(0,i.jsx)(n.code,{children:"DataFetchingEnvironment"})," that provides additional information about the currently executed query including information about what data is requested\nas well as details about current execution state. For more details on the ",(0,i.jsx)(n.code,{children:"DataFetchingEnvironment"})," please refer to ",(0,i.jsx)(n.a,{href:"https://www.graphql-java.com/documentation/v14/data-fetching/",children:"graphql-java documentation"})]}),"\n",(0,i.jsxs)(n.p,{children:["You can access this info by including the ",(0,i.jsx)(n.code,{children:"DataFetchingEnvironment"})," as one of the arguments to a Kotlin function. This argument will be automatically populated and injected\nduring the query execution but will not be included in the schema definition."]}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-kotlin",children:'class Query {\n    fun printEnvironmentInfo(parentField: String): MyObject = MyObject()\n}\n\nclass MyObject {\n  fun printParentField(childField: String, environment: DataFetchingEnvironment): String {\n    val parentField = environment.executionStepInfo.parent.getArgument("parentField")\n    return "The parentField was $parentField and the childField was $childField"\n  }\n}\n'})}),"\n",(0,i.jsx)(n.p,{children:"This will produce the following schema"}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-graphql",children:"type Query {\n  printEnvironmentInfo(parentField: String!): MyObject!\n}\n\ntype MyObject {\n  printParentField(childField: String!): String!\n}\n"})}),"\n",(0,i.jsxs)(n.p,{children:["Then the following query would return ",(0,i.jsx)(n.code,{children:'"The parentField was foo and the childField was bar"'})]}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-graphql",children:'{\n  printEnvironmentInfo(parentField: "foo") {\n    printParentField(childField: "bar")\n  }\n}\n'})}),"\n",(0,i.jsxs)(n.p,{children:["You can also use this to retrieve arguments and query information from higher up the query chain. You can see a working\nexample in the ",(0,i.jsx)(n.code,{children:"graphql-kotlin-spring-example"})," module [",(0,i.jsx)(n.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/master/examples/spring/src/main/kotlin/com/expediagroup/graphql/examples/query/EnvironmentQuery.kt",children:"link"}),"]."]})]})}function h(e={}){const{wrapper:n}={...(0,a.R)(),...e.components};return n?(0,i.jsx)(n,{...e,children:(0,i.jsx)(d,{...e})}):d(e)}},28453:(e,n,t)=>{t.d(n,{R:()=>o,x:()=>c});var i=t(96540);const a={},r=i.createContext(a);function o(e){const n=i.useContext(r);return i.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function c(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(a):e.components||a:o(e.components),i.createElement(r.Provider,{value:n},e.children)}}}]);