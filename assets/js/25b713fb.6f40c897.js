"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[3609],{3905:(e,t,n)=>{n.d(t,{Zo:()=>u,kt:()=>g});var r=n(67294);function a(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function l(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function i(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?l(Object(n),!0).forEach((function(t){a(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):l(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function o(e,t){if(null==e)return{};var n,r,a=function(e,t){if(null==e)return{};var n,r,a={},l=Object.keys(e);for(r=0;r<l.length;r++)n=l[r],t.indexOf(n)>=0||(a[n]=e[n]);return a}(e,t);if(Object.getOwnPropertySymbols){var l=Object.getOwnPropertySymbols(e);for(r=0;r<l.length;r++)n=l[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(a[n]=e[n])}return a}var c=r.createContext({}),s=function(e){var t=r.useContext(c),n=t;return e&&(n="function"==typeof e?e(t):i(i({},t),e)),n},u=function(e){var t=s(e.components);return r.createElement(c.Provider,{value:t},e.children)},p="mdxType",m={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},d=r.forwardRef((function(e,t){var n=e.components,a=e.mdxType,l=e.originalType,c=e.parentName,u=o(e,["components","mdxType","originalType","parentName"]),p=s(n),d=a,g=p["".concat(c,".").concat(d)]||p[d]||m[d]||l;return n?r.createElement(g,i(i({ref:t},u),{},{components:n})):r.createElement(g,i({ref:t},u))}));function g(e,t){var n=arguments,a=t&&t.mdxType;if("string"==typeof e||a){var l=n.length,i=new Array(l);i[0]=d;var o={};for(var c in t)hasOwnProperty.call(t,c)&&(o[c]=t[c]);o.originalType=e,o[p]="string"==typeof e?e:a,i[1]=o;for(var s=2;s<l;s++)i[s]=n[s];return r.createElement.apply(null,i)}return r.createElement.apply(null,n)}d.displayName="MDXCreateElement"},77565:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>u,contentTitle:()=>c,default:()=>g,frontMatter:()=>o,metadata:()=>s,toc:()=>p});var r=n(87462),a=n(63366),l=(n(67294),n(3905)),i=["components"],o={id:"nullability",title:"Nullability",original_id:"nullability"},c=void 0,s={unversionedId:"schema-generator/writing-schemas/nullability",id:"version-3.x.x/schema-generator/writing-schemas/nullability",title:"Nullability",description:"Both GraphQL and Kotlin have a concept of nullable as a marked typed. As a result we can automatically generate null",source:"@site/versioned_docs/version-3.x.x/schema-generator/writing-schemas/nullability.md",sourceDirName:"schema-generator/writing-schemas",slug:"/schema-generator/writing-schemas/nullability",permalink:"/graphql-kotlin/docs/3.x.x/schema-generator/writing-schemas/nullability",draft:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-3.x.x/schema-generator/writing-schemas/nullability.md",tags:[],version:"3.x.x",lastUpdatedBy:"Dariusz Kuc",lastUpdatedAt:1694122111,formattedLastUpdatedAt:"Sep 7, 2023",frontMatter:{id:"nullability",title:"Nullability",original_id:"nullability"},sidebar:"version-3.x.x/docs",previous:{title:"Getting Started with the Schema Generator",permalink:"/graphql-kotlin/docs/3.x.x/schema-generator/schema-generator-getting-started"},next:{title:"Arguments",permalink:"/graphql-kotlin/docs/3.x.x/schema-generator/writing-schemas/arguments"}},u={},p=[],m={toc:p},d="wrapper";function g(e){var t=e.components,n=(0,a.Z)(e,i);return(0,l.kt)(d,(0,r.Z)({},m,n,{components:t,mdxType:"MDXLayout"}),(0,l.kt)("p",null,"Both GraphQL and Kotlin have a concept of ",(0,l.kt)("inlineCode",{parentName:"p"},"nullable")," as a marked typed. As a result we can automatically generate null\nsafe schemas from Kotlin code."),(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-kotlin"},"\nclass SimpleQuery {\n\n    fun generateNullableNumber(): Int? {\n        val num = Random().nextInt(100)\n        return if (num < 50) num else null\n    }\n\n    fun generateNumber(): Int = Random().nextInt(100)\n}\n\n")),(0,l.kt)("p",null,"The above Kotlin code would produce the following GraphQL schema:"),(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-graphql"},"\ntype Query {\n  generateNullableNumber: Int\n\n  generateNumber: Int!\n}\n\n")))}g.isMDXComponent=!0}}]);