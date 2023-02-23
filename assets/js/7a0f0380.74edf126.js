"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[1859],{5162:(e,t,n)=>{n.d(t,{Z:()=>l});var a=n(7294),r=n(6010);const o="tabItem_Ymn6";function l(e){var t=e.children,n=e.hidden,l=e.className;return a.createElement("div",{role:"tabpanel",className:(0,r.Z)(o,l),hidden:n},t)}},4866:(e,t,n)=>{n.d(t,{Z:()=>C});var a=n(7462),r=n(7294),o=n(6010),l=n(2466),i=n(6775),s=n(1980),u=n(7392),c=n(12);function d(e){return function(e){return r.Children.map(e,(function(e){if((0,r.isValidElement)(e)&&"value"in e.props)return e;throw new Error("Docusaurus error: Bad <Tabs> child <"+("string"==typeof e.type?e.type:e.type.name)+'>: all children of the <Tabs> component should be <TabItem>, and every <TabItem> should have a unique "value" prop.')}))}(e).map((function(e){var t=e.props;return{value:t.value,label:t.label,attributes:t.attributes,default:t.default}}))}function p(e){var t=e.values,n=e.children;return(0,r.useMemo)((function(){var e=null!=t?t:d(n);return function(e){var t=(0,u.l)(e,(function(e,t){return e.value===t.value}));if(t.length>0)throw new Error('Docusaurus error: Duplicate values "'+t.map((function(e){return e.value})).join(", ")+'" found in <Tabs>. Every value needs to be unique.')}(e),e}),[t,n])}function m(e){var t=e.value;return e.tabValues.some((function(e){return e.value===t}))}function g(e){var t=e.queryString,n=void 0!==t&&t,a=e.groupId,o=(0,i.k6)(),l=function(e){var t=e.queryString,n=void 0!==t&&t,a=e.groupId;if("string"==typeof n)return n;if(!1===n)return null;if(!0===n&&!a)throw new Error('Docusaurus error: The <Tabs> component groupId prop is required if queryString=true, because this value is used as the search param name. You can also provide an explicit value such as queryString="my-search-param".');return null!=a?a:null}({queryString:n,groupId:a});return[(0,s._X)(l),(0,r.useCallback)((function(e){if(l){var t=new URLSearchParams(o.location.search);t.set(l,e),o.replace(Object.assign({},o.location,{search:t.toString()}))}}),[l,o])]}function h(e){var t,n,a,o,l=e.defaultValue,i=e.queryString,s=void 0!==i&&i,u=e.groupId,d=p(e),h=(0,r.useState)((function(){return function(e){var t,n=e.defaultValue,a=e.tabValues;if(0===a.length)throw new Error("Docusaurus error: the <Tabs> component requires at least one <TabItem> children component");if(n){if(!m({value:n,tabValues:a}))throw new Error('Docusaurus error: The <Tabs> has a defaultValue "'+n+'" but none of its children has the corresponding value. Available values are: '+a.map((function(e){return e.value})).join(", ")+". If you intend to show no default tab, use defaultValue={null} instead.");return n}var r=null!=(t=a.find((function(e){return e.default})))?t:a[0];if(!r)throw new Error("Unexpected error: 0 tabValues");return r.value}({defaultValue:l,tabValues:d})})),v=h[0],f=h[1],b=g({queryString:s,groupId:u}),k=b[0],y=b[1],N=(t=function(e){return e?"docusaurus.tab."+e:null}({groupId:u}.groupId),n=(0,c.Nk)(t),a=n[0],o=n[1],[a,(0,r.useCallback)((function(e){t&&o.set(e)}),[t,o])]),C=N[0],w=N[1],T=function(){var e=null!=k?k:C;return m({value:e,tabValues:d})?e:null}();return(0,r.useLayoutEffect)((function(){T&&f(T)}),[T]),{selectedValue:v,selectValue:(0,r.useCallback)((function(e){if(!m({value:e,tabValues:d}))throw new Error("Can't select invalid tab value="+e);f(e),y(e),w(e)}),[y,w,d]),tabValues:d}}var v=n(2389);const f="tabList__CuJ",b="tabItem_LNqP";function k(e){var t=e.className,n=e.block,i=e.selectedValue,s=e.selectValue,u=e.tabValues,c=[],d=(0,l.o5)().blockElementScrollPositionUntilNextRender,p=function(e){var t=e.currentTarget,n=c.indexOf(t),a=u[n].value;a!==i&&(d(t),s(a))},m=function(e){var t,n=null;switch(e.key){case"Enter":p(e);break;case"ArrowRight":var a,r=c.indexOf(e.currentTarget)+1;n=null!=(a=c[r])?a:c[0];break;case"ArrowLeft":var o,l=c.indexOf(e.currentTarget)-1;n=null!=(o=c[l])?o:c[c.length-1]}null==(t=n)||t.focus()};return r.createElement("ul",{role:"tablist","aria-orientation":"horizontal",className:(0,o.Z)("tabs",{"tabs--block":n},t)},u.map((function(e){var t=e.value,n=e.label,l=e.attributes;return r.createElement("li",(0,a.Z)({role:"tab",tabIndex:i===t?0:-1,"aria-selected":i===t,key:t,ref:function(e){return c.push(e)},onKeyDown:m,onClick:p},l,{className:(0,o.Z)("tabs__item",b,null==l?void 0:l.className,{"tabs__item--active":i===t})}),null!=n?n:t)})))}function y(e){var t=e.lazy,n=e.children,a=e.selectedValue;if(n=Array.isArray(n)?n:[n],t){var o=n.find((function(e){return e.props.value===a}));return o?(0,r.cloneElement)(o,{className:"margin-top--md"}):null}return r.createElement("div",{className:"margin-top--md"},n.map((function(e,t){return(0,r.cloneElement)(e,{key:t,hidden:e.props.value!==a})})))}function N(e){var t=h(e);return r.createElement("div",{className:(0,o.Z)("tabs-container",f)},r.createElement(k,(0,a.Z)({},e,t)),r.createElement(y,(0,a.Z)({},e,t)))}function C(e){var t=(0,v.Z)();return r.createElement(N,(0,a.Z)({key:String(t)},e))}},4244:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>p,contentTitle:()=>c,default:()=>h,frontMatter:()=>u,metadata:()=>d,toc:()=>m});var a=n(7462),r=n(3366),o=(n(7294),n(3905)),l=(n(8561),n(4866)),i=n(5162),s=["components"],u={id:"schema-generator-getting-started",title:"Getting Started"},c=void 0,d={unversionedId:"schema-generator/schema-generator-getting-started",id:"version-6.x.x/schema-generator/schema-generator-getting-started",title:"Getting Started",description:"Install",source:"@site/versioned_docs/version-6.x.x/schema-generator/schema-generator-getting-started.mdx",sourceDirName:"schema-generator",slug:"/schema-generator/schema-generator-getting-started",permalink:"/graphql-kotlin/docs/schema-generator/schema-generator-getting-started",draft:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-6.x.x/schema-generator/schema-generator-getting-started.mdx",tags:[],version:"6.x.x",lastUpdatedBy:"Samuel Vazquez",lastUpdatedAt:1677182897,formattedLastUpdatedAt:"Feb 23, 2023",frontMatter:{id:"schema-generator-getting-started",title:"Getting Started"},sidebar:"docs",previous:{title:"Blogs & Videos",permalink:"/graphql-kotlin/docs/blogs-and-videos"},next:{title:"Schema",permalink:"/graphql-kotlin/docs/schema-generator/writing-schemas/schema"}},p={},m=[{value:"Install",id:"install",level:2},{value:"Usage",id:"usage",level:2},{value:"<code>toSchema</code>",id:"toschema",level:2},{value:"<code>TopLevelObject</code>",id:"toplevelobject",level:2},{value:"Dynamic <code>TopLevelObject</code>",id:"dynamic-toplevelobject",level:3}],g={toc:m};function h(e){var t=e.components,n=(0,r.Z)(e,s);return(0,o.kt)("wrapper",(0,a.Z)({},g,n,{components:t,mdxType:"MDXLayout"}),(0,o.kt)("h2",{id:"install"},"Install"),(0,o.kt)("p",null,"Using a JVM dependency manager, link ",(0,o.kt)("inlineCode",{parentName:"p"},"graphql-kotlin-schema-generator")," to your project."),(0,o.kt)(l.Z,{defaultValue:"gradle",values:[{label:"Gradle Kotlin",value:"gradle"},{label:"Maven",value:"maven"}],mdxType:"Tabs"},(0,o.kt)(i.Z,{value:"gradle",mdxType:"TabItem"},(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-kotlin"},'implementation("com.expediagroup", "graphql-kotlin-schema-generator", latestVersion)\n'))),(0,o.kt)(i.Z,{value:"maven",mdxType:"TabItem"},(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-xml"},"<dependency>\n    <groupId>com.expediagroup</groupId>\n    <artifactId>graphql-kotlin-schema-generator</artifactId>\n    <version>${latestVersion}</version>\n</dependency>\n")))),(0,o.kt)("h2",{id:"usage"},"Usage"),(0,o.kt)("p",null,(0,o.kt)("inlineCode",{parentName:"p"},"graphql-kotlin-schema-generator")," provides a single function, ",(0,o.kt)("inlineCode",{parentName:"p"},"toSchema"),", to generate a schema from Kotlin objects."),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-kotlin"},"data class Widget(val id: Int, val value: String)\n\nclass WidgetQuery {\n  fun widgetById(id: Int): Widget? {\n    // grabs widget from a data source\n  }\n}\n\nclass WidgetMutation {\n  fun saveWidget(value: String): Widget {\n    // some logic for saving widget\n  }\n}\n\nval widgetQuery = WidgetQuery()\nval widgetMutation = WidgetMutation()\nval schema = toSchema(\n  config = yourCustomConfig(),\n  queries = listOf(TopLevelObject(widgetQuery)),\n  mutations = listOf(TopLevelObject(widgetMutation))\n)\n")),(0,o.kt)("p",null,"will generate:"),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-graphql"},"schema {\n  query: Query\n  mutation: Mutation\n}\n\ntype Query {\n  widgetById(id: Int!): Widget\n}\n\ntype Mutation {\n  saveWidget(value: String!): Widget!\n}\n\ntype Widget {\n  id: Int!\n  value: String!\n}\n")),(0,o.kt)("p",null,"Any ",(0,o.kt)("inlineCode",{parentName:"p"},"public")," functions defined on a query, mutation, or subscription Kotlin class will be translated into GraphQL fields on the object\ntype. ",(0,o.kt)("inlineCode",{parentName:"p"},"toSchema")," will then recursively apply Kotlin reflection on the specified classes to generate all\nremaining object types, their properties, functions, and function arguments."),(0,o.kt)("p",null,"The generated ",(0,o.kt)("inlineCode",{parentName:"p"},"GraphQLSchema")," can then be used to expose a GraphQL API endpoint."),(0,o.kt)("h2",{id:"toschema"},(0,o.kt)("inlineCode",{parentName:"h2"},"toSchema")),(0,o.kt)("p",null,"This function accepts five arguments: ",(0,o.kt)("inlineCode",{parentName:"p"},"config"),", ",(0,o.kt)("inlineCode",{parentName:"p"},"queries"),", ",(0,o.kt)("inlineCode",{parentName:"p"},"mutations"),", ",(0,o.kt)("inlineCode",{parentName:"p"},"subscriptions")," and a ",(0,o.kt)("inlineCode",{parentName:"p"},"schemaObject"),". The ",(0,o.kt)("inlineCode",{parentName:"p"},"queries"),", ",(0,o.kt)("inlineCode",{parentName:"p"},"mutations"),"\nand ",(0,o.kt)("inlineCode",{parentName:"p"},"subscriptions")," are a list of ",(0,o.kt)("inlineCode",{parentName:"p"},"TopLevelObject"),"s and will be used to generate corresponding GraphQL root types. ",(0,o.kt)("inlineCode",{parentName:"p"},"schemaObject")," is an\noptional ",(0,o.kt)("inlineCode",{parentName:"p"},"TopLevelObject")," reference to the annotated schema object. See below on why we use this wrapper class. The ",(0,o.kt)("inlineCode",{parentName:"p"},"config")," contains all\nthe extra information you need to pass, including custom hooks, supported packages, and name overrides. See the ",(0,o.kt)("a",{parentName:"p",href:"/graphql-kotlin/docs/schema-generator/customizing-schemas/generator-config"},"Generator Configuration"),"\ndocumentation for more information."),(0,o.kt)("p",null,"You can see the definition for ",(0,o.kt)("inlineCode",{parentName:"p"},"toSchema")," ",(0,o.kt)("a",{parentName:"p",href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/master/generator/graphql-kotlin-schema-generator/src/main/kotlin/com/expediagroup/graphql/generator/toSchema.kt"},"in the\nsource"),"."),(0,o.kt)("h2",{id:"toplevelobject"},(0,o.kt)("inlineCode",{parentName:"h2"},"TopLevelObject")),(0,o.kt)("p",null,(0,o.kt)("inlineCode",{parentName:"p"},"toSchema")," uses Kotlin reflection to build a GraphQL schema from given classes using ",(0,o.kt)("inlineCode",{parentName:"p"},"graphql-java"),"'s schema builder. We\ndon't just pass a ",(0,o.kt)("inlineCode",{parentName:"p"},"KClass")," though, we have to actually pass an object, because the functions on the object are\ntransformed into the data fetchers. In most cases, a ",(0,o.kt)("inlineCode",{parentName:"p"},"TopLevelObject")," can be constructed with just an object:"),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-kotlin"},"class Query {\n  fun getNumber() = 1\n}\n\nval topLevelObject = TopLevelObject(Query())\n\ntoSchema(config = config, queries = listOf(topLevelObject))\n")),(0,o.kt)("p",null,"In the above case, ",(0,o.kt)("inlineCode",{parentName:"p"},"toSchema")," will use ",(0,o.kt)("inlineCode",{parentName:"p"},"topLevelObject::class")," as the reflection target, and ",(0,o.kt)("inlineCode",{parentName:"p"},"Query")," as the data fetcher\ntarget."),(0,o.kt)("h3",{id:"dynamic-toplevelobject"},"Dynamic ",(0,o.kt)("inlineCode",{parentName:"h3"},"TopLevelObject")),(0,o.kt)("p",null,"In a lot of cases, such as with Spring AOP, the object (or bean) being used to generate a schema is a dynamic proxy. In\nthis case, ",(0,o.kt)("inlineCode",{parentName:"p"},"topLevelObject::class")," is not ",(0,o.kt)("inlineCode",{parentName:"p"},"Query"),", but rather a generated class that will confuse the schema generator.\nTo specify the ",(0,o.kt)("inlineCode",{parentName:"p"},"KClass")," to use for reflection on a proxy, pass the class to ",(0,o.kt)("inlineCode",{parentName:"p"},"TopLevelObject"),":"),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-kotlin"},"@Component\nclass Query {\n  fun getNumber() = 1\n}\n\nval query = getObjectFromBean()\nval customDef = TopLevelObject(query, Query::class)\n\ntoSchema(config, listOf(customDef))\n")))}h.isMDXComponent=!0}}]);