"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[1995],{3905:(e,r,t)=>{t.d(r,{Zo:()=>p,kt:()=>g});var n=t(67294);function a(e,r,t){return r in e?Object.defineProperty(e,r,{value:t,enumerable:!0,configurable:!0,writable:!0}):e[r]=t,e}function o(e,r){var t=Object.keys(e);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);r&&(n=n.filter((function(r){return Object.getOwnPropertyDescriptor(e,r).enumerable}))),t.push.apply(t,n)}return t}function i(e){for(var r=1;r<arguments.length;r++){var t=null!=arguments[r]?arguments[r]:{};r%2?o(Object(t),!0).forEach((function(r){a(e,r,t[r])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(t)):o(Object(t)).forEach((function(r){Object.defineProperty(e,r,Object.getOwnPropertyDescriptor(t,r))}))}return e}function l(e,r){if(null==e)return{};var t,n,a=function(e,r){if(null==e)return{};var t,n,a={},o=Object.keys(e);for(n=0;n<o.length;n++)t=o[n],r.indexOf(t)>=0||(a[t]=e[t]);return a}(e,r);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);for(n=0;n<o.length;n++)t=o[n],r.indexOf(t)>=0||Object.prototype.propertyIsEnumerable.call(e,t)&&(a[t]=e[t])}return a}var s=n.createContext({}),c=function(e){var r=n.useContext(s),t=r;return e&&(t="function"==typeof e?e(r):i(i({},r),e)),t},p=function(e){var r=c(e.components);return n.createElement(s.Provider,{value:r},e.children)},u="mdxType",d={inlineCode:"code",wrapper:function(e){var r=e.children;return n.createElement(n.Fragment,{},r)}},m=n.forwardRef((function(e,r){var t=e.components,a=e.mdxType,o=e.originalType,s=e.parentName,p=l(e,["components","mdxType","originalType","parentName"]),u=c(t),m=a,g=u["".concat(s,".").concat(m)]||u[m]||d[m]||o;return t?n.createElement(g,i(i({ref:r},p),{},{components:t})):n.createElement(g,i({ref:r},p))}));function g(e,r){var t=arguments,a=r&&r.mdxType;if("string"==typeof e||a){var o=t.length,i=new Array(o);i[0]=m;var l={};for(var s in r)hasOwnProperty.call(r,s)&&(l[s]=r[s]);l.originalType=e,l[u]="string"==typeof e?e:a,i[1]=l;for(var c=2;c<o;c++)i[c]=t[c];return n.createElement.apply(null,i)}return n.createElement.apply(null,t)}m.displayName="MDXCreateElement"},73243:(e,r,t)=>{t.r(r),t.d(r,{assets:()=>p,contentTitle:()=>s,default:()=>g,frontMatter:()=>l,metadata:()=>c,toc:()=>u});var n=t(87462),a=t(63366),o=(t(67294),t(3905)),i=["components"],l={id:"exceptions",title:"Exceptions and Partial Data"},s=void 0,c={unversionedId:"schema-generator/execution/exceptions",id:"schema-generator/execution/exceptions",title:"Exceptions and Partial Data",description:"Returning GraphQL Errors",source:"@site/docs/schema-generator/execution/exceptions.md",sourceDirName:"schema-generator/execution",slug:"/schema-generator/execution/exceptions",permalink:"/graphql-kotlin/docs/schema-generator/execution/exceptions",draft:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/docs/schema-generator/execution/exceptions.md",tags:[],version:"current",lastUpdatedBy:"Simon B\xf6lscher",lastUpdatedAt:1701733027,formattedLastUpdatedAt:"Dec 4, 2023",frontMatter:{id:"exceptions",title:"Exceptions and Partial Data"},sidebar:"docs",previous:{title:"Async Models",permalink:"/graphql-kotlin/docs/schema-generator/execution/async-models"},next:{title:"Data Fetching Environment",permalink:"/graphql-kotlin/docs/schema-generator/execution/data-fetching-environment"}},p={},u=[{value:"Returning GraphQL Errors",id:"returning-graphql-errors",level:2},{value:"Returning Data and Errors",id:"returning-data-and-errors",level:2}],d={toc:u},m="wrapper";function g(e){var r=e.components,t=(0,a.Z)(e,i);return(0,o.kt)(m,(0,n.Z)({},d,t,{components:r,mdxType:"MDXLayout"}),(0,o.kt)("h2",{id:"returning-graphql-errors"},"Returning GraphQL Errors"),(0,o.kt)("p",null,"Exceptions thrown during execution of an operation will result in an empty data response and a GraphQLError that is added to a list of errors of the result.\nSee ",(0,o.kt)("a",{parentName:"p",href:"https://www.graphql-java.com/documentation/v14/execution/"},"graphql-java documentation")," for more details on how to customize your exception handling."),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-kotlin"},'fun getRandomNumberOrError(): Int {\n    val num = Random().nextInt(100)\n    return if (num <= 50) num else throw Exception("number is greater than 50")\n}\n')),(0,o.kt)("h2",{id:"returning-data-and-errors"},"Returning Data and Errors"),(0,o.kt)("p",null,"GraphQL allows you to return both data and errors in a single response, as long as the data returned still matches the schema. Depending on the criticality of the encountered error, instead of throwing an exception, you may want to return\ndefault data or use a nullable field, but still include more information in the ",(0,o.kt)("inlineCode",{parentName:"p"},"errors")," block. In Kotlin, functions return only a single value, which means that in order to return both data\nand errors you have to explicitly return them wrapped in a ",(0,o.kt)("inlineCode",{parentName:"p"},"DataFetcherResult")," object."),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-kotlin"},"class DataAndErrorsQuery {\n  fun returnDataAndErrors(): DataFetcherResult<String?> {\n    val data: String? = getData()\n    val error = if (data == null) MyError() else null\n\n    return DataFetcherResult.newResult<String?>()\n      .data(data)\n      .error(error)\n      .build()\n  }\n}\n")),(0,o.kt)("p",null,"An example of a query returning partial data is available in our ",(0,o.kt)("a",{parentName:"p",href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/master/examples/server/spring-server/src/main/kotlin/com/expediagroup/graphql/examples/server/spring/query/DataAndErrorsQuery.kt"},"spring example app"),"."))}g.isMDXComponent=!0}}]);