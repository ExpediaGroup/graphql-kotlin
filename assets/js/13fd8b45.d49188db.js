"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[4606],{46138:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>p,contentTitle:()=>l,default:()=>m,frontMatter:()=>s,metadata:()=>u,toc:()=>d});var r=n(87462),a=n(63366),o=(n(67294),n(3905)),i=(n(95657),["components"]),s={id:"exceptions",title:"Exceptions and Partial Data",original_id:"exceptions"},l=void 0,u={unversionedId:"schema-generator/execution/exceptions",id:"version-3.x.x/schema-generator/execution/exceptions",title:"Exceptions and Partial Data",description:"Returning GraphQL Errors",source:"@site/versioned_docs/version-3.x.x/schema-generator/execution/exceptions.md",sourceDirName:"schema-generator/execution",slug:"/schema-generator/execution/exceptions",permalink:"/graphql-kotlin/docs/3.x.x/schema-generator/execution/exceptions",draft:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-3.x.x/schema-generator/execution/exceptions.md",tags:[],version:"3.x.x",lastUpdatedBy:"Samuel Vazquez",lastUpdatedAt:1685659104,formattedLastUpdatedAt:"Jun 1, 2023",frontMatter:{id:"exceptions",title:"Exceptions and Partial Data",original_id:"exceptions"},sidebar:"version-3.x.x/docs",previous:{title:"Async Models",permalink:"/graphql-kotlin/docs/3.x.x/schema-generator/execution/async-models"},next:{title:"Data Fetching Environment",permalink:"/graphql-kotlin/docs/3.x.x/schema-generator/execution/data-fetching-environment"}},p={},d=[{value:"Returning GraphQL Errors",id:"returning-graphql-errors",level:2},{value:"Returning Data and Errors",id:"returning-data-and-errors",level:2}],c={toc:d},h="wrapper";function m(e){var t=e.components,n=(0,a.Z)(e,i);return(0,o.kt)(h,(0,r.Z)({},c,n,{components:t,mdxType:"MDXLayout"}),(0,o.kt)("h2",{id:"returning-graphql-errors"},"Returning GraphQL Errors"),(0,o.kt)("p",null,"Exceptions thrown during execution of an operation will result in an empty data response and a GraphQLError that is added to a list of errors of the result.\nSee ",(0,o.kt)("a",{parentName:"p",href:"https://www.graphql-java.com/documentation/v14/execution/"},"graphql-java documentation")," for more details on how to customize your exception handling."),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-kotlin"},'\nfun getRandomNumberOrError(): Int {\n    val num = Random().nextInt(100)\n    return if (num <= 50) num else throw Exception("number is greater than 50")\n}\n\n')),(0,o.kt)("h2",{id:"returning-data-and-errors"},"Returning Data and Errors"),(0,o.kt)("p",null,"GraphQL allows you to return both data and errors in a single response, as long as the data returned still matches the schema. Depending on the criticality of the encountered error, instead of throwing an exception, you may want to return\ndefault data or use a nullable field, but still include more information in the ",(0,o.kt)("inlineCode",{parentName:"p"},"errors")," block. In Kotlin, functions return only a single value, which means that in order to return both data\nand errors you have to explicitly return them wrapped in a ",(0,o.kt)("inlineCode",{parentName:"p"},"DataFetcherResult")," object."),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-kotlin"},"\nclass DataAndErrorsQuery {\n  fun returnDataAndErrors(): DataFetcherResult<String?> {\n    val data: String? = getData()\n    val error = if (data == null) MyError() else null\n\n    return DataFetcherResult.newResult<String?>()\n      .data(data)\n      .error(error)\n      .build()\n  }\n}\n\n")),(0,o.kt)("p",null,"An example of a query returning partial data is available in our ",(0,o.kt)("a",{parentName:"p",href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/3.x.x/examples/spring/src/main/kotlin/com/expediagroup/graphql/examples/query/DataAndErrorsQuery.kt"},"spring example app"),"."))}m.isMDXComponent=!0}}]);