"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[456],{38726:(e,t,a)=>{a.r(t),a.d(t,{assets:()=>d,contentTitle:()=>s,default:()=>h,frontMatter:()=>r,metadata:()=>i,toc:()=>c});var n=a(74848),o=a(28453);const r={id:"advanced-features",title:"Advanced Features"},s=void 0,i={id:"schema-generator/customizing-schemas/advanced-features",title:"Advanced Features",description:"Adding Custom Additional Types",source:"@site/versioned_docs/version-6.x.x/schema-generator/customizing-schemas/advanced-features.md",sourceDirName:"schema-generator/customizing-schemas",slug:"/schema-generator/customizing-schemas/advanced-features",permalink:"/graphql-kotlin/docs/6.x.x/schema-generator/customizing-schemas/advanced-features",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-6.x.x/schema-generator/customizing-schemas/advanced-features.md",tags:[],version:"6.x.x",lastUpdatedBy:"Dariusz Kuc",lastUpdatedAt:1708623265,formattedLastUpdatedAt:"Feb 22, 2024",frontMatter:{id:"advanced-features",title:"Advanced Features"},sidebar:"docs",previous:{title:"Restricting Input and Output Types",permalink:"/graphql-kotlin/docs/6.x.x/schema-generator/customizing-schemas/restricting-input-output"},next:{title:"Fetching Data",permalink:"/graphql-kotlin/docs/6.x.x/schema-generator/execution/fetching-data"}},d={},c=[{value:"Adding Custom Additional Types",id:"adding-custom-additional-types",level:2},{value:"<code>SchemaGenerator::generateSchema</code>",id:"schemageneratorgenerateschema",level:3},{value:"<code>SchemaGenerator::addAdditionalTypesWithAnnotation</code>",id:"schemageneratoraddadditionaltypeswithannotation",level:3}];function l(e){const t={a:"a",code:"code",h2:"h2",h3:"h3",p:"p",pre:"pre",...(0,o.R)(),...e.components};return(0,n.jsxs)(n.Fragment,{children:[(0,n.jsx)(t.h2,{id:"adding-custom-additional-types",children:"Adding Custom Additional Types"}),"\n",(0,n.jsxs)(t.p,{children:["There are a couple ways you can add more types to the schema without having them be directly consumed by a type in your schema.\nThis may be required for ",(0,n.jsx)(t.a,{href:"/graphql-kotlin/docs/6.x.x/schema-generator/federation/apollo-federation",children:"Apollo Federation"}),", or maybe adding other interface implementations that are not picked up."]}),"\n",(0,n.jsx)(t.h3,{id:"schemageneratorgenerateschema",children:(0,n.jsx)(t.code,{children:"SchemaGenerator::generateSchema"})}),"\n",(0,n.jsxs)(t.p,{children:["When generating a schema you can optionally specify additional types and input types to be included in the schema. This will\nallow you to link to those types from your custom ",(0,n.jsx)(t.code,{children:"SchemaGeneratorHooks"})," implementation using GraphQL reference instead of\nmanually creating the underlying GraphQL type."]}),"\n",(0,n.jsx)(t.pre,{children:(0,n.jsx)(t.code,{className:"language-kotlin",children:'val myConfig = SchemaGeneratorConfig(supportedPackages = listOf("com.example"))\nval generator = SchemaGenerator(myConfig)\n\nval schema = generator.generateSchema(\n    queries = myQueries,\n    additionalTypes = setOf(MyCustomObject::class.createType()),\n    additionalInputTypes = setOf(MyCustomInputObject::class.createType())\n)\n'})}),"\n",(0,n.jsx)(t.h3,{id:"schemageneratoraddadditionaltypeswithannotation",children:(0,n.jsx)(t.code,{children:"SchemaGenerator::addAdditionalTypesWithAnnotation"})}),"\n",(0,n.jsxs)(t.p,{children:["This method is protected so if you override the ",(0,n.jsx)(t.code,{children:"SchemaGenerator"})," used you can call this method to add types that have a specific annotation.\nYou can see how this is used in ",(0,n.jsx)(t.code,{children:"graphql-kotlin-federation"})," as ",(0,n.jsx)(t.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/master/generator/graphql-kotlin-federation/src/main/kotlin/com/expediagroup/graphql/generator/federation/FederatedSchemaGenerator.kt",children:"an example"}),"."]})]})}function h(e={}){const{wrapper:t}={...(0,o.R)(),...e.components};return t?(0,n.jsx)(t,{...e,children:(0,n.jsx)(l,{...e})}):l(e)}},28453:(e,t,a)=>{a.d(t,{R:()=>s,x:()=>i});var n=a(96540);const o={},r=n.createContext(o);function s(e){const t=n.useContext(r);return n.useMemo((function(){return"function"==typeof e?e(t):{...t,...e}}),[t,e])}function i(e){let t;return t=e.disableParentContext?"function"==typeof e.components?e.components(o):e.components||o:s(e.components),n.createElement(r.Provider,{value:t},e.children)}}}]);