"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[7098],{82799:(e,n,t)=>{t.r(n),t.d(n,{assets:()=>s,contentTitle:()=>l,default:()=>d,frontMatter:()=>o,metadata:()=>r,toc:()=>c});var i=t(74848),a=t(28453);const o={id:"optional-undefined-arguments",title:"Optional Undefined Arguments",original_id:"optional-undefined-arguments"},l=void 0,r={id:"schema-generator/execution/optional-undefined-arguments",title:"Optional Undefined Arguments",description:"In GraphQL, input types can be optional which means that the client can either:",source:"@site/versioned_docs/version-3.x.x/schema-generator/execution/optional-undefined-arguments.md",sourceDirName:"schema-generator/execution",slug:"/schema-generator/execution/optional-undefined-arguments",permalink:"/graphql-kotlin/docs/3.x.x/schema-generator/execution/optional-undefined-arguments",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-3.x.x/schema-generator/execution/optional-undefined-arguments.md",tags:[],version:"3.x.x",lastUpdatedBy:"Daniel",lastUpdatedAt:1741819577e3,frontMatter:{id:"optional-undefined-arguments",title:"Optional Undefined Arguments",original_id:"optional-undefined-arguments"},sidebar:"docs",previous:{title:"Contextual Data",permalink:"/graphql-kotlin/docs/3.x.x/schema-generator/execution/contextual-data"},next:{title:"Subscriptions",permalink:"/graphql-kotlin/docs/3.x.x/schema-generator/execution/subscriptions"}},s={},c=[];function u(e){const n={a:"a",code:"code",li:"li",p:"p",pre:"pre",ul:"ul",...(0,a.R)(),...e.components};return(0,i.jsxs)(i.Fragment,{children:[(0,i.jsx)(n.p,{children:"In GraphQL, input types can be optional which means that the client can either:"}),"\n",(0,i.jsxs)(n.ul,{children:["\n",(0,i.jsx)(n.li,{children:"Not specify a value at all"}),"\n",(0,i.jsx)(n.li,{children:"Send null explictly"}),"\n",(0,i.jsx)(n.li,{children:"Send the non-null type"}),"\n"]}),"\n",(0,i.jsx)(n.p,{children:"Optional input types are represented as nullable parameters in Kotlin"}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-kotlin",children:"\nfun optionalInput(value: String?): String? = value\n\n"})}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-graphql",children:'\nquery OptionalInputQuery {\n  undefined: optionalInput\n  null: optionalInput(value: null)\n  foo: optionalInput(value: "foo")\n}\n\n'})}),"\n",(0,i.jsxs)(n.p,{children:["By default, if an optional input value is not specified, then the execution engine will set the argument in Kotlin to ",(0,i.jsx)(n.code,{children:"null"}),".\nThis means that you can not tell, by just the value alone, whether the request did not contain any argument or the client explicitly passed in ",(0,i.jsx)(n.code,{children:"null"}),"."]}),"\n",(0,i.jsxs)(n.p,{children:["Instead, you should inspect the ",(0,i.jsx)(n.a,{href:"/graphql-kotlin/docs/3.x.x/schema-generator/execution/data-fetching-environment",children:"DataFetchingEnvironment"})," where you can see if the request had the variable defined and even check parent arguments as well."]}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-kotlin",children:'\nfun optionalInput(value: String?, dataFetchingEnvironment: DataFetchingEnvironment): String =\n    if (dataFetchingEnvironment.containsArgument("value")) {\n        "The value was $value"\n    } else {\n        "The value was undefined"\n    }\n\n'})})]})}function d(e={}){const{wrapper:n}={...(0,a.R)(),...e.components};return n?(0,i.jsx)(n,{...e,children:(0,i.jsx)(u,{...e})}):u(e)}},28453:(e,n,t)=>{t.d(n,{R:()=>l,x:()=>r});var i=t(96540);const a={},o=i.createContext(a);function l(e){const n=i.useContext(o);return i.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function r(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(a):e.components||a:l(e.components),i.createElement(o.Provider,{value:n},e.children)}}}]);