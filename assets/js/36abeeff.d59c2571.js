"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[2251],{68873:(e,r,n)=>{n.r(r),n.d(r,{assets:()=>c,contentTitle:()=>s,default:()=>h,frontMatter:()=>l,metadata:()=>u,toc:()=>d});var t=n(74848),a=n(28453),i=n(11470),o=n(19365);const l={id:"ktor-overview",title:"Ktor Server Overview"},s=void 0,u={id:"server/ktor-server/ktor-overview",title:"Ktor Server Overview",description:"graphql-kotlin-ktor-server",source:"@site/docs/server/ktor-server/ktor-overview.mdx",sourceDirName:"server/ktor-server",slug:"/server/ktor-server/ktor-overview",permalink:"/graphql-kotlin/docs/server/ktor-server/ktor-overview",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/docs/server/ktor-server/ktor-overview.mdx",tags:[],version:"current",lastUpdatedBy:"Dariusz Kuc",lastUpdatedAt:1708623265,formattedLastUpdatedAt:"Feb 22, 2024",frontMatter:{id:"ktor-overview",title:"Ktor Server Overview"},sidebar:"docs",previous:{title:"Subscriptions",permalink:"/graphql-kotlin/docs/server/spring-server/spring-subscriptions"},next:{title:"Writing Schemas with Ktor",permalink:"/graphql-kotlin/docs/server/ktor-server/ktor-schema"}},c={},d=[{value:"Setup",id:"setup",level:2},{value:"Configuration",id:"configuration",level:2},{value:"Content Negotiation",id:"content-negotiation",level:2},{value:"Routing",id:"routing",level:2},{value:"GraalVm Native Image Support",id:"graalvm-native-image-support",level:2}];function p(e){const r={a:"a",admonition:"admonition",code:"code",h2:"h2",img:"img",li:"li",p:"p",pre:"pre",ul:"ul",...(0,a.R)(),...e.components};return(0,t.jsxs)(t.Fragment,{children:[(0,t.jsxs)(r.p,{children:[(0,t.jsx)(r.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/servers/graphql-kotlin-ktor-server",children:"graphql-kotlin-ktor-server"}),"\nis a Ktor Server Plugin that simplifies setup of your GraphQL server."]}),"\n",(0,t.jsx)(r.h2,{id:"setup",children:"Setup"}),"\n",(0,t.jsxs)(r.p,{children:["The simplest way to create a new Ktor Server app is by generating one using ",(0,t.jsx)(r.a,{href:"https://start.ktor.io/",children:"https://start.ktor.io/"}),"."]}),"\n",(0,t.jsx)(r.p,{children:(0,t.jsx)(r.img,{alt:"Image of https://start.ktor.io/",src:n(67479).A+"",width:"641",height:"693"})}),"\n",(0,t.jsxs)(r.p,{children:["Once you get the sample application setup locally, you will need to add ",(0,t.jsx)(r.code,{children:"graphql-kotlin-ktor-server"})," dependency:"]}),"\n",(0,t.jsxs)(i.A,{defaultValue:"gradle",values:[{label:"Gradle Kotlin",value:"gradle"},{label:"Maven",value:"maven"}],children:[(0,t.jsx)(o.A,{value:"gradle",children:(0,t.jsx)(r.pre,{children:(0,t.jsx)(r.code,{className:"language-kotlin",children:'implementation("com.expediagroup", "graphql-kotlin-ktor-server", latestVersion)\n'})})}),(0,t.jsx)(o.A,{value:"maven",children:(0,t.jsx)(r.pre,{children:(0,t.jsx)(r.code,{className:"language-xml",children:"<dependency>\n    <groupId>com.expediagroup</groupId>\n    <artifactId>graphql-kotlin-ktor-server</artifactId>\n    <version>${latestVersion}</version>\n</dependency>\n"})})})]}),"\n",(0,t.jsx)(r.h2,{id:"configuration",children:"Configuration"}),"\n",(0,t.jsxs)(r.p,{children:[(0,t.jsx)(r.code,{children:"graphql-kotlin-ktor-server"})," is a Ktor Server Plugin and you to manually install it in your ",(0,t.jsx)(r.a,{href:"https://ktor.io/docs/modules.html",children:"module"}),"."]}),"\n",(0,t.jsx)(r.pre,{children:(0,t.jsx)(r.code,{className:"language-kotlin",children:'class HelloWorldQuery : Query {\n    fun hello(): String = "Hello World!"\n}\n\nfun Application.graphQLModule() {\n    install(GraphQL) {\n        schema {\n            packages = listOf("com.example")\n            queries = listOf(\n                HelloWorldQuery()\n            )\n        }\n    }\n    install(Routing) {\n        graphQLPostRoute()\n    }\n}\n'})}),"\n",(0,t.jsxs)(r.p,{children:["If you use ",(0,t.jsx)(r.code,{children:"EngineMain"})," to start your Ktor server, you can specify your module configuration in your ",(0,t.jsx)(r.code,{children:"application.conf"})," (default)\nor ",(0,t.jsx)(r.code,{children:"application.yaml"})," (requires additional ",(0,t.jsx)(r.code,{children:"ktor-server-config-yaml"})," dependency) file."]}),"\n",(0,t.jsx)(r.pre,{children:(0,t.jsx)(r.code,{children:"ktor {\n    application {\n        modules = [ com.example.ApplicationKt.graphQLModule ]\n    }\n}\n"})}),"\n",(0,t.jsx)(r.h2,{id:"content-negotiation",children:"Content Negotiation"}),"\n",(0,t.jsx)(r.admonition,{type:"caution",children:(0,t.jsxs)(r.p,{children:[(0,t.jsx)(r.code,{children:"graphql-kotlin-ktor-server"})," automatically configures ",(0,t.jsx)(r.code,{children:"ContentNegotiation"})," plugin with ",(0,t.jsx)(r.a,{href:"https://github.com/FasterXML/jackson",children:"Jackson"}),"\nserialization for GraphQL GET/POST routes. ",(0,t.jsx)(r.code,{children:"kotlinx-serialization"})," is currently not supported."]})}),"\n",(0,t.jsx)(r.h2,{id:"routing",children:"Routing"}),"\n",(0,t.jsxs)(r.p,{children:[(0,t.jsx)(r.code,{children:"graphql-kotlin-ktor-server"})," plugin DOES NOT automatically configure any routes. You need to explicitly configure ",(0,t.jsx)(r.code,{children:"Routing"}),"\nplugin with GraphQL routes. This allows you to selectively enable routes and wrap them in some additional logic (e.g. ",(0,t.jsx)(r.code,{children:"Authentication"}),")."]}),"\n",(0,t.jsxs)(r.p,{children:["GraphQL plugin provides following ",(0,t.jsx)(r.code,{children:"Route"})," extension functions"]}),"\n",(0,t.jsxs)(r.ul,{children:["\n",(0,t.jsxs)(r.li,{children:[(0,t.jsx)(r.code,{children:"Route#graphQLGetRoute"})," - GraphQL route for processing GET query requests"]}),"\n",(0,t.jsxs)(r.li,{children:[(0,t.jsx)(r.code,{children:"Route#graphQLPostRoute"})," - GraphQL route for processing POST query requests"]}),"\n",(0,t.jsxs)(r.li,{children:[(0,t.jsx)(r.code,{children:"Route#graphQLSDLRoute"})," - GraphQL route for exposing schema in Schema Definition Language (SDL) format"]}),"\n",(0,t.jsxs)(r.li,{children:[(0,t.jsx)(r.code,{children:"Route#graphiQLRoute"})," - GraphQL route for exposing ",(0,t.jsx)(r.a,{href:"https://github.com/graphql/graphiql",children:"an official IDE"})," from the GraphQL Foundation"]}),"\n"]}),"\n",(0,t.jsx)(r.h2,{id:"graalvm-native-image-support",children:"GraalVm Native Image Support"}),"\n",(0,t.jsxs)(r.p,{children:["GraphQL Kotlin Ktor Server can be compiled to a ",(0,t.jsx)(r.a,{href:"https://www.graalvm.org/latest/reference-manual/native-image/",children:"native image"}),"\nusing GraalVM Ahead-of-Time compilation. See ",(0,t.jsx)(r.a,{href:"/graphql-kotlin/docs/plugins/gradle-plugin-usage-graalvm",children:"Gradle plugin"})," and/or\n",(0,t.jsx)(r.a,{href:"/graphql-kotlin/docs/plugins/maven-plugin-usage-graalvm",children:"Maven plugin"})," documentation for details."]})]})}function h(e={}){const{wrapper:r}={...(0,a.R)(),...e.components};return r?(0,t.jsx)(r,{...e,children:(0,t.jsx)(p,{...e})}):p(e)}},19365:(e,r,n)=>{n.d(r,{A:()=>o});n(96540);var t=n(34164);const a={tabItem:"tabItem_Ymn6"};var i=n(74848);function o(e){var r=e.children,n=e.hidden,o=e.className;return(0,i.jsx)("div",{role:"tabpanel",className:(0,t.A)(a.tabItem,o),hidden:n,children:r})}},11470:(e,r,n)=>{n.d(r,{A:()=>y});var t=n(96540),a=n(34164),i=n(23104),o=n(56347),l=n(205),s=n(57485),u=n(31682),c=n(89466);function d(e){var r,n;return null!=(r=null==(n=t.Children.toArray(e).filter((function(e){return"\n"!==e})).map((function(e){if(!e||(0,t.isValidElement)(e)&&((r=e.props)&&"object"==typeof r&&"value"in r))return e;var r;throw new Error("Docusaurus error: Bad <Tabs> child <"+("string"==typeof e.type?e.type:e.type.name)+'>: all children of the <Tabs> component should be <TabItem>, and every <TabItem> should have a unique "value" prop.')})))?void 0:n.filter(Boolean))?r:[]}function p(e){var r=e.values,n=e.children;return(0,t.useMemo)((function(){var e=null!=r?r:function(e){return d(e).map((function(e){var r=e.props;return{value:r.value,label:r.label,attributes:r.attributes,default:r.default}}))}(n);return function(e){var r=(0,u.X)(e,(function(e,r){return e.value===r.value}));if(r.length>0)throw new Error('Docusaurus error: Duplicate values "'+r.map((function(e){return e.value})).join(", ")+'" found in <Tabs>. Every value needs to be unique.')}(e),e}),[r,n])}function h(e){var r=e.value;return e.tabValues.some((function(e){return e.value===r}))}function v(e){var r=e.queryString,n=void 0!==r&&r,a=e.groupId,i=(0,o.W6)(),l=function(e){var r=e.queryString,n=void 0!==r&&r,t=e.groupId;if("string"==typeof n)return n;if(!1===n)return null;if(!0===n&&!t)throw new Error('Docusaurus error: The <Tabs> component groupId prop is required if queryString=true, because this value is used as the search param name. You can also provide an explicit value such as queryString="my-search-param".');return null!=t?t:null}({queryString:n,groupId:a});return[(0,s.aZ)(l),(0,t.useCallback)((function(e){if(l){var r=new URLSearchParams(i.location.search);r.set(l,e),i.replace(Object.assign({},i.location,{search:r.toString()}))}}),[l,i])]}function g(e){var r,n,a,i,o=e.defaultValue,s=e.queryString,u=void 0!==s&&s,d=e.groupId,g=p(e),f=(0,t.useState)((function(){return function(e){var r,n=e.defaultValue,t=e.tabValues;if(0===t.length)throw new Error("Docusaurus error: the <Tabs> component requires at least one <TabItem> children component");if(n){if(!h({value:n,tabValues:t}))throw new Error('Docusaurus error: The <Tabs> has a defaultValue "'+n+'" but none of its children has the corresponding value. Available values are: '+t.map((function(e){return e.value})).join(", ")+". If you intend to show no default tab, use defaultValue={null} instead.");return n}var a=null!=(r=t.find((function(e){return e.default})))?r:t[0];if(!a)throw new Error("Unexpected error: 0 tabValues");return a.value}({defaultValue:o,tabValues:g})})),m=f[0],x=f[1],b=v({queryString:u,groupId:d}),j=b[0],k=b[1],y=(r=function(e){return e?"docusaurus.tab."+e:null}({groupId:d}.groupId),n=(0,c.Dv)(r),a=n[0],i=n[1],[a,(0,t.useCallback)((function(e){r&&i.set(e)}),[r,i])]),w=y[0],q=y[1],S=function(){var e=null!=j?j:w;return h({value:e,tabValues:g})?e:null}();return(0,l.A)((function(){S&&x(S)}),[S]),{selectedValue:m,selectValue:(0,t.useCallback)((function(e){if(!h({value:e,tabValues:g}))throw new Error("Can't select invalid tab value="+e);x(e),k(e),q(e)}),[k,q,g]),tabValues:g}}var f=n(92303);const m={tabList:"tabList__CuJ",tabItem:"tabItem_LNqP"};var x=n(74848);function b(e){var r=e.className,n=e.block,t=e.selectedValue,o=e.selectValue,l=e.tabValues,s=[],u=(0,i.a_)().blockElementScrollPositionUntilNextRender,c=function(e){var r=e.currentTarget,n=s.indexOf(r),a=l[n].value;a!==t&&(u(r),o(a))},d=function(e){var r,n=null;switch(e.key){case"Enter":c(e);break;case"ArrowRight":var t,a=s.indexOf(e.currentTarget)+1;n=null!=(t=s[a])?t:s[0];break;case"ArrowLeft":var i,o=s.indexOf(e.currentTarget)-1;n=null!=(i=s[o])?i:s[s.length-1]}null==(r=n)||r.focus()};return(0,x.jsx)("ul",{role:"tablist","aria-orientation":"horizontal",className:(0,a.A)("tabs",{"tabs--block":n},r),children:l.map((function(e){var r=e.value,n=e.label,i=e.attributes;return(0,x.jsx)("li",Object.assign({role:"tab",tabIndex:t===r?0:-1,"aria-selected":t===r,ref:function(e){return s.push(e)},onKeyDown:d,onClick:c},i,{className:(0,a.A)("tabs__item",m.tabItem,null==i?void 0:i.className,{"tabs__item--active":t===r}),children:null!=n?n:r}),r)}))})}function j(e){var r=e.lazy,n=e.children,a=e.selectedValue,i=(Array.isArray(n)?n:[n]).filter(Boolean);if(r){var o=i.find((function(e){return e.props.value===a}));return o?(0,t.cloneElement)(o,{className:"margin-top--md"}):null}return(0,x.jsx)("div",{className:"margin-top--md",children:i.map((function(e,r){return(0,t.cloneElement)(e,{key:r,hidden:e.props.value!==a})}))})}function k(e){var r=g(e);return(0,x.jsxs)("div",{className:(0,a.A)("tabs-container",m.tabList),children:[(0,x.jsx)(b,Object.assign({},e,r)),(0,x.jsx)(j,Object.assign({},e,r))]})}function y(e){var r=(0,f.A)();return(0,x.jsx)(k,Object.assign({},e,{children:d(e.children)}),String(r))}},67479:(e,r,n)=>{n.d(r,{A:()=>t});const t=n.p+"assets/images/ktor-initializer-a329a7877e50e02f8e8913aac9f41230.png"},28453:(e,r,n)=>{n.d(r,{R:()=>o,x:()=>l});var t=n(96540);const a={},i=t.createContext(a);function o(e){const r=t.useContext(i);return t.useMemo((function(){return"function"==typeof e?e(r):{...r,...e}}),[r,e])}function l(e){let r;return r=e.disableParentContext?"function"==typeof e.components?e.components(a):e.components||a:o(e.components),t.createElement(i.Provider,{value:r},e.children)}}}]);