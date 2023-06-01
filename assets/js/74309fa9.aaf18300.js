"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[9137],{19650:(e,a,t)=>{t.r(a),t.d(a,{assets:()=>l,contentTitle:()=>d,default:()=>u,frontMatter:()=>s,metadata:()=>c,toc:()=>m});var n=t(87462),o=t(63366),i=(t(67294),t(3905)),r=(t(95657),["components"]),s={id:"advanced-features",title:"Advanced Features"},d=void 0,c={unversionedId:"schema-generator/customizing-schemas/advanced-features",id:"schema-generator/customizing-schemas/advanced-features",title:"Advanced Features",description:"Adding Custom Additional Types",source:"@site/docs/schema-generator/customizing-schemas/advanced-features.md",sourceDirName:"schema-generator/customizing-schemas",slug:"/schema-generator/customizing-schemas/advanced-features",permalink:"/graphql-kotlin/docs/7.x.x/schema-generator/customizing-schemas/advanced-features",draft:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/docs/schema-generator/customizing-schemas/advanced-features.md",tags:[],version:"current",lastUpdatedBy:"Samuel Vazquez",lastUpdatedAt:1685659104,formattedLastUpdatedAt:"Jun 1, 2023",frontMatter:{id:"advanced-features",title:"Advanced Features"},sidebar:"docs",previous:{title:"Restricting Input and Output Types",permalink:"/graphql-kotlin/docs/7.x.x/schema-generator/customizing-schemas/restricting-input-output"},next:{title:"Fetching Data",permalink:"/graphql-kotlin/docs/7.x.x/schema-generator/execution/fetching-data"}},l={},m=[{value:"Adding Custom Additional Types",id:"adding-custom-additional-types",level:2},{value:"<code>SchemaGenerator::generateSchema</code>",id:"schemageneratorgenerateschema",level:3},{value:"<code>SchemaGenerator::addAdditionalTypesWithAnnotation</code>",id:"schemageneratoraddadditionaltypeswithannotation",level:3}],p={toc:m},h="wrapper";function u(e){var a=e.components,t=(0,o.Z)(e,r);return(0,i.kt)(h,(0,n.Z)({},p,t,{components:a,mdxType:"MDXLayout"}),(0,i.kt)("h2",{id:"adding-custom-additional-types"},"Adding Custom Additional Types"),(0,i.kt)("p",null,"There are a couple ways you can add more types to the schema without having them be directly consumed by a type in your schema.\nThis may be required for ",(0,i.kt)("a",{parentName:"p",href:"/graphql-kotlin/docs/7.x.x/schema-generator/federation/apollo-federation"},"Apollo Federation"),", or maybe adding other interface implementations that are not picked up."),(0,i.kt)("h3",{id:"schemageneratorgenerateschema"},(0,i.kt)("inlineCode",{parentName:"h3"},"SchemaGenerator::generateSchema")),(0,i.kt)("p",null,"When generating a schema you can optionally specify additional types and input types to be included in the schema. This will\nallow you to link to those types from your custom ",(0,i.kt)("inlineCode",{parentName:"p"},"SchemaGeneratorHooks")," implementation using GraphQL reference instead of\nmanually creating the underlying GraphQL type."),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-kotlin"},'val myConfig = SchemaGeneratorConfig(supportedPackages = listOf("com.example"))\nval generator = SchemaGenerator(myConfig)\n\nval schema = generator.generateSchema(\n    queries = myQueries,\n    additionalTypes = setOf(MyCustomObject::class.createType()),\n    additionalInputTypes = setOf(MyCustomInputObject::class.createType())\n)\n')),(0,i.kt)("h3",{id:"schemageneratoraddadditionaltypeswithannotation"},(0,i.kt)("inlineCode",{parentName:"h3"},"SchemaGenerator::addAdditionalTypesWithAnnotation")),(0,i.kt)("p",null,"This method is protected so if you override the ",(0,i.kt)("inlineCode",{parentName:"p"},"SchemaGenerator")," used you can call this method to add types that have a specific annotation.\nYou can see how this is used in ",(0,i.kt)("inlineCode",{parentName:"p"},"graphql-kotlin-federation")," as ",(0,i.kt)("a",{parentName:"p",href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/master/generator/graphql-kotlin-federation/src/main/kotlin/com/expediagroup/graphql/generator/federation/FederatedSchemaGenerator.kt"},"an example"),"."))}u.isMDXComponent=!0}}]);