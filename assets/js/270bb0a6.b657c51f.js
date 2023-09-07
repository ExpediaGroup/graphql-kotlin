"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[5346],{3905:(e,r,t)=>{t.d(r,{Zo:()=>c,kt:()=>v});var n=t(67294);function o(e,r,t){return r in e?Object.defineProperty(e,r,{value:t,enumerable:!0,configurable:!0,writable:!0}):e[r]=t,e}function i(e,r){var t=Object.keys(e);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);r&&(n=n.filter((function(r){return Object.getOwnPropertyDescriptor(e,r).enumerable}))),t.push.apply(t,n)}return t}function s(e){for(var r=1;r<arguments.length;r++){var t=null!=arguments[r]?arguments[r]:{};r%2?i(Object(t),!0).forEach((function(r){o(e,r,t[r])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(t)):i(Object(t)).forEach((function(r){Object.defineProperty(e,r,Object.getOwnPropertyDescriptor(t,r))}))}return e}function a(e,r){if(null==e)return{};var t,n,o=function(e,r){if(null==e)return{};var t,n,o={},i=Object.keys(e);for(n=0;n<i.length;n++)t=i[n],r.indexOf(t)>=0||(o[t]=e[t]);return o}(e,r);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(n=0;n<i.length;n++)t=i[n],r.indexOf(t)>=0||Object.prototype.propertyIsEnumerable.call(e,t)&&(o[t]=e[t])}return o}var p=n.createContext({}),l=function(e){var r=n.useContext(p),t=r;return e&&(t="function"==typeof e?e(r):s(s({},r),e)),t},c=function(e){var r=l(e.components);return n.createElement(p.Provider,{value:r},e.children)},u="mdxType",d={inlineCode:"code",wrapper:function(e){var r=e.children;return n.createElement(n.Fragment,{},r)}},f=n.forwardRef((function(e,r){var t=e.components,o=e.mdxType,i=e.originalType,p=e.parentName,c=a(e,["components","mdxType","originalType","parentName"]),u=l(t),f=o,v=u["".concat(p,".").concat(f)]||u[f]||d[f]||i;return t?n.createElement(v,s(s({ref:r},c),{},{components:t})):n.createElement(v,s({ref:r},c))}));function v(e,r){var t=arguments,o=r&&r.mdxType;if("string"==typeof e||o){var i=t.length,s=new Array(i);s[0]=f;var a={};for(var p in r)hasOwnProperty.call(r,p)&&(a[p]=r[p]);a.originalType=e,a[u]="string"==typeof e?e:o,s[1]=a;for(var l=2;l<i;l++)s[l]=t[l];return n.createElement.apply(null,s)}return n.createElement.apply(null,t)}f.displayName="MDXCreateElement"},8157:(e,r,t)=>{t.r(r),t.d(r,{assets:()=>c,contentTitle:()=>p,default:()=>v,frontMatter:()=>a,metadata:()=>l,toc:()=>u});var n=t(87462),o=t(63366),i=(t(67294),t(3905)),s=["components"],a={id:"server-subscriptions",title:"Subscriptions"},p=void 0,l={unversionedId:"server/server-subscriptions",id:"version-5.x.x/server/server-subscriptions",title:"Subscriptions",description:"If you are using one of the official server implementations for GraphQL Kotlin, it will have subscription handling setup for you.",source:"@site/versioned_docs/version-5.x.x/server/server-subscriptions.md",sourceDirName:"server",slug:"/server/server-subscriptions",permalink:"/graphql-kotlin/docs/5.x.x/server/server-subscriptions",draft:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-5.x.x/server/server-subscriptions.md",tags:[],version:"5.x.x",lastUpdatedBy:"Dariusz Kuc",lastUpdatedAt:1694122111,formattedLastUpdatedAt:"Sep 7, 2023",frontMatter:{id:"server-subscriptions",title:"Subscriptions"},sidebar:"version-5.x.x/docs",previous:{title:"Data Loaders",permalink:"/graphql-kotlin/docs/5.x.x/server/data-loaders"},next:{title:"Spring Server Overview",permalink:"/graphql-kotlin/docs/5.x.x/server/spring-server/spring-overview"}},c={},u=[],d={toc:u},f="wrapper";function v(e){var r=e.components,t=(0,o.Z)(e,s);return(0,i.kt)(f,(0,n.Z)({},d,t,{components:r,mdxType:"MDXLayout"}),(0,i.kt)("p",null,"If you are using one of the official server implementations for GraphQL Kotlin, it will have subscription handling setup for you."),(0,i.kt)("ul",null,(0,i.kt)("li",{parentName:"ul"},"See ",(0,i.kt)("inlineCode",{parentName:"li"},"graphql-kotlin-spring-server")," ",(0,i.kt)("a",{parentName:"li",href:"/graphql-kotlin/docs/5.x.x/server/spring-server/spring-subscriptions"},"subscriptions"))),(0,i.kt)("p",null,"Subscriptions require a more indepth knoweldge of how the specific server library handles protocols and streaming.\nSince we can only support ",(0,i.kt)("inlineCode",{parentName:"p"},"Publisher")," from ",(0,i.kt)("inlineCode",{parentName:"p"},"graphql-java")," in this common library, we can not provide any common logic for subscriptions.\nTherefore you will still need to implement the route and request handling for subscriptions separately if you are not using a provided server implementation."))}v.isMDXComponent=!0}}]);