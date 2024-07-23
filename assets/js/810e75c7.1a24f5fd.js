"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[4117],{99917:(e,t,r)=>{r.r(t),r.d(t,{assets:()=>a,contentTitle:()=>o,default:()=>h,frontMatter:()=>i,metadata:()=>l,toc:()=>c});var n=r(74848),s=r(28453);const i={id:"lists",title:"Lists",original_id:"lists"},o=void 0,l={id:"schema-generator/writing-schemas/lists",title:"Lists",description:"Both kotlin.Array and kotlin.collections.List are automatically mapped to the GraphQL List type (for unsupported",source:"@site/versioned_docs/version-3.x.x/schema-generator/writing-schemas/lists.md",sourceDirName:"schema-generator/writing-schemas",slug:"/schema-generator/writing-schemas/lists",permalink:"/graphql-kotlin/docs/3.x.x/schema-generator/writing-schemas/lists",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-3.x.x/schema-generator/writing-schemas/lists.md",tags:[],version:"3.x.x",lastUpdatedBy:"mykevinjung",lastUpdatedAt:1721694712e3,frontMatter:{id:"lists",title:"Lists",original_id:"lists"},sidebar:"docs",previous:{title:"Enums",permalink:"/graphql-kotlin/docs/3.x.x/schema-generator/writing-schemas/enums"},next:{title:"Interfaces",permalink:"/graphql-kotlin/docs/3.x.x/schema-generator/writing-schemas/interfaces"}},a={},c=[{value:"Primitive Arrays",id:"primitive-arrays",level:2},{value:"Unsupported Collection Types",id:"unsupported-collection-types",level:2}];function d(e){const t={a:"a",code:"code",h2:"h2",p:"p",pre:"pre",table:"table",tbody:"tbody",td:"td",th:"th",thead:"thead",tr:"tr",...(0,s.R)(),...e.components};return(0,n.jsxs)(n.Fragment,{children:[(0,n.jsxs)(t.p,{children:["Both ",(0,n.jsx)(t.code,{children:"kotlin.Array"})," and ",(0,n.jsx)(t.code,{children:"kotlin.collections.List"})," are automatically mapped to the GraphQL ",(0,n.jsx)(t.code,{children:"List"})," type (for unsupported\nuse cases see below). Type arguments provided to Kotlin collections are used as the type arguments in the GraphQL ",(0,n.jsx)(t.code,{children:"List"}),"\ntype. Kotlin specialized classes representing arrays of Java primitive types without boxing overhead (e.g. ",(0,n.jsx)(t.code,{children:"IntArray"}),")\nare also supported."]}),"\n",(0,n.jsx)(t.pre,{children:(0,n.jsx)(t.code,{className:"language-kotlin",children:"\nclass SimpleQuery {\n    fun generateList(): List<Int> {\n        // some logic here that generates list\n    }\n\n    fun doSomethingWithIntArray(ints: IntArray): String {\n        // some logic here that processes array\n    }\n\n    fun doSomethingWithIntList(ints: List<Int>): String {\n        // some logic here that processes list\n    }\n}\n\n"})}),"\n",(0,n.jsx)(t.p,{children:"The above Kotlin class would produce the following GraphQL schema:"}),"\n",(0,n.jsx)(t.pre,{children:(0,n.jsx)(t.code,{className:"language-graphql",children:"\ntype Query {\n    generateList: [Int!]!\n    doSomethingWithIntArray(ints: [Int!]!): String!\n    doSomethingWithIntList(ints: [Int!]!): String!\n}\n\n"})}),"\n",(0,n.jsx)(t.h2,{id:"primitive-arrays",children:"Primitive Arrays"}),"\n",(0,n.jsxs)(t.p,{children:[(0,n.jsx)(t.code,{children:"graphql-kotlin-schema-generator"})," supports the following primitive array types without autoboxing overhead. Similarly to\nthe ",(0,n.jsx)(t.code,{children:"kotlin.Array"})," of objects the underlying type is automatically mapped to GraphQL ",(0,n.jsx)(t.code,{children:"List"})," type."]}),"\n",(0,n.jsxs)(t.table,{children:[(0,n.jsx)(t.thead,{children:(0,n.jsx)(t.tr,{children:(0,n.jsx)(t.th,{children:"Kotlin Type"})})}),(0,n.jsxs)(t.tbody,{children:[(0,n.jsx)(t.tr,{children:(0,n.jsx)(t.td,{children:(0,n.jsx)(t.code,{children:"kotlin.IntArray"})})}),(0,n.jsx)(t.tr,{children:(0,n.jsx)(t.td,{children:(0,n.jsx)(t.code,{children:"kotlin.LongArray"})})}),(0,n.jsx)(t.tr,{children:(0,n.jsx)(t.td,{children:(0,n.jsx)(t.code,{children:"kotlin.ShortArray"})})}),(0,n.jsx)(t.tr,{children:(0,n.jsx)(t.td,{children:(0,n.jsx)(t.code,{children:"kotlin.FloatArray"})})}),(0,n.jsx)(t.tr,{children:(0,n.jsx)(t.td,{children:(0,n.jsx)(t.code,{children:"kotlin.DoubleArray"})})}),(0,n.jsx)(t.tr,{children:(0,n.jsx)(t.td,{children:(0,n.jsx)(t.code,{children:"kotlin.CharArray"})})}),(0,n.jsx)(t.tr,{children:(0,n.jsx)(t.td,{children:(0,n.jsx)(t.code,{children:"kotlin.BooleanArray"})})})]})]}),"\n",(0,n.jsxs)(t.p,{children:["> NOTE: Underlying GraphQL types of primitive arrays will be corresponding to the built-in scalar types or extended\n> scalar types provided by ",(0,n.jsx)(t.code,{children:"graphql-java"}),"."]}),"\n",(0,n.jsx)(t.h2,{id:"unsupported-collection-types",children:"Unsupported Collection Types"}),"\n",(0,n.jsxs)(t.p,{children:["Currently GraphQL spec only supports ",(0,n.jsx)(t.code,{children:"Lists"}),". Therefore even though Java and Kotlin support number of other collection\ntypes, ",(0,n.jsx)(t.code,{children:"graphql-kotlin-schema-generator"})," only explicitly supports ",(0,n.jsx)(t.code,{children:"Lists"})," and primitive arrays. Other collection types\nsuch as ",(0,n.jsx)(t.code,{children:"Sets"})," (see ",(0,n.jsx)(t.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/issues/201",children:"#201"}),") and arbitrary ",(0,n.jsx)(t.code,{children:"Map"})," data\nstructures are not supported."]})]})}function h(e={}){const{wrapper:t}={...(0,s.R)(),...e.components};return t?(0,n.jsx)(t,{...e,children:(0,n.jsx)(d,{...e})}):d(e)}},28453:(e,t,r)=>{r.d(t,{R:()=>o,x:()=>l});var n=r(96540);const s={},i=n.createContext(s);function o(e){const t=n.useContext(i);return n.useMemo((function(){return"function"==typeof e?e(t):{...t,...e}}),[t,e])}function l(e){let t;return t=e.disableParentContext?"function"==typeof e.components?e.components(s):e.components||s:o(e.components),n.createElement(i.Provider,{value:t},e.children)}}}]);