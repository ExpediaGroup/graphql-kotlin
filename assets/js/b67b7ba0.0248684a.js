"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[1060],{44371:(e,n,t)=>{t.r(n),t.d(n,{assets:()=>c,contentTitle:()=>o,default:()=>u,frontMatter:()=>a,metadata:()=>r,toc:()=>d});var i=t(74848),s=t(28453);const a={id:"documenting-fields",title:"Documenting Schema"},o=void 0,r={id:"schema-generator/customizing-schemas/documenting-fields",title:"Documenting Schema",description:"Since Javadocs are not available at runtime for introspection, graphql-kotlin-schema-generator includes an annotation",source:"@site/versioned_docs/version-4.x.x/schema-generator/customizing-schemas/documenting-fields.md",sourceDirName:"schema-generator/customizing-schemas",slug:"/schema-generator/customizing-schemas/documenting-fields",permalink:"/graphql-kotlin/docs/4.x.x/schema-generator/customizing-schemas/documenting-fields",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-4.x.x/schema-generator/customizing-schemas/documenting-fields.md",tags:[],version:"4.x.x",lastUpdatedBy:"Dariusz Kuc",lastUpdatedAt:1708623265,formattedLastUpdatedAt:"Feb 22, 2024",frontMatter:{id:"documenting-fields",title:"Documenting Schema"},sidebar:"docs",previous:{title:"Generator Configuration & Hooks",permalink:"/graphql-kotlin/docs/4.x.x/schema-generator/customizing-schemas/generator-config"},next:{title:"Excluding Fields",permalink:"/graphql-kotlin/docs/4.x.x/schema-generator/customizing-schemas/excluding-fields"}},c={},d=[];function l(e){const n={code:"code",em:"em",p:"p",pre:"pre",...(0,s.R)(),...e.components};return(0,i.jsxs)(i.Fragment,{children:[(0,i.jsxs)(n.p,{children:["Since Javadocs are not available at runtime for introspection, ",(0,i.jsx)(n.code,{children:"graphql-kotlin-schema-generator"})," includes an annotation\nclass ",(0,i.jsx)(n.code,{children:"@GraphQLDescription"})," that can be used to add schema descriptions to ",(0,i.jsx)(n.em,{children:"any"})," GraphQL schema element. The string value can be in the Markdown format."]}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-kotlin",children:'@GraphQLDescription("A useful widget")\ndata class Widget(\n  @GraphQLDescription("The widget\'s value that can be `null`")\n  val value: Int?\n)\n\nclass WidgetQuery {\n  @GraphQLDescription("Creates new widget for given ID")\n  fun widgetById(@GraphQLDescription("The special ingredient") id: Int): Widget? = Widget(id)\n}\n'})}),"\n",(0,i.jsx)(n.p,{children:"The above query would produce the following GraphQL schema:"}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-graphql",children:'schema {\n  query: Query\n}\n\ntype Query {\n  """Creates new widget for given ID"""\n  widgetById(\n    """The special ingredient"""\n    id: Int!\n  ): Widget\n}\n\n"""A useful widget"""\ntype Widget {\n  """The widget\'s value that can be `null`"""\n  value: Int\n}\n'})})]})}function u(e={}){const{wrapper:n}={...(0,s.R)(),...e.components};return n?(0,i.jsx)(n,{...e,children:(0,i.jsx)(l,{...e})}):l(e)}},28453:(e,n,t)=>{t.d(n,{R:()=>o,x:()=>r});var i=t(96540);const s={},a=i.createContext(s);function o(e){const n=i.useContext(a);return i.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function r(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(s):e.components||s:o(e.components),i.createElement(a.Provider,{value:n},e.children)}}}]);