"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[1330],{3905:(e,t,n)=>{n.d(t,{Zo:()=>u,kt:()=>h});var r=n(67294);function a(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function i(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function o(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?i(Object(n),!0).forEach((function(t){a(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):i(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function l(e,t){if(null==e)return{};var n,r,a=function(e,t){if(null==e)return{};var n,r,a={},i=Object.keys(e);for(r=0;r<i.length;r++)n=i[r],t.indexOf(n)>=0||(a[n]=e[n]);return a}(e,t);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(r=0;r<i.length;r++)n=i[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(a[n]=e[n])}return a}var c=r.createContext({}),p=function(e){var t=r.useContext(c),n=t;return e&&(n="function"==typeof e?e(t):o(o({},t),e)),n},u=function(e){var t=p(e.components);return r.createElement(c.Provider,{value:t},e.children)},s="mdxType",d={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},m=r.forwardRef((function(e,t){var n=e.components,a=e.mdxType,i=e.originalType,c=e.parentName,u=l(e,["components","mdxType","originalType","parentName"]),s=p(n),m=a,h=s["".concat(c,".").concat(m)]||s[m]||d[m]||i;return n?r.createElement(h,o(o({ref:t},u),{},{components:n})):r.createElement(h,o({ref:t},u))}));function h(e,t){var n=arguments,a=t&&t.mdxType;if("string"==typeof e||a){var i=n.length,o=new Array(i);o[0]=m;var l={};for(var c in t)hasOwnProperty.call(t,c)&&(l[c]=t[c]);l.originalType=e,l[s]="string"==typeof e?e:a,o[1]=l;for(var p=2;p<i;p++)o[p]=n[p];return r.createElement.apply(null,o)}return r.createElement.apply(null,n)}m.displayName="MDXCreateElement"},72179:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>u,contentTitle:()=>c,default:()=>h,frontMatter:()=>l,metadata:()=>p,toc:()=>s});var r=n(87462),a=n(63366),i=(n(67294),n(3905)),o=["components"],l={id:"data-fetching-environment",title:"Data Fetching Environment"},c=void 0,p={unversionedId:"schema-generator/execution/data-fetching-environment",id:"version-6.x.x/schema-generator/execution/data-fetching-environment",title:"Data Fetching Environment",description:"Each resolver has access to a DataFetchingEnvironment that provides additional information about the currently executed query including information about what data is requested",source:"@site/versioned_docs/version-6.x.x/schema-generator/execution/data-fetching-environment.md",sourceDirName:"schema-generator/execution",slug:"/schema-generator/execution/data-fetching-environment",permalink:"/graphql-kotlin/docs/6.x.x/schema-generator/execution/data-fetching-environment",draft:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-6.x.x/schema-generator/execution/data-fetching-environment.md",tags:[],version:"6.x.x",lastUpdatedBy:"eocantu",lastUpdatedAt:1697752782,formattedLastUpdatedAt:"Oct 19, 2023",frontMatter:{id:"data-fetching-environment",title:"Data Fetching Environment"},sidebar:"docs",previous:{title:"Exceptions and Partial Data",permalink:"/graphql-kotlin/docs/6.x.x/schema-generator/execution/exceptions"},next:{title:"Contextual Data",permalink:"/graphql-kotlin/docs/6.x.x/schema-generator/execution/contextual-data"}},u={},s=[],d={toc:s},m="wrapper";function h(e){var t=e.components,n=(0,a.Z)(e,o);return(0,i.kt)(m,(0,r.Z)({},d,n,{components:t,mdxType:"MDXLayout"}),(0,i.kt)("p",null,"Each resolver has access to a ",(0,i.kt)("inlineCode",{parentName:"p"},"DataFetchingEnvironment")," that provides additional information about the currently executed query including information about what data is requested\nas well as details about current execution state. For more details on the ",(0,i.kt)("inlineCode",{parentName:"p"},"DataFetchingEnvironment")," please refer to ",(0,i.kt)("a",{parentName:"p",href:"https://www.graphql-java.com/documentation/v14/data-fetching/"},"graphql-java documentation")),(0,i.kt)("p",null,"You can access this info by including the ",(0,i.kt)("inlineCode",{parentName:"p"},"DataFetchingEnvironment")," as one of the arguments to a Kotlin function. This argument will be automatically populated and injected\nduring the query execution but will not be included in the schema definition."),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-kotlin"},'class Query {\n    fun printEnvironmentInfo(parentField: String): MyObject = MyObject()\n}\n\nclass MyObject {\n  fun printParentField(childField: String, environment: DataFetchingEnvironment): String {\n    val parentField = environment.executionStepInfo.parent.getArgument("parentField")\n    return "The parentField was $parentField and the childField was $childField"\n  }\n}\n')),(0,i.kt)("p",null,"This will produce the following schema"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-graphql"},"type Query {\n  printEnvironmentInfo(parentField: String!): MyObject!\n}\n\ntype MyObject {\n  printParentField(childField: String!): String!\n}\n")),(0,i.kt)("p",null,"Then the following query would return ",(0,i.kt)("inlineCode",{parentName:"p"},'"The parentField was foo and the childField was bar"')),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-graphql"},'{\n  printEnvironmentInfo(parentField: "foo") {\n    printParentField(childField: "bar")\n  }\n}\n')),(0,i.kt)("p",null,"You can also use this to retrieve arguments and query information from higher up the query chain. You can see a working\nexample in the ",(0,i.kt)("inlineCode",{parentName:"p"},"graphql-kotlin-spring-example")," module [",(0,i.kt)("a",{parentName:"p",href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/master/examples/spring/src/main/kotlin/com/expediagroup/graphql/examples/query/EnvironmentQuery.kt"},"link"),"]."))}h.isMDXComponent=!0}}]);