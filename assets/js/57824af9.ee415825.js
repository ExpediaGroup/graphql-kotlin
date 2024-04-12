"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[3939],{17125:(e,n,t)=>{t.r(n),t.d(n,{assets:()=>o,contentTitle:()=>c,default:()=>l,frontMatter:()=>r,metadata:()=>i,toc:()=>d});var a=t(74848),s=t(28453);const r={id:"deprecating-schema",title:"Deprecating Schema",original_id:"deprecating-schema"},c=void 0,i={id:"schema-generator/customizing-schemas/deprecating-schema",title:"Deprecating Schema",description:"GraphQL schemas can have fields marked as deprecated. Instead of creating a custom annotation,",source:"@site/versioned_docs/version-3.x.x/schema-generator/customizing-schemas/deprecating-schema.md",sourceDirName:"schema-generator/customizing-schemas",slug:"/schema-generator/customizing-schemas/deprecating-schema",permalink:"/graphql-kotlin/docs/3.x.x/schema-generator/customizing-schemas/deprecating-schema",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-3.x.x/schema-generator/customizing-schemas/deprecating-schema.md",tags:[],version:"3.x.x",lastUpdatedBy:"Curtis Cook",lastUpdatedAt:1712948770,formattedLastUpdatedAt:"Apr 12, 2024",frontMatter:{id:"deprecating-schema",title:"Deprecating Schema",original_id:"deprecating-schema"},sidebar:"docs",previous:{title:"Directives",permalink:"/graphql-kotlin/docs/3.x.x/schema-generator/customizing-schemas/directives"},next:{title:"Advanced Features",permalink:"/graphql-kotlin/docs/3.x.x/schema-generator/customizing-schemas/advanced-features"}},o={},d=[];function p(e){const n={a:"a",code:"code",p:"p",pre:"pre",...(0,s.R)(),...e.components};return(0,a.jsxs)(a.Fragment,{children:[(0,a.jsxs)(n.p,{children:["GraphQL schemas can have fields marked as deprecated. Instead of creating a custom annotation,\n",(0,a.jsx)(n.code,{children:"graphql-kotlin-schema-generator"})," just looks for the ",(0,a.jsx)(n.code,{children:"kotlin.Deprecated"})," annotation and will use that annotation message\nfor the deprecated reason."]}),"\n",(0,a.jsx)(n.pre,{children:(0,a.jsx)(n.code,{className:"language-kotlin",children:'\nclass SimpleQuery {\n  @Deprecated(message = "this query is deprecated", replaceWith = ReplaceWith("shinyNewQuery"))\n  fun simpleDeprecatedQuery(): Boolean = false\n\n  fun shinyNewQuery(): Boolean = true\n}\n\n'})}),"\n",(0,a.jsx)(n.p,{children:"The above query would produce the following GraphQL schema:"}),"\n",(0,a.jsx)(n.pre,{children:(0,a.jsx)(n.code,{className:"language-graphql",children:'\ntype Query {\n  simpleDeprecatedQuery: Boolean! @deprecated(reason: "this query is deprecated, replace with shinyNewQuery")\n\n  shinyNewQuery: Boolean!\n}\n\n'})}),"\n",(0,a.jsx)(n.p,{children:"While you can deprecate any fields/functions/classes in your Kotlin code, GraphQL only supports deprecation directive on\nthe fields (which correspond to Kotlin fields and functions) and enum values."}),"\n",(0,a.jsxs)(n.p,{children:["Deprecation of input types is not yet supported ",(0,a.jsx)(n.a,{href:"https://github.com/graphql/graphql-spec/pull/525",children:"in the GraphQL spec"}),"."]})]})}function l(e={}){const{wrapper:n}={...(0,s.R)(),...e.components};return n?(0,a.jsx)(n,{...e,children:(0,a.jsx)(p,{...e})}):p(e)}},28453:(e,n,t)=>{t.d(n,{R:()=>c,x:()=>i});var a=t(96540);const s={},r=a.createContext(s);function c(e){const n=a.useContext(r);return a.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function i(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(s):e.components||s:c(e.components),a.createElement(r.Provider,{value:n},e.children)}}}]);