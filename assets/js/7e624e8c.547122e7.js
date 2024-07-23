"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[9757],{70403:(e,n,a)=>{a.r(n),a.d(n,{assets:()=>d,contentTitle:()=>s,default:()=>u,frontMatter:()=>o,metadata:()=>c,toc:()=>p});var r=a(74848),l=a(28453),i=a(11470),t=a(19365);const o={id:"gradle-plugin-usage",title:"Gradle Plugin Usage",sidebar_label:"Usage"},s=void 0,c={id:"plugins/gradle-plugin-usage",title:"Gradle Plugin Usage",description:"Downloading Schema SDL",source:"@site/versioned_docs/version-4.x.x/plugins/gradle-plugin-usage.mdx",sourceDirName:"plugins",slug:"/plugins/gradle-plugin-usage",permalink:"/graphql-kotlin/docs/4.x.x/plugins/gradle-plugin-usage",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-4.x.x/plugins/gradle-plugin-usage.mdx",tags:[],version:"4.x.x",lastUpdatedBy:"mykevinjung",lastUpdatedAt:1721694712e3,frontMatter:{id:"gradle-plugin-usage",title:"Gradle Plugin Usage",sidebar_label:"Usage"},sidebar:"docs",previous:{title:"Tasks",permalink:"/graphql-kotlin/docs/4.x.x/plugins/gradle-plugin-tasks"},next:{title:"Goals",permalink:"/graphql-kotlin/docs/4.x.x/plugins/maven-plugin-goals"}},d={},p=[{value:"Downloading Schema SDL",id:"downloading-schema-sdl",level:2},{value:"Introspecting Schema",id:"introspecting-schema",level:2},{value:"Generating Client",id:"generating-client",level:2},{value:"Generating Client with Custom Scalars",id:"generating-client-with-custom-scalars",level:2},{value:"Generating Client using Kotlinx Serialization",id:"generating-client-using-kotlinx-serialization",level:2},{value:"Generating Test Client",id:"generating-test-client",level:2},{value:"Minimal Client Configuration Example",id:"minimal-client-configuration-example",level:2},{value:"Complete Client Configuration Example",id:"complete-client-configuration-example",level:2},{value:"Generating Multiple Clients",id:"generating-multiple-clients",level:2},{value:"Generating SDL Example",id:"generating-sdl-example",level:2},{value:"Generating SDL with Custom Hooks Provider Example",id:"generating-sdl-with-custom-hooks-provider-example",level:2}];function g(e){const n={a:"a",admonition:"admonition",code:"code",h2:"h2",p:"p",pre:"pre",...(0,l.R)(),...e.components};return(0,r.jsxs)(r.Fragment,{children:[(0,r.jsx)(n.h2,{id:"downloading-schema-sdl",children:"Downloading Schema SDL"}),"\n",(0,r.jsxs)(n.p,{children:["GraphQL endpoints are often public and as such many servers might disable introspection queries in production environment.\nSince GraphQL schema is needed to generate type safe clients, as alternative GraphQL servers might expose private\nendpoints (e.g. accessible only from within network, etc) that could be used to download schema in Schema Definition\nLanguage (SDL) directly. ",(0,r.jsx)(n.code,{children:"graphqlDownloadSDL"})," task requires target GraphQL server ",(0,r.jsx)(n.code,{children:"endpoint"})," to be specified and can\nbe executed directly from the command line by explicitly passing ",(0,r.jsx)(n.code,{children:"endpoint"})," parameter"]}),"\n",(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-shell",metastring:"script",children:'$ gradle graphqlDownloadSDL --endpoint="http://localhost:8080/sdl"\n'})}),"\n",(0,r.jsx)(n.p,{children:"Task can also be explicitly configured in your Gradle build file"}),"\n",(0,r.jsxs)(i.A,{defaultValue:"kotlin",values:[{label:"Kotlin",value:"kotlin"},{label:"Groovy",value:"groovy"}],children:[(0,r.jsx)(t.A,{value:"kotlin",children:(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-kotlin",children:'// build.gradle.kts\nimport com.expediagroup.graphql.plugin.gradle.tasks.GraphQLDownloadSDLTask\n\nval graphqlDownloadSDL by tasks.getting(GraphQLDownloadSDLTask::class) {\n    endpoint.set("http://localhost:8080/sdl")\n}\n'})})}),(0,r.jsx)(t.A,{value:"groovy",children:(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-groovy",children:'//build.gradle\ngraphqlDownloadSDL {\n    endpoint = "http://localhost:8080/sdl"\n}\n'})})})]}),"\n",(0,r.jsx)(n.admonition,{type:"info",children:(0,r.jsx)(n.p,{children:"This task does not automatically configure itself to be part of your build lifecycle. You will need to explicitly\ninvoke it OR configure it as a dependency of some other task."})}),"\n",(0,r.jsx)(n.h2,{id:"introspecting-schema",children:"Introspecting Schema"}),"\n",(0,r.jsxs)(n.p,{children:["Introspection task requires target GraphQL server ",(0,r.jsx)(n.code,{children:"endpoint"})," to be specified. Task can be executed directly from the\ncommand line by explicitly passing endpoint parameter"]}),"\n",(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-shell",metastring:"script",children:'$ gradle graphqlIntrospectSchema --endpoint="http://localhost:8080/graphql"\n'})}),"\n",(0,r.jsx)(n.p,{children:"Task can also be explicitly configured in your Gradle build file"}),"\n",(0,r.jsxs)(i.A,{defaultValue:"kotlin",values:[{label:"Kotlin",value:"kotlin"},{label:"Groovy",value:"groovy"}],children:[(0,r.jsx)(t.A,{value:"kotlin",children:(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-kotlin",children:'// build.gradle.kts\nimport com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask\n\nval graphqlIntrospectSchema by tasks.getting(GraphQLIntrospectSchemaTask::class) {\n    endpoint.set("http://localhost:8080/graphql")\n}\n'})})}),(0,r.jsx)(t.A,{value:"groovy",children:(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-groovy",children:'//build.gradle\ngraphqlIntrospectSchema {\n    endpoint = "http://localhost:8080/graphql"\n}\n'})})})]}),"\n",(0,r.jsx)(n.admonition,{type:"info",children:(0,r.jsx)(n.p,{children:"This task does not automatically configure itself to be part of your build lifecycle. You will need to explicitly\ninvoke it OR configure it as a dependency of some other task."})}),"\n",(0,r.jsx)(n.h2,{id:"generating-client",children:"Generating Client"}),"\n",(0,r.jsxs)(n.p,{children:["GraphQL Kotlin client code is generated based on the provided queries that will be executed against target GraphQL ",(0,r.jsx)(n.code,{children:"schemaFile"}),".\nSeparate class is generated for each provided GraphQL query and are saved under specified ",(0,r.jsx)(n.code,{children:"packageName"}),". When using default\nconfiguration and storing GraphQL queries under ",(0,r.jsx)(n.code,{children:"src/main/resources"})," directories, task can be executed directly from the\ncommand line by explicitly providing required properties."]}),"\n",(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-shell",metastring:"script",children:'$ gradle graphqlGenerateClient --schemaFileName"mySchema.graphql" --packageName="com.example.generated"\n'})}),"\n",(0,r.jsx)(n.p,{children:"Task can also be explicitly configured in your Gradle build file"}),"\n",(0,r.jsxs)(i.A,{defaultValue:"kotlin",values:[{label:"Kotlin",value:"kotlin"},{label:"Groovy",value:"groovy"}],children:[(0,r.jsx)(t.A,{value:"kotlin",children:(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-kotlin",children:'// build.gradle.kts\nimport com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask\n\nval graphqlGenerateClient by tasks.getting(GraphQLGenerateClientTask::class) {\n  packageName.set("com.example.generated")\n  schemaFileName.set("mySchema.graphql")\n}\n'})})}),(0,r.jsx)(t.A,{value:"groovy",children:(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-groovy",children:'//build.gradle\ngraphqlGenerateClient {\n    packageName = "com.example.generated"\n    schemaFileName = "mySchema.graphql"\n}\n'})})})]}),"\n",(0,r.jsxs)(n.p,{children:["This will process all GraphQL files located under ",(0,r.jsx)(n.code,{children:"src/main/resources"})," and generate corresponding GraphQL Kotlin client\ndata models. Generated classes will be automatically added to the project compile sources."]}),"\n",(0,r.jsx)(n.h2,{id:"generating-client-with-custom-scalars",children:"Generating Client with Custom Scalars"}),"\n",(0,r.jsxs)(n.p,{children:["By default, all custom GraphQL scalars will be serialized as Strings. You can override this default behavior by specifying\ncustom ",(0,r.jsx)(n.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/master/clients/graphql-kotlin-client/src/main/kotlin/com/expediagroup/graphql/client/converter/ScalarConverter.kt",children:"scalar converter"}),"."]}),"\n",(0,r.jsx)(n.p,{children:"For example given following custom scalar in our GraphQL schema"}),"\n",(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-graphql",children:"scalar UUID\n"})}),"\n",(0,r.jsxs)(n.p,{children:["We can create a custom converter to automatically convert this custom scalar to ",(0,r.jsx)(n.code,{children:"java.util.UUID"})]}),"\n",(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-kotlin",children:"package com.example\n\nimport com.expediagroup.graphql.client.converter.ScalarConverter\nimport java.util.UUID\n\nclass UUIDScalarConverter : ScalarConverter<UUID> {\n    override fun toScalar(rawValue: Any): UUID = UUID.fromString(rawValue.toString()\n    override fun toJson(value: UUID): String = value.toString()\n}\n"})}),"\n",(0,r.jsx)(n.p,{children:"Afterwards we need to configure our plugin to use this custom converter"}),"\n",(0,r.jsxs)(i.A,{defaultValue:"kotlin",values:[{label:"Kotlin",value:"kotlin"},{label:"Groovy",value:"groovy"}],children:[(0,r.jsx)(t.A,{value:"kotlin",children:(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-kotlin",children:'// build.gradle.kts\nimport com.expediagroup.graphql.plugin.gradle.config.GraphQLScalar\nimport com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask\n\nval graphqlGenerateClient by tasks.getting(GraphQLGenerateClientTask::class) {\n  packageName.set("com.example.generated")\n  schemaFileName.set("mySchema.graphql")\n  customScalars.add(GraphQLScalar("UUID", "java.util.UUID", "com.example.UUIDScalarConverter"))\n}\n'})})}),(0,r.jsx)(t.A,{value:"groovy",children:(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-groovy",children:'//build.gradle\nimport com.expediagroup.graphql.plugin.gradle.config.GraphQLScalar\n\ngraphqlGenerateClient {\n    packageName = "com.example.generated"\n    schemaFileName = "mySchema.graphql"\n    customScalars.add(new GraphQLScalar("UUID", "java.util.UUID", "com.example.UUIDScalarConverter"))\n}\n'})})})]}),"\n",(0,r.jsx)(n.h2,{id:"generating-client-using-kotlinx-serialization",children:"Generating Client using Kotlinx Serialization"}),"\n",(0,r.jsxs)(n.p,{children:["GraphQL Kotlin plugins default to generate client data models that are compatible with ",(0,r.jsx)(n.a,{href:"https://github.com/FasterXML/jackson",children:"Jackson"}),".\nWe can change this default behavior by explicitly specifying serializer type (in the extension or explicitly in the generate\nclient task) and configuring ",(0,r.jsx)(n.code,{children:"kotlinx.serialization"})," compiler plugin. See ",(0,r.jsx)(n.a,{href:"https://github.com/Kotlin/kotlinx.serialization",children:"kotlinx.serialization documentation"}),"\nfor details."]}),"\n",(0,r.jsxs)(i.A,{defaultValue:"kotlin",values:[{label:"Kotlin",value:"kotlin"},{label:"Groovy",value:"groovy"}],children:[(0,r.jsx)(t.A,{value:"kotlin",children:(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-kotlin",children:'// build.gradle.kts\nimport com.expediagroup.graphql.plugin.gradle.config.GraphQLScalar\nimport com.expediagroup.graphql.plugin.gradle.config.GraphQLSerializer\n\nplugins {\n    kotlin("plugin.serialization") version $kotlinVersion\n}\n\nval graphqlGenerateClient by tasks.getting(GraphQLGenerateClientTask::class) {\n  packageName.set("com.example.generated")\n  schemaFileName.set("mySchema.graphql")\n  serializer.set(GraphQLSerializer.KOTLINX)\n}\n'})})}),(0,r.jsx)(t.A,{value:"groovy",children:(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-groovy",children:"//build.gradle\nimport com.expediagroup.graphql.plugin.gradle.config.GraphQLScalar\nimport com.expediagroup.graphql.plugin.gradle.config.GraphQLSerializer\n\nplugins {\n    id 'org.jetbrains.kotlin.plugin.serialization' version '$kotlinVersion'\n}\n\ngraphqlGenerateClient {\n    packageName = \"com.example.generated\"\n    schemaFileName = \"mySchema.graphql\"\n    serializer = GraphQLSerializer.KOTLINX\n}\n"})})})]}),"\n",(0,r.jsx)(n.h2,{id:"generating-test-client",children:"Generating Test Client"}),"\n",(0,r.jsxs)(n.p,{children:["GraphQL Kotlin test client code is generated based on the provided queries that will be executed against target GraphQL ",(0,r.jsx)(n.code,{children:"schemaFile"}),".\nSeparate class is generated for each provided GraphQL query and are saved under specified ",(0,r.jsx)(n.code,{children:"packageName"}),". When using default\nconfiguration and storing GraphQL queries under ",(0,r.jsx)(n.code,{children:"src/test/resources"})," directories, task can be executed directly from the\ncommand line by explicitly providing required properties."]}),"\n",(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-shell",metastring:"script",children:'$ gradle graphqlGenerateTestClient --schemaFileName"mySchema.graphql" --packageName="com.example.generated"\n'})}),"\n",(0,r.jsx)(n.p,{children:"Task can also be explicitly configured in your Gradle build file"}),"\n",(0,r.jsxs)(i.A,{defaultValue:"kotlin",values:[{label:"Kotlin",value:"kotlin"},{label:"Groovy",value:"groovy"}],children:[(0,r.jsx)(t.A,{value:"kotlin",children:(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-kotlin",children:'// build.gradle.kts\nimport com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask\n\nval graphqlGenerateTestClient by tasks.getting(GraphQLGenerateTestClientTask::class) {\n  packageName.set("com.example.generated")\n  schemaFileName.set("mySchema.graphql")\n}\n'})})}),(0,r.jsx)(t.A,{value:"groovy",children:(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-groovy",children:'//build.gradle\ngraphqlGenerateTestClient {\n    packageName = "com.example.generated"\n    schemaFileName = "mySchema.graphql"\n}\n'})})})]}),"\n",(0,r.jsxs)(n.p,{children:["This will process all GraphQL queries located under ",(0,r.jsx)(n.code,{children:"src/test/resources"})," and generate corresponding GraphQL Kotlin clients.\nGenerated classes will be automatically added to the project test compile sources."]}),"\n",(0,r.jsx)(n.admonition,{type:"info",children:(0,r.jsxs)(n.p,{children:[(0,r.jsx)(n.code,{children:"graphqlGenerateTestClient"})," cannot be configured through the ",(0,r.jsx)(n.code,{children:"graphql"})," extension and has to be explicitly configured."]})}),"\n",(0,r.jsx)(n.h2,{id:"minimal-client-configuration-example",children:"Minimal Client Configuration Example"}),"\n",(0,r.jsxs)(n.p,{children:["Following is the minimal configuration that runs introspection query against a target GraphQL server and generates a\ncorresponding schema. This generated schema is subsequently used to generate GraphQL client code based on the queries\nprovided under ",(0,r.jsx)(n.code,{children:"src/main/resources"})," directory."]}),"\n",(0,r.jsxs)(i.A,{defaultValue:"kotlin",values:[{label:"Kotlin",value:"kotlin"},{label:"Groovy",value:"groovy"}],children:[(0,r.jsxs)(t.A,{value:"kotlin",children:[(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-kotlin",children:'// build.gradle.kts\nimport com.expediagroup.graphql.plugin.gradle.graphql\n\ngraphql {\n  client {\n    endpoint = "http://localhost:8080/graphql"\n    packageName = "com.example.generated"\n  }\n}\n'})}),(0,r.jsx)(n.p,{children:"Above configuration is equivalent to the following"}),(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-kotlin",children:'// build.gradle.kts\nimport com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask\nimport com.expediagroup.graphql.plugin.gradle.tasks.GraphQLIntrospectSchemaTask\n\nval graphqlIntrospectSchema by tasks.getting(GraphQLIntrospectSchemaTask::class) {\n  endpoint.set("http://localhost:8080/graphql")\n}\nval graphqlGenerateClient by tasks.getting(GraphQLGenerateClientTask::class) {\n  packageName.set("com.example.generated")\n  schemaFile.set(graphqlIntrospectSchema.outputFile)\n  dependsOn("graphqlIntrospectSchema")\n}\n'})})]}),(0,r.jsxs)(t.A,{value:"groovy",children:[(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-groovy",children:'graphql {\n    client {\n        endpoint = "http://localhost:8080/graphql"\n        packageName = "com.example.generated"\n    }\n}\n'})}),(0,r.jsx)(n.p,{children:"Above configuration is equivalent to the following"}),(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-groovy",children:'// build.gradle\ngraphqlIntrospectSchema {\n    endpoint = "http://localhost:8080/graphql"\n}\ngraphqlGenerateClient {\n    packageName = "com.example.generated"\n    schemaFile = graphqlIntrospectSchema.outputFile\n    dependsOn graphqlIntrospectSchema\n}\n'})})]})]}),"\n",(0,r.jsx)(n.h2,{id:"complete-client-configuration-example",children:"Complete Client Configuration Example"}),"\n",(0,r.jsxs)(n.p,{children:["Following is a configuration example that downloads schema SDL from a target GraphQL server that is then used to generate\nthe GraphQL client data models using ",(0,r.jsx)(n.code,{children:"kotlinx.serialization"})," that are based on the provided query."]}),"\n",(0,r.jsxs)(i.A,{defaultValue:"kotlin",values:[{label:"Kotlin",value:"kotlin"},{label:"Groovy",value:"groovy"}],children:[(0,r.jsxs)(t.A,{value:"kotlin",children:[(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-kotlin",children:'// build.gradle.kts\nimport com.expediagroup.graphql.plugin.gradle.config.GraphQLScalar\nimport com.expediagroup.graphql.plugin.gradle.config.GraphQLSerializer\nimport com.expediagroup.graphql.plugin.gradle.graphql\n\nplugins {\n    kotlin("plugin.serialization") version $kotlinVersion\n}\n\ngraphql {\n  client {\n    sdlEndpoint = "http://localhost:8080/sdl"\n    packageName = "com.example.generated"\n    // optional configuration\n    allowDeprecatedFields = true\n    customScalars = listOf(GraphQLScalar("UUID", "java.util.UUID", "com.example.UUIDScalarConverter"))\n    headers = mapOf("X-Custom-Header" to "My-Custom-Header")\n    queryFiles = listOf(file("${project.projectDir}/src/main/resources/queries/MyQuery.graphql"))\n    serializer = GraphQLSerializer.KOTLINX\n    timeout {\n        connect = 10_000\n        read = 30_000\n    }\n  }\n}\n'})}),(0,r.jsx)(n.p,{children:"Above configuration is equivalent to the following"}),(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-kotlin",children:'// build.gradle.kts\nimport com.expediagroup.graphql.plugin.gradle.config.GraphQLScalar\nimport com.expediagroup.graphql.plugin.gradle.config.GraphQLSerializer\nimport com.expediagroup.graphql.plugin.gradle.config.TimeoutConfiguration\nimport com.expediagroup.graphql.plugin.gradle.tasks.GraphQLDownloadSDLTask\nimport com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask\n\nplugins {\n    kotlin("plugin.serialization") version $kotlinVersion\n}\n\nval graphqlDownloadSDL by tasks.getting(GraphQLDownloadSDLTask::class) {\n    endpoint.set("http://localhost:8080/sdl")\n    headers.put("X-Custom-Header", "My-Custom-Header")\n    timeoutConfig.set(TimeoutConfiguration(connect = 10_000, read = 30_000))\n}\nval graphqlGenerateClient by tasks.getting(GraphQLGenerateClientTask::class) {\n    packageName.set("com.example.generated")\n    schemaFile.set(graphqlDownloadSDL.outputFile)\n    // optional\n    allowDeprecatedFields.set(true)\n    customScalars.add(GraphQLScalar("UUID", "java.util.UUID", "com.example.UUIDScalarConverter"))\n    queryFiles.from("${project.projectDir}/src/main/resources/queries/MyQuery.graphql")\n    serializer.set(GraphQLSerializer.KOTLINX)\n\n    dependsOn("graphqlDownloadSDL")\n}\n'})})]}),(0,r.jsxs)(t.A,{value:"groovy",children:[(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-groovy",children:'// build.gradle\nimport com.expediagroup.graphql.plugin.gradle.config.GraphQLScalar\nimport com.expediagroup.graphql.plugin.gradle.config.GraphQLSerializer\n\nplugins {\n    id \'org.jetbrains.kotlin.plugin.serialization\' version \'$kotlinVersion\'\n}\n\ngraphql {\n    client {\n        sdlEndpoint = "http://localhost:8080/sdl"\n        packageName = "com.example.generated"\n        // optional configuration\n        allowDeprecatedFields = true\n        customScalars = [new GraphQLScalar("UUID", "java.util.UUID", "com.example.UUIDScalarConverter")]\n        headers = ["X-Custom-Header" : "My-Custom-Header"]\n        queryFiles = [file("${project.projectDir}/src/main/resources/queries/MyQuery.graphql")]\n        serializer = GraphQLSerializer.KOTLINX\n        timeout { t ->\n            t.connect = 10000\n            t.read = 30000\n        }\n    }\n}\n'})}),(0,r.jsx)(n.p,{children:"Above configuration is equivalent to the following"}),(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-groovy",children:'//build.gradle\nimport com.expediagroup.graphql.plugin.gradle.config.GraphQLScalar\nimport com.expediagroup.graphql.plugin.gradle.config.GraphQLSerializer\nimport com.expediagroup.graphql.plugin.gradle.config.TimeoutConfiguration\n\nplugins {\n    id \'org.jetbrains.kotlin.plugin.serialization\' version \'$kotlinVersion\'\n}\n\ngraphqlDownloadSDL {\n    endpoint = "http://localhost:8080/sdl"\n    headers["X-Custom-Header"] = "My-Custom-Header"\n    timeoutConfig = new TimeoutConfiguration(10000, 30000)\n}\ngraphqlGenerateClient {\n    packageName = "com.example.generated"\n    schemaFile = graphqlDownloadSDL.outputFile\n    // optional\n    allowDeprecatedFields = true\n    customScalars.add(new GraphQLScalar("UUID", "java.util.UUID", "com.example.UUIDScalarConverter"))\n    queryFiles.from("${project.projectDir}/src/main/resources/queries/MyQuery.graphql")\n    serializer = GraphQLSerializer.KOTLINX\n\n    dependsOn graphqlDownloadSDL\n}\n'})})]})]}),"\n",(0,r.jsx)(n.h2,{id:"generating-multiple-clients",children:"Generating Multiple Clients"}),"\n",(0,r.jsx)(n.p,{children:"GraphQL Kotlin Gradle Plugin registers tasks for generation of a client queries targeting single GraphQL endpoint. You\ncan generate queries targeting additional endpoints by explicitly creating and configuring additional tasks."}),"\n",(0,r.jsxs)(i.A,{defaultValue:"kotlin",values:[{label:"Kotlin",value:"kotlin"},{label:"Groovy",value:"groovy"}],children:[(0,r.jsx)(t.A,{value:"kotlin",children:(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-kotlin",children:'// build.gradle.kts\nimport com.expediagroup.graphql.plugin.gradle.tasks.GraphQLDownloadSDLTask\nimport com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask\n\nval graphqlDownloadSDL by tasks.getting(GraphQLDownloadSDLTask::class) {\n    endpoint.set("http://localhost:8080/sdl")\n}\nval graphqlGenerateClient by tasks.getting(GraphQLGenerateClientTask::class) {\n    packageName.set("com.example.generated.first")\n    schemaFile.set(graphqlDownloadSDL.outputFile)\n    queryFiles.from("${project.projectDir}/src/main/resources/queries/MyFirstQuery.graphql")\n    dependsOn("graphqlDownloadSDL")\n}\n\nval graphqlDownloadOtherSDL by tasks.creating(GraphQLDownloadSDLTask::class) {\n    endpoint.set("http://localhost:8081/sdl")\n}\nval graphqlGenerateOtherClient by tasks.creating(GraphQLGenerateClientTask::class) {\n    packageName.set("com.example.generated.second")\n    schemaFile.set(graphqlDownloadOtherSDL.outputFile)\n    queryFiles.from("${project.projectDir}/src/main/resources/queries/MySecondQuery.graphql")\n    dependsOn("graphqlDownloadOtherSDL")\n}\n'})})}),(0,r.jsx)(t.A,{value:"groovy",children:(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-groovy",children:'//build.gradle\nimport com.expediagroup.graphql.plugin.gradle.tasks.GraphQLDownloadSDLTask\nimport com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask\n\ngraphqlDownloadSDL {\n    endpoint = "http://localhost:8080/sdl"\n}\ngraphqlGenerateClient {\n    packageName = "com.example.generated.first"\n    schemaFile = graphqlDownloadSDL.outputFile\n    queryFiles.from("${project.projectDir}/src/main/resources/queries/MyFirstQuery.graphql")\n\n    dependsOn graphqlDownloadSDL\n}\n\ntask graphqlDownloadOtherSDL(type: GraphQLDownloadSDLTask) {\n    endpoint = "http://localhost:8081/sdl"\n}\ntask graphqlGenerateOtherClient(type: GraphQLGenerateClientTask) {\n    packageName = "com.other.generated.second"\n    schemaFile = graphqlDownloadOtherSDL.outputFile\n    queryFiles.from("${project.projectDir}/src/main/resources/queries/MySecondQuery.graphql")\n\n    dependsOn graphqlDownloadOtherSDL\n}\n'})})})]}),"\n",(0,r.jsx)(n.h2,{id:"generating-sdl-example",children:"Generating SDL Example"}),"\n",(0,r.jsxs)(n.p,{children:["GraphQL schema can be generated directly from your source code using reflections. ",(0,r.jsx)(n.code,{children:"graphqlGenerateSDL"})," will scan your\nclasspath looking for classes implementing ",(0,r.jsx)(n.code,{children:"Query"}),", ",(0,r.jsx)(n.code,{children:"Mutation"})," and ",(0,r.jsx)(n.code,{children:"Subscription"})," marker interfaces and then generates the\ncorresponding GraphQL schema using ",(0,r.jsx)(n.code,{children:"graphql-kotlin-schema-generator"})," and default ",(0,r.jsx)(n.code,{children:"NoopSchemaGeneratorHooks"}),". In order to\nlimit the amount of packages to scan, this task requires users to provide a list of ",(0,r.jsx)(n.code,{children:"packages"})," that can contain GraphQL\ntypes."]}),"\n",(0,r.jsxs)(i.A,{defaultValue:"kotlin",values:[{label:"Kotlin",value:"kotlin"},{label:"Groovy",value:"groovy"}],children:[(0,r.jsxs)(t.A,{value:"kotlin",children:[(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-kotlin",children:'// build.gradle.kts\nimport com.expediagroup.graphql.plugin.gradle.graphql\n\ngraphql {\n  schema {\n    packages = listOf("com.example")\n  }\n}\n'})}),(0,r.jsx)(n.p,{children:"Above configuration is equivalent to the following task definition"}),(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-kotlin",children:'// build.gradle.kts\nimport com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateSDLTask\n\nval graphqlGenerateSDL by tasks.getting(GraphQLGenerateSDLTask::class) {\n    packages.set(listOf("com.example"))\n}\n'})})]}),(0,r.jsxs)(t.A,{value:"groovy",children:[(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-groovy",children:'// build.gradle\ngraphql {\n  schema {\n    packages = ["com.example"]\n  }\n}\n'})}),(0,r.jsx)(n.p,{children:"Above configuration is equivalent to the following task definition"}),(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-groovy",children:'//build.gradle\ngraphqlGenerateSDL {\n    packages = ["com.example"]\n}\n'})})]})]}),"\n",(0,r.jsx)(n.admonition,{type:"info",children:(0,r.jsx)(n.p,{children:"This task does not automatically configure itself to be part of your build lifecycle. You will need to explicitly\ninvoke it OR configure it as a dependency of some other task."})}),"\n",(0,r.jsx)(n.h2,{id:"generating-sdl-with-custom-hooks-provider-example",children:"Generating SDL with Custom Hooks Provider Example"}),"\n",(0,r.jsxs)(n.p,{children:["Plugin will default to use ",(0,r.jsx)(n.code,{children:"NoopSchemaGeneratorHooks"})," to generate target GraphQL schema. If your project uses custom hooks\nor needs to generate the federated GraphQL schema, you will need to provide an instance of ",(0,r.jsx)(n.code,{children:"SchemaGeneratorHooksProvider"}),"\nservice provider that will be used to create an instance of your custom hooks."]}),"\n",(0,r.jsxs)(n.p,{children:[(0,r.jsx)(n.code,{children:"graphqlGenerateSDL"})," utilizes ",(0,r.jsx)(n.a,{href:"https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/ServiceLoader.html",children:"ServiceLoader"}),"\nmechanism to dynamically load available ",(0,r.jsx)(n.code,{children:"SchemaGeneratorHooksProvider"})," service providers from the classpath. Service provider\ncan be provided as part of your project, included in one of your project dependencies or through explicitly provided artifact.\nSee ",(0,r.jsx)(n.a,{href:"/graphql-kotlin/docs/4.x.x/plugins/hooks-provider",children:"Schema Generator Hooks Provider"})," for additional details on how to create custom hooks service provider.\nConfiguration below shows how to configure GraphQL Kotlin plugin with explicitly provided artifact to generate federated\nGraphQL schema."]}),"\n",(0,r.jsxs)(i.A,{defaultValue:"kotlin",values:[{label:"Kotlin",value:"kotlin"},{label:"Groovy",value:"groovy"}],children:[(0,r.jsxs)(t.A,{value:"kotlin",children:[(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-kotlin",children:'// build.gradle.kts\nimport com.expediagroup.graphql.plugin.gradle.graphql\n\ngraphql {\n  schema {\n    packages = listOf("com.example")\n  }\n}\n\ndependencies {\n    graphqlSDL("com.expediagroup:graphql-kotlin-federated-hooks-provider:$graphQLKotlinVersion")\n}\n'})}),(0,r.jsx)(n.p,{children:"Above configuration is equivalent to the following task definition"}),(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-kotlin",children:'// build.gradle.kts\nimport com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateSDLTask\n\nval graphqlGenerateSDL by tasks.getting(GraphQLGenerateSDLTask::class) {\n    packages.set(listOf("com.example"))\n}\n\ndependencies {\n    graphqlSDL("com.expediagroup:graphql-kotlin-federated-hooks-provider:$graphQLKotlinVersion")\n}\n'})})]}),(0,r.jsxs)(t.A,{value:"groovy",children:[(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-groovy",children:'// build.gradle\ngraphql {\n  schema {\n    packages = ["com.example"]\n  }\n}\n\ndependencies {\n    graphqlSDL "com.expediagroup:graphql-kotlin-federated-hooks-provider:$DEFAULT_PLUGIN_VERSION"\n}\n'})}),(0,r.jsx)(n.p,{children:"Above configuration is equivalent to the following task definition"}),(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-groovy",children:'//build.gradle\ngraphqlGenerateSDL {\n    packages = ["com.example"]\n}\n\ndependencies {\n    graphqlSDL "com.expediagroup:graphql-kotlin-federated-hooks-provider:$DEFAULT_PLUGIN_VERSION"\n}\n'})})]})]}),"\n",(0,r.jsx)(n.admonition,{type:"info",children:(0,r.jsx)(n.p,{children:"This task does not automatically configure itself to be part of your build lifecycle. You will need to explicitly\ninvoke it OR configure it as a dependency of some other task."})})]})}function u(e={}){const{wrapper:n}={...(0,l.R)(),...e.components};return n?(0,r.jsx)(n,{...e,children:(0,r.jsx)(g,{...e})}):g(e)}},19365:(e,n,a)=>{a.d(n,{A:()=>t});a(96540);var r=a(34164);const l={tabItem:"tabItem_Ymn6"};var i=a(74848);function t(e){var n=e.children,a=e.hidden,t=e.className;return(0,i.jsx)("div",{role:"tabpanel",className:(0,r.A)(l.tabItem,t),hidden:a,children:n})}},11470:(e,n,a)=>{a.d(n,{A:()=>b});var r=a(96540),l=a(34164),i=a(23104),t=a(56347),o=a(205),s=a(57485),c=a(31682),d=a(70679);function p(e){var n,a;return null!=(n=null==(a=r.Children.toArray(e).filter((function(e){return"\n"!==e})).map((function(e){if(!e||(0,r.isValidElement)(e)&&((n=e.props)&&"object"==typeof n&&"value"in n))return e;var n;throw new Error("Docusaurus error: Bad <Tabs> child <"+("string"==typeof e.type?e.type:e.type.name)+'>: all children of the <Tabs> component should be <TabItem>, and every <TabItem> should have a unique "value" prop.')})))?void 0:a.filter(Boolean))?n:[]}function g(e){var n=e.values,a=e.children;return(0,r.useMemo)((function(){var e=null!=n?n:function(e){return p(e).map((function(e){var n=e.props;return{value:n.value,label:n.label,attributes:n.attributes,default:n.default}}))}(a);return function(e){var n=(0,c.X)(e,(function(e,n){return e.value===n.value}));if(n.length>0)throw new Error('Docusaurus error: Duplicate values "'+n.map((function(e){return e.value})).join(", ")+'" found in <Tabs>. Every value needs to be unique.')}(e),e}),[n,a])}function u(e){var n=e.value;return e.tabValues.some((function(e){return e.value===n}))}function h(e){var n=e.queryString,a=void 0!==n&&n,l=e.groupId,i=(0,t.W6)(),o=function(e){var n=e.queryString,a=void 0!==n&&n,r=e.groupId;if("string"==typeof a)return a;if(!1===a)return null;if(!0===a&&!r)throw new Error('Docusaurus error: The <Tabs> component groupId prop is required if queryString=true, because this value is used as the search param name. You can also provide an explicit value such as queryString="my-search-param".');return null!=r?r:null}({queryString:a,groupId:l});return[(0,s.aZ)(o),(0,r.useCallback)((function(e){if(o){var n=new URLSearchParams(i.location.search);n.set(o,e),i.replace(Object.assign({},i.location,{search:n.toString()}))}}),[o,i])]}function m(e){var n,a,l,i,t=e.defaultValue,s=e.queryString,c=void 0!==s&&s,p=e.groupId,m=g(e),x=(0,r.useState)((function(){return function(e){var n,a=e.defaultValue,r=e.tabValues;if(0===r.length)throw new Error("Docusaurus error: the <Tabs> component requires at least one <TabItem> children component");if(a){if(!u({value:a,tabValues:r}))throw new Error('Docusaurus error: The <Tabs> has a defaultValue "'+a+'" but none of its children has the corresponding value. Available values are: '+r.map((function(e){return e.value})).join(", ")+". If you intend to show no default tab, use defaultValue={null} instead.");return a}var l=null!=(n=r.find((function(e){return e.default})))?n:r[0];if(!l)throw new Error("Unexpected error: 0 tabValues");return l.value}({defaultValue:t,tabValues:m})})),v=x[0],f=x[1],k=h({queryString:c,groupId:p}),j=k[0],y=k[1],b=(n=function(e){return e?"docusaurus.tab."+e:null}({groupId:p}.groupId),a=(0,d.Dv)(n),l=a[0],i=a[1],[l,(0,r.useCallback)((function(e){n&&i.set(e)}),[n,i])]),q=b[0],G=b[1],L=function(){var e=null!=j?j:q;return u({value:e,tabValues:m})?e:null}();return(0,o.A)((function(){L&&f(L)}),[L]),{selectedValue:v,selectValue:(0,r.useCallback)((function(e){if(!u({value:e,tabValues:m}))throw new Error("Can't select invalid tab value="+e);f(e),y(e),G(e)}),[y,G,m]),tabValues:m}}var x=a(92303);const v={tabList:"tabList__CuJ",tabItem:"tabItem_LNqP"};var f=a(74848);function k(e){var n=e.className,a=e.block,r=e.selectedValue,t=e.selectValue,o=e.tabValues,s=[],c=(0,i.a_)().blockElementScrollPositionUntilNextRender,d=function(e){var n=e.currentTarget,a=s.indexOf(n),l=o[a].value;l!==r&&(c(n),t(l))},p=function(e){var n,a=null;switch(e.key){case"Enter":d(e);break;case"ArrowRight":var r,l=s.indexOf(e.currentTarget)+1;a=null!=(r=s[l])?r:s[0];break;case"ArrowLeft":var i,t=s.indexOf(e.currentTarget)-1;a=null!=(i=s[t])?i:s[s.length-1]}null==(n=a)||n.focus()};return(0,f.jsx)("ul",{role:"tablist","aria-orientation":"horizontal",className:(0,l.A)("tabs",{"tabs--block":a},n),children:o.map((function(e){var n=e.value,a=e.label,i=e.attributes;return(0,f.jsx)("li",Object.assign({role:"tab",tabIndex:r===n?0:-1,"aria-selected":r===n,ref:function(e){return s.push(e)},onKeyDown:p,onClick:d},i,{className:(0,l.A)("tabs__item",v.tabItem,null==i?void 0:i.className,{"tabs__item--active":r===n}),children:null!=a?a:n}),n)}))})}function j(e){var n=e.lazy,a=e.children,l=e.selectedValue,i=(Array.isArray(a)?a:[a]).filter(Boolean);if(n){var t=i.find((function(e){return e.props.value===l}));return t?(0,r.cloneElement)(t,{className:"margin-top--md"}):null}return(0,f.jsx)("div",{className:"margin-top--md",children:i.map((function(e,n){return(0,r.cloneElement)(e,{key:n,hidden:e.props.value!==l})}))})}function y(e){var n=m(e);return(0,f.jsxs)("div",{className:(0,l.A)("tabs-container",v.tabList),children:[(0,f.jsx)(k,Object.assign({},n,e)),(0,f.jsx)(j,Object.assign({},n,e))]})}function b(e){var n=(0,x.A)();return(0,f.jsx)(y,Object.assign({},e,{children:p(e.children)}),String(n))}},28453:(e,n,a)=>{a.d(n,{R:()=>t,x:()=>o});var r=a(96540);const l={},i=r.createContext(l);function t(e){const n=r.useContext(i);return r.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function o(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(l):e.components||l:t(e.components),r.createElement(i.Provider,{value:n},e.children)}}}]);