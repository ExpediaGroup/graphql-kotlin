"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[8570],{11037:(e,r,n)=>{n.r(r),n.d(r,{assets:()=>c,contentTitle:()=>o,default:()=>p,frontMatter:()=>l,metadata:()=>d,toc:()=>h});var t=n(74848),i=n(28453),s=n(11470),a=n(19365);const l={id:"gradle-plugin-tasks",title:"Gradle Plugin Tasks",sidebar_label:"Tasks"},o=void 0,d={id:"plugins/gradle-plugin-tasks",title:"Gradle Plugin Tasks",description:"GraphQL Kotlin Gradle Plugin provides functionality to generate a lightweight GraphQL HTTP client and generate GraphQL",source:"@site/versioned_docs/version-4.x.x/plugins/gradle-plugin-tasks.mdx",sourceDirName:"plugins",slug:"/plugins/gradle-plugin-tasks",permalink:"/graphql-kotlin/docs/4.x.x/plugins/gradle-plugin-tasks",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-4.x.x/plugins/gradle-plugin-tasks.mdx",tags:[],version:"4.x.x",lastUpdatedBy:"Curtis Cook",lastUpdatedAt:1712948770,formattedLastUpdatedAt:"Apr 12, 2024",frontMatter:{id:"gradle-plugin-tasks",title:"Gradle Plugin Tasks",sidebar_label:"Tasks"},sidebar:"docs",previous:{title:"Client Serialization",permalink:"/graphql-kotlin/docs/4.x.x/client/client-serialization"},next:{title:"Usage",permalink:"/graphql-kotlin/docs/4.x.x/plugins/gradle-plugin-usage"}},c={},h=[{value:"Usage",id:"usage",level:2},{value:"Extension",id:"extension",level:2},{value:"Tasks",id:"tasks",level:2},{value:"graphqlDownloadSDL",id:"graphqldownloadsdl",level:3},{value:"graphqlGenerateClient",id:"graphqlgenerateclient",level:3},{value:"graphqlGenerateSDL",id:"graphqlgeneratesdl",level:3},{value:"graphqlGenerateTestClient",id:"graphqlgeneratetestclient",level:3},{value:"graphqlIntrospectSchema",id:"graphqlintrospectschema",level:3}];function u(e){const r={a:"a",admonition:"admonition",code:"code",h2:"h2",h3:"h3",p:"p",pre:"pre",strong:"strong",table:"table",tbody:"tbody",td:"td",th:"th",thead:"thead",tr:"tr",...(0,i.R)(),...e.components};return(0,t.jsxs)(t.Fragment,{children:[(0,t.jsx)(r.p,{children:"GraphQL Kotlin Gradle Plugin provides functionality to generate a lightweight GraphQL HTTP client and generate GraphQL\nschema directly from your source code."}),"\n",(0,t.jsx)(r.admonition,{type:"info",children:(0,t.jsx)(r.p,{children:"This plugin is dependent on Kotlin compiler plugin as it generates Kotlin source code that needs to be compiled."})}),"\n",(0,t.jsx)(r.h2,{id:"usage",children:"Usage"}),"\n",(0,t.jsxs)(r.p,{children:[(0,t.jsx)(r.code,{children:"graphql-kotlin-gradle-plugin"})," is published on Gradle ",(0,t.jsx)(r.a,{href:"https://plugins.gradle.org/plugin/com.expediagroup.graphql",children:"Plugin Portal"}),".\nIn order to execute any of the provided tasks you need to first apply the plugin on your project."]}),"\n",(0,t.jsxs)(s.A,{defaultValue:"kotlin",values:[{label:"Kotlin",value:"kotlin"},{label:"Groovy",value:"groovy"}],children:[(0,t.jsxs)(a.A,{value:"kotlin",children:[(0,t.jsx)(r.p,{children:"Using plugins DSL syntax"}),(0,t.jsx)(r.pre,{children:(0,t.jsx)(r.code,{className:"language-kotlin",children:'plugins {\n    id("com.expediagroup.graphql") version $graphQLKotlinVersion\n}\n'})}),(0,t.jsx)(r.p,{children:"Or by using legacy plugin application"}),(0,t.jsx)(r.pre,{children:(0,t.jsx)(r.code,{className:"language-kotlin",children:'buildscript {\n  repositories {\n    maven {\n      url = uri("https://plugins.gradle.org/m2/")\n    }\n  }\n  dependencies {\n    classpath("com.expediagroup:graphql-kotlin-gradle-plugin:$graphQLKotlinVersion")\n  }\n}\n\napply(plugin = "com.expediagroup.graphql")\n'})})]}),(0,t.jsxs)(a.A,{value:"groovy",children:[(0,t.jsx)(r.p,{children:"Using plugins DSL syntax"}),(0,t.jsx)(r.pre,{children:(0,t.jsx)(r.code,{className:"language-groovy",children:"// build.gradle\nplugins {\n    id 'com.expediagroup.graphql' version $graphQLKotlinVersion\n}\n"})}),(0,t.jsx)(r.p,{children:"Or by using legacy plugin application"}),(0,t.jsx)(r.pre,{children:(0,t.jsx)(r.code,{className:"language-groovy",children:'// build.gradle\nbuildscript {\n  repositories {\n    maven {\n      url "https://plugins.gradle.org/m2/"\n    }\n  }\n  dependencies {\n    classpath "com.expediagroup:graphql-kotlin-gradle-plugin:$graphQLKotlinVersion"\n  }\n}\n\napply plugin: "com.expediagroup.graphql"\n'})})]})]}),"\n",(0,t.jsx)(r.h2,{id:"extension",children:"Extension"}),"\n",(0,t.jsxs)(r.p,{children:["GraphQL Kotlin Gradle Plugin uses an extension on the project named ",(0,t.jsx)(r.code,{children:"graphql"})," of type\n",(0,t.jsx)(r.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/master/plugins/graphql-kotlin-gradle-plugin/src/main/kotlin/com/expediagroup/graphql/plugin/gradle/GraphQLPluginExtension.kt",children:"GraphQLPluginExtension"}),".\nThis extension can be used to configure global options instead of explicitly configuring individual tasks. Once extension\nis configured, it will automatically download SDL/run introspection to generate GraphQL schema and subsequently generate\nall GraphQL clients. GraphQL Extension should be used by default, except for cases where you need to only run individual\ntasks."]}),"\n",(0,t.jsxs)(s.A,{defaultValue:"kotlin",values:[{label:"Kotlin",value:"kotlin"},{label:"Groovy",value:"groovy"}],children:[(0,t.jsx)(a.A,{value:"kotlin",children:(0,t.jsx)(r.pre,{children:(0,t.jsx)(r.code,{className:"language-kotlin",children:'// build.gradle.kts\nimport com.expediagroup.graphql.plugin.gradle.config.GraphQLScalar\nimport com.expediagroup.graphql.plugin.gradle.config.GraphQLSerializer\nimport com.expediagroup.graphql.plugin.gradle.graphql\n\ngraphql {\n  client {\n    // Boolean flag indicating whether or not selection of deprecated fields is allowed.\n    allowDeprecatedFields = false\n    // List of custom GraphQL scalar to converter mapping containing information about corresponding Java type and converter that should be used to serialize/deserialize values.\n    customScalars = listOf(GraphQLScalar("UUID", "java.util.UUID", "com.example.UUIDScalarConverter"))\n    // GraphQL server endpoint that will be used to for running introspection queries. Alternatively you can download schema directly from `sdlEndpoint`.\n    endpoint = "http://localhost:8080/graphql"\n    // Optional HTTP headers to be specified on an introspection query or SDL request.\n    headers = mapOf("X-Custom-Header" to "Custom-Header-Value")\n    // Target package name to be used for generated classes.\n    packageName = "com.example.generated"\n    // Custom directory containing query files, defaults to src/main/resources\n    queryFileDirectory = "${project.projectDir}/src/main/resources/queries"\n    // Optional list of query files to be processed, takes precedence over queryFileDirectory\n    queryFiles = listOf(file("${project.projectDir}/src/main/resources/queries/MyQuery.graphql"))\n    // JSON serializer that will be used to generate the data classes.\n    serializer = GraphQLSerializer.JACKSON\n    // GraphQL server SDL endpoint that will be used to download schema. Alternatively you can run introspection query against `endpoint`.\n    sdlEndpoint = "http://localhost:8080/sdl"\n    // Timeout configuration for introspection query/downloading SDL\n    timeout {\n        // Connect timeout in milliseconds\n        connect = 5_000\n        // Read timeout in milliseconds\n        read = 15_000\n    }\n  }\n  schema {\n    // List of supported packages that can contain GraphQL schema type definitions\n    packages = listOf("com.example")\n  }\n}\n'})})}),(0,t.jsx)(a.A,{value:"groovy",children:(0,t.jsx)(r.pre,{children:(0,t.jsx)(r.code,{className:"language-groovy",children:'// build.gradle\nimport com.expediagroup.graphql.plugin.gradle.config.GraphQLScalar\nimport com.expediagroup.graphql.plugin.gradle.config.GraphQLSerializer\n\ngraphql {\n    client {\n        // Boolean flag indicating whether or not selection of deprecated fields is allowed.\n        allowDeprecatedFields = false\n        // List of custom GraphQL scalar to converter mapping containing information about corresponding Java type and converter that should be used to serialize/deserialize values.\n        customScalars = [new GraphQLScalar("UUID", "java.util.UUID", "com.example.UUIDScalarConverter")]\n        // GraphQL server endpoint that will be used to for running introspection queries. Alternatively you can download schema directly from `sdlEndpoint`.\n        endpoint = "http://localhost:8080/graphql"\n        // Optional HTTP headers to be specified on an introspection query or SDL request.\n        headers = ["X-Custom-Header" : "My-Custom-Header-Value"]\n        // Target package name to be used for generated classes.\n        packageName = "com.example.generated"\n        // Custom directory containing query files, defaults to src/main/resources\n        queryFileDirectory = "${project.projectDir}/src/main/resources/queries"\n        // Optional list of query files to be processed, takes precedence over queryFileDirectory\n        queryFiles = [file("${project.projectDir}/src/main/resources/queries/MyQuery.graphql")]\n        // JSON serializer that will be used to generate the data classes.\n        serializer = GraphQLSerializer.JACKSON\n        // GraphQL server SDL endpoint that will be used to download schema. Alternatively you can run introspection query against `endpoint`.\n        sdlEndpoint = "http://localhost:8080/sdl"\n        // Timeout configuration for introspection query/downloading SDL\n        timeout { t ->\n            // Connect timeout in milliseconds\n            t.connect = 5000\n            t.read = 15000\n        }\n    }\n    schema {\n        packages = ["com.example"]\n    }\n}\n'})})})]}),"\n",(0,t.jsx)(r.h2,{id:"tasks",children:"Tasks"}),"\n",(0,t.jsxs)(r.p,{children:["By default, ",(0,t.jsx)(r.code,{children:"graphql-kotlin-gradle-plugin"})," lazily registers all its tasks which means that while they are known to the build,\nthey are not created nor executed until something in the build needs the instantiated object (e.g. adding explicit dependency\non those tasks or explicitly creating them). ",(0,t.jsxs)(r.strong,{children:["Configuring plugin through the ",(0,t.jsx)(r.code,{children:"graphql"})," extension will automatically create\nall the corresponding tasks"]}),"."]}),"\n",(0,t.jsxs)(r.p,{children:["All ",(0,t.jsx)(r.code,{children:"graphql-kotlin-gradle-plugin"})," tasks are grouped together under ",(0,t.jsx)(r.code,{children:"GraphQL"})," task group and their names are prefixed with\n",(0,t.jsx)(r.code,{children:"graphql"}),". You can find detailed information about GraphQL kotlin tasks by running Gradle ",(0,t.jsx)(r.code,{children:"help --task <taskName>"})," task."]}),"\n",(0,t.jsx)(r.pre,{children:(0,t.jsx)(r.code,{className:"language-shell",children:"$ gradle --tasks\n\nGraphQL tasks\n-------------\ngraphqlDownloadSDL - Download schema in SDL format from target endpoint.\ngraphqlGenerateClient - Generate HTTP client from the specified GraphQL queries.\ngraphqlGenerateSDL - Generate GraphQL schema in SDL format.\ngraphqlGenerateTestClient - Generate HTTP test client from the specified GraphQL queries.\ngraphqlIntrospectSchema - Run introspection query against target GraphQL endpoint and save schema locally.--&gt;\n"})}),"\n",(0,t.jsx)(r.h3,{id:"graphqldownloadsdl",children:"graphqlDownloadSDL"}),"\n",(0,t.jsxs)(r.p,{children:["Task that attempts to download GraphQL schema in SDL format from the specified ",(0,t.jsx)(r.code,{children:"endpoint"})," and saves the underlying\nschema file as ",(0,t.jsx)(r.code,{children:"schema.graphql"})," under build directory. In general, this task provides limited functionality by itself\nand could be used as an alternative to ",(0,t.jsx)(r.code,{children:"graphqlIntrospectSchema"})," to generate input for the subsequent\n",(0,t.jsx)(r.code,{children:"graphqlGenerateClient"})," task."]}),"\n",(0,t.jsx)(r.p,{children:(0,t.jsx)(r.strong,{children:"Properties"})}),"\n",(0,t.jsxs)(r.table,{children:[(0,t.jsx)(r.thead,{children:(0,t.jsxs)(r.tr,{children:[(0,t.jsx)(r.th,{children:"Property"}),(0,t.jsx)(r.th,{children:"Type"}),(0,t.jsx)(r.th,{children:"Required"}),(0,t.jsx)(r.th,{children:"Description"})]})}),(0,t.jsxs)(r.tbody,{children:[(0,t.jsxs)(r.tr,{children:[(0,t.jsx)(r.td,{children:(0,t.jsx)(r.code,{children:"endpoint"})}),(0,t.jsx)(r.td,{children:"String"}),(0,t.jsx)(r.td,{children:"yes"}),(0,t.jsxs)(r.td,{children:["Target GraphQL server SDL endpoint that will be used to download schema.",(0,t.jsx)("br",{}),(0,t.jsx)(r.strong,{children:"Command line property is"}),": ",(0,t.jsx)(r.code,{children:"endpoint"}),"."]})]}),(0,t.jsxs)(r.tr,{children:[(0,t.jsx)(r.td,{children:(0,t.jsx)(r.code,{children:"headers"})}),(0,t.jsx)(r.td,{children:(0,t.jsx)(r.code,{children:"Map<String, Any>"})}),(0,t.jsx)(r.td,{}),(0,t.jsx)(r.td,{children:"Optional HTTP headers to be specified on a SDL request."})]}),(0,t.jsxs)(r.tr,{children:[(0,t.jsx)(r.td,{children:(0,t.jsx)(r.code,{children:"outputFile"})}),(0,t.jsx)(r.td,{children:"File"}),(0,t.jsx)(r.td,{}),(0,t.jsxs)(r.td,{children:["Target GraphQL schema file to be generated.",(0,t.jsx)("br",{}),(0,t.jsx)(r.strong,{children:"Default value is:"})," ",(0,t.jsx)(r.code,{children:"${project.buildDir}/schema.graphql"})]})]}),(0,t.jsxs)(r.tr,{children:[(0,t.jsx)(r.td,{children:(0,t.jsx)(r.code,{children:"timeoutConfig"})}),(0,t.jsx)(r.td,{children:"TimeoutConfig"}),(0,t.jsx)(r.td,{}),(0,t.jsxs)(r.td,{children:["Timeout configuration (in milliseconds) to download schema from SDL endpoint before we cancel the request.",(0,t.jsx)("br",{}),(0,t.jsx)(r.strong,{children:"Default value are:"}),(0,t.jsx)("br",{}),"connect timeout = 5_000",(0,t.jsx)("br",{}),"read timeout = 15_000.",(0,t.jsx)("br",{})]})]})]})]}),"\n",(0,t.jsx)(r.h3,{id:"graphqlgenerateclient",children:"graphqlGenerateClient"}),"\n",(0,t.jsxs)(r.p,{children:["Task that generates GraphQL Kotlin client and corresponding data classes based on the provided GraphQL queries that are\nevaluated against target Graphql schema. Individual clients with their specific data models are generated for each query\nfile and are placed under specified ",(0,t.jsx)(r.code,{children:"packageName"}),". When this task is added to the project, either through explicit configuration\nor through the ",(0,t.jsx)(r.code,{children:"graphql"})," extension, it will automatically configure itself as a dependency of a ",(0,t.jsx)(r.code,{children:"compileKotlin"})," task and\nresulting generated code will be automatically added to the project main source set."]}),"\n",(0,t.jsx)(r.p,{children:(0,t.jsx)(r.strong,{children:"Properties"})}),"\n",(0,t.jsxs)(r.table,{children:[(0,t.jsx)(r.thead,{children:(0,t.jsxs)(r.tr,{children:[(0,t.jsx)(r.th,{children:"Property"}),(0,t.jsx)(r.th,{children:"Type"}),(0,t.jsx)(r.th,{children:"Required"}),(0,t.jsx)(r.th,{children:"Description"})]})}),(0,t.jsxs)(r.tbody,{children:[(0,t.jsxs)(r.tr,{children:[(0,t.jsx)(r.td,{children:(0,t.jsx)(r.code,{children:"allowDeprecatedFields"})}),(0,t.jsx)(r.td,{children:"Boolean"}),(0,t.jsx)(r.td,{}),(0,t.jsxs)(r.td,{children:["Boolean flag indicating whether selection of deprecated fields is allowed or not.",(0,t.jsx)("br",{}),(0,t.jsx)(r.strong,{children:"Default value is:"})," ",(0,t.jsx)(r.code,{children:"false"}),".",(0,t.jsx)("br",{}),(0,t.jsx)(r.strong,{children:"Command line property is"}),": ",(0,t.jsx)(r.code,{children:"allowDeprecatedFields"}),"."]})]}),(0,t.jsxs)(r.tr,{children:[(0,t.jsx)(r.td,{children:(0,t.jsx)(r.code,{children:"customScalars"})}),(0,t.jsx)(r.td,{children:(0,t.jsx)(r.code,{children:"List<GraphQLScalar>"})}),(0,t.jsx)(r.td,{}),(0,t.jsx)(r.td,{children:"List of custom GraphQL scalar to converter mappings containing information about corresponding Java type and converter that should be used to serialize/deserialize values."})]}),(0,t.jsxs)(r.tr,{children:[(0,t.jsx)(r.td,{children:(0,t.jsx)(r.code,{children:"packageName"})}),(0,t.jsx)(r.td,{children:"String"}),(0,t.jsx)(r.td,{children:"yes"}),(0,t.jsxs)(r.td,{children:["Target package name for generated code.",(0,t.jsx)("br",{}),(0,t.jsx)(r.strong,{children:"Command line property is"}),": ",(0,t.jsx)(r.code,{children:"packageName"}),"."]})]}),(0,t.jsxs)(r.tr,{children:[(0,t.jsx)(r.td,{children:(0,t.jsx)(r.code,{children:"queryFiles"})}),(0,t.jsx)(r.td,{children:"FileCollection"}),(0,t.jsx)(r.td,{}),(0,t.jsxs)(r.td,{children:["List of query files to be processed. Instead of a list of files to be processed you can specify ",(0,t.jsx)(r.code,{children:"queryFileDirectory"})," directory instead. If this property is specified it will take precedence over the corresponding directory property."]})]}),(0,t.jsxs)(r.tr,{children:[(0,t.jsx)(r.td,{children:(0,t.jsx)(r.code,{children:"queryFileDirectory"})}),(0,t.jsx)(r.td,{children:"String"}),(0,t.jsx)(r.td,{}),(0,t.jsxs)(r.td,{children:["Directory file containing GraphQL queries. Instead of specifying a directory you can also specify list of query file by using ",(0,t.jsx)(r.code,{children:"queryFiles"})," property instead.",(0,t.jsx)("br",{}),(0,t.jsx)(r.strong,{children:"Default value is:"})," ",(0,t.jsx)(r.code,{children:"src/main/resources"}),".",(0,t.jsx)("br",{}),(0,t.jsx)(r.strong,{children:"Command line property is"}),": ",(0,t.jsx)(r.code,{children:"queryFileDirectory"}),"."]})]}),(0,t.jsxs)(r.tr,{children:[(0,t.jsx)(r.td,{children:(0,t.jsx)(r.code,{children:"serializer"})}),(0,t.jsx)(r.td,{children:"GraphQLSerializer"}),(0,t.jsx)(r.td,{}),(0,t.jsxs)(r.td,{children:["JSON serializer that will be used to generate the data classes.",(0,t.jsx)("br",{}),(0,t.jsx)(r.strong,{children:"Default value is:"})," ",(0,t.jsx)(r.code,{children:"GraphQLSerializer.JACKSON"}),"."]})]}),(0,t.jsxs)(r.tr,{children:[(0,t.jsx)(r.td,{children:(0,t.jsx)(r.code,{children:"schemaFile"})}),(0,t.jsx)(r.td,{children:"File"}),(0,t.jsxs)(r.td,{children:[(0,t.jsx)(r.code,{children:"schemaFileName"})," or ",(0,t.jsx)(r.code,{children:"schemaFile"})," has to be provided"]}),(0,t.jsx)(r.td,{children:"GraphQL schema file that will be used to generate client code."})]}),(0,t.jsxs)(r.tr,{children:[(0,t.jsx)(r.td,{children:(0,t.jsx)(r.code,{children:"schemaFileName"})}),(0,t.jsx)(r.td,{children:"String"}),(0,t.jsxs)(r.td,{children:[(0,t.jsx)(r.code,{children:"schemaFileName"})," or ",(0,t.jsx)(r.code,{children:"schemaFile"})," has to be provided"]}),(0,t.jsxs)(r.td,{children:["Path to GraphQL schema file that will be used to generate client code.",(0,t.jsx)("br",{}),(0,t.jsx)(r.strong,{children:"Command line property is"}),": ",(0,t.jsx)(r.code,{children:"schemaFileName"}),"."]})]})]})]}),"\n",(0,t.jsxs)(r.p,{children:["By default, this task will generate Jackson compatible data models. See ",(0,t.jsx)(r.a,{href:"/graphql-kotlin/docs/4.x.x/client/client-serialization",children:"client serialization documentation"}),"\nfor details on how to update this process to use ",(0,t.jsx)(r.code,{children:"kotlinx.serialization"})," instead."]}),"\n",(0,t.jsx)(r.h3,{id:"graphqlgeneratesdl",children:"graphqlGenerateSDL"}),"\n",(0,t.jsxs)(r.p,{children:["Task that generates GraphQL schema in SDL format from your source code using reflections. Utilizes ",(0,t.jsx)(r.code,{children:"graphql-kotlin-schema-generator"}),"\nto generate the schema from classes implementing ",(0,t.jsx)(r.code,{children:"graphql-kotlin-server"})," marker ",(0,t.jsx)(r.code,{children:"Query"}),", ",(0,t.jsx)(r.code,{children:"Mutation"})," and ",(0,t.jsx)(r.code,{children:"Subscription"})," interfaces.\nIn order to limit the amount of packages to scan, this task requires users to provide a list of ",(0,t.jsx)(r.code,{children:"packages"})," that can contain\nGraphQL types."]}),"\n",(0,t.jsx)(r.p,{children:(0,t.jsx)(r.strong,{children:"Properties"})}),"\n",(0,t.jsxs)(r.table,{children:[(0,t.jsx)(r.thead,{children:(0,t.jsxs)(r.tr,{children:[(0,t.jsx)(r.th,{children:"Property"}),(0,t.jsx)(r.th,{children:"Type"}),(0,t.jsx)(r.th,{children:"Required"}),(0,t.jsx)(r.th,{children:"Description"})]})}),(0,t.jsxs)(r.tbody,{children:[(0,t.jsxs)(r.tr,{children:[(0,t.jsx)(r.td,{children:(0,t.jsx)(r.code,{children:"packages"})}),(0,t.jsx)(r.td,{children:(0,t.jsx)(r.code,{children:"List<String>"})}),(0,t.jsx)(r.td,{children:"yes"}),(0,t.jsx)(r.td,{children:"List of supported packages that can be scanned to generate SDL."})]}),(0,t.jsxs)(r.tr,{children:[(0,t.jsx)(r.td,{children:(0,t.jsx)(r.code,{children:"schemaFile"})}),(0,t.jsx)(r.td,{children:"File"}),(0,t.jsx)(r.td,{}),(0,t.jsxs)(r.td,{children:["Target GraphQL schema file to be generated.",(0,t.jsx)("br",{}),(0,t.jsx)(r.strong,{children:"Default value is:"})," ",(0,t.jsx)(r.code,{children:"${project.buildDir}/schema.graphql"})]})]})]})]}),"\n",(0,t.jsxs)(r.p,{children:["By default, this task will attempt to generate the schema using ",(0,t.jsx)(r.code,{children:"NoopSchemaGeneratorHooks"}),". If you need to customize your\nschema generation process, you will need to provide your custom instance of ",(0,t.jsx)(r.code,{children:"SchemaGeneratorHooksProvider"})," service provider.\nService provider can be provided as part of your project, included in one of your project dependencies or through explicitly\nprovided artifact to the ",(0,t.jsx)(r.code,{children:"graphqlSDL"})," configuration."]}),"\n",(0,t.jsx)(r.pre,{children:(0,t.jsx)(r.code,{className:"language-kotlin",children:'dependencies {\n    graphqlSDL("com.expediagroup:graphql-kotlin-federated-hooks-provider:$graphQLKotlinVersion")\n}\n'})}),"\n",(0,t.jsx)(r.h3,{id:"graphqlgeneratetestclient",children:"graphqlGenerateTestClient"}),"\n",(0,t.jsxs)(r.p,{children:["Task that generates GraphQL Kotlin test client and corresponding data classes based on the provided GraphQL queries that are\nevaluated against target Graphql schema. Individual test clients with their specific data models are generated for each query\nfile and are placed under specified ",(0,t.jsx)(r.code,{children:"packageName"}),". When this task is added to the project it will automatically configure\nitself as a dependency of a ",(0,t.jsx)(r.code,{children:"compileTestKotlin"})," task and resulting generated code will be automatically added to the project\ntest source set."]}),"\n",(0,t.jsx)(r.p,{children:(0,t.jsx)(r.strong,{children:"Properties"})}),"\n",(0,t.jsxs)(r.table,{children:[(0,t.jsx)(r.thead,{children:(0,t.jsxs)(r.tr,{children:[(0,t.jsx)(r.th,{children:"Property"}),(0,t.jsx)(r.th,{children:"Type"}),(0,t.jsx)(r.th,{children:"Required"}),(0,t.jsx)(r.th,{children:"Description"})]})}),(0,t.jsxs)(r.tbody,{children:[(0,t.jsxs)(r.tr,{children:[(0,t.jsx)(r.td,{children:(0,t.jsx)(r.code,{children:"allowDeprecatedFields"})}),(0,t.jsx)(r.td,{children:"Boolean"}),(0,t.jsx)(r.td,{}),(0,t.jsxs)(r.td,{children:["Boolean flag indicating whether selection of deprecated fields is allowed or not.",(0,t.jsx)("br",{}),(0,t.jsx)(r.strong,{children:"Default value is:"})," ",(0,t.jsx)(r.code,{children:"false"}),".",(0,t.jsx)("br",{}),(0,t.jsx)(r.strong,{children:"Command line property is"}),": ",(0,t.jsx)(r.code,{children:"allowDeprecatedFields"}),"."]})]}),(0,t.jsxs)(r.tr,{children:[(0,t.jsx)(r.td,{children:(0,t.jsx)(r.code,{children:"customScalars"})}),(0,t.jsx)(r.td,{children:(0,t.jsx)(r.code,{children:"List<GraphQLScalar>"})}),(0,t.jsx)(r.td,{}),(0,t.jsx)(r.td,{children:"List of custom GraphQL scalar to converter mappings containing information about corresponding Java type and converter that should be used to serialize/deserialize values."})]}),(0,t.jsxs)(r.tr,{children:[(0,t.jsx)(r.td,{children:(0,t.jsx)(r.code,{children:"packageName"})}),(0,t.jsx)(r.td,{children:"String"}),(0,t.jsx)(r.td,{children:"yes"}),(0,t.jsxs)(r.td,{children:["Target package name for generated code.",(0,t.jsx)("br",{}),(0,t.jsx)(r.strong,{children:"Command line property is"}),": ",(0,t.jsx)(r.code,{children:"packageName"}),"."]})]}),(0,t.jsxs)(r.tr,{children:[(0,t.jsx)(r.td,{children:(0,t.jsx)(r.code,{children:"queryFiles"})}),(0,t.jsx)(r.td,{children:"FileCollection"}),(0,t.jsx)(r.td,{}),(0,t.jsxs)(r.td,{children:["List of query files to be processed. Instead of a list of files to be processed you can specify ",(0,t.jsx)(r.code,{children:"queryFileDirectory"})," directory instead. If this property is specified it will take precedence over the corresponding directory property."]})]}),(0,t.jsxs)(r.tr,{children:[(0,t.jsx)(r.td,{children:(0,t.jsx)(r.code,{children:"queryFileDirectory"})}),(0,t.jsx)(r.td,{children:"String"}),(0,t.jsx)(r.td,{}),(0,t.jsxs)(r.td,{children:["Directory file containing GraphQL queries. Instead of specifying a directory you can also specify list of query file by using ",(0,t.jsx)(r.code,{children:"queryFiles"})," property instead.",(0,t.jsx)("br",{}),(0,t.jsx)(r.strong,{children:"Default value is:"})," ",(0,t.jsx)(r.code,{children:"src/test/resources"}),".",(0,t.jsx)("br",{}),(0,t.jsx)(r.strong,{children:"Command line property is"}),": ",(0,t.jsx)(r.code,{children:"queryFileDirectory"}),"."]})]}),(0,t.jsxs)(r.tr,{children:[(0,t.jsx)(r.td,{children:(0,t.jsx)(r.code,{children:"serializer"})}),(0,t.jsx)(r.td,{children:"GraphQLSerializer"}),(0,t.jsx)(r.td,{}),(0,t.jsxs)(r.td,{children:["JSON serializer that will be used to generate the data classes.",(0,t.jsx)("br",{}),(0,t.jsx)(r.strong,{children:"Default value is:"})," ",(0,t.jsx)(r.code,{children:"GraphQLSerializer.JACKSON"}),"."]})]}),(0,t.jsxs)(r.tr,{children:[(0,t.jsx)(r.td,{children:(0,t.jsx)(r.code,{children:"schemaFile"})}),(0,t.jsx)(r.td,{children:"File"}),(0,t.jsxs)(r.td,{children:[(0,t.jsx)(r.code,{children:"schemaFileName"})," or ",(0,t.jsx)(r.code,{children:"schemaFile"})," has to be provided"]}),(0,t.jsx)(r.td,{children:"GraphQL schema file that will be used to generate client code."})]}),(0,t.jsxs)(r.tr,{children:[(0,t.jsx)(r.td,{children:(0,t.jsx)(r.code,{children:"schemaFileName"})}),(0,t.jsx)(r.td,{children:"String"}),(0,t.jsxs)(r.td,{children:[(0,t.jsx)(r.code,{children:"schemaFileName"})," or ",(0,t.jsx)(r.code,{children:"schemaFile"})," has to be provided"]}),(0,t.jsxs)(r.td,{children:["Path to GraphQL schema file that will be used to generate client code.",(0,t.jsx)("br",{}),(0,t.jsx)(r.strong,{children:"Command line property is"}),": ",(0,t.jsx)(r.code,{children:"schemaFileName"}),"."]})]})]})]}),"\n",(0,t.jsxs)(r.p,{children:["By default, this task will generate Jackson compatible data models. See ",(0,t.jsx)(r.a,{href:"/graphql-kotlin/docs/4.x.x/client/client-serialization",children:"client serialization documentation"}),"\nfor details on how to update this process to use ",(0,t.jsx)(r.code,{children:"kotlinx.serialization"})," instead."]}),"\n",(0,t.jsx)(r.h3,{id:"graphqlintrospectschema",children:"graphqlIntrospectSchema"}),"\n",(0,t.jsxs)(r.p,{children:["Task that executes GraphQL introspection query against specified ",(0,t.jsx)(r.code,{children:"endpoint"})," and saves the underlying schema file as\n",(0,t.jsx)(r.code,{children:"schema.graphql"})," under build directory. In general, this task provides limited functionality by itself and instead\nshould be used to generate input for the subsequent ",(0,t.jsx)(r.code,{children:"graphqlGenerateClient"})," task."]}),"\n",(0,t.jsx)(r.p,{children:(0,t.jsx)(r.strong,{children:"Properties"})}),"\n",(0,t.jsxs)(r.table,{children:[(0,t.jsx)(r.thead,{children:(0,t.jsxs)(r.tr,{children:[(0,t.jsx)(r.th,{children:"Property"}),(0,t.jsx)(r.th,{children:"Type"}),(0,t.jsx)(r.th,{children:"Required"}),(0,t.jsx)(r.th,{children:"Description"})]})}),(0,t.jsxs)(r.tbody,{children:[(0,t.jsxs)(r.tr,{children:[(0,t.jsx)(r.td,{children:(0,t.jsx)(r.code,{children:"endpoint"})}),(0,t.jsx)(r.td,{children:"String"}),(0,t.jsx)(r.td,{children:"yes"}),(0,t.jsxs)(r.td,{children:["Target GraphQL server endpoint that will be used to execute introspection queries.",(0,t.jsx)("br",{}),(0,t.jsx)(r.strong,{children:"Command line property is"}),": ",(0,t.jsx)(r.code,{children:"endpoint"}),"."]})]}),(0,t.jsxs)(r.tr,{children:[(0,t.jsx)(r.td,{children:(0,t.jsx)(r.code,{children:"headers"})}),(0,t.jsx)(r.td,{children:(0,t.jsx)(r.code,{children:"Map<String, Any>"})}),(0,t.jsx)(r.td,{}),(0,t.jsx)(r.td,{children:"Optional HTTP headers to be specified on an introspection query."})]}),(0,t.jsxs)(r.tr,{children:[(0,t.jsx)(r.td,{children:(0,t.jsx)(r.code,{children:"outputFile"})}),(0,t.jsx)(r.td,{children:"File"}),(0,t.jsx)(r.td,{}),(0,t.jsxs)(r.td,{children:["Target GraphQL schema file to be generated.",(0,t.jsx)("br",{}),(0,t.jsx)(r.strong,{children:"Default value is:"})," ",(0,t.jsx)(r.code,{children:"${project.buildDir}/schema.graphql"})]})]}),(0,t.jsxs)(r.tr,{children:[(0,t.jsx)(r.td,{children:(0,t.jsx)(r.code,{children:"timeoutConfig"})}),(0,t.jsx)(r.td,{children:"TimeoutConfig"}),(0,t.jsx)(r.td,{}),(0,t.jsxs)(r.td,{children:["Timeout configuration (in milliseconds) to download schema from SDL endpoint before we cancel the request.",(0,t.jsx)("br",{}),(0,t.jsx)(r.strong,{children:"Default value are:"}),(0,t.jsx)("br",{}),"connect timeout = 5_000",(0,t.jsx)("br",{}),"read timeout = 15_000.",(0,t.jsx)("br",{})]})]})]})]})]})}function p(e={}){const{wrapper:r}={...(0,i.R)(),...e.components};return r?(0,t.jsx)(r,{...e,children:(0,t.jsx)(u,{...e})}):u(e)}},19365:(e,r,n)=>{n.d(r,{A:()=>a});n(96540);var t=n(34164);const i={tabItem:"tabItem_Ymn6"};var s=n(74848);function a(e){var r=e.children,n=e.hidden,a=e.className;return(0,s.jsx)("div",{role:"tabpanel",className:(0,t.A)(i.tabItem,a),hidden:n,children:r})}},11470:(e,r,n)=>{n.d(r,{A:()=>q});var t=n(96540),i=n(34164),s=n(23104),a=n(56347),l=n(205),o=n(57485),d=n(31682),c=n(89466);function h(e){var r,n;return null!=(r=null==(n=t.Children.toArray(e).filter((function(e){return"\n"!==e})).map((function(e){if(!e||(0,t.isValidElement)(e)&&((r=e.props)&&"object"==typeof r&&"value"in r))return e;var r;throw new Error("Docusaurus error: Bad <Tabs> child <"+("string"==typeof e.type?e.type:e.type.name)+'>: all children of the <Tabs> component should be <TabItem>, and every <TabItem> should have a unique "value" prop.')})))?void 0:n.filter(Boolean))?r:[]}function u(e){var r=e.values,n=e.children;return(0,t.useMemo)((function(){var e=null!=r?r:function(e){return h(e).map((function(e){var r=e.props;return{value:r.value,label:r.label,attributes:r.attributes,default:r.default}}))}(n);return function(e){var r=(0,d.X)(e,(function(e,r){return e.value===r.value}));if(r.length>0)throw new Error('Docusaurus error: Duplicate values "'+r.map((function(e){return e.value})).join(", ")+'" found in <Tabs>. Every value needs to be unique.')}(e),e}),[r,n])}function p(e){var r=e.value;return e.tabValues.some((function(e){return e.value===r}))}function x(e){var r=e.queryString,n=void 0!==r&&r,i=e.groupId,s=(0,a.W6)(),l=function(e){var r=e.queryString,n=void 0!==r&&r,t=e.groupId;if("string"==typeof n)return n;if(!1===n)return null;if(!0===n&&!t)throw new Error('Docusaurus error: The <Tabs> component groupId prop is required if queryString=true, because this value is used as the search param name. You can also provide an explicit value such as queryString="my-search-param".');return null!=t?t:null}({queryString:n,groupId:i});return[(0,o.aZ)(l),(0,t.useCallback)((function(e){if(l){var r=new URLSearchParams(s.location.search);r.set(l,e),s.replace(Object.assign({},s.location,{search:r.toString()}))}}),[l,s])]}function g(e){var r,n,i,s,a=e.defaultValue,o=e.queryString,d=void 0!==o&&o,h=e.groupId,g=u(e),j=(0,t.useState)((function(){return function(e){var r,n=e.defaultValue,t=e.tabValues;if(0===t.length)throw new Error("Docusaurus error: the <Tabs> component requires at least one <TabItem> children component");if(n){if(!p({value:n,tabValues:t}))throw new Error('Docusaurus error: The <Tabs> has a defaultValue "'+n+'" but none of its children has the corresponding value. Available values are: '+t.map((function(e){return e.value})).join(", ")+". If you intend to show no default tab, use defaultValue={null} instead.");return n}var i=null!=(r=t.find((function(e){return e.default})))?r:t[0];if(!i)throw new Error("Unexpected error: 0 tabValues");return i.value}({defaultValue:a,tabValues:g})})),m=j[0],f=j[1],y=x({queryString:d,groupId:h}),b=y[0],v=y[1],q=(r=function(e){return e?"docusaurus.tab."+e:null}({groupId:h}.groupId),n=(0,c.Dv)(r),i=n[0],s=n[1],[i,(0,t.useCallback)((function(e){r&&s.set(e)}),[r,s])]),k=q[0],L=q[1],w=function(){var e=null!=b?b:k;return p({value:e,tabValues:g})?e:null}();return(0,l.A)((function(){w&&f(w)}),[w]),{selectedValue:m,selectValue:(0,t.useCallback)((function(e){if(!p({value:e,tabValues:g}))throw new Error("Can't select invalid tab value="+e);f(e),v(e),L(e)}),[v,L,g]),tabValues:g}}var j=n(92303);const m={tabList:"tabList__CuJ",tabItem:"tabItem_LNqP"};var f=n(74848);function y(e){var r=e.className,n=e.block,t=e.selectedValue,a=e.selectValue,l=e.tabValues,o=[],d=(0,s.a_)().blockElementScrollPositionUntilNextRender,c=function(e){var r=e.currentTarget,n=o.indexOf(r),i=l[n].value;i!==t&&(d(r),a(i))},h=function(e){var r,n=null;switch(e.key){case"Enter":c(e);break;case"ArrowRight":var t,i=o.indexOf(e.currentTarget)+1;n=null!=(t=o[i])?t:o[0];break;case"ArrowLeft":var s,a=o.indexOf(e.currentTarget)-1;n=null!=(s=o[a])?s:o[o.length-1]}null==(r=n)||r.focus()};return(0,f.jsx)("ul",{role:"tablist","aria-orientation":"horizontal",className:(0,i.A)("tabs",{"tabs--block":n},r),children:l.map((function(e){var r=e.value,n=e.label,s=e.attributes;return(0,f.jsx)("li",Object.assign({role:"tab",tabIndex:t===r?0:-1,"aria-selected":t===r,ref:function(e){return o.push(e)},onKeyDown:h,onClick:c},s,{className:(0,i.A)("tabs__item",m.tabItem,null==s?void 0:s.className,{"tabs__item--active":t===r}),children:null!=n?n:r}),r)}))})}function b(e){var r=e.lazy,n=e.children,i=e.selectedValue,s=(Array.isArray(n)?n:[n]).filter(Boolean);if(r){var a=s.find((function(e){return e.props.value===i}));return a?(0,t.cloneElement)(a,{className:"margin-top--md"}):null}return(0,f.jsx)("div",{className:"margin-top--md",children:s.map((function(e,r){return(0,t.cloneElement)(e,{key:r,hidden:e.props.value!==i})}))})}function v(e){var r=g(e);return(0,f.jsxs)("div",{className:(0,i.A)("tabs-container",m.tabList),children:[(0,f.jsx)(y,Object.assign({},e,r)),(0,f.jsx)(b,Object.assign({},e,r))]})}function q(e){var r=(0,j.A)();return(0,f.jsx)(v,Object.assign({},e,{children:h(e.children)}),String(r))}},28453:(e,r,n)=>{n.d(r,{R:()=>a,x:()=>l});var t=n(96540);const i={},s=t.createContext(i);function a(e){const r=t.useContext(s);return t.useMemo((function(){return"function"==typeof e?e(r):{...r,...e}}),[r,e])}function l(e){let r;return r=e.disableParentContext?"function"==typeof e.components?e.components(i):e.components||i:a(e.components),t.createElement(s.Provider,{value:r},e.children)}}}]);