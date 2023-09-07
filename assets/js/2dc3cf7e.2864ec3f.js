"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[3775],{3905:(e,t,n)=>{n.d(t,{Zo:()=>c,kt:()=>m});var r=n(67294);function i(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function a(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function s(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?a(Object(n),!0).forEach((function(t){i(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):a(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function o(e,t){if(null==e)return{};var n,r,i=function(e,t){if(null==e)return{};var n,r,i={},a=Object.keys(e);for(r=0;r<a.length;r++)n=a[r],t.indexOf(n)>=0||(i[n]=e[n]);return i}(e,t);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);for(r=0;r<a.length;r++)n=a[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(i[n]=e[n])}return i}var p=r.createContext({}),l=function(e){var t=r.useContext(p),n=t;return e&&(n="function"==typeof e?e(t):s(s({},t),e)),n},c=function(e){var t=l(e.components);return r.createElement(p.Provider,{value:t},e.children)},u="mdxType",d={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},g=r.forwardRef((function(e,t){var n=e.components,i=e.mdxType,a=e.originalType,p=e.parentName,c=o(e,["components","mdxType","originalType","parentName"]),u=l(n),g=i,m=u["".concat(p,".").concat(g)]||u[g]||d[g]||a;return n?r.createElement(m,s(s({ref:t},c),{},{components:n})):r.createElement(m,s({ref:t},c))}));function m(e,t){var n=arguments,i=t&&t.mdxType;if("string"==typeof e||i){var a=n.length,s=new Array(a);s[0]=g;var o={};for(var p in t)hasOwnProperty.call(t,p)&&(o[p]=t[p]);o.originalType=e,o[u]="string"==typeof e?e:i,s[1]=o;for(var l=2;l<a;l++)s[l]=n[l];return r.createElement.apply(null,s)}return r.createElement.apply(null,n)}g.displayName="MDXCreateElement"},92858:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>c,contentTitle:()=>p,default:()=>m,frontMatter:()=>o,metadata:()=>l,toc:()=>u});var r=n(87462),i=n(63366),a=(n(67294),n(3905)),s=["components"],o={id:"spring-schema",title:"Writing Schemas with Spring"},p=void 0,l={unversionedId:"server/spring-server/spring-schema",id:"server/spring-server/spring-schema",title:"Writing Schemas with Spring",description:"In order to expose your schema directives, queries, mutations, and subscriptions in the GraphQL schema create beans that",source:"@site/docs/server/spring-server/spring-schema.md",sourceDirName:"server/spring-server",slug:"/server/spring-server/spring-schema",permalink:"/graphql-kotlin/docs/7.x.x/server/spring-server/spring-schema",draft:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/docs/server/spring-server/spring-schema.md",tags:[],version:"current",lastUpdatedBy:"Dariusz Kuc",lastUpdatedAt:1694122111,formattedLastUpdatedAt:"Sep 7, 2023",frontMatter:{id:"spring-schema",title:"Writing Schemas with Spring"},sidebar:"docs",previous:{title:"Spring Server Overview",permalink:"/graphql-kotlin/docs/7.x.x/server/spring-server/spring-overview"},next:{title:"Generating GraphQL Context",permalink:"/graphql-kotlin/docs/7.x.x/server/spring-server/spring-graphql-context"}},c={},u=[{value:"Spring Beans",id:"spring-beans",level:2},{value:"Spring Beans in Arguments",id:"spring-beans-in-arguments",level:2}],d={toc:u},g="wrapper";function m(e){var t=e.components,n=(0,i.Z)(e,s);return(0,a.kt)(g,(0,r.Z)({},d,n,{components:t,mdxType:"MDXLayout"}),(0,a.kt)("p",null,"In order to expose your schema directives, queries, mutations, and subscriptions in the GraphQL schema create beans that\nimplement the corresponding marker interface and they will be automatically picked up by ",(0,a.kt)("inlineCode",{parentName:"p"},"graphql-kotlin-spring-server"),"\nauto-configuration library."),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-kotlin"},'@ContactDirective(\n    name = "My Team Name",\n    url = "https://myteam.slack.com/archives/teams-chat-room-url",\n    description = "send urgent issues to [#oncall](https://yourteam.slack.com/archives/oncall)."\n)\n@GraphQLDescription("My schema description")\n@Component\nclass MySchema : Schema\n\ndata class Widget(val id: ID, val value: String)\n\n@Component\nclass WidgetQuery : Query {\n  fun widget(id: ID): Widget = getWidgetFromDB(id)\n}\n\n@Component\nclass WidgetMutation : Mutation {\n  fun updateWidget(id: ID, value: String): Boolean = updateWidgetInDB(id, value)\n}\n\n@Component\nclass WidgetSubscription : Subscription {\n  fun widgetChanges(id: ID): Publisher<Widget> = getPublisherOfUpdates(id)\n}\n')),(0,a.kt)("p",null,"will result in a Spring Boot reactive GraphQL web application with following schema."),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-graphql"},'schema @contact(description : "send urgent issues to [#oncall](https://yourteam.slack.com/archives/oncall).", name : "My Team Name", url : "https://myteam.slack.com/archives/teams-chat-room-url"){\n  query: Query\n  mutation: Mutation\n  subscription: Subscription\n}\n\ntype Widget {\n    id: ID!\n    value: String!\n}\n\ntype Query {\n  widget(id: ID!): Widget!\n}\n\ntype Mutation {\n    updateWidget(id: ID!, value: String!): Boolean!\n}\n\ntype Subscription {\n    widgetChanges(id: ID!): Widget!\n}\n')),(0,a.kt)("h2",{id:"spring-beans"},"Spring Beans"),(0,a.kt)("p",null,"Since the top level objects are Spring components, Spring will automatically autowire dependent beans as normal. Refer to ",(0,a.kt)("a",{parentName:"p",href:"https://docs.spring.io/spring/docs/current/spring-framework-reference/"},"Spring Documentation")," for details."),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-kotlin"},"@Component\nclass WidgetQuery(private val repository: WidgetRepository) : Query {\n    fun getWidget(id: Int): Widget = repository.findWidget(id)\n}\n")),(0,a.kt)("h2",{id:"spring-beans-in-arguments"},"Spring Beans in Arguments"),(0,a.kt)("p",null,(0,a.kt)("inlineCode",{parentName:"p"},"graphql-kotlin-spring-server")," provides Spring-aware data fetcher that automatically autowires Spring beans when they are\nspecified as function arguments. ",(0,a.kt)("inlineCode",{parentName:"p"},"@Autowired")," arguments should be explicitly excluded from the GraphQL schema by also\nspecifying ",(0,a.kt)("inlineCode",{parentName:"p"},"@GraphQLIgnore"),"."),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-kotlin"},"@Component\nclass SpringQuery : Query {\n    fun getWidget(@GraphQLIgnore @Autowired repository: WidgetRepository, id: Int): Widget = repository.findWidget(id)\n}\n")),(0,a.kt)("admonition",{type:"note"},(0,a.kt)("p",{parentName:"admonition"},"If you are using custom data fetcher make sure that you extend ",(0,a.kt)("inlineCode",{parentName:"p"},"SpringDataFetcher")," instead of the base ",(0,a.kt)("inlineCode",{parentName:"p"},"FunctionDataFetcher")," to keep this functionallity.")),(0,a.kt)("p",null,"We have examples of these techniques implemented in Spring boot in the ",(0,a.kt)("a",{parentName:"p",href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/master/examples/server/spring-server/src/main/kotlin/com/expediagroup/graphql/examples/server/spring/query/NestedQueries.kt"},"example\napp"),"."))}m.isMDXComponent=!0}}]);