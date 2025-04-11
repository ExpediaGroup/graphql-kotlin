"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[2251],{6154:(e,r,n)=>{n.r(r),n.d(r,{assets:()=>c,contentTitle:()=>s,default:()=>h,frontMatter:()=>l,metadata:()=>u,toc:()=>d});var t=n(74848),a=n(28453),o=n(11470),i=n(19365);const l={id:"ktor-overview",title:"Ktor Server Overview"},s=void 0,u={id:"server/ktor-server/ktor-overview",title:"Ktor Server Overview",description:"graphql-kotlin-ktor-server",source:"@site/docs/server/ktor-server/ktor-overview.mdx",sourceDirName:"server/ktor-server",slug:"/server/ktor-server/ktor-overview",permalink:"/graphql-kotlin/docs/9.x.x/server/ktor-server/ktor-overview",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/docs/server/ktor-server/ktor-overview.mdx",tags:[],version:"current",lastUpdatedBy:"Samuel Vazquez",lastUpdatedAt:174440151e4,frontMatter:{id:"ktor-overview",title:"Ktor Server Overview"},sidebar:"docs",previous:{title:"Subscriptions",permalink:"/graphql-kotlin/docs/9.x.x/server/spring-server/spring-subscriptions"},next:{title:"Writing Schemas with Ktor",permalink:"/graphql-kotlin/docs/9.x.x/server/ktor-server/ktor-schema"}},c={},d=[{value:"Setup",id:"setup",level:2},{value:"Configuration",id:"configuration",level:2},{value:"Content Negotiation",id:"content-negotiation",level:2},{value:"Routing",id:"routing",level:2},{value:"StatusPages",id:"statuspages",level:2},{value:"GraalVm Native Image Support",id:"graalvm-native-image-support",level:2}];function p(e){const r={a:"a",admonition:"admonition",code:"code",h2:"h2",img:"img",li:"li",p:"p",pre:"pre",ul:"ul",...(0,a.R)(),...e.components};return(0,t.jsxs)(t.Fragment,{children:[(0,t.jsxs)(r.p,{children:[(0,t.jsx)(r.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/servers/graphql-kotlin-ktor-server",children:"graphql-kotlin-ktor-server"}),"\nis a Ktor Server Plugin that simplifies setup of your GraphQL server."]}),"\n",(0,t.jsx)(r.h2,{id:"setup",children:"Setup"}),"\n",(0,t.jsxs)(r.p,{children:["The simplest way to create a new Ktor Server app is by generating one using ",(0,t.jsx)(r.a,{href:"https://start.ktor.io/",children:"https://start.ktor.io/"}),"."]}),"\n",(0,t.jsx)(r.p,{children:(0,t.jsx)(r.img,{alt:"Image of https://start.ktor.io/",src:n(67479).A+"",width:"641",height:"693"})}),"\n",(0,t.jsxs)(r.p,{children:["Once you get the sample application setup locally, you will need to add ",(0,t.jsx)(r.code,{children:"graphql-kotlin-ktor-server"})," dependency:"]}),"\n",(0,t.jsxs)(o.A,{defaultValue:"gradle",values:[{label:"Gradle Kotlin",value:"gradle"},{label:"Maven",value:"maven"}],children:[(0,t.jsx)(i.A,{value:"gradle",children:(0,t.jsx)(r.pre,{children:(0,t.jsx)(r.code,{className:"language-kotlin",children:'implementation("com.expediagroup", "graphql-kotlin-ktor-server", latestVersion)\n'})})}),(0,t.jsx)(i.A,{value:"maven",children:(0,t.jsx)(r.pre,{children:(0,t.jsx)(r.code,{className:"language-xml",children:"<dependency>\n    <groupId>com.expediagroup</groupId>\n    <artifactId>graphql-kotlin-ktor-server</artifactId>\n    <version>${latestVersion}</version>\n</dependency>\n"})})})]}),"\n",(0,t.jsx)(r.h2,{id:"configuration",children:"Configuration"}),"\n",(0,t.jsxs)(r.p,{children:[(0,t.jsx)(r.code,{children:"graphql-kotlin-ktor-server"})," is a Ktor Server Plugin, and you need to manually install it in your ",(0,t.jsx)(r.a,{href:"https://ktor.io/docs/modules.html",children:"module"}),"."]}),"\n",(0,t.jsx)(r.pre,{children:(0,t.jsx)(r.code,{className:"language-kotlin",children:'class HelloWorldQuery : Query {\n    fun hello(): String = "Hello World!"\n}\n\nfun Application.graphQLModule() {\n    install(GraphQL) {\n        schema {\n            packages = listOf("com.example")\n            queries = listOf(\n                HelloWorldQuery()\n            )\n        }\n    }\n    install(Routing) {\n        graphQLPostRoute()\n    }\n    install(StatusPages) {\n        defaultGraphQLStatusPages()\n    }\n}\n'})}),"\n",(0,t.jsxs)(r.p,{children:["If you use ",(0,t.jsx)(r.code,{children:"EngineMain"})," to start your Ktor server, you can specify your module configuration in your ",(0,t.jsx)(r.code,{children:"application.conf"})," (default)\nor ",(0,t.jsx)(r.code,{children:"application.yaml"})," (requires additional ",(0,t.jsx)(r.code,{children:"ktor-server-config-yaml"})," dependency) file."]}),"\n",(0,t.jsx)(r.pre,{children:(0,t.jsx)(r.code,{children:"ktor {\n    application {\n        modules = [ com.example.ApplicationKt.graphQLModule ]\n    }\n}\n"})}),"\n",(0,t.jsx)(r.h2,{id:"content-negotiation",children:"Content Negotiation"}),"\n",(0,t.jsx)(r.admonition,{type:"caution",children:(0,t.jsxs)(r.p,{children:[(0,t.jsx)(r.code,{children:"graphql-kotlin-ktor-server"})," automatically configures ",(0,t.jsx)(r.code,{children:"ContentNegotiation"})," plugin with ",(0,t.jsx)(r.a,{href:"https://github.com/FasterXML/jackson",children:"Jackson"}),"\nserialization for GraphQL GET/POST routes. ",(0,t.jsx)(r.code,{children:"kotlinx-serialization"})," is currently not supported."]})}),"\n",(0,t.jsx)(r.h2,{id:"routing",children:"Routing"}),"\n",(0,t.jsxs)(r.p,{children:[(0,t.jsx)(r.code,{children:"graphql-kotlin-ktor-server"})," plugin DOES NOT automatically configure any routes. You need to explicitly configure ",(0,t.jsx)(r.code,{children:"Routing"}),"\nplugin with GraphQL routes. This allows you to selectively enable routes and wrap them in some additional logic (e.g. ",(0,t.jsx)(r.code,{children:"Authentication"}),")."]}),"\n",(0,t.jsxs)(r.p,{children:["GraphQL plugin provides following ",(0,t.jsx)(r.code,{children:"Route"})," extension functions"]}),"\n",(0,t.jsxs)(r.ul,{children:["\n",(0,t.jsxs)(r.li,{children:[(0,t.jsx)(r.code,{children:"Route#graphQLGetRoute"})," - GraphQL route for processing GET query requests"]}),"\n",(0,t.jsxs)(r.li,{children:[(0,t.jsx)(r.code,{children:"Route#graphQLPostRoute"})," - GraphQL route for processing POST query requests"]}),"\n",(0,t.jsxs)(r.li,{children:[(0,t.jsx)(r.code,{children:"Route#graphQLSDLRoute"})," - GraphQL route for exposing schema in Schema Definition Language (SDL) format"]}),"\n",(0,t.jsxs)(r.li,{children:[(0,t.jsx)(r.code,{children:"Route#graphiQLRoute"})," - GraphQL route for exposing ",(0,t.jsx)(r.a,{href:"https://github.com/graphql/graphiql",children:"an official IDE"})," from the GraphQL Foundation"]}),"\n"]}),"\n",(0,t.jsx)(r.h2,{id:"statuspages",children:"StatusPages"}),"\n",(0,t.jsxs)(r.p,{children:[(0,t.jsx)(r.code,{children:"graphql-kotlin-ktor-server"})," plugin differs from Spring as it relies on Ktor's StatusPages plugin to perform error handling.\nIt is recommended to use the default settings, however, if you would like to customize your error handling you can create\nyour own handler. One example might be if you need to catch a custom Authorization error to return a 401 status code.\nPlease see ",(0,t.jsx)(r.a,{href:"https://ktor.io/docs/server-status-pages.html",children:"Ktor's Official Documentation for StatusPages"})]}),"\n",(0,t.jsx)(r.h2,{id:"graalvm-native-image-support",children:"GraalVm Native Image Support"}),"\n",(0,t.jsxs)(r.p,{children:["GraphQL Kotlin Ktor Server can be compiled to a ",(0,t.jsx)(r.a,{href:"https://www.graalvm.org/latest/reference-manual/native-image/",children:"native image"}),"\nusing GraalVM Ahead-of-Time compilation. See ",(0,t.jsx)(r.a,{href:"/graphql-kotlin/docs/9.x.x/plugins/gradle-plugin-usage-graalvm",children:"Gradle plugin"})," and/or\n",(0,t.jsx)(r.a,{href:"/graphql-kotlin/docs/9.x.x/plugins/maven-plugin-usage-graalvm",children:"Maven plugin"})," documentation for details."]})]})}function h(e={}){const{wrapper:r}={...(0,a.R)(),...e.components};return r?(0,t.jsx)(r,{...e,children:(0,t.jsx)(p,{...e})}):p(e)}},19365:(e,r,n)=>{n.d(r,{A:()=>i});n(96540);var t=n(34164);const a={tabItem:"tabItem_Ymn6"};var o=n(74848);function i(e){var r=e.children,n=e.hidden,i=e.className;return(0,o.jsx)("div",{role:"tabpanel",className:(0,t.A)(a.tabItem,i),hidden:n,children:r})}},11470:(e,r,n)=>{n.d(r,{A:()=>y});var t=n(96540),a=n(34164),o=n(23104),i=n(56347),l=n(205),s=n(57485),u=n(31682),c=n(70679);function d(e){var r,n;return null!=(r=null==(n=t.Children.toArray(e).filter((function(e){return"\n"!==e})).map((function(e){if(!e||(0,t.isValidElement)(e)&&((r=e.props)&&"object"==typeof r&&"value"in r))return e;var r;throw new Error("Docusaurus error: Bad <Tabs> child <"+("string"==typeof e.type?e.type:e.type.name)+'>: all children of the <Tabs> component should be <TabItem>, and every <TabItem> should have a unique "value" prop.')})))?void 0:n.filter(Boolean))?r:[]}function p(e){var r=e.values,n=e.children;return(0,t.useMemo)((function(){var e=null!=r?r:function(e){return d(e).map((function(e){var r=e.props;return{value:r.value,label:r.label,attributes:r.attributes,default:r.default}}))}(n);return function(e){var r=(0,u.XI)(e,(function(e,r){return e.value===r.value}));if(r.length>0)throw new Error('Docusaurus error: Duplicate values "'+r.map((function(e){return e.value})).join(", ")+'" found in <Tabs>. Every value needs to be unique.')}(e),e}),[r,n])}function h(e){var r=e.value;return e.tabValues.some((function(e){return e.value===r}))}function v(e){var r=e.queryString,n=void 0!==r&&r,a=e.groupId,o=(0,i.W6)(),l=function(e){var r=e.queryString,n=void 0!==r&&r,t=e.groupId;if("string"==typeof n)return n;if(!1===n)return null;if(!0===n&&!t)throw new Error('Docusaurus error: The <Tabs> component groupId prop is required if queryString=true, because this value is used as the search param name. You can also provide an explicit value such as queryString="my-search-param".');return null!=t?t:null}({queryString:n,groupId:a});return[(0,s.aZ)(l),(0,t.useCallback)((function(e){if(l){var r=new URLSearchParams(o.location.search);r.set(l,e),o.replace(Object.assign({},o.location,{search:r.toString()}))}}),[l,o])]}function g(e){var r,n,a,o,i=e.defaultValue,s=e.queryString,u=void 0!==s&&s,d=e.groupId,g=p(e),f=(0,t.useState)((function(){return function(e){var r,n=e.defaultValue,t=e.tabValues;if(0===t.length)throw new Error("Docusaurus error: the <Tabs> component requires at least one <TabItem> children component");if(n){if(!h({value:n,tabValues:t}))throw new Error('Docusaurus error: The <Tabs> has a defaultValue "'+n+'" but none of its children has the corresponding value. Available values are: '+t.map((function(e){return e.value})).join(", ")+". If you intend to show no default tab, use defaultValue={null} instead.");return n}var a=null!=(r=t.find((function(e){return e.default})))?r:t[0];if(!a)throw new Error("Unexpected error: 0 tabValues");return a.value}({defaultValue:i,tabValues:g})})),m=f[0],x=f[1],j=v({queryString:u,groupId:d}),b=j[0],k=j[1],y=(r=function(e){return e?"docusaurus.tab."+e:null}({groupId:d}.groupId),n=(0,c.Dv)(r),a=n[0],o=n[1],[a,(0,t.useCallback)((function(e){r&&o.set(e)}),[r,o])]),w=y[0],S=y[1],q=function(){var e=null!=b?b:w;return h({value:e,tabValues:g})?e:null}();return(0,l.A)((function(){q&&x(q)}),[q]),{selectedValue:m,selectValue:(0,t.useCallback)((function(e){if(!h({value:e,tabValues:g}))throw new Error("Can't select invalid tab value="+e);x(e),k(e),S(e)}),[k,S,g]),tabValues:g}}var f=n(92303);const m={tabList:"tabList__CuJ",tabItem:"tabItem_LNqP"};var x=n(74848);function j(e){var r=e.className,n=e.block,t=e.selectedValue,i=e.selectValue,l=e.tabValues,s=[],u=(0,o.a_)().blockElementScrollPositionUntilNextRender,c=function(e){var r=e.currentTarget,n=s.indexOf(r),a=l[n].value;a!==t&&(u(r),i(a))},d=function(e){var r,n=null;switch(e.key){case"Enter":c(e);break;case"ArrowRight":var t,a=s.indexOf(e.currentTarget)+1;n=null!=(t=s[a])?t:s[0];break;case"ArrowLeft":var o,i=s.indexOf(e.currentTarget)-1;n=null!=(o=s[i])?o:s[s.length-1]}null==(r=n)||r.focus()};return(0,x.jsx)("ul",{role:"tablist","aria-orientation":"horizontal",className:(0,a.A)("tabs",{"tabs--block":n},r),children:l.map((function(e){var r=e.value,n=e.label,o=e.attributes;return(0,x.jsx)("li",Object.assign({role:"tab",tabIndex:t===r?0:-1,"aria-selected":t===r,ref:function(e){return s.push(e)},onKeyDown:d,onClick:c},o,{className:(0,a.A)("tabs__item",m.tabItem,null==o?void 0:o.className,{"tabs__item--active":t===r}),children:null!=n?n:r}),r)}))})}function b(e){var r=e.lazy,n=e.children,o=e.selectedValue,i=(Array.isArray(n)?n:[n]).filter(Boolean);if(r){var l=i.find((function(e){return e.props.value===o}));return l?(0,t.cloneElement)(l,{className:(0,a.A)("margin-top--md",l.props.className)}):null}return(0,x.jsx)("div",{className:"margin-top--md",children:i.map((function(e,r){return(0,t.cloneElement)(e,{key:r,hidden:e.props.value!==o})}))})}function k(e){var r=g(e);return(0,x.jsxs)("div",{className:(0,a.A)("tabs-container",m.tabList),children:[(0,x.jsx)(j,Object.assign({},r,e)),(0,x.jsx)(b,Object.assign({},r,e))]})}function y(e){var r=(0,f.A)();return(0,x.jsx)(k,Object.assign({},e,{children:d(e.children)}),String(r))}},67479:(e,r,n)=>{n.d(r,{A:()=>t});const t=n.p+"assets/images/ktor-initializer-a329a7877e50e02f8e8913aac9f41230.png"},28453:(e,r,n)=>{n.d(r,{R:()=>i,x:()=>l});var t=n(96540);const a={},o=t.createContext(a);function i(e){const r=t.useContext(o);return t.useMemo((function(){return"function"==typeof e?e(r):{...r,...e}}),[r,e])}function l(e){let r;return r=e.disableParentContext?"function"==typeof e.components?e.components(a):e.components||a:i(e.components),t.createElement(o.Provider,{value:r},e.children)}}}]);