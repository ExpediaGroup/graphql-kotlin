"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[8668],{3905:(e,t,n)=>{n.d(t,{Zo:()=>s,kt:()=>m});var a=n(67294);function r(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function o(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);t&&(a=a.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,a)}return n}function i(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?o(Object(n),!0).forEach((function(t){r(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):o(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function c(e,t){if(null==e)return{};var n,a,r=function(e,t){if(null==e)return{};var n,a,r={},o=Object.keys(e);for(a=0;a<o.length;a++)n=o[a],t.indexOf(n)>=0||(r[n]=e[n]);return r}(e,t);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);for(a=0;a<o.length;a++)n=o[a],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(r[n]=e[n])}return r}var p=a.createContext({}),l=function(e){var t=a.useContext(p),n=t;return e&&(n="function"==typeof e?e(t):i(i({},t),e)),n},s=function(e){var t=l(e.components);return a.createElement(p.Provider,{value:t},e.children)},u="mdxType",h={inlineCode:"code",wrapper:function(e){var t=e.children;return a.createElement(a.Fragment,{},t)}},d=a.forwardRef((function(e,t){var n=e.components,r=e.mdxType,o=e.originalType,p=e.parentName,s=c(e,["components","mdxType","originalType","parentName"]),u=l(n),d=r,m=u["".concat(p,".").concat(d)]||u[d]||h[d]||o;return n?a.createElement(m,i(i({ref:t},s),{},{components:n})):a.createElement(m,i({ref:t},s))}));function m(e,t){var n=arguments,r=t&&t.mdxType;if("string"==typeof e||r){var o=n.length,i=new Array(o);i[0]=d;var c={};for(var p in t)hasOwnProperty.call(t,p)&&(c[p]=t[p]);c.originalType=e,c[u]="string"==typeof e?e:r,i[1]=c;for(var l=2;l<o;l++)i[l]=n[l];return a.createElement.apply(null,i)}return a.createElement.apply(null,n)}d.displayName="MDXCreateElement"},18773:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>s,contentTitle:()=>p,default:()=>m,frontMatter:()=>c,metadata:()=>l,toc:()=>u});var a=n(87462),r=n(63366),o=(n(67294),n(3905)),i=["components"],c={id:"contextual-data",title:"Contextual Data"},p=void 0,l={unversionedId:"schema-generator/execution/contextual-data",id:"schema-generator/execution/contextual-data",title:"Contextual Data",description:'All GraphQL servers have a concept of a "context". A GraphQL context contains metadata that is useful to the GraphQL',source:"@site/docs/schema-generator/execution/contextual-data.md",sourceDirName:"schema-generator/execution",slug:"/schema-generator/execution/contextual-data",permalink:"/graphql-kotlin/docs/7.x.x/schema-generator/execution/contextual-data",draft:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/docs/schema-generator/execution/contextual-data.md",tags:[],version:"current",lastUpdatedBy:"Dariusz Kuc",lastUpdatedAt:1694122111,formattedLastUpdatedAt:"Sep 7, 2023",frontMatter:{id:"contextual-data",title:"Contextual Data"},sidebar:"docs",previous:{title:"Data Fetching Environment",permalink:"/graphql-kotlin/docs/7.x.x/schema-generator/execution/data-fetching-environment"},next:{title:"Optional Undefined Arguments",permalink:"/graphql-kotlin/docs/7.x.x/schema-generator/execution/optional-undefined-arguments"}},s={},u=[{value:"GraphQL Context Map",id:"graphql-context-map",level:2}],h={toc:u},d="wrapper";function m(e){var t=e.components,n=(0,r.Z)(e,i);return(0,o.kt)(d,(0,a.Z)({},h,n,{components:t,mdxType:"MDXLayout"}),(0,o.kt)("p",null,'All GraphQL servers have a concept of a "context". A GraphQL context contains metadata that is useful to the GraphQL\nserver, but shouldn\'t necessarily be part of the GraphQL schema. A prime example of something that is appropriate\nfor the GraphQL context would be trace headers for an OpenTracing system such as\n',(0,o.kt)("a",{parentName:"p",href:"https://expediadotcom.github.io/haystack"},"Haystack"),". The GraphQL query does not need the information to perform\nits function, but the server needs the information to ensure observability."),(0,o.kt)("p",null,"The contents of the GraphQL context vary across applications and it is up to the GraphQL server developers to decide\nwhat it should contain. ",(0,o.kt)("inlineCode",{parentName:"p"},"graphql-kotlin-server")," provides a simple mechanism to build a context per operation with the\n",(0,o.kt)("a",{parentName:"p",href:"/graphql-kotlin/docs/7.x.x/server/graphql-context-factory"},"GraphQLContextFactory"),".\nIf a custom factory is defined, it will then be used to populate GraphQL context based on the incoming request and make\nit available during execution."),(0,o.kt)("h2",{id:"graphql-context-map"},"GraphQL Context Map"),(0,o.kt)("p",null,"In graphql-java v17 a new context map was added to the ",(0,o.kt)("inlineCode",{parentName:"p"},"DataFetchingEnvironment"),". This is now the way of saving info for execution, and\nyou can access this map through the ",(0,o.kt)("a",{parentName:"p",href:"/graphql-kotlin/docs/7.x.x/schema-generator/execution/data-fetching-environment"},"DataFetchingEnvironment"),"."),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-kotlin"},'class ContextualQuery : Query {\n    fun contextualQuery(\n        dataFetchingEnvironment: DataFetchingEnvironment,\n        value: Int\n    ): String =\n        "The custom value was ${dataFetchingEnvironment.graphQLContext.get("foo")} and the value was $value"\n}\n')))}m.isMDXComponent=!0}}]);