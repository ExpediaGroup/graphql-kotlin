"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[7670],{3905:(e,n,a)=>{a.d(n,{Zo:()=>s,kt:()=>m});var t=a(67294);function i(e,n,a){return n in e?Object.defineProperty(e,n,{value:a,enumerable:!0,configurable:!0,writable:!0}):e[n]=a,e}function r(e,n){var a=Object.keys(e);if(Object.getOwnPropertySymbols){var t=Object.getOwnPropertySymbols(e);n&&(t=t.filter((function(n){return Object.getOwnPropertyDescriptor(e,n).enumerable}))),a.push.apply(a,t)}return a}function l(e){for(var n=1;n<arguments.length;n++){var a=null!=arguments[n]?arguments[n]:{};n%2?r(Object(a),!0).forEach((function(n){i(e,n,a[n])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(a)):r(Object(a)).forEach((function(n){Object.defineProperty(e,n,Object.getOwnPropertyDescriptor(a,n))}))}return e}function o(e,n){if(null==e)return{};var a,t,i=function(e,n){if(null==e)return{};var a,t,i={},r=Object.keys(e);for(t=0;t<r.length;t++)a=r[t],n.indexOf(a)>=0||(i[a]=e[a]);return i}(e,n);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);for(t=0;t<r.length;t++)a=r[t],n.indexOf(a)>=0||Object.prototype.propertyIsEnumerable.call(e,a)&&(i[a]=e[a])}return i}var p=t.createContext({}),c=function(e){var n=t.useContext(p),a=n;return e&&(a="function"==typeof e?e(n):l(l({},n),e)),a},s=function(e){var n=c(e.components);return t.createElement(p.Provider,{value:n},e.children)},u="mdxType",d={inlineCode:"code",wrapper:function(e){var n=e.children;return t.createElement(t.Fragment,{},n)}},g=t.forwardRef((function(e,n){var a=e.components,i=e.mdxType,r=e.originalType,p=e.parentName,s=o(e,["components","mdxType","originalType","parentName"]),u=c(a),g=i,m=u["".concat(p,".").concat(g)]||u[g]||d[g]||r;return a?t.createElement(m,l(l({ref:n},s),{},{components:a})):t.createElement(m,l({ref:n},s))}));function m(e,n){var a=arguments,i=n&&n.mdxType;if("string"==typeof e||i){var r=a.length,l=new Array(r);l[0]=g;var o={};for(var p in n)hasOwnProperty.call(n,p)&&(o[p]=n[p]);o.originalType=e,o[u]="string"==typeof e?e:i,l[1]=o;for(var c=2;c<r;c++)l[c]=a[c];return t.createElement.apply(null,l)}return t.createElement.apply(null,a)}g.displayName="MDXCreateElement"},85162:(e,n,a)=>{a.d(n,{Z:()=>l});var t=a(67294),i=a(86010);const r={tabItem:"tabItem_Ymn6"};function l(e){var n=e.children,a=e.hidden,l=e.className;return t.createElement("div",{role:"tabpanel",className:(0,i.Z)(r.tabItem,l),hidden:a},n)}},74866:(e,n,a)=>{a.d(n,{Z:()=>y});var t=a(87462),i=a(67294),r=a(86010),l=a(12466),o=a(16550),p=a(91980),c=a(67392),s=a(50012);function u(e){return function(e){var n,a;return null!=(n=null==(a=i.Children.map(e,(function(e){if(!e||(0,i.isValidElement)(e)&&(n=e.props)&&"object"==typeof n&&"value"in n)return e;var n;throw new Error("Docusaurus error: Bad <Tabs> child <"+("string"==typeof e.type?e.type:e.type.name)+'>: all children of the <Tabs> component should be <TabItem>, and every <TabItem> should have a unique "value" prop.')})))?void 0:a.filter(Boolean))?n:[]}(e).map((function(e){var n=e.props;return{value:n.value,label:n.label,attributes:n.attributes,default:n.default}}))}function d(e){var n=e.values,a=e.children;return(0,i.useMemo)((function(){var e=null!=n?n:u(a);return function(e){var n=(0,c.l)(e,(function(e,n){return e.value===n.value}));if(n.length>0)throw new Error('Docusaurus error: Duplicate values "'+n.map((function(e){return e.value})).join(", ")+'" found in <Tabs>. Every value needs to be unique.')}(e),e}),[n,a])}function g(e){var n=e.value;return e.tabValues.some((function(e){return e.value===n}))}function m(e){var n=e.queryString,a=void 0!==n&&n,t=e.groupId,r=(0,o.k6)(),l=function(e){var n=e.queryString,a=void 0!==n&&n,t=e.groupId;if("string"==typeof a)return a;if(!1===a)return null;if(!0===a&&!t)throw new Error('Docusaurus error: The <Tabs> component groupId prop is required if queryString=true, because this value is used as the search param name. You can also provide an explicit value such as queryString="my-search-param".');return null!=t?t:null}({queryString:a,groupId:t});return[(0,p._X)(l),(0,i.useCallback)((function(e){if(l){var n=new URLSearchParams(r.location.search);n.set(l,e),r.replace(Object.assign({},r.location,{search:n.toString()}))}}),[l,r])]}function h(e){var n,a,t,r,l=e.defaultValue,o=e.queryString,p=void 0!==o&&o,c=e.groupId,u=d(e),h=(0,i.useState)((function(){return function(e){var n,a=e.defaultValue,t=e.tabValues;if(0===t.length)throw new Error("Docusaurus error: the <Tabs> component requires at least one <TabItem> children component");if(a){if(!g({value:a,tabValues:t}))throw new Error('Docusaurus error: The <Tabs> has a defaultValue "'+a+'" but none of its children has the corresponding value. Available values are: '+t.map((function(e){return e.value})).join(", ")+". If you intend to show no default tab, use defaultValue={null} instead.");return a}var i=null!=(n=t.find((function(e){return e.default})))?n:t[0];if(!i)throw new Error("Unexpected error: 0 tabValues");return i.value}({defaultValue:l,tabValues:u})})),k=h[0],v=h[1],f=m({queryString:p,groupId:c}),x=f[0],b=f[1],y=(n=function(e){return e?"docusaurus.tab."+e:null}({groupId:c}.groupId),a=(0,s.Nk)(n),t=a[0],r=a[1],[t,(0,i.useCallback)((function(e){n&&r.set(e)}),[n,r])]),I=y[0],N=y[1],q=function(){var e=null!=x?x:I;return g({value:e,tabValues:u})?e:null}();return(0,i.useLayoutEffect)((function(){q&&v(q)}),[q]),{selectedValue:k,selectValue:(0,i.useCallback)((function(e){if(!g({value:e,tabValues:u}))throw new Error("Can't select invalid tab value="+e);v(e),b(e),N(e)}),[b,N,u]),tabValues:u}}var k=a(72389);const v={tabList:"tabList__CuJ",tabItem:"tabItem_LNqP"};function f(e){var n=e.className,a=e.block,o=e.selectedValue,p=e.selectValue,c=e.tabValues,s=[],u=(0,l.o5)().blockElementScrollPositionUntilNextRender,d=function(e){var n=e.currentTarget,a=s.indexOf(n),t=c[a].value;t!==o&&(u(n),p(t))},g=function(e){var n,a=null;switch(e.key){case"Enter":d(e);break;case"ArrowRight":var t,i=s.indexOf(e.currentTarget)+1;a=null!=(t=s[i])?t:s[0];break;case"ArrowLeft":var r,l=s.indexOf(e.currentTarget)-1;a=null!=(r=s[l])?r:s[s.length-1]}null==(n=a)||n.focus()};return i.createElement("ul",{role:"tablist","aria-orientation":"horizontal",className:(0,r.Z)("tabs",{"tabs--block":a},n)},c.map((function(e){var n=e.value,a=e.label,l=e.attributes;return i.createElement("li",(0,t.Z)({role:"tab",tabIndex:o===n?0:-1,"aria-selected":o===n,key:n,ref:function(e){return s.push(e)},onKeyDown:g,onClick:d},l,{className:(0,r.Z)("tabs__item",v.tabItem,null==l?void 0:l.className,{"tabs__item--active":o===n})}),null!=a?a:n)})))}function x(e){var n=e.lazy,a=e.children,t=e.selectedValue,r=(Array.isArray(a)?a:[a]).filter(Boolean);if(n){var l=r.find((function(e){return e.props.value===t}));return l?(0,i.cloneElement)(l,{className:"margin-top--md"}):null}return i.createElement("div",{className:"margin-top--md"},r.map((function(e,n){return(0,i.cloneElement)(e,{key:n,hidden:e.props.value!==t})})))}function b(e){var n=h(e);return i.createElement("div",{className:(0,r.Z)("tabs-container",v.tabList)},i.createElement(f,(0,t.Z)({},e,n)),i.createElement(x,(0,t.Z)({},e,n)))}function y(e){var n=(0,k.Z)();return i.createElement(b,(0,t.Z)({key:String(n)},e))}},98472:(e,n,a)=>{a.r(n),a.d(n,{assets:()=>d,contentTitle:()=>s,default:()=>k,frontMatter:()=>c,metadata:()=>u,toc:()=>g});var t=a(87462),i=a(63366),r=(a(67294),a(3905)),l=a(74866),o=a(85162),p=["components"],c={id:"client-serialization",title:"Client Serialization"},s=void 0,u={unversionedId:"client/client-serialization",id:"version-5.x.x/client/client-serialization",title:"Client Serialization",description:"GraphQL Kotlin build plugins can generate GraphQL client data classes that are compatible with Jackson",source:"@site/versioned_docs/version-5.x.x/client/client-serialization.mdx",sourceDirName:"client",slug:"/client/client-serialization",permalink:"/graphql-kotlin/docs/5.x.x/client/client-serialization",draft:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-5.x.x/client/client-serialization.mdx",tags:[],version:"5.x.x",lastUpdatedBy:"Dariusz Kuc",lastUpdatedAt:1694122111,formattedLastUpdatedAt:"Sep 7, 2023",frontMatter:{id:"client-serialization",title:"Client Serialization"},sidebar:"version-5.x.x/docs",previous:{title:"Client Customization",permalink:"/graphql-kotlin/docs/5.x.x/client/client-customization"},next:{title:"Tasks",permalink:"/graphql-kotlin/docs/5.x.x/plugins/gradle-plugin-tasks"}},d={},g=[{value:"GraphQL Kotlin Spring Client",id:"graphql-kotlin-spring-client",level:2},{value:"Using Jackson",id:"using-jackson",level:3},{value:"Using Kotlinx Serialization",id:"using-kotlinx-serialization",level:3},{value:"GraphQL Kotlin Ktor Client",id:"graphql-kotlin-ktor-client",level:2},{value:"Using Kotlinx Serialization",id:"using-kotlinx-serialization-1",level:3},{value:"Using Jackson",id:"using-jackson-1",level:3}],m={toc:g},h="wrapper";function k(e){var n=e.components,a=(0,i.Z)(e,p);return(0,r.kt)(h,(0,t.Z)({},m,a,{components:n,mdxType:"MDXLayout"}),(0,r.kt)("p",null,"GraphQL Kotlin build plugins can generate GraphQL client data classes that are compatible with ",(0,r.kt)("a",{parentName:"p",href:"https://github.com/FasterXML/jackson"},(0,r.kt)("inlineCode",{parentName:"a"},"Jackson")),"\n(default) or ",(0,r.kt)("a",{parentName:"p",href:"https://github.com/Kotlin/kotlinx.serialization"},(0,r.kt)("inlineCode",{parentName:"a"},"kotlinx.serialization"))," data models. By default, GraphQL\nclients will attempt to pick up the appropriate serializer from a classpath - ",(0,r.kt)("inlineCode",{parentName:"p"},"graphql-kotlin-spring-client")," defines implicit\ndependency on ",(0,r.kt)("inlineCode",{parentName:"p"},"Jackson")," based serializer and ",(0,r.kt)("inlineCode",{parentName:"p"},"graphql-kotlin-ktor-client")," define a dependency on a ",(0,r.kt)("inlineCode",{parentName:"p"},"kotlinx.serialization"),"."),(0,r.kt)("p",null,(0,r.kt)("inlineCode",{parentName:"p"},"GraphQLClientSerializer")," is a service provider interface that expose generic serialize/deserialize methods that are used\nby the GraphQL clients to serialize requests to String and deserialize responses from String. By utilizing Java ",(0,r.kt)("a",{parentName:"p",href:"https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/ServiceLoader.html"},"ServiceLoader"),"\nmechanism we can dynamically load appropriate serializer from the classpath. If there are multiple providers on the classpath,\nwe default to select the first one available one on the classpath."),(0,r.kt)("h2",{id:"graphql-kotlin-spring-client"},"GraphQL Kotlin Spring Client"),(0,r.kt)("h3",{id:"using-jackson"},"Using Jackson"),(0,r.kt)("p",null,(0,r.kt)("inlineCode",{parentName:"p"},"Jackson")," is the default serializer used by the build plugins and by GraphQL Kotlin Spring Client."),(0,r.kt)(l.Z,{defaultValue:"gradle",values:[{label:"Gradle",value:"gradle"},{label:"Maven",value:"maven"}],mdxType:"Tabs"},(0,r.kt)(o.Z,{value:"gradle",mdxType:"TabItem"},(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},'// build.gradle.kts\nimport com.expediagroup.graphql.plugin.gradle.graphql\n\ndependencies {\n    implementation("com.expediagroup", "graphql-kotlin-spring-client", $graphQLKotlinVersion) {\n}\n\ngraphql {\n  client {\n    endpoint = "http://localhost:8080/graphql"\n    packageName = "com.example.generated"\n  }\n}\n'))),(0,r.kt)(o.Z,{value:"maven",mdxType:"TabItem"},(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-xml"},"<project>\n    \x3c!-- other elements omitted for brewity --\x3e\n    <dependencies>\n        <dependency>\n            <groupId>com.expediagroup</groupId>\n            <artifactId>graphql-kotlin-spring-client</artifactId>\n            <version>${graphql-kotlin.version}</version>\n        </dependency>\n    </dependencies>\n\n    <build>\n        <plugins>\n            \x3c!-- other plugins omitted for clarity --\x3e\n            <plugin>\n                <groupId>com.expediagroup</groupId>\n                <artifactId>graphql-kotlin-maven-plugin</artifactId>\n                <version>${graphql-kotlin.version}</version>\n                <executions>\n                    <execution>\n                        <goals>\n                            <goal>introspect-schema</goal>\n                            <goal>generate-client</goal>\n                        </goals>\n                        <configuration>\n                            <endpoint>http://localhost:8080/graphql</endpoint>\n                            <packageName>com.example.generated</packageName>\n                        </configuration>\n                    </execution>\n                </executions>\n            </plugin>\n        </plugins>\n    </build>\n</project>\n")))),(0,r.kt)("p",null,"By default, ",(0,r.kt)("inlineCode",{parentName:"p"},"ServiceLoader")," mechanism will load the first available GraphQL client serializer from the classpath."),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},'val client = GraphQLWebClient(\n  url = "http://localhost:8080/graphql"\n  serializer = GraphQLClientJacksonSerializer()\n)\n')),(0,r.kt)("h3",{id:"using-kotlinx-serialization"},"Using Kotlinx Serialization"),(0,r.kt)("p",null,"In order to use ",(0,r.kt)("inlineCode",{parentName:"p"},"kotlinx.serialization")," we need to"),(0,r.kt)("ul",null,(0,r.kt)("li",{parentName:"ul"},"add dependency on ",(0,r.kt)("inlineCode",{parentName:"li"},"graphql-kotlin-client-serialization")),(0,r.kt)("li",{parentName:"ul"},"configure GraphQL plugin to generate ",(0,r.kt)("inlineCode",{parentName:"li"},"kotlinx.serialization")," compatible data models"),(0,r.kt)("li",{parentName:"ul"},"configure corresponding compiler plugin"),(0,r.kt)("li",{parentName:"ul"},"explicitly specify the target serializer during client construction OR exclude ",(0,r.kt)("inlineCode",{parentName:"li"},"graphql-kotlin-client-jackson")," dependency")),(0,r.kt)(l.Z,{defaultValue:"gradle",values:[{label:"Gradle",value:"gradle"},{label:"Maven",value:"maven"}],mdxType:"Tabs"},(0,r.kt)(o.Z,{value:"gradle",mdxType:"TabItem"},(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},'// build.gradle.kts\nimport com.expediagroup.graphql.plugin.gradle.config.GraphQLSerializer\nimport com.expediagroup.graphql.plugin.gradle.graphql\n\nplugins {\n    kotlin("plugin.serialization") version $kotlinVersion\n}\n\ndependencies {\n    implementation("com.expediagroup", "graphql-kotlin-spring-client", $graphQLKotlinVersion) {\n        exclude("com.expediagroup", "graphql-kotlin-client-jackson")\n    }\n    implementation("com.expediagroup", "graphql-kotlin-client-serialization", $graphQLKotlinVersion)\n}\n\ngraphql {\n  client {\n    endpoint = "http://localhost:8080/graphql"\n    packageName = "com.example.generated"\n    serializer = GraphQLSerializer.KOTLINX\n  }\n}\n'))),(0,r.kt)(o.Z,{value:"maven",mdxType:"TabItem"},(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-xml"},"<project>\n    \x3c!-- other elements omitted for brewity --\x3e\n    <dependencies>\n        <dependency>\n            <groupId>com.expediagroup</groupId>\n            <artifactId>graphql-kotlin-spring-client</artifactId>\n            <version>${graphql-kotlin.version}</version>\n            <exclusions>\n                <exclusion>\n                    <groupId>com.expediagroup</groupId>\n                    <artifactId>graphql-kotlin-client-jackson</artifactId>\n                </exclusion>\n            </exclusions>\n        </dependency>\n        <dependency>\n            <groupId>com.expediagroup</groupId>\n            <artifactId>graphql-kotlin-client-serialization</artifactId>\n            <version>${graphql-kotlin.version}</version>\n        </dependency>\n        <dependency>\n            <groupId>org.jetbrains.kotlinx</groupId>\n            <artifactId>kotlinx-serialization-json</artifactId>\n            <version>${kotlinx-serialization.version}</version>\n        </dependency>\n    </dependencies>\n\n    <build>\n        <plugins>\n            \x3c!-- other plugins omitted for clarity --\x3e\n            <plugin>\n                <groupId>org.jetbrains.kotlin</groupId>\n                <artifactId>kotlin-maven-plugin</artifactId>\n                <version>${kotlin.version}</version>\n                <configuration>\n                    <jvmTarget>1.8</jvmTarget>\n                    <compilerPlugins>\n                        <plugin>kotlinx-serialization</plugin>\n                    </compilerPlugins>\n                </configuration>\n                <executions>\n                    <execution>\n                        <id>compile</id>\n                        <goals>\n                            <goal>compile</goal>\n                        </goals>\n                    </execution>\n                    <execution>\n                        <id>test-compile</id>\n                        <goals>\n                            <goal>test-compile</goal>\n                        </goals>\n                    </execution>\n                </executions>\n                <dependencies>\n                    <dependency>\n                        <groupId>org.jetbrains.kotlin</groupId>\n                        <artifactId>kotlin-maven-serialization</artifactId>\n                        <version>${kotlin.version}</version>\n                    </dependency>\n                </dependencies>\n            </plugin>\n            <plugin>\n                <groupId>com.expediagroup</groupId>\n                <artifactId>graphql-kotlin-maven-plugin</artifactId>\n                <version>${graphql-kotlin.version}</version>\n                <executions>\n                    <execution>\n                        <goals>\n                            <goal>introspect-schema</goal>\n                            <goal>generate-client</goal>\n                        </goals>\n                        <configuration>\n                            <endpoint>http://localhost:8080/graphql</endpoint>\n                            <packageName>com.example.generated</packageName>\n                            <serializer>KOTLINX</serializer>\n                        </configuration>\n                    </execution>\n                </executions>\n            </plugin>\n        </plugins>\n    </build>\n</project>\n")))),(0,r.kt)("p",null,"By default, ",(0,r.kt)("inlineCode",{parentName:"p"},"ServiceLoader")," mechanism will load the first available GraphQL client serializer from the classpath. We can\nalso explicitly specify serializer during client construction"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},'val client = GraphQLWebClient(\n  url = "http://localhost:8080/graphql"\n  serializer = GraphQLClientKotlinxSerializer()\n)\n')),(0,r.kt)("h2",{id:"graphql-kotlin-ktor-client"},"GraphQL Kotlin Ktor Client"),(0,r.kt)("h3",{id:"using-kotlinx-serialization-1"},"Using Kotlinx Serialization"),(0,r.kt)("p",null,(0,r.kt)("inlineCode",{parentName:"p"},"kotlinx.serialization")," is the default serializer used by the GraphQL Kotlin Ktor Client. Build plugins default to use\n",(0,r.kt)("inlineCode",{parentName:"p"},"Jackson")," so we have to explicitly configure the tasks/mojos to use appropriate serializer."),(0,r.kt)(l.Z,{defaultValue:"gradle",values:[{label:"Gradle",value:"gradle"},{label:"Maven",value:"maven"}],mdxType:"Tabs"},(0,r.kt)(o.Z,{value:"gradle",mdxType:"TabItem"},(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},'// build.gradle.kts\nimport com.expediagroup.graphql.plugin.gradle.config.GraphQLSerializer\nimport com.expediagroup.graphql.plugin.gradle.graphql\n\nplugins {\n    kotlin("plugin.serialization") version $kotlinVersion\n}\n\ndependencies {\n    implementation("com.expediagroup", "graphql-kotlin-ktor-client", $graphQLKotlinVersion) {\n}\n\ngraphql {\n  client {\n    endpoint = "http://localhost:8080/graphql"\n    packageName = "com.example.generated"\n    serializer = GraphQLSerializer.KOTLINX\n  }\n}\n'))),(0,r.kt)(o.Z,{value:"maven",mdxType:"TabItem"},(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-xml"},"<project>\n    \x3c!-- other elements omitted for brewity --\x3e\n    <dependencies>\n        <dependency>\n            <groupId>com.expediagroup</groupId>\n            <artifactId>graphql-kotlin-ktor-client</artifactId>\n            <version>${graphql-kotlin.version}</version>\n        </dependency>\n        <dependency>\n            <groupId>org.jetbrains.kotlinx</groupId>\n            <artifactId>kotlinx-serialization-json</artifactId>\n            <version>${kotlinx-serialization.version}</version>\n        </dependency>\n    </dependencies>\n\n    <build>\n        <plugins>\n            \x3c!-- other plugins omitted for clarity --\x3e\n            <plugin>\n                <groupId>org.jetbrains.kotlin</groupId>\n                <artifactId>kotlin-maven-plugin</artifactId>\n                <version>${kotlin.version}</version>\n                <configuration>\n                    <jvmTarget>1.8</jvmTarget>\n                    <compilerPlugins>\n                        <plugin>kotlinx-serialization</plugin>\n                    </compilerPlugins>\n                </configuration>\n                <executions>\n                    <execution>\n                        <id>compile</id>\n                        <goals>\n                            <goal>compile</goal>\n                        </goals>\n                    </execution>\n                    <execution>\n                        <id>test-compile</id>\n                        <goals>\n                            <goal>test-compile</goal>\n                        </goals>\n                    </execution>\n                </executions>\n                <dependencies>\n                    <dependency>\n                        <groupId>org.jetbrains.kotlin</groupId>\n                        <artifactId>kotlin-maven-serialization</artifactId>\n                        <version>${kotlin.version}</version>\n                    </dependency>\n                </dependencies>\n            </plugin>\n            <plugin>\n                <groupId>com.expediagroup</groupId>\n                <artifactId>graphql-kotlin-maven-plugin</artifactId>\n                <version>${graphql-kotlin.version}</version>\n                <executions>\n                    <execution>\n                        <goals>\n                            <goal>introspect-schema</goal>\n                            <goal>generate-client</goal>\n                        </goals>\n                        <configuration>\n                            <endpoint>http://localhost:8080/graphql</endpoint>\n                            <packageName>com.example.generated</packageName>\n                            <serializer>KOTLINX</serializer>\n                        </configuration>\n                    </execution>\n                </executions>\n            </plugin>\n        </plugins>\n    </build>\n</project>\n")))),(0,r.kt)("p",null,"By default, ",(0,r.kt)("inlineCode",{parentName:"p"},"ServiceLoader")," mechanism will load the first available GraphQL client serializer from the classpath."),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},'val client = GraphQLKtorClient(\n  url = URL("http://localhost:8080/graphql")\n  serializer = GraphQLClientKotlinxSerializer()\n)\n')),(0,r.kt)("h3",{id:"using-jackson-1"},"Using Jackson"),(0,r.kt)("p",null,"In order to use ",(0,r.kt)("inlineCode",{parentName:"p"},"Jackson")," we need to"),(0,r.kt)("ul",null,(0,r.kt)("li",{parentName:"ul"},"add dependency on ",(0,r.kt)("inlineCode",{parentName:"li"},"graphql-kotlin-client-jackson")),(0,r.kt)("li",{parentName:"ul"},"explicitly specify the target serializer during client construction OR exclude ",(0,r.kt)("inlineCode",{parentName:"li"},"graphql-kotlin-client-serialization")," dependency")),(0,r.kt)(l.Z,{defaultValue:"gradle",values:[{label:"Gradle",value:"gradle"},{label:"Maven",value:"maven"}],mdxType:"Tabs"},(0,r.kt)(o.Z,{value:"gradle",mdxType:"TabItem"},(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},'// build.gradle.kts\nimport com.expediagroup.graphql.plugin.gradle.graphql\n\ndependencies {\n    implementation("com.expediagroup", "graphql-kotlin-ktor-client", $graphQLKotlinVersion) {\n        exclude("com.expediagroup", "graphql-kotlin-client-serialization")\n    }\n    implementation("com.expediagroup", "graphql-kotlin-client-jackson", $graphQLKotlinVersion)\n}\n\ngraphql {\n  client {\n    endpoint = "http://localhost:8080/graphql"\n    packageName = "com.example.generated"\n  }\n}\n'))),(0,r.kt)(o.Z,{value:"maven",mdxType:"TabItem"},(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-xml"},"<project>\n    \x3c!-- other elements omitted for brewity --\x3e\n    <dependencies>\n        <dependency>\n            <groupId>com.expediagroup</groupId>\n            <artifactId>graphql-kotlin-ktor-client</artifactId>\n            <version>${graphql-kotlin.version}</version>\n            <exclusions>\n                <exclusion>\n                    <groupId>com.expediagroup</groupId>\n                    <artifactId>graphql-kotlin-client-serialization</artifactId>\n                </exclusion>\n            </exclusions>\n        </dependency>\n        <dependency>\n            <groupId>com.expediagroup</groupId>\n            <artifactId>graphql-kotlin-client-jackson</artifactId>\n            <version>${graphql-kotlin.version}</version>\n        </dependency>\n    </dependencies>\n\n    <build>\n        <plugins>\n            \x3c!-- other plugins omitted for clarity --\x3e\n            <plugin>\n                <groupId>com.expediagroup</groupId>\n                <artifactId>graphql-kotlin-maven-plugin</artifactId>\n                <version>${graphql-kotlin.version}</version>\n                <executions>\n                    <execution>\n                        <goals>\n                            <goal>introspect-schema</goal>\n                            <goal>generate-client</goal>\n                        </goals>\n                        <configuration>\n                            <endpoint>http://localhost:8080/graphql</endpoint>\n                            <packageName>com.example.generated</packageName>\n                        </configuration>\n                    </execution>\n                </executions>\n            </plugin>\n        </plugins>\n    </build>\n</project>\n")))),(0,r.kt)("p",null,"By default, ",(0,r.kt)("inlineCode",{parentName:"p"},"ServiceLoader")," mechanism will load the first available GraphQL client serializer from the classpath. We can\nalso explicitly specify serializer during client construction"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},'val client = GraphQLKtorClient(\n  url = URL("http://localhost:8080/graphql")\n  serializer = GraphQLClientJacksonSerializer()\n)\n')))}k.isMDXComponent=!0}}]);