"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[1797],{3905:(e,r,t)=>{t.d(r,{Zo:()=>l,kt:()=>g});var n=t(67294);function s(e,r,t){return r in e?Object.defineProperty(e,r,{value:t,enumerable:!0,configurable:!0,writable:!0}):e[r]=t,e}function o(e,r){var t=Object.keys(e);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);r&&(n=n.filter((function(r){return Object.getOwnPropertyDescriptor(e,r).enumerable}))),t.push.apply(t,n)}return t}function p(e){for(var r=1;r<arguments.length;r++){var t=null!=arguments[r]?arguments[r]:{};r%2?o(Object(t),!0).forEach((function(r){s(e,r,t[r])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(t)):o(Object(t)).forEach((function(r){Object.defineProperty(e,r,Object.getOwnPropertyDescriptor(t,r))}))}return e}function a(e,r){if(null==e)return{};var t,n,s=function(e,r){if(null==e)return{};var t,n,s={},o=Object.keys(e);for(n=0;n<o.length;n++)t=o[n],r.indexOf(t)>=0||(s[t]=e[t]);return s}(e,r);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);for(n=0;n<o.length;n++)t=o[n],r.indexOf(t)>=0||Object.prototype.propertyIsEnumerable.call(e,t)&&(s[t]=e[t])}return s}var i=n.createContext({}),c=function(e){var r=n.useContext(i),t=r;return e&&(t="function"==typeof e?e(r):p(p({},r),e)),t},l=function(e){var r=c(e.components);return n.createElement(i.Provider,{value:r},e.children)},u="mdxType",d={inlineCode:"code",wrapper:function(e){var r=e.children;return n.createElement(n.Fragment,{},r)}},f=n.forwardRef((function(e,r){var t=e.components,s=e.mdxType,o=e.originalType,i=e.parentName,l=a(e,["components","mdxType","originalType","parentName"]),u=c(t),f=s,g=u["".concat(i,".").concat(f)]||u[f]||d[f]||o;return t?n.createElement(g,p(p({ref:r},l),{},{components:t})):n.createElement(g,p({ref:r},l))}));function g(e,r){var t=arguments,s=r&&r.mdxType;if("string"==typeof e||s){var o=t.length,p=new Array(o);p[0]=f;var a={};for(var i in r)hasOwnProperty.call(r,i)&&(a[i]=r[i]);a.originalType=e,a[u]="string"==typeof e?e:s,p[1]=a;for(var c=2;c<o;c++)p[c]=t[c];return n.createElement.apply(null,p)}return n.createElement.apply(null,t)}f.displayName="MDXCreateElement"},39994:(e,r,t)=>{t.r(r),t.d(r,{assets:()=>l,contentTitle:()=>i,default:()=>g,frontMatter:()=>a,metadata:()=>c,toc:()=>u});var n=t(87462),s=t(63366),o=(t(67294),t(3905)),p=["components"],a={id:"spring-http-request-response",title:"HTTP Request and Response"},i=void 0,c={unversionedId:"server/spring-server/spring-http-request-response",id:"server/spring-server/spring-http-request-response",title:"HTTP Request and Response",description:"To access the HTTP request and response methods, use  Spring WebFilter.",source:"@site/docs/server/spring-server/spring-http-request-response.md",sourceDirName:"server/spring-server",slug:"/server/spring-server/spring-http-request-response",permalink:"/graphql-kotlin/docs/7.x.x/server/spring-server/spring-http-request-response",draft:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/docs/server/spring-server/spring-http-request-response.md",tags:[],version:"current",lastUpdatedBy:"Dariusz Kuc",lastUpdatedAt:1694122111,formattedLastUpdatedAt:"Sep 7, 2023",frontMatter:{id:"spring-http-request-response",title:"HTTP Request and Response"},sidebar:"docs",previous:{title:"Generating GraphQL Context",permalink:"/graphql-kotlin/docs/7.x.x/server/spring-server/spring-graphql-context"},next:{title:"Automatically Created Beans",permalink:"/graphql-kotlin/docs/7.x.x/server/spring-server/spring-beans"}},l={},u=[],d={toc:u},f="wrapper";function g(e){var r=e.components,t=(0,s.Z)(e,p);return(0,o.kt)(f,(0,n.Z)({},d,t,{components:r,mdxType:"MDXLayout"}),(0,o.kt)("p",null,"To access the HTTP request and response methods, use  ",(0,o.kt)("a",{parentName:"p",href:"https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/server/WebFilter.html"},"Spring WebFilter"),".\nFrom these filters you can modify the request and response, both before and after the GraphQL execution."))}g.isMDXComponent=!0}}]);