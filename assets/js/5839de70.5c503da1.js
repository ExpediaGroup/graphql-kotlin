"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[4988],{26543:(e,n,a)=>{a.r(n),a.d(n,{assets:()=>u,contentTitle:()=>s,default:()=>h,frontMatter:()=>o,metadata:()=>c,toc:()=>d});var t=a(74848),r=a(28453),i=a(11470),l=a(19365);const o={id:"client-features",title:"Client Features"},s=void 0,c={id:"client/client-features",title:"Client Features",description:"Jackson and Kotlinx Serialization Support",source:"@site/versioned_docs/version-5.x.x/client/client-features.mdx",sourceDirName:"client",slug:"/client/client-features",permalink:"/graphql-kotlin/docs/5.x.x/client/client-features",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-5.x.x/client/client-features.mdx",tags:[],version:"5.x.x",lastUpdatedBy:"Samuel Vazquez",lastUpdatedAt:172791071e4,frontMatter:{id:"client-features",title:"Client Features"},sidebar:"docs",previous:{title:"Client Overview",permalink:"/graphql-kotlin/docs/5.x.x/client/client-overview"},next:{title:"Client Customization",permalink:"/graphql-kotlin/docs/5.x.x/client/client-customization"}},u={},d=[{value:"Jackson and Kotlinx Serialization Support",id:"jackson-and-kotlinx-serialization-support",level:2},{value:"Polymorphic Types Support",id:"polymorphic-types-support",level:2},{value:"Default Enum Values",id:"default-enum-values",level:2},{value:"Auto Generated Documentation",id:"auto-generated-documentation",level:2},{value:"Native Support for Coroutines",id:"native-support-for-coroutines",level:2},{value:"Batch Operation Support",id:"batch-operation-support",level:2},{value:"Optional Input Support",id:"optional-input-support",level:2}];function p(e){const n={a:"a",code:"code",h2:"h2",p:"p",pre:"pre",strong:"strong",...(0,r.R)(),...e.components};return(0,t.jsxs)(t.Fragment,{children:[(0,t.jsx)(n.h2,{id:"jackson-and-kotlinx-serialization-support",children:"Jackson and Kotlinx Serialization Support"}),"\n",(0,t.jsxs)(n.p,{children:["GraphQL Kotlin supports generation of client data models that are compatible with both ",(0,t.jsx)(n.code,{children:"Jackson"})," (default) and ",(0,t.jsx)(n.code,{children:"kotlinx.serialization"}),"\nformats. Build plugins and ",(0,t.jsx)(n.code,{children:"graphql-kotlin-spring-client"})," default to use ",(0,t.jsx)(n.code,{children:"Jackson"})," whereas ",(0,t.jsx)(n.code,{children:"graphql-kotlin-ktor-client"}),"\ndefaults to ",(0,t.jsx)(n.code,{children:"kotlinx.serialization"}),"."]}),"\n",(0,t.jsxs)(n.p,{children:["See ",(0,t.jsx)(n.a,{href:"/graphql-kotlin/docs/5.x.x/client/client-serialization",children:"client serialization documentation"})," for additional details."]}),"\n",(0,t.jsx)(n.h2,{id:"polymorphic-types-support",children:"Polymorphic Types Support"}),"\n",(0,t.jsxs)(n.p,{children:["GraphQL supports polymorphic types through unions and interfaces which can be represented in Kotlin as marker and\nregular interfaces. In order to ensure generated objects are not empty, GraphQL queries referencing polymorphic types\nhave to ",(0,t.jsx)(n.strong,{children:"explicitly specify all implementations"}),". Polymorphic queries also have to explicitly request ",(0,t.jsx)(n.code,{children:"__typename"}),"\nfield so it can be used to Jackson correctly distinguish between different implementations."]}),"\n",(0,t.jsx)(n.p,{children:"Given example schema"}),"\n",(0,t.jsx)(n.pre,{children:(0,t.jsx)(n.code,{className:"language-graphql",children:"type Query {\n  interfaceQuery: BasicInterface!\n}\n\ninterface BasicInterface {\n  id: Int!\n  name: String!\n}\n\ntype FirstInterfaceImplementation implements BasicInterface {\n  id: Int!\n  intValue: Int!\n  name: String!\n}\n\ntype SecondInterfaceImplementation implements BasicInterface {\n  floatValue: Float!\n  id: Int!\n  name: String!\n}\n"})}),"\n",(0,t.jsx)(n.p,{children:"We can query interface field as"}),"\n",(0,t.jsx)(n.pre,{children:(0,t.jsx)(n.code,{className:"language-graphql",children:"query PolymorphicQuery {\n  interfaceQuery {\n    __typename\n    id\n    name\n    ... on FirstInterfaceImplementation {\n      intValue\n    }\n    ... on SecondInterfaceImplementation {\n      floatValue\n    }\n  }\n}\n"})}),"\n",(0,t.jsx)(n.p,{children:"Which will generate following data models"}),"\n",(0,t.jsxs)(i.A,{defaultValue:"jackson",values:[{label:"Jackson",value:"jackson"},{label:"kotlinx.serialization",value:"kotlinx"}],children:[(0,t.jsx)(l.A,{value:"jackson",children:(0,t.jsx)(n.pre,{children:(0,t.jsx)(n.code,{className:"language-kotlin",children:'@Generated\n@JsonTypeInfo(\n  use = JsonTypeInfo.Id.NAME,\n  include = JsonTypeInfo.As.PROPERTY,\n  property = "__typename"\n)\n@JsonSubTypes(value = [com.fasterxml.jackson.annotation.JsonSubTypes.Type(value =\n    FirstInterfaceImplementation::class,\n    name="FirstInterfaceImplementation"),com.fasterxml.jackson.annotation.JsonSubTypes.Type(value\n    = SecondInterfaceImplementation::class, name="SecondInterfaceImplementation")])\ninterface BasicInterface {\n  abstract val id: Int\n  abstract val name: String\n}\n\n@Generated\ndata class FirstInterfaceImplementation(\n  override val id: Int,\n  override val name: String,\n  val intValue: Int\n) : BasicInterface\n\n@Generated\ndata class SecondInterfaceImplementation(\n  override val id: Int,\n  override val name: String,\n  val floatValue: Float\n) : BasicInterface\n'})})}),(0,t.jsx)(l.A,{value:"kotlinx",children:(0,t.jsx)(n.pre,{children:(0,t.jsx)(n.code,{className:"language-kotlin",children:'@Generated\n@Serializable\nsealed class BasicInterface {\n  abstract val id: Int\n  abstract val name: String\n}\n\n@Generated\n@Serializable\n@SerialName(value = "FirstInterfaceImplementation")\ndata class FirstInterfaceImplementation(\n  override val id: Int,\n  override val name: String,\n  val intValue: Int\n) : BasicInterface()\n\n@Generated\n@Serializable\n@SerialName(value = "SecondInterfaceImplementation")\ndata class SecondInterfaceImplementation(\n  override val id: Int,\n  override val name: String,\n  val floatValue: Float\n) : BasicInterface()\n'})})})]}),"\n",(0,t.jsx)(n.h2,{id:"default-enum-values",children:"Default Enum Values"}),"\n",(0,t.jsxs)(n.p,{children:["Enums represent predefined set of values. Adding additional enum values could be a potentially breaking change as your\nclients may not be able to process it. GraphQL Kotlin Client automatically adds default ",(0,t.jsx)(n.code,{children:"__UNKNOWN_VALUE"})," to all generated\nenums as a catch all safeguard for handling new enum values."]}),"\n",(0,t.jsx)(n.h2,{id:"auto-generated-documentation",children:"Auto Generated Documentation"}),"\n",(0,t.jsx)(n.p,{children:"GraphQL Kotlin build plugins automatically pull in GraphQL descriptions of the queried fields from the target schema and\nadd it as KDoc to corresponding data models."}),"\n",(0,t.jsx)(n.p,{children:"Given simple GraphQL object definition"}),"\n",(0,t.jsx)(n.pre,{children:(0,t.jsx)(n.code,{className:"language-graphql",children:'"Some basic description"\ntype BasicObject {\n  "Unique identifier"\n  id: Int!\n  "Object name"\n  name: String!\n}\n'})}),"\n",(0,t.jsx)(n.p,{children:"Will result in a corresponding auto generated data class"}),"\n",(0,t.jsx)(n.pre,{children:(0,t.jsx)(n.code,{className:"language-kotlin",children:"/**\n * Some basic description\n */\n @Generated\ndata class BasicObject(\n  /**\n   * Unique identifier\n   */\n  val id: Int,\n  /**\n   * Object name\n   */\n  val name: String\n)\n"})}),"\n",(0,t.jsx)(n.h2,{id:"native-support-for-coroutines",children:"Native Support for Coroutines"}),"\n",(0,t.jsxs)(n.p,{children:["GraphQL Kotlin Client is a generic interface that exposes ",(0,t.jsx)(n.code,{children:"execute"})," methods that will suspend your GraphQL operation until\nit gets a response back without blocking the underlying thread. Reference Ktor and Spring WebClient based implementations\noffer fully asynchronous communication through Kotlin coroutines. In order to fetch data asynchronously you should wrap\nyour client execution in ",(0,t.jsx)(n.code,{children:"launch"})," or ",(0,t.jsx)(n.code,{children:"async"})," coroutine builder and explicitly ",(0,t.jsx)(n.code,{children:"await"})," for their results."]}),"\n",(0,t.jsxs)(n.p,{children:["See ",(0,t.jsx)(n.a,{href:"https://kotlinlang.org/docs/reference/coroutines-overview.html",children:"Kotlin coroutines documentation"})," for additional details."]}),"\n",(0,t.jsx)(n.h2,{id:"batch-operation-support",children:"Batch Operation Support"}),"\n",(0,t.jsx)(n.p,{children:"GraphQL Kotlin Clients provide out of the box support for batching multiple client operations into a single GraphQL request.\nBatch requests are sent as an array of individual GraphQL requests and clients expect the server to respond with a corresponding\narray of GraphQL responses. Each response is then deserialized to a corresponding result type."}),"\n",(0,t.jsx)(n.pre,{children:(0,t.jsx)(n.code,{className:"language-kotlin",children:'val client = GraphQLKtorClient(url = URL("http://localhost:8080/graphql"))\nval firstQuery = FirstQuery(variables = FirstQuery.Variables(foo = "bar"))\nval secondQuery = SecondQuery(variables = SecondQuery.Variables(foo = "baz"))\n\nval results: List<GraphQLResponse<*>> = client.execute(listOf(firstQuery, secondQuery))\n'})}),"\n",(0,t.jsx)(n.h2,{id:"optional-input-support",children:"Optional Input Support"}),"\n",(0,t.jsxs)(n.p,{children:["In the GraphQL world, input types can be optional which means that the client can specify a value, specify a ",(0,t.jsx)(n.code,{children:"null"})," value\nOR don't specify any value. This is in contrast with the JVM world where objects can either have some specific value or\ndon't have any value (i.e. are ",(0,t.jsx)(n.code,{children:"null"}),"). By default, GraphQL Kotlin Client treats ",(0,t.jsx)(n.code,{children:"null"})," Kotlin values as unspecified, which\nmeans they will skip all ",(0,t.jsx)(n.code,{children:"null"})," values when serializing the request, e.g. given following query"]}),"\n",(0,t.jsx)(n.pre,{children:(0,t.jsx)(n.code,{className:"language-graphql",children:"query OptionalInputQuery($optionalValue: String) {\n  optional(value: $optionalValue)\n"})}),"\n",(0,t.jsxs)(n.p,{children:["GraphQL Kotlin plugins will generate corresponding POJO that defines ",(0,t.jsx)(n.code,{children:"Variables"})," as"]}),"\n",(0,t.jsx)(n.pre,{children:(0,t.jsx)(n.code,{className:"language-kotlin",children:"public data class Variables(\n  public val optionalValue: String? = null\n)\n"})}),"\n",(0,t.jsxs)(n.p,{children:["Regardless whether we specify ",(0,t.jsx)(n.code,{children:"optionalValue"})," as ",(0,t.jsx)(n.code,{children:"null"})," or rely on the default value, this field won't be serialized,\ni.e. variables will be serialized as an empty JSON object ",(0,t.jsx)(n.code,{children:"{}"}),"."]}),"\n",(0,t.jsxs)(n.p,{children:["By specifying ",(0,t.jsx)(n.code,{children:"useOptionalInputWrapper = true"})," plugin configuration option, we can opt-in to a behavior that supports\nthree states - defined, ",(0,t.jsx)(n.code,{children:"null"})," or undefined. Generated code will then use ",(0,t.jsx)(n.code,{children:"OptionalInput"})," wrapper to represent those states.\nSee ",(0,t.jsx)(n.a,{href:"../plugins/gradle-plugin-tasks",children:"Gradle"})," and ",(0,t.jsx)(n.a,{href:"../plugins/maven-plugin-goals",children:"Maven"})," plugin for configuration details."]}),"\n",(0,t.jsx)(n.pre,{children:(0,t.jsx)(n.code,{className:"language-kotlin",children:'public data class Variables(\n  public val optionalValue: OptionalInput<String> = OptionalInput.Undefined\n)\n\n// usage\n// - same behavior as default null, serialized as {}\nval undefinedVariables = Variables(optionalValue = OptionalInput.Undefined)\n\n// - serialized as {"optionalValue": null}\nval nullVariables = Variables(optionalValue = OptionalInput.Defined(null))\n\n// - serialized as {"optionalValue": "foo"}\nval definedVariables = Variables(optionalValue = OptionalInput.Defined("foo")\n'})})]})}function h(e={}){const{wrapper:n}={...(0,r.R)(),...e.components};return n?(0,t.jsx)(n,{...e,children:(0,t.jsx)(p,{...e})}):p(e)}},19365:(e,n,a)=>{a.d(n,{A:()=>l});a(96540);var t=a(34164);const r={tabItem:"tabItem_Ymn6"};var i=a(74848);function l(e){var n=e.children,a=e.hidden,l=e.className;return(0,i.jsx)("div",{role:"tabpanel",className:(0,t.A)(r.tabItem,l),hidden:a,children:n})}},11470:(e,n,a)=>{a.d(n,{A:()=>I});var t=a(96540),r=a(34164),i=a(23104),l=a(56347),o=a(205),s=a(57485),c=a(31682),u=a(70679);function d(e){var n,a;return null!=(n=null==(a=t.Children.toArray(e).filter((function(e){return"\n"!==e})).map((function(e){if(!e||(0,t.isValidElement)(e)&&((n=e.props)&&"object"==typeof n&&"value"in n))return e;var n;throw new Error("Docusaurus error: Bad <Tabs> child <"+("string"==typeof e.type?e.type:e.type.name)+'>: all children of the <Tabs> component should be <TabItem>, and every <TabItem> should have a unique "value" prop.')})))?void 0:a.filter(Boolean))?n:[]}function p(e){var n=e.values,a=e.children;return(0,t.useMemo)((function(){var e=null!=n?n:function(e){return d(e).map((function(e){var n=e.props;return{value:n.value,label:n.label,attributes:n.attributes,default:n.default}}))}(a);return function(e){var n=(0,c.XI)(e,(function(e,n){return e.value===n.value}));if(n.length>0)throw new Error('Docusaurus error: Duplicate values "'+n.map((function(e){return e.value})).join(", ")+'" found in <Tabs>. Every value needs to be unique.')}(e),e}),[n,a])}function h(e){var n=e.value;return e.tabValues.some((function(e){return e.value===n}))}function f(e){var n=e.queryString,a=void 0!==n&&n,r=e.groupId,i=(0,l.W6)(),o=function(e){var n=e.queryString,a=void 0!==n&&n,t=e.groupId;if("string"==typeof a)return a;if(!1===a)return null;if(!0===a&&!t)throw new Error('Docusaurus error: The <Tabs> component groupId prop is required if queryString=true, because this value is used as the search param name. You can also provide an explicit value such as queryString="my-search-param".');return null!=t?t:null}({queryString:a,groupId:r});return[(0,s.aZ)(o),(0,t.useCallback)((function(e){if(o){var n=new URLSearchParams(i.location.search);n.set(o,e),i.replace(Object.assign({},i.location,{search:n.toString()}))}}),[o,i])]}function m(e){var n,a,r,i,l=e.defaultValue,s=e.queryString,c=void 0!==s&&s,d=e.groupId,m=p(e),v=(0,t.useState)((function(){return function(e){var n,a=e.defaultValue,t=e.tabValues;if(0===t.length)throw new Error("Docusaurus error: the <Tabs> component requires at least one <TabItem> children component");if(a){if(!h({value:a,tabValues:t}))throw new Error('Docusaurus error: The <Tabs> has a defaultValue "'+a+'" but none of its children has the corresponding value. Available values are: '+t.map((function(e){return e.value})).join(", ")+". If you intend to show no default tab, use defaultValue={null} instead.");return a}var r=null!=(n=t.find((function(e){return e.default})))?n:t[0];if(!r)throw new Error("Unexpected error: 0 tabValues");return r.value}({defaultValue:l,tabValues:m})})),g=v[0],x=v[1],b=f({queryString:c,groupId:d}),y=b[0],j=b[1],I=(n=function(e){return e?"docusaurus.tab."+e:null}({groupId:d}.groupId),a=(0,u.Dv)(n),r=a[0],i=a[1],[r,(0,t.useCallback)((function(e){n&&i.set(e)}),[n,i])]),S=I[0],k=I[1],w=function(){var e=null!=y?y:S;return h({value:e,tabValues:m})?e:null}();return(0,o.A)((function(){w&&x(w)}),[w]),{selectedValue:g,selectValue:(0,t.useCallback)((function(e){if(!h({value:e,tabValues:m}))throw new Error("Can't select invalid tab value="+e);x(e),j(e),k(e)}),[j,k,m]),tabValues:m}}var v=a(92303);const g={tabList:"tabList__CuJ",tabItem:"tabItem_LNqP"};var x=a(74848);function b(e){var n=e.className,a=e.block,t=e.selectedValue,l=e.selectValue,o=e.tabValues,s=[],c=(0,i.a_)().blockElementScrollPositionUntilNextRender,u=function(e){var n=e.currentTarget,a=s.indexOf(n),r=o[a].value;r!==t&&(c(n),l(r))},d=function(e){var n,a=null;switch(e.key){case"Enter":u(e);break;case"ArrowRight":var t,r=s.indexOf(e.currentTarget)+1;a=null!=(t=s[r])?t:s[0];break;case"ArrowLeft":var i,l=s.indexOf(e.currentTarget)-1;a=null!=(i=s[l])?i:s[s.length-1]}null==(n=a)||n.focus()};return(0,x.jsx)("ul",{role:"tablist","aria-orientation":"horizontal",className:(0,r.A)("tabs",{"tabs--block":a},n),children:o.map((function(e){var n=e.value,a=e.label,i=e.attributes;return(0,x.jsx)("li",Object.assign({role:"tab",tabIndex:t===n?0:-1,"aria-selected":t===n,ref:function(e){return s.push(e)},onKeyDown:d,onClick:u},i,{className:(0,r.A)("tabs__item",g.tabItem,null==i?void 0:i.className,{"tabs__item--active":t===n}),children:null!=a?a:n}),n)}))})}function y(e){var n=e.lazy,a=e.children,i=e.selectedValue,l=(Array.isArray(a)?a:[a]).filter(Boolean);if(n){var o=l.find((function(e){return e.props.value===i}));return o?(0,t.cloneElement)(o,{className:(0,r.A)("margin-top--md",o.props.className)}):null}return(0,x.jsx)("div",{className:"margin-top--md",children:l.map((function(e,n){return(0,t.cloneElement)(e,{key:n,hidden:e.props.value!==i})}))})}function j(e){var n=m(e);return(0,x.jsxs)("div",{className:(0,r.A)("tabs-container",g.tabList),children:[(0,x.jsx)(b,Object.assign({},n,e)),(0,x.jsx)(y,Object.assign({},n,e))]})}function I(e){var n=(0,v.A)();return(0,x.jsx)(j,Object.assign({},e,{children:d(e.children)}),String(n))}},28453:(e,n,a)=>{a.d(n,{R:()=>l,x:()=>o});var t=a(96540);const r={},i=t.createContext(r);function l(e){const n=t.useContext(i);return t.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function o(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(r):e.components||r:l(e.components),t.createElement(i.Provider,{value:n},e.children)}}}]);