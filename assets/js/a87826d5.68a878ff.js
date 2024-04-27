"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[4732],{43473:(e,n,t)=>{t.r(n),t.d(n,{assets:()=>c,contentTitle:()=>r,default:()=>h,frontMatter:()=>o,metadata:()=>i,toc:()=>l});var a=t(74848),s=t(28453);const o={id:"schema-generator-getting-started",title:"Getting Started with the Schema Generator",original_id:"schema-generator-getting-started"},r=void 0,i={id:"schema-generator/schema-generator-getting-started",title:"Getting Started with the Schema Generator",description:"Install",source:"@site/versioned_docs/version-3.x.x/schema-generator/schema-generator-getting-started.md",sourceDirName:"schema-generator",slug:"/schema-generator/schema-generator-getting-started",permalink:"/graphql-kotlin/docs/3.x.x/schema-generator/schema-generator-getting-started",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-3.x.x/schema-generator/schema-generator-getting-started.md",tags:[],version:"3.x.x",lastUpdatedBy:"Tasuku Nakagawa",lastUpdatedAt:1714237421,formattedLastUpdatedAt:"Apr 27, 2024",frontMatter:{id:"schema-generator-getting-started",title:"Getting Started with the Schema Generator",original_id:"schema-generator-getting-started"},sidebar:"docs",previous:{title:"Blogs & Videos",permalink:"/graphql-kotlin/docs/3.x.x/blogs-and-videos"},next:{title:"Nullability",permalink:"/graphql-kotlin/docs/3.x.x/schema-generator/writing-schemas/nullability"}},c={},l=[{value:"Install",id:"install",level:2},{value:"Usage",id:"usage",level:2},{value:"<code>toSchema</code>",id:"toschema",level:2},{value:"Class <code>TopLevelObject</code>",id:"class-toplevelobject",level:2}];function d(e){const n={a:"a",code:"code",h2:"h2",p:"p",pre:"pre",...(0,s.R)(),...e.components};return(0,a.jsxs)(a.Fragment,{children:[(0,a.jsx)(n.h2,{id:"install",children:"Install"}),"\n",(0,a.jsxs)(n.p,{children:["Using a JVM dependency manager, link ",(0,a.jsx)(n.code,{children:"graphql-kotlin-schema-generator"})," to your project."]}),"\n",(0,a.jsx)(n.p,{children:"With Maven:"}),"\n",(0,a.jsx)(n.pre,{children:(0,a.jsx)(n.code,{className:"language-xml",children:"\n<dependency>\n  <groupId>com.expediagroup</groupId>\n  <artifactId>graphql-kotlin-schema-generator</artifactId>\n  <version>${latestVersion}</version>\n</dependency>\n\n"})}),"\n",(0,a.jsx)(n.p,{children:"With Gradle:"}),"\n",(0,a.jsx)(n.pre,{children:(0,a.jsx)(n.code,{className:"language-kotlin",children:'\nimplementation("com.expediagroup", "graphql-kotlin-schema-generator", latestVersion)\n\n'})}),"\n",(0,a.jsx)(n.h2,{id:"usage",children:"Usage"}),"\n",(0,a.jsxs)(n.p,{children:[(0,a.jsx)(n.code,{children:"graphql-kotlin-schema-generator"})," provides a single function, ",(0,a.jsx)(n.code,{children:"toSchema"}),", to generate a schema from Kotlin objects."]}),"\n",(0,a.jsx)(n.pre,{children:(0,a.jsx)(n.code,{className:"language-kotlin",children:"\ndata class Widget(val id: Int, val value: String)\n\nclass WidgetQuery {\n  fun widgetById(id: Int): Widget? {\n    // grabs widget from a data source\n  }\n}\n\nclass WidgetMutation {\n  fun saveWidget(value: String): Widget {\n    // some logic for saving widget\n  }\n}\n\nval widgetQuery = WidgetQuery()\nval widgetMutation = WidgetMutation()\nval schema = toSchema(\n  config = yourCustomConfig(),\n  queries = listOf(TopLevelObject(widgetQuery)),\n  mutations = listOf(TopLevelObject(widgetMutation))\n)\n\n"})}),"\n",(0,a.jsx)(n.p,{children:"will generate:"}),"\n",(0,a.jsx)(n.pre,{children:(0,a.jsx)(n.code,{className:"language-graphql",children:"\nschema {\n  query: Query\n  mutation: Mutation\n}\n\ntype Query {\n  widgetById(id: Int!): Widget\n}\n\ntype Mutation {\n  saveWidget(value: String!): Widget!\n}\n\ntype Widget {\n  id: Int!\n  value: String!\n}\n\n"})}),"\n",(0,a.jsxs)(n.p,{children:["Any ",(0,a.jsx)(n.code,{children:"public"})," functions defined on a query, mutation, or subscription Kotlin class will be translated into GraphQL fields on the object\ntype. ",(0,a.jsx)(n.code,{children:"toSchema"})," will then recursively apply Kotlin reflection on the specified classes to generate all\nremaining object types, their properties, functions, and function arguments."]}),"\n",(0,a.jsxs)(n.p,{children:["The generated ",(0,a.jsx)(n.code,{children:"GraphQLSchema"})," can then be used to expose a GraphQL API endpoint."]}),"\n",(0,a.jsx)(n.h2,{id:"toschema",children:(0,a.jsx)(n.code,{children:"toSchema"})}),"\n",(0,a.jsxs)(n.p,{children:["This function accepts four arguments: ",(0,a.jsx)(n.code,{children:"config"}),", ",(0,a.jsx)(n.code,{children:"queries"}),", ",(0,a.jsx)(n.code,{children:"mutations"})," and ",(0,a.jsx)(n.code,{children:"subscriptions"}),". The ",(0,a.jsx)(n.code,{children:"queries"}),", ",(0,a.jsx)(n.code,{children:"mutations"}),"\nand ",(0,a.jsx)(n.code,{children:"subscriptions"})," are a list of ",(0,a.jsx)(n.code,{children:"TopLevelObject"}),"s and will be used to generate corresponding GraphQL root types. See\nbelow on why we use this wrapper class. The ",(0,a.jsx)(n.code,{children:"config"})," contains all the extra information you need to pass, including\ncustom hooks, supported packages, and name overrides. See the ",(0,a.jsx)(n.a,{href:"/graphql-kotlin/docs/3.x.x/schema-generator/customizing-schemas/generator-config",children:"Generator Configuration"})," documentation for more information."]}),"\n",(0,a.jsxs)(n.p,{children:["You can see the definition for ",(0,a.jsx)(n.code,{children:"toSchema"})," ",(0,a.jsx)(n.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/3.x.x/generator/graphql-kotlin-schema-generator/src/main/kotlin/com/expediagroup/graphql/generator/toSchema.kt",children:"in the\nsource"})]}),"\n",(0,a.jsxs)(n.h2,{id:"class-toplevelobject",children:["Class ",(0,a.jsx)(n.code,{children:"TopLevelObject"})]}),"\n",(0,a.jsxs)(n.p,{children:[(0,a.jsx)(n.code,{children:"toSchema"})," uses Kotlin reflection to build a GraphQL schema from given classes using ",(0,a.jsx)(n.code,{children:"graphql-java"}),"'s schema builder. We\ndon't just pass a ",(0,a.jsx)(n.code,{children:"KClass"})," though, we have to actually pass an object, because the functions on the object are\ntransformed into the data fetchers. In most cases, a ",(0,a.jsx)(n.code,{children:"TopLevelObject"})," can be constructed with just an object:"]}),"\n",(0,a.jsx)(n.pre,{children:(0,a.jsx)(n.code,{className:"language-kotlin",children:"\nclass Query {\n  fun getNumber() = 1\n}\n\nval topLevelObject = TopLevelObject(Query())\n\ntoSchema(config = config, queries = listOf(topLevelObject))\n\n"})}),"\n",(0,a.jsxs)(n.p,{children:["In the above case, ",(0,a.jsx)(n.code,{children:"toSchema"})," will use ",(0,a.jsx)(n.code,{children:"topLevelObject::class"})," as the reflection target, and ",(0,a.jsx)(n.code,{children:"Query"})," as the data fetcher\ntarget."]}),"\n",(0,a.jsxs)(n.p,{children:["In a lot of cases, such as with Spring AOP, the object (or bean) being used to generate a schema is a dynamic proxy. In\nthis case, ",(0,a.jsx)(n.code,{children:"topLevelObject::class"})," is not ",(0,a.jsx)(n.code,{children:"Query"}),", but rather a generated class that will confuse the schema generator.\nTo specify the ",(0,a.jsx)(n.code,{children:"KClass"})," to use for reflection on a proxy, pass the class to ",(0,a.jsx)(n.code,{children:"TopLevelObject"}),":"]}),"\n",(0,a.jsx)(n.pre,{children:(0,a.jsx)(n.code,{className:"language-kotlin",children:"\n@Component\nclass Query {\n  @Timed\n  fun getNumber() = 1\n}\n\nval query = getObjectFromBean()\nval customDef = TopLevelObject(query, Query::class)\n\ntoSchema(config, listOf(customDef))\n\n"})})]})}function h(e={}){const{wrapper:n}={...(0,s.R)(),...e.components};return n?(0,a.jsx)(n,{...e,children:(0,a.jsx)(d,{...e})}):d(e)}},28453:(e,n,t)=>{t.d(n,{R:()=>r,x:()=>i});var a=t(96540);const s={},o=a.createContext(s);function r(e){const n=a.useContext(o);return a.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function i(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(s):e.components||s:r(e.components),a.createElement(o.Provider,{value:n},e.children)}}}]);