"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[9155],{17325:(e,n,r)=>{r.r(n),r.d(n,{assets:()=>u,contentTitle:()=>s,default:()=>h,frontMatter:()=>o,metadata:()=>c,toc:()=>d});var t=r(74848),a=r(28453),l=r(11470),i=r(19365);const o={id:"client-overview",title:"Client Overview"},s=void 0,c={id:"client/client-overview",title:"Client Overview",description:"GraphQL Kotlin provides a set of lightweight type-safe GraphQL HTTP clients. The library provides Ktor HTTP client",source:"@site/versioned_docs/version-7.x.x/client/client-overview.mdx",sourceDirName:"client",slug:"/client/client-overview",permalink:"/graphql-kotlin/docs/7.x.x/client/client-overview",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-7.x.x/client/client-overview.mdx",tags:[],version:"7.x.x",lastUpdatedBy:"Dale Seo",lastUpdatedAt:1732301091e3,frontMatter:{id:"client-overview",title:"Client Overview"},sidebar:"docs",previous:{title:"Subscriptions",permalink:"/graphql-kotlin/docs/7.x.x/server/ktor-server/ktor-subscriptions"},next:{title:"Client Features",permalink:"/graphql-kotlin/docs/7.x.x/client/client-features"}},u={},d=[{value:"Project Configuration",id:"project-configuration",level:2},{value:"Build Configuration",id:"build-configuration",level:3},{value:"Generating GraphQL Operations",id:"generating-graphql-operations",level:3},{value:"Executing Operations",id:"executing-operations",level:3}];function p(e){const n={a:"a",code:"code",h2:"h2",h3:"h3",li:"li",p:"p",pre:"pre",ul:"ul",...(0,a.R)(),...e.components};return(0,t.jsxs)(t.Fragment,{children:[(0,t.jsxs)(n.p,{children:["GraphQL Kotlin provides a set of lightweight type-safe GraphQL HTTP clients. The library provides ",(0,t.jsx)(n.a,{href:"https://ktor.io/clients/index.html",children:"Ktor HTTP client"}),"\nand ",(0,t.jsx)(n.a,{href:"https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-webclient",children:"Spring WebClient"}),"\nbased reference implementations as well as allows for custom implementations using other engines, see ",(0,t.jsx)(n.a,{href:"/graphql-kotlin/docs/7.x.x/client/client-customization",children:"client customization"}),"\ndocumentation for additional details. Type-safe data models are generated at build time by the GraphQL Kotlin ",(0,t.jsx)(n.a,{href:"/graphql-kotlin/docs/7.x.x/plugins/gradle-plugin-tasks",children:"Gradle"}),"\nand ",(0,t.jsx)(n.a,{href:"/graphql-kotlin/docs/7.x.x/plugins/maven-plugin-goals",children:"Maven"})," plugins."]}),"\n",(0,t.jsx)(n.p,{children:"Client Features:"}),"\n",(0,t.jsxs)(n.ul,{children:["\n",(0,t.jsx)(n.li,{children:"Supports query and mutation operations"}),"\n",(0,t.jsx)(n.li,{children:"Supports batch operations"}),"\n",(0,t.jsxs)(n.li,{children:["Automatic generation of type-safe Kotlin models supporting ",(0,t.jsx)(n.code,{children:"kotlinx.serialization"})," and ",(0,t.jsx)(n.code,{children:"Jackson"})," formats"]}),"\n",(0,t.jsx)(n.li,{children:"Custom scalar support - defaults to String but can be configured to deserialize to specific types"}),"\n",(0,t.jsx)(n.li,{children:"Supports default enum values to gracefully handle new/unknown server values"}),"\n",(0,t.jsx)(n.li,{children:"Native support for coroutines"}),"\n",(0,t.jsx)(n.li,{children:"Easily configurable Ktor and Spring WebClient based HTTP Clients"}),"\n",(0,t.jsx)(n.li,{children:"Documentation generated from the underlying GraphQL schema"}),"\n"]}),"\n",(0,t.jsx)(n.h2,{id:"project-configuration",children:"Project Configuration"}),"\n",(0,t.jsx)(n.p,{children:"GraphQL Kotlin provides both Gradle and Maven plugins to automatically generate your client code at build time. In order\nto auto-generate the client code, plugins require target GraphQL schema and a list of query files to process."}),"\n",(0,t.jsx)(n.p,{children:"GraphQL schema can be provided as"}),"\n",(0,t.jsxs)(n.ul,{children:["\n",(0,t.jsx)(n.li,{children:"result of introspection query (default)"}),"\n",(0,t.jsx)(n.li,{children:"downloaded from an SDL endpoint"}),"\n",(0,t.jsx)(n.li,{children:"local file"}),"\n"]}),"\n",(0,t.jsxs)(n.p,{children:["See ",(0,t.jsx)(n.a,{href:"https://expediagroup.github.io/graphql-kotlin/docs/plugins/gradle-plugin",children:"Gradle"})," and ",(0,t.jsx)(n.a,{href:"https://expediagroup.github.io/graphql-kotlin/docs/plugins/maven-plugin",children:"Maven"}),"\nplugin documentation for additional details."]}),"\n",(0,t.jsxs)(n.p,{children:["GraphQL Kotlin plugins generated classes are simple POJOs that implement ",(0,t.jsx)(n.code,{children:"GraphQLClientRequest"})," and optionally accept variables\n(only if underlying operation uses variables) as a constructor parameter. Generated classes can then be passed directly\nto a GraphQL client to execute either a single or a batch request."]}),"\n",(0,t.jsx)(n.p,{children:"Example below configures the project to use introspection query to obtain the schema and uses Spring WebClient based HTTP client."}),"\n",(0,t.jsx)(n.h3,{id:"build-configuration",children:"Build Configuration"}),"\n",(0,t.jsxs)(l.A,{defaultValue:"gradle",values:[{label:"Gradle",value:"gradle"},{label:"Maven",value:"maven"}],children:[(0,t.jsxs)(i.A,{value:"gradle",children:[(0,t.jsxs)(n.p,{children:["Basic ",(0,t.jsx)(n.code,{children:"build.gradle.kts"})," Gradle configuration that executes introspection query against specified endpoint to obtain target\nschema and then generate the clients under ",(0,t.jsx)(n.code,{children:"com.example.generated"})," package name:"]}),(0,t.jsx)(n.pre,{children:(0,t.jsx)(n.code,{className:"language-kotlin",children:'import com.expediagroup.graphql.plugin.gradle.graphql\n\nplugins {\n    id("com.expediagroup.graphql") version $latestGraphQLKotlinVersion\n}\n\ndependencies {\n  implementation("com.expediagroup:graphql-kotlin-spring-client:$latestGraphQLKotlinVersion")\n}\n\ngraphql {\n    client {\n        endpoint = "http://localhost:8080/graphql"\n        packageName = "com.example.generated"\n    }\n}\n'})})]}),(0,t.jsxs)(i.A,{value:"maven",children:[(0,t.jsxs)(n.p,{children:["Basic Maven ",(0,t.jsx)(n.code,{children:"pom.xml"})," configuration that executes introspection query against specified endpoint to obtain target\nschema and then generate the clients under ",(0,t.jsx)(n.code,{children:"com.example.generated"})," package name:"]}),(0,t.jsx)(n.pre,{children:(0,t.jsx)(n.code,{className:"language-xml",children:'<?xml version="1.0" encoding="UTF-8"?>\n<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">\n    <modelVersion>4.0.0</modelVersion>\n\n    <groupId>com.example</groupId>\n    <artifactId>graphql-kotlin-maven-client-example</artifactId>\n    <version>1.0.0-SNAPSHOT</version>\n\n    <properties>\n        <graphql-kotlin.version>$latestGraphQLKotlinVersion</graphql-kotlin.version>\n    </properties>\n\n    <dependencies>\n        <dependency>\n            <groupId>com.expediagroup</groupId>\n            <artifactId>graphql-kotlin-spring-client</artifactId>\n            <version>${graphql-kotlin.version}</version>\n        </dependency>\n    </dependencies>\n\n    <build>\n        <plugins>\n            <plugin>\n                <groupId>com.expediagroup</groupId>\n                <artifactId>graphql-kotlin-maven-plugin</artifactId>\n                <version>${graphql-kotlin.version}</version>\n                <executions>\n                    <execution>\n                        <id>generate-graphql-client</id>\n                        <goals>\n                            <goal>introspect-schema</goal>\n                            <goal>generate-client</goal>\n                        </goals>\n                        <configuration>\n                            <endpoint>http://localhost:8080/graphql</endpoint>\n                            <packageName>com.example.generated</packageName>\n                            <schemaFile>${project.build.directory}/schema.graphql</schemaFile>\n                        </configuration>\n                    </execution>\n                </executions>\n            </plugin>\n        </plugins>\n    </build>\n</project>\n'})})]})]}),"\n",(0,t.jsxs)(n.p,{children:["See ",(0,t.jsx)(n.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/examples/client",children:"graphql-kotlin-client-example"})," project for complete\nworking examples of Gradle and Maven based projects."]}),"\n",(0,t.jsx)(n.h3,{id:"generating-graphql-operations",children:"Generating GraphQL Operations"}),"\n",(0,t.jsxs)(n.p,{children:["By default, GraphQL Kotlin build plugins will attempt to generate GraphQL operations from all ",(0,t.jsx)(n.code,{children:"*.graphql"})," files located under\n",(0,t.jsx)(n.code,{children:"src/main/resources"}),". Operations are validated against the target GraphQL schema, which can be manually provided, retrieved by\nthe plugins through introspection (as configured in examples above) or downloaded directly from a custom SDL endpoint.\nSee our documentation for more details on supported ",(0,t.jsx)(n.a,{href:"/graphql-kotlin/docs/7.x.x/plugins/gradle-plugin-tasks",children:"Gradle tasks"}),"\nand ",(0,t.jsx)(n.a,{href:"/graphql-kotlin/docs/7.x.x/plugins/maven-plugin-goals",children:"Maven Mojos"}),"."]}),"\n",(0,t.jsxs)(n.p,{children:["When creating your GraphQL operations make sure to always specify an operation name and name the files accordingly. Each\none of your GraphQL operation files will generate a corresponding Kotlin file with a class matching your operation\nname. Input objects, enums and custom scalars definitions will be shared across different operations. All other objects\nwill be generated operation specific package name. For example, given ",(0,t.jsx)(n.code,{children:"HelloWorldQuery.graphql"})," with ",(0,t.jsx)(n.code,{children:"HelloWorldQuery"})," as\nthe operation name, GraphQL Kotlin plugins will generate a corresponding ",(0,t.jsx)(n.code,{children:"HelloWorldQuery.kt"})," file with a ",(0,t.jsx)(n.code,{children:"HelloWorldQuery"}),"\nclass under the configured package."]}),"\n",(0,t.jsx)(n.p,{children:"For example, given a simple schema"}),"\n",(0,t.jsx)(n.pre,{children:(0,t.jsx)(n.code,{className:"language-graphql",children:"type Query {\n  helloWorld: String\n}\n"})}),"\n",(0,t.jsxs)(n.p,{children:["And a corresponding ",(0,t.jsx)(n.code,{children:"HelloWorldQuery.graphql"})," query"]}),"\n",(0,t.jsx)(n.pre,{children:(0,t.jsx)(n.code,{className:"language-graphql",children:"query HelloWorldQuery {\n  helloWorld\n}\n"})}),"\n",(0,t.jsx)(n.p,{children:"Plugins will generate following client code"}),"\n",(0,t.jsx)(n.pre,{children:(0,t.jsx)(n.code,{className:"language-kotlin",children:'package com.example.generated\n\nimport com.expediagroup.graphql.client.Generated\nimport com.expediagroup.graphql.client.types.GraphQLClientRequest\nimport kotlin.String\nimport kotlin.reflect.KClass\n\nconst val HELLO_WORLD_QUERY: String = "query HelloWorldQuery {\\n    helloWorld\\n}"\n\n@Generated\nclass HelloWorldQuery: GraphQLClientRequest<HelloWorldQuery.Result> {\n    override val query: String = HELLO_WORLD_QUERY\n\n    override val operationName: String = "HelloWorldQuery"\n\n    override fun responseType(): KClass<HelloWorldQuery.Result> = HelloWorldQuery.Result::class\n\n    @Generated\n    data class Result(\n        val helloWorld: String\n    }\n}\n'})}),"\n",(0,t.jsxs)(n.p,{children:["Generated classes are simple POJOs that implement ",(0,t.jsx)(n.code,{children:"GraphQLClientRequest"})," interface and represent a GraphQL request."]}),"\n",(0,t.jsx)(n.h3,{id:"executing-operations",children:"Executing Operations"}),"\n",(0,t.jsxs)(n.p,{children:[(0,t.jsx)(n.code,{children:"GraphQLWebClient"})," uses the Spring WebClient to execute the underlying operations and allows you to customize it by providing\nan instance of ",(0,t.jsx)(n.code,{children:"WebClient.Builder"}),". ",(0,t.jsx)(n.code,{children:"GraphQLWebClient"})," requires target URL to be specified and defaults to use ",(0,t.jsx)(n.code,{children:"Jackson"}),"\nbased GraphQL serializer. Please refer to ",(0,t.jsx)(n.a,{href:"https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html#webflux-client",children:"Spring documentation"}),"\nfor additional details."]}),"\n",(0,t.jsx)(n.pre,{children:(0,t.jsx)(n.code,{className:"language-kotlin",children:'package com.example.client\n\nimport com.expediagroup.graphql.client.spring.GraphQLWebClient\nimport com.expediagroup.graphql.generated.HelloWorldQuery\nimport kotlinx.coroutines.runBlocking\n\nfun main() {\n    val client = GraphQLWebClient(url = "http://localhost:8080/graphql")\n    runBlocking {\n        val helloWorldQuery = HelloWorldQuery()\n        val result = client.execute(helloWorldQuery)\n        println("hello world query result: ${result.data?.helloWorld}")\n    }\n}\n'})})]})}function h(e={}){const{wrapper:n}={...(0,a.R)(),...e.components};return n?(0,t.jsx)(n,{...e,children:(0,t.jsx)(p,{...e})}):p(e)}},19365:(e,n,r)=>{r.d(n,{A:()=>i});r(96540);var t=r(34164);const a={tabItem:"tabItem_Ymn6"};var l=r(74848);function i(e){var n=e.children,r=e.hidden,i=e.className;return(0,l.jsx)("div",{role:"tabpanel",className:(0,t.A)(a.tabItem,i),hidden:r,children:n})}},11470:(e,n,r)=>{r.d(n,{A:()=>q});var t=r(96540),a=r(34164),l=r(23104),i=r(56347),o=r(205),s=r(57485),c=r(31682),u=r(70679);function d(e){var n,r;return null!=(n=null==(r=t.Children.toArray(e).filter((function(e){return"\n"!==e})).map((function(e){if(!e||(0,t.isValidElement)(e)&&((n=e.props)&&"object"==typeof n&&"value"in n))return e;var n;throw new Error("Docusaurus error: Bad <Tabs> child <"+("string"==typeof e.type?e.type:e.type.name)+'>: all children of the <Tabs> component should be <TabItem>, and every <TabItem> should have a unique "value" prop.')})))?void 0:r.filter(Boolean))?n:[]}function p(e){var n=e.values,r=e.children;return(0,t.useMemo)((function(){var e=null!=n?n:function(e){return d(e).map((function(e){var n=e.props;return{value:n.value,label:n.label,attributes:n.attributes,default:n.default}}))}(r);return function(e){var n=(0,c.XI)(e,(function(e,n){return e.value===n.value}));if(n.length>0)throw new Error('Docusaurus error: Duplicate values "'+n.map((function(e){return e.value})).join(", ")+'" found in <Tabs>. Every value needs to be unique.')}(e),e}),[n,r])}function h(e){var n=e.value;return e.tabValues.some((function(e){return e.value===n}))}function g(e){var n=e.queryString,r=void 0!==n&&n,a=e.groupId,l=(0,i.W6)(),o=function(e){var n=e.queryString,r=void 0!==n&&n,t=e.groupId;if("string"==typeof r)return r;if(!1===r)return null;if(!0===r&&!t)throw new Error('Docusaurus error: The <Tabs> component groupId prop is required if queryString=true, because this value is used as the search param name. You can also provide an explicit value such as queryString="my-search-param".');return null!=t?t:null}({queryString:r,groupId:a});return[(0,s.aZ)(o),(0,t.useCallback)((function(e){if(o){var n=new URLSearchParams(l.location.search);n.set(o,e),l.replace(Object.assign({},l.location,{search:n.toString()}))}}),[o,l])]}function m(e){var n,r,a,l,i=e.defaultValue,s=e.queryString,c=void 0!==s&&s,d=e.groupId,m=p(e),f=(0,t.useState)((function(){return function(e){var n,r=e.defaultValue,t=e.tabValues;if(0===t.length)throw new Error("Docusaurus error: the <Tabs> component requires at least one <TabItem> children component");if(r){if(!h({value:r,tabValues:t}))throw new Error('Docusaurus error: The <Tabs> has a defaultValue "'+r+'" but none of its children has the corresponding value. Available values are: '+t.map((function(e){return e.value})).join(", ")+". If you intend to show no default tab, use defaultValue={null} instead.");return r}var a=null!=(n=t.find((function(e){return e.default})))?n:t[0];if(!a)throw new Error("Unexpected error: 0 tabValues");return a.value}({defaultValue:i,tabValues:m})})),x=f[0],v=f[1],b=g({queryString:c,groupId:d}),j=b[0],y=b[1],q=(n=function(e){return e?"docusaurus.tab."+e:null}({groupId:d}.groupId),r=(0,u.Dv)(n),a=r[0],l=r[1],[a,(0,t.useCallback)((function(e){n&&l.set(e)}),[n,l])]),k=q[0],w=q[1],Q=function(){var e=null!=j?j:k;return h({value:e,tabValues:m})?e:null}();return(0,o.A)((function(){Q&&v(Q)}),[Q]),{selectedValue:x,selectValue:(0,t.useCallback)((function(e){if(!h({value:e,tabValues:m}))throw new Error("Can't select invalid tab value="+e);v(e),y(e),w(e)}),[y,w,m]),tabValues:m}}var f=r(92303);const x={tabList:"tabList__CuJ",tabItem:"tabItem_LNqP"};var v=r(74848);function b(e){var n=e.className,r=e.block,t=e.selectedValue,i=e.selectValue,o=e.tabValues,s=[],c=(0,l.a_)().blockElementScrollPositionUntilNextRender,u=function(e){var n=e.currentTarget,r=s.indexOf(n),a=o[r].value;a!==t&&(c(n),i(a))},d=function(e){var n,r=null;switch(e.key){case"Enter":u(e);break;case"ArrowRight":var t,a=s.indexOf(e.currentTarget)+1;r=null!=(t=s[a])?t:s[0];break;case"ArrowLeft":var l,i=s.indexOf(e.currentTarget)-1;r=null!=(l=s[i])?l:s[s.length-1]}null==(n=r)||n.focus()};return(0,v.jsx)("ul",{role:"tablist","aria-orientation":"horizontal",className:(0,a.A)("tabs",{"tabs--block":r},n),children:o.map((function(e){var n=e.value,r=e.label,l=e.attributes;return(0,v.jsx)("li",Object.assign({role:"tab",tabIndex:t===n?0:-1,"aria-selected":t===n,ref:function(e){return s.push(e)},onKeyDown:d,onClick:u},l,{className:(0,a.A)("tabs__item",x.tabItem,null==l?void 0:l.className,{"tabs__item--active":t===n}),children:null!=r?r:n}),n)}))})}function j(e){var n=e.lazy,r=e.children,l=e.selectedValue,i=(Array.isArray(r)?r:[r]).filter(Boolean);if(n){var o=i.find((function(e){return e.props.value===l}));return o?(0,t.cloneElement)(o,{className:(0,a.A)("margin-top--md",o.props.className)}):null}return(0,v.jsx)("div",{className:"margin-top--md",children:i.map((function(e,n){return(0,t.cloneElement)(e,{key:n,hidden:e.props.value!==l})}))})}function y(e){var n=m(e);return(0,v.jsxs)("div",{className:(0,a.A)("tabs-container",x.tabList),children:[(0,v.jsx)(b,Object.assign({},n,e)),(0,v.jsx)(j,Object.assign({},n,e))]})}function q(e){var n=(0,f.A)();return(0,v.jsx)(y,Object.assign({},e,{children:d(e.children)}),String(n))}},28453:(e,n,r)=>{r.d(n,{R:()=>i,x:()=>o});var t=r(96540);const a={},l=t.createContext(a);function i(e){const n=t.useContext(l);return t.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function o(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(a):e.components||a:i(e.components),t.createElement(l.Provider,{value:n},e.children)}}}]);