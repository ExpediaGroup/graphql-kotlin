"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[1051],{3905:(e,n,t)=>{t.d(n,{Zo:()=>s,kt:()=>g});var r=t(67294);function a(e,n,t){return n in e?Object.defineProperty(e,n,{value:t,enumerable:!0,configurable:!0,writable:!0}):e[n]=t,e}function l(e,n){var t=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);n&&(r=r.filter((function(n){return Object.getOwnPropertyDescriptor(e,n).enumerable}))),t.push.apply(t,r)}return t}function i(e){for(var n=1;n<arguments.length;n++){var t=null!=arguments[n]?arguments[n]:{};n%2?l(Object(t),!0).forEach((function(n){a(e,n,t[n])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(t)):l(Object(t)).forEach((function(n){Object.defineProperty(e,n,Object.getOwnPropertyDescriptor(t,n))}))}return e}function o(e,n){if(null==e)return{};var t,r,a=function(e,n){if(null==e)return{};var t,r,a={},l=Object.keys(e);for(r=0;r<l.length;r++)t=l[r],n.indexOf(t)>=0||(a[t]=e[t]);return a}(e,n);if(Object.getOwnPropertySymbols){var l=Object.getOwnPropertySymbols(e);for(r=0;r<l.length;r++)t=l[r],n.indexOf(t)>=0||Object.prototype.propertyIsEnumerable.call(e,t)&&(a[t]=e[t])}return a}var u=r.createContext({}),p=function(e){var n=r.useContext(u),t=n;return e&&(t="function"==typeof e?e(n):i(i({},n),e)),t},s=function(e){var n=p(e.components);return r.createElement(u.Provider,{value:n},e.children)},c="mdxType",d={inlineCode:"code",wrapper:function(e){var n=e.children;return r.createElement(r.Fragment,{},n)}},m=r.forwardRef((function(e,n){var t=e.components,a=e.mdxType,l=e.originalType,u=e.parentName,s=o(e,["components","mdxType","originalType","parentName"]),c=p(t),m=a,g=c["".concat(u,".").concat(m)]||c[m]||d[m]||l;return t?r.createElement(g,i(i({ref:n},s),{},{components:t})):r.createElement(g,i({ref:n},s))}));function g(e,n){var t=arguments,a=n&&n.mdxType;if("string"==typeof e||a){var l=t.length,i=new Array(l);i[0]=m;var o={};for(var u in n)hasOwnProperty.call(n,u)&&(o[u]=n[u]);o.originalType=e,o[c]="string"==typeof e?e:a,i[1]=o;for(var p=2;p<l;p++)i[p]=t[p];return r.createElement.apply(null,i)}return r.createElement.apply(null,t)}m.displayName="MDXCreateElement"},85162:(e,n,t)=>{t.d(n,{Z:()=>i});var r=t(67294),a=t(86010);const l={tabItem:"tabItem_Ymn6"};function i(e){var n=e.children,t=e.hidden,i=e.className;return r.createElement("div",{role:"tabpanel",className:(0,a.Z)(l.tabItem,i),hidden:t},n)}},74866:(e,n,t)=>{t.d(n,{Z:()=>x});var r=t(87462),a=t(67294),l=t(86010),i=t(12466),o=t(16550),u=t(91980),p=t(67392),s=t(50012);function c(e){return function(e){var n,t;return null!=(n=null==(t=a.Children.map(e,(function(e){if(!e||(0,a.isValidElement)(e)&&(n=e.props)&&"object"==typeof n&&"value"in n)return e;var n;throw new Error("Docusaurus error: Bad <Tabs> child <"+("string"==typeof e.type?e.type:e.type.name)+'>: all children of the <Tabs> component should be <TabItem>, and every <TabItem> should have a unique "value" prop.')})))?void 0:t.filter(Boolean))?n:[]}(e).map((function(e){var n=e.props;return{value:n.value,label:n.label,attributes:n.attributes,default:n.default}}))}function d(e){var n=e.values,t=e.children;return(0,a.useMemo)((function(){var e=null!=n?n:c(t);return function(e){var n=(0,p.l)(e,(function(e,n){return e.value===n.value}));if(n.length>0)throw new Error('Docusaurus error: Duplicate values "'+n.map((function(e){return e.value})).join(", ")+'" found in <Tabs>. Every value needs to be unique.')}(e),e}),[n,t])}function m(e){var n=e.value;return e.tabValues.some((function(e){return e.value===n}))}function g(e){var n=e.queryString,t=void 0!==n&&n,r=e.groupId,l=(0,o.k6)(),i=function(e){var n=e.queryString,t=void 0!==n&&n,r=e.groupId;if("string"==typeof t)return t;if(!1===t)return null;if(!0===t&&!r)throw new Error('Docusaurus error: The <Tabs> component groupId prop is required if queryString=true, because this value is used as the search param name. You can also provide an explicit value such as queryString="my-search-param".');return null!=r?r:null}({queryString:t,groupId:r});return[(0,u._X)(i),(0,a.useCallback)((function(e){if(i){var n=new URLSearchParams(l.location.search);n.set(i,e),l.replace(Object.assign({},l.location,{search:n.toString()}))}}),[i,l])]}function h(e){var n,t,r,l,i=e.defaultValue,o=e.queryString,u=void 0!==o&&o,p=e.groupId,c=d(e),h=(0,a.useState)((function(){return function(e){var n,t=e.defaultValue,r=e.tabValues;if(0===r.length)throw new Error("Docusaurus error: the <Tabs> component requires at least one <TabItem> children component");if(t){if(!m({value:t,tabValues:r}))throw new Error('Docusaurus error: The <Tabs> has a defaultValue "'+t+'" but none of its children has the corresponding value. Available values are: '+r.map((function(e){return e.value})).join(", ")+". If you intend to show no default tab, use defaultValue={null} instead.");return t}var a=null!=(n=r.find((function(e){return e.default})))?n:r[0];if(!a)throw new Error("Unexpected error: 0 tabValues");return a.value}({defaultValue:i,tabValues:c})})),f=h[0],v=h[1],k=g({queryString:u,groupId:p}),b=k[0],y=k[1],x=(n=function(e){return e?"docusaurus.tab."+e:null}({groupId:p}.groupId),t=(0,s.Nk)(n),r=t[0],l=t[1],[r,(0,a.useCallback)((function(e){n&&l.set(e)}),[n,l])]),w=x[0],N=x[1],q=function(){var e=null!=b?b:w;return m({value:e,tabValues:c})?e:null}();return(0,a.useLayoutEffect)((function(){q&&v(q)}),[q]),{selectedValue:f,selectValue:(0,a.useCallback)((function(e){if(!m({value:e,tabValues:c}))throw new Error("Can't select invalid tab value="+e);v(e),y(e),N(e)}),[y,N,c]),tabValues:c}}var f=t(72389);const v={tabList:"tabList__CuJ",tabItem:"tabItem_LNqP"};function k(e){var n=e.className,t=e.block,o=e.selectedValue,u=e.selectValue,p=e.tabValues,s=[],c=(0,i.o5)().blockElementScrollPositionUntilNextRender,d=function(e){var n=e.currentTarget,t=s.indexOf(n),r=p[t].value;r!==o&&(c(n),u(r))},m=function(e){var n,t=null;switch(e.key){case"Enter":d(e);break;case"ArrowRight":var r,a=s.indexOf(e.currentTarget)+1;t=null!=(r=s[a])?r:s[0];break;case"ArrowLeft":var l,i=s.indexOf(e.currentTarget)-1;t=null!=(l=s[i])?l:s[s.length-1]}null==(n=t)||n.focus()};return a.createElement("ul",{role:"tablist","aria-orientation":"horizontal",className:(0,l.Z)("tabs",{"tabs--block":t},n)},p.map((function(e){var n=e.value,t=e.label,i=e.attributes;return a.createElement("li",(0,r.Z)({role:"tab",tabIndex:o===n?0:-1,"aria-selected":o===n,key:n,ref:function(e){return s.push(e)},onKeyDown:m,onClick:d},i,{className:(0,l.Z)("tabs__item",v.tabItem,null==i?void 0:i.className,{"tabs__item--active":o===n})}),null!=t?t:n)})))}function b(e){var n=e.lazy,t=e.children,r=e.selectedValue,l=(Array.isArray(t)?t:[t]).filter(Boolean);if(n){var i=l.find((function(e){return e.props.value===r}));return i?(0,a.cloneElement)(i,{className:"margin-top--md"}):null}return a.createElement("div",{className:"margin-top--md"},l.map((function(e,n){return(0,a.cloneElement)(e,{key:n,hidden:e.props.value!==r})})))}function y(e){var n=h(e);return a.createElement("div",{className:(0,l.Z)("tabs-container",v.tabList)},a.createElement(k,(0,r.Z)({},e,n)),a.createElement(b,(0,r.Z)({},e,n)))}function x(e){var n=(0,f.Z)();return a.createElement(y,(0,r.Z)({key:String(n)},e))}},97520:(e,n,t)=>{t.r(n),t.d(n,{assets:()=>d,contentTitle:()=>s,default:()=>f,frontMatter:()=>p,metadata:()=>c,toc:()=>m});var r=t(87462),a=t(63366),l=(t(67294),t(3905)),i=t(74866),o=t(85162),u=["components"],p={id:"client-overview",title:"Client Overview"},s=void 0,c={unversionedId:"client/client-overview",id:"version-4.x.x/client/client-overview",title:"Client Overview",description:"GraphQL Kotlin provides a set of lightweight type-safe GraphQL HTTP clients. The library provides Ktor HTTP client",source:"@site/versioned_docs/version-4.x.x/client/client-overview.mdx",sourceDirName:"client",slug:"/client/client-overview",permalink:"/graphql-kotlin/docs/4.x.x/client/client-overview",draft:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-4.x.x/client/client-overview.mdx",tags:[],version:"4.x.x",lastUpdatedBy:"Dariusz Kuc",lastUpdatedAt:1694122111,formattedLastUpdatedAt:"Sep 7, 2023",frontMatter:{id:"client-overview",title:"Client Overview"},sidebar:"version-4.x.x/docs",previous:{title:"Subscriptions",permalink:"/graphql-kotlin/docs/4.x.x/server/spring-server/spring-subscriptions"},next:{title:"Client Features",permalink:"/graphql-kotlin/docs/4.x.x/client/client-features"}},d={},m=[{value:"Project Configuration",id:"project-configuration",level:2},{value:"Build Configuration",id:"build-configuration",level:3},{value:"Generating GraphQL Operations",id:"generating-graphql-operations",level:3},{value:"Executing Operations",id:"executing-operations",level:3}],g={toc:m},h="wrapper";function f(e){var n=e.components,t=(0,a.Z)(e,u);return(0,l.kt)(h,(0,r.Z)({},g,t,{components:n,mdxType:"MDXLayout"}),(0,l.kt)("p",null,"GraphQL Kotlin provides a set of lightweight type-safe GraphQL HTTP clients. The library provides ",(0,l.kt)("a",{parentName:"p",href:"https://ktor.io/clients/index.html"},"Ktor HTTP client"),"\nand ",(0,l.kt)("a",{parentName:"p",href:"https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-webclient"},"Spring WebClient"),"\nbased reference implementations as well as allows for custom implementations using other engines, see ",(0,l.kt)("a",{parentName:"p",href:"/graphql-kotlin/docs/4.x.x/client/client-customization"},"client customization"),"\ndocumentation for additional details. Type-safe data models are generated at build time by the GraphQL Kotlin ",(0,l.kt)("a",{parentName:"p",href:"/graphql-kotlin/docs/4.x.x/plugins/gradle-plugin-tasks"},"Gradle"),"\nand ",(0,l.kt)("a",{parentName:"p",href:"/graphql-kotlin/docs/4.x.x/plugins/maven-plugin-goals"},"Maven")," plugins."),(0,l.kt)("p",null,"Client Features:"),(0,l.kt)("ul",null,(0,l.kt)("li",{parentName:"ul"},"Supports query and mutation operations"),(0,l.kt)("li",{parentName:"ul"},"Supports batch operations"),(0,l.kt)("li",{parentName:"ul"},"Automatic generation of type-safe Kotlin models supporting ",(0,l.kt)("inlineCode",{parentName:"li"},"kotlinx.serialization")," and ",(0,l.kt)("inlineCode",{parentName:"li"},"Jackson")," formats"),(0,l.kt)("li",{parentName:"ul"},"Custom scalar support - defaults to String but can be configured to deserialize to specific types"),(0,l.kt)("li",{parentName:"ul"},"Supports default enum values to gracefully handle new/unknown server values"),(0,l.kt)("li",{parentName:"ul"},"Native support for coroutines"),(0,l.kt)("li",{parentName:"ul"},"Easily configurable Ktor and Spring WebClient based HTTP Clients"),(0,l.kt)("li",{parentName:"ul"},"Documentation generated from the underlying GraphQL schema")),(0,l.kt)("h2",{id:"project-configuration"},"Project Configuration"),(0,l.kt)("p",null,"GraphQL Kotlin provides both Gradle and Maven plugins to automatically generate your client code at build time. In order\nto auto-generate the client code, plugins require target GraphQL schema and a list of query files to process."),(0,l.kt)("p",null,"GraphQL schema can be provided as"),(0,l.kt)("ul",null,(0,l.kt)("li",{parentName:"ul"},"result of introspection query (default)"),(0,l.kt)("li",{parentName:"ul"},"downloaded from an SDL endpoint"),(0,l.kt)("li",{parentName:"ul"},"local file")),(0,l.kt)("p",null,"See ",(0,l.kt)("a",{parentName:"p",href:"https://expediagroup.github.io/graphql-kotlin/docs/plugins/gradle-plugin"},"Gradle")," and ",(0,l.kt)("a",{parentName:"p",href:"https://expediagroup.github.io/graphql-kotlin/docs/plugins/maven-plugin"},"Maven"),"\nplugin documentation for additional details."),(0,l.kt)("p",null,"GraphQL Kotlin plugins generated classes are simple POJOs that implement ",(0,l.kt)("inlineCode",{parentName:"p"},"GraphQLClientRequest")," and optionally accept variables\n(only if underlying operation uses variables) as a constructor parameter. Generated classes can then be passed directly\nto a GraphQL client to execute either a single or a batch request."),(0,l.kt)("p",null,"Example below configures the project to use introspection query to obtain the schema and uses Spring WebClient based HTTP client."),(0,l.kt)("h3",{id:"build-configuration"},"Build Configuration"),(0,l.kt)(i.Z,{defaultValue:"gradle",values:[{label:"Gradle",value:"gradle"},{label:"Maven",value:"maven"}],mdxType:"Tabs"},(0,l.kt)(o.Z,{value:"gradle",mdxType:"TabItem"},(0,l.kt)("p",null,"Basic ",(0,l.kt)("inlineCode",{parentName:"p"},"build.gradle.kts")," Gradle configuration that executes introspection query against specified endpoint to obtain target\nschema and then generate the clients under ",(0,l.kt)("inlineCode",{parentName:"p"},"com.example.generated")," package name:"),(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-kotlin"},'import com.expediagroup.graphql.plugin.gradle.graphql\n\nplugins {\n    id("com.expediagroup.graphql") version $latestGraphQLKotlinVersion\n}\n\ndependencies {\n  implementation("com.expediagroup:graphql-kotlin-spring-client:$latestGraphQLKotlinVersion")\n}\n\ngraphql {\n    client {\n        endpoint = "http://localhost:8080/graphql"\n        packageName = "com.example.generated"\n    }\n}\n'))),(0,l.kt)(o.Z,{value:"maven",mdxType:"TabItem"},(0,l.kt)("p",null,"Basic Maven ",(0,l.kt)("inlineCode",{parentName:"p"},"pom.xml")," configuration that executes introspection query against specified endpoint to obtain target\nschema and then generate the clients under ",(0,l.kt)("inlineCode",{parentName:"p"},"com.example.generated")," package name:"),(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-xml"},'<?xml version="1.0" encoding="UTF-8"?>\n<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">\n    <modelVersion>4.0.0</modelVersion>\n\n    <groupId>com.example</groupId>\n    <artifactId>graphql-kotlin-maven-client-example</artifactId>\n    <version>1.0.0-SNAPSHOT</version>\n\n    <properties>\n        <graphql-kotlin.version>$latestGraphQLKotlinVersion</graphql-kotlin.version>\n    </properties>\n\n    <dependencies>\n        <dependency>\n            <groupId>com.expediagroup</groupId>\n            <artifactId>graphql-kotlin-spring-client</artifactId>\n            <version>${graphql-kotlin.version}</version>\n        </dependency>\n    </dependencies>\n\n    <build>\n        <plugins>\n            <plugin>\n                <groupId>com.expediagroup</groupId>\n                <artifactId>graphql-kotlin-maven-plugin</artifactId>\n                <version>${graphql-kotlin.version}</version>\n                <executions>\n                    <execution>\n                        <id>generate-graphql-client</id>\n                        <goals>\n                            <goal>introspectSchema</goal>\n                            <goal>generateClient</goal>\n                        </goals>\n                        <configuration>\n                            <endpoint>http://localhost:8080/graphql</endpoint>\n                            <packageName>com.example.generated</packageName>\n                            <schemaFile>${project.build.directory}/schema.graphql</schemaFile>\n                        </configuration>\n                    </execution>\n                </executions>\n            </plugin>\n        </plugins>\n    </build>\n</project>\n')))),(0,l.kt)("p",null,"See ",(0,l.kt)("a",{parentName:"p",href:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/examples/client"},"graphql-kotlin-client-example")," project for complete\nworking examples of Gradle and Maven based projects."),(0,l.kt)("h3",{id:"generating-graphql-operations"},"Generating GraphQL Operations"),(0,l.kt)("p",null,"By default, GraphQL Kotlin build plugins will attempt to generate GraphQL operations from all ",(0,l.kt)("inlineCode",{parentName:"p"},"*.graphql")," files located under\n",(0,l.kt)("inlineCode",{parentName:"p"},"src/main/resources"),". Operations are validated against the target GraphQL schema, which can be manually provided, retrieved by\nthe plugins through introspection (as configured in examples above) or downloaded directly from a custom SDL endpoint.\nSee our documentation for more details on supported ",(0,l.kt)("a",{parentName:"p",href:"/graphql-kotlin/docs/4.x.x/plugins/gradle-plugin-tasks"},"Gradle tasks"),"\nand ",(0,l.kt)("a",{parentName:"p",href:"/graphql-kotlin/docs/4.x.x/plugins/maven-plugin-goals"},"Maven Mojos"),"."),(0,l.kt)("p",null,"When creating your GraphQL operations make sure to always specify an operation name and name the files accordingly. Each\none of your GraphQL operation files will generate a corresponding Kotlin file with a class matching your operation\nname. Input objects, enums and custom scalars definitions will be shared across different operations. All other objects\nwill be generated operation specific package name. For example, given ",(0,l.kt)("inlineCode",{parentName:"p"},"HelloWorldQuery.graphql")," with ",(0,l.kt)("inlineCode",{parentName:"p"},"HelloWorldQuery")," as\nthe operation name, GraphQL Kotlin plugins will generate a corresponding ",(0,l.kt)("inlineCode",{parentName:"p"},"HelloWorldQuery.kt")," file with a ",(0,l.kt)("inlineCode",{parentName:"p"},"HelloWorldQuery"),"\nclass under the configured package."),(0,l.kt)("p",null,"For example, given a simple schema"),(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-graphql"},"type Query {\n  helloWorld: String\n}\n")),(0,l.kt)("p",null,"And a corresponding ",(0,l.kt)("inlineCode",{parentName:"p"},"HelloWorldQuery.graphql")," query"),(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-graphql"},"query HelloWorldQuery {\n  helloWorld\n}\n")),(0,l.kt)("p",null,"Plugins will generate following client code"),(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-kotlin"},'package com.example.generated\n\nimport com.expediagroup.graphql.client.types.GraphQLClientRequest\nimport kotlin.String\nimport kotlin.reflect.KClass\n\nconst val HELLO_WORLD_QUERY: String = "query HelloWorldQuery {\\n    helloWorld\\n}"\n\nclass HelloWorldQuery: GraphQLClientRequest<HelloWorldQuery.Result> {\n    override val query: String = HELLO_WORLD_QUERY\n\n    override val operationName: String = "HelloWorldQuery"\n\n    override fun responseType(): KClass<HelloWorldQuery.Result> = HelloWorldQuery.Result::class\n\n    data class Result(\n        val helloWorld: String\n    }\n}\n')),(0,l.kt)("p",null,"Generated classes are simple POJOs that implement ",(0,l.kt)("inlineCode",{parentName:"p"},"GraphQLClientRequest")," interface and represent a GraphQL request."),(0,l.kt)("h3",{id:"executing-operations"},"Executing Operations"),(0,l.kt)("p",null,(0,l.kt)("inlineCode",{parentName:"p"},"GraphQLWebClient")," uses the Spring WebClient to execute the underlying operations and allows you to customize it by providing\nan instance of ",(0,l.kt)("inlineCode",{parentName:"p"},"WebClient.Builder"),". ",(0,l.kt)("inlineCode",{parentName:"p"},"GraphQLWebClient")," requires target URL to be specified and defaults to use ",(0,l.kt)("inlineCode",{parentName:"p"},"Jackson"),"\nbased GraphQL serializer. Please refer to ",(0,l.kt)("a",{parentName:"p",href:"https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html#webflux-client"},"Spring documentation"),"\nfor additional details."),(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-kotlin"},'package com.example.client\n\nimport com.expediagroup.graphql.client.spring.GraphQLWebClient\nimport com.expediagroup.graphql.generated.HelloWorldQuery\nimport kotlinx.coroutines.runBlocking\n\nfun main() {\n    val client = GraphQLWebClient(url = "http://localhost:8080/graphql")\n    runBlocking {\n        val helloWorldQuery = HelloWorldQuery()\n        val result = client.execute(helloWorldQuery)\n        println("hello world query result: ${result.data?.helloWorld}")\n    }\n}\n')))}f.isMDXComponent=!0}}]);