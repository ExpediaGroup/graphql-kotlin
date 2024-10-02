"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[7281],{30634:(e,r,n)=>{n.r(r),n.d(r,{assets:()=>c,contentTitle:()=>o,default:()=>h,frontMatter:()=>l,metadata:()=>u,toc:()=>d});var t=n(74848),a=n(28453),i=n(11470),s=n(19365);const l={id:"spring-overview",title:"Spring Server Overview"},o=void 0,u={id:"server/spring-server/spring-overview",title:"Spring Server Overview",description:"graphql-kotlin-spring-server",source:"@site/versioned_docs/version-4.x.x/server/spring-server/spring-overview.mdx",sourceDirName:"server/spring-server",slug:"/server/spring-server/spring-overview",permalink:"/graphql-kotlin/docs/4.x.x/server/spring-server/spring-overview",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-4.x.x/server/spring-server/spring-overview.mdx",tags:[],version:"4.x.x",lastUpdatedBy:"Samuel Vazquez",lastUpdatedAt:172791071e4,frontMatter:{id:"spring-overview",title:"Spring Server Overview"},sidebar:"docs",previous:{title:"Subscriptions",permalink:"/graphql-kotlin/docs/4.x.x/server/server-subscriptions"},next:{title:"Writing Schemas with Spring",permalink:"/graphql-kotlin/docs/4.x.x/server/spring-server/spring-schema"}},c={},d=[{value:"WebFlux vs WebMVC",id:"webflux-vs-webmvc",level:2},{value:"Setup",id:"setup",level:2},{value:"Configuration",id:"configuration",level:2},{value:"Default Routes",id:"default-routes",level:2}];function p(e){const r={a:"a",code:"code",h2:"h2",img:"img",li:"li",p:"p",pre:"pre",strong:"strong",ul:"ul",...(0,a.R)(),...e.components};return(0,t.jsxs)(t.Fragment,{children:[(0,t.jsxs)(r.p,{children:[(0,t.jsx)(r.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/graphql-kotlin-spring-server",children:"graphql-kotlin-spring-server"}),"\nis a Spring Boot auto-configuration library that automatically configures beans required to start up a reactive GraphQL\nweb server."]}),"\n",(0,t.jsx)(r.h2,{id:"webflux-vs-webmvc",children:"WebFlux vs WebMVC"}),"\n",(0,t.jsxs)(r.p,{children:["This library is built on a ",(0,t.jsx)(r.a,{href:"https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html",children:"Spring WebFlux (reactive)"})," stack which is a non-blocking alternative to a traditional ",(0,t.jsx)(r.a,{href:"https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html",children:"Spring Web MVC (servlet)"})," based stack.\nSince the frameworks utilize different threading models they cannot and should not be intermixed.\nWhen building a GraphQL server using ",(0,t.jsx)(r.code,{children:"graphql-kotlin-spring-server"})," all your queries and mutations should follow one of the supported ",(0,t.jsx)(r.a,{href:"/graphql-kotlin/docs/4.x.x/schema-generator/execution/async-models",children:"asynchronous execution models"}),"."]}),"\n",(0,t.jsx)(r.h2,{id:"setup",children:"Setup"}),"\n",(0,t.jsxs)(r.p,{children:["The simplest way to create a new Kotlin Spring Boot app is by generating one using ",(0,t.jsx)(r.a,{href:"https://start.spring.io/",children:"Spring Initializr."})]}),"\n",(0,t.jsx)(r.p,{children:(0,t.jsx)(r.img,{alt:"Image of https://start.spring.io/",src:n(33046).A+"",width:"1689",height:"726"})}),"\n",(0,t.jsxs)(r.p,{children:["Once you get the sample application setup locally, you will need to add ",(0,t.jsx)(r.code,{children:"graphql-kotlin-spring-server"})," dependency:"]}),"\n",(0,t.jsxs)(i.A,{defaultValue:"gradle",values:[{label:"Gradle Kotlin",value:"gradle"},{label:"Maven",value:"maven"}],children:[(0,t.jsx)(s.A,{value:"gradle",children:(0,t.jsx)(r.pre,{children:(0,t.jsx)(r.code,{className:"language-kotlin",children:'implementation("com.expediagroup", "graphql-kotlin-spring-server", latestVersion)\n'})})}),(0,t.jsx)(s.A,{value:"maven",children:(0,t.jsx)(r.pre,{children:(0,t.jsx)(r.code,{className:"language-xml",children:"<dependency>\n    <groupId>com.expediagroup</groupId>\n    <artifactId>graphql-kotlin-spring-server</artifactId>\n    <version>${latestVersion}</version>\n</dependency>\n"})})})]}),"\n",(0,t.jsx)(r.h2,{id:"configuration",children:"Configuration"}),"\n",(0,t.jsxs)(r.p,{children:["At a minimum, in order for ",(0,t.jsx)(r.code,{children:"graphql-kotlin-spring-server"})," to automatically configure your GraphQL web server you need to\nspecify a list of supported packages that can be scanned for exposing your schema objects through reflections."]}),"\n",(0,t.jsxs)(r.p,{children:["You can do this through the spring application config or by overriding the ",(0,t.jsx)(r.code,{children:"SchemaGeneratorConfig"})," bean. See customization below."]}),"\n",(0,t.jsx)(r.pre,{children:(0,t.jsx)(r.code,{className:"language-yaml",children:'graphql:\n  packages:\n    - "com.your.package"\n'})}),"\n",(0,t.jsx)(r.h2,{id:"default-routes",children:"Default Routes"}),"\n",(0,t.jsx)(r.p,{children:"Your newly created GraphQL server starts up with following preconfigured default routes:"}),"\n",(0,t.jsxs)(r.ul,{children:["\n",(0,t.jsxs)(r.li,{children:[(0,t.jsx)(r.strong,{children:"/graphql"})," - GraphQL server endpoint used for processing queries and mutations"]}),"\n",(0,t.jsxs)(r.li,{children:[(0,t.jsx)(r.strong,{children:"/subscriptions"})," - GraphQL server endpoint used for processing subscriptions"]}),"\n",(0,t.jsxs)(r.li,{children:[(0,t.jsx)(r.strong,{children:"/sdl"})," - Convenience endpoint that returns current schema in Schema Definition Language format"]}),"\n",(0,t.jsxs)(r.li,{children:[(0,t.jsx)(r.strong,{children:"/playground"})," - Prisma Labs ",(0,t.jsx)(r.a,{href:"https://github.com/prisma-labs/graphql-playground",children:"GraphQL Playground IDE"})," endpoint"]}),"\n"]})]})}function h(e={}){const{wrapper:r}={...(0,a.R)(),...e.components};return r?(0,t.jsx)(r,{...e,children:(0,t.jsx)(p,{...e})}):p(e)}},19365:(e,r,n)=>{n.d(r,{A:()=>s});n(96540);var t=n(34164);const a={tabItem:"tabItem_Ymn6"};var i=n(74848);function s(e){var r=e.children,n=e.hidden,s=e.className;return(0,i.jsx)("div",{role:"tabpanel",className:(0,t.A)(a.tabItem,s),hidden:n,children:r})}},11470:(e,r,n)=>{n.d(r,{A:()=>y});var t=n(96540),a=n(34164),i=n(23104),s=n(56347),l=n(205),o=n(57485),u=n(31682),c=n(70679);function d(e){var r,n;return null!=(r=null==(n=t.Children.toArray(e).filter((function(e){return"\n"!==e})).map((function(e){if(!e||(0,t.isValidElement)(e)&&((r=e.props)&&"object"==typeof r&&"value"in r))return e;var r;throw new Error("Docusaurus error: Bad <Tabs> child <"+("string"==typeof e.type?e.type:e.type.name)+'>: all children of the <Tabs> component should be <TabItem>, and every <TabItem> should have a unique "value" prop.')})))?void 0:n.filter(Boolean))?r:[]}function p(e){var r=e.values,n=e.children;return(0,t.useMemo)((function(){var e=null!=r?r:function(e){return d(e).map((function(e){var r=e.props;return{value:r.value,label:r.label,attributes:r.attributes,default:r.default}}))}(n);return function(e){var r=(0,u.XI)(e,(function(e,r){return e.value===r.value}));if(r.length>0)throw new Error('Docusaurus error: Duplicate values "'+r.map((function(e){return e.value})).join(", ")+'" found in <Tabs>. Every value needs to be unique.')}(e),e}),[r,n])}function h(e){var r=e.value;return e.tabValues.some((function(e){return e.value===r}))}function v(e){var r=e.queryString,n=void 0!==r&&r,a=e.groupId,i=(0,s.W6)(),l=function(e){var r=e.queryString,n=void 0!==r&&r,t=e.groupId;if("string"==typeof n)return n;if(!1===n)return null;if(!0===n&&!t)throw new Error('Docusaurus error: The <Tabs> component groupId prop is required if queryString=true, because this value is used as the search param name. You can also provide an explicit value such as queryString="my-search-param".');return null!=t?t:null}({queryString:n,groupId:a});return[(0,o.aZ)(l),(0,t.useCallback)((function(e){if(l){var r=new URLSearchParams(i.location.search);r.set(l,e),i.replace(Object.assign({},i.location,{search:r.toString()}))}}),[l,i])]}function g(e){var r,n,a,i,s=e.defaultValue,o=e.queryString,u=void 0!==o&&o,d=e.groupId,g=p(e),f=(0,t.useState)((function(){return function(e){var r,n=e.defaultValue,t=e.tabValues;if(0===t.length)throw new Error("Docusaurus error: the <Tabs> component requires at least one <TabItem> children component");if(n){if(!h({value:n,tabValues:t}))throw new Error('Docusaurus error: The <Tabs> has a defaultValue "'+n+'" but none of its children has the corresponding value. Available values are: '+t.map((function(e){return e.value})).join(", ")+". If you intend to show no default tab, use defaultValue={null} instead.");return n}var a=null!=(r=t.find((function(e){return e.default})))?r:t[0];if(!a)throw new Error("Unexpected error: 0 tabValues");return a.value}({defaultValue:s,tabValues:g})})),m=f[0],b=f[1],x=v({queryString:u,groupId:d}),j=x[0],w=x[1],y=(r=function(e){return e?"docusaurus.tab."+e:null}({groupId:d}.groupId),n=(0,c.Dv)(r),a=n[0],i=n[1],[a,(0,t.useCallback)((function(e){r&&i.set(e)}),[r,i])]),k=y[0],q=y[1],S=function(){var e=null!=j?j:k;return h({value:e,tabValues:g})?e:null}();return(0,l.A)((function(){S&&b(S)}),[S]),{selectedValue:m,selectValue:(0,t.useCallback)((function(e){if(!h({value:e,tabValues:g}))throw new Error("Can't select invalid tab value="+e);b(e),w(e),q(e)}),[w,q,g]),tabValues:g}}var f=n(92303);const m={tabList:"tabList__CuJ",tabItem:"tabItem_LNqP"};var b=n(74848);function x(e){var r=e.className,n=e.block,t=e.selectedValue,s=e.selectValue,l=e.tabValues,o=[],u=(0,i.a_)().blockElementScrollPositionUntilNextRender,c=function(e){var r=e.currentTarget,n=o.indexOf(r),a=l[n].value;a!==t&&(u(r),s(a))},d=function(e){var r,n=null;switch(e.key){case"Enter":c(e);break;case"ArrowRight":var t,a=o.indexOf(e.currentTarget)+1;n=null!=(t=o[a])?t:o[0];break;case"ArrowLeft":var i,s=o.indexOf(e.currentTarget)-1;n=null!=(i=o[s])?i:o[o.length-1]}null==(r=n)||r.focus()};return(0,b.jsx)("ul",{role:"tablist","aria-orientation":"horizontal",className:(0,a.A)("tabs",{"tabs--block":n},r),children:l.map((function(e){var r=e.value,n=e.label,i=e.attributes;return(0,b.jsx)("li",Object.assign({role:"tab",tabIndex:t===r?0:-1,"aria-selected":t===r,ref:function(e){return o.push(e)},onKeyDown:d,onClick:c},i,{className:(0,a.A)("tabs__item",m.tabItem,null==i?void 0:i.className,{"tabs__item--active":t===r}),children:null!=n?n:r}),r)}))})}function j(e){var r=e.lazy,n=e.children,i=e.selectedValue,s=(Array.isArray(n)?n:[n]).filter(Boolean);if(r){var l=s.find((function(e){return e.props.value===i}));return l?(0,t.cloneElement)(l,{className:(0,a.A)("margin-top--md",l.props.className)}):null}return(0,b.jsx)("div",{className:"margin-top--md",children:s.map((function(e,r){return(0,t.cloneElement)(e,{key:r,hidden:e.props.value!==i})}))})}function w(e){var r=g(e);return(0,b.jsxs)("div",{className:(0,a.A)("tabs-container",m.tabList),children:[(0,b.jsx)(x,Object.assign({},r,e)),(0,b.jsx)(j,Object.assign({},r,e))]})}function y(e){var r=(0,f.A)();return(0,b.jsx)(w,Object.assign({},e,{children:d(e.children)}),String(r))}},33046:(e,r,n)=>{n.d(r,{A:()=>t});const t=n.p+"assets/images/spring-initializer-a906245aeee5602ea82b7bb98a9a50bb.png"},28453:(e,r,n)=>{n.d(r,{R:()=>s,x:()=>l});var t=n(96540);const a={},i=t.createContext(a);function s(e){const r=t.useContext(i);return t.useMemo((function(){return"function"==typeof e?e(r):{...r,...e}}),[r,e])}function l(e){let r;return r=e.disableParentContext?"function"==typeof e.components?e.components(a):e.components||a:s(e.components),t.createElement(i.Provider,{value:r},e.children)}}}]);