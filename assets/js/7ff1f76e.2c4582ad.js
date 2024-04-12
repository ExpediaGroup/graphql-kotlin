"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[5701],{9857:(e,n,t)=>{t.r(n),t.d(n,{assets:()=>a,contentTitle:()=>r,default:()=>h,frontMatter:()=>o,metadata:()=>c,toc:()=>l});var i=t(74848),s=t(28453);const o={id:"excluding-fields",title:"Excluding Fields"},r=void 0,c={id:"schema-generator/customizing-schemas/excluding-fields",title:"Excluding Fields",description:"There are two ways to ensure the GraphQL schema generation omits fields when using Kotlin reflection:",source:"@site/versioned_docs/version-4.x.x/schema-generator/customizing-schemas/excluding-fields.md",sourceDirName:"schema-generator/customizing-schemas",slug:"/schema-generator/customizing-schemas/excluding-fields",permalink:"/graphql-kotlin/docs/4.x.x/schema-generator/customizing-schemas/excluding-fields",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-4.x.x/schema-generator/customizing-schemas/excluding-fields.md",tags:[],version:"4.x.x",lastUpdatedBy:"Curtis Cook",lastUpdatedAt:1712948770,formattedLastUpdatedAt:"Apr 12, 2024",frontMatter:{id:"excluding-fields",title:"Excluding Fields"},sidebar:"docs",previous:{title:"Documenting Schema",permalink:"/graphql-kotlin/docs/4.x.x/schema-generator/customizing-schemas/documenting-fields"},next:{title:"Renaming Fields",permalink:"/graphql-kotlin/docs/4.x.x/schema-generator/customizing-schemas/renaming-fields"}},a={},l=[];function d(e){const n={code:"code",li:"li",p:"p",pre:"pre",ul:"ul",...(0,s.R)(),...e.components};return(0,i.jsxs)(i.Fragment,{children:[(0,i.jsx)(n.p,{children:"There are two ways to ensure the GraphQL schema generation omits fields when using Kotlin reflection:"}),"\n",(0,i.jsxs)(n.ul,{children:["\n",(0,i.jsxs)(n.li,{children:["The first is by marking the field as non-",(0,i.jsx)(n.code,{children:"public"})," scope (",(0,i.jsx)(n.code,{children:"private"}),", ",(0,i.jsx)(n.code,{children:"protected"}),", ",(0,i.jsx)(n.code,{children:"internal"}),")"]}),"\n",(0,i.jsxs)(n.li,{children:["The second method is by annotating the field with ",(0,i.jsx)(n.code,{children:"@GraphQLIgnore"}),"."]}),"\n"]}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-kotlin",children:'class SimpleQuery {\n  @GraphQLIgnore\n  fun notPartOfSchema() = "ignore me!"\n\n  private fun privateFunctionsAreNotVisible() = "ignored private function"\n\n  fun doSomething(value: Int): Boolean = true\n}\n'})}),"\n",(0,i.jsx)(n.p,{children:"The above query would produce the following GraphQL schema:"}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-graphql",children:"type Query {\n  doSomething(value: Int!): Boolean!\n}\n"})}),"\n",(0,i.jsxs)(n.p,{children:["Note that the public method ",(0,i.jsx)(n.code,{children:"notPartOfSchema"})," is not included in the schema."]})]})}function h(e={}){const{wrapper:n}={...(0,s.R)(),...e.components};return n?(0,i.jsx)(n,{...e,children:(0,i.jsx)(d,{...e})}):d(e)}},28453:(e,n,t)=>{t.d(n,{R:()=>r,x:()=>c});var i=t(96540);const s={},o=i.createContext(s);function r(e){const n=i.useContext(o);return i.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function c(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(s):e.components||s:r(e.components),i.createElement(o.Provider,{value:n},e.children)}}}]);