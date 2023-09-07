"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[4158],{3905:(e,t,r)=>{r.d(t,{Zo:()=>l,kt:()=>h});var n=r(67294);function a(e,t,r){return t in e?Object.defineProperty(e,t,{value:r,enumerable:!0,configurable:!0,writable:!0}):e[t]=r,e}function o(e,t){var r=Object.keys(e);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);t&&(n=n.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),r.push.apply(r,n)}return r}function c(e){for(var t=1;t<arguments.length;t++){var r=null!=arguments[t]?arguments[t]:{};t%2?o(Object(r),!0).forEach((function(t){a(e,t,r[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(r)):o(Object(r)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(r,t))}))}return e}function s(e,t){if(null==e)return{};var r,n,a=function(e,t){if(null==e)return{};var r,n,a={},o=Object.keys(e);for(n=0;n<o.length;n++)r=o[n],t.indexOf(r)>=0||(a[r]=e[r]);return a}(e,t);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);for(n=0;n<o.length;n++)r=o[n],t.indexOf(r)>=0||Object.prototype.propertyIsEnumerable.call(e,r)&&(a[r]=e[r])}return a}var i=n.createContext({}),p=function(e){var t=n.useContext(i),r=t;return e&&(r="function"==typeof e?e(t):c(c({},t),e)),r},l=function(e){var t=p(e.components);return n.createElement(i.Provider,{value:t},e.children)},u="mdxType",m={inlineCode:"code",wrapper:function(e){var t=e.children;return n.createElement(n.Fragment,{},t)}},d=n.forwardRef((function(e,t){var r=e.components,a=e.mdxType,o=e.originalType,i=e.parentName,l=s(e,["components","mdxType","originalType","parentName"]),u=p(r),d=a,h=u["".concat(i,".").concat(d)]||u[d]||m[d]||o;return r?n.createElement(h,c(c({ref:t},l),{},{components:r})):n.createElement(h,c({ref:t},l))}));function h(e,t){var r=arguments,a=t&&t.mdxType;if("string"==typeof e||a){var o=r.length,c=new Array(o);c[0]=d;var s={};for(var i in t)hasOwnProperty.call(t,i)&&(s[i]=t[i]);s.originalType=e,s[u]="string"==typeof e?e:a,c[1]=s;for(var p=2;p<o;p++)c[p]=r[p];return n.createElement.apply(null,c)}return n.createElement.apply(null,r)}d.displayName="MDXCreateElement"},73912:(e,t,r)=>{r.r(t),r.d(t,{assets:()=>l,contentTitle:()=>i,default:()=>h,frontMatter:()=>s,metadata:()=>p,toc:()=>u});var n=r(87462),a=r(63366),o=(r(67294),r(3905)),c=["components"],s={id:"deprecating-schema",title:"Deprecating Schema"},i=void 0,p={unversionedId:"schema-generator/customizing-schemas/deprecating-schema",id:"version-5.x.x/schema-generator/customizing-schemas/deprecating-schema",title:"Deprecating Schema",description:"GraphQL schemas can have fields marked as deprecated. Instead of creating a custom annotation,",source:"@site/versioned_docs/version-5.x.x/schema-generator/customizing-schemas/deprecating-schema.md",sourceDirName:"schema-generator/customizing-schemas",slug:"/schema-generator/customizing-schemas/deprecating-schema",permalink:"/graphql-kotlin/docs/5.x.x/schema-generator/customizing-schemas/deprecating-schema",draft:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-5.x.x/schema-generator/customizing-schemas/deprecating-schema.md",tags:[],version:"5.x.x",lastUpdatedBy:"Dariusz Kuc",lastUpdatedAt:1694122111,formattedLastUpdatedAt:"Sep 7, 2023",frontMatter:{id:"deprecating-schema",title:"Deprecating Schema"},sidebar:"version-5.x.x/docs",previous:{title:"Directives",permalink:"/graphql-kotlin/docs/5.x.x/schema-generator/customizing-schemas/directives"},next:{title:"Custom Types",permalink:"/graphql-kotlin/docs/5.x.x/schema-generator/customizing-schemas/custom-type-reference"}},l={},u=[],m={toc:u},d="wrapper";function h(e){var t=e.components,r=(0,a.Z)(e,c);return(0,o.kt)(d,(0,n.Z)({},m,r,{components:t,mdxType:"MDXLayout"}),(0,o.kt)("p",null,"GraphQL schemas can have fields marked as deprecated. Instead of creating a custom annotation,\n",(0,o.kt)("inlineCode",{parentName:"p"},"graphql-kotlin-schema-generator")," just looks for the ",(0,o.kt)("inlineCode",{parentName:"p"},"kotlin.Deprecated")," annotation and will use that annotation message\nfor the deprecated reason."),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-kotlin"},'class SimpleQuery {\n  @Deprecated(message = "this query is deprecated", replaceWith = ReplaceWith("shinyNewQuery"))\n  fun simpleDeprecatedQuery(): Boolean = false\n\n  fun shinyNewQuery(): Boolean = true\n}\n')),(0,o.kt)("p",null,"The above query would produce the following GraphQL schema:"),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-graphql"},'type Query {\n  simpleDeprecatedQuery: Boolean! @deprecated(reason: "this query is deprecated, replace with shinyNewQuery")\n\n  shinyNewQuery: Boolean!\n}\n')),(0,o.kt)("p",null,"While you can deprecate any fields/functions/classes in your Kotlin code, GraphQL only supports deprecation directive on\nthe fields (which correspond to Kotlin fields and functions) and enum values."),(0,o.kt)("p",null,"Deprecation of input types is not yet supported ",(0,o.kt)("a",{parentName:"p",href:"https://github.com/graphql/graphql-spec/pull/525"},"in the GraphQL spec"),"."))}h.isMDXComponent=!0}}]);