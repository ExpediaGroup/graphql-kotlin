"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[1743],{42447:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>l,contentTitle:()=>o,default:()=>h,frontMatter:()=>i,metadata:()=>a,toc:()=>c});var r=n(74848),s=n(28453);const i={id:"lists",title:"Lists"},o=void 0,a={id:"schema-generator/writing-schemas/lists",title:"Lists",description:"Both kotlin.Array and kotlin.collections.List are automatically mapped to the GraphQL List type (for unsupported",source:"@site/versioned_docs/version-5.x.x/schema-generator/writing-schemas/lists.md",sourceDirName:"schema-generator/writing-schemas",slug:"/schema-generator/writing-schemas/lists",permalink:"/graphql-kotlin/docs/5.x.x/schema-generator/writing-schemas/lists",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-5.x.x/schema-generator/writing-schemas/lists.md",tags:[],version:"5.x.x",lastUpdatedBy:"Samuel Vazquez",lastUpdatedAt:174440151e4,frontMatter:{id:"lists",title:"Lists"},sidebar:"docs",previous:{title:"Enums",permalink:"/graphql-kotlin/docs/5.x.x/schema-generator/writing-schemas/enums"},next:{title:"Interfaces",permalink:"/graphql-kotlin/docs/5.x.x/schema-generator/writing-schemas/interfaces"}},l={},c=[{value:"Primitive Arrays",id:"primitive-arrays",level:2},{value:"Unsupported Collection Types",id:"unsupported-collection-types",level:2}];function d(e){const t={a:"a",admonition:"admonition",code:"code",h2:"h2",p:"p",pre:"pre",table:"table",tbody:"tbody",td:"td",th:"th",thead:"thead",tr:"tr",...(0,s.R)(),...e.components};return(0,r.jsxs)(r.Fragment,{children:[(0,r.jsxs)(t.p,{children:["Both ",(0,r.jsx)(t.code,{children:"kotlin.Array"})," and ",(0,r.jsx)(t.code,{children:"kotlin.collections.List"})," are automatically mapped to the GraphQL ",(0,r.jsx)(t.code,{children:"List"})," type (for unsupported\nuse cases see below). Type arguments provided to Kotlin collections are used as the type arguments in the GraphQL ",(0,r.jsx)(t.code,{children:"List"}),"\ntype. Kotlin specialized classes (e.g. ",(0,r.jsx)(t.code,{children:"IntArray"}),") representing arrays of Java primitive types without boxing overhead\nare also supported."]}),"\n",(0,r.jsx)(t.pre,{children:(0,r.jsx)(t.code,{className:"language-kotlin",children:"class SimpleQuery {\n    fun generateList(): List<Int> {\n        // some logic here that generates list\n    }\n\n    fun doSomethingWithIntArray(ints: IntArray): String {\n        // some logic here that processes array\n    }\n\n    fun doSomethingWithIntList(ints: List<Int>): String {\n        // some logic here that processes list\n    }\n}\n"})}),"\n",(0,r.jsx)(t.p,{children:"The above Kotlin class would produce the following GraphQL schema:"}),"\n",(0,r.jsx)(t.pre,{children:(0,r.jsx)(t.code,{className:"language-graphql",children:"type Query {\n    generateList: [Int!]!\n    doSomethingWithIntArray(ints: [Int!]!): String!\n    doSomethingWithIntList(ints: [Int!]!): String!\n}\n"})}),"\n",(0,r.jsx)(t.h2,{id:"primitive-arrays",children:"Primitive Arrays"}),"\n",(0,r.jsxs)(t.p,{children:[(0,r.jsx)(t.code,{children:"graphql-kotlin-schema-generator"})," supports the following primitive array types without autoboxing overhead. Similarly to\nthe ",(0,r.jsx)(t.code,{children:"kotlin.Array"})," of objects the underlying type is automatically mapped to GraphQL ",(0,r.jsx)(t.code,{children:"List"})," type."]}),"\n",(0,r.jsxs)(t.table,{children:[(0,r.jsx)(t.thead,{children:(0,r.jsx)(t.tr,{children:(0,r.jsx)(t.th,{children:"Kotlin Type"})})}),(0,r.jsxs)(t.tbody,{children:[(0,r.jsx)(t.tr,{children:(0,r.jsx)(t.td,{children:(0,r.jsx)(t.code,{children:"kotlin.IntArray"})})}),(0,r.jsx)(t.tr,{children:(0,r.jsx)(t.td,{children:(0,r.jsx)(t.code,{children:"kotlin.LongArray"})})}),(0,r.jsx)(t.tr,{children:(0,r.jsx)(t.td,{children:(0,r.jsx)(t.code,{children:"kotlin.ShortArray"})})}),(0,r.jsx)(t.tr,{children:(0,r.jsx)(t.td,{children:(0,r.jsx)(t.code,{children:"kotlin.FloatArray"})})}),(0,r.jsx)(t.tr,{children:(0,r.jsx)(t.td,{children:(0,r.jsx)(t.code,{children:"kotlin.DoubleArray"})})}),(0,r.jsx)(t.tr,{children:(0,r.jsx)(t.td,{children:(0,r.jsx)(t.code,{children:"kotlin.CharArray"})})}),(0,r.jsx)(t.tr,{children:(0,r.jsx)(t.td,{children:(0,r.jsx)(t.code,{children:"kotlin.BooleanArray"})})})]})]}),"\n",(0,r.jsx)(t.admonition,{type:"note",children:(0,r.jsxs)(t.p,{children:["The underlying GraphQL types of primitive arrays will be corresponding to the built-in scalar types or extended scalar types provided by ",(0,r.jsx)(t.code,{children:"graphql-java"}),"."]})}),"\n",(0,r.jsx)(t.h2,{id:"unsupported-collection-types",children:"Unsupported Collection Types"}),"\n",(0,r.jsxs)(t.p,{children:["Currently, the GraphQL spec only supports ",(0,r.jsx)(t.code,{children:"Lists"}),". Therefore, even though Java and Kotlin support number of other collection\ntypes, ",(0,r.jsx)(t.code,{children:"graphql-kotlin-schema-generator"})," only explicitly supports ",(0,r.jsx)(t.code,{children:"Lists"})," and primitive arrays. Other collection types\nsuch as ",(0,r.jsx)(t.code,{children:"Sets"})," (see ",(0,r.jsx)(t.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/issues/201",children:"#201"}),") and arbitrary ",(0,r.jsx)(t.code,{children:"Map"})," data\nstructures are not supported out of the box. While we do not reccomend using ",(0,r.jsx)(t.code,{children:"Map"})," or ",(0,r.jsx)(t.code,{children:"Set"})," in the schema,\nthey are supported with the use of the schema hooks."]}),"\n",(0,r.jsx)(t.pre,{children:(0,r.jsx)(t.code,{className:"language-kotlin",children:"override fun willResolveMonad(type: KType): KType = when (type.classifier) {\n    Set::class -> List::class.createType(type.arguments)\n    else -> type\n}\n"})}),"\n",(0,r.jsxs)(t.p,{children:["See ",(0,r.jsx)(t.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/discussions/1110",children:"Discussion #1110"})," for more details."]})]})}function h(e={}){const{wrapper:t}={...(0,s.R)(),...e.components};return t?(0,r.jsx)(t,{...e,children:(0,r.jsx)(d,{...e})}):d(e)}},28453:(e,t,n)=>{n.d(t,{R:()=>o,x:()=>a});var r=n(96540);const s={},i=r.createContext(s);function o(e){const t=r.useContext(i);return r.useMemo((function(){return"function"==typeof e?e(t):{...t,...e}}),[t,e])}function a(e){let t;return t=e.disableParentContext?"function"==typeof e.components?e.components(s):e.components||s:o(e.components),r.createElement(i.Provider,{value:t},e.children)}}}]);