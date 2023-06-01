"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[5773],{90055:(e,n,t)=>{t.r(n),t.d(n,{assets:()=>c,contentTitle:()=>r,default:()=>h,frontMatter:()=>l,metadata:()=>d,toc:()=>m});var i=t(87462),a=t(63366),s=(t(67294),t(3905)),o=(t(95657),["components"]),l={id:"excluding-fields",title:"Excluding Fields",original_id:"excluding-fields"},r=void 0,d={unversionedId:"schema-generator/customizing-schemas/excluding-fields",id:"version-3.x.x/schema-generator/customizing-schemas/excluding-fields",title:"Excluding Fields",description:"There are two ways to ensure the GraphQL schema generation omits fields when using Kotlin reflection:",source:"@site/versioned_docs/version-3.x.x/schema-generator/customizing-schemas/excluding-fields.md",sourceDirName:"schema-generator/customizing-schemas",slug:"/schema-generator/customizing-schemas/excluding-fields",permalink:"/graphql-kotlin/docs/3.x.x/schema-generator/customizing-schemas/excluding-fields",draft:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-3.x.x/schema-generator/customizing-schemas/excluding-fields.md",tags:[],version:"3.x.x",lastUpdatedBy:"Samuel Vazquez",lastUpdatedAt:1685659104,formattedLastUpdatedAt:"Jun 1, 2023",frontMatter:{id:"excluding-fields",title:"Excluding Fields",original_id:"excluding-fields"},sidebar:"version-3.x.x/docs",previous:{title:"Documenting Schema",permalink:"/graphql-kotlin/docs/3.x.x/schema-generator/customizing-schemas/documenting-fields"},next:{title:"Renaming Fields",permalink:"/graphql-kotlin/docs/3.x.x/schema-generator/customizing-schemas/renaming-fields"}},c={},m=[],u={toc:m},g="wrapper";function h(e){var n=e.components,t=(0,a.Z)(e,o);return(0,s.kt)(g,(0,i.Z)({},u,t,{components:n,mdxType:"MDXLayout"}),(0,s.kt)("p",null,"There are two ways to ensure the GraphQL schema generation omits fields when using Kotlin reflection:"),(0,s.kt)("ul",null,(0,s.kt)("li",{parentName:"ul"},"The first is by marking the field as non-",(0,s.kt)("inlineCode",{parentName:"li"},"public")," scope (",(0,s.kt)("inlineCode",{parentName:"li"},"private"),", ",(0,s.kt)("inlineCode",{parentName:"li"},"protected"),", ",(0,s.kt)("inlineCode",{parentName:"li"},"internal"),")"),(0,s.kt)("li",{parentName:"ul"},"The second method is by annotating the field with ",(0,s.kt)("inlineCode",{parentName:"li"},"@GraphQLIgnore"),".")),(0,s.kt)("pre",null,(0,s.kt)("code",{parentName:"pre",className:"language-kotlin"},'\nclass SimpleQuery {\n  @GraphQLIgnore\n  fun notPartOfSchema() = "ignore me!"\n\n  private fun privateFunctionsAreNotVisible() = "ignored private function"\n\n  fun doSomething(value: Int): Boolean = true\n}\n\n')),(0,s.kt)("p",null,"The above query would produce the following GraphQL schema:"),(0,s.kt)("pre",null,(0,s.kt)("code",{parentName:"pre",className:"language-graphql"},"\ntype Query {\n  doSomething(value: Int!): Boolean!\n}\n\n")),(0,s.kt)("p",null,"Note that the public method ",(0,s.kt)("inlineCode",{parentName:"p"},"notPartOfSchema")," is not included in the schema."))}h.isMDXComponent=!0}}]);