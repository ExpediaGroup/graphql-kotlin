"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[1353],{1878:(e,t,a)=>{a.r(t),a.d(t,{assets:()=>d,contentTitle:()=>p,default:()=>u,frontMatter:()=>l,metadata:()=>i,toc:()=>h});var r=a(7462),n=a(3366),s=(a(7294),a(3905)),o=(a(8561),["components"]),l={id:"graphql-request-handler",title:"GraphQLRequestHandler"},p=void 0,i={unversionedId:"server/graphql-request-handler",id:"server/graphql-request-handler",title:"GraphQLRequestHandler",description:"The GraphQLRequestHandler is an open and extendable class that contains the basic logic to get a GraphQLResponse.",source:"@site/docs/server/graphql-request-handler.md",sourceDirName:"server",slug:"/server/graphql-request-handler",permalink:"/graphql-kotlin/docs/7.x.x/server/graphql-request-handler",draft:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/docs/server/graphql-request-handler.md",tags:[],version:"current",lastUpdatedBy:"Samuel Vazquez",lastUpdatedAt:1677182897,formattedLastUpdatedAt:"Feb 23, 2023",frontMatter:{id:"graphql-request-handler",title:"GraphQLRequestHandler"},sidebar:"docs",previous:{title:"GraphQLContextFactory",permalink:"/graphql-kotlin/docs/7.x.x/server/graphql-context-factory"},next:{title:"Subscriptions",permalink:"/graphql-kotlin/docs/7.x.x/server/server-subscriptions"}},d={},h=[],c={toc:h};function u(e){var t=e.components,a=(0,n.Z)(e,o);return(0,s.kt)("wrapper",(0,r.Z)({},c,a,{components:t,mdxType:"MDXLayout"}),(0,s.kt)("p",null,"The ",(0,s.kt)("inlineCode",{parentName:"p"},"GraphQLRequestHandler")," is an open and extendable class that contains the basic logic to get a ",(0,s.kt)("inlineCode",{parentName:"p"},"GraphQLResponse"),"."),(0,s.kt)("p",null,"It requires a ",(0,s.kt)("inlineCode",{parentName:"p"},"GraphQLSchema")," and a ",(0,s.kt)("a",{parentName:"p",href:"/graphql-kotlin/docs/7.x.x/server/data-loader/"},"KotlinDataLoaderRegistryFactory")," in the constructor.\nFor each request, it accepts a ",(0,s.kt)("inlineCode",{parentName:"p"},"GraphQLRequest")," and an optional ",(0,s.kt)("a",{parentName:"p",href:"/graphql-kotlin/docs/7.x.x/server/graphql-context-factory"},"GraphQLContext"),",\nand calls the ",(0,s.kt)("inlineCode",{parentName:"p"},"KotlinDataLoaderRegistryFactory")," to generate a new ",(0,s.kt)("inlineCode",{parentName:"p"},"KotlinDataLoaderRegistry"),". Then all of these objects are sent to the schema for\nexecution and the result is mapped to a ",(0,s.kt)("inlineCode",{parentName:"p"},"GraphQLResponse"),"."),(0,s.kt)("p",null,"There shouldn't be much need to change this class but if you wanted to add custom logic\nor logging it is possible to override it or just create your own."))}u.isMDXComponent=!0}}]);