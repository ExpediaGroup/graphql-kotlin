"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[2757],{69086:(e,r,n)=>{n.r(r),n.d(r,{assets:()=>u,contentTitle:()=>l,default:()=>p,frontMatter:()=>s,metadata:()=>c,toc:()=>d});var o=n(74848),a=n(28453),t=n(11470),i=n(19365);const s={id:"hooks-provider",title:"Schema Generator Hooks Provider"},l=void 0,c={id:"plugins/hooks-provider",title:"Schema Generator Hooks Provider",description:"GraphQL Kotlin plugins can generate GraphQL schema as your build artifact directly from your source code. Plugins will scan",source:"@site/versioned_docs/version-4.x.x/plugins/hooks-provider.mdx",sourceDirName:"plugins",slug:"/plugins/hooks-provider",permalink:"/graphql-kotlin/docs/4.x.x/plugins/hooks-provider",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-4.x.x/plugins/hooks-provider.mdx",tags:[],version:"4.x.x",lastUpdatedBy:"Samuel Vazquez",lastUpdatedAt:1744404742e3,frontMatter:{id:"hooks-provider",title:"Schema Generator Hooks Provider"},sidebar:"docs",previous:{title:"Usage",permalink:"/graphql-kotlin/docs/4.x.x/plugins/maven-plugin-usage"}},u={},d=[{value:"Creating Custom Hooks Service Provider",id:"creating-custom-hooks-service-provider",level:2},{value:"Add dependency on graphql-kotlin-hooks-provider",id:"add-dependency-on-graphql-kotlin-hooks-provider",level:3},{value:"Create new SchemaGeneratorHooksProvider implementation",id:"create-new-schemageneratorhooksprovider-implementation",level:3},{value:"Create provider configuration file",id:"create-provider-configuration-file",level:3},{value:"Limitations",id:"limitations",level:2}];function h(e){const r={a:"a",code:"code",h2:"h2",h3:"h3",p:"p",pre:"pre",...(0,a.R)(),...e.components};return(0,o.jsxs)(o.Fragment,{children:[(0,o.jsxs)(r.p,{children:["GraphQL Kotlin plugins can generate GraphQL schema as your build artifact directly from your source code. Plugins will scan\nyour classpath for classes implementing ",(0,o.jsx)(r.code,{children:"graphql-kotlin-server"})," marker ",(0,o.jsx)(r.code,{children:"Query"}),", ",(0,o.jsx)(r.code,{children:"Mutation"})," and ",(0,o.jsx)(r.code,{children:"Subscription"})," interfaces\nand then generate corresponding GraphQL schema using ",(0,o.jsx)(r.code,{children:"graphql-kotlin-schema-generator"}),". By default, plugins will generate\nthe schema using ",(0,o.jsx)(r.code,{children:"NoopSchemaGeneratorHooks"}),". If your project uses custom hooks or needs to generate the federated GraphQL\nschema, you will need to provide an instance of ",(0,o.jsx)(r.code,{children:"SchemaGeneratorHooksProvider"})," that will be used to create an instance of\nyour custom hooks."]}),"\n",(0,o.jsxs)(r.p,{children:[(0,o.jsx)(r.code,{children:"SchemaGeneratorHooksProvider"})," is a service provider interface that exposes a single ",(0,o.jsx)(r.code,{children:"hooks"})," method to generate an instance\nof ",(0,o.jsx)(r.code,{children:"SchemaGeneratorHooks"})," that will be used to generate your schema. By utilizing Java ",(0,o.jsx)(r.a,{href:"https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/ServiceLoader.html",children:"ServiceLoader"}),"\nwe can dynamically load your custom provider from the classpath. Service provider can be provided as part of your project\nsources, included inside of one of your project dependencies or through explicitly provided artifact. Since we need to be\nable to deterministically choose a single hooks provider, generation of schema will fail if there are multiple providers\non the classpath."]}),"\n",(0,o.jsx)(r.h2,{id:"creating-custom-hooks-service-provider",children:"Creating Custom Hooks Service Provider"}),"\n",(0,o.jsx)(r.h3,{id:"add-dependency-on-graphql-kotlin-hooks-provider",children:"Add dependency on graphql-kotlin-hooks-provider"}),"\n",(0,o.jsxs)(r.p,{children:[(0,o.jsx)(r.code,{children:"SchemaGeneratorHooksProvider"})," interface is defined in ",(0,o.jsx)(r.code,{children:"graphql-kotlin-hooks-provider"})," module."]}),"\n",(0,o.jsxs)(t.A,{defaultValue:"gradle",values:[{label:"Gradle",value:"gradle"},{label:"Maven",value:"maven"}],children:[(0,o.jsx)(i.A,{value:"gradle",children:(0,o.jsx)(r.pre,{children:(0,o.jsx)(r.code,{className:"language-kotlin",children:'// build.gradle.kts\nimplementation("com.expediagroup", "graphql-kotlin-hooks-provider", latestVersion)\n'})})}),(0,o.jsx)(i.A,{value:"maven",children:(0,o.jsx)(r.pre,{children:(0,o.jsx)(r.code,{className:"language-xml",children:"<dependency>\n    <groupId>com.expediagroup</groupId>\n    <artifactId>graphql-kotlin-hooks-provider</artifactId>\n    <version>${latestVersion}</version>\n</dependency>\n"})})})]}),"\n",(0,o.jsx)(r.h3,{id:"create-new-schemageneratorhooksprovider-implementation",children:"Create new SchemaGeneratorHooksProvider implementation"}),"\n",(0,o.jsxs)(r.p,{children:["Service provider implementation has to implement ",(0,o.jsx)(r.code,{children:"SchemaGeneratorHooksProvider"})," interface that provides a way to instantiate\nschema generator hooks that will be used to generate the GraphQL schema."]}),"\n",(0,o.jsx)(r.pre,{children:(0,o.jsx)(r.code,{className:"language-kotlin",children:"package com.example\n\nclass MyCustomSchemaGeneratorHooksProvider : SchemaGeneratorHooksProvider {\n    override fun hooks(): SchemaGeneratorHooks = MyCustomHooks()\n}\n"})}),"\n",(0,o.jsx)(r.h3,{id:"create-provider-configuration-file",children:"Create provider configuration file"}),"\n",(0,o.jsxs)(r.p,{children:["Service loader provider configuration file should be created under JAR ",(0,o.jsx)(r.code,{children:"/META-INF/services"})," directory (e.g. ",(0,o.jsx)(r.code,{children:"src/main/resources/META-INF/services"}),"\nin default project structure). Name of the provider configuration should be fully qualified service provider interface name, i.e.\n",(0,o.jsx)(r.code,{children:"com.expediagroup.graphql.plugin.schema.hooks.SchemaGeneratorHooksProvider"})," and contain single entry - a fully qualified\nname of the service provider implementation."]}),"\n",(0,o.jsx)(r.p,{children:"Using the example service provider implementation from the above, our project structure should look like"}),"\n",(0,o.jsx)(r.pre,{children:(0,o.jsx)(r.code,{children:"my-project\n|- src\n  |- main\n    |- kotlin\n      |- com\n        |- example\n          |- MyCustomSchemaGeneratorHooksProvider.kt\n    |- resources\n      |- META-INF\n        |- services\n          |- com.expediagroup.graphql.plugin.schema.hooks.SchemaGeneratorHooksProvider\n\n"})}),"\n",(0,o.jsx)(r.p,{children:"Our service provider configuration file should have following content"}),"\n",(0,o.jsx)(r.pre,{children:(0,o.jsx)(r.code,{className:"language-text",children:"com.example.MyCustomSchemaGeneratorHooksProvider\n"})}),"\n",(0,o.jsx)(r.h2,{id:"limitations",children:"Limitations"}),"\n",(0,o.jsxs)(r.p,{children:["We don't support Java 9 module mechanism for declaring ",(0,o.jsx)(r.code,{children:"ServiceLoader"})," implementations. As a workaround, you have to define\nyour service providers in the provider configuration file under ",(0,o.jsx)(r.code,{children:"META-INF/services"}),"."]})]})}function p(e={}){const{wrapper:r}={...(0,a.R)(),...e.components};return r?(0,o.jsx)(r,{...e,children:(0,o.jsx)(h,{...e})}):h(e)}},19365:(e,r,n)=>{n.d(r,{A:()=>i});n(96540);var o=n(34164);const a={tabItem:"tabItem_Ymn6"};var t=n(74848);function i(e){var r=e.children,n=e.hidden,i=e.className;return(0,t.jsx)("div",{role:"tabpanel",className:(0,o.A)(a.tabItem,i),hidden:n,children:r})}},11470:(e,r,n)=>{n.d(r,{A:()=>y});var o=n(96540),a=n(34164),t=n(23104),i=n(56347),s=n(205),l=n(57485),c=n(31682),u=n(70679);function d(e){var r,n;return null!=(r=null==(n=o.Children.toArray(e).filter((function(e){return"\n"!==e})).map((function(e){if(!e||(0,o.isValidElement)(e)&&((r=e.props)&&"object"==typeof r&&"value"in r))return e;var r;throw new Error("Docusaurus error: Bad <Tabs> child <"+("string"==typeof e.type?e.type:e.type.name)+'>: all children of the <Tabs> component should be <TabItem>, and every <TabItem> should have a unique "value" prop.')})))?void 0:n.filter(Boolean))?r:[]}function h(e){var r=e.values,n=e.children;return(0,o.useMemo)((function(){var e=null!=r?r:function(e){return d(e).map((function(e){var r=e.props;return{value:r.value,label:r.label,attributes:r.attributes,default:r.default}}))}(n);return function(e){var r=(0,c.XI)(e,(function(e,r){return e.value===r.value}));if(r.length>0)throw new Error('Docusaurus error: Duplicate values "'+r.map((function(e){return e.value})).join(", ")+'" found in <Tabs>. Every value needs to be unique.')}(e),e}),[r,n])}function p(e){var r=e.value;return e.tabValues.some((function(e){return e.value===r}))}function v(e){var r=e.queryString,n=void 0!==r&&r,a=e.groupId,t=(0,i.W6)(),s=function(e){var r=e.queryString,n=void 0!==r&&r,o=e.groupId;if("string"==typeof n)return n;if(!1===n)return null;if(!0===n&&!o)throw new Error('Docusaurus error: The <Tabs> component groupId prop is required if queryString=true, because this value is used as the search param name. You can also provide an explicit value such as queryString="my-search-param".');return null!=o?o:null}({queryString:n,groupId:a});return[(0,l.aZ)(s),(0,o.useCallback)((function(e){if(s){var r=new URLSearchParams(t.location.search);r.set(s,e),t.replace(Object.assign({},t.location,{search:r.toString()}))}}),[s,t])]}function m(e){var r,n,a,t,i=e.defaultValue,l=e.queryString,c=void 0!==l&&l,d=e.groupId,m=h(e),f=(0,o.useState)((function(){return function(e){var r,n=e.defaultValue,o=e.tabValues;if(0===o.length)throw new Error("Docusaurus error: the <Tabs> component requires at least one <TabItem> children component");if(n){if(!p({value:n,tabValues:o}))throw new Error('Docusaurus error: The <Tabs> has a defaultValue "'+n+'" but none of its children has the corresponding value. Available values are: '+o.map((function(e){return e.value})).join(", ")+". If you intend to show no default tab, use defaultValue={null} instead.");return n}var a=null!=(r=o.find((function(e){return e.default})))?r:o[0];if(!a)throw new Error("Unexpected error: 0 tabValues");return a.value}({defaultValue:i,tabValues:m})})),g=f[0],x=f[1],k=v({queryString:c,groupId:d}),b=k[0],j=k[1],y=(r=function(e){return e?"docusaurus.tab."+e:null}({groupId:d}.groupId),n=(0,u.Dv)(r),a=n[0],t=n[1],[a,(0,o.useCallback)((function(e){r&&t.set(e)}),[r,t])]),w=y[0],S=y[1],q=function(){var e=null!=b?b:w;return p({value:e,tabValues:m})?e:null}();return(0,s.A)((function(){q&&x(q)}),[q]),{selectedValue:g,selectValue:(0,o.useCallback)((function(e){if(!p({value:e,tabValues:m}))throw new Error("Can't select invalid tab value="+e);x(e),j(e),S(e)}),[j,S,m]),tabValues:m}}var f=n(92303);const g={tabList:"tabList__CuJ",tabItem:"tabItem_LNqP"};var x=n(74848);function k(e){var r=e.className,n=e.block,o=e.selectedValue,i=e.selectValue,s=e.tabValues,l=[],c=(0,t.a_)().blockElementScrollPositionUntilNextRender,u=function(e){var r=e.currentTarget,n=l.indexOf(r),a=s[n].value;a!==o&&(c(r),i(a))},d=function(e){var r,n=null;switch(e.key){case"Enter":u(e);break;case"ArrowRight":var o,a=l.indexOf(e.currentTarget)+1;n=null!=(o=l[a])?o:l[0];break;case"ArrowLeft":var t,i=l.indexOf(e.currentTarget)-1;n=null!=(t=l[i])?t:l[l.length-1]}null==(r=n)||r.focus()};return(0,x.jsx)("ul",{role:"tablist","aria-orientation":"horizontal",className:(0,a.A)("tabs",{"tabs--block":n},r),children:s.map((function(e){var r=e.value,n=e.label,t=e.attributes;return(0,x.jsx)("li",Object.assign({role:"tab",tabIndex:o===r?0:-1,"aria-selected":o===r,ref:function(e){return l.push(e)},onKeyDown:d,onClick:u},t,{className:(0,a.A)("tabs__item",g.tabItem,null==t?void 0:t.className,{"tabs__item--active":o===r}),children:null!=n?n:r}),r)}))})}function b(e){var r=e.lazy,n=e.children,t=e.selectedValue,i=(Array.isArray(n)?n:[n]).filter(Boolean);if(r){var s=i.find((function(e){return e.props.value===t}));return s?(0,o.cloneElement)(s,{className:(0,a.A)("margin-top--md",s.props.className)}):null}return(0,x.jsx)("div",{className:"margin-top--md",children:i.map((function(e,r){return(0,o.cloneElement)(e,{key:r,hidden:e.props.value!==t})}))})}function j(e){var r=m(e);return(0,x.jsxs)("div",{className:(0,a.A)("tabs-container",g.tabList),children:[(0,x.jsx)(k,Object.assign({},r,e)),(0,x.jsx)(b,Object.assign({},r,e))]})}function y(e){var r=(0,f.A)();return(0,x.jsx)(j,Object.assign({},e,{children:d(e.children)}),String(r))}},28453:(e,r,n)=>{n.d(r,{R:()=>i,x:()=>s});var o=n(96540);const a={},t=o.createContext(a);function i(e){const r=o.useContext(t);return o.useMemo((function(){return"function"==typeof e?e(r):{...r,...e}}),[r,e])}function s(e){let r;return r=e.disableParentContext?"function"==typeof e.components?e.components(a):e.components||a:i(e.components),o.createElement(t.Provider,{value:r},e.children)}}}]);