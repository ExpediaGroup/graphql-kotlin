"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[2671],{20078:(e,n,t)=>{t.r(n),t.d(n,{assets:()=>c,contentTitle:()=>l,default:()=>x,frontMatter:()=>o,metadata:()=>d,toc:()=>h});var a=t(74848),s=t(28453),r=t(11470),i=t(19365);const o={id:"data-loader-instrumentation",title:"Data Loader Instrumentations"},l=void 0,d={id:"server/data-loader/data-loader-instrumentation",title:"Data Loader Instrumentations",description:"graphql-kotlin-dataloader-instrumentation is set of custom Instrumentations",source:"@site/versioned_docs/version-7.x.x/server/data-loader/data-loader-instrumentation.mdx",sourceDirName:"server/data-loader",slug:"/server/data-loader/data-loader-instrumentation",permalink:"/graphql-kotlin/docs/7.x.x/server/data-loader/data-loader-instrumentation",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-7.x.x/server/data-loader/data-loader-instrumentation.mdx",tags:[],version:"7.x.x",lastUpdatedBy:"Daniel",lastUpdatedAt:1741819577e3,frontMatter:{id:"data-loader-instrumentation",title:"Data Loader Instrumentations"},sidebar:"docs",previous:{title:"Data Loaders",permalink:"/graphql-kotlin/docs/7.x.x/server/data-loader/"},next:{title:"Spring Server Overview",permalink:"/graphql-kotlin/docs/7.x.x/server/spring-server/spring-overview"}},c={},h=[{value:"Dispatching by level",id:"dispatching-by-level",level:2},{value:"Example",id:"example",level:3},{value:"Usage",id:"usage",level:3},{value:"Limitations",id:"limitations",level:3},{value:"Dispatching by synchronous execution exhaustion",id:"dispatching-by-synchronous-execution-exhaustion",level:2},{value:"Example",id:"example-1",level:3},{value:"Usage",id:"usage-1",level:3},{value:"Multiple data loaders per field data fetcher",id:"multiple-data-loaders-per-field-data-fetcher",level:2},{value:"DispatchIfNeeded",id:"dispatchifneeded",level:3},{value:"Example",id:"example-2",level:3}];function u(e){const n={a:"a",admonition:"admonition",code:"code",em:"em",h2:"h2",h3:"h3",img:"img",li:"li",ol:"ol",p:"p",pre:"pre",strong:"strong",ul:"ul",...(0,s.R)(),...e.components};return(0,a.jsxs)(a.Fragment,{children:[(0,a.jsxs)(n.p,{children:[(0,a.jsx)(n.code,{children:"graphql-kotlin-dataloader-instrumentation"})," is set of custom ",(0,a.jsx)(n.a,{href:"https://www.graphql-java.com/documentation/instrumentation/",children:"Instrumentations"}),"\nthat will calculate the right moment to dispatch ",(0,a.jsx)(n.code,{children:"KotlinDataLoader"}),"s across single or batch GraphQL operations."]}),"\n",(0,a.jsxs)(n.p,{children:["These custom instrumentations follow the similar approach as the default ",(0,a.jsx)(n.a,{href:"https://github.com/graphql-java/graphql-java/blob/master/src/main/java/graphql/execution/instrumentation/dataloader/DataLoaderDispatcherInstrumentation.java",children:"DataLoaderDispatcherInstrumentation"}),"\nfrom ",(0,a.jsx)(n.code,{children:"graphql-java"}),", the main difference is that regular instrumentations apply to a single ",(0,a.jsx)(n.code,{children:"ExecutionInput"})," aka ",(0,a.jsx)(n.a,{href:"https://www.graphql-java.com/documentation/execution#queries",children:"GraphQL Operation"}),",\nwhereas these custom instrumentations apply to multiple GraphQL operations (say a BatchRequest) and stores their state in the ",(0,a.jsx)(n.code,{children:"GraphQLContext"}),"\nallowing batching and deduplication of transactions across those multiple GraphQL operations."]}),"\n",(0,a.jsx)(n.p,{children:"By default, each GraphQL operation is processed independently of each other. Multiple operations can be processed\ntogether as if they were single GraphQL request if they are part of the same batch request."}),"\n",(0,a.jsxs)(n.p,{children:["The ",(0,a.jsx)(n.code,{children:"graphql-kotlin-dataloader-instrumentation"})," module contains 2 custom ",(0,a.jsx)(n.code,{children:"DataLoader"})," instrumentations."]}),"\n",(0,a.jsx)(n.h2,{id:"dispatching-by-level",children:"Dispatching by level"}),"\n",(0,a.jsxs)(n.p,{children:["The ",(0,a.jsx)(n.code,{children:"DataLoaderLevelDispatchedInstrumentation"})," tracks the state of all ",(0,a.jsx)(n.code,{children:"ExecutionInputs"})," across operations. When a certain\nfield dispatches, it will check if all fields across all operations for a particular level were dispatched and if the condition is met,\nit will dispatch all the data loaders."]}),"\n",(0,a.jsx)(n.h3,{id:"example",children:"Example"}),"\n",(0,a.jsxs)(n.p,{children:["You can find additional examples in our ",(0,a.jsx)(n.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/master/executions/graphql-kotlin-dataloader-instrumentation/src/test/kotlin/com/expediagroup/graphql/dataloader/instrumentation/level/DataLoaderLevelDispatchedInstrumentationTest.kt",children:"unit tests"}),"."]}),"\n",(0,a.jsxs)(r.A,{defaultValue:"by-level-queries",values:[{label:"Queries",value:"by-level-queries"},{label:"Execution",value:"by-level-execution"}],children:[(0,a.jsx)(i.A,{value:"by-level-queries",children:(0,a.jsx)(n.pre,{children:(0,a.jsx)(n.code,{className:"language-graphql",children:"query Q1 {\n    astronaut(id: 1) { # async\n        id\n        name\n        missions { # async\n            id\n            designation\n        }\n    }\n}\n\nquery Q2 {\n    astronaut(id: 2) { # async\n        id\n        name\n        missions { # async\n            id\n            designation\n        }\n    }\n}\n"})})}),(0,a.jsxs)(i.A,{value:"by-level-execution",children:[(0,a.jsx)(n.p,{children:(0,a.jsx)(n.img,{alt:"Image of data loader level dispatched instrumentation",src:t(66663).A+"",width:"1213",height:"571"})}),(0,a.jsxs)(n.ul,{children:["\n",(0,a.jsxs)(n.li,{children:["The ",(0,a.jsx)(n.code,{children:"astronaut"})," ",(0,a.jsx)(n.code,{children:"DataFetcher"})," uses a ",(0,a.jsx)(n.code,{children:"AstronautDataLoader"})," which will be dispatched when ",(0,a.jsx)(n.strong,{children:"Level 1"})," of those 2 operations\nis dispatched, causing the ",(0,a.jsx)(n.code,{children:"AstronautDataLoader"})," to load 2 astronauts."]}),"\n",(0,a.jsxs)(n.li,{children:["The ",(0,a.jsx)(n.code,{children:"missions"})," ",(0,a.jsx)(n.code,{children:"DataFetcher"})," uses a ",(0,a.jsx)(n.code,{children:"MissionsByAstronautDataLoader"})," which will be dispatched when ",(0,a.jsx)(n.strong,{children:"Level 2"})," of those 2 operations\nis dispatched, causing the ",(0,a.jsx)(n.code,{children:"MissionsByAstronautDataLoader"})," to load 2 lists of missions by astronaut."]}),"\n"]})]})]}),"\n",(0,a.jsx)(n.h3,{id:"usage",children:"Usage"}),"\n",(0,a.jsxs)(n.p,{children:["In order to enable batching by level, you need to configure your GraphQL instance with the ",(0,a.jsx)(n.code,{children:"DataLoaderLevelDispatchedInstrumentation"}),"."]}),"\n",(0,a.jsx)(n.pre,{children:(0,a.jsx)(n.code,{className:"language-kotlin",children:"val graphQL = GraphQL.Builder()\n    .doNotAddDefaultInstrumentations()\n    .instrumentation(DataLoaderLevelDispatchedInstrumentation())\n    // configure schema, type wiring, etc.\n    .build()\n"})}),"\n",(0,a.jsx)(n.p,{children:"This data loader instrumentation relies on a global state object that needs be stored in the GraphQLContext map"}),"\n",(0,a.jsx)(n.pre,{children:(0,a.jsx)(n.code,{className:"language-kotlin",children:"val graphQLContext = mapOf(\n    ExecutionLevelDispatchedState::class to ExecutionLevelDispatchedState(queries.size)\n)\n"})}),"\n",(0,a.jsxs)(n.admonition,{type:"info",children:[(0,a.jsxs)(n.p,{children:[(0,a.jsx)(n.code,{children:"graphql-kotlin-spring-server"})," provides convenient integration of batch loader functionality through simple configuration.\nBatching by level can be enabled by configuring following properties:"]}),(0,a.jsx)(n.pre,{children:(0,a.jsx)(n.code,{className:"language-yaml",children:"graphql:\n  batching:\n   enabled: true\n   strategy: LEVEL_DISPATCHED\n"})})]}),"\n",(0,a.jsx)(n.h3,{id:"limitations",children:"Limitations"}),"\n",(0,a.jsxs)(n.p,{children:["This instrumentation is a good option if your ",(0,a.jsx)(n.strong,{children:"GraphQLServer"})," will receive a batched request with operations of the same type,\nin those cases batching by level is enough, however, this solution is far from being the most optimal as we don't necessarily want to dispatch by level."]}),"\n",(0,a.jsx)(n.h2,{id:"dispatching-by-synchronous-execution-exhaustion",children:"Dispatching by synchronous execution exhaustion"}),"\n",(0,a.jsx)(n.p,{children:"The most optimal time to dispatch all data loaders is when all possible synchronous execution paths across all batch\noperations were exhausted. Synchronous execution path is considered exhausted (or completed) when all currently processed\ndata fetchers were either resolved to a scalar or a future promise."}),"\n",(0,a.jsx)(n.p,{children:"Let's analyze how GraphQL execution works, but first lets check some GraphQL concepts:"}),"\n",(0,a.jsx)(n.p,{children:(0,a.jsx)(n.strong,{children:"DataFetcher"})}),"\n",(0,a.jsxs)(n.p,{children:["Each field in GraphQL has a resolver aka ",(0,a.jsx)(n.code,{children:"DataFetcher"})," associated with it, some fields will use specialized ",(0,a.jsx)(n.code,{children:"DataFetcher"}),"s\nthat knows how to go to a database or make a network request to get field information while most simply take\ndata from the returned memory objects."]}),"\n",(0,a.jsx)(n.p,{children:(0,a.jsx)(n.strong,{children:"Execution Strategy"})}),"\n",(0,a.jsx)(n.p,{children:"The process of finding values for a list of fields from the GraphQL Query, using a recursive strategy."}),"\n",(0,a.jsx)(n.h3,{id:"example-1",children:"Example"}),"\n",(0,a.jsxs)(n.p,{children:["You can find additional examples in our ",(0,a.jsx)(n.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/master/executions/graphql-kotlin-dataloader-instrumentation/src/test/kotlin/com/expediagroup/graphql/dataloader/instrumentation/syncexhaustion/DataLoaderSyncExecutionExhaustedInstrumentationTest.kt",children:"unit tests"}),"."]}),"\n",(0,a.jsxs)(r.A,{defaultValue:"by-sync-exhaustion-queries",values:[{label:"Queries",value:"by-sync-exhaustion-queries"},{label:"Execution",value:"by-sync-exhaustion-execution"}],children:[(0,a.jsx)(i.A,{value:"by-sync-exhaustion-queries",children:(0,a.jsx)(n.pre,{children:(0,a.jsx)(n.code,{className:"language-graphql",children:"query Q1 {\n    astronaut(id: 1) { # async\n        id\n        name\n        missions { # async\n            id\n            designation\n        }\n    }\n}\n\nquery Q2 {\n    nasa { #sync\n        astronaut(id: 2) { # async\n            id\n            name\n            missions { # async\n                id\n                designation\n            }\n        }\n        address { # sync\n            street\n            zipCode\n        }\n        phoneNumber\n    }\n}\n"})})}),(0,a.jsxs)(i.A,{value:"by-sync-exhaustion-execution",children:[(0,a.jsx)(n.p,{children:(0,a.jsx)(n.img,{alt:"Image of data loader level dispatched instrumentation",src:t(88460).A+"",width:"1375",height:"759"})}),(0,a.jsx)(n.p,{children:(0,a.jsx)(n.strong,{children:"The order of execution of the queries will be:"})}),(0,a.jsx)(n.p,{children:(0,a.jsx)(n.em,{children:(0,a.jsx)(n.strong,{children:"for Q1"})})}),(0,a.jsxs)(n.ol,{children:["\n",(0,a.jsxs)(n.li,{children:["Start an ",(0,a.jsx)(n.code,{children:"ExecutionStrategy"})," for the ",(0,a.jsx)(n.code,{children:"root"})," field of the query, to concurrently resolve ",(0,a.jsx)(n.code,{children:"astronaut"})," field.","\n",(0,a.jsxs)(n.ul,{children:["\n",(0,a.jsxs)(n.li,{children:[(0,a.jsx)(n.code,{children:"astronaut"})," ",(0,a.jsx)(n.strong,{children:"DataFetcher"})," will invoke the ",(0,a.jsx)(n.code,{children:"AstronautDataLoader"})," and will return a ",(0,a.jsx)(n.code,{children:"CompletableFuture<Astronaut>"})," so we can consider this path exhausted."]}),"\n"]}),"\n"]}),"\n"]}),(0,a.jsx)(n.p,{children:(0,a.jsx)(n.em,{children:(0,a.jsx)(n.strong,{children:"for Q2"})})}),(0,a.jsxs)(n.ol,{children:["\n",(0,a.jsxs)(n.li,{children:["Start an ",(0,a.jsx)(n.code,{children:"ExecutionStrategy"})," for the ",(0,a.jsx)(n.code,{children:"root"})," field of the query, to concurrently resolve ",(0,a.jsx)(n.code,{children:"nasa"})," field.","\n",(0,a.jsxs)(n.ul,{children:["\n",(0,a.jsxs)(n.li,{children:[(0,a.jsx)(n.code,{children:"nasa"})," ",(0,a.jsx)(n.strong,{children:"DataFetcher"})," will synchronously return a ",(0,a.jsx)(n.code,{children:"Nasa"})," object, so we can descend more that path."]}),"\n"]}),"\n"]}),"\n",(0,a.jsxs)(n.li,{children:["Start an ",(0,a.jsx)(n.code,{children:"ExecutionStrategy"})," for the ",(0,a.jsx)(n.code,{children:"nasa"})," field of the ",(0,a.jsx)(n.code,{children:"root"})," field of the query to concurrently resolve ",(0,a.jsx)(n.code,{children:"astronaut"}),", ",(0,a.jsx)(n.code,{children:"address"})," and ",(0,a.jsx)(n.code,{children:"phoneNumber"}),".","\n",(0,a.jsxs)(n.ul,{children:["\n",(0,a.jsxs)(n.li,{children:[(0,a.jsx)(n.code,{children:"astronaut"})," ",(0,a.jsx)(n.strong,{children:"DataFetcher"}),"  will invoke the ",(0,a.jsx)(n.code,{children:"AstronautDataLoader"})," and will return a ",(0,a.jsx)(n.code,{children:"CompletableFuture<Astronaut>"})," so we can consider this path exhausted"]}),"\n",(0,a.jsxs)(n.li,{children:[(0,a.jsx)(n.code,{children:"address"})," ",(0,a.jsx)(n.strong,{children:"DataFetcher"})," will synchronously return an ",(0,a.jsx)(n.code,{children:"Address"})," object, so we can descend more that path."]}),"\n",(0,a.jsxs)(n.li,{children:[(0,a.jsx)(n.code,{children:"phoneNumber"})," ",(0,a.jsx)(n.strong,{children:"DataFetcher"})," will return a scalar, so we can consider this path exhausted."]}),"\n"]}),"\n"]}),"\n",(0,a.jsxs)(n.li,{children:["Start an ",(0,a.jsx)(n.code,{children:"ExecutionStrategy"})," for the ",(0,a.jsx)(n.code,{children:"address"})," field of the ",(0,a.jsx)(n.code,{children:"nasa"})," field to concurrently resolve ",(0,a.jsx)(n.code,{children:"street"})," and ",(0,a.jsx)(n.code,{children:"zipCode"}),".","\n",(0,a.jsxs)(n.ul,{children:["\n",(0,a.jsxs)(n.li,{children:[(0,a.jsx)(n.code,{children:"street"})," ",(0,a.jsx)(n.strong,{children:"DataFetcher"})," will return a scalar, so we can consider this path exhausted."]}),"\n",(0,a.jsxs)(n.li,{children:[(0,a.jsx)(n.code,{children:"zipCode"})," ",(0,a.jsx)(n.strong,{children:"DataFetcher"})," will return a scalar, so we can consider this path exhausted."]}),"\n"]}),"\n"]}),"\n"]}),(0,a.jsx)(n.p,{children:(0,a.jsxs)(n.strong,{children:["At this point we can consider the synchronous execution exhausted and the ",(0,a.jsx)(n.code,{children:"AstronautDataLoader"})," has 2 keys to be dispatched,\nif we proceed dispatching all data loaders the execution will continue as following:"]})}),(0,a.jsx)(n.p,{children:(0,a.jsx)(n.em,{children:(0,a.jsx)(n.strong,{children:"for Q1"})})}),(0,a.jsxs)(n.ol,{children:["\n",(0,a.jsxs)(n.li,{children:["Start and ",(0,a.jsx)(n.code,{children:"ExecutionStrategy"})," for the ",(0,a.jsx)(n.code,{children:"astronaut"})," field of the ",(0,a.jsx)(n.code,{children:"root"})," field of the query to concurrently resolve ",(0,a.jsx)(n.code,{children:"id"}),", ",(0,a.jsx)(n.code,{children:"name"})," and ",(0,a.jsx)(n.code,{children:"mission"})," fields.","\n",(0,a.jsxs)(n.ul,{children:["\n",(0,a.jsxs)(n.li,{children:[(0,a.jsx)(n.code,{children:"id"})," ",(0,a.jsx)(n.strong,{children:"DataFetcher"})," will return a scalar, so we can consider this path exhausted."]}),"\n",(0,a.jsxs)(n.li,{children:[(0,a.jsx)(n.code,{children:"name"})," ",(0,a.jsx)(n.strong,{children:"DataFetcher"})," will return a scalar, so we can consider this path exhausted."]}),"\n",(0,a.jsxs)(n.li,{children:[(0,a.jsx)(n.code,{children:"missions"})," ",(0,a.jsx)(n.strong,{children:"DataFetcher"})," will invoke the ",(0,a.jsx)(n.code,{children:"MissionsByAstronautDataLoader"})," and will return a ",(0,a.jsx)(n.code,{children:"CompletableFuture<List<Mission>>"})," so we can consider this path exhausted."]}),"\n"]}),"\n"]}),"\n"]}),(0,a.jsx)(n.p,{children:(0,a.jsx)(n.em,{children:(0,a.jsx)(n.strong,{children:"for Q2"})})}),(0,a.jsxs)(n.ol,{children:["\n",(0,a.jsxs)(n.li,{children:["Start and ",(0,a.jsx)(n.code,{children:"ExecutionStrategy"})," for the ",(0,a.jsx)(n.code,{children:"astronaut"})," field of the ",(0,a.jsx)(n.code,{children:"nasa"})," field of the query to concurrently resolve ",(0,a.jsx)(n.code,{children:"id"}),", ",(0,a.jsx)(n.code,{children:"name"})," and ",(0,a.jsx)(n.code,{children:"mission"})," fields.","\n",(0,a.jsxs)(n.ul,{children:["\n",(0,a.jsxs)(n.li,{children:[(0,a.jsx)(n.code,{children:"id"})," ",(0,a.jsx)(n.strong,{children:"DataFetcher"})," will return a scalar, so we can consider this path exhausted."]}),"\n",(0,a.jsxs)(n.li,{children:[(0,a.jsx)(n.code,{children:"name"})," ",(0,a.jsx)(n.strong,{children:"DataFetcher"})," will return a scalar, so we can consider this path exhausted."]}),"\n",(0,a.jsxs)(n.li,{children:[(0,a.jsx)(n.code,{children:"missions"})," ",(0,a.jsx)(n.strong,{children:"DataFetcher"})," will invoke the ",(0,a.jsx)(n.code,{children:"MissionsByAstronautDataLoader"})," and will return a ",(0,a.jsx)(n.code,{children:"CompletableFuture<List<Mission>>"})," so we can consider this path exhausted."]}),"\n"]}),"\n"]}),"\n"]}),(0,a.jsx)(n.p,{children:(0,a.jsxs)(n.strong,{children:["At this point we can consider the synchronous execution exhausted and the ",(0,a.jsx)(n.code,{children:"MissionsByAstronautDataLoader"})," has 2 keys to be dispatched,\nif we proceed dispatching all data loaders the execution will continue to just resolve scalar fields."]})})]})]}),"\n",(0,a.jsx)(n.h3,{id:"usage-1",children:"Usage"}),"\n",(0,a.jsxs)(n.p,{children:["In order to enable batching by synchronous execution exhaustion, you need to configure your GraphQL instance with the ",(0,a.jsx)(n.code,{children:"DataLoaderSyncExecutionExhaustedInstrumentation"}),"."]}),"\n",(0,a.jsx)(n.pre,{children:(0,a.jsx)(n.code,{className:"language-kotlin",children:"val graphQL = GraphQL.Builder()\n    .doNotAddDefaultInstrumentations()\n    .instrumentation(DataLoaderSyncExecutionExhaustedInstrumentation())\n    // configure schema, type wiring, etc.\n    .build()\n"})}),"\n",(0,a.jsx)(n.p,{children:"This data loader instrumentation relies on a global state object that needs be stored in the GraphQLContext map"}),"\n",(0,a.jsx)(n.pre,{children:(0,a.jsx)(n.code,{className:"language-kotlin",children:"val graphQLContext = mapOf(\n    SyncExecutionExhaustedState::class to SyncExecutionExhaustedState(\n        queries.size,\n        kotlinDataLoaderRegistry\n    )\n)\n"})}),"\n",(0,a.jsxs)(n.admonition,{type:"info",children:[(0,a.jsxs)(n.p,{children:[(0,a.jsx)(n.code,{children:"graphql-kotlin-spring-server"})," provides convenient integration of batch loader functionality through simple configuration.\nBatching by synchronous execution exhaustion can be enabled by configuring following properties:"]}),(0,a.jsx)(n.pre,{children:(0,a.jsx)(n.code,{className:"language-yaml",children:"graphql:\n  batching:\n   enabled: true\n   strategy: SYNC_EXHAUSTION\n"})})]}),"\n",(0,a.jsx)(n.h2,{id:"multiple-data-loaders-per-field-data-fetcher",children:"Multiple data loaders per field data fetcher"}),"\n",(0,a.jsx)(n.p,{children:"There are some cases when a GraphQL Schema doesn't match the data source schema, a field can require data from multiple\nsources to be fetched and you will still want to do batching with data loaders."}),"\n",(0,a.jsx)(n.h3,{id:"dispatchifneeded",children:"DispatchIfNeeded"}),"\n",(0,a.jsxs)(n.p,{children:[(0,a.jsx)(n.code,{children:"graphql-kotlin-dataloader-instrumentation"})," includes a helpful extension function of the ",(0,a.jsx)(n.code,{children:"CompletableFuture"})," class\nso that you can easily instruct the ",(0,a.jsx)(n.a,{href:"./data-loader-instrumentation#dispatching-by-level",children:"previously selected data loader instrumentation"}),"\nthat you want to apply batching and deduplication to a chained ",(0,a.jsx)(n.code,{children:"DataLoader"})," in your ",(0,a.jsx)(n.code,{children:"DataFetcher"})," (resolver)."]}),"\n",(0,a.jsx)(n.h3,{id:"example-2",children:"Example"}),"\n",(0,a.jsx)(n.pre,{children:(0,a.jsx)(n.code,{className:"language-graphql",children:"type Query {\n    astronaut(id: ID!): Astronaut\n}\n\n# In the data source, let's say a database,\n# an `Astronaut` can have multiple `Mission`s and a `Mission` can have multiple `Planet`s.\ntype Astronaut {\n    id: ID!\n    name: String!\n    # The schema exposes the `Astronaut` `Planet`s, without traversing his `Mission`s.\n    planets: [Planet!]!\n}\n\ntype Planet {\n    id: ID!\n    name: String!\n}\n"})}),"\n",(0,a.jsxs)(n.p,{children:["The  ",(0,a.jsx)(n.code,{children:"Astronaut"})," ",(0,a.jsx)(n.code,{children:"planets"})," data fetcher (resolver) will contain the logic to chain two data loaders,\nfirst collect missions by astronaut, and then, planets by mission."]}),"\n",(0,a.jsx)(n.p,{children:(0,a.jsx)(n.strong,{children:"DataLoaders"})}),"\n",(0,a.jsxs)(n.p,{children:["For this specific example we would need 2 ",(0,a.jsx)(n.code,{children:"DataLoader"}),"s"]}),"\n",(0,a.jsxs)(n.ol,{children:["\n",(0,a.jsxs)(n.li,{children:[(0,a.jsx)(n.strong,{children:"MissionsByAstronaut:"})," to retrieve ",(0,a.jsx)(n.code,{children:"Mission"}),"s by a given ",(0,a.jsx)(n.code,{children:"Astronaut"}),"."]}),"\n",(0,a.jsxs)(n.li,{children:[(0,a.jsx)(n.strong,{children:"PlanetsByMission:"})," to retrieve ",(0,a.jsx)(n.code,{children:"Planet"}),"s by a given ",(0,a.jsx)(n.code,{children:"Mission"}),"."]}),"\n"]}),"\n",(0,a.jsx)(n.p,{children:(0,a.jsx)(n.strong,{children:"Fetching logic"})}),"\n",(0,a.jsx)(n.pre,{children:(0,a.jsx)(n.code,{className:"language-kotlin",children:'class Astronaut {\n    fun getPlanets(\n        astronautId: Int,\n        environment: DataFetchingEnvironment\n    ): CompletableFuture<List<Planet>> {\n        val missionsByAstronautDataLoader = environment.getDataLoader("MissionsByAstronautDataLoader")\n        val planetsByMissionDataLoader = environment.getDataLoader("PlanetsByMissionDataLoader")\n        return missionsByAstronautDataLoader\n            .load(astronautId)\n            // chain data loader\n            .thenCompose { missions ->\n                planetsByMissionDataLoader\n                    .loadMany(missions.map { mission -> mission.id })\n                    // extension function to schedule a dispatch of registry if needed\n                    .dispatchIfNeeded(environment)\n            }\n}\n'})})]})}function x(e={}){const{wrapper:n}={...(0,s.R)(),...e.components};return n?(0,a.jsx)(n,{...e,children:(0,a.jsx)(u,{...e})}):u(e)}},19365:(e,n,t)=>{t.d(n,{A:()=>i});t(96540);var a=t(34164);const s={tabItem:"tabItem_Ymn6"};var r=t(74848);function i(e){var n=e.children,t=e.hidden,i=e.className;return(0,r.jsx)("div",{role:"tabpanel",className:(0,a.A)(s.tabItem,i),hidden:t,children:n})}},11470:(e,n,t)=>{t.d(n,{A:()=>w});var a=t(96540),s=t(34164),r=t(23104),i=t(56347),o=t(205),l=t(57485),d=t(31682),c=t(70679);function h(e){var n,t;return null!=(n=null==(t=a.Children.toArray(e).filter((function(e){return"\n"!==e})).map((function(e){if(!e||(0,a.isValidElement)(e)&&((n=e.props)&&"object"==typeof n&&"value"in n))return e;var n;throw new Error("Docusaurus error: Bad <Tabs> child <"+("string"==typeof e.type?e.type:e.type.name)+'>: all children of the <Tabs> component should be <TabItem>, and every <TabItem> should have a unique "value" prop.')})))?void 0:t.filter(Boolean))?n:[]}function u(e){var n=e.values,t=e.children;return(0,a.useMemo)((function(){var e=null!=n?n:function(e){return h(e).map((function(e){var n=e.props;return{value:n.value,label:n.label,attributes:n.attributes,default:n.default}}))}(t);return function(e){var n=(0,d.XI)(e,(function(e,n){return e.value===n.value}));if(n.length>0)throw new Error('Docusaurus error: Duplicate values "'+n.map((function(e){return e.value})).join(", ")+'" found in <Tabs>. Every value needs to be unique.')}(e),e}),[n,t])}function x(e){var n=e.value;return e.tabValues.some((function(e){return e.value===n}))}function p(e){var n=e.queryString,t=void 0!==n&&n,s=e.groupId,r=(0,i.W6)(),o=function(e){var n=e.queryString,t=void 0!==n&&n,a=e.groupId;if("string"==typeof t)return t;if(!1===t)return null;if(!0===t&&!a)throw new Error('Docusaurus error: The <Tabs> component groupId prop is required if queryString=true, because this value is used as the search param name. You can also provide an explicit value such as queryString="my-search-param".');return null!=a?a:null}({queryString:t,groupId:s});return[(0,l.aZ)(o),(0,a.useCallback)((function(e){if(o){var n=new URLSearchParams(r.location.search);n.set(o,e),r.replace(Object.assign({},r.location,{search:n.toString()}))}}),[o,r])]}function m(e){var n,t,s,r,i=e.defaultValue,l=e.queryString,d=void 0!==l&&l,h=e.groupId,m=u(e),j=(0,a.useState)((function(){return function(e){var n,t=e.defaultValue,a=e.tabValues;if(0===a.length)throw new Error("Docusaurus error: the <Tabs> component requires at least one <TabItem> children component");if(t){if(!x({value:t,tabValues:a}))throw new Error('Docusaurus error: The <Tabs> has a defaultValue "'+t+'" but none of its children has the corresponding value. Available values are: '+a.map((function(e){return e.value})).join(", ")+". If you intend to show no default tab, use defaultValue={null} instead.");return t}var s=null!=(n=a.find((function(e){return e.default})))?n:a[0];if(!s)throw new Error("Unexpected error: 0 tabValues");return s.value}({defaultValue:i,tabValues:m})})),g=j[0],f=j[1],v=p({queryString:d,groupId:h}),y=v[0],b=v[1],w=(n=function(e){return e?"docusaurus.tab."+e:null}({groupId:h}.groupId),t=(0,c.Dv)(n),s=t[0],r=t[1],[s,(0,a.useCallback)((function(e){n&&r.set(e)}),[n,r])]),D=w[0],L=w[1],q=function(){var e=null!=y?y:D;return x({value:e,tabValues:m})?e:null}();return(0,o.A)((function(){q&&f(q)}),[q]),{selectedValue:g,selectValue:(0,a.useCallback)((function(e){if(!x({value:e,tabValues:m}))throw new Error("Can't select invalid tab value="+e);f(e),b(e),L(e)}),[b,L,m]),tabValues:m}}var j=t(92303);const g={tabList:"tabList__CuJ",tabItem:"tabItem_LNqP"};var f=t(74848);function v(e){var n=e.className,t=e.block,a=e.selectedValue,i=e.selectValue,o=e.tabValues,l=[],d=(0,r.a_)().blockElementScrollPositionUntilNextRender,c=function(e){var n=e.currentTarget,t=l.indexOf(n),s=o[t].value;s!==a&&(d(n),i(s))},h=function(e){var n,t=null;switch(e.key){case"Enter":c(e);break;case"ArrowRight":var a,s=l.indexOf(e.currentTarget)+1;t=null!=(a=l[s])?a:l[0];break;case"ArrowLeft":var r,i=l.indexOf(e.currentTarget)-1;t=null!=(r=l[i])?r:l[l.length-1]}null==(n=t)||n.focus()};return(0,f.jsx)("ul",{role:"tablist","aria-orientation":"horizontal",className:(0,s.A)("tabs",{"tabs--block":t},n),children:o.map((function(e){var n=e.value,t=e.label,r=e.attributes;return(0,f.jsx)("li",Object.assign({role:"tab",tabIndex:a===n?0:-1,"aria-selected":a===n,ref:function(e){return l.push(e)},onKeyDown:h,onClick:c},r,{className:(0,s.A)("tabs__item",g.tabItem,null==r?void 0:r.className,{"tabs__item--active":a===n}),children:null!=t?t:n}),n)}))})}function y(e){var n=e.lazy,t=e.children,r=e.selectedValue,i=(Array.isArray(t)?t:[t]).filter(Boolean);if(n){var o=i.find((function(e){return e.props.value===r}));return o?(0,a.cloneElement)(o,{className:(0,s.A)("margin-top--md",o.props.className)}):null}return(0,f.jsx)("div",{className:"margin-top--md",children:i.map((function(e,n){return(0,a.cloneElement)(e,{key:n,hidden:e.props.value!==r})}))})}function b(e){var n=m(e);return(0,f.jsxs)("div",{className:(0,s.A)("tabs-container",g.tabList),children:[(0,f.jsx)(v,Object.assign({},n,e)),(0,f.jsx)(y,Object.assign({},n,e))]})}function w(e){var n=(0,j.A)();return(0,f.jsx)(b,Object.assign({},e,{children:h(e.children)}),String(n))}},66663:(e,n,t)=>{t.d(n,{A:()=>a});const a=t.p+"assets/images/data-loader-level-dispatched-instrumentation-5aa0dcea159d7f614e3f9894936ce2a6.png"},88460:(e,n,t)=>{t.d(n,{A:()=>a});const a=t.p+"assets/images/data-loader-level-sync-execution-exhausted-instrumentation-716af35282dac7cd02bf2d3541752dbf.png"},28453:(e,n,t)=>{t.d(n,{R:()=>i,x:()=>o});var a=t(96540);const s={},r=a.createContext(s);function i(e){const n=a.useContext(r);return a.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function o(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(s):e.components||s:i(e.components),a.createElement(r.Provider,{value:n},e.children)}}}]);