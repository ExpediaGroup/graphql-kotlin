"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[181],{3905:(e,t,r)=>{r.d(t,{Zo:()=>d,kt:()=>c});var n=r(67294);function a(e,t,r){return t in e?Object.defineProperty(e,t,{value:r,enumerable:!0,configurable:!0,writable:!0}):e[t]=r,e}function i(e,t){var r=Object.keys(e);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);t&&(n=n.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),r.push.apply(r,n)}return r}function l(e){for(var t=1;t<arguments.length;t++){var r=null!=arguments[t]?arguments[t]:{};t%2?i(Object(r),!0).forEach((function(t){a(e,t,r[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(r)):i(Object(r)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(r,t))}))}return e}function p(e,t){if(null==e)return{};var r,n,a=function(e,t){if(null==e)return{};var r,n,a={},i=Object.keys(e);for(n=0;n<i.length;n++)r=i[n],t.indexOf(r)>=0||(a[r]=e[r]);return a}(e,t);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(n=0;n<i.length;n++)r=i[n],t.indexOf(r)>=0||Object.prototype.propertyIsEnumerable.call(e,r)&&(a[r]=e[r])}return a}var o=n.createContext({}),s=function(e){var t=n.useContext(o),r=t;return e&&(r="function"==typeof e?e(t):l(l({},t),e)),r},d=function(e){var t=s(e.components);return n.createElement(o.Provider,{value:t},e.children)},g={inlineCode:"code",wrapper:function(e){var t=e.children;return n.createElement(n.Fragment,{},t)}},u=n.forwardRef((function(e,t){var r=e.components,a=e.mdxType,i=e.originalType,o=e.parentName,d=p(e,["components","mdxType","originalType","parentName"]),u=s(r),c=a,m=u["".concat(o,".").concat(c)]||u[c]||g[c]||i;return r?n.createElement(m,l(l({ref:t},d),{},{components:r})):n.createElement(m,l({ref:t},d))}));function c(e,t){var r=arguments,a=t&&t.mdxType;if("string"==typeof e||a){var i=r.length,l=new Array(i);l[0]=u;var p={};for(var o in t)hasOwnProperty.call(t,o)&&(p[o]=t[o]);p.originalType=e,p.mdxType="string"==typeof e?e:a,l[1]=p;for(var s=2;s<i;s++)l[s]=r[s];return n.createElement.apply(null,l)}return n.createElement.apply(null,r)}u.displayName="MDXCreateElement"},95671:(e,t,r)=>{r.r(t),r.d(t,{assets:()=>d,contentTitle:()=>o,default:()=>c,frontMatter:()=>p,metadata:()=>s,toc:()=>g});var n=r(87462),a=r(63366),i=(r(67294),r(3905)),l=["components"],p={id:"spring-properties",title:"Configuration Properties",original_id:"spring-properties"},o=void 0,s={unversionedId:"spring-server/spring-properties",id:"version-3.x.x/spring-server/spring-properties",title:"Configuration Properties",description:"graphql-kotlin-spring-server relies on GraphQLConfigurationProperties",source:"@site/versioned_docs/version-3.x.x/spring-server/spring-properties.md",sourceDirName:"spring-server",slug:"/spring-server/spring-properties",permalink:"/graphql-kotlin/docs/3.x.x/spring-server/spring-properties",draft:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-3.x.x/spring-server/spring-properties.md",tags:[],version:"3.x.x",lastUpdatedBy:"Dariusz Kuc",lastUpdatedAt:1673384839,formattedLastUpdatedAt:"Jan 10, 2023",frontMatter:{id:"spring-properties",title:"Configuration Properties",original_id:"spring-properties"},sidebar:"version-3.x.x/docs",previous:{title:"Automatically Created Beans",permalink:"/graphql-kotlin/docs/3.x.x/spring-server/spring-beans"},next:{title:"Writing Schemas with Spring",permalink:"/graphql-kotlin/docs/3.x.x/spring-server/spring-schema"}},d={},g=[],u={toc:g};function c(e){var t=e.components,r=(0,a.Z)(e,l);return(0,i.kt)("wrapper",(0,n.Z)({},u,r,{components:t,mdxType:"MDXLayout"}),(0,i.kt)("p",null,(0,i.kt)("inlineCode",{parentName:"p"},"graphql-kotlin-spring-server")," relies on ",(0,i.kt)("a",{parentName:"p",href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/3.x.x/graphql-kotlin-spring-server/src/main/kotlin/com/expediagroup/graphql/spring/GraphQLConfigurationProperties.kt"},"GraphQLConfigurationProperties"),"\nto provide various customizations of the auto-configuration library. All applicable configuration properties expose ",(0,i.kt)("a",{parentName:"p",href:"https://docs.spring.io/spring-boot/docs/current/reference/html/configuration-metadata.html"},"configuration\nmetadata")," that provide\ndetails on the supported configuration properties."),(0,i.kt)("table",null,(0,i.kt)("thead",{parentName:"table"},(0,i.kt)("tr",{parentName:"thead"},(0,i.kt)("th",{parentName:"tr",align:null},"Property"),(0,i.kt)("th",{parentName:"tr",align:null},"Description"),(0,i.kt)("th",{parentName:"tr",align:null},"Default Value"))),(0,i.kt)("tbody",{parentName:"table"},(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:null},"graphql.endpoint"),(0,i.kt)("td",{parentName:"tr",align:null},"GraphQL server endpoint"),(0,i.kt)("td",{parentName:"tr",align:null},"graphql")),(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:null},"graphql.packages"),(0,i.kt)("td",{parentName:"tr",align:null},"List of supported packages that can contain GraphQL schema type definitions"),(0,i.kt)("td",{parentName:"tr",align:null})),(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:null},"graphql.federation.enabled"),(0,i.kt)("td",{parentName:"tr",align:null},"Boolean flag indicating whether to generate federated GraphQL model"),(0,i.kt)("td",{parentName:"tr",align:null},"false")),(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:null},"graphql.introspection.enabled"),(0,i.kt)("td",{parentName:"tr",align:null},"Boolean flag indicating whether introspection queries are enabled"),(0,i.kt)("td",{parentName:"tr",align:null},"true")),(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:null},"graphql.playground.enabled"),(0,i.kt)("td",{parentName:"tr",align:null},"Boolean flag indicating whether to enabled Prisma Labs Playground GraphQL IDE"),(0,i.kt)("td",{parentName:"tr",align:null},"true")),(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:null},"graphql.playground.endpoint"),(0,i.kt)("td",{parentName:"tr",align:null},"Prisma Labs Playground GraphQL IDE endpoint"),(0,i.kt)("td",{parentName:"tr",align:null},"playground")),(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:null},"graphql.sdl.enabled"),(0,i.kt)("td",{parentName:"tr",align:null},"Boolean flag indicating whether to expose SDL endpoint"),(0,i.kt)("td",{parentName:"tr",align:null},"true")),(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:null},"graphql.sdl.endpoint"),(0,i.kt)("td",{parentName:"tr",align:null},"GraphQL SDL endpoint"),(0,i.kt)("td",{parentName:"tr",align:null},"sdl")),(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:null},"graphql.subscriptions.endpoint"),(0,i.kt)("td",{parentName:"tr",align:null},"GraphQL subscriptions endpoint"),(0,i.kt)("td",{parentName:"tr",align:null},"subscriptions")),(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:null},"graphql.subscriptions.keepAliveInterval"),(0,i.kt)("td",{parentName:"tr",align:null},"Keep the websocket alive and send a message to the client every interval in ms. Defaults to not sending messages"),(0,i.kt)("td",{parentName:"tr",align:null},"null")))))}c.isMDXComponent=!0}}]);