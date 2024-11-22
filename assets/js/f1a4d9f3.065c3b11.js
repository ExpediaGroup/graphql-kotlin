"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[6264],{88774:(e,n,i)=>{i.r(n),i.d(n,{assets:()=>a,contentTitle:()=>l,default:()=>h,frontMatter:()=>t,metadata:()=>d,toc:()=>c});var r=i(74848),s=i(28453);const t={id:"maven-plugin",title:"Maven Plugin",original_id:"maven-plugin"},l=void 0,d={id:"plugins/maven-plugin",title:"Maven Plugin",description:"GraphQL Kotlin Maven Plugin provides functionality to introspect GraphQL schemas and generate a lightweight GraphQL HTTP client.",source:"@site/versioned_docs/version-3.x.x/plugins/maven-plugin.md",sourceDirName:"plugins",slug:"/plugins/maven-plugin",permalink:"/graphql-kotlin/docs/3.x.x/plugins/maven-plugin",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-3.x.x/plugins/maven-plugin.md",tags:[],version:"3.x.x",lastUpdatedBy:"Dale Seo",lastUpdatedAt:1732301091e3,frontMatter:{id:"maven-plugin",title:"Maven Plugin",original_id:"maven-plugin"},sidebar:"docs",previous:{title:"Gradle Plugin",permalink:"/graphql-kotlin/docs/3.x.x/plugins/gradle-plugin"},next:{title:"Releasing a new version",permalink:"/graphql-kotlin/docs/3.x.x/contributors/release-proc"}},a={},c=[{value:"Goals",id:"goals",level:2},{value:"download-sdl",id:"download-sdl",level:3},{value:"generate-client",id:"generate-client",level:3},{value:"generate-test-client",id:"generate-test-client",level:3},{value:"introspect-schema",id:"introspect-schema",level:3},{value:"Examples",id:"examples",level:2},{value:"Downloading Schema SDL",id:"downloading-schema-sdl",level:3},{value:"Introspecting Schema",id:"introspecting-schema",level:3},{value:"Generating Client",id:"generating-client",level:3},{value:"Generating Client with Custom Scalars",id:"generating-client-with-custom-scalars",level:3},{value:"Generating Test Client",id:"generating-test-client",level:3},{value:"Complete Minimal Configuration Example",id:"complete-minimal-configuration-example",level:3},{value:"Complete Configuration Example",id:"complete-configuration-example",level:3}];function o(e){const n={a:"a",code:"code",em:"em",h2:"h2",h3:"h3",li:"li",p:"p",pre:"pre",strong:"strong",table:"table",tbody:"tbody",td:"td",th:"th",thead:"thead",tr:"tr",ul:"ul",...(0,s.R)(),...e.components};return(0,r.jsxs)(r.Fragment,{children:[(0,r.jsx)(n.p,{children:"GraphQL Kotlin Maven Plugin provides functionality to introspect GraphQL schemas and generate a lightweight GraphQL HTTP client."}),"\n",(0,r.jsx)(n.h2,{id:"goals",children:"Goals"}),"\n",(0,r.jsxs)(n.p,{children:["You can find detailed information about ",(0,r.jsx)(n.code,{children:"graphql-kotlin-maven-plugin"})," and all its goals by running ",(0,r.jsx)(n.code,{children:"mvn help:describe -Dplugin=com.expediagroup:graphql-kotlin-maven-plugin -Ddetail"}),"."]}),"\n",(0,r.jsx)(n.h3,{id:"download-sdl",children:"download-sdl"}),"\n",(0,r.jsxs)(n.p,{children:["GraphQL endpoints are often public and as such many servers might disable introspection queries in production environment.\nSince GraphQL schema is needed to generate type safe clients, as alternative GraphQL servers might expose private\nendpoints (e.g. accessible only from within network, etc) that could be used to download schema in Schema Definition\nLanguage (SDL) directly. This Mojo attempts to download schema from the specified ",(0,r.jsx)(n.code,{children:"graphql.endpoint"}),", validates the\nresult whether it is a valid schema and saves it locally as ",(0,r.jsx)(n.code,{children:"schema.graphql"})," under build directory. In general, this\ngoal provides limited functionality by itself and instead should be used to generate input for the subsequent\n",(0,r.jsx)(n.code,{children:"generate-client"})," goal."]}),"\n",(0,r.jsx)(n.p,{children:(0,r.jsx)(n.strong,{children:"Attributes"})}),"\n",(0,r.jsxs)(n.ul,{children:["\n",(0,r.jsxs)(n.li,{children:[(0,r.jsx)(n.em,{children:"Default Lifecycle Phase"}),": ",(0,r.jsx)(n.code,{children:"generate-sources"})]}),"\n"]}),"\n",(0,r.jsx)(n.p,{children:(0,r.jsx)(n.strong,{children:"Parameters"})}),"\n",(0,r.jsxs)(n.table,{children:[(0,r.jsx)(n.thead,{children:(0,r.jsxs)(n.tr,{children:[(0,r.jsx)(n.th,{children:"Property"}),(0,r.jsx)(n.th,{children:"Type"}),(0,r.jsx)(n.th,{children:"Required"}),(0,r.jsx)(n.th,{children:"Description"})]})}),(0,r.jsxs)(n.tbody,{children:[(0,r.jsxs)(n.tr,{children:[(0,r.jsx)(n.td,{children:(0,r.jsx)(n.code,{children:"endpoint"})}),(0,r.jsx)(n.td,{children:"String"}),(0,r.jsx)(n.td,{children:"yes"}),(0,r.jsxs)(n.td,{children:["Target GraphQL server SDL endpoint that will be used to download schema.",(0,r.jsx)("br",{}),(0,r.jsx)(n.strong,{children:"User property is"}),": ",(0,r.jsx)(n.code,{children:"graphql.endpoint"}),"."]})]}),(0,r.jsxs)(n.tr,{children:[(0,r.jsx)(n.td,{children:(0,r.jsx)(n.code,{children:"headers"})}),(0,r.jsx)(n.td,{children:"Map<String, Any>"}),(0,r.jsx)(n.td,{}),(0,r.jsx)(n.td,{children:"Optional HTTP headers to be specified on a SDL request."})]}),(0,r.jsxs)(n.tr,{children:[(0,r.jsx)(n.td,{children:(0,r.jsx)(n.code,{children:"timeoutConfiguration"})}),(0,r.jsx)(n.td,{children:"TimeoutConfiguration"}),(0,r.jsx)(n.td,{}),(0,r.jsxs)(n.td,{children:["Optional timeout configuration (in milliseconds) to download schema from SDL endpoint before we cancel the request.",(0,r.jsx)("br",{}),(0,r.jsx)(n.strong,{children:"Default values are:"})," connect timeout = 5000, read timeout = 15000.",(0,r.jsx)("br",{})]})]})]})]}),"\n",(0,r.jsx)(n.p,{children:(0,r.jsx)(n.strong,{children:"Parameter Details"})}),"\n",(0,r.jsxs)(n.ul,{children:["\n",(0,r.jsxs)(n.li,{children:["\n",(0,r.jsxs)(n.p,{children:[(0,r.jsx)(n.em,{children:"timeoutConfiguration"})," - Timeout configuration that allows you to specify connect and read timeout values in milliseconds."]}),"\n",(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-xml",children:"\n<timeoutConfiguration>\n    \x3c!-- timeout values in milliseconds \n    connect1000\n    read30000\n--&gt;\n\n"})}),"\n"]}),"\n"]}),"\n",(0,r.jsx)(n.h3,{id:"generate-client",children:"generate-client"}),"\n",(0,r.jsx)(n.p,{children:"Generate GraphQL client code based on the provided GraphQL schema and target queries."}),"\n",(0,r.jsx)(n.p,{children:(0,r.jsx)(n.strong,{children:"Attributes"})}),"\n",(0,r.jsxs)(n.ul,{children:["\n",(0,r.jsxs)(n.li,{children:[(0,r.jsx)(n.em,{children:"Default Lifecycle Phase"}),": ",(0,r.jsx)(n.code,{children:"generate-sources"})]}),"\n",(0,r.jsx)(n.li,{children:(0,r.jsx)(n.em,{children:"Requires Maven Project"})}),"\n",(0,r.jsx)(n.li,{children:"Generated classes are automatically added to the list of compiled sources."}),"\n"]}),"\n",(0,r.jsx)(n.p,{children:(0,r.jsx)(n.strong,{children:"Parameters"})}),"\n",(0,r.jsxs)(n.table,{children:[(0,r.jsx)(n.thead,{children:(0,r.jsxs)(n.tr,{children:[(0,r.jsx)(n.th,{children:"Property"}),(0,r.jsx)(n.th,{children:"Type"}),(0,r.jsx)(n.th,{children:"Required"}),(0,r.jsx)(n.th,{children:"Description"})]})}),(0,r.jsxs)(n.tbody,{children:[(0,r.jsxs)(n.tr,{children:[(0,r.jsx)(n.td,{children:(0,r.jsx)(n.code,{children:"allowDeprecatedFields"})}),(0,r.jsx)(n.td,{children:"Boolean"}),(0,r.jsx)(n.td,{}),(0,r.jsxs)(n.td,{children:["Boolean flag indicating whether selection of deprecated fields is allowed or not.",(0,r.jsx)("br",{}),(0,r.jsx)(n.strong,{children:"Default value is:"})," ",(0,r.jsx)(n.code,{children:"false"}),".",(0,r.jsx)("br",{}),(0,r.jsx)(n.strong,{children:"User property is"}),": ",(0,r.jsx)(n.code,{children:"graphql.allowDeprecatedFields"}),"."]})]}),(0,r.jsxs)(n.tr,{children:[(0,r.jsx)(n.td,{children:(0,r.jsx)(n.code,{children:"converters"})}),(0,r.jsx)(n.td,{children:"Map<String, ScalarConverter>"}),(0,r.jsx)(n.td,{}),(0,r.jsx)(n.td,{children:"Custom GraphQL scalar to converter mapping containing information about corresponding Java type and converter that should be used to serialize/deserialize values."})]}),(0,r.jsxs)(n.tr,{children:[(0,r.jsx)(n.td,{children:(0,r.jsx)(n.code,{children:"outputDirectory"})}),(0,r.jsx)(n.td,{children:"File"}),(0,r.jsx)(n.td,{}),(0,r.jsxs)(n.td,{children:["Target directory where to store generated files.",(0,r.jsx)("br",{}),(0,r.jsx)(n.strong,{children:"Default value is"}),": ",(0,r.jsx)(n.code,{children:"${project.build.directory}/generated-sources/graphql"})]})]}),(0,r.jsxs)(n.tr,{children:[(0,r.jsx)(n.td,{children:(0,r.jsx)(n.code,{children:"packageName"})}),(0,r.jsx)(n.td,{children:"String"}),(0,r.jsx)(n.td,{children:"yes"}),(0,r.jsxs)(n.td,{children:["Target package name for generated code.",(0,r.jsx)("br",{}),(0,r.jsx)(n.strong,{children:"User property is"}),": ",(0,r.jsx)(n.code,{children:"graphql.packageName"}),"."]})]}),(0,r.jsxs)(n.tr,{children:[(0,r.jsx)(n.td,{children:(0,r.jsx)(n.code,{children:"queryFileDirectory"})}),(0,r.jsx)(n.td,{children:"File"}),(0,r.jsx)(n.td,{}),(0,r.jsxs)(n.td,{children:["Directory file containing GraphQL queries. Instead of specifying a directory you can also specify list of query file by using ",(0,r.jsx)(n.code,{children:"queryFiles"})," property instead.",(0,r.jsx)("br",{}),(0,r.jsx)(n.strong,{children:"Default value is:"})," ",(0,r.jsx)(n.code,{children:"src/main/resources"}),"."]})]}),(0,r.jsxs)(n.tr,{children:[(0,r.jsx)(n.td,{children:(0,r.jsx)(n.code,{children:"queryFiles"})}),(0,r.jsx)(n.td,{children:"Listfile"}),(0,r.jsx)(n.td,{}),(0,r.jsx)(n.td,{children:"List of query files to be processed. Instead of a list of files to be processed you can also specify `` directory containing all the files. If this property is specified it will take precedence over the corresponding directory property."})]}),(0,r.jsxs)(n.tr,{children:[(0,r.jsx)(n.td,{children:"``"}),(0,r.jsx)(n.td,{children:"String"}),(0,r.jsx)(n.td,{children:"yes"}),(0,r.jsxs)(n.td,{children:["GraphQL schema file that will be used to generate client code.",(0,r.jsx)(n.strong,{children:"User property is"}),": ``."]})]})]})]}),"\n",(0,r.jsx)(n.p,{children:(0,r.jsx)(n.strong,{children:"Parameter Details"})}),"\n",(0,r.jsxs)(n.ul,{children:["\n",(0,r.jsxs)(n.li,{children:["\n",(0,r.jsxs)(n.p,{children:[(0,r.jsx)(n.em,{children:"converters"})," - Custom GraphQL scalar to converter mapping containing information about corresponding Java type and converter that should be used to serialize/deserialize values."]}),"\n",(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-xml",children:"\n\n  \n    \n    java.util.UUID\n    \n    com.example.UUIDScalarConverter\n  \n--&gt;\n\n"})}),"\n"]}),"\n"]}),"\n",(0,r.jsx)(n.h3,{id:"generate-test-client",children:"generate-test-client"}),"\n",(0,r.jsx)(n.p,{children:"Generate GraphQL test client code based on the provided GraphQL schema and target queries."}),"\n",(0,r.jsx)(n.p,{children:(0,r.jsx)(n.strong,{children:"Attributes"})}),"\n",(0,r.jsxs)(n.ul,{children:["\n",(0,r.jsxs)(n.li,{children:[(0,r.jsx)(n.em,{children:"Default Lifecycle Phase"}),": ``"]}),"\n",(0,r.jsx)(n.li,{children:(0,r.jsx)(n.em,{children:"Requires Maven Project"})}),"\n",(0,r.jsx)(n.li,{children:"Generated classes are automatically added to the list of test compiled sources."}),"\n"]}),"\n",(0,r.jsx)(n.p,{children:(0,r.jsx)(n.strong,{children:"Parameters"})}),"\n",(0,r.jsxs)(n.table,{children:[(0,r.jsx)(n.thead,{children:(0,r.jsxs)(n.tr,{children:[(0,r.jsx)(n.th,{children:"Property"}),(0,r.jsx)(n.th,{children:"Type"}),(0,r.jsx)(n.th,{children:"Required"}),(0,r.jsx)(n.th,{children:"Description"})]})}),(0,r.jsxs)(n.tbody,{children:[(0,r.jsxs)(n.tr,{children:[(0,r.jsx)(n.td,{children:"``"}),(0,r.jsx)(n.td,{children:"Boolean"}),(0,r.jsx)(n.td,{}),(0,r.jsxs)(n.td,{children:["Boolean flag indicating whether selection of deprecated fields is allowed or not.",(0,r.jsx)(n.strong,{children:"Default value is:"})," ",(0,r.jsx)(n.code,{children:".**User property is**: "}),"."]})]}),(0,r.jsxs)(n.tr,{children:[(0,r.jsx)(n.td,{children:"``"}),(0,r.jsx)(n.td,{children:"Map<String, ScalarConverter>"}),(0,r.jsx)(n.td,{}),(0,r.jsx)(n.td,{children:"Custom GraphQL scalar to converter mapping containing information about corresponding Java type and converter that should be used to serialize/deserialize values."})]}),(0,r.jsxs)(n.tr,{children:[(0,r.jsx)(n.td,{children:"``"}),(0,r.jsx)(n.td,{children:"File"}),(0,r.jsx)(n.td,{}),(0,r.jsxs)(n.td,{children:["Target directory where to store generated files.",(0,r.jsx)(n.strong,{children:"Default value is"}),": ``"]})]}),(0,r.jsxs)(n.tr,{children:[(0,r.jsx)(n.td,{children:"``"}),(0,r.jsx)(n.td,{children:"String"}),(0,r.jsx)(n.td,{children:"yes"}),(0,r.jsxs)(n.td,{children:["Target package name for generated code.",(0,r.jsx)(n.strong,{children:"User property is"}),": ``."]})]}),(0,r.jsxs)(n.tr,{children:[(0,r.jsx)(n.td,{children:"``"}),(0,r.jsx)(n.td,{children:"File"}),(0,r.jsx)(n.td,{}),(0,r.jsxs)(n.td,{children:["Directory file containing GraphQL queries. Instead of specifying a directory you can also specify list of query file by using ",(0,r.jsx)(n.code,{children:"property instead.**Default value is:**"}),"."]})]}),(0,r.jsxs)(n.tr,{children:[(0,r.jsx)(n.td,{children:"``"}),(0,r.jsx)(n.td,{children:"List"}),(0,r.jsx)(n.td,{}),(0,r.jsx)(n.td,{children:"List of query files to be processed. Instead of a list of files to be processed you can also specify `` directory containing all the files. If this property is specified it will take precedence over the corresponding directory property."})]}),(0,r.jsxs)(n.tr,{children:[(0,r.jsx)(n.td,{children:"``"}),(0,r.jsx)(n.td,{children:"String"}),(0,r.jsx)(n.td,{children:"yes"}),(0,r.jsxs)(n.td,{children:["GraphQL schema file that will be used to generate client code.",(0,r.jsx)(n.strong,{children:"User property is"}),": ``."]})]})]})]}),"\n",(0,r.jsx)(n.p,{children:(0,r.jsx)(n.strong,{children:"Parameter Details"})}),"\n",(0,r.jsxs)(n.ul,{children:["\n",(0,r.jsxs)(n.li,{children:["\n",(0,r.jsxs)(n.p,{children:[(0,r.jsx)(n.em,{children:"converters"})," - Custom GraphQL scalar to converter mapping containing information about corresponding Java type and converter that should be used to serialize/deserialize values."]}),"\n",(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-xml",children:"\n\n  \n    \n    java.util.UUID\n    \n    com.example.UUIDScalarConverter\n  \n--&gt;\n\n"})}),"\n"]}),"\n"]}),"\n",(0,r.jsx)(n.h3,{id:"introspect-schema",children:"introspect-schema"}),"\n",(0,r.jsxs)(n.p,{children:["Executes GraphQL introspection query against specified ",(0,r.jsx)(n.code,{children:"and saves the underlying schema file as"})," under build directory. In general, this goal provides limited functionality by itself and instead\nshould be used to generate input for the subsequent `` goal."]}),"\n",(0,r.jsx)(n.p,{children:(0,r.jsx)(n.strong,{children:"Attributes"})}),"\n",(0,r.jsxs)(n.ul,{children:["\n",(0,r.jsxs)(n.li,{children:[(0,r.jsx)(n.em,{children:"Default Lifecycle Phase"}),": ``"]}),"\n"]}),"\n",(0,r.jsx)(n.p,{children:(0,r.jsx)(n.strong,{children:"Parameters"})}),"\n",(0,r.jsxs)(n.table,{children:[(0,r.jsx)(n.thead,{children:(0,r.jsxs)(n.tr,{children:[(0,r.jsx)(n.th,{children:"Property"}),(0,r.jsx)(n.th,{children:"Type"}),(0,r.jsx)(n.th,{children:"Required"}),(0,r.jsx)(n.th,{children:"Description"})]})}),(0,r.jsxs)(n.tbody,{children:[(0,r.jsxs)(n.tr,{children:[(0,r.jsx)(n.td,{children:"``"}),(0,r.jsx)(n.td,{children:"String"}),(0,r.jsx)(n.td,{children:"yes"}),(0,r.jsxs)(n.td,{children:["Target GraphQL server endpoint that will be used to execute introspection queries.",(0,r.jsx)(n.strong,{children:"User property is"}),": ``."]})]}),(0,r.jsxs)(n.tr,{children:[(0,r.jsx)(n.td,{children:"``"}),(0,r.jsx)(n.td,{children:"Map<String, Any>"}),(0,r.jsx)(n.td,{}),(0,r.jsx)(n.td,{children:"Optional HTTP headers to be specified on an introspection query."})]}),(0,r.jsxs)(n.tr,{children:[(0,r.jsx)(n.td,{children:"``"}),(0,r.jsx)(n.td,{children:"TimeoutConfiguration"}),(0,r.jsx)(n.td,{}),(0,r.jsxs)(n.td,{children:["Optional timeout configuration(in milliseconds) to execute introspection query before we cancel the request.",(0,r.jsx)(n.strong,{children:"Default values are:"})," connect timeout = 5000, read timeout = 15000."]})]})]})]}),"\n",(0,r.jsx)(n.p,{children:(0,r.jsx)(n.strong,{children:"Parameter Details"})}),"\n",(0,r.jsxs)(n.ul,{children:["\n",(0,r.jsxs)(n.li,{children:["\n",(0,r.jsxs)(n.p,{children:[(0,r.jsx)(n.em,{children:"timeoutConfiguration"})," - Timeout configuration that allows you to specify connect and read timeout values in milliseconds."]}),"\n",(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-xml",children:"\n\n    1000\n    30000\n--&gt;\n\n"})}),"\n"]}),"\n"]}),"\n",(0,r.jsx)(n.h2,{id:"examples",children:"Examples"}),"\n",(0,r.jsx)(n.h3,{id:"downloading-schema-sdl",children:"Downloading Schema SDL"}),"\n",(0,r.jsxs)(n.p,{children:["Download SDL Mojo requires target GraphQL server ",(0,r.jsx)(n.code,{children:"to be specified. Task can be executed directly from the command line by explicitly specifying"})," property."]}),"\n",(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-shell",metastring:"script",children:"\n\n\n"})}),"\n",(0,r.jsx)(n.p,{children:"Mojo can also be configured in your Maven build file"}),"\n",(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-xml",children:"\n\n\n"})}),"\n",(0,r.jsxs)(n.p,{children:["By default, ",(0,r.jsx)(n.code,{children:"goal will be executed as part of the"})," ",(0,r.jsx)(n.a,{href:"https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html",children:"build lifecycle phase"}),"."]}),"\n",(0,r.jsx)(n.h3,{id:"introspecting-schema",children:"Introspecting Schema"}),"\n",(0,r.jsxs)(n.p,{children:["Introspection Mojo requires target GraphQL server ",(0,r.jsx)(n.code,{children:"to be specified. Task can be executed directly from the command line by explicitly specifying"})," property"]}),"\n",(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-shell",metastring:"script",children:"\n\n\n"})}),"\n",(0,r.jsx)(n.p,{children:"Mojo can also be configured in your Maven build file"}),"\n",(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-xml",children:"\n\n\n"})}),"\n",(0,r.jsxs)(n.p,{children:["By default, ",(0,r.jsx)(n.code,{children:"goal will be executed as part of the"})," ",(0,r.jsx)(n.a,{href:"https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html",children:"build lifecycle phase"}),"."]}),"\n",(0,r.jsx)(n.h3,{id:"generating-client",children:"Generating Client"}),"\n",(0,r.jsxs)(n.p,{children:["This Mojo generates GraphQL client code based on the provided queries using target GraphQL ",(0,r.jsx)(n.code,{children:". Classes are generated under specified "}),". When using default configuration and storing GraphQL queries under ``\ndirectories, task can be executed directly from the command line by explicitly providing required properties."]}),"\n",(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-shell",metastring:"script",children:"\n\n\n"})}),"\n",(0,r.jsx)(n.p,{children:"Mojo can also be configured in your Maven build file to become part of your build lifecycle. Plugin also provides additional\nconfiguration options that are not available on command line."}),"\n",(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-xml",children:"\n\n\n"})}),"\n",(0,r.jsx)(n.p,{children:"This will process all GraphQL queries located under `` and generate corresponding GraphQL Kotlin clients.\nGenerated classes will be automatically added to the project compile sources."}),"\n",(0,r.jsxs)(n.p,{children:["> NOTE: You might need to explicitly add generated clients to your project sources for your IDE to recognize them. See\n> ",(0,r.jsx)(n.a,{href:"https://www.mojohaus.org/build-helper-maven-plugin/",children:"build-helper-maven-plugin"})," for details."]}),"\n",(0,r.jsx)(n.h3,{id:"generating-client-with-custom-scalars",children:"Generating Client with Custom Scalars"}),"\n",(0,r.jsxs)(n.p,{children:["By default, all custom GraphQL scalars will be serialized as Strings. You can override this default behavior by specifying\ncustom ",(0,r.jsx)(n.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/3.x.x/graphql-kotlin-client/src/main/kotlin/com/expediagroup/graphql/client/converter/ScalarConverter.kt",children:"scalar converter"}),"."]}),"\n",(0,r.jsx)(n.p,{children:"For example given following custom scalar in our GraphQL schema"}),"\n",(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-graphql",children:"\n\n\n"})}),"\n",(0,r.jsx)(n.p,{children:"We can create a custom converter to automatically convert this custom scalar to ``"}),"\n",(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-kotlin",children:"\n\n\n"})}),"\n",(0,r.jsx)(n.p,{children:"Afterwards we need to configure our plugin to use this custom converter"}),"\n",(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-xml",children:"\n\n                    \n                        \n                        java.util.UUID\n                        \n                        com.example.UUIDScalarConverter\n                    \n                \n                com.example.generated\n                mySchema.graphql\n            \n        \n    \n--&gt;\n\n"})}),"\n",(0,r.jsx)(n.h3,{id:"generating-test-client",children:"Generating Test Client"}),"\n",(0,r.jsxs)(n.p,{children:["This Mojo generates GraphQL Kotlin test client code based on the provided queries using target GraphQL ",(0,r.jsx)(n.code,{children:". Classes are generated under specified "}),". When using default configuration and storing GraphQL queries under ``\ndirectories, task can be executed directly from the command line by explicitly providing required properties."]}),"\n",(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-shell",metastring:"script",children:"\n\n\n"})}),"\n",(0,r.jsx)(n.p,{children:"Mojo can also be configured in your Maven build file to become part of your build lifecycle. Plugin also provides additional\nconfiguration options that are not available on command line."}),"\n",(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-xml",children:"\n\n\n"})}),"\n",(0,r.jsx)(n.p,{children:"This will process all GraphQL queries located under `` and generate corresponding GraphQL Kotlin test clients.\nGenerated classes will be automatically added to the project test compile sources."}),"\n",(0,r.jsxs)(n.p,{children:["> NOTE: You might need to explicitly add generated test clients to your project test sources for your IDE to recognize them.\n> See ",(0,r.jsx)(n.a,{href:"https://www.mojohaus.org/build-helper-maven-plugin/",children:"build-helper-maven-plugin"})," for details."]}),"\n",(0,r.jsx)(n.h3,{id:"complete-minimal-configuration-example",children:"Complete Minimal Configuration Example"}),"\n",(0,r.jsx)(n.p,{children:"Following is the minimal configuration that runs introspection query against a target GraphQL server and generates a corresponding schema.\nThis generated schema is subsequently used to generate GraphQL client code based on the queries provided under `` directory."}),"\n",(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-xml",children:"\n\n\n"})}),"\n",(0,r.jsxs)(n.p,{children:["> NOTE: Both ",(0,r.jsx)(n.code,{children:"and"})," goals are bound to the same ",(0,r.jsx)(n.code,{children:"Maven lifecycle phase. &gt; As opposed to Gradle, Maven does not support explicit ordering of different goals bound to the same build phase. Maven &gt; Mojos will be executed in the order they are defined in your"})," build file."]}),"\n",(0,r.jsx)(n.h3,{id:"complete-configuration-example",children:"Complete Configuration Example"}),"\n",(0,r.jsx)(n.p,{children:"Following is a configuration example that downloads schema SDL from a target GraphQL server that is then used to generate\nthe GraphQL client code based on the provided query."}),"\n",(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-xml",children:'\n\n                true\n                \n                    \n                    \n                        \n                        java.util.UUID\n                        \n                        com.example.UUIDScalarConverter\n                    \n                \n                \n                    My-Custom-Header\n                \n                \n                    \n                    1000\n                    30000\n                \n                \n                    ${"{"}project.basedir{"}"}/src/main/resources/queries/MyQuery.graphql\n                \n            \n        \n    \n--&gt;\n\n'})})]})}function h(e={}){const{wrapper:n}={...(0,s.R)(),...e.components};return n?(0,r.jsx)(n,{...e,children:(0,r.jsx)(o,{...e})}):o(e)}},28453:(e,n,i)=>{i.d(n,{R:()=>l,x:()=>d});var r=i(96540);const s={},t=r.createContext(s);function l(e){const n=r.useContext(t);return r.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function d(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(s):e.components||s:l(e.components),r.createElement(t.Provider,{value:n},e.children)}}}]);