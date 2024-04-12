"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[3537],{44658:(e,n,a)=>{a.r(n),a.d(n,{assets:()=>c,contentTitle:()=>s,default:()=>h,frontMatter:()=>i,metadata:()=>u,toc:()=>d});var r=a(74848),t=a(28453),o=a(11470),l=a(19365);const i={id:"gradle-plugin-usage-sdl",title:"Gradle Plugin SDL Usage",sidebar_label:"Generating SDL"},s=void 0,u={id:"plugins/gradle-plugin-usage-sdl",title:"Gradle Plugin SDL Usage",description:"GraphQL Kotlin follows a code-first approach where schema is auto generated from your source code at runtime. GraphQL Kotlin",source:"@site/docs/plugins/gradle-plugin-usage-sdl.mdx",sourceDirName:"plugins",slug:"/plugins/gradle-plugin-usage-sdl",permalink:"/graphql-kotlin/docs/8.x.x/plugins/gradle-plugin-usage-sdl",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/docs/plugins/gradle-plugin-usage-sdl.mdx",tags:[],version:"current",lastUpdatedBy:"Curtis Cook",lastUpdatedAt:1712948770,formattedLastUpdatedAt:"Apr 12, 2024",frontMatter:{id:"gradle-plugin-usage-sdl",title:"Gradle Plugin SDL Usage",sidebar_label:"Generating SDL"},sidebar:"docs",previous:{title:"Generating Client",permalink:"/graphql-kotlin/docs/8.x.x/plugins/gradle-plugin-usage-client"},next:{title:"GraalVM Native Image",permalink:"/graphql-kotlin/docs/8.x.x/plugins/gradle-plugin-usage-graalvm"}},c={},d=[{value:"Generating SDL",id:"generating-sdl",level:2},{value:"Using Custom Hooks Provider",id:"using-custom-hooks-provider",level:2}];function p(e){const n={a:"a",admonition:"admonition",code:"code",h2:"h2",p:"p",pre:"pre",...(0,t.R)(),...e.components};return(0,r.jsxs)(r.Fragment,{children:[(0,r.jsx)(n.p,{children:"GraphQL Kotlin follows a code-first approach where schema is auto generated from your source code at runtime. GraphQL Kotlin\nplugins can be used to generate schema as a build time artifact. This allows you to seamlessly integrate with various\nGraphQL tools that may require a schema artifact as an input (e.g. to perform backwards compatibility checks, etc)."}),"\n",(0,r.jsx)(n.h2,{id:"generating-sdl",children:"Generating SDL"}),"\n",(0,r.jsxs)(n.p,{children:["GraphQL schema can be generated directly from your source code using reflections. ",(0,r.jsx)(n.code,{children:"graphqlGenerateSDL"})," will scan your\nclasspath looking for classes implementing ",(0,r.jsx)(n.code,{children:"Query"}),", ",(0,r.jsx)(n.code,{children:"Mutation"})," and ",(0,r.jsx)(n.code,{children:"Subscription"})," marker interfaces and then generates the\ncorresponding GraphQL schema using ",(0,r.jsx)(n.code,{children:"graphql-kotlin-schema-generator"})," and default ",(0,r.jsx)(n.code,{children:"NoopSchemaGeneratorHooks"}),". In order to\nlimit the amount of packages to scan, this task requires users to provide a list of ",(0,r.jsx)(n.code,{children:"packages"})," that can contain GraphQL\ntypes."]}),"\n",(0,r.jsxs)(o.A,{defaultValue:"kotlin",values:[{label:"Kotlin",value:"kotlin"},{label:"Groovy",value:"groovy"}],children:[(0,r.jsxs)(l.A,{value:"kotlin",children:[(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-kotlin",children:'// build.gradle.kts\nimport com.expediagroup.graphql.plugin.gradle.graphql\n\ngraphql {\n  schema {\n    packages = listOf("com.example")\n  }\n}\n'})}),(0,r.jsx)(n.p,{children:"Above configuration is equivalent to the following task definition"}),(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-kotlin",children:'// build.gradle.kts\nimport com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateSDLTask\n\nval graphqlGenerateSDL by tasks.getting(GraphQLGenerateSDLTask::class) {\n    packages.set(listOf("com.example"))\n}\n'})})]}),(0,r.jsxs)(l.A,{value:"groovy",children:[(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-groovy",children:'// build.gradle\ngraphql {\n  schema {\n    packages = ["com.example"]\n  }\n}\n'})}),(0,r.jsx)(n.p,{children:"Above configuration is equivalent to the following task definition"}),(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-groovy",children:'//build.gradle\ngraphqlGenerateSDL {\n    packages = ["com.example"]\n}\n'})})]})]}),"\n",(0,r.jsx)(n.admonition,{type:"info",children:(0,r.jsx)(n.p,{children:"This task does not automatically configure itself to be part of your build lifecycle. You will need to explicitly\ninvoke it OR configure it as a dependency of some other task."})}),"\n",(0,r.jsx)(n.h2,{id:"using-custom-hooks-provider",children:"Using Custom Hooks Provider"}),"\n",(0,r.jsxs)(n.p,{children:["Plugin will default to use ",(0,r.jsx)(n.code,{children:"NoopSchemaGeneratorHooks"})," to generate target GraphQL schema. If your project uses custom hooks\nor needs to generate the federated GraphQL schema, you will need to provide an instance of ",(0,r.jsx)(n.code,{children:"SchemaGeneratorHooksProvider"}),"\nservice provider that will be used to create an instance of your custom hooks."]}),"\n",(0,r.jsxs)(n.p,{children:[(0,r.jsx)(n.code,{children:"graphqlGenerateSDL"})," utilizes ",(0,r.jsx)(n.a,{href:"https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/ServiceLoader.html",children:"ServiceLoader"}),"\nmechanism to dynamically load available ",(0,r.jsx)(n.code,{children:"SchemaGeneratorHooksProvider"})," service providers from the classpath. Service provider\ncan be provided as part of your project, included in one of your project dependencies or through explicitly provided artifact.\nSee ",(0,r.jsx)(n.a,{href:"/graphql-kotlin/docs/8.x.x/plugins/hooks-provider",children:"Schema Generator Hooks Provider"})," for additional details on how to create custom hooks service provider.\nConfiguration below shows how to configure GraphQL Kotlin plugin with explicitly provided artifact to generate federated\nGraphQL schema."]}),"\n",(0,r.jsxs)(o.A,{defaultValue:"kotlin",values:[{label:"Kotlin",value:"kotlin"},{label:"Groovy",value:"groovy"}],children:[(0,r.jsxs)(l.A,{value:"kotlin",children:[(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-kotlin",children:'// build.gradle.kts\nimport com.expediagroup.graphql.plugin.gradle.graphql\n\ngraphql {\n  schema {\n    packages = listOf("com.example")\n  }\n}\n\ndependencies {\n    graphqlSDL("com.expediagroup:graphql-kotlin-federated-hooks-provider:$graphQLKotlinVersion")\n}\n'})}),(0,r.jsx)(n.p,{children:"Above configuration is equivalent to the following task definition"}),(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-kotlin",children:'// build.gradle.kts\nimport com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateSDLTask\n\nval graphqlGenerateSDL by tasks.getting(GraphQLGenerateSDLTask::class) {\n    packages.set(listOf("com.example"))\n}\n\ndependencies {\n    graphqlSDL("com.expediagroup:graphql-kotlin-federated-hooks-provider:$graphQLKotlinVersion")\n}\n'})})]}),(0,r.jsxs)(l.A,{value:"groovy",children:[(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-groovy",children:'// build.gradle\ngraphql {\n  schema {\n    packages = ["com.example"]\n  }\n}\n\ndependencies {\n    graphqlSDL "com.expediagroup:graphql-kotlin-federated-hooks-provider:$DEFAULT_PLUGIN_VERSION"\n}\n'})}),(0,r.jsx)(n.p,{children:"Above configuration is equivalent to the following task definition"}),(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-groovy",children:'//build.gradle\ngraphqlGenerateSDL {\n    packages = ["com.example"]\n}\n\ndependencies {\n    graphqlSDL "com.expediagroup:graphql-kotlin-federated-hooks-provider:$DEFAULT_PLUGIN_VERSION"\n}\n'})})]})]}),"\n",(0,r.jsx)(n.admonition,{type:"info",children:(0,r.jsx)(n.p,{children:"This task does not automatically configure itself to be part of your build lifecycle. You will need to explicitly\ninvoke it OR configure it as a dependency of some other task."})})]})}function h(e={}){const{wrapper:n}={...(0,t.R)(),...e.components};return n?(0,r.jsx)(n,{...e,children:(0,r.jsx)(p,{...e})}):p(e)}},19365:(e,n,a)=>{a.d(n,{A:()=>l});a(96540);var r=a(34164);const t={tabItem:"tabItem_Ymn6"};var o=a(74848);function l(e){var n=e.children,a=e.hidden,l=e.className;return(0,o.jsx)("div",{role:"tabpanel",className:(0,r.A)(t.tabItem,l),hidden:a,children:n})}},11470:(e,n,a)=>{a.d(n,{A:()=>y});var r=a(96540),t=a(34164),o=a(23104),l=a(56347),i=a(205),s=a(57485),u=a(31682),c=a(89466);function d(e){var n,a;return null!=(n=null==(a=r.Children.toArray(e).filter((function(e){return"\n"!==e})).map((function(e){if(!e||(0,r.isValidElement)(e)&&((n=e.props)&&"object"==typeof n&&"value"in n))return e;var n;throw new Error("Docusaurus error: Bad <Tabs> child <"+("string"==typeof e.type?e.type:e.type.name)+'>: all children of the <Tabs> component should be <TabItem>, and every <TabItem> should have a unique "value" prop.')})))?void 0:a.filter(Boolean))?n:[]}function p(e){var n=e.values,a=e.children;return(0,r.useMemo)((function(){var e=null!=n?n:function(e){return d(e).map((function(e){var n=e.props;return{value:n.value,label:n.label,attributes:n.attributes,default:n.default}}))}(a);return function(e){var n=(0,u.X)(e,(function(e,n){return e.value===n.value}));if(n.length>0)throw new Error('Docusaurus error: Duplicate values "'+n.map((function(e){return e.value})).join(", ")+'" found in <Tabs>. Every value needs to be unique.')}(e),e}),[n,a])}function h(e){var n=e.value;return e.tabValues.some((function(e){return e.value===n}))}function g(e){var n=e.queryString,a=void 0!==n&&n,t=e.groupId,o=(0,l.W6)(),i=function(e){var n=e.queryString,a=void 0!==n&&n,r=e.groupId;if("string"==typeof a)return a;if(!1===a)return null;if(!0===a&&!r)throw new Error('Docusaurus error: The <Tabs> component groupId prop is required if queryString=true, because this value is used as the search param name. You can also provide an explicit value such as queryString="my-search-param".');return null!=r?r:null}({queryString:a,groupId:t});return[(0,s.aZ)(i),(0,r.useCallback)((function(e){if(i){var n=new URLSearchParams(o.location.search);n.set(i,e),o.replace(Object.assign({},o.location,{search:n.toString()}))}}),[i,o])]}function f(e){var n,a,t,o,l=e.defaultValue,s=e.queryString,u=void 0!==s&&s,d=e.groupId,f=p(e),m=(0,r.useState)((function(){return function(e){var n,a=e.defaultValue,r=e.tabValues;if(0===r.length)throw new Error("Docusaurus error: the <Tabs> component requires at least one <TabItem> children component");if(a){if(!h({value:a,tabValues:r}))throw new Error('Docusaurus error: The <Tabs> has a defaultValue "'+a+'" but none of its children has the corresponding value. Available values are: '+r.map((function(e){return e.value})).join(", ")+". If you intend to show no default tab, use defaultValue={null} instead.");return a}var t=null!=(n=r.find((function(e){return e.default})))?n:r[0];if(!t)throw new Error("Unexpected error: 0 tabValues");return t.value}({defaultValue:l,tabValues:f})})),v=m[0],b=m[1],x=g({queryString:u,groupId:d}),k=x[0],j=x[1],y=(n=function(e){return e?"docusaurus.tab."+e:null}({groupId:d}.groupId),a=(0,c.Dv)(n),t=a[0],o=a[1],[t,(0,r.useCallback)((function(e){n&&o.set(e)}),[n,o])]),L=y[0],q=y[1],w=function(){var e=null!=k?k:L;return h({value:e,tabValues:f})?e:null}();return(0,i.A)((function(){w&&b(w)}),[w]),{selectedValue:v,selectValue:(0,r.useCallback)((function(e){if(!h({value:e,tabValues:f}))throw new Error("Can't select invalid tab value="+e);b(e),j(e),q(e)}),[j,q,f]),tabValues:f}}var m=a(92303);const v={tabList:"tabList__CuJ",tabItem:"tabItem_LNqP"};var b=a(74848);function x(e){var n=e.className,a=e.block,r=e.selectedValue,l=e.selectValue,i=e.tabValues,s=[],u=(0,o.a_)().blockElementScrollPositionUntilNextRender,c=function(e){var n=e.currentTarget,a=s.indexOf(n),t=i[a].value;t!==r&&(u(n),l(t))},d=function(e){var n,a=null;switch(e.key){case"Enter":c(e);break;case"ArrowRight":var r,t=s.indexOf(e.currentTarget)+1;a=null!=(r=s[t])?r:s[0];break;case"ArrowLeft":var o,l=s.indexOf(e.currentTarget)-1;a=null!=(o=s[l])?o:s[s.length-1]}null==(n=a)||n.focus()};return(0,b.jsx)("ul",{role:"tablist","aria-orientation":"horizontal",className:(0,t.A)("tabs",{"tabs--block":a},n),children:i.map((function(e){var n=e.value,a=e.label,o=e.attributes;return(0,b.jsx)("li",Object.assign({role:"tab",tabIndex:r===n?0:-1,"aria-selected":r===n,ref:function(e){return s.push(e)},onKeyDown:d,onClick:c},o,{className:(0,t.A)("tabs__item",v.tabItem,null==o?void 0:o.className,{"tabs__item--active":r===n}),children:null!=a?a:n}),n)}))})}function k(e){var n=e.lazy,a=e.children,t=e.selectedValue,o=(Array.isArray(a)?a:[a]).filter(Boolean);if(n){var l=o.find((function(e){return e.props.value===t}));return l?(0,r.cloneElement)(l,{className:"margin-top--md"}):null}return(0,b.jsx)("div",{className:"margin-top--md",children:o.map((function(e,n){return(0,r.cloneElement)(e,{key:n,hidden:e.props.value!==t})}))})}function j(e){var n=f(e);return(0,b.jsxs)("div",{className:(0,t.A)("tabs-container",v.tabList),children:[(0,b.jsx)(x,Object.assign({},e,n)),(0,b.jsx)(k,Object.assign({},e,n))]})}function y(e){var n=(0,m.A)();return(0,b.jsx)(j,Object.assign({},e,{children:d(e.children)}),String(n))}},28453:(e,n,a)=>{a.d(n,{R:()=>l,x:()=>i});var r=a(96540);const t={},o=r.createContext(t);function l(e){const n=r.useContext(o);return r.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function i(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(t):e.components||t:l(e.components),r.createElement(o.Provider,{value:n},e.children)}}}]);