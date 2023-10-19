"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[2578],{3905:(e,n,t)=>{t.d(n,{Zo:()=>c,kt:()=>m});var a=t(67294);function r(e,n,t){return n in e?Object.defineProperty(e,n,{value:t,enumerable:!0,configurable:!0,writable:!0}):e[n]=t,e}function o(e,n){var t=Object.keys(e);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);n&&(a=a.filter((function(n){return Object.getOwnPropertyDescriptor(e,n).enumerable}))),t.push.apply(t,a)}return t}function i(e){for(var n=1;n<arguments.length;n++){var t=null!=arguments[n]?arguments[n]:{};n%2?o(Object(t),!0).forEach((function(n){r(e,n,t[n])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(t)):o(Object(t)).forEach((function(n){Object.defineProperty(e,n,Object.getOwnPropertyDescriptor(t,n))}))}return e}function l(e,n){if(null==e)return{};var t,a,r=function(e,n){if(null==e)return{};var t,a,r={},o=Object.keys(e);for(a=0;a<o.length;a++)t=o[a],n.indexOf(t)>=0||(r[t]=e[t]);return r}(e,n);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);for(a=0;a<o.length;a++)t=o[a],n.indexOf(t)>=0||Object.prototype.propertyIsEnumerable.call(e,t)&&(r[t]=e[t])}return r}var s=a.createContext({}),p=function(e){var n=a.useContext(s),t=n;return e&&(t="function"==typeof e?e(n):i(i({},n),e)),t},c=function(e){var n=p(e.components);return a.createElement(s.Provider,{value:n},e.children)},u="mdxType",d={inlineCode:"code",wrapper:function(e){var n=e.children;return a.createElement(a.Fragment,{},n)}},g=a.forwardRef((function(e,n){var t=e.components,r=e.mdxType,o=e.originalType,s=e.parentName,c=l(e,["components","mdxType","originalType","parentName"]),u=p(t),g=r,m=u["".concat(s,".").concat(g)]||u[g]||d[g]||o;return t?a.createElement(m,i(i({ref:n},c),{},{components:t})):a.createElement(m,i({ref:n},c))}));function m(e,n){var t=arguments,r=n&&n.mdxType;if("string"==typeof e||r){var o=t.length,i=new Array(o);i[0]=g;var l={};for(var s in n)hasOwnProperty.call(n,s)&&(l[s]=n[s]);l.originalType=e,l[u]="string"==typeof e?e:r,i[1]=l;for(var p=2;p<o;p++)i[p]=t[p];return a.createElement.apply(null,i)}return a.createElement.apply(null,t)}g.displayName="MDXCreateElement"},37460:(e,n,t)=>{t.r(n),t.d(n,{assets:()=>c,contentTitle:()=>s,default:()=>m,frontMatter:()=>l,metadata:()=>p,toc:()=>u});var a=t(87462),r=t(63366),o=(t(67294),t(3905)),i=["components"],l={id:"maven-plugin-usage-sdl",title:"Maven Plugin SDL Usage",sidebar_label:"Generating SDL"},s=void 0,p={unversionedId:"plugins/maven-plugin-usage-sdl",id:"plugins/maven-plugin-usage-sdl",title:"Maven Plugin SDL Usage",description:"GraphQL Kotlin follows a code-first approach where schema is auto generated from your source code at runtime. GraphQL Kotlin",source:"@site/docs/plugins/maven-plugin-usage-sdl.md",sourceDirName:"plugins",slug:"/plugins/maven-plugin-usage-sdl",permalink:"/graphql-kotlin/docs/plugins/maven-plugin-usage-sdl",draft:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/docs/plugins/maven-plugin-usage-sdl.md",tags:[],version:"current",lastUpdatedBy:"eocantu",lastUpdatedAt:1697752782,formattedLastUpdatedAt:"Oct 19, 2023",frontMatter:{id:"maven-plugin-usage-sdl",title:"Maven Plugin SDL Usage",sidebar_label:"Generating SDL"},sidebar:"docs",previous:{title:"Generating Client",permalink:"/graphql-kotlin/docs/plugins/maven-plugin-usage-client"},next:{title:"GraalVM Native Image",permalink:"/graphql-kotlin/docs/plugins/maven-plugin-usage-graalvm"}},c={},u=[{value:"Generating SDL",id:"generating-sdl",level:2},{value:"Using Custom Hooks Provider",id:"using-custom-hooks-provider",level:2}],d={toc:u},g="wrapper";function m(e){var n=e.components,t=(0,r.Z)(e,i);return(0,o.kt)(g,(0,a.Z)({},d,t,{components:n,mdxType:"MDXLayout"}),(0,o.kt)("p",null,"GraphQL Kotlin follows a code-first approach where schema is auto generated from your source code at runtime. GraphQL Kotlin\nplugins can be used to generate schema as a build time artifact. This allows you to seamlessly integrate with various\nGraphQL tools that may require a schema artifact as an input (e.g. to perform backwards compatibility checks, etc)."),(0,o.kt)("h2",{id:"generating-sdl"},"Generating SDL"),(0,o.kt)("p",null,"GraphQL schema can be generated directly from your source code using reflections. ",(0,o.kt)("inlineCode",{parentName:"p"},"generate-sdl")," mojo will scan your\nclasspath looking for classes implementing ",(0,o.kt)("inlineCode",{parentName:"p"},"Query"),", ",(0,o.kt)("inlineCode",{parentName:"p"},"Mutation")," and ",(0,o.kt)("inlineCode",{parentName:"p"},"Subscription")," marker interfaces and then generates the\ncorresponding GraphQL schema using ",(0,o.kt)("inlineCode",{parentName:"p"},"graphql-kotlin-schema-generator")," and default ",(0,o.kt)("inlineCode",{parentName:"p"},"NoopSchemaGeneratorHooks"),". In order to\nlimit the amount of packages to scan, this mojo requires users to provide a list of ",(0,o.kt)("inlineCode",{parentName:"p"},"packages")," that can contain GraphQL\ntypes."),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-xml"},"<plugin>\n    <groupId>com.expediagroup</groupId>\n    <artifactId>graphql-kotlin-maven-plugin</artifactId>\n    <version>${graphql-kotlin.version}</version>\n    <executions>\n        <execution>\n            <goals>\n                <goal>generate-sdl</goal>\n            </goals>\n            <configuration>\n                <packages>\n                    <package>com.example</package>\n                </packages>\n            </configuration>\n        </execution>\n    </executions>\n</plugin>\n")),(0,o.kt)("h2",{id:"using-custom-hooks-provider"},"Using Custom Hooks Provider"),(0,o.kt)("p",null,"Plugin will default to use ",(0,o.kt)("inlineCode",{parentName:"p"},"NoopSchemaGeneratorHooks")," to generate target GraphQL schema. If your project uses custom hooks\nor needs to generate the federated GraphQL schema, you will need to provide an instance of ",(0,o.kt)("inlineCode",{parentName:"p"},"SchemaGeneratorHooksProvider"),"\nservice provider that will be used to create an instance of your custom hooks."),(0,o.kt)("p",null,(0,o.kt)("inlineCode",{parentName:"p"},"generate-sdl")," mojo utilizes ",(0,o.kt)("a",{parentName:"p",href:"https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/ServiceLoader.html"},"ServiceLoader"),"\nmechanism to dynamically load available ",(0,o.kt)("inlineCode",{parentName:"p"},"SchemaGeneratorHooksProvider")," service providers from the classpath. Service provider\ncan be provided as part of your project, included in one of your project dependencies or through explicitly provided artifact.\nSee ",(0,o.kt)("a",{parentName:"p",href:"/graphql-kotlin/docs/plugins/hooks-provider"},"Schema Generator Hooks Provider")," for additional details on how to create custom hooks service provider.\nConfiguration below shows how to configure GraphQL Kotlin plugin with explicitly provided artifact to generate federated\nGraphQL schema."),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-xml"},"<plugin>\n    <groupId>com.expediagroup</groupId>\n    <artifactId>graphql-kotlin-maven-plugin</artifactId>\n    <version>${graphql-kotlin.version}</version>\n    <executions>\n        <execution>\n            <goals>\n                <goal>generate-sdl</goal>\n            </goals>\n            <configuration>\n                <packages>\n                    <package>com.example</package>\n                </packages>\n            </configuration>\n        </execution>\n    </executions>\n    <dependencies>\n        <dependency>\n            <groupId>com.expediagroup</groupId>\n            <artifactId>graphql-kotlin-federated-hooks-provider</artifactId>\n            <version>${graphql-kotlin.version}</version>\n        </dependency>\n    </dependencies>\n</plugin>\n")))}m.isMDXComponent=!0}}]);