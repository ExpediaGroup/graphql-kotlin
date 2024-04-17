"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[8042],{67657:(n,e,t)=>{t.r(e),t.d(e,{assets:()=>r,contentTitle:()=>o,default:()=>c,frontMatter:()=>l,metadata:()=>s,toc:()=>u});var i=t(74848),a=t(28453);const l={id:"optional-undefined-arguments",title:"Optional Undefined Arguments"},o=void 0,s={id:"schema-generator/execution/optional-undefined-arguments",title:"Optional Undefined Arguments",description:"In the GraphQL world, input types can be optional which means that the client can either:",source:"@site/versioned_docs/version-4.x.x/schema-generator/execution/optional-undefined-arguments.md",sourceDirName:"schema-generator/execution",slug:"/schema-generator/execution/optional-undefined-arguments",permalink:"/graphql-kotlin/docs/4.x.x/schema-generator/execution/optional-undefined-arguments",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-4.x.x/schema-generator/execution/optional-undefined-arguments.md",tags:[],version:"4.x.x",lastUpdatedBy:"Dariusz Kuc",lastUpdatedAt:1713385577,formattedLastUpdatedAt:"Apr 17, 2024",frontMatter:{id:"optional-undefined-arguments",title:"Optional Undefined Arguments"},sidebar:"docs",previous:{title:"Contextual Data",permalink:"/graphql-kotlin/docs/4.x.x/schema-generator/execution/contextual-data"},next:{title:"Subscriptions",permalink:"/graphql-kotlin/docs/4.x.x/schema-generator/execution/subscriptions"}},r={},u=[{value:"Using OptionalInput wrapper",id:"using-optionalinput-wrapper",level:2},{value:"Using DataFetchingEnvironment",id:"using-datafetchingenvironment",level:2},{value:"Kotlin Default Values",id:"kotlin-default-values",level:2}];function d(n){const e={a:"a",admonition:"admonition",code:"code",h2:"h2",li:"li",p:"p",pre:"pre",ul:"ul",...(0,a.R)(),...n.components};return(0,i.jsxs)(i.Fragment,{children:[(0,i.jsx)(e.p,{children:"In the GraphQL world, input types can be optional which means that the client can either:"}),"\n",(0,i.jsxs)(e.ul,{children:["\n",(0,i.jsx)(e.li,{children:"Not specify a value at all"}),"\n",(0,i.jsx)(e.li,{children:"Send null explicitly"}),"\n",(0,i.jsx)(e.li,{children:"Send a non-null value"}),"\n"]}),"\n",(0,i.jsxs)(e.p,{children:["This is in contrast with the JVM world where objects can either have some specific value or don't have any value (i.e.\nare ",(0,i.jsx)(e.code,{children:"null"}),"). As a result, when using default serialization logic it is not possible to distinguish between missing/unspecified\nvalue and explicit ",(0,i.jsx)(e.code,{children:"null"})," value."]}),"\n",(0,i.jsx)(e.h2,{id:"using-optionalinput-wrapper",children:"Using OptionalInput wrapper"}),"\n",(0,i.jsxs)(e.p,{children:[(0,i.jsx)(e.code,{children:"OptionalInput"})," is a convenient sealed class wrapper that provides distinction between undefined, null, and non-null\nvalues. If the argument is not specified in the request it will be represented as a ",(0,i.jsx)(e.code,{children:"OptionalInput.Undefined"})," object, otherwise the\nvalue will be wrapped in ",(0,i.jsx)(e.code,{children:"OptionalInput.Defined"})," class."]}),"\n",(0,i.jsx)(e.pre,{children:(0,i.jsx)(e.code,{className:"language-kotlin",children:'fun optionalInput(input: OptionalInput<String>): String = when (input) {\n    is OptionalInput.Undefined -> "input was not specified"\n    is OptionalInput.Defined<String> -> "input value: ${input.value}"\n}\n'})}),"\n",(0,i.jsx)(e.pre,{children:(0,i.jsx)(e.code,{className:"language-graphql",children:'query OptionalInputQuery {\n  undefined: optionalInput # input was not specified\n  null: optionalInput(value: null) # input value: null\n  foo: optionalInput(value: "foo") # input value: foo\n}\n'})}),"\n",(0,i.jsx)(e.admonition,{type:"info",children:(0,i.jsxs)(e.p,{children:["Regardless whether the generic type of ",(0,i.jsx)(e.code,{children:"OptionalInput"})," is specified as nullable or not it will always result in a nullable\nvalue in ",(0,i.jsx)(e.code,{children:"Defined"})," class, i.e. ",(0,i.jsx)(e.code,{children:"OptionalInput<String>"})," will appear as nullable ",(0,i.jsx)(e.code,{children:"String"})," in the GraphQL schema and in the wrapped value."]})}),"\n",(0,i.jsx)(e.h2,{id:"using-datafetchingenvironment",children:"Using DataFetchingEnvironment"}),"\n",(0,i.jsx)(e.p,{children:"Optional input types can be represented as nullable parameters in Kotlin"}),"\n",(0,i.jsx)(e.pre,{children:(0,i.jsx)(e.code,{className:"language-kotlin",children:"fun optionalInput(value: String?): String? = value\n"})}),"\n",(0,i.jsx)(e.pre,{children:(0,i.jsx)(e.code,{className:"language-graphql",children:'query OptionalInputQuery {\n  undefined: optionalInput # null\n  null: optionalInput(value: null) # null\n  foo: optionalInput(value: "foo") # foo\n}\n'})}),"\n",(0,i.jsxs)(e.p,{children:["By default, if an optional input value is not specified, then the execution engine will set the argument in Kotlin to ",(0,i.jsx)(e.code,{children:"null"}),".\nThis means that you can not tell, by just the value alone, whether the request did not contain any argument or the client\nexplicitly passed in ",(0,i.jsx)(e.code,{children:"null"}),"."]}),"\n",(0,i.jsxs)(e.p,{children:["Instead, you can inspect all passed in arguments using the ",(0,i.jsx)(e.a,{href:"/graphql-kotlin/docs/4.x.x/schema-generator/execution/data-fetching-environment",children:"DataFetchingEnvironment"}),"."]}),"\n",(0,i.jsx)(e.pre,{children:(0,i.jsx)(e.code,{className:"language-kotlin",children:'fun optionalInput(value: String?, dataFetchingEnvironment: DataFetchingEnvironment): String =\n    if (dataFetchingEnvironment.containsArgument("value")) {\n        "The value was $value"\n    } else {\n        "The value was undefined"\n    }\n'})}),"\n",(0,i.jsx)(e.h2,{id:"kotlin-default-values",children:"Kotlin Default Values"}),"\n",(0,i.jsxs)(e.p,{children:["If you don't need logic for when the client specified a value, you can still use ",(0,i.jsx)(e.a,{href:"/graphql-kotlin/docs/4.x.x/schema-generator/writing-schemas/arguments",children:"Kotlin default values"})]})]})}function c(n={}){const{wrapper:e}={...(0,a.R)(),...n.components};return e?(0,i.jsx)(e,{...n,children:(0,i.jsx)(d,{...n})}):d(n)}},28453:(n,e,t)=>{t.d(e,{R:()=>o,x:()=>s});var i=t(96540);const a={},l=i.createContext(a);function o(n){const e=i.useContext(l);return i.useMemo((function(){return"function"==typeof n?n(e):{...e,...n}}),[e,n])}function s(n){let e;return e=n.disableParentContext?"function"==typeof n.components?n.components(a):n.components||a:o(n.components),i.createElement(l.Provider,{value:e},n.children)}}}]);