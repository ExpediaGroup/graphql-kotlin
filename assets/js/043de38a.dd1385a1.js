"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[6160],{8812:(e,n,t)=>{t.r(n),t.d(n,{assets:()=>d,contentTitle:()=>o,default:()=>c,frontMatter:()=>p,metadata:()=>l,toc:()=>g});var r=t(7462),i=t(3366),a=(t(7294),t(3905)),s=(t(8561),["components"]),p={id:"spring-schema",title:"Writing Schemas with Spring"},o=void 0,l={unversionedId:"server/spring-server/spring-schema",id:"version-5.x.x/server/spring-server/spring-schema",title:"Writing Schemas with Spring",description:"In order to expose your queries, mutations, and subscriptions in the GraphQL schema create beans that",source:"@site/versioned_docs/version-5.x.x/server/spring-server/spring-schema.md",sourceDirName:"server/spring-server",slug:"/server/spring-server/spring-schema",permalink:"/graphql-kotlin/docs/5.x.x/server/spring-server/spring-schema",draft:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-5.x.x/server/spring-server/spring-schema.md",tags:[],version:"5.x.x",lastUpdatedBy:"Samuel Vazquez",lastUpdatedAt:1677182897,formattedLastUpdatedAt:"Feb 23, 2023",frontMatter:{id:"spring-schema",title:"Writing Schemas with Spring"},sidebar:"version-5.x.x/docs",previous:{title:"Spring Server Overview",permalink:"/graphql-kotlin/docs/5.x.x/server/spring-server/spring-overview"},next:{title:"Generating GraphQL Context",permalink:"/graphql-kotlin/docs/5.x.x/server/spring-server/spring-graphql-context"}},d={},g=[{value:"Spring Beans",id:"spring-beans",level:2},{value:"Spring Beans in Arguments",id:"spring-beans-in-arguments",level:2}],u={toc:g};function c(e){var n=e.components,t=(0,i.Z)(e,s);return(0,a.kt)("wrapper",(0,r.Z)({},u,t,{components:n,mdxType:"MDXLayout"}),(0,a.kt)("p",null,"In order to expose your queries, mutations, and subscriptions in the GraphQL schema create beans that\nimplement the corresponding marker interface and they will be automatically picked up by ",(0,a.kt)("inlineCode",{parentName:"p"},"graphql-kotlin-spring-server"),"\nauto-configuration library."),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-kotlin"},"data class Widget(val id: ID, val value: String)\n\n@Component\nclass WidgetQuery : Query {\n  fun widget(id: ID): Widget = getWidgetFromDB(id)\n}\n\n@Component\nclass WidgetMutation : Mutation {\n  fun updateWidget(id: ID, value: String): Boolean = updateWidgetInDB(id, value)\n}\n\n@Component\nclass WidgetSubscription : Subscription {\n  fun widgetChanges(id: ID): Publisher<Widget> = getPublisherOfUpdates(id)\n}\n")),(0,a.kt)("p",null,"will result in a Spring Boot reactive GraphQL web application with following schema."),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-graphql"},"schema {\n  query: Query\n  mutation: Mutation\n  subscription: Subscription\n}\n\ntype Widget {\n    id: ID!\n    value: String!\n}\n\ntype Query {\n  widget(id: ID!): Widget!\n}\n\ntype Mutation {\n    updateWidget(id: ID!, value: String!): Boolean!\n}\n\ntype Subscription {\n    widgetChanges(id: ID!): Widget!\n}\n")),(0,a.kt)("h2",{id:"spring-beans"},"Spring Beans"),(0,a.kt)("p",null,"Since the top level objects are Spring components, Spring will automatically autowire dependent beans as normal. Refer to ",(0,a.kt)("a",{parentName:"p",href:"https://docs.spring.io/spring/docs/current/spring-framework-reference/"},"Spring Documentation")," for details."),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-kotlin"},"@Component\nclass WidgetQuery(private val repository: WidgetRepository) : Query {\n    fun getWidget(id: Int): Widget = repository.findWidget(id)\n}\n")),(0,a.kt)("h2",{id:"spring-beans-in-arguments"},"Spring Beans in Arguments"),(0,a.kt)("p",null,(0,a.kt)("inlineCode",{parentName:"p"},"graphql-kotlin-spring-server")," provides Spring-aware data fetcher that automatically autowires Spring beans when they are\nspecified as function arguments. ",(0,a.kt)("inlineCode",{parentName:"p"},"@Autowired")," arguments should be explicitly excluded from the GraphQL schema by also\nspecifying ",(0,a.kt)("inlineCode",{parentName:"p"},"@GraphQLIgnore"),"."),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-kotlin"},"@Component\nclass SpringQuery : Query {\n    fun getWidget(@GraphQLIgnore @Autowired repository: WidgetRepository, id: Int): Widget = repository.findWidget(id)\n}\n")),(0,a.kt)("admonition",{type:"note"},(0,a.kt)("p",{parentName:"admonition"},"If you are using custom data fetcher make sure that you extend ",(0,a.kt)("inlineCode",{parentName:"p"},"SpringDataFetcher")," instead of the base ",(0,a.kt)("inlineCode",{parentName:"p"},"FunctionDataFetcher")," to keep this functionallity.")),(0,a.kt)("p",null,"We have examples of these techniques implemented in Spring boot in the ",(0,a.kt)("a",{parentName:"p",href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/master/examples/server/spring-server/src/main/kotlin/com/expediagroup/graphql/examples/server/spring/query/NestedQueries.kt"},"example\napp"),"."))}c.isMDXComponent=!0}}]);