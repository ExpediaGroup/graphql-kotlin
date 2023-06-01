"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[4960],{57645:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>c,contentTitle:()=>l,default:()=>h,frontMatter:()=>s,metadata:()=>p,toc:()=>d});var i=n(87462),a=n(63366),o=(n(67294),n(3905)),r=(n(95657),["components"]),s={id:"introspection",title:"Introspection"},l=void 0,p={unversionedId:"schema-generator/execution/introspection",id:"version-5.x.x/schema-generator/execution/introspection",title:"Introspection",description:"By default, GraphQL servers expose a built-in system, called introspection, that exposes details about the underlying schema.",source:"@site/versioned_docs/version-5.x.x/schema-generator/execution/introspection.md",sourceDirName:"schema-generator/execution",slug:"/schema-generator/execution/introspection",permalink:"/graphql-kotlin/docs/5.x.x/schema-generator/execution/introspection",draft:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-5.x.x/schema-generator/execution/introspection.md",tags:[],version:"5.x.x",lastUpdatedBy:"Samuel Vazquez",lastUpdatedAt:1685659104,formattedLastUpdatedAt:"Jun 1, 2023",frontMatter:{id:"introspection",title:"Introspection"},sidebar:"version-5.x.x/docs",previous:{title:"Subscriptions",permalink:"/graphql-kotlin/docs/5.x.x/schema-generator/execution/subscriptions"},next:{title:"Apollo Federation",permalink:"/graphql-kotlin/docs/5.x.x/schema-generator/federation/apollo-federation"}},c={},d=[{value:"Introspection types",id:"introspection-types",level:2},{value:"Disabling Introspection",id:"disabling-introspection",level:2}],m={toc:d},u="wrapper";function h(e){var t=e.components,n=(0,a.Z)(e,r);return(0,o.kt)(u,(0,i.Z)({},m,n,{components:t,mdxType:"MDXLayout"}),(0,o.kt)("p",null,"By default, GraphQL servers expose a built-in system, called ",(0,o.kt)("strong",{parentName:"p"},"introspection"),", that exposes details about the underlying schema.\nClients can use introspection to obtain information about all the supported queries as well as all the types exposed in the schema."),(0,o.kt)("h2",{id:"introspection-types"},"Introspection types"),(0,o.kt)("ul",null,(0,o.kt)("li",{parentName:"ul"},(0,o.kt)("em",{parentName:"li"},"_","_","schema")," - root level query field that provides information about all entry points (e.g. ",(0,o.kt)("inlineCode",{parentName:"li"},"queryType"),"), all types exposed\nby the schema (including built-in scalars and introspection types) as well as all directives supported by the system"),(0,o.kt)("li",{parentName:"ul"},(0,o.kt)("em",{parentName:"li"},"_","_","type(name: String!)")," - root level query field that provides information about the requested type (if it exists)"),(0,o.kt)("li",{parentName:"ul"},(0,o.kt)("strong",{parentName:"li"},(0,o.kt)("em",{parentName:"strong"},"typename"))," - field that can be added to ",(0,o.kt)("em",{parentName:"li"},"ANY")," selection and will return the name of the enclosing type,\nis often used in polymorphic queries in order to easily determine underlying implementation type"),(0,o.kt)("li",{parentName:"ul"},(0,o.kt)("strong",{parentName:"li"},(0,o.kt)("em",{parentName:"strong"},"Directive, DirectiveLocation, EnumValue, Field, InputValue, Schema, Type, TypeKind"))," - built-in\nintrospection types that are used to describe the schema.")),(0,o.kt)("p",null,"For example, the query below will return a root Query object name as well as names of all types and all directives."),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-graphql"},"query {\n  __schema {\n    queryType {\n      name\n    }\n    types {\n      name\n    }\n    directives {\n      name\n    }\n  }\n}\n")),(0,o.kt)("p",null,"Additional information on introspection can be found on ",(0,o.kt)("a",{parentName:"p",href:"https://graphql.org/learn/introspection/"},"GraphQL.org"),"."),(0,o.kt)("h2",{id:"disabling-introspection"},"Disabling Introspection"),(0,o.kt)("p",null,"Introspection system can be disabled by specifying ",(0,o.kt)("inlineCode",{parentName:"p"},"introspectionEnabled=false")," configuration option on an instance of\n",(0,o.kt)("inlineCode",{parentName:"p"},"SchemaGeneratorConfig")," that will be used by the ",(0,o.kt)("inlineCode",{parentName:"p"},"SchemaGenerator")," to generate the GraphQL schema."),(0,o.kt)("p",null,"Many GraphQL tools (e.g. ",(0,o.kt)("a",{parentName:"p",href:"https://github.com/prisma-labs/graphql-playground"},"GraphQL Playground")," or ",(0,o.kt)("a",{parentName:"p",href:"https://github.com/graphql/graphiql"},"GraphiQL"),")\nrely on introspection queries to function properly. Disabling introspection will prevent clients from accessing ",(0,o.kt)("inlineCode",{parentName:"p"},"__schema"),"\nand ",(0,o.kt)("inlineCode",{parentName:"p"},"__type")," fields. This may break some of the functionality that your clients might rely on and should be used with\nextreme caution."))}h.isMDXComponent=!0}}]);