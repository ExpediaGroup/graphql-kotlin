"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[5741],{77718:(e,n,r)=>{r.r(n),r.d(n,{assets:()=>d,contentTitle:()=>l,default:()=>h,frontMatter:()=>a,metadata:()=>s,toc:()=>o});var i=r(74848),t=r(28453);const a={id:"gradle-plugin",title:"Gradle Plugin",original_id:"gradle-plugin"},l=void 0,s={id:"plugins/gradle-plugin",title:"Gradle Plugin",description:"GraphQL Kotlin Gradle Plugin provides functionality to introspect GraphQL schemas and generate a lightweight GraphQL HTTP client.",source:"@site/versioned_docs/version-3.x.x/plugins/gradle-plugin.md",sourceDirName:"plugins",slug:"/plugins/gradle-plugin",permalink:"/graphql-kotlin/docs/3.x.x/plugins/gradle-plugin",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-3.x.x/plugins/gradle-plugin.md",tags:[],version:"3.x.x",lastUpdatedBy:"Samuel Vazquez",lastUpdatedAt:1744404742e3,frontMatter:{id:"gradle-plugin",title:"Gradle Plugin",original_id:"gradle-plugin"},sidebar:"docs",previous:{title:"Client Customization",permalink:"/graphql-kotlin/docs/3.x.x/client/client-customization"},next:{title:"Maven Plugin",permalink:"/graphql-kotlin/docs/3.x.x/plugins/maven-plugin"}},d={},o=[{value:"Usage",id:"usage",level:2},{value:"Extension",id:"extension",level:2},{value:"Tasks",id:"tasks",level:2},{value:"graphqlDownloadSDL",id:"graphqldownloadsdl",level:3},{value:"graphqlGenerateClient",id:"graphqlgenerateclient",level:3},{value:"graphqlGenerateTestClient",id:"graphqlgeneratetestclient",level:3},{value:"graphqlIntrospectSchema",id:"graphqlintrospectschema",level:3},{value:"Examples",id:"examples",level:2},{value:"Downloading Schema SDL",id:"downloading-schema-sdl",level:3},{value:"Introspecting Schema",id:"introspecting-schema",level:3},{value:"Generating Client",id:"generating-client",level:3},{value:"Generating Client with Custom Scalars",id:"generating-client-with-custom-scalars",level:3},{value:"Generating Test Client",id:"generating-test-client",level:3},{value:"Complete Minimal Configuration Example",id:"complete-minimal-configuration-example",level:3},{value:"Complete Configuration Example",id:"complete-configuration-example",level:3}];function c(e){const n={a:"a",code:"code",h2:"h2",h3:"h3",p:"p",pre:"pre",strong:"strong",table:"table",tbody:"tbody",td:"td",th:"th",thead:"thead",tr:"tr",...(0,t.R)(),...e.components};return(0,i.jsxs)(i.Fragment,{children:[(0,i.jsx)(n.p,{children:"GraphQL Kotlin Gradle Plugin provides functionality to introspect GraphQL schemas and generate a lightweight GraphQL HTTP client."}),"\n",(0,i.jsx)(n.h2,{id:"usage",children:"Usage"}),"\n",(0,i.jsxs)(n.p,{children:[(0,i.jsx)(n.code,{children:"graphql-kotlin-gradle-plugin"})," is published on Gradle ",(0,i.jsx)(n.a,{href:"https://plugins.gradle.org/plugin/com.expediagroup.graphql",children:"Plugin Portal"}),".\nIn order to execute any of the provided tasks you need to first apply the plugin on your project."]}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-kotlin",children:'\n// build.gradle.kts\nplugins {\n    id("com.expediagroup.graphql") version $graphQLKotlinVersion\n}\n\n'})}),"\n",(0,i.jsx)(n.h2,{id:"extension",children:"Extension"}),"\n",(0,i.jsxs)(n.p,{children:["GraphQL Kotlin Gradle Plugin uses an extension on the project named ",(0,i.jsx)(n.code,{children:"graphql"})," of type\n",(0,i.jsx)(n.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/3.x.x/plugins/graphql-kotlin-gradle-plugin/src/main/kotlin/com/expediagroup/graphql/plugin/gradle/GraphQLPluginExtension.kt",children:"GraphQLPluginExtension"}),".\nThis extension can be used to configure global options instead of explicitly configuring individual tasks. Once extension\nis configured, it will automatically download SDL/run introspection to generate GraphQL schema and subsequently generate\nall GraphQL clients. GraphQL Extension should be used by default, except for cases where you need to only run individual\ntasks."]}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-kotlin",children:'\n// build.gradle.kts\nimport com.expediagroup.graphql.plugin.gradle.graphql\n\ngraphql {\n  client {\n    // GraphQL server endpoint that will be used to for running introspection queries. Alternatively you can download schema directly from `sdlEndpoint`.\n    endpoint = "http://localhost:8080/graphql"\n    // GraphQL server SDL endpoint that will be used to download schema. Alternatively you can run introspection query against `endpoint`.\n    sdlEndpoint = "http://localhost:8080/sdl"\n    // Target package name to be used for generated classes.\n    packageName = "com.example.generated"\n    // Optional HTTP headers to be specified on an introspection query or SDL request.\n    headers["X-Custom-Header"] = "Custom-Header-Value"\n    // Boolean flag indicating whether or not selection of deprecated fields is allowed.\n    allowDeprecatedFields = false\n    // Custom GraphQL scalar to converter mapping containing information about corresponding Java type and converter that should be used to serialize/deserialize values.\n    converters["UUID"] = ScalarConverterMapping("java.util.UUID", "com.example.UUIDScalarConverter")\n    // List of query files to be processed.\n    queryFiles.add(file("${project.projectDir}/src/main/resources/queries/MyQuery.graphql"))\n    // Timeout configuration\n    timeout {\n        // Connect timeout in milliseconds\n        connect = 5_000\n        // Read timeout in milliseconds\n        read = 15_000\n    }\n  }\n}\n\n'})}),"\n",(0,i.jsx)(n.h2,{id:"tasks",children:"Tasks"}),"\n",(0,i.jsxs)(n.p,{children:["All ",(0,i.jsx)(n.code,{children:"graphql-kotlin-gradle-plugin"})," tasks are grouped together under ",(0,i.jsx)(n.code,{children:"GraphQL"})," task group and their names are prefixed with\n",(0,i.jsx)(n.code,{children:"graphql"}),". You can find detailed information about GraphQL kotlin tasks by running Gradle ",(0,i.jsx)(n.code,{children:"help --task <taskName>"})," task."]}),"\n",(0,i.jsx)(n.h3,{id:"graphqldownloadsdl",children:"graphqlDownloadSDL"}),"\n",(0,i.jsxs)(n.p,{children:["Task that attempts to download GraphQL schema in SDL format from the specified ",(0,i.jsx)(n.code,{children:"endpoint"})," and saves the underlying\nschema file as ",(0,i.jsx)(n.code,{children:"schema.graphql"})," under build directory. In general, this task provides limited functionality by itself\nand could be used as an alternative to ",(0,i.jsx)(n.code,{children:"graphqlIntrospectSchema"})," to generate input for the subsequent\n",(0,i.jsx)(n.code,{children:"graphqlGenerateClient"})," task."]}),"\n",(0,i.jsx)(n.p,{children:(0,i.jsx)(n.strong,{children:"Properties"})}),"\n",(0,i.jsxs)(n.table,{children:[(0,i.jsx)(n.thead,{children:(0,i.jsxs)(n.tr,{children:[(0,i.jsx)(n.th,{children:"Property"}),(0,i.jsx)(n.th,{children:"Type"}),(0,i.jsx)(n.th,{children:"Required"}),(0,i.jsx)(n.th,{children:"Description"})]})}),(0,i.jsxs)(n.tbody,{children:[(0,i.jsxs)(n.tr,{children:[(0,i.jsx)(n.td,{children:(0,i.jsx)(n.code,{children:"endpoint"})}),(0,i.jsx)(n.td,{children:"String"}),(0,i.jsx)(n.td,{children:"yes"}),(0,i.jsxs)(n.td,{children:["Target GraphQL server SDL endpoint that will be used to download schema.",(0,i.jsx)("br",{}),(0,i.jsx)(n.strong,{children:"Command line property is"}),": ",(0,i.jsx)(n.code,{children:"endpoint"}),"."]})]}),(0,i.jsxs)(n.tr,{children:[(0,i.jsx)(n.td,{children:(0,i.jsx)(n.code,{children:"headers"})}),(0,i.jsx)(n.td,{children:"Map<String, Any>"}),(0,i.jsx)(n.td,{}),(0,i.jsx)(n.td,{children:"Optional HTTP headers to be specified on a SDL request."})]}),(0,i.jsxs)(n.tr,{children:[(0,i.jsx)(n.td,{children:(0,i.jsx)(n.code,{children:"timeoutConfig"})}),(0,i.jsx)(n.td,{children:"TimeoutConfig"}),(0,i.jsx)(n.td,{}),(0,i.jsxs)(n.td,{children:["Timeout configuration(in milliseconds) to download schema from SDL endpoint before we cancel the request.",(0,i.jsx)("br",{}),(0,i.jsx)(n.strong,{children:"Default value are:"})," connect timeout = 5_000, read timeout = 15_000.",(0,i.jsx)("br",{})]})]})]})]}),"\n",(0,i.jsx)(n.h3,{id:"graphqlgenerateclient",children:"graphqlGenerateClient"}),"\n",(0,i.jsxs)(n.p,{children:["Task that generates GraphQL Kotlin client and corresponding data classes based on the provided GraphQL queries that are\nevaluated against target Graphql schema. Individual clients with their specific data models are generated for each query\nfile and are placed under specified ",(0,i.jsx)(n.code,{children:"packageName"}),". When this task is added to the project, either through explicit configuration\nor through the ",(0,i.jsx)(n.code,{children:"graphql"})," extension, it will automatically configure itself as a dependency of a ",(0,i.jsx)(n.code,{children:"compileKotlin"})," task and\nresulting generated code will be automatically added to the project main source set."]}),"\n",(0,i.jsx)(n.p,{children:(0,i.jsx)(n.strong,{children:"Properties"})}),"\n",(0,i.jsxs)(n.table,{children:[(0,i.jsx)(n.thead,{children:(0,i.jsxs)(n.tr,{children:[(0,i.jsx)(n.th,{children:"Property"}),(0,i.jsx)(n.th,{children:"Type"}),(0,i.jsx)(n.th,{children:"Required"}),(0,i.jsx)(n.th,{children:"Description"})]})}),(0,i.jsxs)(n.tbody,{children:[(0,i.jsxs)(n.tr,{children:[(0,i.jsx)(n.td,{children:(0,i.jsx)(n.code,{children:"allowDeprecatedFields"})}),(0,i.jsx)(n.td,{children:"Boolean"}),(0,i.jsx)(n.td,{}),(0,i.jsxs)(n.td,{children:["Boolean flag indicating whether selection of deprecated fields is allowed or not.",(0,i.jsx)("br",{}),(0,i.jsx)(n.strong,{children:"Default value is:"})," ",(0,i.jsx)(n.code,{children:"false"}),".",(0,i.jsx)("br",{}),(0,i.jsx)(n.strong,{children:"Command line property is"}),": ",(0,i.jsx)(n.code,{children:"allowDeprecatedFields"}),"."]})]}),(0,i.jsxs)(n.tr,{children:[(0,i.jsx)(n.td,{children:(0,i.jsx)(n.code,{children:"converters"})}),(0,i.jsx)(n.td,{children:"Map<String, ScalarConverter>"}),(0,i.jsx)(n.td,{}),(0,i.jsx)(n.td,{children:"Custom GraphQL scalar to converter mapping containing information about corresponding Java type and converter that should be used to serialize/deserialize values."})]}),(0,i.jsxs)(n.tr,{children:[(0,i.jsx)(n.td,{children:(0,i.jsx)(n.code,{children:"packageName"})}),(0,i.jsx)(n.td,{children:"String"}),(0,i.jsx)(n.td,{children:"yes"}),(0,i.jsxs)(n.td,{children:["Target package name for generated code.",(0,i.jsx)("br",{}),(0,i.jsx)(n.strong,{children:"Command line property is"}),": ",(0,i.jsx)(n.code,{children:"packageName"}),"."]})]}),(0,i.jsxs)(n.tr,{children:[(0,i.jsx)(n.td,{children:(0,i.jsx)(n.code,{children:"queryFiles"})}),(0,i.jsx)(n.td,{children:"FileCollection"}),(0,i.jsx)(n.td,{}),(0,i.jsxs)(n.td,{children:["List of query files to be processed. Instead of a list of files to be processed you can specify ",(0,i.jsx)(n.code,{children:"queryFileDirectory"})," directory instead. If this property is specified it will take precedence over the corresponding directory property."]})]}),(0,i.jsxs)(n.tr,{children:[(0,i.jsx)(n.td,{children:(0,i.jsx)(n.code,{children:"queryFileDirectory"})}),(0,i.jsx)(n.td,{children:"String"}),(0,i.jsx)(n.td,{}),(0,i.jsxs)(n.td,{children:["Directory file containing GraphQL queries. Instead of specifying a directory you can also specify list of query file by using ",(0,i.jsx)(n.code,{children:"queryFiles"})," property instead.",(0,i.jsx)("br",{}),(0,i.jsx)(n.strong,{children:"Default value is:"})," ",(0,i.jsx)(n.code,{children:"src/main/resources"}),".",(0,i.jsx)("br",{}),(0,i.jsx)(n.strong,{children:"Command line property is"}),": ",(0,i.jsx)(n.code,{children:"queryFileDirectory"}),"."]})]}),(0,i.jsxs)(n.tr,{children:[(0,i.jsx)(n.td,{children:(0,i.jsx)(n.code,{children:"schemaFile"})}),(0,i.jsx)(n.td,{children:"File"}),(0,i.jsxs)(n.td,{children:[(0,i.jsx)(n.code,{children:"schemaFileName"})," or ",(0,i.jsx)(n.code,{children:"schemaFile"})," has to be provided"]}),(0,i.jsx)(n.td,{children:"GraphQL schema file that will be used to generate client code."})]}),(0,i.jsxs)(n.tr,{children:[(0,i.jsx)(n.td,{children:(0,i.jsx)(n.code,{children:"schemaFileName"})}),(0,i.jsx)(n.td,{children:"String"}),(0,i.jsxs)(n.td,{children:[(0,i.jsx)(n.code,{children:"schemaFileName"})," or ",(0,i.jsx)(n.code,{children:"schemaFile"})," has to be provided"]}),(0,i.jsxs)(n.td,{children:["Path to GraphQL schema file that will be used to generate client code.",(0,i.jsx)("br",{}),(0,i.jsx)(n.strong,{children:"Command line property is"}),": ",(0,i.jsx)(n.code,{children:"schemaFileName"}),"."]})]})]})]}),"\n",(0,i.jsx)(n.h3,{id:"graphqlgeneratetestclient",children:"graphqlGenerateTestClient"}),"\n",(0,i.jsxs)(n.p,{children:["Task that generates GraphQL Kotlin test client and corresponding data classes based on the provided GraphQL queries that are\nevaluated against target Graphql schema. Individual test clients with their specific data models are generated for each query\nfile and are placed under specified ",(0,i.jsx)(n.code,{children:"packageName"}),". When this task is added to the project it will automatically configure\nitself as a dependency of a ",(0,i.jsx)(n.code,{children:"compileTestKotlin"})," task and resulting generated code will be automatically added to the project\ntest source set."]}),"\n",(0,i.jsx)(n.p,{children:(0,i.jsx)(n.strong,{children:"Properties"})}),"\n",(0,i.jsxs)(n.table,{children:[(0,i.jsx)(n.thead,{children:(0,i.jsxs)(n.tr,{children:[(0,i.jsx)(n.th,{children:"Property"}),(0,i.jsx)(n.th,{children:"Type"}),(0,i.jsx)(n.th,{children:"Required"}),(0,i.jsx)(n.th,{children:"Description"})]})}),(0,i.jsxs)(n.tbody,{children:[(0,i.jsxs)(n.tr,{children:[(0,i.jsx)(n.td,{children:(0,i.jsx)(n.code,{children:"allowDeprecatedFields"})}),(0,i.jsx)(n.td,{children:"Boolean"}),(0,i.jsx)(n.td,{}),(0,i.jsxs)(n.td,{children:["Boolean flag indicating whether selection of deprecated fields is allowed or not.",(0,i.jsx)("br",{}),(0,i.jsx)(n.strong,{children:"Default value is:"})," ",(0,i.jsx)(n.code,{children:"false"}),".",(0,i.jsx)("br",{}),(0,i.jsx)(n.strong,{children:"Command line property is"}),": ",(0,i.jsx)(n.code,{children:"allowDeprecatedFields"}),"."]})]}),(0,i.jsxs)(n.tr,{children:[(0,i.jsx)(n.td,{children:(0,i.jsx)(n.code,{children:"converters"})}),(0,i.jsx)(n.td,{children:"Map<String, ScalarConverter>"}),(0,i.jsx)(n.td,{}),(0,i.jsx)(n.td,{children:"Custom GraphQL scalar to converter mapping containing information about corresponding Java type and converter that should be used to serialize/deserialize values."})]}),(0,i.jsxs)(n.tr,{children:[(0,i.jsx)(n.td,{children:(0,i.jsx)(n.code,{children:"packageName"})}),(0,i.jsx)(n.td,{children:"String"}),(0,i.jsx)(n.td,{children:"yes"}),(0,i.jsxs)(n.td,{children:["Target package name for generated code.",(0,i.jsx)("br",{}),(0,i.jsx)(n.strong,{children:"Command line property is"}),": ",(0,i.jsx)(n.code,{children:"packageName"}),"."]})]}),(0,i.jsxs)(n.tr,{children:[(0,i.jsx)(n.td,{children:(0,i.jsx)(n.code,{children:"queryFiles"})}),(0,i.jsx)(n.td,{children:"FileCollection"}),(0,i.jsx)(n.td,{}),(0,i.jsxs)(n.td,{children:["List of query files to be processed. Instead of a list of files to be processed you can specify ",(0,i.jsx)(n.code,{children:"queryFileDirectory"})," directory instead. If this property is specified it will take precedence over the corresponding directory property."]})]}),(0,i.jsxs)(n.tr,{children:[(0,i.jsx)(n.td,{children:(0,i.jsx)(n.code,{children:"queryFileDirectory"})}),(0,i.jsx)(n.td,{children:"String"}),(0,i.jsx)(n.td,{}),(0,i.jsxs)(n.td,{children:["Directory file containing GraphQL queries. Instead of specifying a directory you can also specify list of query file by using ",(0,i.jsx)(n.code,{children:"queryFiles"})," property instead.",(0,i.jsx)("br",{}),(0,i.jsx)(n.strong,{children:"Default value is:"})," ",(0,i.jsx)(n.code,{children:"src/test/resources"}),".",(0,i.jsx)("br",{}),(0,i.jsx)(n.strong,{children:"Command line property is"}),": ",(0,i.jsx)(n.code,{children:"queryFileDirectory"}),"."]})]}),(0,i.jsxs)(n.tr,{children:[(0,i.jsx)(n.td,{children:(0,i.jsx)(n.code,{children:"schemaFile"})}),(0,i.jsx)(n.td,{children:"File"}),(0,i.jsxs)(n.td,{children:[(0,i.jsx)(n.code,{children:"schemaFileName"})," or ",(0,i.jsx)(n.code,{children:"schemaFile"})," has to be provided"]}),(0,i.jsx)(n.td,{children:"GraphQL schema file that will be used to generate client code."})]}),(0,i.jsxs)(n.tr,{children:[(0,i.jsx)(n.td,{children:(0,i.jsx)(n.code,{children:"schemaFileName"})}),(0,i.jsx)(n.td,{children:"String"}),(0,i.jsxs)(n.td,{children:[(0,i.jsx)(n.code,{children:"schemaFileName"})," or ",(0,i.jsx)(n.code,{children:"schemaFile"})," has to be provided"]}),(0,i.jsxs)(n.td,{children:["Path to GraphQL schema file that will be used to generate client code.",(0,i.jsx)("br",{}),(0,i.jsx)(n.strong,{children:"Command line property is"}),": ",(0,i.jsx)(n.code,{children:"schemaFileName"}),"."]})]})]})]}),"\n",(0,i.jsx)(n.h3,{id:"graphqlintrospectschema",children:"graphqlIntrospectSchema"}),"\n",(0,i.jsxs)(n.p,{children:["Task that executes GraphQL introspection query against specified ",(0,i.jsx)(n.code,{children:"endpoint"})," and saves the underlying schema file as\n",(0,i.jsx)(n.code,{children:"schema.graphql"})," under build directory. In general, this task provides limited functionality by itself and instead\nshould be used to generate input for the subsequent ",(0,i.jsx)(n.code,{children:"graphqlGenerateClient"})," task."]}),"\n",(0,i.jsx)(n.p,{children:(0,i.jsx)(n.strong,{children:"Properties"})}),"\n",(0,i.jsxs)(n.table,{children:[(0,i.jsx)(n.thead,{children:(0,i.jsxs)(n.tr,{children:[(0,i.jsx)(n.th,{children:"Property"}),(0,i.jsx)(n.th,{children:"Type"}),(0,i.jsx)(n.th,{children:"Required"}),(0,i.jsx)(n.th,{children:"Description"})]})}),(0,i.jsxs)(n.tbody,{children:[(0,i.jsxs)(n.tr,{children:[(0,i.jsx)(n.td,{children:(0,i.jsx)(n.code,{children:"endpoint"})}),(0,i.jsx)(n.td,{children:"String"}),(0,i.jsx)(n.td,{children:"yes"}),(0,i.jsxs)(n.td,{children:["Target GraphQL server endpoint that will be used to execute introspection queries.",(0,i.jsx)("br",{}),(0,i.jsx)(n.strong,{children:"Command line property is"}),": ",(0,i.jsx)(n.code,{children:"endpoint"}),"."]})]}),(0,i.jsxs)(n.tr,{children:[(0,i.jsx)(n.td,{children:(0,i.jsx)(n.code,{children:"headers"})}),(0,i.jsx)(n.td,{children:"Map<String, Any>"}),(0,i.jsx)(n.td,{}),(0,i.jsx)(n.td,{children:"Optional HTTP headers to be specified on an introspection query."})]}),(0,i.jsxs)(n.tr,{children:[(0,i.jsx)(n.td,{children:(0,i.jsx)(n.code,{children:"timeoutConfig"})}),(0,i.jsx)(n.td,{children:"TimeoutConfig"}),(0,i.jsx)(n.td,{}),(0,i.jsxs)(n.td,{children:["Timeout configuration(in milliseconds) to download schema from SDL endpoint before we cancel the request.",(0,i.jsx)("br",{}),(0,i.jsx)(n.strong,{children:"Default value are:"})," connect timeout = 5_000, read timeout = 15_000.",(0,i.jsx)("br",{})]})]})]})]}),"\n",(0,i.jsx)(n.h2,{id:"examples",children:"Examples"}),"\n",(0,i.jsx)(n.h3,{id:"downloading-schema-sdl",children:"Downloading Schema SDL"}),"\n",(0,i.jsxs)(n.p,{children:["GraphQL endpoints are often public and as such many servers might disable introspection queries in production environment.\nSince GraphQL schema is needed to generate type safe clients, as alternative GraphQL servers might expose private\nendpoints (e.g. accessible only from within network, etc) that could be used to download schema in Schema Definition\nLanguage (SDL) directly. ",(0,i.jsx)(n.code,{children:"graphqlDownloadSDL"})," task requires target GraphQL server ",(0,i.jsx)(n.code,{children:"endpoint"})," to be specified and can\nbe executed directly from the command line by explicitly passing ",(0,i.jsx)(n.code,{children:"endpoint"})," parameter"]}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-shell",metastring:"script",children:'\n$ gradle graphqlDownloadSDL --endpoint="http://localhost:8080/sdl"\n\n'})}),"\n",(0,i.jsx)(n.p,{children:"Task can also be explicitly configured in your Gradle build file"}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-kotlin",children:'\n// build.gradle.kts\nimport com.expediagroup.graphql.plugin.gradle.tasks.GraphQLDownloadSDLTask\n\nval graphqlDownloadSDL by tasks.getting(GraphQLDownloadSDLTask::class) {\n    endpoint.set("http://localhost:8080/sdl")\n}\n\n'})}),"\n",(0,i.jsx)(n.p,{children:"> NOTE: This task does not automatically configure itself to be part of your build lifecycle. You will need to explicitly\n> invoke it OR configure it as a dependency of some other task."}),"\n",(0,i.jsx)(n.h3,{id:"introspecting-schema",children:"Introspecting Schema"}),"\n",(0,i.jsxs)(n.p,{children:["Introspection task requires target GraphQL server ",(0,i.jsx)(n.code,{children:"endpoint"})," to be specified. Task can be executed directly from the\ncommand line by explicitly passing endpoint parameter"]}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-shell",metastring:"script",children:'\n$ gradle graphqlIntrospectSchema --endpoint="http://localhost:8080/graphql"\n\n'})}),"\n",(0,i.jsx)(n.p,{children:"Task can also be explicitly configured in your Gradle build file"}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-kotlin",children:'\n// build.gradle.kts\nimport com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask\n\nval graphqlIntrospectSchema by tasks.getting(GraphQLIntrospectSchemaTask::class) {\n    endpoint.set("http://localhost:8080/graphql")\n}\n\n'})}),"\n",(0,i.jsx)(n.p,{children:"> NOTE: This task does not automatically configure itself to be part of your build lifecycle. You will need to explicitly\n> invoke it OR configure it as a dependency of some other task."}),"\n",(0,i.jsx)(n.h3,{id:"generating-client",children:"Generating Client"}),"\n",(0,i.jsxs)(n.p,{children:["GraphQL Kotlin client code is generated based on the provided queries that will be executed against target GraphQL ",(0,i.jsx)(n.code,{children:"schemaFile"}),".\nSeparate class is generated for each provided GraphQL query and are saved under specified ",(0,i.jsx)(n.code,{children:"packageName"}),". When using default\nconfiguration and storing GraphQL queries under ",(0,i.jsx)(n.code,{children:"src/main/resources"})," directories, task can be executed directly from the\ncommand line by explicitly providing required properties."]}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-shell",metastring:"script",children:'\n$ gradle graphqlGenerateClient --schemaFileName"mySchema.graphql" --packageName="com.example.generated"\n\n'})}),"\n",(0,i.jsx)(n.p,{children:"Task can also be explicitly configured in your Gradle build file"}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-kotlin",children:'\n// build.gradle.kts\nimport com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask\n\nval graphqlGenerateClient by tasks.getting(GraphQLGenerateClientTask::class) {\n  packageName.set("com.example.generated")\n  schemaFileName.set("mySchema.graphql")\n}\n\n'})}),"\n",(0,i.jsxs)(n.p,{children:["This will process all GraphQL queries located under ",(0,i.jsx)(n.code,{children:"src/main/resources"})," and generate corresponding GraphQL Kotlin clients.\nGenerated classes will be automatically added to the project compile sources."]}),"\n",(0,i.jsx)(n.h3,{id:"generating-client-with-custom-scalars",children:"Generating Client with Custom Scalars"}),"\n",(0,i.jsxs)(n.p,{children:["By default, all custom GraphQL scalars will be serialized as Strings. You can override this default behavior by specifying\ncustom ",(0,i.jsx)(n.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/3.x.x/graphql-kotlin-client/src/main/kotlin/com/expediagroup/graphql/client/converter/ScalarConverter.kt",children:"scalar converter"}),"."]}),"\n",(0,i.jsx)(n.p,{children:"For example given following custom scalar in our GraphQL schema"}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-graphql",children:"\nscalar UUID\n\n"})}),"\n",(0,i.jsxs)(n.p,{children:["We can create a custom converter to automatically convert this custom scalar to ",(0,i.jsx)(n.code,{children:"java.util.UUID"})]}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-kotlin",children:"\npackage com.example\n\nimport com.expediagroup.graphql.client.converter.ScalarConverter\nimport java.util.UUID\n\nclass UUIDScalarConverter : ScalarConverter<UUID> {\n    override fun toScalar(rawValue: String): UUID = UUID.fromString(rawValue)\n    override fun toJson(value: UUID): String = value.toString()\n}\n\n"})}),"\n",(0,i.jsx)(n.p,{children:"Afterwards we need to configure our plugin to use this custom converter"}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-kotlin",children:'\n// build.gradle.kts\nimport com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask\n\nval graphqlGenerateClient by tasks.getting(GraphQLGenerateClientTask::class) {\n  packageName.set("com.example.generated")\n  schemaFileName.set("mySchema.graphql")\n  converters.put("UUID", ScalarConverterMapping("java.util.UUID", "com.example.UUIDScalarConverter"))\n}\n\n'})}),"\n",(0,i.jsx)(n.h3,{id:"generating-test-client",children:"Generating Test Client"}),"\n",(0,i.jsxs)(n.p,{children:["GraphQL Kotlin test client code is generated based on the provided queries that will be executed against target GraphQL ",(0,i.jsx)(n.code,{children:"schemaFile"}),".\nSeparate class is generated for each provided GraphQL query and are saved under specified ",(0,i.jsx)(n.code,{children:"packageName"}),". When using default\nconfiguration and storing GraphQL queries under ",(0,i.jsx)(n.code,{children:"src/test/resources"})," directories, task can be executed directly from the\ncommand line by explicitly providing required properties."]}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-shell",metastring:"script",children:'\n$ gradle graphqlGenerateTestClient --schemaFileName"mySchema.graphql" --packageName="com.example.generated"\n\n'})}),"\n",(0,i.jsx)(n.p,{children:"Task can also be explicitly configured in your Gradle build file"}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-kotlin",children:'\n// build.gradle.kts\nimport com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask\n\nval graphqlGenerateTestClient by tasks.getting(GraphQLGenerateClientTask::class) {\n  packageName.set("com.example.generated")\n  schemaFileName.set("mySchema.graphql")\n}\n\n'})}),"\n",(0,i.jsxs)(n.p,{children:["This will process all GraphQL queries located under ",(0,i.jsx)(n.code,{children:"src/test/resources"})," and generate corresponding GraphQL Kotlin clients.\nGenerated classes will be automatically added to the project test compile sources."]}),"\n",(0,i.jsxs)(n.p,{children:["> NOTE: ",(0,i.jsx)(n.code,{children:"graphqlGenerateTestClient"})," cannot be configured through the ",(0,i.jsx)(n.code,{children:"graphql"})," extension and has to be explicitly configured."]}),"\n",(0,i.jsx)(n.h3,{id:"complete-minimal-configuration-example",children:"Complete Minimal Configuration Example"}),"\n",(0,i.jsxs)(n.p,{children:["Following is the minimal configuration that runs introspection query against a target GraphQL server and generates a\ncorresponding schema. This generated schema is subsequently used to generate GraphQL client code based on the queries\nprovided under ",(0,i.jsx)(n.code,{children:"src/main/resources"})," directory."]}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-kotlin",children:'\n// build.gradle.kts\nimport com.expediagroup.graphql.plugin.gradle.graphql\n\ngraphql {\n  client {\n    endpoint = "http://localhost:8080/graphql"\n    packageName = "com.example.generated"\n  }\n}\n\n'})}),"\n",(0,i.jsx)(n.p,{children:"Above configuration is equivalent to the following"}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-kotlin",children:'\n// build.gradle.kts\nimport com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask\nimport com.expediagroup.graphql.plugin.gradle.tasks.GraphQLIntrospectSchemaTask\n\nval graphqlIntrospectSchema by tasks.getting(GraphQLIntrospectSchemaTask::class) {\n  endpoint.set("http://localhost:8080/graphql")\n}\nval graphqlGenerateClient by tasks.getting(GraphQLGenerateClientTask::class) {\n  packageName.set("com.example.generated")\n  schemaFile.set(graphqlIntrospectSchema.outputFile)\n  dependsOn("graphqlIntrospectSchema")\n}\n\n'})}),"\n",(0,i.jsx)(n.h3,{id:"complete-configuration-example",children:"Complete Configuration Example"}),"\n",(0,i.jsx)(n.p,{children:"Following is a configuration example that downloads schema SDL from a target GraphQL server that is then used to generate\nthe GraphQL client code based on the provided query."}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-kotlin",children:'\n// build.gradle.kts\nimport com.expediagroup.graphql.plugin.config.TimeoutConfig\nimport com.expediagroup.graphql.plugin.generator.ScalarConverterMapping\nimport com.expediagroup.graphql.plugin.gradle.graphql\n\ngraphql {\n  client {\n    sdlEndpoint = "http://localhost:8080/sdl"\n    packageName = "com.example.generated"\n    // optional configuration\n    allowDeprecatedFields = true\n    headers["X-Custom-Header"] = "My-Custom-Header"\n    converters["UUID"] = ScalarConverterMapping("java.util.UUID", "com.example.UUIDScalarConverter")\n    queryFiles.add(file("${project.projectDir}/src/main/resources/queries/MyQuery.graphql"))\n    timeout {\n        connect = 10_000\n        read = 30_000\n    }\n  }\n}\n\n'})}),"\n",(0,i.jsx)(n.p,{children:"Above configuration is equivalent to the following"}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-kotlin",children:'\n// build.gradle.kts\nimport com.expediagroup.graphql.plugin.config.TimeoutConfig\nimport com.expediagroup.graphql.plugin.generator.ScalarConverterMapping\nimport com.expediagroup.graphql.plugin.gradle.tasks.GraphQLDownloadSDLTask\nimport com.expediagroup.graphql.plugin.gradle.tasks.GraphQLIntrospectSchemaTask\n\nval graphqlDownloadSDL by tasks.getting(GraphQLDownloadSDLTask::class) {\n    endpoint.set("http://localhost:8080/sdl")\n    headers.put("X-Custom-Header", "My-Custom-Header")\n    timeoutConfig.set(TimeoutConfig(connect = 10_000, read = 30_000))\n}\nval graphqlGenerateClient by tasks.getting(GraphQLGenerateClientTask::class) {\n    packageName.set("com.example.generated")\n    schemaFile.set(graphqlDownloadSDL.outputFile)\n    // optional\n    allowDeprecatedFields.set(true)\n    converters.put("UUID", ScalarConverterMapping("java.util.UUID", "com.example.UUIDScalarConverter"))\n    queryFiles.from("${project.projectDir}/src/main/resources/queries/MyQuery.graphql")\n\n    dependsOn("graphqlDownloadSDL")\n}\n\n'})})]})}function h(e={}){const{wrapper:n}={...(0,t.R)(),...e.components};return n?(0,i.jsx)(n,{...e,children:(0,i.jsx)(c,{...e})}):c(e)}},28453:(e,n,r)=>{r.d(n,{R:()=>l,x:()=>s});var i=r(96540);const t={},a=i.createContext(t);function l(e){const n=i.useContext(a);return i.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function s(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(t):e.components||t:l(e.components),i.createElement(a.Provider,{value:n},e.children)}}}]);