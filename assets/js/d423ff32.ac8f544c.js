"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[3415],{67281:(e,n,a)=>{a.r(n),a.d(n,{assets:()=>c,contentTitle:()=>s,default:()=>g,frontMatter:()=>o,metadata:()=>u,toc:()=>p});var i=a(74848),l=a(28453),t=a(11470),r=a(19365);const o={id:"gradle-plugin-usage-graalvm",title:"Gradle Plugin GraalVM Usage",sidebar_label:"GraalVM Native Image"},s=void 0,u={id:"plugins/gradle-plugin-usage-graalvm",title:"Gradle Plugin GraalVM Usage",description:"GraalVm is a high performance runtime from Oracle that supports Ahead-of-Time (AOT) compilation",source:"@site/docs/plugins/gradle-plugin-usage-graalvm.mdx",sourceDirName:"plugins",slug:"/plugins/gradle-plugin-usage-graalvm",permalink:"/graphql-kotlin/docs/8.x.x/plugins/gradle-plugin-usage-graalvm",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/docs/plugins/gradle-plugin-usage-graalvm.mdx",tags:[],version:"current",lastUpdatedBy:"Tasuku Nakagawa",lastUpdatedAt:1714237421,formattedLastUpdatedAt:"Apr 27, 2024",frontMatter:{id:"gradle-plugin-usage-graalvm",title:"Gradle Plugin GraalVM Usage",sidebar_label:"GraalVM Native Image"},sidebar:"docs",previous:{title:"Generating SDL",permalink:"/graphql-kotlin/docs/8.x.x/plugins/gradle-plugin-usage-sdl"},next:{title:"Goals Overview",permalink:"/graphql-kotlin/docs/8.x.x/plugins/maven-plugin-goals"}},c={},p=[{value:"Ktor GraalVM Native Image",id:"ktor-graalvm-native-image",level:2},{value:"Spring GraalVM Native Image",id:"spring-graalvm-native-image",level:2}];function d(e){const n={a:"a",code:"code",h2:"h2",li:"li",ol:"ol",p:"p",pre:"pre",strong:"strong",...(0,l.R)(),...e.components};return(0,i.jsxs)(i.Fragment,{children:[(0,i.jsxs)(n.p,{children:[(0,i.jsx)(n.a,{href:"https://www.graalvm.org/",children:"GraalVm"})," is a high performance runtime from Oracle that supports Ahead-of-Time (AOT) compilation\nthat allows you to build native images. By shifting compilation to the build time, we can create binaries that are\n",(0,i.jsx)(n.strong,{children:"already optimized so they start almost instantaneously with immediate peak performance"}),". Compiled code is also much\nmore memory efficient as we no longer need the big memory overhead of running the JVM."]}),"\n",(0,i.jsxs)(n.p,{children:["In order to generate GraalVM Native image we need to provide the information about all the dynamic JVM features that our\napplication relies on. Since ",(0,i.jsx)(n.code,{children:"graphql-kotlin"})," generates schema directly from your source code using reflections, we need\nto capture this information to make it available at build time. By default, ",(0,i.jsx)(n.code,{children:"graphql-kotlin"})," also relies on classpath scanning\nto look up all polymorphic types implementations as well as to locate all the (Apollo) Federated entity types."]}),"\n",(0,i.jsx)(n.h2,{id:"ktor-graalvm-native-image",children:"Ktor GraalVM Native Image"}),"\n",(0,i.jsx)(n.p,{children:"Given following schema"}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-kotlin",children:'class NativeExampleQuery : Query {\n    fun helloWorld() = "Hello World"\n}\n'})}),"\n",(0,i.jsxs)(n.p,{children:["We first need to configure our server to avoid class scanning. Even though our example schema does not contain any\npolymorphic types, ",(0,i.jsx)(n.strong,{children:"we still need to explicitly opt-out of class scanning by providing type hierarchy"}),"."]}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-kotlin",children:'fun Application.graphQLModule() {\n    install(GraphQL) {\n        schema {\n            packages = listOf("com.example")\n            queries = listOf(\n                HelloWorldQuery()\n            )\n            // mapping between interfaces/union KClass and their implementation KClasses\n            typeHierarchy = mapOf()\n        }\n    }\n    install(Routing) {\n        graphQLPostRoute()\n        graphiQLRoute()\n    }\n}\n'})}),"\n",(0,i.jsx)(n.p,{children:"We then need to update our build with native configuration"}),"\n",(0,i.jsxs)(t.A,{defaultValue:"native",values:[{label:"Original Build File",value:"original"},{label:"Native Build File",value:"native"}],children:[(0,i.jsx)(r.A,{value:"original",children:(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-kotlin",children:'import org.jetbrains.kotlin.gradle.tasks.KotlinCompile\n\nplugins {\n    kotlin("jvm") version "1.7.21"\n    application\n}\n\ndependencies {\n    implementation("com.expediagroup", "graphql-kotlin-ktor-server", $latestGraphQLKotlinVersion)\n    implementation("ch.qos.logback", "logback-classic", "1.4.7")\n    implementation("io.ktor", "ktor-client-cio", "2.2.4")\n}\n\ntasks.withType<KotlinCompile> {\n    kotlinOptions.jvmTarget = "17"\n}\n\napplication {\n    mainClass.set("com.example.ApplicationKt")\n}\n'})})}),(0,i.jsxs)(r.A,{value:"native",children:[(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-kotlin",children:'import com.expediagroup.graphql.plugin.gradle.graphql\nimport org.jetbrains.kotlin.gradle.tasks.KotlinCompile\n\nplugins {\n    kotlin("jvm") version "1.7.21"\n    application\n    id("org.graalvm.buildtools.native") version "0.9.21" // (1)\n    id("com.expediagroup.graphql") version $latestGraphQLKotlinVersion // (2)\n}\n\ndependencies {\n    implementation("com.expediagroup", "graphql-kotlin-ktor-server", $latestGraphQLKotlinVersion)\n    implementation("ch.qos.logback", "logback-classic", "1.4.7")\n    implementation("io.ktor", "ktor-client-cio", "2.2.4")\n}\n\ntasks.withType<KotlinCompile> {\n    kotlinOptions.jvmTarget = "17"\n}\n\napplication {\n    mainClass.set("com.example.ApplicationKt")\n}\n\ngraalvmNative { // (3)\n    toolchainDetection.set(false)\n    binaries {\n        named("main") {\n            verbose.set(true)\n            buildArgs.add("--initialize-at-build-time=io.ktor,kotlin,ch.qos.logback,org.slf4j")\n            buildArgs.add("-H:+ReportExceptionStackTraces")\n        }\n        // enable using reachability metadata repository\n        metadataRepository {\n            enabled.set(true)\n        }\n    }\n}\n\ngraphql { // (4)\n    graalVm {\n        packages = listOf("com.example")\n    }\n}\n'})}),(0,i.jsx)(n.p,{children:"We need to make couple changes to our build file to be able to generate GraalVM native image:"}),(0,i.jsxs)(n.ol,{children:["\n",(0,i.jsxs)(n.li,{children:["Apply ",(0,i.jsx)(n.a,{href:"https://graalvm.github.io/native-build-tools/latest/gradle-plugin.html",children:"GraalVM Native Gradle plugin"})]}),"\n",(0,i.jsx)(n.li,{children:"Apply GraphQL Kotlin Gradle plugin"}),"\n",(0,i.jsx)(n.li,{children:"Configure GraalVM native image"}),"\n",(0,i.jsx)(n.li,{children:"Configure GraphQL Kotlin GraalVM extension"}),"\n"]})]})]}),"\n",(0,i.jsxs)(n.p,{children:["Once the build is configured we can then generate our native image by running ",(0,i.jsx)(n.code,{children:"nativeCompile"})," task."]}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-shell",children:"> ./gradlew nativeCompile\n"})}),"\n",(0,i.jsxs)(n.p,{children:["Native executable image will then be generated under ",(0,i.jsx)(n.code,{children:"build/native/nativeCompile"})," directory."]}),"\n",(0,i.jsx)(n.h2,{id:"spring-graalvm-native-image",children:"Spring GraalVM Native Image"}),"\n",(0,i.jsx)(n.p,{children:"Given following schema"}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-kotlin",children:'@Component\nclass NativeExampleQuery : Query {\n    fun helloWorld() = "Hello World"\n}\n'})}),"\n",(0,i.jsxs)(n.p,{children:["We first need to configure our server to avoid class scanning. Even though our example schema does not contain any\npolymorphic types, ",(0,i.jsx)(n.strong,{children:"we still need to explicitly opt-out of class scanning by providing type hierarchy"}),"."]}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-kotlin",children:"@SpringBootApplication\nclass Application {\n    @Bean\n    fun typeResolver(): GraphQLTypeResolver = SimpleTypeResolver(mapOf())\n}\n\nfun main(args: Array<String>) {\n    runApplication<Application>(*args)\n}\n"})}),"\n",(0,i.jsx)(n.p,{children:"We then need to update our build with native configuration"}),"\n",(0,i.jsxs)(t.A,{defaultValue:"native",values:[{label:"Original Build File",value:"original"},{label:"Native Build File",value:"native"}],children:[(0,i.jsx)(r.A,{value:"original",children:(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-kotlin",children:'import org.jetbrains.kotlin.gradle.tasks.KotlinCompile\n\nplugins {\n    kotlin("jvm") version "1.7.21"\n    kotlin("plugin.spring") version "1.7.21"\n    id("org.springframework.boot") version "3.0.5"\n}\n\ndependencies {\n    implementation("com.expediagroup", "graphql-kotlin-spring-server", $latestGraphQLKotlinVersion)\n}\n\ntasks.withType<KotlinCompile> {\n    kotlinOptions.jvmTarget = "17"\n}\n'})})}),(0,i.jsxs)(r.A,{value:"native",children:[(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-kotlin",children:'import com.expediagroup.graphql.plugin.gradle.graphql\nimport org.jetbrains.kotlin.gradle.tasks.KotlinCompile\n\nplugins {\n    kotlin("jvm") version "1.7.21"\n    kotlin("plugin.spring") version "1.7.21"\n    id("org.springframework.boot") version "3.0.6"\n    id("org.graalvm.buildtools.native") version "0.9.21" // (1)\n    id("com.expediagroup.graphql") version $latestGraphQLKotlinVersion // (2)\n}\n\ndependencies {\n    implementation("com.expediagroup", "graphql-kotlin-spring-server", $latestGraphQLKotlinVersion)\n}\n\ntasks.withType<KotlinCompile> {\n    kotlinOptions.jvmTarget = "17"\n}\n\ngraalvmNative { // (3)\n    toolchainDetection.set(false)\n    binaries {\n        named("main") {\n            verbose.set(true)\n        }\n        // enable using reachability metadata repository\n        metadataRepository {\n            enabled.set(true)\n        }\n    }\n}\n\ngraphql { // (4)\n    graalVm {\n        packages = listOf("com.example")\n    }\n}\n'})}),(0,i.jsx)(n.p,{children:"We need to make couple changes to our build file to be able to generate GraalVM native image:"}),(0,i.jsxs)(n.ol,{children:["\n",(0,i.jsxs)(n.li,{children:["Apply ",(0,i.jsx)(n.a,{href:"https://graalvm.github.io/native-build-tools/latest/gradle-plugin.html",children:"GraalVM Native Gradle plugin"})]}),"\n",(0,i.jsx)(n.li,{children:"Apply GraphQL Kotlin Gradle plugin"}),"\n",(0,i.jsx)(n.li,{children:"Configure GraalVM native image"}),"\n",(0,i.jsx)(n.li,{children:"Configure GraphQL Kotlin GraalVM extension"}),"\n"]})]})]}),"\n",(0,i.jsxs)(n.p,{children:["Once the build is configured we can then generate our native image by running ",(0,i.jsx)(n.code,{children:"nativeCompile"})," task."]}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-shell",children:"> ./gradlew nativeCompile\n"})}),"\n",(0,i.jsxs)(n.p,{children:["Native executable image will then be generated under ",(0,i.jsx)(n.code,{children:"build/native/nativeCompile"})," directory."]})]})}function g(e={}){const{wrapper:n}={...(0,l.R)(),...e.components};return n?(0,i.jsx)(n,{...e,children:(0,i.jsx)(d,{...e})}):d(e)}},19365:(e,n,a)=>{a.d(n,{A:()=>r});a(96540);var i=a(34164);const l={tabItem:"tabItem_Ymn6"};var t=a(74848);function r(e){var n=e.children,a=e.hidden,r=e.className;return(0,t.jsx)("div",{role:"tabpanel",className:(0,i.A)(l.tabItem,r),hidden:a,children:n})}},11470:(e,n,a)=>{a.d(n,{A:()=>y});var i=a(96540),l=a(34164),t=a(23104),r=a(56347),o=a(205),s=a(57485),u=a(31682),c=a(89466);function p(e){var n,a;return null!=(n=null==(a=i.Children.toArray(e).filter((function(e){return"\n"!==e})).map((function(e){if(!e||(0,i.isValidElement)(e)&&((n=e.props)&&"object"==typeof n&&"value"in n))return e;var n;throw new Error("Docusaurus error: Bad <Tabs> child <"+("string"==typeof e.type?e.type:e.type.name)+'>: all children of the <Tabs> component should be <TabItem>, and every <TabItem> should have a unique "value" prop.')})))?void 0:a.filter(Boolean))?n:[]}function d(e){var n=e.values,a=e.children;return(0,i.useMemo)((function(){var e=null!=n?n:function(e){return p(e).map((function(e){var n=e.props;return{value:n.value,label:n.label,attributes:n.attributes,default:n.default}}))}(a);return function(e){var n=(0,u.X)(e,(function(e,n){return e.value===n.value}));if(n.length>0)throw new Error('Docusaurus error: Duplicate values "'+n.map((function(e){return e.value})).join(", ")+'" found in <Tabs>. Every value needs to be unique.')}(e),e}),[n,a])}function g(e){var n=e.value;return e.tabValues.some((function(e){return e.value===n}))}function m(e){var n=e.queryString,a=void 0!==n&&n,l=e.groupId,t=(0,r.W6)(),o=function(e){var n=e.queryString,a=void 0!==n&&n,i=e.groupId;if("string"==typeof a)return a;if(!1===a)return null;if(!0===a&&!i)throw new Error('Docusaurus error: The <Tabs> component groupId prop is required if queryString=true, because this value is used as the search param name. You can also provide an explicit value such as queryString="my-search-param".');return null!=i?i:null}({queryString:a,groupId:l});return[(0,s.aZ)(o),(0,i.useCallback)((function(e){if(o){var n=new URLSearchParams(t.location.search);n.set(o,e),t.replace(Object.assign({},t.location,{search:n.toString()}))}}),[o,t])]}function h(e){var n,a,l,t,r=e.defaultValue,s=e.queryString,u=void 0!==s&&s,p=e.groupId,h=d(e),v=(0,i.useState)((function(){return function(e){var n,a=e.defaultValue,i=e.tabValues;if(0===i.length)throw new Error("Docusaurus error: the <Tabs> component requires at least one <TabItem> children component");if(a){if(!g({value:a,tabValues:i}))throw new Error('Docusaurus error: The <Tabs> has a defaultValue "'+a+'" but none of its children has the corresponding value. Available values are: '+i.map((function(e){return e.value})).join(", ")+". If you intend to show no default tab, use defaultValue={null} instead.");return a}var l=null!=(n=i.find((function(e){return e.default})))?n:i[0];if(!l)throw new Error("Unexpected error: 0 tabValues");return l.value}({defaultValue:r,tabValues:h})})),f=v[0],b=v[1],x=m({queryString:u,groupId:p}),j=x[0],k=x[1],y=(n=function(e){return e?"docusaurus.tab."+e:null}({groupId:p}.groupId),a=(0,c.Dv)(n),l=a[0],t=a[1],[l,(0,i.useCallback)((function(e){n&&t.set(e)}),[n,t])]),w=y[0],V=y[1],G=function(){var e=null!=j?j:w;return g({value:e,tabValues:h})?e:null}();return(0,o.A)((function(){G&&b(G)}),[G]),{selectedValue:f,selectValue:(0,i.useCallback)((function(e){if(!g({value:e,tabValues:h}))throw new Error("Can't select invalid tab value="+e);b(e),k(e),V(e)}),[k,V,h]),tabValues:h}}var v=a(92303);const f={tabList:"tabList__CuJ",tabItem:"tabItem_LNqP"};var b=a(74848);function x(e){var n=e.className,a=e.block,i=e.selectedValue,r=e.selectValue,o=e.tabValues,s=[],u=(0,t.a_)().blockElementScrollPositionUntilNextRender,c=function(e){var n=e.currentTarget,a=s.indexOf(n),l=o[a].value;l!==i&&(u(n),r(l))},p=function(e){var n,a=null;switch(e.key){case"Enter":c(e);break;case"ArrowRight":var i,l=s.indexOf(e.currentTarget)+1;a=null!=(i=s[l])?i:s[0];break;case"ArrowLeft":var t,r=s.indexOf(e.currentTarget)-1;a=null!=(t=s[r])?t:s[s.length-1]}null==(n=a)||n.focus()};return(0,b.jsx)("ul",{role:"tablist","aria-orientation":"horizontal",className:(0,l.A)("tabs",{"tabs--block":a},n),children:o.map((function(e){var n=e.value,a=e.label,t=e.attributes;return(0,b.jsx)("li",Object.assign({role:"tab",tabIndex:i===n?0:-1,"aria-selected":i===n,ref:function(e){return s.push(e)},onKeyDown:p,onClick:c},t,{className:(0,l.A)("tabs__item",f.tabItem,null==t?void 0:t.className,{"tabs__item--active":i===n}),children:null!=a?a:n}),n)}))})}function j(e){var n=e.lazy,a=e.children,l=e.selectedValue,t=(Array.isArray(a)?a:[a]).filter(Boolean);if(n){var r=t.find((function(e){return e.props.value===l}));return r?(0,i.cloneElement)(r,{className:"margin-top--md"}):null}return(0,b.jsx)("div",{className:"margin-top--md",children:t.map((function(e,n){return(0,i.cloneElement)(e,{key:n,hidden:e.props.value!==l})}))})}function k(e){var n=h(e);return(0,b.jsxs)("div",{className:(0,l.A)("tabs-container",f.tabList),children:[(0,b.jsx)(x,Object.assign({},e,n)),(0,b.jsx)(j,Object.assign({},e,n))]})}function y(e){var n=(0,v.A)();return(0,b.jsx)(k,Object.assign({},e,{children:p(e.children)}),String(n))}},28453:(e,n,a)=>{a.d(n,{R:()=>r,x:()=>o});var i=a(96540);const l={},t=i.createContext(l);function r(e){const n=i.useContext(t);return i.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function o(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(l):e.components||l:r(e.components),i.createElement(t.Provider,{value:n},e.children)}}}]);