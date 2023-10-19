"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[7222],{3905:(e,n,a)=>{a.d(n,{Zo:()=>p,kt:()=>d});var t=a(67294);function r(e,n,a){return n in e?Object.defineProperty(e,n,{value:a,enumerable:!0,configurable:!0,writable:!0}):e[n]=a,e}function l(e,n){var a=Object.keys(e);if(Object.getOwnPropertySymbols){var t=Object.getOwnPropertySymbols(e);n&&(t=t.filter((function(n){return Object.getOwnPropertyDescriptor(e,n).enumerable}))),a.push.apply(a,t)}return a}function i(e){for(var n=1;n<arguments.length;n++){var a=null!=arguments[n]?arguments[n]:{};n%2?l(Object(a),!0).forEach((function(n){r(e,n,a[n])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(a)):l(Object(a)).forEach((function(n){Object.defineProperty(e,n,Object.getOwnPropertyDescriptor(a,n))}))}return e}function o(e,n){if(null==e)return{};var a,t,r=function(e,n){if(null==e)return{};var a,t,r={},l=Object.keys(e);for(t=0;t<l.length;t++)a=l[t],n.indexOf(a)>=0||(r[a]=e[a]);return r}(e,n);if(Object.getOwnPropertySymbols){var l=Object.getOwnPropertySymbols(e);for(t=0;t<l.length;t++)a=l[t],n.indexOf(a)>=0||Object.prototype.propertyIsEnumerable.call(e,a)&&(r[a]=e[a])}return r}var u=t.createContext({}),s=function(e){var n=t.useContext(u),a=n;return e&&(a="function"==typeof e?e(n):i(i({},n),e)),a},p=function(e){var n=s(e.components);return t.createElement(u.Provider,{value:n},e.children)},c="mdxType",m={inlineCode:"code",wrapper:function(e){var n=e.children;return t.createElement(t.Fragment,{},n)}},g=t.forwardRef((function(e,n){var a=e.components,r=e.mdxType,l=e.originalType,u=e.parentName,p=o(e,["components","mdxType","originalType","parentName"]),c=s(a),g=r,d=c["".concat(u,".").concat(g)]||c[g]||m[g]||l;return a?t.createElement(d,i(i({ref:n},p),{},{components:a})):t.createElement(d,i({ref:n},p))}));function d(e,n){var a=arguments,r=n&&n.mdxType;if("string"==typeof e||r){var l=a.length,i=new Array(l);i[0]=g;var o={};for(var u in n)hasOwnProperty.call(n,u)&&(o[u]=n[u]);o.originalType=e,o[c]="string"==typeof e?e:r,i[1]=o;for(var s=2;s<l;s++)i[s]=a[s];return t.createElement.apply(null,i)}return t.createElement.apply(null,a)}g.displayName="MDXCreateElement"},85162:(e,n,a)=>{a.d(n,{Z:()=>i});var t=a(67294),r=a(86010);const l={tabItem:"tabItem_Ymn6"};function i(e){var n=e.children,a=e.hidden,i=e.className;return t.createElement("div",{role:"tabpanel",className:(0,r.Z)(l.tabItem,i),hidden:a},n)}},74866:(e,n,a)=>{a.d(n,{Z:()=>N});var t=a(87462),r=a(67294),l=a(86010),i=a(12466),o=a(16550),u=a(91980),s=a(67392),p=a(50012);function c(e){return function(e){var n,a;return null!=(n=null==(a=r.Children.map(e,(function(e){if(!e||(0,r.isValidElement)(e)&&(n=e.props)&&"object"==typeof n&&"value"in n)return e;var n;throw new Error("Docusaurus error: Bad <Tabs> child <"+("string"==typeof e.type?e.type:e.type.name)+'>: all children of the <Tabs> component should be <TabItem>, and every <TabItem> should have a unique "value" prop.')})))?void 0:a.filter(Boolean))?n:[]}(e).map((function(e){var n=e.props;return{value:n.value,label:n.label,attributes:n.attributes,default:n.default}}))}function m(e){var n=e.values,a=e.children;return(0,r.useMemo)((function(){var e=null!=n?n:c(a);return function(e){var n=(0,s.l)(e,(function(e,n){return e.value===n.value}));if(n.length>0)throw new Error('Docusaurus error: Duplicate values "'+n.map((function(e){return e.value})).join(", ")+'" found in <Tabs>. Every value needs to be unique.')}(e),e}),[n,a])}function g(e){var n=e.value;return e.tabValues.some((function(e){return e.value===n}))}function d(e){var n=e.queryString,a=void 0!==n&&n,t=e.groupId,l=(0,o.k6)(),i=function(e){var n=e.queryString,a=void 0!==n&&n,t=e.groupId;if("string"==typeof a)return a;if(!1===a)return null;if(!0===a&&!t)throw new Error('Docusaurus error: The <Tabs> component groupId prop is required if queryString=true, because this value is used as the search param name. You can also provide an explicit value such as queryString="my-search-param".');return null!=t?t:null}({queryString:a,groupId:t});return[(0,u._X)(i),(0,r.useCallback)((function(e){if(i){var n=new URLSearchParams(l.location.search);n.set(i,e),l.replace(Object.assign({},l.location,{search:n.toString()}))}}),[i,l])]}function v(e){var n,a,t,l,i=e.defaultValue,o=e.queryString,u=void 0!==o&&o,s=e.groupId,c=m(e),v=(0,r.useState)((function(){return function(e){var n,a=e.defaultValue,t=e.tabValues;if(0===t.length)throw new Error("Docusaurus error: the <Tabs> component requires at least one <TabItem> children component");if(a){if(!g({value:a,tabValues:t}))throw new Error('Docusaurus error: The <Tabs> has a defaultValue "'+a+'" but none of its children has the corresponding value. Available values are: '+t.map((function(e){return e.value})).join(", ")+". If you intend to show no default tab, use defaultValue={null} instead.");return a}var r=null!=(n=t.find((function(e){return e.default})))?n:t[0];if(!r)throw new Error("Unexpected error: 0 tabValues");return r.value}({defaultValue:i,tabValues:c})})),f=v[0],h=v[1],b=d({queryString:u,groupId:s}),k=b[0],y=b[1],N=(n=function(e){return e?"docusaurus.tab."+e:null}({groupId:s}.groupId),a=(0,p.Nk)(n),t=a[0],l=a[1],[t,(0,r.useCallback)((function(e){n&&l.set(e)}),[n,l])]),w=N[0],V=N[1],O=function(){var e=null!=k?k:w;return g({value:e,tabValues:c})?e:null}();return(0,r.useLayoutEffect)((function(){O&&h(O)}),[O]),{selectedValue:f,selectValue:(0,r.useCallback)((function(e){if(!g({value:e,tabValues:c}))throw new Error("Can't select invalid tab value="+e);h(e),y(e),V(e)}),[y,V,c]),tabValues:c}}var f=a(72389);const h={tabList:"tabList__CuJ",tabItem:"tabItem_LNqP"};function b(e){var n=e.className,a=e.block,o=e.selectedValue,u=e.selectValue,s=e.tabValues,p=[],c=(0,i.o5)().blockElementScrollPositionUntilNextRender,m=function(e){var n=e.currentTarget,a=p.indexOf(n),t=s[a].value;t!==o&&(c(n),u(t))},g=function(e){var n,a=null;switch(e.key){case"Enter":m(e);break;case"ArrowRight":var t,r=p.indexOf(e.currentTarget)+1;a=null!=(t=p[r])?t:p[0];break;case"ArrowLeft":var l,i=p.indexOf(e.currentTarget)-1;a=null!=(l=p[i])?l:p[p.length-1]}null==(n=a)||n.focus()};return r.createElement("ul",{role:"tablist","aria-orientation":"horizontal",className:(0,l.Z)("tabs",{"tabs--block":a},n)},s.map((function(e){var n=e.value,a=e.label,i=e.attributes;return r.createElement("li",(0,t.Z)({role:"tab",tabIndex:o===n?0:-1,"aria-selected":o===n,key:n,ref:function(e){return p.push(e)},onKeyDown:g,onClick:m},i,{className:(0,l.Z)("tabs__item",h.tabItem,null==i?void 0:i.className,{"tabs__item--active":o===n})}),null!=a?a:n)})))}function k(e){var n=e.lazy,a=e.children,t=e.selectedValue,l=(Array.isArray(a)?a:[a]).filter(Boolean);if(n){var i=l.find((function(e){return e.props.value===t}));return i?(0,r.cloneElement)(i,{className:"margin-top--md"}):null}return r.createElement("div",{className:"margin-top--md"},l.map((function(e,n){return(0,r.cloneElement)(e,{key:n,hidden:e.props.value!==t})})))}function y(e){var n=v(e);return r.createElement("div",{className:(0,l.Z)("tabs-container",h.tabList)},r.createElement(b,(0,t.Z)({},e,n)),r.createElement(k,(0,t.Z)({},e,n)))}function N(e){var n=(0,f.Z)();return r.createElement(y,(0,t.Z)({key:String(n)},e))}},44441:(e,n,a)=>{a.r(n),a.d(n,{assets:()=>m,contentTitle:()=>p,default:()=>f,frontMatter:()=>s,metadata:()=>c,toc:()=>g});var t=a(87462),r=a(63366),l=(a(67294),a(3905)),i=a(74866),o=a(85162),u=["components"],s={id:"gradle-plugin-usage-graalvm",title:"Gradle Plugin GraalVM Usage",sidebar_label:"GraalVM Native Image"},p=void 0,c={unversionedId:"plugins/gradle-plugin-usage-graalvm",id:"plugins/gradle-plugin-usage-graalvm",title:"Gradle Plugin GraalVM Usage",description:"GraalVm is a high performance runtime from Oracle that supports Ahead-of-Time (AOT) compilation",source:"@site/docs/plugins/gradle-plugin-usage-graalvm.mdx",sourceDirName:"plugins",slug:"/plugins/gradle-plugin-usage-graalvm",permalink:"/graphql-kotlin/docs/plugins/gradle-plugin-usage-graalvm",draft:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/docs/plugins/gradle-plugin-usage-graalvm.mdx",tags:[],version:"current",lastUpdatedBy:"eocantu",lastUpdatedAt:1697752782,formattedLastUpdatedAt:"Oct 19, 2023",frontMatter:{id:"gradle-plugin-usage-graalvm",title:"Gradle Plugin GraalVM Usage",sidebar_label:"GraalVM Native Image"},sidebar:"docs",previous:{title:"Generating SDL",permalink:"/graphql-kotlin/docs/plugins/gradle-plugin-usage-sdl"},next:{title:"Goals Overview",permalink:"/graphql-kotlin/docs/plugins/maven-plugin-goals"}},m={},g=[{value:"Ktor GraalVM Native Image",id:"ktor-graalvm-native-image",level:2},{value:"Spring GraalVM Native Image",id:"spring-graalvm-native-image",level:2}],d={toc:g},v="wrapper";function f(e){var n=e.components,a=(0,r.Z)(e,u);return(0,l.kt)(v,(0,t.Z)({},d,a,{components:n,mdxType:"MDXLayout"}),(0,l.kt)("p",null,(0,l.kt)("a",{parentName:"p",href:"https://www.graalvm.org/"},"GraalVm")," is a high performance runtime from Oracle that supports Ahead-of-Time (AOT) compilation\nthat allows you to build native images. By shifting compilation to the build time, we can create binaries that are\n",(0,l.kt)("strong",{parentName:"p"},"already optimized so they start almost instantaneously with immediate peak performance"),". Compiled code is also much\nmore memory efficient as we no longer need the big memory overhead of running the JVM."),(0,l.kt)("p",null,"In order to generate GraalVM Native image we need to provide the information about all the dynamic JVM features that our\napplication relies on. Since ",(0,l.kt)("inlineCode",{parentName:"p"},"graphql-kotlin")," generates schema directly from your source code using reflections, we need\nto capture this information to make it available at build time. By default, ",(0,l.kt)("inlineCode",{parentName:"p"},"graphql-kotlin")," also relies on classpath scanning\nto look up all polymorphic types implementations as well as to locate all the (Apollo) Federated entity types."),(0,l.kt)("h2",{id:"ktor-graalvm-native-image"},"Ktor GraalVM Native Image"),(0,l.kt)("p",null,"Given following schema"),(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-kotlin"},'class NativeExampleQuery : Query {\n    fun helloWorld() = "Hello World"\n}\n')),(0,l.kt)("p",null,"We first need to configure our server to avoid class scanning. Even though our example schema does not contain any\npolymorphic types, ",(0,l.kt)("strong",{parentName:"p"},"we still need to explicitly opt-out of class scanning by providing type hierarchy"),"."),(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-kotlin"},'fun Application.graphQLModule() {\n    install(GraphQL) {\n        schema {\n            packages = listOf("com.example")\n            queries = listOf(\n                HelloWorldQuery()\n            )\n        }\n        // mapping between interfaces/union KClass and their implementation KClasses\n        typeHierarchy = mapOf()\n    }\n    install(Routing) {\n        graphQLPostRoute()\n        graphiQLRoute()\n    }\n}\n')),(0,l.kt)("p",null,"We then need to update our build with native configuration"),(0,l.kt)(i.Z,{defaultValue:"native",values:[{label:"Original Build File",value:"original"},{label:"Native Build File",value:"native"}],mdxType:"Tabs"},(0,l.kt)(o.Z,{value:"original",mdxType:"TabItem"},(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-kotlin"},'import org.jetbrains.kotlin.gradle.tasks.KotlinCompile\n\nplugins {\n    kotlin("jvm") version "1.7.21"\n    application\n}\n\ndependencies {\n    implementation("com.expediagroup", "graphql-kotlin-ktor-server", $latestGraphQLKotlinVersion)\n    implementation("ch.qos.logback", "logback-classic", "1.4.7")\n    implementation("io.ktor", "ktor-client-cio", "2.2.4")\n}\n\ntasks.withType<KotlinCompile> {\n    kotlinOptions.jvmTarget = "17"\n}\n\napplication {\n    mainClass.set("com.example.ApplicationKt")\n}\n'))),(0,l.kt)(o.Z,{value:"native",mdxType:"TabItem"},(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-kotlin"},'import com.expediagroup.graphql.plugin.gradle.graphql\nimport org.jetbrains.kotlin.gradle.tasks.KotlinCompile\n\nplugins {\n    kotlin("jvm") version "1.7.21"\n    application\n    id("org.graalvm.buildtools.native") version "0.9.21" // (1)\n    id("com.expediagroup.graphql") version $latestGraphQLKotlinVersion // (2)\n}\n\ndependencies {\n    implementation("com.expediagroup", "graphql-kotlin-ktor-server", $latestGraphQLKotlinVersion)\n    implementation("ch.qos.logback", "logback-classic", "1.4.7")\n    implementation("io.ktor", "ktor-client-cio", "2.2.4")\n}\n\ntasks.withType<KotlinCompile> {\n    kotlinOptions.jvmTarget = "17"\n}\n\napplication {\n    mainClass.set("com.example.ApplicationKt")\n}\n\ngraalvmNative { // (3)\n    toolchainDetection.set(false)\n    binaries {\n        named("main") {\n            verbose.set(true)\n            buildArgs.add("--initialize-at-build-time=io.ktor,kotlin,ch.qos.logback,org.slf4j")\n            buildArgs.add("-H:+ReportExceptionStackTraces")\n        }\n        // enable using reachability metadata repository\n        metadataRepository {\n            enabled.set(true)\n        }\n    }\n}\n\ngraphql { // (4)\n    graalVm {\n        packages = listOf("com.example")\n    }\n}\n')),(0,l.kt)("p",null,"We need to make couple changes to our build file to be able to generate GraalVM native image:"),(0,l.kt)("ol",null,(0,l.kt)("li",{parentName:"ol"},"Apply ",(0,l.kt)("a",{parentName:"li",href:"https://graalvm.github.io/native-build-tools/latest/gradle-plugin.html"},"GraalVM Native Gradle plugin")),(0,l.kt)("li",{parentName:"ol"},"Apply GraphQL Kotlin Gradle plugin"),(0,l.kt)("li",{parentName:"ol"},"Configure GraalVM native image"),(0,l.kt)("li",{parentName:"ol"},"Configure GraphQL Kotlin GraalVM extension")))),(0,l.kt)("p",null,"Once the build is configured we can then generate our native image by running ",(0,l.kt)("inlineCode",{parentName:"p"},"nativeCompile")," task."),(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-shell"},"> ./gradlew nativeCompile\n")),(0,l.kt)("p",null,"Native executable image will then be generated under ",(0,l.kt)("inlineCode",{parentName:"p"},"build/native/nativeCompile")," directory."),(0,l.kt)("h2",{id:"spring-graalvm-native-image"},"Spring GraalVM Native Image"),(0,l.kt)("p",null,"Given following schema"),(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-kotlin"},'@Component\nclass NativeExampleQuery : Query {\n    fun helloWorld() = "Hello World"\n}\n')),(0,l.kt)("p",null,"We first need to configure our server to avoid class scanning. Even though our example schema does not contain any\npolymorphic types, ",(0,l.kt)("strong",{parentName:"p"},"we still need to explicitly opt-out of class scanning by providing type hierarchy"),"."),(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-kotlin"},"@SpringBootApplication\nclass Application {\n    @Bean\n    fun typeResolver(): GraphQLTypeResolver = SimpleTypeResolver(mapOf())\n}\n\nfun main(args: Array<String>) {\n    runApplication<Application>(*args)\n}\n")),(0,l.kt)("p",null,"We then need to update our build with native configuration"),(0,l.kt)(i.Z,{defaultValue:"native",values:[{label:"Original Build File",value:"original"},{label:"Native Build File",value:"native"}],mdxType:"Tabs"},(0,l.kt)(o.Z,{value:"original",mdxType:"TabItem"},(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-kotlin"},'import org.jetbrains.kotlin.gradle.tasks.KotlinCompile\n\nplugins {\n    kotlin("jvm") version "1.7.21"\n    kotlin("plugin.spring") version "1.7.21"\n    id("org.springframework.boot") version "3.0.5"\n}\n\ndependencies {\n    implementation("com.expediagroup", "graphql-kotlin-spring-server", $latestGraphQLKotlinVersion)\n}\n\ntasks.withType<KotlinCompile> {\n    kotlinOptions.jvmTarget = "17"\n}\n'))),(0,l.kt)(o.Z,{value:"native",mdxType:"TabItem"},(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-kotlin"},'import com.expediagroup.graphql.plugin.gradle.graphql\nimport org.jetbrains.kotlin.gradle.tasks.KotlinCompile\n\nplugins {\n    kotlin("jvm") version "1.7.21"\n    kotlin("plugin.spring") version "1.7.21"\n    id("org.springframework.boot") version "3.0.6"\n    id("org.graalvm.buildtools.native") version "0.9.21" // (1)\n    id("com.expediagroup.graphql") version $latestGraphQLKotlinVersion // (2)\n}\n\ndependencies {\n    implementation("com.expediagroup", "graphql-kotlin-spring-server", $latestGraphQLKotlinVersion)\n}\n\ntasks.withType<KotlinCompile> {\n    kotlinOptions.jvmTarget = "17"\n}\n\ngraalvmNative { // (3)\n    toolchainDetection.set(false)\n    binaries {\n        named("main") {\n            verbose.set(true)\n        }\n        // enable using reachability metadata repository\n        metadataRepository {\n            enabled.set(true)\n        }\n    }\n}\n\ngraphql { // (4)\n    graalVm {\n        packages = listOf("com.example")\n    }\n}\n')),(0,l.kt)("p",null,"We need to make couple changes to our build file to be able to generate GraalVM native image:"),(0,l.kt)("ol",null,(0,l.kt)("li",{parentName:"ol"},"Apply ",(0,l.kt)("a",{parentName:"li",href:"https://graalvm.github.io/native-build-tools/latest/gradle-plugin.html"},"GraalVM Native Gradle plugin")),(0,l.kt)("li",{parentName:"ol"},"Apply GraphQL Kotlin Gradle plugin"),(0,l.kt)("li",{parentName:"ol"},"Configure GraalVM native image"),(0,l.kt)("li",{parentName:"ol"},"Configure GraphQL Kotlin GraalVM extension")))),(0,l.kt)("p",null,"Once the build is configured we can then generate our native image by running ",(0,l.kt)("inlineCode",{parentName:"p"},"nativeCompile")," task."),(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-shell"},"> ./gradlew nativeCompile\n")),(0,l.kt)("p",null,"Native executable image will then be generated under ",(0,l.kt)("inlineCode",{parentName:"p"},"build/native/nativeCompile")," directory."))}f.isMDXComponent=!0}}]);