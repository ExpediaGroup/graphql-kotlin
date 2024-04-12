"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[257],{88276:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>i,contentTitle:()=>c,default:()=>l,frontMatter:()=>r,metadata:()=>o,toc:()=>d});var s=n(74848),a=n(28453);const r={id:"deprecating-schema",title:"Deprecating Schema"},c=void 0,o={id:"schema-generator/customizing-schemas/deprecating-schema",title:"Deprecating Schema",description:"GraphQL schemas can have fields marked as deprecated. Instead of creating a custom annotation,",source:"@site/versioned_docs/version-4.x.x/schema-generator/customizing-schemas/deprecating-schema.md",sourceDirName:"schema-generator/customizing-schemas",slug:"/schema-generator/customizing-schemas/deprecating-schema",permalink:"/graphql-kotlin/docs/4.x.x/schema-generator/customizing-schemas/deprecating-schema",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-4.x.x/schema-generator/customizing-schemas/deprecating-schema.md",tags:[],version:"4.x.x",lastUpdatedBy:"Curtis Cook",lastUpdatedAt:1712948770,formattedLastUpdatedAt:"Apr 12, 2024",frontMatter:{id:"deprecating-schema",title:"Deprecating Schema"},sidebar:"docs",previous:{title:"Directives",permalink:"/graphql-kotlin/docs/4.x.x/schema-generator/customizing-schemas/directives"},next:{title:"Restricting Input and Output Types",permalink:"/graphql-kotlin/docs/4.x.x/schema-generator/customizing-schemas/restricting-input-output"}},i={},d=[];function p(e){const t={a:"a",code:"code",p:"p",pre:"pre",...(0,a.R)(),...e.components};return(0,s.jsxs)(s.Fragment,{children:[(0,s.jsxs)(t.p,{children:["GraphQL schemas can have fields marked as deprecated. Instead of creating a custom annotation,\n",(0,s.jsx)(t.code,{children:"graphql-kotlin-schema-generator"})," just looks for the ",(0,s.jsx)(t.code,{children:"kotlin.Deprecated"})," annotation and will use that annotation message\nfor the deprecated reason."]}),"\n",(0,s.jsx)(t.pre,{children:(0,s.jsx)(t.code,{className:"language-kotlin",children:'class SimpleQuery {\n  @Deprecated(message = "this query is deprecated", replaceWith = ReplaceWith("shinyNewQuery"))\n  fun simpleDeprecatedQuery(): Boolean = false\n\n  fun shinyNewQuery(): Boolean = true\n}\n'})}),"\n",(0,s.jsx)(t.p,{children:"The above query would produce the following GraphQL schema:"}),"\n",(0,s.jsx)(t.pre,{children:(0,s.jsx)(t.code,{className:"language-graphql",children:'type Query {\n  simpleDeprecatedQuery: Boolean! @deprecated(reason: "this query is deprecated, replace with shinyNewQuery")\n\n  shinyNewQuery: Boolean!\n}\n'})}),"\n",(0,s.jsx)(t.p,{children:"While you can deprecate any fields/functions/classes in your Kotlin code, GraphQL only supports deprecation directive on\nthe fields (which correspond to Kotlin fields and functions) and enum values."}),"\n",(0,s.jsxs)(t.p,{children:["Deprecation of input types is not yet supported ",(0,s.jsx)(t.a,{href:"https://github.com/graphql/graphql-spec/pull/525",children:"in the GraphQL spec"}),"."]})]})}function l(e={}){const{wrapper:t}={...(0,a.R)(),...e.components};return t?(0,s.jsx)(t,{...e,children:(0,s.jsx)(p,{...e})}):p(e)}},28453:(e,t,n)=>{n.d(t,{R:()=>c,x:()=>o});var s=n(96540);const a={},r=s.createContext(a);function c(e){const t=s.useContext(r);return s.useMemo((function(){return"function"==typeof e?e(t):{...t,...e}}),[t,e])}function o(e){let t;return t=e.disableParentContext?"function"==typeof e.components?e.components(a):e.components||a:c(e.components),s.createElement(r.Provider,{value:t},e.children)}}}]);