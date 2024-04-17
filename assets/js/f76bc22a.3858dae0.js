"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[2354],{49701:(t,e,n)=>{n.r(e),n.d(e,{assets:()=>r,contentTitle:()=>o,default:()=>l,frontMatter:()=>i,metadata:()=>u,toc:()=>c});var s=n(74848),a=n(28453);const i={id:"restricting-input-output",title:"Restricting Input and Output Types"},o=void 0,u={id:"schema-generator/customizing-schemas/restricting-input-output",title:"Restricting Input and Output Types",description:"Since we are using Kotlin classes to represent both GraphQL input and output objects we can use the same class for both and the generator will handle type conflicts.",source:"@site/docs/schema-generator/customizing-schemas/restricting-input-output.md",sourceDirName:"schema-generator/customizing-schemas",slug:"/schema-generator/customizing-schemas/restricting-input-output",permalink:"/graphql-kotlin/docs/8.x.x/schema-generator/customizing-schemas/restricting-input-output",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/docs/schema-generator/customizing-schemas/restricting-input-output.md",tags:[],version:"current",lastUpdatedBy:"Dariusz Kuc",lastUpdatedAt:1713385577,formattedLastUpdatedAt:"Apr 17, 2024",frontMatter:{id:"restricting-input-output",title:"Restricting Input and Output Types"},sidebar:"docs",previous:{title:"Custom Types",permalink:"/graphql-kotlin/docs/8.x.x/schema-generator/customizing-schemas/custom-type-reference"},next:{title:"Advanced Features",permalink:"/graphql-kotlin/docs/8.x.x/schema-generator/customizing-schemas/advanced-features"}},r={},c=[];function p(t){const e={code:"code",p:"p",pre:"pre",...(0,a.R)(),...t.components};return(0,s.jsxs)(s.Fragment,{children:[(0,s.jsx)(e.p,{children:"Since we are using Kotlin classes to represent both GraphQL input and output objects we can use the same class for both and the generator will handle type conflicts."}),"\n",(0,s.jsxs)(e.p,{children:["If you want to enforce that a type should never be used as an input or output you can use the ",(0,s.jsx)(e.code,{children:"@GraphQLValidObjectLocations"})," annotation.\nIf the class was used in the schema in an invalid location an exception will be thrown."]}),"\n",(0,s.jsx)(e.pre,{children:(0,s.jsx)(e.code,{className:"language-kotlin",children:'class SimpleClass(val value: String)\n\n@GraphQLValidObjectLocations([Locations.INPUT_OBJECT])\nclass InputOnly(val value: String)\n\n@GraphQLValidObjectLocations([Locations.OBJECT])\nclass OutputOnly(val value: String)\n\n// Valid Usage\nfun output1() = SimpleClass("foo")\nfun output2() = OutputOnly("foo")\nfun input1(input: SimpleClass) = "value was ${input.value}"\nfun input2(input: InputOnly) = "value was ${input.value}"\n\n// Throws Exception\nfun output3() = InputOnly("foo")\nfun input3(input: OutputOnly) = "value was ${input.value}"\n'})})]})}function l(t={}){const{wrapper:e}={...(0,a.R)(),...t.components};return e?(0,s.jsx)(e,{...t,children:(0,s.jsx)(p,{...t})}):p(t)}},28453:(t,e,n)=>{n.d(e,{R:()=>o,x:()=>u});var s=n(96540);const a={},i=s.createContext(a);function o(t){const e=s.useContext(i);return s.useMemo((function(){return"function"==typeof t?t(e):{...e,...t}}),[e,t])}function u(t){let e;return e=t.disableParentContext?"function"==typeof t.components?t.components(a):t.components||a:o(t.components),s.createElement(i.Provider,{value:e},t.children)}}}]);