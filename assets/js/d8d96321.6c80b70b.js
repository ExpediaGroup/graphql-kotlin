"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[3385],{3905:(e,t,n)=>{n.d(t,{Zo:()=>c,kt:()=>h});var r=n(67294);function o(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function i(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function a(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?i(Object(n),!0).forEach((function(t){o(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):i(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function s(e,t){if(null==e)return{};var n,r,o=function(e,t){if(null==e)return{};var n,r,o={},i=Object.keys(e);for(r=0;r<i.length;r++)n=i[r],t.indexOf(n)>=0||(o[n]=e[n]);return o}(e,t);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(r=0;r<i.length;r++)n=i[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(o[n]=e[n])}return o}var l=r.createContext({}),p=function(e){var t=r.useContext(l),n=t;return e&&(n="function"==typeof e?e(t):a(a({},t),e)),n},c=function(e){var t=p(e.components);return r.createElement(l.Provider,{value:t},e.children)},u="mdxType",m={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},d=r.forwardRef((function(e,t){var n=e.components,o=e.mdxType,i=e.originalType,l=e.parentName,c=s(e,["components","mdxType","originalType","parentName"]),u=p(n),d=o,h=u["".concat(l,".").concat(d)]||u[d]||m[d]||i;return n?r.createElement(h,a(a({ref:t},c),{},{components:n})):r.createElement(h,a({ref:t},c))}));function h(e,t){var n=arguments,o=t&&t.mdxType;if("string"==typeof e||o){var i=n.length,a=new Array(i);a[0]=d;var s={};for(var l in t)hasOwnProperty.call(t,l)&&(s[l]=t[l]);s.originalType=e,s[u]="string"==typeof e?e:o,a[1]=s;for(var p=2;p<i;p++)a[p]=n[p];return r.createElement.apply(null,a)}return r.createElement.apply(null,n)}d.displayName="MDXCreateElement"},13048:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>c,contentTitle:()=>l,default:()=>h,frontMatter:()=>s,metadata:()=>p,toc:()=>u});var r=n(87462),o=n(63366),i=(n(67294),n(3905)),a=["components"],s={id:"introspection",title:"Introspection"},l=void 0,p={unversionedId:"schema-generator/execution/introspection",id:"version-4.x.x/schema-generator/execution/introspection",title:"Introspection",description:"By default, GraphQL servers expose a built-in system, called introspection, that exposes details about the underlying schema.",source:"@site/versioned_docs/version-4.x.x/schema-generator/execution/introspection.md",sourceDirName:"schema-generator/execution",slug:"/schema-generator/execution/introspection",permalink:"/graphql-kotlin/docs/4.x.x/schema-generator/execution/introspection",draft:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-4.x.x/schema-generator/execution/introspection.md",tags:[],version:"4.x.x",lastUpdatedBy:"Dariusz Kuc",lastUpdatedAt:1694122111,formattedLastUpdatedAt:"Sep 7, 2023",frontMatter:{id:"introspection",title:"Introspection"},sidebar:"version-4.x.x/docs",previous:{title:"Subscriptions",permalink:"/graphql-kotlin/docs/4.x.x/schema-generator/execution/subscriptions"},next:{title:"Apollo Federation",permalink:"/graphql-kotlin/docs/4.x.x/schema-generator/federation/apollo-federation"}},c={},u=[{value:"Introspection types",id:"introspection-types",level:2},{value:"Disabling Introspection",id:"disabling-introspection",level:2}],m={toc:u},d="wrapper";function h(e){var t=e.components,n=(0,o.Z)(e,a);return(0,i.kt)(d,(0,r.Z)({},m,n,{components:t,mdxType:"MDXLayout"}),(0,i.kt)("p",null,"By default, GraphQL servers expose a built-in system, called ",(0,i.kt)("strong",{parentName:"p"},"introspection"),", that exposes details about the underlying schema.\nClients can use introspection to obtain information about all the supported queries as well as all the types exposed in the schema."),(0,i.kt)("h2",{id:"introspection-types"},"Introspection types"),(0,i.kt)("ul",null,(0,i.kt)("li",{parentName:"ul"},(0,i.kt)("em",{parentName:"li"},"_","_","schema")," - root level query field that provides information about all entry points (e.g. ",(0,i.kt)("inlineCode",{parentName:"li"},"queryType"),"), all types exposed\nby the schema (including built-in scalars and introspection types) as well as all directives supported by the system"),(0,i.kt)("li",{parentName:"ul"},(0,i.kt)("em",{parentName:"li"},"_","_","type(name: String!)")," - root level query field that provides information about the requested type (if it exists)"),(0,i.kt)("li",{parentName:"ul"},(0,i.kt)("strong",{parentName:"li"},(0,i.kt)("em",{parentName:"strong"},"typename"))," - field that can be added to ",(0,i.kt)("em",{parentName:"li"},"ANY")," selection and will return the name of the enclosing type,\nis often used in polymorphic queries in order to easily determine underlying implementation type"),(0,i.kt)("li",{parentName:"ul"},(0,i.kt)("strong",{parentName:"li"},(0,i.kt)("em",{parentName:"strong"},"Directive, DirectiveLocation, EnumValue, Field, InputValue, Schema, Type, TypeKind"))," - built-in\nintrospection types that are used to describe the schema.")),(0,i.kt)("p",null,"For example, the query below will return a root Query object name as well as names of all types and all directives."),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-graphql"},"query {\n  __schema {\n    queryType {\n      name\n    }\n    types {\n      name\n    }\n    directives {\n      name\n    }\n  }\n}\n")),(0,i.kt)("p",null,"Additional information on introspection can be found on ",(0,i.kt)("a",{parentName:"p",href:"https://graphql.org/learn/introspection/"},"GraphQL.org"),"."),(0,i.kt)("h2",{id:"disabling-introspection"},"Disabling Introspection"),(0,i.kt)("p",null,"Introspection system can be disabled by specifying ",(0,i.kt)("inlineCode",{parentName:"p"},"introspectionEnabled=false")," configuration option on an instance of\n",(0,i.kt)("inlineCode",{parentName:"p"},"SchemaGeneratorConfig")," that will be used by the ",(0,i.kt)("inlineCode",{parentName:"p"},"SchemaGenerator")," to generate the GraphQL schema."),(0,i.kt)("p",null,"Many GraphQL tools (e.g. ",(0,i.kt)("a",{parentName:"p",href:"https://github.com/prisma-labs/graphql-playground"},"GraphQL Playground")," or ",(0,i.kt)("a",{parentName:"p",href:"https://github.com/graphql/graphiql"},"GraphiQL"),")\nrely on introspection queries to function properly. Disabling introspection will prevent clients from accessing ",(0,i.kt)("inlineCode",{parentName:"p"},"__schema"),"\nand ",(0,i.kt)("inlineCode",{parentName:"p"},"__type")," fields. This may break some of the functionality that your clients might rely on and should be used with\nextreme caution."))}h.isMDXComponent=!0}}]);