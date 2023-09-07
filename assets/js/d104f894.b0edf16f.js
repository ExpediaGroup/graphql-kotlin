"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[5668],{3905:(e,n,t)=>{t.d(n,{Zo:()=>s,kt:()=>f});var a=t(67294);function r(e,n,t){return n in e?Object.defineProperty(e,n,{value:t,enumerable:!0,configurable:!0,writable:!0}):e[n]=t,e}function i(e,n){var t=Object.keys(e);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);n&&(a=a.filter((function(n){return Object.getOwnPropertyDescriptor(e,n).enumerable}))),t.push.apply(t,a)}return t}function o(e){for(var n=1;n<arguments.length;n++){var t=null!=arguments[n]?arguments[n]:{};n%2?i(Object(t),!0).forEach((function(n){r(e,n,t[n])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(t)):i(Object(t)).forEach((function(n){Object.defineProperty(e,n,Object.getOwnPropertyDescriptor(t,n))}))}return e}function l(e,n){if(null==e)return{};var t,a,r=function(e,n){if(null==e)return{};var t,a,r={},i=Object.keys(e);for(a=0;a<i.length;a++)t=i[a],n.indexOf(t)>=0||(r[t]=e[t]);return r}(e,n);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(a=0;a<i.length;a++)t=i[a],n.indexOf(t)>=0||Object.prototype.propertyIsEnumerable.call(e,t)&&(r[t]=e[t])}return r}var c=a.createContext({}),p=function(e){var n=a.useContext(c),t=n;return e&&(t="function"==typeof e?e(n):o(o({},n),e)),t},s=function(e){var n=p(e.components);return a.createElement(c.Provider,{value:n},e.children)},u="mdxType",d={inlineCode:"code",wrapper:function(e){var n=e.children;return a.createElement(a.Fragment,{},n)}},m=a.forwardRef((function(e,n){var t=e.components,r=e.mdxType,i=e.originalType,c=e.parentName,s=l(e,["components","mdxType","originalType","parentName"]),u=p(t),m=r,f=u["".concat(c,".").concat(m)]||u[m]||d[m]||i;return t?a.createElement(f,o(o({ref:n},s),{},{components:t})):a.createElement(f,o({ref:n},s))}));function f(e,n){var t=arguments,r=n&&n.mdxType;if("string"==typeof e||r){var i=t.length,o=new Array(i);o[0]=m;var l={};for(var c in n)hasOwnProperty.call(n,c)&&(l[c]=n[c]);l.originalType=e,l[u]="string"==typeof e?e:r,o[1]=l;for(var p=2;p<i;p++)o[p]=t[p];return a.createElement.apply(null,o)}return a.createElement.apply(null,t)}m.displayName="MDXCreateElement"},53818:(e,n,t)=>{t.r(n),t.d(n,{assets:()=>s,contentTitle:()=>c,default:()=>f,frontMatter:()=>l,metadata:()=>p,toc:()=>u});var a=t(87462),r=t(63366),i=(t(67294),t(3905)),o=["components"],l={id:"client-features",title:"Client Features",original_id:"client-features"},c=void 0,p={unversionedId:"client/client-features",id:"version-3.x.x/client/client-features",title:"Client Features",description:"Polymorphic Types\xa0Support",source:"@site/versioned_docs/version-3.x.x/client/client-features.md",sourceDirName:"client",slug:"/client/client-features",permalink:"/graphql-kotlin/docs/3.x.x/client/client-features",draft:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-3.x.x/client/client-features.md",tags:[],version:"3.x.x",lastUpdatedBy:"Dariusz Kuc",lastUpdatedAt:1694122111,formattedLastUpdatedAt:"Sep 7, 2023",frontMatter:{id:"client-features",title:"Client Features",original_id:"client-features"},sidebar:"version-3.x.x/docs",previous:{title:"Client Overview",permalink:"/graphql-kotlin/docs/3.x.x/client/client-overview"},next:{title:"Client Customization",permalink:"/graphql-kotlin/docs/3.x.x/client/client-customization"}},s={},u=[{value:"Polymorphic Types\xa0Support",id:"polymorphic-typessupport",level:2},{value:"Default Enum\xa0Values",id:"default-enumvalues",level:2},{value:"Auto Generated Documentation",id:"auto-generated-documentation",level:2},{value:"Native Support for Coroutines",id:"native-support-for-coroutines",level:2}],d={toc:u},m="wrapper";function f(e){var n=e.components,t=(0,r.Z)(e,o);return(0,i.kt)(m,(0,a.Z)({},d,t,{components:n,mdxType:"MDXLayout"}),(0,i.kt)("h2",{id:"polymorphic-typessupport"},"Polymorphic Types\xa0Support"),(0,i.kt)("p",null,"GraphQL supports polymorphic types through unions and interfaces which can be represented in Kotlin as marker and\nregular interfaces. In order to ensure generated objects are not empty, GraphQL queries referencing polymorphic types\nhave to ",(0,i.kt)("strong",{parentName:"p"},"explicitly specify all implementations"),". Polymorphic queries also have to explicitly request ",(0,i.kt)("inlineCode",{parentName:"p"},"__typename"),"\nfield so it can be used to Jackson correctly distinguish between different implementations."),(0,i.kt)("p",null,"Given example schema"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-graphql"},"\ntype Query {\n  interfaceQuery: BasicInterface!\n}\n\ninterface BasicInterface {\n  id: Int!\n  name: String!\n}\n\ntype FirstInterfaceImplementation implements BasicInterface {\n  id: Int!\n  intValue: Int!\n  name: String!\n}\n\ntype SecondInterfaceImplementation implements BasicInterface {\n  floatValue: Float!\n  id: Int!\n  name: String!\n}\n\n")),(0,i.kt)("p",null,"We can query interface field as"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-graphql"},"\nquery PolymorphicQuery {\n  interfaceQuery {\n    __typename\n    id\n    name\n    ... on FirstInterfaceImplementation {\n      intValue\n    }\n    ... on SecondInterfaceImplementation {\n      floatValue\n    }\n  }\n}\n\n")),(0,i.kt)("p",null,"Which will generate following data model"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-kotlin"},'\n@JsonTypeInfo(\n  use = JsonTypeInfo.Id.NAME,\n  include = JsonTypeInfo.As.PROPERTY,\n  property = "__typename"\n)\n@JsonSubTypes(value = [com.fasterxml.jackson.annotation.JsonSubTypes.Type(value =\n    PolymorphicQuery.FirstInterfaceImplementation::class,\n    name="FirstInterfaceImplementation"),com.fasterxml.jackson.annotation.JsonSubTypes.Type(value\n    = PolymorphicQuery.SecondInterfaceImplementation::class, name="SecondInterfaceImplementation")])\ninterface BasicInterface {\n  val id: Int\n  val name: String\n}\n\ndata class FirstInterfaceImplementation(\n  override val id: Int,\n  override val name: String,\n  val intValue: Int\n) : PolymorphicQuery.BasicInterface\n\ndata class SecondInterfaceImplementation(\n  override val id: Int,\n  override val name: String,\n  val floatValue: Float\n) : PolymorphicQuery.BasicInterface\n\n')),(0,i.kt)("h2",{id:"default-enumvalues"},"Default Enum\xa0Values"),(0,i.kt)("p",null,"Enums represent predefined set of values. Adding additional enum values could be a potentially breaking change as your\nclients may not be able to process it. GraphQL Kotlin Client automatically adds default ",(0,i.kt)("inlineCode",{parentName:"p"},"@JsonEnumDefaultValue __UNKNOWN_VALUE"),"\nto all generated enums as a catch all safeguard for handling new enum values."),(0,i.kt)("h2",{id:"auto-generated-documentation"},"Auto Generated Documentation"),(0,i.kt)("p",null,"GraphQL Kotlin build plugins automatically pull in GraphQL descriptions of the queried fields from the target schema and\nadd it as KDoc to corresponding data models."),(0,i.kt)("p",null,"Given simple GraphQL object definition"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-graphql"},'\n"Some basic description"\ntype BasicObject {\n  "Unique identifier"\n  id: Int!\n  "Object name"\n  name: String!\n}\n\n')),(0,i.kt)("p",null,"Will result in a corresponding auto generated data class"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-kotlin"},"\n/**\n * Some basic description\n */\ndata class BasicObject(\n  /**\n   * Unique identifier\n   */\n  val id: Int,\n  /**\n   * Object name\n   */\n  val name: String\n)\n\n")),(0,i.kt)("h2",{id:"native-support-for-coroutines"},"Native Support for Coroutines"),(0,i.kt)("p",null,"GraphQL Kotlin Client is a thin wrapper on top of ",(0,i.kt)("a",{parentName:"p",href:"https://ktor.io/clients/index.html"},"Ktor HTTP Client")," which provides\nfully asynchronous communication through Kotlin coroutines. ",(0,i.kt)("inlineCode",{parentName:"p"},"GraphQLClient")," exposes single ",(0,i.kt)("inlineCode",{parentName:"p"},"execute")," method that will\nsuspend your GraphQL operation until it gets a response back without blocking the underlying thread. In order to fetch\ndata asynchronously and perform some additional computations at the same time you should wrap your client execution in\n",(0,i.kt)("inlineCode",{parentName:"p"},"launch")," or ",(0,i.kt)("inlineCode",{parentName:"p"},"async")," coroutine builder and explicitly ",(0,i.kt)("inlineCode",{parentName:"p"},"await")," for their results."),(0,i.kt)("p",null,"See ",(0,i.kt)("a",{parentName:"p",href:"https://kotlinlang.org/docs/reference/coroutines-overview.html"},"Kotlin coroutines documentation")," for additional details."))}f.isMDXComponent=!0}}]);