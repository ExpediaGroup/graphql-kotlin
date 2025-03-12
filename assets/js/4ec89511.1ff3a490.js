"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[3883],{51322:(e,n,t)=>{t.r(n),t.d(n,{assets:()=>r,contentTitle:()=>o,default:()=>m,frontMatter:()=>s,metadata:()=>c,toc:()=>d});var a=t(74848),i=t(28453);const s={id:"documenting-schema",title:"Documenting Schema"},o=void 0,c={id:"schema-generator/customizing-schemas/documenting-schema",title:"Documenting Schema",description:"Since Javadocs are not available at runtime for introspection, graphql-kotlin-schema-generator includes an annotation",source:"@site/versioned_docs/version-8.x.x/schema-generator/customizing-schemas/documenting-schema.md",sourceDirName:"schema-generator/customizing-schemas",slug:"/schema-generator/customizing-schemas/documenting-schema",permalink:"/graphql-kotlin/docs/schema-generator/customizing-schemas/documenting-schema",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-8.x.x/schema-generator/customizing-schemas/documenting-schema.md",tags:[],version:"8.x.x",lastUpdatedBy:"Daniel",lastUpdatedAt:1741819577e3,frontMatter:{id:"documenting-schema",title:"Documenting Schema"},sidebar:"docs",previous:{title:"Generator Configuration & Hooks",permalink:"/graphql-kotlin/docs/schema-generator/customizing-schemas/generator-config"},next:{title:"Excluding Fields",permalink:"/graphql-kotlin/docs/schema-generator/customizing-schemas/excluding-fields"}},r={},d=[];function l(e){const n={code:"code",em:"em",p:"p",pre:"pre",...(0,i.R)(),...e.components};return(0,a.jsxs)(a.Fragment,{children:[(0,a.jsxs)(n.p,{children:["Since Javadocs are not available at runtime for introspection, ",(0,a.jsx)(n.code,{children:"graphql-kotlin-schema-generator"})," includes an annotation\nclass ",(0,a.jsx)(n.code,{children:"@GraphQLDescription"})," that can be used to add schema descriptions to ",(0,a.jsx)(n.em,{children:"any"})," GraphQL schema element. The string value can be in the Markdown format."]}),"\n",(0,a.jsx)(n.pre,{children:(0,a.jsx)(n.code,{className:"language-kotlin",children:'@GraphQLDescription("A useful widget")\ndata class Widget(\n  @GraphQLDescription("The widget\'s value that can be `null`")\n  val value: Int?\n)\n\nclass WidgetQuery {\n  @GraphQLDescription("Creates new widget for given ID")\n  fun widgetById(@GraphQLDescription("The special ingredient") id: Int): Widget? = Widget(id)\n}\n'})}),"\n",(0,a.jsx)(n.p,{children:"The above query would produce the following GraphQL schema:"}),"\n",(0,a.jsx)(n.pre,{children:(0,a.jsx)(n.code,{className:"language-graphql",children:'schema {\n  query: Query\n}\n\ntype Query {\n  """Creates new widget for given ID"""\n  widgetById(\n    """The special ingredient"""\n    id: Int!\n  ): Widget\n}\n\n"""A useful widget"""\ntype Widget {\n  """The widget\'s value that can be `null`"""\n  value: Int\n}\n'})})]})}function m(e={}){const{wrapper:n}={...(0,i.R)(),...e.components};return n?(0,a.jsx)(n,{...e,children:(0,a.jsx)(l,{...e})}):l(e)}},28453:(e,n,t)=>{t.d(n,{R:()=>o,x:()=>c});var a=t(96540);const i={},s=a.createContext(i);function o(e){const n=a.useContext(s);return a.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function c(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(i):e.components||i:o(e.components),a.createElement(s.Provider,{value:n},e.children)}}}]);