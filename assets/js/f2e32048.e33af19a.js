"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[8122],{4498:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>u,contentTitle:()=>s,default:()=>d,frontMatter:()=>l,metadata:()=>c,toc:()=>h});var a=n(7462),o=n(3366),r=(n(7294),n(3905)),i=(n(8561),["components"]),l={id:"contextual-data",title:"Contextual Data"},s=void 0,c={unversionedId:"schema-generator/execution/contextual-data",id:"version-4.x.x/schema-generator/execution/contextual-data",title:"Contextual Data",description:'All GraphQL servers have a concept of a "context". A GraphQL context contains metadata that is useful to the GraphQL',source:"@site/versioned_docs/version-4.x.x/schema-generator/execution/contextual-data.md",sourceDirName:"schema-generator/execution",slug:"/schema-generator/execution/contextual-data",permalink:"/graphql-kotlin/docs/4.x.x/schema-generator/execution/contextual-data",draft:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-4.x.x/schema-generator/execution/contextual-data.md",tags:[],version:"4.x.x",lastUpdatedBy:"Samuel Vazquez",lastUpdatedAt:1677182897,formattedLastUpdatedAt:"Feb 23, 2023",frontMatter:{id:"contextual-data",title:"Contextual Data"},sidebar:"version-4.x.x/docs",previous:{title:"Data Fetching Environment",permalink:"/graphql-kotlin/docs/4.x.x/schema-generator/execution/data-fetching-environment"},next:{title:"Optional Undefined Arguments",permalink:"/graphql-kotlin/docs/4.x.x/schema-generator/execution/optional-undefined-arguments"}},u={},h=[{value:"GraphQLContext Interface",id:"graphqlcontext-interface",level:2},{value:"Handling Context Errors",id:"handling-context-errors",level:2},{value:"Injection Customization",id:"injection-customization",level:2}],p={toc:h};function d(e){var t=e.components,n=(0,o.Z)(e,i);return(0,r.kt)("wrapper",(0,a.Z)({},p,n,{components:t,mdxType:"MDXLayout"}),(0,r.kt)("p",null,'All GraphQL servers have a concept of a "context". A GraphQL context contains metadata that is useful to the GraphQL\nserver, but shouldn\'t necessarily be part of the GraphQL schema. A prime example of something that is appropriate\nfor the GraphQL context would be trace headers for an OpenTracing system such as\n',(0,r.kt)("a",{parentName:"p",href:"https://expediadotcom.github.io/haystack"},"Haystack"),". The GraphQL query does not need the information to perform\nits function, but the server needs the information to ensure observability."),(0,r.kt)("p",null,"The contents of the GraphQL context vary across applications and it is up to the GraphQL server developers to decide\nwhat it should contain. ",(0,r.kt)("inlineCode",{parentName:"p"},"graphql-kotlin-server")," provides a simple mechanism to\nbuild a context per operation with the ",(0,r.kt)("a",{parentName:"p",href:"/graphql-kotlin/docs/4.x.x/server/graphql-context-factory"},"GraphQLContextFactory"),".\nIf a custom factory is defined, it will then be used to populate GraphQL context based on the incoming request and make it available during execution."),(0,r.kt)("h2",{id:"graphqlcontext-interface"},"GraphQLContext Interface"),(0,r.kt)("p",null,"The easiest way to specify a context class is to use the ",(0,r.kt)("inlineCode",{parentName:"p"},"GraphQLContext")," marker interface. This interface does not require any implementations,\nit is just used to inform the schema generator that this is the class that should be used as the context for every request."),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},"class MyGraphQLContext(val customValue: String) : GraphQLContext\n")),(0,r.kt)("p",null,"Then, you can use the class as an argument and it will be automatically injected during execution time."),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},'class ContextualQuery : Query {\n    fun contextualQuery(\n        context: MyGraphQLContext,\n        value: Int\n    ): String = "The custom value was ${context.customValue} and the value was $value"\n}\n')),(0,r.kt)("p",null,"The above query would produce the following GraphQL schema:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-graphql"},"type Query {\n  contextualQuery(value: Int!): String!\n}\n")),(0,r.kt)("p",null,"Note that the argument that implements ",(0,r.kt)("inlineCode",{parentName:"p"},"GraphQLContext")," is not reflected in the GraphQL schema."),(0,r.kt)("h2",{id:"handling-context-errors"},"Handling Context Errors"),(0,r.kt)("p",null,"The ",(0,r.kt)("a",{parentName:"p",href:"/graphql-kotlin/docs/4.x.x/server/graphql-context-factory"},"GraphQLContextFactory")," may return ",(0,r.kt)("inlineCode",{parentName:"p"},"null"),". If your factory implementation never returns ",(0,r.kt)("inlineCode",{parentName:"p"},"null"),", then there is no need to change your schema.\nIf the factory could return ",(0,r.kt)("inlineCode",{parentName:"p"},"null"),", then the context arugments in your schema should be nullable so a runtime exception is not thrown."),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},'class ContextualQuery : Query {\n    fun contextualQuery(context: MyGraphQLContext?, value: Int): String {\n        if (context != null) {\n            return "The custom value was ${context.customValue} and the value was $value"\n        }\n\n        return "The context was null and the value was $value"\n    }\n}\n')),(0,r.kt)("h2",{id:"injection-customization"},"Injection Customization"),(0,r.kt)("p",null,"The context is injected into the execution through the ",(0,r.kt)("inlineCode",{parentName:"p"},"FunctionDataFetcher")," class.\nIf you want to customize the logic on how the context is determined, that is possible to override.\nSee more details on the ",(0,r.kt)("a",{parentName:"p",href:"/graphql-kotlin/docs/4.x.x/schema-generator/execution/fetching-data"},"Fetching Data documentation")))}d.isMDXComponent=!0}}]);