"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[8976],{54894:(e,t,a)=>{a.r(t),a.d(t,{assets:()=>m,contentTitle:()=>l,default:()=>g,frontMatter:()=>s,metadata:()=>c,toc:()=>p});var n=a(87462),o=a(63366),r=(a(67294),a(3905)),i=(a(95657),["components"]),s={id:"generator-config",title:"Generator Configuration & Hooks"},l=void 0,c={unversionedId:"schema-generator/customizing-schemas/generator-config",id:"version-6.x.x/schema-generator/customizing-schemas/generator-config",title:"Generator Configuration & Hooks",description:"graphql-kotlin-schema-generator provides a single function, toSchema, to generate a schema from Kotlin objects. This",source:"@site/versioned_docs/version-6.x.x/schema-generator/customizing-schemas/generator-config.md",sourceDirName:"schema-generator/customizing-schemas",slug:"/schema-generator/customizing-schemas/generator-config",permalink:"/graphql-kotlin/docs/schema-generator/customizing-schemas/generator-config",draft:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-6.x.x/schema-generator/customizing-schemas/generator-config.md",tags:[],version:"6.x.x",lastUpdatedBy:"Samuel Vazquez",lastUpdatedAt:1685659104,formattedLastUpdatedAt:"Jun 1, 2023",frontMatter:{id:"generator-config",title:"Generator Configuration & Hooks"},sidebar:"docs",previous:{title:"Annotations",permalink:"/graphql-kotlin/docs/schema-generator/customizing-schemas/annotations"},next:{title:"Documenting Schema",permalink:"/graphql-kotlin/docs/schema-generator/customizing-schemas/documenting-schema"}},m={},p=[{value:"TopLevelObjects",id:"toplevelobjects",level:2},{value:"SchemaGeneratorConfig",id:"schemageneratorconfig",level:2},{value:"SchemaGeneratorHooks",id:"schemageneratorhooks",level:2}],h={toc:p},u="wrapper";function g(e){var t=e.components,a=(0,o.Z)(e,i);return(0,r.kt)(u,(0,n.Z)({},h,a,{components:t,mdxType:"MDXLayout"}),(0,r.kt)("p",null,(0,r.kt)("inlineCode",{parentName:"p"},"graphql-kotlin-schema-generator")," provides a single function, ",(0,r.kt)("inlineCode",{parentName:"p"},"toSchema"),", to generate a schema from Kotlin objects. This\nfunction accepts four arguments: config, queries, mutations and subscriptions."),(0,r.kt)("h2",{id:"toplevelobjects"},"TopLevelObjects"),(0,r.kt)("ul",null,(0,r.kt)("li",{parentName:"ul"},"The queries, mutations and subscriptions are a list of\n",(0,r.kt)("a",{parentName:"li",href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/master/generator/graphql-kotlin-schema-generator/src/main/kotlin/com/expediagroup/graphql/generator/TopLevelObject.kt"},"TopLevelObjects"),"\nand will be used to generate corresponding GraphQL root types."),(0,r.kt)("li",{parentName:"ul"},"Annotated schema ",(0,r.kt)("inlineCode",{parentName:"li"},"TopLevelObject")," will be used to generate any schema directives")),(0,r.kt)("h2",{id:"schemageneratorconfig"},"SchemaGeneratorConfig"),(0,r.kt)("p",null,"The ",(0,r.kt)("a",{parentName:"p",href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/master/generator/graphql-kotlin-schema-generator/src/main/kotlin/com/expediagroup/graphql/generator/SchemaGeneratorConfig.kt"},"config"),"\ncontains all the extra information you need to pass, including custom hooks, supported packages and name overrides.\n",(0,r.kt)("inlineCode",{parentName:"p"},"SchemaGeneratorConfig")," has some default settings but you can override them and add custom behaviors for generating your\nschema."),(0,r.kt)("ul",null,(0,r.kt)("li",{parentName:"ul"},(0,r.kt)("inlineCode",{parentName:"li"},"supportedPackages")," ",(0,r.kt)("strong",{parentName:"li"},"[Required]")," - List of Kotlin packages that can contain schema objects. Limits the scope of\npackages that can be scanned using reflections."),(0,r.kt)("li",{parentName:"ul"},(0,r.kt)("inlineCode",{parentName:"li"},"topLevelNames")," ",(0,r.kt)("em",{parentName:"li"},"[Optional]")," - Set the name of the top level GraphQL fields, defaults to ",(0,r.kt)("inlineCode",{parentName:"li"},"Query"),", ",(0,r.kt)("inlineCode",{parentName:"li"},"Mutation")," and\n",(0,r.kt)("inlineCode",{parentName:"li"},"Subscription")),(0,r.kt)("li",{parentName:"ul"},(0,r.kt)("inlineCode",{parentName:"li"},"hooks")," ",(0,r.kt)("em",{parentName:"li"},"[Optional]")," - Set custom behaviors for generating the schema, see below for details."),(0,r.kt)("li",{parentName:"ul"},(0,r.kt)("inlineCode",{parentName:"li"},"dataFetcherFactory")," ",(0,r.kt)("em",{parentName:"li"},"[Optional]")," - Sets custom behavior for generating data fetchers"),(0,r.kt)("li",{parentName:"ul"},(0,r.kt)("inlineCode",{parentName:"li"},"introspectionEnabled")," ",(0,r.kt)("em",{parentName:"li"},"[Optional]")," - Boolean flag indicating whether introspection queries are enabled, introspection queries are enabled by default"),(0,r.kt)("li",{parentName:"ul"},(0,r.kt)("inlineCode",{parentName:"li"},"additionalTypes")," ",(0,r.kt)("em",{parentName:"li"},"[Optional]")," - Set of additional GraphQL types to include when generating the schema."),(0,r.kt)("li",{parentName:"ul"},(0,r.kt)("inlineCode",{parentName:"li"},"schemaObject")," ",(0,r.kt)("em",{parentName:"li"},"[Optional]")," - Object that contains schema directive information")),(0,r.kt)("h2",{id:"schemageneratorhooks"},"SchemaGeneratorHooks"),(0,r.kt)("p",null,"Hooks are lifecycle events that are called and triggered while the schema is building that allow users to customize the\nschema."),(0,r.kt)("p",null,"For exact names and details of every hook, see the comments and descriptions in our latest\n",(0,r.kt)("a",{parentName:"p",href:"https://www.javadoc.io/doc/com.expediagroup/graphql-kotlin-schema-generator"},"javadocs")," or directly in the source file:\n",(0,r.kt)("a",{parentName:"p",href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/master/generator/graphql-kotlin-schema-generator/src/main/kotlin/com/expediagroup/graphql/generator/hooks/SchemaGeneratorHooks.kt"},"SchemaGeneratorHooks.kt")),(0,r.kt)("p",null,"As an example here is how you would write a custom hook and provide it through the configuration"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},'class MyCustomHooks : SchemaGeneratorHooks {\n  // Only generate functions that start with "dog"\n  // This would probably be better just to use @GraphQLIgnore, but this is just an example\n  override fun isValidFunction(function: KFunction<*>) = function.name.startsWith("dog")\n}\n\nclass Query {\n  fun dogSound() = "bark"\n\n  fun catSound() = "meow"\n}\n\nval config = SchemaGeneratorConfig(supportedPackages = listOf("org.example"), hooks = MyCustomHooks())\n\nval queries = listOf(TopLevelObject(Query()))\n\ntoSchema(queries = queries, config = config)\n')),(0,r.kt)("p",null,"will generate"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-graphql"},"schema {\n  query: Query\n}\n\ntype Query {\n  dogSound: String!\n}\n")),(0,r.kt)("p",null,"Notice there is no ",(0,r.kt)("inlineCode",{parentName:"p"},"catSound")," function."))}g.isMDXComponent=!0}}]);