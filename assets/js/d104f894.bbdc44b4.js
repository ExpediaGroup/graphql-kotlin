"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[5283],{45018:(e,n,t)=>{t.r(n),t.d(n,{assets:()=>s,contentTitle:()=>o,default:()=>p,frontMatter:()=>r,metadata:()=>l,toc:()=>c});var i=t(74848),a=t(28453);const r={id:"client-features",title:"Client Features",original_id:"client-features"},o=void 0,l={id:"client/client-features",title:"Client Features",description:"Polymorphic Types\xa0Support",source:"@site/versioned_docs/version-3.x.x/client/client-features.md",sourceDirName:"client",slug:"/client/client-features",permalink:"/graphql-kotlin/docs/3.x.x/client/client-features",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-3.x.x/client/client-features.md",tags:[],version:"3.x.x",lastUpdatedBy:"Dariusz Kuc",lastUpdatedAt:1713385577,formattedLastUpdatedAt:"Apr 17, 2024",frontMatter:{id:"client-features",title:"Client Features",original_id:"client-features"},sidebar:"docs",previous:{title:"Client Overview",permalink:"/graphql-kotlin/docs/3.x.x/client/client-overview"},next:{title:"Client Customization",permalink:"/graphql-kotlin/docs/3.x.x/client/client-customization"}},s={},c=[{value:"Polymorphic Types\xa0Support",id:"polymorphic-typessupport",level:2},{value:"Default Enum\xa0Values",id:"default-enumvalues",level:2},{value:"Auto Generated Documentation",id:"auto-generated-documentation",level:2},{value:"Native Support for Coroutines",id:"native-support-for-coroutines",level:2}];function d(e){const n={a:"a",code:"code",h2:"h2",p:"p",pre:"pre",strong:"strong",...(0,a.R)(),...e.components};return(0,i.jsxs)(i.Fragment,{children:[(0,i.jsx)(n.h2,{id:"polymorphic-typessupport",children:"Polymorphic Types\xa0Support"}),"\n",(0,i.jsxs)(n.p,{children:["GraphQL supports polymorphic types through unions and interfaces which can be represented in Kotlin as marker and\nregular interfaces. In order to ensure generated objects are not empty, GraphQL queries referencing polymorphic types\nhave to ",(0,i.jsx)(n.strong,{children:"explicitly specify all implementations"}),". Polymorphic queries also have to explicitly request ",(0,i.jsx)(n.code,{children:"__typename"}),"\nfield so it can be used to Jackson correctly distinguish between different implementations."]}),"\n",(0,i.jsx)(n.p,{children:"Given example schema"}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-graphql",children:"\ntype Query {\n  interfaceQuery: BasicInterface!\n}\n\ninterface BasicInterface {\n  id: Int!\n  name: String!\n}\n\ntype FirstInterfaceImplementation implements BasicInterface {\n  id: Int!\n  intValue: Int!\n  name: String!\n}\n\ntype SecondInterfaceImplementation implements BasicInterface {\n  floatValue: Float!\n  id: Int!\n  name: String!\n}\n\n"})}),"\n",(0,i.jsx)(n.p,{children:"We can query interface field as"}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-graphql",children:"\nquery PolymorphicQuery {\n  interfaceQuery {\n    __typename\n    id\n    name\n    ... on FirstInterfaceImplementation {\n      intValue\n    }\n    ... on SecondInterfaceImplementation {\n      floatValue\n    }\n  }\n}\n\n"})}),"\n",(0,i.jsx)(n.p,{children:"Which will generate following data model"}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-kotlin",children:'\n@JsonTypeInfo(\n  use = JsonTypeInfo.Id.NAME,\n  include = JsonTypeInfo.As.PROPERTY,\n  property = "__typename"\n)\n@JsonSubTypes(value = [com.fasterxml.jackson.annotation.JsonSubTypes.Type(value =\n    PolymorphicQuery.FirstInterfaceImplementation::class,\n    name="FirstInterfaceImplementation"),com.fasterxml.jackson.annotation.JsonSubTypes.Type(value\n    = PolymorphicQuery.SecondInterfaceImplementation::class, name="SecondInterfaceImplementation")])\ninterface BasicInterface {\n  val id: Int\n  val name: String\n}\n\ndata class FirstInterfaceImplementation(\n  override val id: Int,\n  override val name: String,\n  val intValue: Int\n) : PolymorphicQuery.BasicInterface\n\ndata class SecondInterfaceImplementation(\n  override val id: Int,\n  override val name: String,\n  val floatValue: Float\n) : PolymorphicQuery.BasicInterface\n\n'})}),"\n",(0,i.jsx)(n.h2,{id:"default-enumvalues",children:"Default Enum\xa0Values"}),"\n",(0,i.jsxs)(n.p,{children:["Enums represent predefined set of values. Adding additional enum values could be a potentially breaking change as your\nclients may not be able to process it. GraphQL Kotlin Client automatically adds default ",(0,i.jsx)(n.code,{children:"@JsonEnumDefaultValue __UNKNOWN_VALUE"}),"\nto all generated enums as a catch all safeguard for handling new enum values."]}),"\n",(0,i.jsx)(n.h2,{id:"auto-generated-documentation",children:"Auto Generated Documentation"}),"\n",(0,i.jsx)(n.p,{children:"GraphQL Kotlin build plugins automatically pull in GraphQL descriptions of the queried fields from the target schema and\nadd it as KDoc to corresponding data models."}),"\n",(0,i.jsx)(n.p,{children:"Given simple GraphQL object definition"}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-graphql",children:'\n"Some basic description"\ntype BasicObject {\n  "Unique identifier"\n  id: Int!\n  "Object name"\n  name: String!\n}\n\n'})}),"\n",(0,i.jsx)(n.p,{children:"Will result in a corresponding auto generated data class"}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-kotlin",children:"\n/**\n * Some basic description\n */\ndata class BasicObject(\n  /**\n   * Unique identifier\n   */\n  val id: Int,\n  /**\n   * Object name\n   */\n  val name: String\n)\n\n"})}),"\n",(0,i.jsx)(n.h2,{id:"native-support-for-coroutines",children:"Native Support for Coroutines"}),"\n",(0,i.jsxs)(n.p,{children:["GraphQL Kotlin Client is a thin wrapper on top of ",(0,i.jsx)(n.a,{href:"https://ktor.io/clients/index.html",children:"Ktor HTTP Client"})," which provides\nfully asynchronous communication through Kotlin coroutines. ",(0,i.jsx)(n.code,{children:"GraphQLClient"})," exposes single ",(0,i.jsx)(n.code,{children:"execute"})," method that will\nsuspend your GraphQL operation until it gets a response back without blocking the underlying thread. In order to fetch\ndata asynchronously and perform some additional computations at the same time you should wrap your client execution in\n",(0,i.jsx)(n.code,{children:"launch"})," or ",(0,i.jsx)(n.code,{children:"async"})," coroutine builder and explicitly ",(0,i.jsx)(n.code,{children:"await"})," for their results."]}),"\n",(0,i.jsxs)(n.p,{children:["See ",(0,i.jsx)(n.a,{href:"https://kotlinlang.org/docs/reference/coroutines-overview.html",children:"Kotlin coroutines documentation"})," for additional details."]})]})}function p(e={}){const{wrapper:n}={...(0,a.R)(),...e.components};return n?(0,i.jsx)(n,{...e,children:(0,i.jsx)(d,{...e})}):d(e)}},28453:(e,n,t)=>{t.d(n,{R:()=>o,x:()=>l});var i=t(96540);const a={},r=i.createContext(a);function o(e){const n=i.useContext(r);return i.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function l(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(a):e.components||a:o(e.components),i.createElement(r.Provider,{value:n},e.children)}}}]);