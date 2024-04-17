"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[9916],{7461:(e,n,t)=>{t.r(n),t.d(n,{assets:()=>o,contentTitle:()=>s,default:()=>c,frontMatter:()=>l,metadata:()=>r,toc:()=>u});var a=t(74848),i=t(28453);const l={id:"arguments",title:"Arguments",original_id:"arguments"},s=void 0,r={id:"schema-generator/writing-schemas/arguments",title:"Arguments",description:"Method arguments are automatically exposed as part of the arguments to the corresponding GraphQL fields.",source:"@site/versioned_docs/version-3.x.x/schema-generator/writing-schemas/arguments.md",sourceDirName:"schema-generator/writing-schemas",slug:"/schema-generator/writing-schemas/arguments",permalink:"/graphql-kotlin/docs/3.x.x/schema-generator/writing-schemas/arguments",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-3.x.x/schema-generator/writing-schemas/arguments.md",tags:[],version:"3.x.x",lastUpdatedBy:"Dariusz Kuc",lastUpdatedAt:1713385577,formattedLastUpdatedAt:"Apr 17, 2024",frontMatter:{id:"arguments",title:"Arguments",original_id:"arguments"},sidebar:"docs",previous:{title:"Nullability",permalink:"/graphql-kotlin/docs/3.x.x/schema-generator/writing-schemas/nullability"},next:{title:"Scalars",permalink:"/graphql-kotlin/docs/3.x.x/schema-generator/writing-schemas/scalars"}},o={},u=[{value:"Input Types",id:"input-types",level:2},{value:"Optional input fields",id:"optional-input-fields",level:2},{value:"Default values",id:"default-values",level:2}];function d(e){const n={a:"a",code:"code",h2:"h2",p:"p",pre:"pre",...(0,i.R)(),...e.components};return(0,a.jsxs)(a.Fragment,{children:[(0,a.jsx)(n.p,{children:"Method arguments are automatically exposed as part of the arguments to the corresponding GraphQL fields."}),"\n",(0,a.jsx)(n.pre,{children:(0,a.jsx)(n.code,{className:"language-kotlin",children:"\nclass SimpleQuery{\n  fun doSomething(value: Int): Boolean = true\n}\n\n"})}),"\n",(0,a.jsx)(n.p,{children:"The above Kotlin code will generate following GraphQL schema:"}),"\n",(0,a.jsx)(n.pre,{children:(0,a.jsx)(n.code,{className:"language-graphql",children:"\ntype Query {\n  doSomething(value: Int!): Boolean!\n}\n\n"})}),"\n",(0,a.jsxs)(n.p,{children:["This behavior is true for all arguments except for the special classes for the ",(0,a.jsx)(n.a,{href:"../execution/contextual-data",children:"GraphQLContext"})," and the ",(0,a.jsx)(n.a,{href:"../execution/data-fetching-environment",children:"DataFetchingEnvironment"})]}),"\n",(0,a.jsx)(n.h2,{id:"input-types",children:"Input Types"}),"\n",(0,a.jsxs)(n.p,{children:["Query and mutation function arguments are automatically converted to corresponding GraphQL input fields. GraphQL makes a\ndistinction between input and output types and requires unique names for all the types. Since we can use the same\nobjects for input and output in our Kotlin functions, ",(0,a.jsx)(n.code,{children:"graphql-kotlin-schema-generator"})," will automatically append\nan ",(0,a.jsx)(n.code,{children:"Input"})," suffix to the query input objects."]}),"\n",(0,a.jsx)(n.p,{children:"For example, the following code:"}),"\n",(0,a.jsx)(n.pre,{children:(0,a.jsx)(n.code,{className:"language-kotlin",children:"\nclass WidgetMutation {\n    fun processWidget(widget: Widget): Widget {\n        if (null == widget.value) {\n            widget.value = 42\n        }\n        return widget\n    }\n}\n\ndata class Widget(var value: Int? = nul) {\n    fun multiplyValueBy(multiplier: Int) = value?.times(multiplier)\n}\n\n"})}),"\n",(0,a.jsx)(n.p,{children:"Will generate the following schema:"}),"\n",(0,a.jsx)(n.pre,{children:(0,a.jsx)(n.code,{className:"language-graphql",children:"\ntype Mutation {\n  processWidget(widget: WidgetInput!): Widget!\n}\n\ntype Widget {\n  value: Int\n  multiplyValueBy(multiplier: Int!): Int\n}\n\ninput WidgetInput {\n  value: Int\n}\n\n"})}),"\n",(0,a.jsx)(n.p,{children:"Please note that only fields are exposed in the input objects. Functions will only be available on the GraphQL output\ntypes."}),"\n",(0,a.jsxs)(n.p,{children:["If you know a type will only be used for input types you can call your class something like ",(0,a.jsx)(n.code,{children:"CustomTypeInput"}),". The library will not\nappend ",(0,a.jsx)(n.code,{children:"Input"})," if the class name already ends with ",(0,a.jsx)(n.code,{children:"Input"})," but that means you can not use this type as output because\nthe schema would have two types with the same name and that would be invalid."]}),"\n",(0,a.jsx)(n.h2,{id:"optional-input-fields",children:"Optional input fields"}),"\n",(0,a.jsx)(n.p,{children:"Kotlin requires variables/values to be initialized upon their declaration either from the user input OR by providing\ndefaults (even if they are marked as nullable). Therefore in order for a GraphQL input field to be optional it needs to be\nnullable and also specify a default Kotlin value."}),"\n",(0,a.jsx)(n.pre,{children:(0,a.jsx)(n.code,{className:"language-kotlin",children:'\nfun doSomethingWithOptionalInput(requiredValue: Int, optionalValue: Int?) = "required value=$requiredValue, optional value=$optionalValue"\n\n'})}),"\n",(0,a.jsx)(n.p,{children:"NOTE: Non nullable input fields will always require users to specify the value regardless of whether a default Kotlin value\nis provided or not."}),"\n",(0,a.jsxs)(n.p,{children:["NOTE: Even though you could specify a default values for arguments in Kotlin ",(0,a.jsx)(n.code,{children:"optionalValue: Int? = null"}),", this will not\nbe used. If query does not explicitly specify root argument values, our function data fetcher will default to use null as\nthe value. This is because Kotlin properties always have to be initialized, and we cannot determine whether underlying\nargument has default value or not. As a result, Kotlin default value will never be used. For example, with argument\n",(0,a.jsx)(n.code,{children:"optionalList: List<Int>? = emptyList()"}),", the value will be null if not passed a value by the client."]}),"\n",(0,a.jsxs)(n.p,{children:["See ",(0,a.jsx)(n.a,{href:"../execution/optional-undefined-arguments",children:"optional undefined arguments"})," for details how to determine whether argument\nwas specified or not."]}),"\n",(0,a.jsx)(n.h2,{id:"default-values",children:"Default values"}),"\n",(0,a.jsxs)(n.p,{children:["Default argument values are currently not supported. See issue ",(0,a.jsx)(n.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/issues/53",children:"#53"}),"\nfor more details."]})]})}function c(e={}){const{wrapper:n}={...(0,i.R)(),...e.components};return n?(0,a.jsx)(n,{...e,children:(0,a.jsx)(d,{...e})}):d(e)}},28453:(e,n,t)=>{t.d(n,{R:()=>s,x:()=>r});var a=t(96540);const i={},l=a.createContext(i);function s(e){const n=a.useContext(l);return a.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function r(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(i):e.components||i:s(e.components),a.createElement(l.Provider,{value:n},e.children)}}}]);