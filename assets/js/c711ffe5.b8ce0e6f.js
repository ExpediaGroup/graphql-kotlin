"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[5865],{89840:(e,n,a)=>{a.r(n),a.d(n,{assets:()=>p,contentTitle:()=>s,default:()=>g,frontMatter:()=>o,metadata:()=>c,toc:()=>d});var l=a(74848),r=a(28453),t=a(11470),i=a(19365);const o={id:"gradle-plugin-usage-client",title:"Gradle Plugin Client Usage",sidebar_label:"Generating Client"},s=void 0,c={id:"plugins/gradle-plugin-usage-client",title:"Gradle Plugin Client Usage",description:"GraphQL Kotlin plugins can be used to generate a lightweight type-safe GraphQL HTTP clients. See examples below for more",source:"@site/docs/plugins/gradle-plugin-usage-client.mdx",sourceDirName:"plugins",slug:"/plugins/gradle-plugin-usage-client",permalink:"/graphql-kotlin/docs/8.x.x/plugins/gradle-plugin-usage-client",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/docs/plugins/gradle-plugin-usage-client.mdx",tags:[],version:"current",lastUpdatedBy:"Dariusz Kuc",lastUpdatedAt:1713385577,formattedLastUpdatedAt:"Apr 17, 2024",frontMatter:{id:"gradle-plugin-usage-client",title:"Gradle Plugin Client Usage",sidebar_label:"Generating Client"},sidebar:"docs",previous:{title:"Tasks Overiew",permalink:"/graphql-kotlin/docs/8.x.x/plugins/gradle-plugin-tasks"},next:{title:"Generating SDL",permalink:"/graphql-kotlin/docs/8.x.x/plugins/gradle-plugin-usage-sdl"}},p={},d=[{value:"Downloading Schema SDL",id:"downloading-schema-sdl",level:2},{value:"Introspecting Schema",id:"introspecting-schema",level:2},{value:"Generating Client",id:"generating-client",level:2},{value:"Generating Client with Custom Scalars",id:"generating-client-with-custom-scalars",level:2},{value:"Generating Client using Kotlinx Serialization",id:"generating-client-using-kotlinx-serialization",level:2},{value:"Generating Client using Classpath Schema",id:"generating-client-using-classpath-schema",level:2},{value:"Generating Test Client",id:"generating-test-client",level:2},{value:"Minimal Client Configuration Example",id:"minimal-client-configuration-example",level:2},{value:"Complete Client Configuration Example",id:"complete-client-configuration-example",level:2},{value:"Generating Multiple Clients",id:"generating-multiple-clients",level:2}];function u(e){const n={a:"a",admonition:"admonition",code:"code",h2:"h2",p:"p",pre:"pre",...(0,r.R)(),...e.components};return(0,l.jsxs)(l.Fragment,{children:[(0,l.jsx)(n.p,{children:"GraphQL Kotlin plugins can be used to generate a lightweight type-safe GraphQL HTTP clients. See examples below for more\ninformation about the client generating tasks."}),"\n",(0,l.jsx)(n.h2,{id:"downloading-schema-sdl",children:"Downloading Schema SDL"}),"\n",(0,l.jsxs)(n.p,{children:["GraphQL endpoints are often public and as such many servers might disable introspection queries in production environment.\nSince GraphQL schema is needed to generate type safe clients, as alternative GraphQL servers might expose private\nendpoints (e.g. accessible only from within network, etc) that could be used to download schema in Schema Definition\nLanguage (SDL) directly. ",(0,l.jsx)(n.code,{children:"graphqlDownloadSDL"})," task requires target GraphQL server ",(0,l.jsx)(n.code,{children:"endpoint"})," to be specified and can\nbe executed directly from the command line by explicitly passing ",(0,l.jsx)(n.code,{children:"endpoint"})," parameter"]}),"\n",(0,l.jsx)(n.pre,{children:(0,l.jsx)(n.code,{className:"language-shell",metastring:"script",children:'$ gradle graphqlDownloadSDL --endpoint="http://localhost:8080/sdl"\n'})}),"\n",(0,l.jsx)(n.p,{children:"Task can also be explicitly configured in your Gradle build file"}),"\n",(0,l.jsxs)(t.A,{defaultValue:"kotlin",values:[{label:"Kotlin",value:"kotlin"},{label:"Groovy",value:"groovy"}],children:[(0,l.jsx)(i.A,{value:"kotlin",children:(0,l.jsx)(n.pre,{children:(0,l.jsx)(n.code,{className:"language-kotlin",children:'// build.gradle.kts\nimport com.expediagroup.graphql.plugin.gradle.tasks.GraphQLDownloadSDLTask\n\nval graphqlDownloadSDL by tasks.getting(GraphQLDownloadSDLTask::class) {\n    endpoint.set("http://localhost:8080/sdl")\n}\n'})})}),(0,l.jsx)(i.A,{value:"groovy",children:(0,l.jsx)(n.pre,{children:(0,l.jsx)(n.code,{className:"language-groovy",children:'//build.gradle\ngraphqlDownloadSDL {\n    endpoint = "http://localhost:8080/sdl"\n}\n'})})})]}),"\n",(0,l.jsx)(n.admonition,{type:"info",children:(0,l.jsx)(n.p,{children:"This task does not automatically configure itself to be part of your build lifecycle. You will need to explicitly\ninvoke it OR configure it as a dependency of some other task."})}),"\n",(0,l.jsx)(n.h2,{id:"introspecting-schema",children:"Introspecting Schema"}),"\n",(0,l.jsxs)(n.p,{children:["Introspection task requires target GraphQL server ",(0,l.jsx)(n.code,{children:"endpoint"})," to be specified. Task can be executed directly from the\ncommand line by explicitly passing endpoint parameter"]}),"\n",(0,l.jsx)(n.pre,{children:(0,l.jsx)(n.code,{className:"language-shell",metastring:"script",children:'$ gradle graphqlIntrospectSchema --endpoint="http://localhost:8080/graphql"\n'})}),"\n",(0,l.jsx)(n.p,{children:"Task can also be explicitly configured in your Gradle build file"}),"\n",(0,l.jsxs)(t.A,{defaultValue:"kotlin",values:[{label:"Kotlin",value:"kotlin"},{label:"Groovy",value:"groovy"}],children:[(0,l.jsx)(i.A,{value:"kotlin",children:(0,l.jsx)(n.pre,{children:(0,l.jsx)(n.code,{className:"language-kotlin",children:'// build.gradle.kts\nimport com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask\n\nval graphqlIntrospectSchema by tasks.getting(GraphQLIntrospectSchemaTask::class) {\n    endpoint.set("http://localhost:8080/graphql")\n}\n'})})}),(0,l.jsx)(i.A,{value:"groovy",children:(0,l.jsx)(n.pre,{children:(0,l.jsx)(n.code,{className:"language-groovy",children:'//build.gradle\ngraphqlIntrospectSchema {\n    endpoint = "http://localhost:8080/graphql"\n}\n'})})})]}),"\n",(0,l.jsx)(n.admonition,{type:"info",children:(0,l.jsx)(n.p,{children:"This task does not automatically configure itself to be part of your build lifecycle. You will need to explicitly\ninvoke it OR configure it as a dependency of some other task."})}),"\n",(0,l.jsx)(n.h2,{id:"generating-client",children:"Generating Client"}),"\n",(0,l.jsxs)(n.p,{children:["GraphQL Kotlin client code is generated based on the provided queries that will be executed against target GraphQL ",(0,l.jsx)(n.code,{children:"schemaFile"}),".\nSeparate class is generated for each provided GraphQL query and are saved under specified ",(0,l.jsx)(n.code,{children:"packageName"}),"."]}),"\n",(0,l.jsxs)(t.A,{defaultValue:"kotlin",values:[{label:"Kotlin",value:"kotlin"},{label:"Groovy",value:"groovy"}],children:[(0,l.jsx)(i.A,{value:"kotlin",children:(0,l.jsx)(n.pre,{children:(0,l.jsx)(n.code,{className:"language-kotlin",children:'// build.gradle.kts\nimport com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask\n\nval graphqlGenerateClient by tasks.getting(GraphQLGenerateClientTask::class) {\n  packageName.set("com.example.generated")\n  schemaFile.set(file("${project.projectDir}/mySchema.graphql"))\n}\n'})})}),(0,l.jsx)(i.A,{value:"groovy",children:(0,l.jsx)(n.pre,{children:(0,l.jsx)(n.code,{className:"language-groovy",children:'//build.gradle\ngraphqlGenerateClient {\n    packageName = "com.example.generated"\n    schemaFile = file("${project.projectDir}/mySchema.graphql")\n}\n'})})})]}),"\n",(0,l.jsxs)(n.p,{children:["This will process all GraphQL files located under ",(0,l.jsx)(n.code,{children:"src/main/resources"})," and generate corresponding GraphQL Kotlin client\ndata models. Generated classes will be automatically added to the project compile sources."]}),"\n",(0,l.jsx)(n.h2,{id:"generating-client-with-custom-scalars",children:"Generating Client with Custom Scalars"}),"\n",(0,l.jsxs)(n.p,{children:["By default, all custom GraphQL scalars will be serialized as Strings. You can override this default behavior by specifying\ncustom ",(0,l.jsx)(n.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/master/clients/graphql-kotlin-client/src/main/kotlin/com/expediagroup/graphql/client/converter/ScalarConverter.kt",children:"scalar converter"}),"."]}),"\n",(0,l.jsx)(n.p,{children:"For example given following custom scalar in our GraphQL schema"}),"\n",(0,l.jsx)(n.pre,{children:(0,l.jsx)(n.code,{className:"language-graphql",children:"scalar UUID\n"})}),"\n",(0,l.jsxs)(n.p,{children:["We can create a custom converter to automatically convert this custom scalar to ",(0,l.jsx)(n.code,{children:"java.util.UUID"})]}),"\n",(0,l.jsx)(n.pre,{children:(0,l.jsx)(n.code,{className:"language-kotlin",children:"package com.example\n\nimport com.expediagroup.graphql.client.converter.ScalarConverter\nimport java.util.UUID\n\nclass UUIDScalarConverter : ScalarConverter<UUID> {\n    override fun toScalar(rawValue: Any): UUID = UUID.fromString(rawValue.toString()\n    override fun toJson(value: UUID): String = value.toString()\n}\n"})}),"\n",(0,l.jsx)(n.p,{children:"Afterwards we need to configure our plugin to use this custom converter"}),"\n",(0,l.jsxs)(t.A,{defaultValue:"kotlin",values:[{label:"Kotlin",value:"kotlin"},{label:"Groovy",value:"groovy"}],children:[(0,l.jsx)(i.A,{value:"kotlin",children:(0,l.jsx)(n.pre,{children:(0,l.jsx)(n.code,{className:"language-kotlin",children:'// build.gradle.kts\nimport com.expediagroup.graphql.plugin.gradle.config.GraphQLScalar\nimport com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask\n\nval graphqlGenerateClient by tasks.getting(GraphQLGenerateClientTask::class) {\n  packageName.set("com.example.generated")\n  schemaFile.set(file("${project.projectDir}/mySchema.graphql"))\n  customScalars.add(GraphQLScalar("UUID", "java.util.UUID", "com.example.UUIDScalarConverter"))\n}\n'})})}),(0,l.jsx)(i.A,{value:"groovy",children:(0,l.jsx)(n.pre,{children:(0,l.jsx)(n.code,{className:"language-groovy",children:'//build.gradle\nimport com.expediagroup.graphql.plugin.gradle.config.GraphQLScalar\n\ngraphqlGenerateClient {\n    packageName = "com.example.generated"\n    schemaFile = file("${project.projectDir}/mySchema.graphql")\n    customScalars.add(new GraphQLScalar("UUID", "java.util.UUID", "com.example.UUIDScalarConverter"))\n}\n'})})})]}),"\n",(0,l.jsx)(n.h2,{id:"generating-client-using-kotlinx-serialization",children:"Generating Client using Kotlinx Serialization"}),"\n",(0,l.jsxs)(n.p,{children:["GraphQL Kotlin plugins default to generate client data models that are compatible with ",(0,l.jsx)(n.a,{href:"https://github.com/FasterXML/jackson",children:"Jackson"}),".\nWe can change this default behavior by explicitly specifying serializer type (in the extension or explicitly in the generate\nclient task) and configuring ",(0,l.jsx)(n.code,{children:"kotlinx.serialization"})," compiler plugin. See ",(0,l.jsx)(n.a,{href:"https://github.com/Kotlin/kotlinx.serialization",children:"kotlinx.serialization documentation"}),"\nfor details."]}),"\n",(0,l.jsxs)(t.A,{defaultValue:"kotlin",values:[{label:"Kotlin",value:"kotlin"},{label:"Groovy",value:"groovy"}],children:[(0,l.jsx)(i.A,{value:"kotlin",children:(0,l.jsx)(n.pre,{children:(0,l.jsx)(n.code,{className:"language-kotlin",children:'// build.gradle.kts\nimport com.expediagroup.graphql.plugin.gradle.config.GraphQLScalar\nimport com.expediagroup.graphql.plugin.gradle.config.GraphQLSerializer\n\nplugins {\n    kotlin("plugin.serialization") version $kotlinVersion\n}\n\nval graphqlGenerateClient by tasks.getting(GraphQLGenerateClientTask::class) {\n  packageName.set("com.example.generated")\n  schemaFile.set(file("${project.projectDir}/mySchema.graphql"))\n  serializer.set(GraphQLSerializer.KOTLINX)\n}\n'})})}),(0,l.jsx)(i.A,{value:"groovy",children:(0,l.jsx)(n.pre,{children:(0,l.jsx)(n.code,{className:"language-groovy",children:"//build.gradle\nimport com.expediagroup.graphql.plugin.gradle.config.GraphQLScalar\nimport com.expediagroup.graphql.plugin.gradle.config.GraphQLSerializer\n\nplugins {\n    id 'org.jetbrains.kotlin.plugin.serialization' version '$kotlinVersion'\n}\n\ngraphqlGenerateClient {\n    packageName = \"com.example.generated\"\n    schemaFile = file(\"${project.projectDir}/mySchema.graphql\")\n    serializer = GraphQLSerializer.KOTLINX\n}\n"})})})]}),"\n",(0,l.jsx)(n.h2,{id:"generating-client-using-classpath-schema",children:"Generating Client using Classpath Schema"}),"\n",(0,l.jsxs)(n.p,{children:["Client generation tasks require ",(0,l.jsx)(n.code,{children:"schemaFile"})," to be provided. Using Gradle we can configure tasks to use local schema file,\nload it from an URI or consume it directly from a classpath. See ",(0,l.jsx)(n.a,{href:"https://docs.gradle.org/current/dsl/org.gradle.api.resources.TextResourceFactory.html",children:"Gradle TextResourceFactory"}),"\nfor additional details."]}),"\n",(0,l.jsxs)(n.p,{children:["If ",(0,l.jsx)(n.code,{children:"schema.graphql"})," file is provided in a ",(0,l.jsx)(n.code,{children:"my-lib"})," JAR we can configure our generate client task as follows"]}),"\n",(0,l.jsxs)(t.A,{defaultValue:"kotlin",values:[{label:"Kotlin",value:"kotlin"},{label:"Groovy",value:"groovy"}],children:[(0,l.jsx)(i.A,{value:"kotlin",children:(0,l.jsx)(n.pre,{children:(0,l.jsx)(n.code,{className:"language-kotlin",children:'// build.gradle.kts\nval graphqlGenerateClient by tasks.getting(GraphQLGenerateClientTask::class) {\n  packageName.set("com.example.generated")\n  val archive = configurations["compileClasspath"].filter {\n      // filter on the jar name.\n      it.name.startsWith("my-lib")\n  }\n  schemaFile.set(resources.text.fromArchive(archive, "schema.graphql").asFile())\n}\n'})})}),(0,l.jsx)(i.A,{value:"groovy",children:(0,l.jsx)(n.pre,{children:(0,l.jsx)(n.code,{className:"language-groovy",children:'//build.gradle\ngraphqlGenerateClient {\n    packageName = "com.example.generated"\n    val archive = configurations["compileClasspath"].filter {\n        // filter on the jar name.\n        it.name.startsWith("my-lib")\n    }\n    schemaFile = resources.text.fromArchive(archive, "schema.graphql").asFile()\n}\n'})})})]}),"\n",(0,l.jsx)(n.h2,{id:"generating-test-client",children:"Generating Test Client"}),"\n",(0,l.jsxs)(n.p,{children:["GraphQL Kotlin test client code is generated based on the provided queries that will be executed against target GraphQL ",(0,l.jsx)(n.code,{children:"schemaFile"}),".\nSeparate class is generated for each provided GraphQL query and are saved under specified ",(0,l.jsx)(n.code,{children:"packageName"}),"."]}),"\n",(0,l.jsxs)(t.A,{defaultValue:"kotlin",values:[{label:"Kotlin",value:"kotlin"},{label:"Groovy",value:"groovy"}],children:[(0,l.jsx)(i.A,{value:"kotlin",children:(0,l.jsx)(n.pre,{children:(0,l.jsx)(n.code,{className:"language-kotlin",children:'// build.gradle.kts\nimport com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateTestClientTask\n\nval graphqlGenerateTestClient by tasks.getting(GraphQLGenerateTestClientTask::class) {\n  packageName.set("com.example.generated")\n  schemaFile.set(file("${project.projectDir}/mySchema.graphql"))\n}\n'})})}),(0,l.jsx)(i.A,{value:"groovy",children:(0,l.jsx)(n.pre,{children:(0,l.jsx)(n.code,{className:"language-groovy",children:'//build.gradle\ngraphqlGenerateTestClient {\n    packageName = "com.example.generated"\n    schemaFile = file("${project.projectDir}/mySchema.graphql")\n}\n'})})})]}),"\n",(0,l.jsxs)(n.p,{children:["This will process all GraphQL queries located under ",(0,l.jsx)(n.code,{children:"src/test/resources"})," and generate corresponding GraphQL Kotlin clients.\nGenerated classes will be automatically added to the project test compile sources."]}),"\n",(0,l.jsx)(n.admonition,{type:"info",children:(0,l.jsxs)(n.p,{children:[(0,l.jsx)(n.code,{children:"graphqlGenerateTestClient"})," cannot be configured through the ",(0,l.jsx)(n.code,{children:"graphql"})," extension and has to be explicitly configured."]})}),"\n",(0,l.jsx)(n.h2,{id:"minimal-client-configuration-example",children:"Minimal Client Configuration Example"}),"\n",(0,l.jsxs)(n.p,{children:["Following is the minimal configuration that runs introspection query against a target GraphQL server and generates a\ncorresponding schema. This generated schema is subsequently used to generate GraphQL client code based on the queries\nprovided under ",(0,l.jsx)(n.code,{children:"src/main/resources"})," directory."]}),"\n",(0,l.jsxs)(t.A,{defaultValue:"kotlin",values:[{label:"Kotlin",value:"kotlin"},{label:"Groovy",value:"groovy"}],children:[(0,l.jsxs)(i.A,{value:"kotlin",children:[(0,l.jsx)(n.pre,{children:(0,l.jsx)(n.code,{className:"language-kotlin",children:'// build.gradle.kts\nimport com.expediagroup.graphql.plugin.gradle.graphql\n\ngraphql {\n  client {\n    endpoint = "http://localhost:8080/graphql"\n    packageName = "com.example.generated"\n  }\n}\n'})}),(0,l.jsx)(n.p,{children:"Above configuration is equivalent to the following"}),(0,l.jsx)(n.pre,{children:(0,l.jsx)(n.code,{className:"language-kotlin",children:'// build.gradle.kts\nimport com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask\nimport com.expediagroup.graphql.plugin.gradle.tasks.GraphQLIntrospectSchemaTask\n\nval graphqlIntrospectSchema by tasks.getting(GraphQLIntrospectSchemaTask::class) {\n  endpoint.set("http://localhost:8080/graphql")\n}\nval graphqlGenerateClient by tasks.getting(GraphQLGenerateClientTask::class) {\n  packageName.set("com.example.generated")\n  schemaFile.set(graphqlIntrospectSchema.outputFile)\n  dependsOn("graphqlIntrospectSchema")\n}\n'})})]}),(0,l.jsxs)(i.A,{value:"groovy",children:[(0,l.jsx)(n.pre,{children:(0,l.jsx)(n.code,{className:"language-groovy",children:'graphql {\n    client {\n        endpoint = "http://localhost:8080/graphql"\n        packageName = "com.example.generated"\n    }\n}\n'})}),(0,l.jsx)(n.p,{children:"Above configuration is equivalent to the following"}),(0,l.jsx)(n.pre,{children:(0,l.jsx)(n.code,{className:"language-groovy",children:'// build.gradle\ngraphqlIntrospectSchema {\n    endpoint = "http://localhost:8080/graphql"\n}\ngraphqlGenerateClient {\n    packageName = "com.example.generated"\n    schemaFile = graphqlIntrospectSchema.outputFile\n    dependsOn graphqlIntrospectSchema\n}\n'})})]})]}),"\n",(0,l.jsx)(n.h2,{id:"complete-client-configuration-example",children:"Complete Client Configuration Example"}),"\n",(0,l.jsxs)(n.p,{children:["Following is a configuration example that downloads schema SDL from a target GraphQL server that is then used to generate\nthe GraphQL client data models using ",(0,l.jsx)(n.code,{children:"kotlinx.serialization"})," that are based on the provided query."]}),"\n",(0,l.jsxs)(t.A,{defaultValue:"kotlin",values:[{label:"Kotlin",value:"kotlin"},{label:"Groovy",value:"groovy"}],children:[(0,l.jsxs)(i.A,{value:"kotlin",children:[(0,l.jsx)(n.pre,{children:(0,l.jsx)(n.code,{className:"language-kotlin",children:'// build.gradle.kts\nimport com.expediagroup.graphql.plugin.gradle.config.GraphQLScalar\nimport com.expediagroup.graphql.plugin.gradle.config.GraphQLSerializer\nimport com.expediagroup.graphql.plugin.gradle.graphql\n\nplugins {\n    kotlin("plugin.serialization") version $kotlinVersion\n}\n\ngraphql {\n  client {\n    sdlEndpoint = "http://localhost:8080/sdl"\n    packageName = "com.example.generated"\n    // optional configuration\n    allowDeprecatedFields = true\n    customScalars = listOf(GraphQLScalar("UUID", "java.util.UUID", "com.example.UUIDScalarConverter"))\n    headers = mapOf("X-Custom-Header" to "My-Custom-Header")\n    queryFiles = listOf(file("${project.projectDir}/src/main/resources/queries/MyQuery.graphql"))\n    serializer = GraphQLSerializer.KOTLINX\n    timeout {\n        connect = 10_000\n        read = 30_000\n    }\n  }\n}\n'})}),(0,l.jsx)(n.p,{children:"Above configuration is equivalent to the following"}),(0,l.jsx)(n.pre,{children:(0,l.jsx)(n.code,{className:"language-kotlin",children:'// build.gradle.kts\nimport com.expediagroup.graphql.plugin.gradle.config.GraphQLScalar\nimport com.expediagroup.graphql.plugin.gradle.config.GraphQLSerializer\nimport com.expediagroup.graphql.plugin.gradle.config.TimeoutConfiguration\nimport com.expediagroup.graphql.plugin.gradle.tasks.GraphQLDownloadSDLTask\nimport com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask\n\nplugins {\n    kotlin("plugin.serialization") version $kotlinVersion\n}\n\nval graphqlDownloadSDL by tasks.getting(GraphQLDownloadSDLTask::class) {\n    endpoint.set("http://localhost:8080/sdl")\n    headers.put("X-Custom-Header", "My-Custom-Header")\n    timeoutConfig.set(TimeoutConfiguration(connect = 10_000, read = 30_000))\n}\nval graphqlGenerateClient by tasks.getting(GraphQLGenerateClientTask::class) {\n    packageName.set("com.example.generated")\n    schemaFile.set(graphqlDownloadSDL.outputFile)\n    // optional\n    allowDeprecatedFields.set(true)\n    customScalars.add(GraphQLScalar("UUID", "java.util.UUID", "com.example.UUIDScalarConverter"))\n    queryFiles.from("${project.projectDir}/src/main/resources/queries/MyQuery.graphql")\n    serializer.set(GraphQLSerializer.KOTLINX)\n\n    dependsOn("graphqlDownloadSDL")\n}\n'})})]}),(0,l.jsxs)(i.A,{value:"groovy",children:[(0,l.jsx)(n.pre,{children:(0,l.jsx)(n.code,{className:"language-groovy",children:'// build.gradle\nimport com.expediagroup.graphql.plugin.gradle.config.GraphQLScalar\nimport com.expediagroup.graphql.plugin.gradle.config.GraphQLSerializer\n\nplugins {\n    id \'org.jetbrains.kotlin.plugin.serialization\' version \'$kotlinVersion\'\n}\n\ngraphql {\n    client {\n        sdlEndpoint = "http://localhost:8080/sdl"\n        packageName = "com.example.generated"\n        // optional configuration\n        allowDeprecatedFields = true\n        customScalars = [new GraphQLScalar("UUID", "java.util.UUID", "com.example.UUIDScalarConverter")]\n        headers = ["X-Custom-Header" : "My-Custom-Header"]\n        queryFiles = [file("${project.projectDir}/src/main/resources/queries/MyQuery.graphql")]\n        serializer = GraphQLSerializer.KOTLINX\n        timeout { t ->\n            t.connect = 10000\n            t.read = 30000\n        }\n    }\n}\n'})}),(0,l.jsx)(n.p,{children:"Above configuration is equivalent to the following"}),(0,l.jsx)(n.pre,{children:(0,l.jsx)(n.code,{className:"language-groovy",children:'//build.gradle\nimport com.expediagroup.graphql.plugin.gradle.config.GraphQLScalar\nimport com.expediagroup.graphql.plugin.gradle.config.GraphQLSerializer\nimport com.expediagroup.graphql.plugin.gradle.config.TimeoutConfiguration\n\nplugins {\n    id \'org.jetbrains.kotlin.plugin.serialization\' version \'$kotlinVersion\'\n}\n\ngraphqlDownloadSDL {\n    endpoint = "http://localhost:8080/sdl"\n    headers["X-Custom-Header"] = "My-Custom-Header"\n    timeoutConfig = new TimeoutConfiguration(10000, 30000)\n}\ngraphqlGenerateClient {\n    packageName = "com.example.generated"\n    schemaFile = graphqlDownloadSDL.outputFile\n    // optional\n    allowDeprecatedFields = true\n    customScalars.add(new GraphQLScalar("UUID", "java.util.UUID", "com.example.UUIDScalarConverter"))\n    queryFiles.from("${project.projectDir}/src/main/resources/queries/MyQuery.graphql")\n    serializer = GraphQLSerializer.KOTLINX\n\n    dependsOn graphqlDownloadSDL\n}\n'})})]})]}),"\n",(0,l.jsx)(n.h2,{id:"generating-multiple-clients",children:"Generating Multiple Clients"}),"\n",(0,l.jsx)(n.p,{children:"GraphQL Kotlin Gradle Plugin registers tasks for generation of a client queries targeting single GraphQL endpoint. You\ncan generate queries targeting additional endpoints by explicitly creating and configuring additional tasks."}),"\n",(0,l.jsxs)(t.A,{defaultValue:"kotlin",values:[{label:"Kotlin",value:"kotlin"},{label:"Groovy",value:"groovy"}],children:[(0,l.jsx)(i.A,{value:"kotlin",children:(0,l.jsx)(n.pre,{children:(0,l.jsx)(n.code,{className:"language-kotlin",children:'// build.gradle.kts\nimport com.expediagroup.graphql.plugin.gradle.tasks.GraphQLDownloadSDLTask\nimport com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask\n\nval graphqlDownloadSDL by tasks.getting(GraphQLDownloadSDLTask::class) {\n    endpoint.set("http://localhost:8080/sdl")\n}\nval graphqlGenerateClient by tasks.getting(GraphQLGenerateClientTask::class) {\n    packageName.set("com.example.generated.first")\n    schemaFile.set(graphqlDownloadSDL.outputFile)\n    queryFiles.from("${project.projectDir}/src/main/resources/queries/MyFirstQuery.graphql")\n    dependsOn("graphqlDownloadSDL")\n}\n\nval graphqlDownloadOtherSDL by tasks.creating(GraphQLDownloadSDLTask::class) {\n    endpoint.set("http://localhost:8081/sdl")\n}\nval graphqlGenerateOtherClient by tasks.creating(GraphQLGenerateClientTask::class) {\n    packageName.set("com.example.generated.second")\n    schemaFile.set(graphqlDownloadOtherSDL.outputFile)\n    queryFiles.from("${project.projectDir}/src/main/resources/queries/MySecondQuery.graphql")\n    dependsOn("graphqlDownloadOtherSDL")\n}\n'})})}),(0,l.jsx)(i.A,{value:"groovy",children:(0,l.jsx)(n.pre,{children:(0,l.jsx)(n.code,{className:"language-groovy",children:'//build.gradle\nimport com.expediagroup.graphql.plugin.gradle.tasks.GraphQLDownloadSDLTask\nimport com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask\n\ngraphqlDownloadSDL {\n    endpoint = "http://localhost:8080/sdl"\n}\ngraphqlGenerateClient {\n    packageName = "com.example.generated.first"\n    schemaFile = graphqlDownloadSDL.outputFile\n    queryFiles.from("${project.projectDir}/src/main/resources/queries/MyFirstQuery.graphql")\n\n    dependsOn graphqlDownloadSDL\n}\n\ntask graphqlDownloadOtherSDL(type: GraphQLDownloadSDLTask) {\n    endpoint = "http://localhost:8081/sdl"\n}\ntask graphqlGenerateOtherClient(type: GraphQLGenerateClientTask) {\n    packageName = "com.other.generated.second"\n    schemaFile = graphqlDownloadOtherSDL.outputFile\n    queryFiles.from("${project.projectDir}/src/main/resources/queries/MySecondQuery.graphql")\n\n    dependsOn graphqlDownloadOtherSDL\n}\n'})})})]})]})}function g(e={}){const{wrapper:n}={...(0,r.R)(),...e.components};return n?(0,l.jsx)(n,{...e,children:(0,l.jsx)(u,{...e})}):u(e)}},19365:(e,n,a)=>{a.d(n,{A:()=>i});a(96540);var l=a(34164);const r={tabItem:"tabItem_Ymn6"};var t=a(74848);function i(e){var n=e.children,a=e.hidden,i=e.className;return(0,t.jsx)("div",{role:"tabpanel",className:(0,l.A)(r.tabItem,i),hidden:a,children:n})}},11470:(e,n,a)=>{a.d(n,{A:()=>y});var l=a(96540),r=a(34164),t=a(23104),i=a(56347),o=a(205),s=a(57485),c=a(31682),p=a(89466);function d(e){var n,a;return null!=(n=null==(a=l.Children.toArray(e).filter((function(e){return"\n"!==e})).map((function(e){if(!e||(0,l.isValidElement)(e)&&((n=e.props)&&"object"==typeof n&&"value"in n))return e;var n;throw new Error("Docusaurus error: Bad <Tabs> child <"+("string"==typeof e.type?e.type:e.type.name)+'>: all children of the <Tabs> component should be <TabItem>, and every <TabItem> should have a unique "value" prop.')})))?void 0:a.filter(Boolean))?n:[]}function u(e){var n=e.values,a=e.children;return(0,l.useMemo)((function(){var e=null!=n?n:function(e){return d(e).map((function(e){var n=e.props;return{value:n.value,label:n.label,attributes:n.attributes,default:n.default}}))}(a);return function(e){var n=(0,c.X)(e,(function(e,n){return e.value===n.value}));if(n.length>0)throw new Error('Docusaurus error: Duplicate values "'+n.map((function(e){return e.value})).join(", ")+'" found in <Tabs>. Every value needs to be unique.')}(e),e}),[n,a])}function g(e){var n=e.value;return e.tabValues.some((function(e){return e.value===n}))}function h(e){var n=e.queryString,a=void 0!==n&&n,r=e.groupId,t=(0,i.W6)(),o=function(e){var n=e.queryString,a=void 0!==n&&n,l=e.groupId;if("string"==typeof a)return a;if(!1===a)return null;if(!0===a&&!l)throw new Error('Docusaurus error: The <Tabs> component groupId prop is required if queryString=true, because this value is used as the search param name. You can also provide an explicit value such as queryString="my-search-param".');return null!=l?l:null}({queryString:a,groupId:r});return[(0,s.aZ)(o),(0,l.useCallback)((function(e){if(o){var n=new URLSearchParams(t.location.search);n.set(o,e),t.replace(Object.assign({},t.location,{search:n.toString()}))}}),[o,t])]}function m(e){var n,a,r,t,i=e.defaultValue,s=e.queryString,c=void 0!==s&&s,d=e.groupId,m=u(e),x=(0,l.useState)((function(){return function(e){var n,a=e.defaultValue,l=e.tabValues;if(0===l.length)throw new Error("Docusaurus error: the <Tabs> component requires at least one <TabItem> children component");if(a){if(!g({value:a,tabValues:l}))throw new Error('Docusaurus error: The <Tabs> has a defaultValue "'+a+'" but none of its children has the corresponding value. Available values are: '+l.map((function(e){return e.value})).join(", ")+". If you intend to show no default tab, use defaultValue={null} instead.");return a}var r=null!=(n=l.find((function(e){return e.default})))?n:l[0];if(!r)throw new Error("Unexpected error: 0 tabValues");return r.value}({defaultValue:i,tabValues:m})})),v=x[0],f=x[1],j=h({queryString:c,groupId:d}),k=j[0],b=j[1],y=(n=function(e){return e?"docusaurus.tab."+e:null}({groupId:d}.groupId),a=(0,p.Dv)(n),r=a[0],t=a[1],[r,(0,l.useCallback)((function(e){n&&t.set(e)}),[n,t])]),q=y[0],G=y[1],S=function(){var e=null!=k?k:q;return g({value:e,tabValues:m})?e:null}();return(0,o.A)((function(){S&&f(S)}),[S]),{selectedValue:v,selectValue:(0,l.useCallback)((function(e){if(!g({value:e,tabValues:m}))throw new Error("Can't select invalid tab value="+e);f(e),b(e),G(e)}),[b,G,m]),tabValues:m}}var x=a(92303);const v={tabList:"tabList__CuJ",tabItem:"tabItem_LNqP"};var f=a(74848);function j(e){var n=e.className,a=e.block,l=e.selectedValue,i=e.selectValue,o=e.tabValues,s=[],c=(0,t.a_)().blockElementScrollPositionUntilNextRender,p=function(e){var n=e.currentTarget,a=s.indexOf(n),r=o[a].value;r!==l&&(c(n),i(r))},d=function(e){var n,a=null;switch(e.key){case"Enter":p(e);break;case"ArrowRight":var l,r=s.indexOf(e.currentTarget)+1;a=null!=(l=s[r])?l:s[0];break;case"ArrowLeft":var t,i=s.indexOf(e.currentTarget)-1;a=null!=(t=s[i])?t:s[s.length-1]}null==(n=a)||n.focus()};return(0,f.jsx)("ul",{role:"tablist","aria-orientation":"horizontal",className:(0,r.A)("tabs",{"tabs--block":a},n),children:o.map((function(e){var n=e.value,a=e.label,t=e.attributes;return(0,f.jsx)("li",Object.assign({role:"tab",tabIndex:l===n?0:-1,"aria-selected":l===n,ref:function(e){return s.push(e)},onKeyDown:d,onClick:p},t,{className:(0,r.A)("tabs__item",v.tabItem,null==t?void 0:t.className,{"tabs__item--active":l===n}),children:null!=a?a:n}),n)}))})}function k(e){var n=e.lazy,a=e.children,r=e.selectedValue,t=(Array.isArray(a)?a:[a]).filter(Boolean);if(n){var i=t.find((function(e){return e.props.value===r}));return i?(0,l.cloneElement)(i,{className:"margin-top--md"}):null}return(0,f.jsx)("div",{className:"margin-top--md",children:t.map((function(e,n){return(0,l.cloneElement)(e,{key:n,hidden:e.props.value!==r})}))})}function b(e){var n=m(e);return(0,f.jsxs)("div",{className:(0,r.A)("tabs-container",v.tabList),children:[(0,f.jsx)(j,Object.assign({},e,n)),(0,f.jsx)(k,Object.assign({},e,n))]})}function y(e){var n=(0,x.A)();return(0,f.jsx)(b,Object.assign({},e,{children:d(e.children)}),String(n))}},28453:(e,n,a)=>{a.d(n,{R:()=>i,x:()=>o});var l=a(96540);const r={},t=l.createContext(r);function i(e){const n=l.useContext(t);return l.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function o(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(r):e.components||r:i(e.components),l.createElement(t.Provider,{value:n},e.children)}}}]);