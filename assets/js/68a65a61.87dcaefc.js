"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[3219],{85162:(e,n,t)=>{t.d(n,{Z:()=>i});var a=t(67294),r=t(86010);const l={tabItem:"tabItem_Ymn6"};function i(e){var n=e.children,t=e.hidden,i=e.className;return a.createElement("div",{role:"tabpanel",className:(0,r.Z)(l.tabItem,i),hidden:t},n)}},74866:(e,n,t)=>{t.d(n,{Z:()=>I});var a=t(87462),r=t(67294),l=t(86010),i=t(12466),o=t(76775),u=t(91980),s=t(67392),c=t(50012);function p(e){return function(e){var n,t;return null!=(n=null==(t=r.Children.map(e,(function(e){if(!e||(0,r.isValidElement)(e)&&(n=e.props)&&"object"==typeof n&&"value"in n)return e;var n;throw new Error("Docusaurus error: Bad <Tabs> child <"+("string"==typeof e.type?e.type:e.type.name)+'>: all children of the <Tabs> component should be <TabItem>, and every <TabItem> should have a unique "value" prop.')})))?void 0:t.filter(Boolean))?n:[]}(e).map((function(e){var n=e.props;return{value:n.value,label:n.label,attributes:n.attributes,default:n.default}}))}function d(e){var n=e.values,t=e.children;return(0,r.useMemo)((function(){var e=null!=n?n:p(t);return function(e){var n=(0,s.l)(e,(function(e,n){return e.value===n.value}));if(n.length>0)throw new Error('Docusaurus error: Duplicate values "'+n.map((function(e){return e.value})).join(", ")+'" found in <Tabs>. Every value needs to be unique.')}(e),e}),[n,t])}function m(e){var n=e.value;return e.tabValues.some((function(e){return e.value===n}))}function f(e){var n=e.queryString,t=void 0!==n&&n,a=e.groupId,l=(0,o.k6)(),i=function(e){var n=e.queryString,t=void 0!==n&&n,a=e.groupId;if("string"==typeof t)return t;if(!1===t)return null;if(!0===t&&!a)throw new Error('Docusaurus error: The <Tabs> component groupId prop is required if queryString=true, because this value is used as the search param name. You can also provide an explicit value such as queryString="my-search-param".');return null!=a?a:null}({queryString:t,groupId:a});return[(0,u._X)(i),(0,r.useCallback)((function(e){if(i){var n=new URLSearchParams(l.location.search);n.set(i,e),l.replace(Object.assign({},l.location,{search:n.toString()}))}}),[i,l])]}function v(e){var n,t,a,l,i=e.defaultValue,o=e.queryString,u=void 0!==o&&o,s=e.groupId,p=d(e),v=(0,r.useState)((function(){return function(e){var n,t=e.defaultValue,a=e.tabValues;if(0===a.length)throw new Error("Docusaurus error: the <Tabs> component requires at least one <TabItem> children component");if(t){if(!m({value:t,tabValues:a}))throw new Error('Docusaurus error: The <Tabs> has a defaultValue "'+t+'" but none of its children has the corresponding value. Available values are: '+a.map((function(e){return e.value})).join(", ")+". If you intend to show no default tab, use defaultValue={null} instead.");return t}var r=null!=(n=a.find((function(e){return e.default})))?n:a[0];if(!r)throw new Error("Unexpected error: 0 tabValues");return r.value}({defaultValue:i,tabValues:p})})),h=v[0],k=v[1],b=f({queryString:u,groupId:s}),g=b[0],y=b[1],I=(n=function(e){return e?"docusaurus.tab."+e:null}({groupId:s}.groupId),t=(0,c.Nk)(n),a=t[0],l=t[1],[a,(0,r.useCallback)((function(e){n&&l.set(e)}),[n,l])]),S=I[0],x=I[1],N=function(){var e=null!=g?g:S;return m({value:e,tabValues:p})?e:null}();return(0,r.useLayoutEffect)((function(){N&&k(N)}),[N]),{selectedValue:h,selectValue:(0,r.useCallback)((function(e){if(!m({value:e,tabValues:p}))throw new Error("Can't select invalid tab value="+e);k(e),y(e),x(e)}),[y,x,p]),tabValues:p}}var h=t(72389);const k={tabList:"tabList__CuJ",tabItem:"tabItem_LNqP"};function b(e){var n=e.className,t=e.block,o=e.selectedValue,u=e.selectValue,s=e.tabValues,c=[],p=(0,i.o5)().blockElementScrollPositionUntilNextRender,d=function(e){var n=e.currentTarget,t=c.indexOf(n),a=s[t].value;a!==o&&(p(n),u(a))},m=function(e){var n,t=null;switch(e.key){case"Enter":d(e);break;case"ArrowRight":var a,r=c.indexOf(e.currentTarget)+1;t=null!=(a=c[r])?a:c[0];break;case"ArrowLeft":var l,i=c.indexOf(e.currentTarget)-1;t=null!=(l=c[i])?l:c[c.length-1]}null==(n=t)||n.focus()};return r.createElement("ul",{role:"tablist","aria-orientation":"horizontal",className:(0,l.Z)("tabs",{"tabs--block":t},n)},s.map((function(e){var n=e.value,t=e.label,i=e.attributes;return r.createElement("li",(0,a.Z)({role:"tab",tabIndex:o===n?0:-1,"aria-selected":o===n,key:n,ref:function(e){return c.push(e)},onKeyDown:m,onClick:d},i,{className:(0,l.Z)("tabs__item",k.tabItem,null==i?void 0:i.className,{"tabs__item--active":o===n})}),null!=t?t:n)})))}function g(e){var n=e.lazy,t=e.children,a=e.selectedValue,l=(Array.isArray(t)?t:[t]).filter(Boolean);if(n){var i=l.find((function(e){return e.props.value===a}));return i?(0,r.cloneElement)(i,{className:"margin-top--md"}):null}return r.createElement("div",{className:"margin-top--md"},l.map((function(e,n){return(0,r.cloneElement)(e,{key:n,hidden:e.props.value!==a})})))}function y(e){var n=v(e);return r.createElement("div",{className:(0,l.Z)("tabs-container",k.tabList)},r.createElement(b,(0,a.Z)({},e,n)),r.createElement(g,(0,a.Z)({},e,n)))}function I(e){var n=(0,h.Z)();return r.createElement(y,(0,a.Z)({key:String(n)},e))}},71489:(e,n,t)=>{t.r(n),t.d(n,{assets:()=>d,contentTitle:()=>c,default:()=>h,frontMatter:()=>s,metadata:()=>p,toc:()=>m});var a=t(87462),r=t(63366),l=(t(67294),t(3905)),i=(t(95657),t(74866)),o=t(85162),u=["components"],s={id:"client-features",title:"Client Features"},c=void 0,p={unversionedId:"client/client-features",id:"version-4.x.x/client/client-features",title:"Client Features",description:"Jackson and Kotlinx Serialization Support",source:"@site/versioned_docs/version-4.x.x/client/client-features.mdx",sourceDirName:"client",slug:"/client/client-features",permalink:"/graphql-kotlin/docs/4.x.x/client/client-features",draft:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-4.x.x/client/client-features.mdx",tags:[],version:"4.x.x",lastUpdatedBy:"Samuel Vazquez",lastUpdatedAt:1685659104,formattedLastUpdatedAt:"Jun 1, 2023",frontMatter:{id:"client-features",title:"Client Features"},sidebar:"version-4.x.x/docs",previous:{title:"Client Overview",permalink:"/graphql-kotlin/docs/4.x.x/client/client-overview"},next:{title:"Client Customization",permalink:"/graphql-kotlin/docs/4.x.x/client/client-customization"}},d={},m=[{value:"Jackson and Kotlinx Serialization Support",id:"jackson-and-kotlinx-serialization-support",level:2},{value:"Polymorphic Types Support",id:"polymorphic-types-support",level:2},{value:"Default Enum Values",id:"default-enum-values",level:2},{value:"Auto Generated Documentation",id:"auto-generated-documentation",level:2},{value:"Native Support for Coroutines",id:"native-support-for-coroutines",level:2},{value:"Batch Operation Support",id:"batch-operation-support",level:2}],f={toc:m},v="wrapper";function h(e){var n=e.components,t=(0,r.Z)(e,u);return(0,l.kt)(v,(0,a.Z)({},f,t,{components:n,mdxType:"MDXLayout"}),(0,l.kt)("h2",{id:"jackson-and-kotlinx-serialization-support"},"Jackson and Kotlinx Serialization Support"),(0,l.kt)("p",null,"GraphQL Kotlin supports generation of client data models that are compatible with both ",(0,l.kt)("inlineCode",{parentName:"p"},"Jackson")," (default) and ",(0,l.kt)("inlineCode",{parentName:"p"},"kotlinx.serialization"),"\nformats. Build plugins and ",(0,l.kt)("inlineCode",{parentName:"p"},"graphql-kotlin-spring-client")," default to use ",(0,l.kt)("inlineCode",{parentName:"p"},"Jackson")," whereas ",(0,l.kt)("inlineCode",{parentName:"p"},"graphql-kotlin-ktor-client"),"\ndefaults to ",(0,l.kt)("inlineCode",{parentName:"p"},"kotlinx.serialization"),"."),(0,l.kt)("p",null,"See ",(0,l.kt)("a",{parentName:"p",href:"/graphql-kotlin/docs/4.x.x/client/client-serialization"},"client serialization documentation")," for additional details."),(0,l.kt)("h2",{id:"polymorphic-types-support"},"Polymorphic Types Support"),(0,l.kt)("p",null,"GraphQL supports polymorphic types through unions and interfaces which can be represented in Kotlin as marker and\nregular interfaces. In order to ensure generated objects are not empty, GraphQL queries referencing polymorphic types\nhave to ",(0,l.kt)("strong",{parentName:"p"},"explicitly specify all implementations"),". Polymorphic queries also have to explicitly request ",(0,l.kt)("inlineCode",{parentName:"p"},"__typename"),"\nfield so it can be used to Jackson correctly distinguish between different implementations."),(0,l.kt)("p",null,"Given example schema"),(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-graphql"},"type Query {\n  interfaceQuery: BasicInterface!\n}\n\ninterface BasicInterface {\n  id: Int!\n  name: String!\n}\n\ntype FirstInterfaceImplementation implements BasicInterface {\n  id: Int!\n  intValue: Int!\n  name: String!\n}\n\ntype SecondInterfaceImplementation implements BasicInterface {\n  floatValue: Float!\n  id: Int!\n  name: String!\n}\n")),(0,l.kt)("p",null,"We can query interface field as"),(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-graphql"},"query PolymorphicQuery {\n  interfaceQuery {\n    __typename\n    id\n    name\n    ... on FirstInterfaceImplementation {\n      intValue\n    }\n    ... on SecondInterfaceImplementation {\n      floatValue\n    }\n  }\n}\n")),(0,l.kt)("p",null,"Which will generate following data models"),(0,l.kt)(i.Z,{defaultValue:"jackson",values:[{label:"Jackson",value:"jackson"},{label:"kotlinx.serialization",value:"kotlinx"}],mdxType:"Tabs"},(0,l.kt)(o.Z,{value:"jackson",mdxType:"TabItem"},(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-kotlin"},'@JsonTypeInfo(\n  use = JsonTypeInfo.Id.NAME,\n  include = JsonTypeInfo.As.PROPERTY,\n  property = "__typename"\n)\n@JsonSubTypes(value = [com.fasterxml.jackson.annotation.JsonSubTypes.Type(value =\n    FirstInterfaceImplementation::class,\n    name="FirstInterfaceImplementation"),com.fasterxml.jackson.annotation.JsonSubTypes.Type(value\n    = SecondInterfaceImplementation::class, name="SecondInterfaceImplementation")])\ninterface BasicInterface {\n  abstract val id: Int\n  abstract val name: String\n}\n\ndata class FirstInterfaceImplementation(\n  override val id: Int,\n  override val name: String,\n  val intValue: Int\n) : BasicInterface\n\ndata class SecondInterfaceImplementation(\n  override val id: Int,\n  override val name: String,\n  val floatValue: Float\n) : BasicInterface\n'))),(0,l.kt)(o.Z,{value:"kotlinx",mdxType:"TabItem"},(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-kotlin"},'@Serializable\nsealed class BasicInterface {\n  abstract val id: Int\n  abstract val name: String\n}\n\n@Serializable\n@SerialName(value = "FirstInterfaceImplementation")\ndata class FirstInterfaceImplementation(\n  override val id: Int,\n  override val name: String,\n  val intValue: Int\n) : BasicInterface()\n\n@Serializable\n@SerialName(value = "SecondInterfaceImplementation")\ndata class SecondInterfaceImplementation(\n  override val id: Int,\n  override val name: String,\n  val floatValue: Float\n) : BasicInterface()\n')))),(0,l.kt)("h2",{id:"default-enum-values"},"Default Enum Values"),(0,l.kt)("p",null,"Enums represent predefined set of values. Adding additional enum values could be a potentially breaking change as your\nclients may not be able to process it. GraphQL Kotlin Client automatically adds default ",(0,l.kt)("inlineCode",{parentName:"p"},"__UNKNOWN_VALUE")," to all generated\nenums as a catch all safeguard for handling new enum values."),(0,l.kt)("h2",{id:"auto-generated-documentation"},"Auto Generated Documentation"),(0,l.kt)("p",null,"GraphQL Kotlin build plugins automatically pull in GraphQL descriptions of the queried fields from the target schema and\nadd it as KDoc to corresponding data models."),(0,l.kt)("p",null,"Given simple GraphQL object definition"),(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-graphql"},'"Some basic description"\ntype BasicObject {\n  "Unique identifier"\n  id: Int!\n  "Object name"\n  name: String!\n}\n')),(0,l.kt)("p",null,"Will result in a corresponding auto generated data class"),(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-kotlin"},"/**\n * Some basic description\n */\ndata class BasicObject(\n  /**\n   * Unique identifier\n   */\n  val id: Int,\n  /**\n   * Object name\n   */\n  val name: String\n)\n")),(0,l.kt)("h2",{id:"native-support-for-coroutines"},"Native Support for Coroutines"),(0,l.kt)("p",null,"GraphQL Kotlin Client is a generic interface that exposes ",(0,l.kt)("inlineCode",{parentName:"p"},"execute")," methods that will suspend your GraphQL operation until\nit gets a response back without blocking the underlying thread. Reference Ktor and Spring WebClient based implementations\noffer fully asynchronous communication through Kotlin coroutines. In order to fetch data asynchronously you should wrap\nyour client execution in ",(0,l.kt)("inlineCode",{parentName:"p"},"launch")," or ",(0,l.kt)("inlineCode",{parentName:"p"},"async")," coroutine builder and explicitly ",(0,l.kt)("inlineCode",{parentName:"p"},"await")," for their results."),(0,l.kt)("p",null,"See ",(0,l.kt)("a",{parentName:"p",href:"https://kotlinlang.org/docs/reference/coroutines-overview.html"},"Kotlin coroutines documentation")," for additional details."),(0,l.kt)("h2",{id:"batch-operation-support"},"Batch Operation Support"),(0,l.kt)("p",null,"GraphQL Kotlin Clients provide out of the box support for batching multiple client operations into a single GraphQL request.\nBatch requests are sent as an array of individual GraphQL requests and clients expect the server to respond with a corresponding\narray of GraphQL responses. Each response is then deserialized to a corresponding result type."),(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-kotlin"},'val client = GraphQLKtorClient(url = URL("http://localhost:8080/graphql"))\nval firstQuery = FirstQuery(variables = FirstQuery.Variables(foo = "bar"))\nval secondQuery = SecondQuery(variables = SecondQuery.Variables(foo = "baz"))\n\nval results: List<GraphQLResponse<*>> = client.execute(listOf(firstQuery, secondQuery))\n')))}h.isMDXComponent=!0}}]);