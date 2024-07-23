"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[8713],{75437:(e,n,t)=>{t.r(n),t.d(n,{assets:()=>o,contentTitle:()=>c,default:()=>l,frontMatter:()=>s,metadata:()=>i,toc:()=>d});var r=t(74848),a=t(28453);const s={id:"deprecating-schema",title:"Deprecating Schema"},c=void 0,i={id:"schema-generator/customizing-schemas/deprecating-schema",title:"Deprecating Schema",description:"GraphQL schemas supports deprecation directive on",source:"@site/versioned_docs/version-6.x.x/schema-generator/customizing-schemas/deprecating-schema.md",sourceDirName:"schema-generator/customizing-schemas",slug:"/schema-generator/customizing-schemas/deprecating-schema",permalink:"/graphql-kotlin/docs/6.x.x/schema-generator/customizing-schemas/deprecating-schema",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-6.x.x/schema-generator/customizing-schemas/deprecating-schema.md",tags:[],version:"6.x.x",lastUpdatedBy:"mykevinjung",lastUpdatedAt:1721694712e3,frontMatter:{id:"deprecating-schema",title:"Deprecating Schema"},sidebar:"docs",previous:{title:"Directives",permalink:"/graphql-kotlin/docs/6.x.x/schema-generator/customizing-schemas/directives"},next:{title:"Custom Types",permalink:"/graphql-kotlin/docs/6.x.x/schema-generator/customizing-schemas/custom-type-reference"}},o={},d=[{value:"Kotlin.Deprecated",id:"kotlindeprecated",level:2},{value:"GraphQLDeprecated",id:"graphqldeprecated",level:2}];function p(e){const n={a:"a",code:"code",h2:"h2",p:"p",pre:"pre",...(0,a.R)(),...e.components};return(0,r.jsxs)(r.Fragment,{children:[(0,r.jsx)(n.p,{children:"GraphQL schemas supports deprecation directive on\nthe fields (which correspond to Kotlin properties and functions), input fields and enum values."}),"\n",(0,r.jsxs)(n.p,{children:["Deprecation of arguments is currently not supported ",(0,r.jsx)(n.a,{href:"https://youtrack.jetbrains.com/issue/KT-25643",children:"in Kotlin"}),"."]}),"\n",(0,r.jsx)(n.h2,{id:"kotlindeprecated",children:"Kotlin.Deprecated"}),"\n",(0,r.jsxs)(n.p,{children:["Instead of creating a custom annotation,\n",(0,r.jsx)(n.code,{children:"graphql-kotlin-schema-generator"})," just looks for the ",(0,r.jsx)(n.code,{children:"@kotlin.Deprecated"})," annotation and will use that annotation message\nfor the deprecated reason."]}),"\n",(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-kotlin",children:'class SimpleQuery {\n  @Deprecated(message = "this query is deprecated", replaceWith = ReplaceWith("shinyNewQuery"))\n  fun simpleDeprecatedQuery(): Boolean = false\n\n  fun shinyNewQuery(): Boolean = true\n}\n'})}),"\n",(0,r.jsx)(n.p,{children:"The above query would produce the following GraphQL schema:"}),"\n",(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-graphql",children:'type Query {\n  simpleDeprecatedQuery: Boolean! @deprecated(reason: "this query is deprecated, replace with shinyNewQuery")\n\n  shinyNewQuery: Boolean!\n}\n'})}),"\n",(0,r.jsx)(n.h2,{id:"graphqldeprecated",children:"GraphQLDeprecated"}),"\n",(0,r.jsxs)(n.p,{children:["A side-effect of using ",(0,r.jsx)(n.code,{children:"@Deprecated"})," is that it marks your own Kotlin code as being deprecated, which may not be what you want. Using ",(0,r.jsx)(n.code,{children:"@GraphQLDeprecated"})," you can add the ",(0,r.jsx)(n.code,{children:"@deprecated"})," directive to the GraphQL schema, but not have your Kotlin code show up as deprecated in your editor."]}),"\n",(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-kotlin",children:'class SimpleQuery {\n  @GraphQLDeprecated(message = "this query is deprecated", replaceWith = ReplaceWith("shinyNewQuery"))\n  fun simpleDeprecatedQuery(): Boolean = false\n\n  fun shinyNewQuery(): Boolean = true\n}\n'})})]})}function l(e={}){const{wrapper:n}={...(0,a.R)(),...e.components};return n?(0,r.jsx)(n,{...e,children:(0,r.jsx)(p,{...e})}):p(e)}},28453:(e,n,t)=>{t.d(n,{R:()=>c,x:()=>i});var r=t(96540);const a={},s=r.createContext(a);function c(e){const n=r.useContext(s);return r.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function i(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(a):e.components||a:c(e.components),r.createElement(s.Provider,{value:n},e.children)}}}]);