"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[1063],{3905:(e,t,a)=>{a.d(t,{Zo:()=>d,kt:()=>h});var n=a(67294);function r(e,t,a){return t in e?Object.defineProperty(e,t,{value:a,enumerable:!0,configurable:!0,writable:!0}):e[t]=a,e}function i(e,t){var a=Object.keys(e);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);t&&(n=n.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),a.push.apply(a,n)}return a}function o(e){for(var t=1;t<arguments.length;t++){var a=null!=arguments[t]?arguments[t]:{};t%2?i(Object(a),!0).forEach((function(t){r(e,t,a[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(a)):i(Object(a)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(a,t))}))}return e}function l(e,t){if(null==e)return{};var a,n,r=function(e,t){if(null==e)return{};var a,n,r={},i=Object.keys(e);for(n=0;n<i.length;n++)a=i[n],t.indexOf(a)>=0||(r[a]=e[a]);return r}(e,t);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(n=0;n<i.length;n++)a=i[n],t.indexOf(a)>=0||Object.prototype.propertyIsEnumerable.call(e,a)&&(r[a]=e[a])}return r}var s=n.createContext({}),u=function(e){var t=n.useContext(s),a=t;return e&&(a="function"==typeof e?e(t):o(o({},t),e)),a},d=function(e){var t=u(e.components);return n.createElement(s.Provider,{value:t},e.children)},p="mdxType",c={inlineCode:"code",wrapper:function(e){var t=e.children;return n.createElement(n.Fragment,{},t)}},m=n.forwardRef((function(e,t){var a=e.components,r=e.mdxType,i=e.originalType,s=e.parentName,d=l(e,["components","mdxType","originalType","parentName"]),p=u(a),m=r,h=p["".concat(s,".").concat(m)]||p[m]||c[m]||i;return a?n.createElement(h,o(o({ref:t},d),{},{components:a})):n.createElement(h,o({ref:t},d))}));function h(e,t){var a=arguments,r=t&&t.mdxType;if("string"==typeof e||r){var i=a.length,o=new Array(i);o[0]=m;var l={};for(var s in t)hasOwnProperty.call(t,s)&&(l[s]=t[s]);l.originalType=e,l[p]="string"==typeof e?e:r,o[1]=l;for(var u=2;u<i;u++)o[u]=a[u];return n.createElement.apply(null,o)}return n.createElement.apply(null,a)}m.displayName="MDXCreateElement"},85162:(e,t,a)=>{a.d(t,{Z:()=>o});var n=a(67294),r=a(86010);const i={tabItem:"tabItem_Ymn6"};function o(e){var t=e.children,a=e.hidden,o=e.className;return n.createElement("div",{role:"tabpanel",className:(0,r.Z)(i.tabItem,o),hidden:a},t)}},74866:(e,t,a)=>{a.d(t,{Z:()=>b});var n=a(87462),r=a(67294),i=a(86010),o=a(12466),l=a(16550),s=a(91980),u=a(67392),d=a(50012);function p(e){return function(e){var t,a;return null!=(t=null==(a=r.Children.map(e,(function(e){if(!e||(0,r.isValidElement)(e)&&(t=e.props)&&"object"==typeof t&&"value"in t)return e;var t;throw new Error("Docusaurus error: Bad <Tabs> child <"+("string"==typeof e.type?e.type:e.type.name)+'>: all children of the <Tabs> component should be <TabItem>, and every <TabItem> should have a unique "value" prop.')})))?void 0:a.filter(Boolean))?t:[]}(e).map((function(e){var t=e.props;return{value:t.value,label:t.label,attributes:t.attributes,default:t.default}}))}function c(e){var t=e.values,a=e.children;return(0,r.useMemo)((function(){var e=null!=t?t:p(a);return function(e){var t=(0,u.l)(e,(function(e,t){return e.value===t.value}));if(t.length>0)throw new Error('Docusaurus error: Duplicate values "'+t.map((function(e){return e.value})).join(", ")+'" found in <Tabs>. Every value needs to be unique.')}(e),e}),[t,a])}function m(e){var t=e.value;return e.tabValues.some((function(e){return e.value===t}))}function h(e){var t=e.queryString,a=void 0!==t&&t,n=e.groupId,i=(0,l.k6)(),o=function(e){var t=e.queryString,a=void 0!==t&&t,n=e.groupId;if("string"==typeof a)return a;if(!1===a)return null;if(!0===a&&!n)throw new Error('Docusaurus error: The <Tabs> component groupId prop is required if queryString=true, because this value is used as the search param name. You can also provide an explicit value such as queryString="my-search-param".');return null!=n?n:null}({queryString:a,groupId:n});return[(0,s._X)(o),(0,r.useCallback)((function(e){if(o){var t=new URLSearchParams(i.location.search);t.set(o,e),i.replace(Object.assign({},i.location,{search:t.toString()}))}}),[o,i])]}function k(e){var t,a,n,i,o=e.defaultValue,l=e.queryString,s=void 0!==l&&l,u=e.groupId,p=c(e),k=(0,r.useState)((function(){return function(e){var t,a=e.defaultValue,n=e.tabValues;if(0===n.length)throw new Error("Docusaurus error: the <Tabs> component requires at least one <TabItem> children component");if(a){if(!m({value:a,tabValues:n}))throw new Error('Docusaurus error: The <Tabs> has a defaultValue "'+a+'" but none of its children has the corresponding value. Available values are: '+n.map((function(e){return e.value})).join(", ")+". If you intend to show no default tab, use defaultValue={null} instead.");return a}var r=null!=(t=n.find((function(e){return e.default})))?t:n[0];if(!r)throw new Error("Unexpected error: 0 tabValues");return r.value}({defaultValue:o,tabValues:p})})),f=k[0],g=k[1],v=h({queryString:s,groupId:u}),y=v[0],N=v[1],b=(t=function(e){return e?"docusaurus.tab."+e:null}({groupId:u}.groupId),a=(0,d.Nk)(t),n=a[0],i=a[1],[n,(0,r.useCallback)((function(e){t&&i.set(e)}),[t,i])]),w=b[0],x=b[1],C=function(){var e=null!=y?y:w;return m({value:e,tabValues:p})?e:null}();return(0,r.useLayoutEffect)((function(){C&&g(C)}),[C]),{selectedValue:f,selectValue:(0,r.useCallback)((function(e){if(!m({value:e,tabValues:p}))throw new Error("Can't select invalid tab value="+e);g(e),N(e),x(e)}),[N,x,p]),tabValues:p}}var f=a(72389);const g={tabList:"tabList__CuJ",tabItem:"tabItem_LNqP"};function v(e){var t=e.className,a=e.block,l=e.selectedValue,s=e.selectValue,u=e.tabValues,d=[],p=(0,o.o5)().blockElementScrollPositionUntilNextRender,c=function(e){var t=e.currentTarget,a=d.indexOf(t),n=u[a].value;n!==l&&(p(t),s(n))},m=function(e){var t,a=null;switch(e.key){case"Enter":c(e);break;case"ArrowRight":var n,r=d.indexOf(e.currentTarget)+1;a=null!=(n=d[r])?n:d[0];break;case"ArrowLeft":var i,o=d.indexOf(e.currentTarget)-1;a=null!=(i=d[o])?i:d[d.length-1]}null==(t=a)||t.focus()};return r.createElement("ul",{role:"tablist","aria-orientation":"horizontal",className:(0,i.Z)("tabs",{"tabs--block":a},t)},u.map((function(e){var t=e.value,a=e.label,o=e.attributes;return r.createElement("li",(0,n.Z)({role:"tab",tabIndex:l===t?0:-1,"aria-selected":l===t,key:t,ref:function(e){return d.push(e)},onKeyDown:m,onClick:c},o,{className:(0,i.Z)("tabs__item",g.tabItem,null==o?void 0:o.className,{"tabs__item--active":l===t})}),null!=a?a:t)})))}function y(e){var t=e.lazy,a=e.children,n=e.selectedValue,i=(Array.isArray(a)?a:[a]).filter(Boolean);if(t){var o=i.find((function(e){return e.props.value===n}));return o?(0,r.cloneElement)(o,{className:"margin-top--md"}):null}return r.createElement("div",{className:"margin-top--md"},i.map((function(e,t){return(0,r.cloneElement)(e,{key:t,hidden:e.props.value!==n})})))}function N(e){var t=k(e);return r.createElement("div",{className:(0,i.Z)("tabs-container",g.tabList)},r.createElement(v,(0,n.Z)({},e,t)),r.createElement(y,(0,n.Z)({},e,t)))}function b(e){var t=(0,f.Z)();return r.createElement(N,(0,n.Z)({key:String(t)},e))}},82393:(e,t,a)=>{a.r(t),a.d(t,{assets:()=>c,contentTitle:()=>d,default:()=>f,frontMatter:()=>u,metadata:()=>p,toc:()=>m});var n=a(87462),r=a(63366),i=(a(67294),a(3905)),o=a(74866),l=a(85162),s=["components"],u={id:"data-loader-instrumentation",title:"Data Loader Instrumentations"},d=void 0,p={unversionedId:"server/data-loader/data-loader-instrumentation",id:"version-6.x.x/server/data-loader/data-loader-instrumentation",title:"Data Loader Instrumentations",description:"graphql-kotlin-dataloader-instrumentation is set of custom Instrumentations",source:"@site/versioned_docs/version-6.x.x/server/data-loader/data-loader-instrumentation.mdx",sourceDirName:"server/data-loader",slug:"/server/data-loader/data-loader-instrumentation",permalink:"/graphql-kotlin/docs/server/data-loader/data-loader-instrumentation",draft:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-6.x.x/server/data-loader/data-loader-instrumentation.mdx",tags:[],version:"6.x.x",lastUpdatedBy:"Dariusz Kuc",lastUpdatedAt:1694122111,formattedLastUpdatedAt:"Sep 7, 2023",frontMatter:{id:"data-loader-instrumentation",title:"Data Loader Instrumentations"},sidebar:"docs",previous:{title:"Data Loaders",permalink:"/graphql-kotlin/docs/server/data-loader/"},next:{title:"Spring Server Overview",permalink:"/graphql-kotlin/docs/server/spring-server/spring-overview"}},c={},m=[{value:"Dispatching by level",id:"dispatching-by-level",level:2},{value:"Example",id:"example",level:3},{value:"Usage",id:"usage",level:3},{value:"Limitations",id:"limitations",level:3},{value:"Dispatching by synchronous execution exhaustion",id:"dispatching-by-synchronous-execution-exhaustion",level:2},{value:"Example",id:"example-1",level:3},{value:"Usage",id:"usage-1",level:3},{value:"Multiple data loaders per field data fetcher",id:"multiple-data-loaders-per-field-data-fetcher",level:2},{value:"DispatchIfNeeded",id:"dispatchifneeded",level:3},{value:"Example",id:"example-2",level:3}],h={toc:m},k="wrapper";function f(e){var t=e.components,u=(0,r.Z)(e,s);return(0,i.kt)(k,(0,n.Z)({},h,u,{components:t,mdxType:"MDXLayout"}),(0,i.kt)("p",null,(0,i.kt)("inlineCode",{parentName:"p"},"graphql-kotlin-dataloader-instrumentation")," is set of custom ",(0,i.kt)("a",{parentName:"p",href:"https://www.graphql-java.com/documentation/instrumentation/"},"Instrumentations"),"\nthat will calculate when is the right moment to dispatch ",(0,i.kt)("inlineCode",{parentName:"p"},"KotlinDataLoader"),"s across single and batch GraphQL operations."),(0,i.kt)("p",null,"These custom instrumentations follow the similar approach as the default ",(0,i.kt)("a",{parentName:"p",href:"https://github.com/graphql-java/graphql-java/blob/master/src/main/java/graphql/execution/instrumentation/dataloader/DataLoaderDispatcherInstrumentation.java"},"DataLoaderDispatcherInstrumentation"),"\nfrom ",(0,i.kt)("inlineCode",{parentName:"p"},"graphql-java"),", the main difference is that regular instrumentations apply to a single ",(0,i.kt)("inlineCode",{parentName:"p"},"ExecutionInput")," aka ",(0,i.kt)("a",{parentName:"p",href:"https://www.graphql-java.com/documentation/execution#queries"},"GraphQL Operation"),",\nwhereas these custom instrumentations apply to multiple GraphQL operations (say a BatchRequest) and stores their state in the ",(0,i.kt)("inlineCode",{parentName:"p"},"GraphQLContext"),"\nallowing batching and deduplication of transactions across those multiple GraphQL operations."),(0,i.kt)("p",null,"By default, each GraphQL operation is processed independently of each other. Multiple operations can be processed\ntogether as if they were single GraphQL request if they are part of the same batch request."),(0,i.kt)("p",null,"The ",(0,i.kt)("inlineCode",{parentName:"p"},"graphql-kotlin-dataloader-instrumentation")," module contains 2 custom ",(0,i.kt)("inlineCode",{parentName:"p"},"DataLoader")," instrumentations."),(0,i.kt)("h2",{id:"dispatching-by-level"},"Dispatching by level"),(0,i.kt)("p",null,"The ",(0,i.kt)("inlineCode",{parentName:"p"},"DataLoaderLevelDispatchedInstrumentation")," tracks the state of all ",(0,i.kt)("inlineCode",{parentName:"p"},"ExecutionInputs")," across operations. When a certain\nfield dispatches, it will check if all fields across all operations for a particular level were dispatched and if the condition is met,\nit will dispatch all the data loaders."),(0,i.kt)("h3",{id:"example"},"Example"),(0,i.kt)("p",null,"You can find additional examples in our ",(0,i.kt)("a",{parentName:"p",href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/master/executions/graphql-kotlin-dataloader-instrumentation/src/test/kotlin/com/expediagroup/graphql/dataloader/instrumentation/level/DataLoaderLevelDispatchedInstrumentationTest.kt"},"unit tests"),"."),(0,i.kt)(o.Z,{defaultValue:"by-level-queries",values:[{label:"Queries",value:"by-level-queries"},{label:"Execution",value:"by-level-execution"}],mdxType:"Tabs"},(0,i.kt)(l.Z,{value:"by-level-queries",mdxType:"TabItem"},(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-graphql"},"query Q1 {\n    astronaut(id: 1) { # async\n        id\n        name\n        missions { # async\n            id\n            designation\n        }\n    }\n}\n\nquery Q2 {\n    astronaut(id: 2) { # async\n        id\n        name\n        missions { # async\n            id\n            designation\n        }\n    }\n}\n"))),(0,i.kt)(l.Z,{value:"by-level-execution",mdxType:"TabItem"},(0,i.kt)("p",null,(0,i.kt)("img",{alt:"Image of data loader level dispatched instrumentation",src:a(67942).Z,width:"1213",height:"571"})),(0,i.kt)("ul",null,(0,i.kt)("li",{parentName:"ul"},"The ",(0,i.kt)("inlineCode",{parentName:"li"},"astronaut")," ",(0,i.kt)("inlineCode",{parentName:"li"},"DataFetcher")," uses a ",(0,i.kt)("inlineCode",{parentName:"li"},"AstronautDataLoader")," which will be dispatched when ",(0,i.kt)("strong",{parentName:"li"},"Level 1")," of those 2 operations\nis dispatched, causing the ",(0,i.kt)("inlineCode",{parentName:"li"},"AstronautDataLoader")," to load 2 astronauts."),(0,i.kt)("li",{parentName:"ul"},"The ",(0,i.kt)("inlineCode",{parentName:"li"},"missions")," ",(0,i.kt)("inlineCode",{parentName:"li"},"DataFetcher")," uses a ",(0,i.kt)("inlineCode",{parentName:"li"},"MissionsByAstronautDataLoader")," which will be dispatched when ",(0,i.kt)("strong",{parentName:"li"},"Level 2")," of those 2 operations\nis dispatched, causing the ",(0,i.kt)("inlineCode",{parentName:"li"},"MissionsByAstronautDataLoader")," to load 2 lists of missions by astronaut.")))),(0,i.kt)("h3",{id:"usage"},"Usage"),(0,i.kt)("p",null,"In order to enable batching by level, you need to configure your GraphQL instance with the ",(0,i.kt)("inlineCode",{parentName:"p"},"DataLoaderLevelDispatchedInstrumentation"),"."),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-kotlin"},"val graphQL = GraphQL.Builder()\n    .doNotAddDefaultInstrumentations()\n    .instrumentation(DataLoaderLevelDispatchedInstrumentation())\n    // configure schema, type wiring, etc.\n    .build()\n")),(0,i.kt)("p",null,"This data loader instrumentation relies on a global state object that should be stored in the GraphQL context map"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-kotlin"},"val graphQLContext = mapOf(\n    SyncExecutionExhaustedState::class to ExecutionLevelDispatchedState(queries.size)\n)\n")),(0,i.kt)("admonition",{type:"info"},(0,i.kt)("p",{parentName:"admonition"},(0,i.kt)("inlineCode",{parentName:"p"},"graphql-kotlin-spring-server")," provides convenient integration of batch loader functionality through simple configuration.\nBatching by level can be enabled by configuring following properties:"),(0,i.kt)("pre",{parentName:"admonition"},(0,i.kt)("code",{parentName:"pre",className:"language-yaml"},"graphql:\n  batching:\n   enabled: true\n   strategy: LEVEL_DISPATCHED\n"))),(0,i.kt)("h3",{id:"limitations"},"Limitations"),(0,i.kt)("p",null,"This instrumentation is a good option if your ",(0,i.kt)("strong",{parentName:"p"},"GraphQLServer")," will receive a batched request with operations of the same type,\nin those cases batching by level is enough, however, this solution is far from being the most optimal as we don't necessarily want to dispatch by level."),(0,i.kt)("h2",{id:"dispatching-by-synchronous-execution-exhaustion"},"Dispatching by synchronous execution exhaustion"),(0,i.kt)("p",null,"The most optimal time to dispatch all data loaders is when all possible synchronous execution paths across all batch\noperations were exhausted. Synchronous execution path is considered exhausted (or completed) when all currently processed\ndata fetchers were either resolved to a scalar or a future promise."),(0,i.kt)("p",null,"Let's analyze how GraphQL execution works, but first lets check some GraphQL concepts:"),(0,i.kt)("p",null,(0,i.kt)("strong",{parentName:"p"},"DataFetcher")),(0,i.kt)("p",null,"Each field in GraphQL has a resolver aka ",(0,i.kt)("inlineCode",{parentName:"p"},"DataFetcher")," associated with it, some fields will use specialized ",(0,i.kt)("inlineCode",{parentName:"p"},"DataFetcher"),"s\nthat knows how to go to a database or make a network request to get field information while most simply take\ndata from the returned memory objects."),(0,i.kt)("p",null,(0,i.kt)("strong",{parentName:"p"},"Execution Strategy")),(0,i.kt)("p",null,"The process of finding values for a list of fields from the GraphQL Query, using a recursive strategy."),(0,i.kt)("h3",{id:"example-1"},"Example"),(0,i.kt)("p",null,"You can find additional examples in our ",(0,i.kt)("a",{parentName:"p",href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/master/executions/graphql-kotlin-dataloader-instrumentation/src/test/kotlin/com/expediagroup/graphql/dataloader/instrumentation/syncexhaustion/DataLoaderSyncExecutionExhaustedInstrumentationTest.kt"},"unit tests"),"."),(0,i.kt)(o.Z,{defaultValue:"by-sync-exhaustion-queries",values:[{label:"Queries",value:"by-sync-exhaustion-queries"},{label:"Execution",value:"by-sync-exhaustion-execution"}],mdxType:"Tabs"},(0,i.kt)(l.Z,{value:"by-sync-exhaustion-queries",mdxType:"TabItem"},(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-graphql"},"query Q1 {\n    astronaut(id: 1) { # async\n        id\n        name\n        missions { # async\n            id\n            designation\n        }\n    }\n}\n\nquery Q2 {\n    nasa { #sync\n        astronaut(id: 2) { # async\n            id\n            name\n            missions { # async\n                id\n                designation\n            }\n        }\n        address { # sync\n            street\n            zipCode\n        }\n        phoneNumber\n    }\n}\n"))),(0,i.kt)(l.Z,{value:"by-sync-exhaustion-execution",mdxType:"TabItem"},(0,i.kt)("p",null,(0,i.kt)("img",{alt:"Image of data loader level dispatched instrumentation",src:a(81221).Z,width:"1375",height:"759"})),(0,i.kt)("p",null,(0,i.kt)("strong",{parentName:"p"},"The order of execution of the queries will be:")),(0,i.kt)("p",null,(0,i.kt)("strong",{parentName:"p"},(0,i.kt)("em",{parentName:"strong"},"for Q1"))),(0,i.kt)("ol",null,(0,i.kt)("li",{parentName:"ol"},"Start an ",(0,i.kt)("inlineCode",{parentName:"li"},"ExecutionStrategy")," for the ",(0,i.kt)("inlineCode",{parentName:"li"},"root")," field of the query, to concurrently resolve ",(0,i.kt)("inlineCode",{parentName:"li"},"astronaut")," field.",(0,i.kt)("ul",{parentName:"li"},(0,i.kt)("li",{parentName:"ul"},(0,i.kt)("inlineCode",{parentName:"li"},"astronaut")," ",(0,i.kt)("strong",{parentName:"li"},"DataFetcher")," will invoke the ",(0,i.kt)("inlineCode",{parentName:"li"},"AstronautDataLoader")," and will return a ",(0,i.kt)("inlineCode",{parentName:"li"},"CompletableFuture<Astronaut>")," so we can consider this path exhausted.")))),(0,i.kt)("p",null,(0,i.kt)("strong",{parentName:"p"},(0,i.kt)("em",{parentName:"strong"},"for Q2"))),(0,i.kt)("ol",null,(0,i.kt)("li",{parentName:"ol"},"Start an ",(0,i.kt)("inlineCode",{parentName:"li"},"ExecutionStrategy")," for the ",(0,i.kt)("inlineCode",{parentName:"li"},"root")," field of the query, to concurrently resolve ",(0,i.kt)("inlineCode",{parentName:"li"},"nasa")," field.",(0,i.kt)("ul",{parentName:"li"},(0,i.kt)("li",{parentName:"ul"},(0,i.kt)("inlineCode",{parentName:"li"},"nasa")," ",(0,i.kt)("strong",{parentName:"li"},"DataFetcher")," will synchronously return a ",(0,i.kt)("inlineCode",{parentName:"li"},"Nasa")," object, so we can descend more that path."))),(0,i.kt)("li",{parentName:"ol"},"Start an ",(0,i.kt)("inlineCode",{parentName:"li"},"ExecutionStrategy")," for the ",(0,i.kt)("inlineCode",{parentName:"li"},"nasa")," field of the ",(0,i.kt)("inlineCode",{parentName:"li"},"root")," field of the query to concurrently resolve ",(0,i.kt)("inlineCode",{parentName:"li"},"astronaut"),", ",(0,i.kt)("inlineCode",{parentName:"li"},"address")," and ",(0,i.kt)("inlineCode",{parentName:"li"},"phoneNumber"),".",(0,i.kt)("ul",{parentName:"li"},(0,i.kt)("li",{parentName:"ul"},(0,i.kt)("inlineCode",{parentName:"li"},"astronaut")," ",(0,i.kt)("strong",{parentName:"li"},"DataFetcher"),"  will invoke the ",(0,i.kt)("inlineCode",{parentName:"li"},"AstronautDataLoader")," and will return a ",(0,i.kt)("inlineCode",{parentName:"li"},"CompletableFuture<Astronaut>")," so we can consider this path exhausted"),(0,i.kt)("li",{parentName:"ul"},(0,i.kt)("inlineCode",{parentName:"li"},"address")," ",(0,i.kt)("strong",{parentName:"li"},"DataFetcher")," will synchronously return an ",(0,i.kt)("inlineCode",{parentName:"li"},"Address")," object, so we can descend more that path."),(0,i.kt)("li",{parentName:"ul"},(0,i.kt)("inlineCode",{parentName:"li"},"phoneNumber")," ",(0,i.kt)("strong",{parentName:"li"},"DataFetcher")," will return a scalar, so we can consider this path exhausted."))),(0,i.kt)("li",{parentName:"ol"},"Start an ",(0,i.kt)("inlineCode",{parentName:"li"},"ExecutionStrategy")," for the ",(0,i.kt)("inlineCode",{parentName:"li"},"address")," field of the ",(0,i.kt)("inlineCode",{parentName:"li"},"nasa")," field to concurrently resolve ",(0,i.kt)("inlineCode",{parentName:"li"},"street")," and ",(0,i.kt)("inlineCode",{parentName:"li"},"zipCode"),".",(0,i.kt)("ul",{parentName:"li"},(0,i.kt)("li",{parentName:"ul"},(0,i.kt)("inlineCode",{parentName:"li"},"street")," ",(0,i.kt)("strong",{parentName:"li"},"DataFetcher")," will return a scalar, so we can consider this path exhausted."),(0,i.kt)("li",{parentName:"ul"},(0,i.kt)("inlineCode",{parentName:"li"},"zipCode")," ",(0,i.kt)("strong",{parentName:"li"},"DataFetcher")," will return a scalar, so we can consider this path exhausted.")))),(0,i.kt)("p",null,(0,i.kt)("strong",{parentName:"p"},"At this point we can consider the synchronous execution exhausted and the ",(0,i.kt)("inlineCode",{parentName:"strong"},"AstronautDataLoader")," has 2 keys to be dispatched,\nif we proceed dispatching all data loaders the execution will continue as following:")),(0,i.kt)("p",null,(0,i.kt)("strong",{parentName:"p"},(0,i.kt)("em",{parentName:"strong"},"for Q1"))),(0,i.kt)("ol",null,(0,i.kt)("li",{parentName:"ol"},"Start and ",(0,i.kt)("inlineCode",{parentName:"li"},"ExecutionStrategy")," for the ",(0,i.kt)("inlineCode",{parentName:"li"},"astronaut")," field of the ",(0,i.kt)("inlineCode",{parentName:"li"},"root")," field of the query to concurrently resolve ",(0,i.kt)("inlineCode",{parentName:"li"},"id"),", ",(0,i.kt)("inlineCode",{parentName:"li"},"name")," and ",(0,i.kt)("inlineCode",{parentName:"li"},"mission")," fields.",(0,i.kt)("ul",{parentName:"li"},(0,i.kt)("li",{parentName:"ul"},(0,i.kt)("inlineCode",{parentName:"li"},"id")," ",(0,i.kt)("strong",{parentName:"li"},"DataFetcher")," will return a scalar, so we can consider this path exhausted."),(0,i.kt)("li",{parentName:"ul"},(0,i.kt)("inlineCode",{parentName:"li"},"name")," ",(0,i.kt)("strong",{parentName:"li"},"DataFetcher")," will return a scalar, so we can consider this path exhausted."),(0,i.kt)("li",{parentName:"ul"},(0,i.kt)("inlineCode",{parentName:"li"},"missions")," ",(0,i.kt)("strong",{parentName:"li"},"DataFetcher")," will invoke the ",(0,i.kt)("inlineCode",{parentName:"li"},"MissionsByAstronautDataLoader")," and will return a ",(0,i.kt)("inlineCode",{parentName:"li"},"CompletableFuture<List<Mission>>")," so we can consider this path exhausted.")))),(0,i.kt)("p",null,(0,i.kt)("strong",{parentName:"p"},(0,i.kt)("em",{parentName:"strong"},"for Q2"))),(0,i.kt)("ol",null,(0,i.kt)("li",{parentName:"ol"},"Start and ",(0,i.kt)("inlineCode",{parentName:"li"},"ExecutionStrategy")," for the ",(0,i.kt)("inlineCode",{parentName:"li"},"astronaut")," field of the ",(0,i.kt)("inlineCode",{parentName:"li"},"nasa")," field of the query to concurrently resolve ",(0,i.kt)("inlineCode",{parentName:"li"},"id"),", ",(0,i.kt)("inlineCode",{parentName:"li"},"name")," and ",(0,i.kt)("inlineCode",{parentName:"li"},"mission")," fields.",(0,i.kt)("ul",{parentName:"li"},(0,i.kt)("li",{parentName:"ul"},(0,i.kt)("inlineCode",{parentName:"li"},"id")," ",(0,i.kt)("strong",{parentName:"li"},"DataFetcher")," will return a scalar, so we can consider this path exhausted."),(0,i.kt)("li",{parentName:"ul"},(0,i.kt)("inlineCode",{parentName:"li"},"name")," ",(0,i.kt)("strong",{parentName:"li"},"DataFetcher")," will return a scalar, so we can consider this path exhausted."),(0,i.kt)("li",{parentName:"ul"},(0,i.kt)("inlineCode",{parentName:"li"},"missions")," ",(0,i.kt)("strong",{parentName:"li"},"DataFetcher")," will invoke the ",(0,i.kt)("inlineCode",{parentName:"li"},"MissionsByAstronautDataLoader")," and will return a ",(0,i.kt)("inlineCode",{parentName:"li"},"CompletableFuture<List<Mission>>")," so we can consider this path exhausted.")))),(0,i.kt)("p",null,(0,i.kt)("strong",{parentName:"p"},"At this point we can consider the synchronous execution exhausted and the ",(0,i.kt)("inlineCode",{parentName:"strong"},"MissionsByAstronautDataLoader")," has 2 keys to be dispatched,\nif we proceed dispatching all data loaders the execution will continue to just resolve scalar fields.")))),(0,i.kt)("h3",{id:"usage-1"},"Usage"),(0,i.kt)("p",null,"In order to enable batching by synchronous execution exhaustion, you need to configure your GraphQL instance with the ",(0,i.kt)("inlineCode",{parentName:"p"},"DataLoaderLevelDispatchedInstrumentation"),"."),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-kotlin"},"val graphQL = GraphQL.Builder()\n    .doNotAddDefaultInstrumentations()\n    .instrumentation(DataLoaderSyncExecutionExhaustedInstrumentation())\n    // configure schema, type wiring, etc.\n    .build()\n")),(0,i.kt)("p",null,"This data loader instrumentation relies on a global state object that should be stored in the GraphQL context map"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-kotlin"},"val graphQLContext = mapOf(\n    SyncExecutionExhaustedState::class to ExecutionLevelDispatchedState(\n        queries.size,\n        kotlinDataLoaderRegistry\n    )\n)\n")),(0,i.kt)("admonition",{type:"info"},(0,i.kt)("p",{parentName:"admonition"},(0,i.kt)("inlineCode",{parentName:"p"},"graphql-kotlin-spring-server")," provides convenient integration of batch loader functionality through simple configuration.\nBatching by synchronous execution exhaustion can be enabled by configuring following properties:"),(0,i.kt)("pre",{parentName:"admonition"},(0,i.kt)("code",{parentName:"pre",className:"language-yaml"},"graphql:\n  batching:\n   enabled: true\n   strategy: SYNC_EXHAUSTION\n"))),(0,i.kt)("h2",{id:"multiple-data-loaders-per-field-data-fetcher"},"Multiple data loaders per field data fetcher"),(0,i.kt)("p",null,"There are some cases when a GraphQL Schema doesn't match the data source schema, a field can require data from multiple\nsources to be fetched and you will still want to do batching with data loaders."),(0,i.kt)("h3",{id:"dispatchifneeded"},"DispatchIfNeeded"),(0,i.kt)("p",null,(0,i.kt)("inlineCode",{parentName:"p"},"graphql-kotlin-dataloader-instrumentation")," includes a helpful extension function of the ",(0,i.kt)("inlineCode",{parentName:"p"},"CompletableFuture")," class\nso that you can easily instruct the ",(0,i.kt)("a",{parentName:"p",href:"./data-loader-instrumentation#dispatching-by-level"},"previously selected data loader instrumentation"),"\nthat you want to apply batching and deduplication to a chained ",(0,i.kt)("inlineCode",{parentName:"p"},"DataLoader")," in your ",(0,i.kt)("inlineCode",{parentName:"p"},"DataFetcher")," (resolver)."),(0,i.kt)("h3",{id:"example-2"},"Example"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-graphql"},"type Query {\n    astronaut(id: ID!): Astronaut\n}\n\n# In the data source, let's say a database,\n# an `Astronaut` can have multiple `Mission`s and a `Mission` can have multiple `Planet`s.\ntype Astronaut {\n    id: ID!\n    name: String!\n    # The schema exposes the `Astronaut` `Planet`s, without traversing his `Mission`s.\n    planets: [Planet!]!\n}\n\ntype Planet {\n    id: ID!\n    name: String!\n}\n")),(0,i.kt)("p",null,"The  ",(0,i.kt)("inlineCode",{parentName:"p"},"Astronaut")," ",(0,i.kt)("inlineCode",{parentName:"p"},"planets")," data fetcher (resolver) will contain the logic to chain two data loaders,\nfirst collect missions by astronaut, and then, planets by mission."),(0,i.kt)("p",null,(0,i.kt)("strong",{parentName:"p"},"DataLoaders")),(0,i.kt)("p",null,"For this specific example we would need 2 ",(0,i.kt)("inlineCode",{parentName:"p"},"DataLoader"),"s"),(0,i.kt)("ol",null,(0,i.kt)("li",{parentName:"ol"},(0,i.kt)("strong",{parentName:"li"},"MissionsByAstronaut:")," to retrieve ",(0,i.kt)("inlineCode",{parentName:"li"},"Mission"),"s by a given ",(0,i.kt)("inlineCode",{parentName:"li"},"Astronaut"),"."),(0,i.kt)("li",{parentName:"ol"},(0,i.kt)("strong",{parentName:"li"},"PlanetsByMission:")," to retrieve ",(0,i.kt)("inlineCode",{parentName:"li"},"Planet"),"s by a given ",(0,i.kt)("inlineCode",{parentName:"li"},"Mission"),".")),(0,i.kt)("p",null,(0,i.kt)("strong",{parentName:"p"},"Fetching logic")),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-kotlin"},'class Astronaut {\n    fun getPlanets(\n        astronautId: Int,\n        environment: DataFetchingEnvironment\n    ): CompletableFuture<List<Planet>> {\n        val missionsByAstronautDataLoader = environment.getDataLoader("MissionsByAstronautDataLoader")\n        val planetsByMissionDataLoader = environment.getDataLoader("PlanetsByMissionDataLoader")\n        return missionsByAstronautDataLoader\n            .load(astronautId)\n            // chain data loader\n            .thenCompose { missions ->\n                planetsByMissionDataLoader\n                    .loadMany(missions.map { mission -> mission.id })\n                    // extension function to schedule a dispatch of registry if needed\n                    .dispatchIfNeeded(environment)\n            }\n}\n')))}f.isMDXComponent=!0},67942:(e,t,a)=>{a.d(t,{Z:()=>n});const n=a.p+"assets/images/data-loader-level-dispatched-instrumentation-5aa0dcea159d7f614e3f9894936ce2a6.png"},81221:(e,t,a)=>{a.d(t,{Z:()=>n});const n=a.p+"assets/images/data-loader-level-sync-execution-exhausted-instrumentation-716af35282dac7cd02bf2d3541752dbf.png"}}]);