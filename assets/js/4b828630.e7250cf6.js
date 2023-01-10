"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[7670],{3905:(e,n,a)=>{a.d(n,{Zo:()=>s,kt:()=>g});var i=a(67294);function t(e,n,a){return n in e?Object.defineProperty(e,n,{value:a,enumerable:!0,configurable:!0,writable:!0}):e[n]=a,e}function l(e,n){var a=Object.keys(e);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);n&&(i=i.filter((function(n){return Object.getOwnPropertyDescriptor(e,n).enumerable}))),a.push.apply(a,i)}return a}function r(e){for(var n=1;n<arguments.length;n++){var a=null!=arguments[n]?arguments[n]:{};n%2?l(Object(a),!0).forEach((function(n){t(e,n,a[n])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(a)):l(Object(a)).forEach((function(n){Object.defineProperty(e,n,Object.getOwnPropertyDescriptor(a,n))}))}return e}function o(e,n){if(null==e)return{};var a,i,t=function(e,n){if(null==e)return{};var a,i,t={},l=Object.keys(e);for(i=0;i<l.length;i++)a=l[i],n.indexOf(a)>=0||(t[a]=e[a]);return t}(e,n);if(Object.getOwnPropertySymbols){var l=Object.getOwnPropertySymbols(e);for(i=0;i<l.length;i++)a=l[i],n.indexOf(a)>=0||Object.prototype.propertyIsEnumerable.call(e,a)&&(t[a]=e[a])}return t}var p=i.createContext({}),c=function(e){var n=i.useContext(p),a=n;return e&&(a="function"==typeof e?e(n):r(r({},n),e)),a},s=function(e){var n=c(e.components);return i.createElement(p.Provider,{value:n},e.children)},u={inlineCode:"code",wrapper:function(e){var n=e.children;return i.createElement(i.Fragment,{},n)}},d=i.forwardRef((function(e,n){var a=e.components,t=e.mdxType,l=e.originalType,p=e.parentName,s=o(e,["components","mdxType","originalType","parentName"]),d=c(a),g=t,m=d["".concat(p,".").concat(g)]||d[g]||u[g]||l;return a?i.createElement(m,r(r({ref:n},s),{},{components:a})):i.createElement(m,r({ref:n},s))}));function g(e,n){var a=arguments,t=n&&n.mdxType;if("string"==typeof e||t){var l=a.length,r=new Array(l);r[0]=d;var o={};for(var p in n)hasOwnProperty.call(n,p)&&(o[p]=n[p]);o.originalType=e,o.mdxType="string"==typeof e?e:t,r[1]=o;for(var c=2;c<l;c++)r[c]=a[c];return i.createElement.apply(null,r)}return i.createElement.apply(null,a)}d.displayName="MDXCreateElement"},85162:(e,n,a)=>{a.d(n,{Z:()=>r});var i=a(67294),t=a(86010);const l="tabItem_Ymn6";function r(e){var n=e.children,a=e.hidden,r=e.className;return i.createElement("div",{role:"tabpanel",className:(0,t.Z)(l,r),hidden:a},n)}},65488:(e,n,a)=>{a.d(n,{Z:()=>g});var i=a(87462),t=a(67294),l=a(86010),r=a(72389),o=a(67392),p=a(7094),c=a(12466);const s="tabList__CuJ",u="tabItem_LNqP";function d(e){var n,a,r=e.lazy,d=e.block,g=e.defaultValue,m=e.values,h=e.groupId,k=e.className,v=t.Children.map(e.children,(function(e){if((0,t.isValidElement)(e)&&"value"in e.props)return e;throw new Error("Docusaurus error: Bad <Tabs> child <"+("string"==typeof e.type?e.type:e.type.name)+'>: all children of the <Tabs> component should be <TabItem>, and every <TabItem> should have a unique "value" prop.')})),f=null!=m?m:v.map((function(e){var n=e.props;return{value:n.value,label:n.label,attributes:n.attributes}})),x=(0,o.l)(f,(function(e,n){return e.value===n.value}));if(x.length>0)throw new Error('Docusaurus error: Duplicate values "'+x.map((function(e){return e.value})).join(", ")+'" found in <Tabs>. Every value needs to be unique.');var b=null===g?g:null!=(n=null!=g?g:null==(a=v.find((function(e){return e.props.default})))?void 0:a.props.value)?n:v[0].props.value;if(null!==b&&!f.some((function(e){return e.value===b})))throw new Error('Docusaurus error: The <Tabs> has a defaultValue "'+b+'" but none of its children has the corresponding value. Available values are: '+f.map((function(e){return e.value})).join(", ")+". If you intend to show no default tab, use defaultValue={null} instead.");var y=(0,p.U)(),I=y.tabGroupChoices,N=y.setTabGroupChoices,z=(0,t.useState)(b),q=z[0],j=z[1],L=[],C=(0,c.o5)().blockElementScrollPositionUntilNextRender;if(null!=h){var T=I[h];null!=T&&T!==q&&f.some((function(e){return e.value===T}))&&j(T)}var w=function(e){var n=e.currentTarget,a=L.indexOf(n),i=f[a].value;i!==q&&(C(n),j(i),null!=h&&N(h,String(i)))},G=function(e){var n,a=null;switch(e.key){case"Enter":w(e);break;case"ArrowRight":var i,t=L.indexOf(e.currentTarget)+1;a=null!=(i=L[t])?i:L[0];break;case"ArrowLeft":var l,r=L.indexOf(e.currentTarget)-1;a=null!=(l=L[r])?l:L[L.length-1]}null==(n=a)||n.focus()};return t.createElement("div",{className:(0,l.Z)("tabs-container",s)},t.createElement("ul",{role:"tablist","aria-orientation":"horizontal",className:(0,l.Z)("tabs",{"tabs--block":d},k)},f.map((function(e){var n=e.value,a=e.label,r=e.attributes;return t.createElement("li",(0,i.Z)({role:"tab",tabIndex:q===n?0:-1,"aria-selected":q===n,key:n,ref:function(e){return L.push(e)},onKeyDown:G,onClick:w},r,{className:(0,l.Z)("tabs__item",u,null==r?void 0:r.className,{"tabs__item--active":q===n})}),null!=a?a:n)}))),r?(0,t.cloneElement)(v.filter((function(e){return e.props.value===q}))[0],{className:"margin-top--md"}):t.createElement("div",{className:"margin-top--md"},v.map((function(e,n){return(0,t.cloneElement)(e,{key:n,hidden:e.props.value!==q})}))))}function g(e){var n=(0,r.Z)();return t.createElement(d,(0,i.Z)({key:String(n)},e))}},98472:(e,n,a)=>{a.r(n),a.d(n,{assets:()=>d,contentTitle:()=>s,default:()=>h,frontMatter:()=>c,metadata:()=>u,toc:()=>g});var i=a(87462),t=a(63366),l=(a(67294),a(3905)),r=a(65488),o=a(85162),p=["components"],c={id:"client-serialization",title:"Client Serialization"},s=void 0,u={unversionedId:"client/client-serialization",id:"version-5.x.x/client/client-serialization",title:"Client Serialization",description:"GraphQL Kotlin build plugins can generate GraphQL client data classes that are compatible with Jackson",source:"@site/versioned_docs/version-5.x.x/client/client-serialization.mdx",sourceDirName:"client",slug:"/client/client-serialization",permalink:"/graphql-kotlin/docs/5.x.x/client/client-serialization",draft:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-5.x.x/client/client-serialization.mdx",tags:[],version:"5.x.x",lastUpdatedBy:"Dariusz Kuc",lastUpdatedAt:1673384839,formattedLastUpdatedAt:"Jan 10, 2023",frontMatter:{id:"client-serialization",title:"Client Serialization"},sidebar:"version-5.x.x/docs",previous:{title:"Client Customization",permalink:"/graphql-kotlin/docs/5.x.x/client/client-customization"},next:{title:"Tasks",permalink:"/graphql-kotlin/docs/5.x.x/plugins/gradle-plugin-tasks"}},d={},g=[{value:"GraphQL Kotlin Spring Client",id:"graphql-kotlin-spring-client",level:2},{value:"Using Jackson",id:"using-jackson",level:3},{value:"Using Kotlinx Serialization",id:"using-kotlinx-serialization",level:3},{value:"GraphQL Kotlin Ktor Client",id:"graphql-kotlin-ktor-client",level:2},{value:"Using Kotlinx Serialization",id:"using-kotlinx-serialization-1",level:3},{value:"Using Jackson",id:"using-jackson-1",level:3}],m={toc:g};function h(e){var n=e.components,a=(0,t.Z)(e,p);return(0,l.kt)("wrapper",(0,i.Z)({},m,a,{components:n,mdxType:"MDXLayout"}),(0,l.kt)("p",null,"GraphQL Kotlin build plugins can generate GraphQL client data classes that are compatible with ",(0,l.kt)("a",{parentName:"p",href:"https://github.com/FasterXML/jackson"},(0,l.kt)("inlineCode",{parentName:"a"},"Jackson")),"\n(default) or ",(0,l.kt)("a",{parentName:"p",href:"https://github.com/Kotlin/kotlinx.serialization"},(0,l.kt)("inlineCode",{parentName:"a"},"kotlinx.serialization"))," data models. By default, GraphQL\nclients will attempt to pick up the appropriate serializer from a classpath - ",(0,l.kt)("inlineCode",{parentName:"p"},"graphql-kotlin-spring-client")," defines implicit\ndependency on ",(0,l.kt)("inlineCode",{parentName:"p"},"Jackson")," based serializer and ",(0,l.kt)("inlineCode",{parentName:"p"},"graphql-kotlin-ktor-client")," define a dependency on a ",(0,l.kt)("inlineCode",{parentName:"p"},"kotlinx.serialization"),"."),(0,l.kt)("p",null,(0,l.kt)("inlineCode",{parentName:"p"},"GraphQLClientSerializer")," is a service provider interface that expose generic serialize/deserialize methods that are used\nby the GraphQL clients to serialize requests to String and deserialize responses from String. By utilizing Java ",(0,l.kt)("a",{parentName:"p",href:"https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/ServiceLoader.html"},"ServiceLoader"),"\nmechanism we can dynamically load appropriate serializer from the classpath. If there are multiple providers on the classpath,\nwe default to select the first one available one on the classpath."),(0,l.kt)("h2",{id:"graphql-kotlin-spring-client"},"GraphQL Kotlin Spring Client"),(0,l.kt)("h3",{id:"using-jackson"},"Using Jackson"),(0,l.kt)("p",null,(0,l.kt)("inlineCode",{parentName:"p"},"Jackson")," is the default serializer used by the build plugins and by GraphQL Kotlin Spring Client."),(0,l.kt)(r.Z,{defaultValue:"gradle",values:[{label:"Gradle",value:"gradle"},{label:"Maven",value:"maven"}],mdxType:"Tabs"},(0,l.kt)(o.Z,{value:"gradle",mdxType:"TabItem"},(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-kotlin"},'// build.gradle.kts\nimport com.expediagroup.graphql.plugin.gradle.graphql\n\ndependencies {\n    implementation("com.expediagroup", "graphql-kotlin-spring-client", $graphQLKotlinVersion) {\n}\n\ngraphql {\n  client {\n    endpoint = "http://localhost:8080/graphql"\n    packageName = "com.example.generated"\n  }\n}\n'))),(0,l.kt)(o.Z,{value:"maven",mdxType:"TabItem"},(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-xml"},"<project>\n    \x3c!-- other elements omitted for brewity --\x3e\n    <dependencies>\n        <dependency>\n            <groupId>com.expediagroup</groupId>\n            <artifactId>graphql-kotlin-spring-client</artifactId>\n            <version>${graphql-kotlin.version}</version>\n        </dependency>\n    </dependencies>\n\n    <build>\n        <plugins>\n            \x3c!-- other plugins omitted for clarity --\x3e\n            <plugin>\n                <groupId>com.expediagroup</groupId>\n                <artifactId>graphql-kotlin-maven-plugin</artifactId>\n                <version>${graphql-kotlin.version}</version>\n                <executions>\n                    <execution>\n                        <goals>\n                            <goal>introspect-schema</goal>\n                            <goal>generate-client</goal>\n                        </goals>\n                        <configuration>\n                            <endpoint>http://localhost:8080/graphql</endpoint>\n                            <packageName>com.example.generated</packageName>\n                        </configuration>\n                    </execution>\n                </executions>\n            </plugin>\n        </plugins>\n    </build>\n</project>\n")))),(0,l.kt)("p",null,"By default, ",(0,l.kt)("inlineCode",{parentName:"p"},"ServiceLoader")," mechanism will load the first available GraphQL client serializer from the classpath."),(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-kotlin"},'val client = GraphQLWebClient(\n  url = "http://localhost:8080/graphql"\n  serializer = GraphQLClientJacksonSerializer()\n)\n')),(0,l.kt)("h3",{id:"using-kotlinx-serialization"},"Using Kotlinx Serialization"),(0,l.kt)("p",null,"In order to use ",(0,l.kt)("inlineCode",{parentName:"p"},"kotlinx.serialization")," we need to"),(0,l.kt)("ul",null,(0,l.kt)("li",{parentName:"ul"},"add dependency on ",(0,l.kt)("inlineCode",{parentName:"li"},"graphql-kotlin-client-serialization")),(0,l.kt)("li",{parentName:"ul"},"configure GraphQL plugin to generate ",(0,l.kt)("inlineCode",{parentName:"li"},"kotlinx.serialization")," compatible data models"),(0,l.kt)("li",{parentName:"ul"},"configure corresponding compiler plugin"),(0,l.kt)("li",{parentName:"ul"},"explicitly specify the target serializer during client construction OR exclude ",(0,l.kt)("inlineCode",{parentName:"li"},"graphql-kotlin-client-jackson")," dependency")),(0,l.kt)(r.Z,{defaultValue:"gradle",values:[{label:"Gradle",value:"gradle"},{label:"Maven",value:"maven"}],mdxType:"Tabs"},(0,l.kt)(o.Z,{value:"gradle",mdxType:"TabItem"},(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-kotlin"},'// build.gradle.kts\nimport com.expediagroup.graphql.plugin.gradle.config.GraphQLSerializer\nimport com.expediagroup.graphql.plugin.gradle.graphql\n\nplugins {\n    kotlin("plugin.serialization") version $kotlinVersion\n}\n\ndependencies {\n    implementation("com.expediagroup", "graphql-kotlin-spring-client", $graphQLKotlinVersion) {\n        exclude("com.expediagroup", "graphql-kotlin-client-jackson")\n    }\n    implementation("com.expediagroup", "graphql-kotlin-client-serialization", $graphQLKotlinVersion)\n}\n\ngraphql {\n  client {\n    endpoint = "http://localhost:8080/graphql"\n    packageName = "com.example.generated"\n    serializer = GraphQLSerializer.KOTLINX\n  }\n}\n'))),(0,l.kt)(o.Z,{value:"maven",mdxType:"TabItem"},(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-xml"},"<project>\n    \x3c!-- other elements omitted for brewity --\x3e\n    <dependencies>\n        <dependency>\n            <groupId>com.expediagroup</groupId>\n            <artifactId>graphql-kotlin-spring-client</artifactId>\n            <version>${graphql-kotlin.version}</version>\n            <exclusions>\n                <exclusion>\n                    <groupId>com.expediagroup</groupId>\n                    <artifactId>graphql-kotlin-client-jackson</artifactId>\n                </exclusion>\n            </exclusions>\n        </dependency>\n        <dependency>\n            <groupId>com.expediagroup</groupId>\n            <artifactId>graphql-kotlin-client-serialization</artifactId>\n            <version>${graphql-kotlin.version}</version>\n        </dependency>\n        <dependency>\n            <groupId>org.jetbrains.kotlinx</groupId>\n            <artifactId>kotlinx-serialization-json</artifactId>\n            <version>${kotlinx-serialization.version}</version>\n        </dependency>\n    </dependencies>\n\n    <build>\n        <plugins>\n            \x3c!-- other plugins omitted for clarity --\x3e\n            <plugin>\n                <groupId>org.jetbrains.kotlin</groupId>\n                <artifactId>kotlin-maven-plugin</artifactId>\n                <version>${kotlin.version}</version>\n                <configuration>\n                    <jvmTarget>1.8</jvmTarget>\n                    <compilerPlugins>\n                        <plugin>kotlinx-serialization</plugin>\n                    </compilerPlugins>\n                </configuration>\n                <executions>\n                    <execution>\n                        <id>compile</id>\n                        <goals>\n                            <goal>compile</goal>\n                        </goals>\n                    </execution>\n                    <execution>\n                        <id>test-compile</id>\n                        <goals>\n                            <goal>test-compile</goal>\n                        </goals>\n                    </execution>\n                </executions>\n                <dependencies>\n                    <dependency>\n                        <groupId>org.jetbrains.kotlin</groupId>\n                        <artifactId>kotlin-maven-serialization</artifactId>\n                        <version>${kotlin.version}</version>\n                    </dependency>\n                </dependencies>\n            </plugin>\n            <plugin>\n                <groupId>com.expediagroup</groupId>\n                <artifactId>graphql-kotlin-maven-plugin</artifactId>\n                <version>${graphql-kotlin.version}</version>\n                <executions>\n                    <execution>\n                        <goals>\n                            <goal>introspect-schema</goal>\n                            <goal>generate-client</goal>\n                        </goals>\n                        <configuration>\n                            <endpoint>http://localhost:8080/graphql</endpoint>\n                            <packageName>com.example.generated</packageName>\n                            <serializer>KOTLINX</serializer>\n                        </configuration>\n                    </execution>\n                </executions>\n            </plugin>\n        </plugins>\n    </build>\n</project>\n")))),(0,l.kt)("p",null,"By default, ",(0,l.kt)("inlineCode",{parentName:"p"},"ServiceLoader")," mechanism will load the first available GraphQL client serializer from the classpath. We can\nalso explicitly specify serializer during client construction"),(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-kotlin"},'val client = GraphQLWebClient(\n  url = "http://localhost:8080/graphql"\n  serializer = GraphQLClientKotlinxSerializer()\n)\n')),(0,l.kt)("h2",{id:"graphql-kotlin-ktor-client"},"GraphQL Kotlin Ktor Client"),(0,l.kt)("h3",{id:"using-kotlinx-serialization-1"},"Using Kotlinx Serialization"),(0,l.kt)("p",null,(0,l.kt)("inlineCode",{parentName:"p"},"kotlinx.serialization")," is the default serializer used by the GraphQL Kotlin Ktor Client. Build plugins default to use\n",(0,l.kt)("inlineCode",{parentName:"p"},"Jackson")," so we have to explicitly configure the tasks/mojos to use appropriate serializer."),(0,l.kt)(r.Z,{defaultValue:"gradle",values:[{label:"Gradle",value:"gradle"},{label:"Maven",value:"maven"}],mdxType:"Tabs"},(0,l.kt)(o.Z,{value:"gradle",mdxType:"TabItem"},(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-kotlin"},'// build.gradle.kts\nimport com.expediagroup.graphql.plugin.gradle.config.GraphQLSerializer\nimport com.expediagroup.graphql.plugin.gradle.graphql\n\nplugins {\n    kotlin("plugin.serialization") version $kotlinVersion\n}\n\ndependencies {\n    implementation("com.expediagroup", "graphql-kotlin-ktor-client", $graphQLKotlinVersion) {\n}\n\ngraphql {\n  client {\n    endpoint = "http://localhost:8080/graphql"\n    packageName = "com.example.generated"\n    serializer = GraphQLSerializer.KOTLINX\n  }\n}\n'))),(0,l.kt)(o.Z,{value:"maven",mdxType:"TabItem"},(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-xml"},"<project>\n    \x3c!-- other elements omitted for brewity --\x3e\n    <dependencies>\n        <dependency>\n            <groupId>com.expediagroup</groupId>\n            <artifactId>graphql-kotlin-ktor-client</artifactId>\n            <version>${graphql-kotlin.version}</version>\n        </dependency>\n        <dependency>\n            <groupId>org.jetbrains.kotlinx</groupId>\n            <artifactId>kotlinx-serialization-json</artifactId>\n            <version>${kotlinx-serialization.version}</version>\n        </dependency>\n    </dependencies>\n\n    <build>\n        <plugins>\n            \x3c!-- other plugins omitted for clarity --\x3e\n            <plugin>\n                <groupId>org.jetbrains.kotlin</groupId>\n                <artifactId>kotlin-maven-plugin</artifactId>\n                <version>${kotlin.version}</version>\n                <configuration>\n                    <jvmTarget>1.8</jvmTarget>\n                    <compilerPlugins>\n                        <plugin>kotlinx-serialization</plugin>\n                    </compilerPlugins>\n                </configuration>\n                <executions>\n                    <execution>\n                        <id>compile</id>\n                        <goals>\n                            <goal>compile</goal>\n                        </goals>\n                    </execution>\n                    <execution>\n                        <id>test-compile</id>\n                        <goals>\n                            <goal>test-compile</goal>\n                        </goals>\n                    </execution>\n                </executions>\n                <dependencies>\n                    <dependency>\n                        <groupId>org.jetbrains.kotlin</groupId>\n                        <artifactId>kotlin-maven-serialization</artifactId>\n                        <version>${kotlin.version}</version>\n                    </dependency>\n                </dependencies>\n            </plugin>\n            <plugin>\n                <groupId>com.expediagroup</groupId>\n                <artifactId>graphql-kotlin-maven-plugin</artifactId>\n                <version>${graphql-kotlin.version}</version>\n                <executions>\n                    <execution>\n                        <goals>\n                            <goal>introspect-schema</goal>\n                            <goal>generate-client</goal>\n                        </goals>\n                        <configuration>\n                            <endpoint>http://localhost:8080/graphql</endpoint>\n                            <packageName>com.example.generated</packageName>\n                            <serializer>KOTLINX</serializer>\n                        </configuration>\n                    </execution>\n                </executions>\n            </plugin>\n        </plugins>\n    </build>\n</project>\n")))),(0,l.kt)("p",null,"By default, ",(0,l.kt)("inlineCode",{parentName:"p"},"ServiceLoader")," mechanism will load the first available GraphQL client serializer from the classpath."),(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-kotlin"},'val client = GraphQLKtorClient(\n  url = URL("http://localhost:8080/graphql")\n  serializer = GraphQLClientKotlinxSerializer()\n)\n')),(0,l.kt)("h3",{id:"using-jackson-1"},"Using Jackson"),(0,l.kt)("p",null,"In order to use ",(0,l.kt)("inlineCode",{parentName:"p"},"Jackson")," we need to"),(0,l.kt)("ul",null,(0,l.kt)("li",{parentName:"ul"},"add dependency on ",(0,l.kt)("inlineCode",{parentName:"li"},"graphql-kotlin-client-jackson")),(0,l.kt)("li",{parentName:"ul"},"explicitly specify the target serializer during client construction OR exclude ",(0,l.kt)("inlineCode",{parentName:"li"},"graphql-kotlin-client-serialization")," dependency")),(0,l.kt)(r.Z,{defaultValue:"gradle",values:[{label:"Gradle",value:"gradle"},{label:"Maven",value:"maven"}],mdxType:"Tabs"},(0,l.kt)(o.Z,{value:"gradle",mdxType:"TabItem"},(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-kotlin"},'// build.gradle.kts\nimport com.expediagroup.graphql.plugin.gradle.graphql\n\ndependencies {\n    implementation("com.expediagroup", "graphql-kotlin-ktor-client", $graphQLKotlinVersion) {\n        exclude("com.expediagroup", "graphql-kotlin-client-serialization")\n    }\n    implementation("com.expediagroup", "graphql-kotlin-client-jackson", $graphQLKotlinVersion)\n}\n\ngraphql {\n  client {\n    endpoint = "http://localhost:8080/graphql"\n    packageName = "com.example.generated"\n  }\n}\n'))),(0,l.kt)(o.Z,{value:"maven",mdxType:"TabItem"},(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-xml"},"<project>\n    \x3c!-- other elements omitted for brewity --\x3e\n    <dependencies>\n        <dependency>\n            <groupId>com.expediagroup</groupId>\n            <artifactId>graphql-kotlin-ktor-client</artifactId>\n            <version>${graphql-kotlin.version}</version>\n            <exclusions>\n                <exclusion>\n                    <groupId>com.expediagroup</groupId>\n                    <artifactId>graphql-kotlin-client-serialization</artifactId>\n                </exclusion>\n            </exclusions>\n        </dependency>\n        <dependency>\n            <groupId>com.expediagroup</groupId>\n            <artifactId>graphql-kotlin-client-jackson</artifactId>\n            <version>${graphql-kotlin.version}</version>\n        </dependency>\n    </dependencies>\n\n    <build>\n        <plugins>\n            \x3c!-- other plugins omitted for clarity --\x3e\n            <plugin>\n                <groupId>com.expediagroup</groupId>\n                <artifactId>graphql-kotlin-maven-plugin</artifactId>\n                <version>${graphql-kotlin.version}</version>\n                <executions>\n                    <execution>\n                        <goals>\n                            <goal>introspect-schema</goal>\n                            <goal>generate-client</goal>\n                        </goals>\n                        <configuration>\n                            <endpoint>http://localhost:8080/graphql</endpoint>\n                            <packageName>com.example.generated</packageName>\n                        </configuration>\n                    </execution>\n                </executions>\n            </plugin>\n        </plugins>\n    </build>\n</project>\n")))),(0,l.kt)("p",null,"By default, ",(0,l.kt)("inlineCode",{parentName:"p"},"ServiceLoader")," mechanism will load the first available GraphQL client serializer from the classpath. We can\nalso explicitly specify serializer during client construction"),(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-kotlin"},'val client = GraphQLKtorClient(\n  url = URL("http://localhost:8080/graphql")\n  serializer = GraphQLClientJacksonSerializer()\n)\n')))}h.isMDXComponent=!0}}]);