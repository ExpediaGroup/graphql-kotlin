"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[6939],{60195:(e,n,r)=>{r.r(n),r.d(n,{assets:()=>p,contentTitle:()=>a,default:()=>d,frontMatter:()=>s,metadata:()=>o,toc:()=>c});var t=r(74848),i=r(28453);const s={id:"spring-schema",title:"Writing Schemas with Spring",original_id:"spring-schema"},a=void 0,o={id:"spring-server/spring-schema",title:"Writing Schemas with Spring",description:"In order to expose your queries, mutations and/or subscriptions in the GraphQL schema you need to create beans that",source:"@site/versioned_docs/version-3.x.x/spring-server/spring-schema.md",sourceDirName:"spring-server",slug:"/spring-server/spring-schema",permalink:"/graphql-kotlin/docs/3.x.x/spring-server/spring-schema",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-3.x.x/spring-server/spring-schema.md",tags:[],version:"3.x.x",lastUpdatedBy:"Samuel Vazquez",lastUpdatedAt:172791071e4,frontMatter:{id:"spring-schema",title:"Writing Schemas with Spring",original_id:"spring-schema"},sidebar:"docs",previous:{title:"Configuration Properties",permalink:"/graphql-kotlin/docs/3.x.x/spring-server/spring-properties"},next:{title:"Generating GraphQL Context",permalink:"/graphql-kotlin/docs/3.x.x/spring-server/spring-graphql-context"}},p={},c=[{value:"Spring Query Beans",id:"spring-query-beans",level:2},{value:"Spring Data Fetcher",id:"spring-data-fetcher",level:2},{value:"Spring BeanFactoryAware",id:"spring-beanfactoryaware",level:2}];function l(e){const n={a:"a",blockquote:"blockquote",code:"code",h2:"h2",hr:"hr",p:"p",pre:"pre",...(0,i.R)(),...e.components};return(0,t.jsxs)(t.Fragment,{children:[(0,t.jsxs)(n.p,{children:["In order to expose your queries, mutations and/or subscriptions in the GraphQL schema you need to create beans that\nimplement corresponding marker interface and they will be automatically picked up by ",(0,t.jsx)(n.code,{children:"graphql-kotlin-spring-server"}),"\nauto-configuration library."]}),"\n",(0,t.jsx)(n.pre,{children:(0,t.jsx)(n.code,{className:"language-kotlin",children:"@Component\nclass MyAwesomeQuery : Query {\n  fun myAwesomeQuery(): Widget { ... }\n}\n\n@Component\nclass MyAwesomeMutation : Mutation {\n  fun myAwesomeMutation(widget: Widget): Widget { ... }\n}\n\n@Component\nclass MyAwesomeSubscription : Subscription {\n  fun myAwesomeSubscription(): Publisher<Widget> { ... }\n}\n\ndata class Widget(val id: Int, val value: String)\n"})}),"\n",(0,t.jsx)(n.p,{children:"will result in a Spring Boot reactive GraphQL web application with following schema."}),"\n",(0,t.jsx)(n.pre,{children:(0,t.jsx)(n.code,{className:"language-graphql",children:"schema {\n  query: Query\n  mutation: Mutation\n  subscription: Subscription\n}\n\ntype Query {\n  myAwesomeQuery: Widget!\n}\n\ntype Mutation {\n  myAwesomeMutation(widget: WidgetInput!): Widget!\n}\n\ntype Subscription {\n  myAwesomeSubscription: Widget!\n}\n\ntype Widget {\n  id: Int!\n  value: String!\n}\n\ninput WidgetInput {\n  id: Int!\n  value: String!\n}\n"})}),"\n",(0,t.jsx)(n.h2,{id:"spring-query-beans",children:"Spring Query Beans"}),"\n",(0,t.jsxs)(n.p,{children:["Spring will automatically autowire dependent beans to our Spring query beans. Refer to ",(0,t.jsx)(n.a,{href:"https://docs.spring.io/spring/docs/current/spring-framework-reference/",children:"Spring Documentation"})," for details."]}),"\n",(0,t.jsx)(n.pre,{children:(0,t.jsx)(n.code,{className:"language-kotlin",children:"@Component\nclass WidgetQuery(private val repository: WidgetRepository) : Query {\n    fun getWidget(id: Int): Widget = repository.findWidget(id)\n}\n"})}),"\n",(0,t.jsx)(n.h2,{id:"spring-data-fetcher",children:"Spring Data Fetcher"}),"\n",(0,t.jsxs)(n.p,{children:[(0,t.jsx)(n.code,{children:"graphql-kotlin-spring-server"})," provides Spring aware data fetcher that automatically autowires Spring beans when they are\nspecified as function arguments. ",(0,t.jsx)(n.code,{children:"@Autowired"})," arguments should be explicitly excluded from the GraphQL schema by also\nspecifying ",(0,t.jsx)(n.code,{children:"@GraphQLIgnore"}),"."]}),"\n",(0,t.jsx)(n.pre,{children:(0,t.jsx)(n.code,{className:"language-kotlin",children:"@Component\nclass SpringQuery : Query {\n    fun getWidget(@GraphQLIgnore @Autowired repository: WidgetRepository, id: Int): Widget = repository.findWidget(id)\n}\n"})}),"\n",(0,t.jsxs)(n.blockquote,{children:["\n",(0,t.jsxs)(n.p,{children:["NOTE: if you are using custom data fetcher make sure that you extend ",(0,t.jsx)(n.code,{children:"SpringDataFetcher"})," instead of a base ",(0,t.jsx)(n.code,{children:"FunctionDataFetcher"}),"."]}),"\n"]}),"\n",(0,t.jsx)(n.h2,{id:"spring-beanfactoryaware",children:"Spring BeanFactoryAware"}),"\n",(0,t.jsxs)(n.p,{children:["You can use Spring beans to wire different objects together at runtime. Instead of autowiring specific beans as properties,\nyou can also dynamically resolve beans by using bean factories. There is an example of how to set this up in the example\napp in the ",(0,t.jsx)(n.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/3.x.x/examples/spring/src/main/kotlin/com/expediagroup/graphql/examples/query/TopLevelBeanFactoryQuery.kt",children:"TopLevelBeanFactoryQuery.kt"}),"."]}),"\n",(0,t.jsx)(n.pre,{children:(0,t.jsx)(n.code,{className:"language-kotlin",children:'@Component\nclass UsersQuery : Query, BeanFactoryAware {\n    private lateinit var beanFactory: BeanFactory\n\n    @GraphQLIgnore\n    override fun setBeanFactory(beanFactory: BeanFactory) {\n        this.beanFactory = beanFactory\n    }\n\n    fun findUser(id: String): SubQuery = beanFactory.getBean(User::class.java, id)\n}\n\n@Component\n@Scope("prototype")\nclass User @Autowired(required = false) constructor(private val userId: String) {\n\n    @Autowired\n    private lateinit var service: PhotoService\n\n    fun photos(numberOfPhotos: Int): List<Photo> = service.findPhotos(userId, numberOfPhotos)\n}\n'})}),"\n",(0,t.jsx)(n.hr,{}),"\n",(0,t.jsxs)(n.p,{children:["We have examples of these techniques implemented in Spring boot in the ",(0,t.jsx)(n.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/3.x.x/examples/spring/src/main/kotlin/com/expediagroup/graphql/examples/query/NestedQueries.kt",children:"example\napp"}),"."]})]})}function d(e={}){const{wrapper:n}={...(0,i.R)(),...e.components};return n?(0,t.jsx)(n,{...e,children:(0,t.jsx)(l,{...e})}):l(e)}},28453:(e,n,r)=>{r.d(n,{R:()=>a,x:()=>o});var t=r(96540);const i={},s=t.createContext(i);function a(e){const n=t.useContext(s);return t.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function o(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(i):e.components||i:a(e.components),t.createElement(s.Provider,{value:n},e.children)}}}]);