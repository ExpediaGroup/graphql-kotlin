"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[9501],{93458:(e,n,t)=>{t.r(n),t.d(n,{assets:()=>o,contentTitle:()=>s,default:()=>d,frontMatter:()=>r,metadata:()=>i,toc:()=>c});var a=t(74848),l=t(28453);const r={id:"nullability",title:"Nullability"},s=void 0,i={id:"schema-generator/writing-schemas/nullability",title:"Nullability",description:"Both GraphQL and Kotlin have a concept of nullable as a marked typed. As a result we can automatically generate null",source:"@site/docs/schema-generator/writing-schemas/nullability.md",sourceDirName:"schema-generator/writing-schemas",slug:"/schema-generator/writing-schemas/nullability",permalink:"/graphql-kotlin/docs/9.x.x/schema-generator/writing-schemas/nullability",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/docs/schema-generator/writing-schemas/nullability.md",tags:[],version:"current",lastUpdatedBy:"Templeton Peck",lastUpdatedAt:1743524928e3,frontMatter:{id:"nullability",title:"Nullability"},sidebar:"docs",previous:{title:"Types and Fields",permalink:"/graphql-kotlin/docs/9.x.x/schema-generator/writing-schemas/fields"},next:{title:"Arguments",permalink:"/graphql-kotlin/docs/9.x.x/schema-generator/writing-schemas/arguments"}},o={},c=[];function u(e){const n={code:"code",p:"p",pre:"pre",...(0,l.R)(),...e.components};return(0,a.jsxs)(a.Fragment,{children:[(0,a.jsxs)(n.p,{children:["Both GraphQL and Kotlin have a concept of ",(0,a.jsx)(n.code,{children:"nullable"})," as a marked typed. As a result we can automatically generate null\nsafe schemas from Kotlin code."]}),"\n",(0,a.jsx)(n.pre,{children:(0,a.jsx)(n.code,{className:"language-kotlin",children:"class SimpleQuery {\n\n    fun generateNullableNumber(): Int? {\n        val num = Random().nextInt(100)\n        return if (num < 50) num else null\n    }\n\n    fun generateNumber(): Int = Random().nextInt(100)\n}\n"})}),"\n",(0,a.jsx)(n.p,{children:"The above Kotlin code would produce the following GraphQL schema:"}),"\n",(0,a.jsx)(n.pre,{children:(0,a.jsx)(n.code,{className:"language-graphql",children:"type Query {\n  generateNullableNumber: Int\n\n  generateNumber: Int!\n}\n"})})]})}function d(e={}){const{wrapper:n}={...(0,l.R)(),...e.components};return n?(0,a.jsx)(n,{...e,children:(0,a.jsx)(u,{...e})}):u(e)}},28453:(e,n,t)=>{t.d(n,{R:()=>s,x:()=>i});var a=t(96540);const l={},r=a.createContext(l);function s(e){const n=a.useContext(r);return a.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function i(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(l):e.components||l:s(e.components),a.createElement(r.Provider,{value:n},e.children)}}}]);