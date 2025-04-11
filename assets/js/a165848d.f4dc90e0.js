"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[5334],{31948:(e,t,s)=>{s.r(t),s.d(t,{assets:()=>l,contentTitle:()=>o,default:()=>d,frontMatter:()=>i,metadata:()=>a,toc:()=>c});var n=s(74848),r=s(28453);const i={id:"lists",title:"Lists"},o=void 0,a={id:"schema-generator/writing-schemas/lists",title:"Lists",description:"kotlin.collections.List is automatically mapped to the GraphQL List type. Type arguments provided to Kotlin collections",source:"@site/versioned_docs/version-6.x.x/schema-generator/writing-schemas/lists.md",sourceDirName:"schema-generator/writing-schemas",slug:"/schema-generator/writing-schemas/lists",permalink:"/graphql-kotlin/docs/6.x.x/schema-generator/writing-schemas/lists",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-6.x.x/schema-generator/writing-schemas/lists.md",tags:[],version:"6.x.x",lastUpdatedBy:"Samuel Vazquez",lastUpdatedAt:1744404742e3,frontMatter:{id:"lists",title:"Lists"},sidebar:"docs",previous:{title:"Enums",permalink:"/graphql-kotlin/docs/6.x.x/schema-generator/writing-schemas/enums"},next:{title:"Interfaces",permalink:"/graphql-kotlin/docs/6.x.x/schema-generator/writing-schemas/interfaces"}},l={},c=[{value:"Arrays and Unsupported Collection Types",id:"arrays-and-unsupported-collection-types",level:2}];function p(e){const t={a:"a",code:"code",h2:"h2",p:"p",pre:"pre",...(0,r.R)(),...e.components};return(0,n.jsxs)(n.Fragment,{children:[(0,n.jsxs)(t.p,{children:[(0,n.jsx)(t.code,{children:"kotlin.collections.List"})," is automatically mapped to the GraphQL ",(0,n.jsx)(t.code,{children:"List"})," type. Type arguments provided to Kotlin collections\nare used as the type arguments in the GraphQL ",(0,n.jsx)(t.code,{children:"List"})," type."]}),"\n",(0,n.jsx)(t.pre,{children:(0,n.jsx)(t.code,{className:"language-kotlin",children:"class SimpleQuery {\n    fun generateList(): List<String> {\n        // some logic here that generates list\n    }\n\n    fun doSomethingWithIntList(ints: List<Int>): String {\n        // some logic here that processes list\n    }\n}\n"})}),"\n",(0,n.jsx)(t.p,{children:"The above Kotlin class would produce the following GraphQL schema:"}),"\n",(0,n.jsx)(t.pre,{children:(0,n.jsx)(t.code,{className:"language-graphql",children:"type Query {\n    generateList: [String!]!\n    doSomethingWithIntList(ints: [Int!]!): String!\n}\n"})}),"\n",(0,n.jsx)(t.h2,{id:"arrays-and-unsupported-collection-types",children:"Arrays and Unsupported Collection Types"}),"\n",(0,n.jsxs)(t.p,{children:["Currently, the GraphQL spec only supports ",(0,n.jsx)(t.code,{children:"Lists"}),". Therefore, even though Java and Kotlin support number of other collection\ntypes, ",(0,n.jsx)(t.code,{children:"graphql-kotlin-schema-generator"})," only explicitly supports ",(0,n.jsx)(t.code,{children:"Lists"}),". Other collection types such as ",(0,n.jsx)(t.code,{children:"Sets"})," (see ",(0,n.jsx)(t.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/issues/201",children:"#201"}),")\nand arbitrary ",(0,n.jsx)(t.code,{children:"Map"})," data structures are not supported out of the box. While we do not recommend using ",(0,n.jsx)(t.code,{children:"Map"})," or ",(0,n.jsx)(t.code,{children:"Set"})," in the schema,\nthey could be supported with the use of the schema hooks."]}),"\n",(0,n.jsxs)(t.p,{children:["Due to the ",(0,n.jsx)(t.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/pull/1379",children:"argument deserialization issues"}),", arrays are currently not supported"]}),"\n",(0,n.jsx)(t.pre,{children:(0,n.jsx)(t.code,{className:"language-kotlin",children:"override fun willResolveMonad(type: KType): KType = when (type.classifier) {\n    Set::class -> List::class.createType(type.arguments)\n    else -> type\n}\n"})}),"\n",(0,n.jsxs)(t.p,{children:["See ",(0,n.jsx)(t.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/discussions/1110",children:"Discussion #1110"})," for more details."]})]})}function d(e={}){const{wrapper:t}={...(0,r.R)(),...e.components};return t?(0,n.jsx)(t,{...e,children:(0,n.jsx)(p,{...e})}):p(e)}},28453:(e,t,s)=>{s.d(t,{R:()=>o,x:()=>a});var n=s(96540);const r={},i=n.createContext(r);function o(e){const t=n.useContext(i);return n.useMemo((function(){return"function"==typeof e?e(t):{...t,...e}}),[t,e])}function a(e){let t;return t=e.disableParentContext?"function"==typeof e.components?e.components(r):e.components||r:o(e.components),n.createElement(i.Provider,{value:t},e.children)}}}]);