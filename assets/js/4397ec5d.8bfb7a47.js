"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[5427],{91174:(e,n,a)=>{a.r(n),a.d(n,{assets:()=>o,contentTitle:()=>t,default:()=>h,frontMatter:()=>l,metadata:()=>i,toc:()=>c});var s=a(74848),r=a(28453);const l={id:"scalars",title:"Scalars"},t=void 0,i={id:"schema-generator/writing-schemas/scalars",title:"Scalars",description:"Primitive Types",source:"@site/docs/schema-generator/writing-schemas/scalars.md",sourceDirName:"schema-generator/writing-schemas",slug:"/schema-generator/writing-schemas/scalars",permalink:"/graphql-kotlin/docs/9.x.x/schema-generator/writing-schemas/scalars",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/docs/schema-generator/writing-schemas/scalars.md",tags:[],version:"current",lastUpdatedBy:"Samuel Vazquez",lastUpdatedAt:172791071e4,frontMatter:{id:"scalars",title:"Scalars"},sidebar:"docs",previous:{title:"Arguments",permalink:"/graphql-kotlin/docs/9.x.x/schema-generator/writing-schemas/arguments"},next:{title:"Enums",permalink:"/graphql-kotlin/docs/9.x.x/schema-generator/writing-schemas/enums"}},o={},c=[{value:"Primitive Types",id:"primitive-types",level:2},{value:"GraphQL ID",id:"graphql-id",level:2},{value:"Custom Scalars",id:"custom-scalars",level:2},{value:"Inline Value Classes",id:"inline-value-classes",level:3},{value:"Representing Unwrapped Value Classes in the Schema as the Underlying Type",id:"representing-unwrapped-value-classes-in-the-schema-as-the-underlying-type",level:4},{value:"Representing Unwrapped Value Classes in the Schema as a Custom Scalar Type",id:"representing-unwrapped-value-classes-in-the-schema-as-a-custom-scalar-type",level:4},{value:"Representing Value Classes in the Schema as Objects",id:"representing-value-classes-in-the-schema-as-objects",level:4},{value:"Common Issues",id:"common-issues",level:2},{value:"Extended Scalars",id:"extended-scalars",level:3},{value:"<code>TypeNotSupportedException</code>",id:"typenotsupportedexception",level:3}];function d(e){const n={a:"a",admonition:"admonition",code:"code",em:"em",h2:"h2",h3:"h3",h4:"h4",p:"p",pre:"pre",table:"table",tbody:"tbody",td:"td",th:"th",thead:"thead",tr:"tr",...(0,r.R)(),...e.components};return(0,s.jsxs)(s.Fragment,{children:[(0,s.jsx)(n.h2,{id:"primitive-types",children:"Primitive Types"}),"\n",(0,s.jsxs)(n.p,{children:[(0,s.jsx)(n.code,{children:"graphql-kotlin-schema-generator"}),' can directly map most Kotlin "primitive" types to standard GraphQL scalar types or\nextended scalar types provided by ',(0,s.jsx)(n.code,{children:"graphql-java"}),"."]}),"\n",(0,s.jsxs)(n.table,{children:[(0,s.jsx)(n.thead,{children:(0,s.jsxs)(n.tr,{children:[(0,s.jsx)(n.th,{children:"Kotlin Type"}),(0,s.jsx)(n.th,{children:"GraphQL Type"})]})}),(0,s.jsxs)(n.tbody,{children:[(0,s.jsxs)(n.tr,{children:[(0,s.jsx)(n.td,{children:(0,s.jsx)(n.code,{children:"kotlin.String"})}),(0,s.jsx)(n.td,{children:(0,s.jsx)(n.code,{children:"String"})})]}),(0,s.jsxs)(n.tr,{children:[(0,s.jsx)(n.td,{children:(0,s.jsx)(n.code,{children:"kotlin.Boolean"})}),(0,s.jsx)(n.td,{children:(0,s.jsx)(n.code,{children:"Boolean"})})]}),(0,s.jsxs)(n.tr,{children:[(0,s.jsx)(n.td,{children:(0,s.jsx)(n.code,{children:"kotlin.Int"})}),(0,s.jsx)(n.td,{children:(0,s.jsx)(n.code,{children:"Int"})})]}),(0,s.jsxs)(n.tr,{children:[(0,s.jsx)(n.td,{children:(0,s.jsx)(n.code,{children:"kotlin.Double"})}),(0,s.jsx)(n.td,{children:(0,s.jsx)(n.code,{children:"Float"})})]}),(0,s.jsxs)(n.tr,{children:[(0,s.jsx)(n.td,{children:(0,s.jsx)(n.code,{children:"kotlin.Float"})}),(0,s.jsx)(n.td,{children:(0,s.jsx)(n.code,{children:"Float"})})]})]})]}),"\n",(0,s.jsx)(n.admonition,{type:"note",children:(0,s.jsxs)(n.p,{children:["The GraphQL spec uses the term ",(0,s.jsx)(n.code,{children:"Float"})," for signed double\u2010precision fractional values. ",(0,s.jsx)(n.code,{children:"graphql-java"})," maps this to a ",(0,s.jsx)(n.code,{children:"java.lang.Double"})," for the execution. The generator will map both ",(0,s.jsx)(n.code,{children:"kotlin.Double"})," and ",(0,s.jsx)(n.code,{children:"kotlin.Float"})," to GraphQL ",(0,s.jsx)(n.code,{children:"Float"})," but we recommend you use ",(0,s.jsx)(n.code,{children:"kotlin.Double"}),"."]})}),"\n",(0,s.jsx)(n.h2,{id:"graphql-id",children:"GraphQL ID"}),"\n",(0,s.jsxs)(n.p,{children:["GraphQL supports the scalar type ",(0,s.jsx)(n.code,{children:"ID"}),", a unique identifier that is not intended to be human-readable. IDs are\nserialized as a ",(0,s.jsx)(n.code,{children:"String"}),". To expose a GraphQL ",(0,s.jsx)(n.code,{children:"ID"})," field, you must use the ",(0,s.jsx)(n.code,{children:"com.expediagroup.graphql.generator.scalars.ID"}),"\nclass, which is an ",(0,s.jsx)(n.em,{children:"inline value class"})," that wraps the underlying ",(0,s.jsx)(n.code,{children:"String"})," value."]}),"\n",(0,s.jsx)(n.admonition,{type:"note",children:(0,s.jsxs)(n.p,{children:[(0,s.jsx)(n.code,{children:"graphql-java"})," supports additional types (",(0,s.jsx)(n.code,{children:"String"}),", ",(0,s.jsx)(n.code,{children:"Int"}),", ",(0,s.jsx)(n.code,{children:"Long"}),", or ",(0,s.jsx)(n.code,{children:"UUID"}),") but ",(0,s.jsx)(n.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/issues/317",children:"due to serialization issues"})," we can only directly support Strings."]})}),"\n",(0,s.jsxs)(n.p,{children:["Since ",(0,s.jsx)(n.code,{children:"ID"})," is a value class, it may be represented at runtime as a wrapper or directly as underlying type. Due to the generic\nnature of the query processing logic we ",(0,s.jsx)(n.em,{children:"always"})," end up with up a wrapper type when resolving the field value. As a result,\nin order to ensure that underlying scalar value is correctly serialized, we need to explicitly unwrap it by registering\n",(0,s.jsx)(n.code,{children:"IDValueUnboxer"})," with your GraphQL instance."]}),"\n",(0,s.jsx)(n.pre,{children:(0,s.jsx)(n.code,{className:"language-kotlin",children:"// registering custom value unboxer\nval graphQL = GraphQL.newGraphQL(graphQLSchema)\n    .valueUnboxer(IDValueUnboxer())\n    .build()\n"})}),"\n",(0,s.jsx)(n.admonition,{type:"note",children:(0,s.jsxs)(n.p,{children:[(0,s.jsx)(n.code,{children:"IDValueUnboxer"})," bean is automatically configured by ",(0,s.jsx)(n.code,{children:"graphql-kotlin-spring-server"}),"."]})}),"\n",(0,s.jsx)(n.pre,{children:(0,s.jsx)(n.code,{className:"language-kotlin",children:'data class Person(\n    val id: ID,\n    val name: String\n)\n\nfun findPersonById(id: ID) = Person(id, "John Smith")\n\nfun generateRandomId(): ID = ID(UUID.randomUUID().toString())\n'})}),"\n",(0,s.jsx)(n.p,{children:"This would produce the following schema:"}),"\n",(0,s.jsx)(n.pre,{children:(0,s.jsx)(n.code,{className:"language-graphql",children:"schema {\n    query: Query\n}\n\ntype Query {\n    findPersonById(id: ID!): Person!\n    generateRandomId: ID!\n}\n\ntype Person {\n    id: ID!\n    name: String!\n}\n"})}),"\n",(0,s.jsx)(n.h2,{id:"custom-scalars",children:"Custom Scalars"}),"\n",(0,s.jsxs)(n.p,{children:["By default, ",(0,s.jsx)(n.code,{children:"graphql-kotlin-schema-generator"})," uses Kotlin reflections to generate all schema objects. If you want to\napply custom behavior to the objects, you can also define your own custom scalars. Custom scalars have to be explicitly\nadded to the schema through ",(0,s.jsx)(n.code,{children:"SchemaGeneratorHooks.willGenerateGraphQLType"}),".\nSee the ",(0,s.jsx)(n.a,{href:"/graphql-kotlin/docs/9.x.x/schema-generator/customizing-schemas/generator-config",children:"Generator Configuration"})," documentation for more information."]}),"\n",(0,s.jsx)(n.p,{children:"Example usage"}),"\n",(0,s.jsx)(n.pre,{children:(0,s.jsx)(n.code,{className:"language-kotlin",children:'class CustomSchemaGeneratorHooks : SchemaGeneratorHooks {\n\n  override fun willGenerateGraphQLType(type: KType): GraphQLType? = when (type.classifier as? KClass<*>) {\n    UUID::class -> graphqlUUIDType\n    else -> null\n  }\n}\n\nval graphqlUUIDType = GraphQLScalarType.newScalar()\n    .name("UUID")\n    .description("A type representing a formatted java.util.UUID")\n    .coercing(UUIDCoercing)\n    .build()\n\nobject UUIDCoercing : Coercing<UUID, String> {\n    override fun parseValue(input: Any, graphQLContext: GraphQLContext, locale: Locale): UUID = runCatching {\n        UUID.fromString(serialize(input, graphQLContext, locale))\n    }.getOrElse {\n        throw CoercingParseValueException("Expected valid UUID but was $input")\n    }\n\n    override fun parseLiteral(input: Value<*>, variables: CoercedVariables, graphQLContext: GraphQLContext, locale: Locale): UUID {\n        val uuidString = (input as? StringValue)?.value\n        return runCatching {\n            UUID.fromString(uuidString)\n        }.getOrElse {\n            throw CoercingParseLiteralException("Expected valid UUID literal but was $uuidString")\n        }\n    }\n\n    override fun serialize(dataFetcherResult: Any, graphQLContext: GraphQLContext, locale: Locale): String = runCatching {\n        dataFetcherResult.toString()\n    }.getOrElse {\n        throw CoercingSerializeException("Data fetcher result $dataFetcherResult cannot be serialized to a String")\n    }\n}\n'})}),"\n",(0,s.jsx)(n.p,{children:"Once the scalars are registered you can use them anywhere in the schema as regular objects."}),"\n",(0,s.jsx)(n.h3,{id:"inline-value-classes",children:"Inline Value Classes"}),"\n",(0,s.jsx)(n.p,{children:"It is often beneficial to create a wrapper around the underlying primitive type to better represent its meaning. Inline value classes can be used\nto optimize such use cases - Kotlin compiler will attempt to use underlying type directly whenever possible and only keep the wrapper classes\nwhenever it is necessary."}),"\n",(0,s.jsx)(n.admonition,{type:"note",children:(0,s.jsxs)(n.p,{children:["Nullable value class types may result in a runtime ",(0,s.jsx)(n.code,{children:"IllegalArgumentException"})," due to ",(0,s.jsx)(n.a,{href:"https://youtrack.jetbrains.com/issue/KT-31141",children:"https://youtrack.jetbrains.com/issue/KT-31141"}),". This should be resolved in Kotlin 1.7.0+."]})}),"\n",(0,s.jsx)(n.h4,{id:"representing-unwrapped-value-classes-in-the-schema-as-the-underlying-type",children:"Representing Unwrapped Value Classes in the Schema as the Underlying Type"}),"\n",(0,s.jsxs)(n.p,{children:["In order to represent unwrapped inline value classes in your schema as the underlying type, you need to register it using hooks and also provide value unboxer that will be used by\n",(0,s.jsx)(n.code,{children:"graphql-java"})," when dealing with its wrapper object."]}),"\n",(0,s.jsx)(n.pre,{children:(0,s.jsx)(n.code,{className:"language-kotlin",children:'@JvmInline\nvalue class MyValueClass(\n    val value: String\n)\n\nclass MyQuery : Query {\n    fun inlineValueClassQuery(value: MyValueClass? = null): MyValueClass = value ?: MyValueClass("default")\n}\n\nclass MySchemaGeneratorHooks : SchemaGeneratorHooks {\n    override fun willGenerateGraphQLType(type: KType): GraphQLType? = when (type.classifier) {\n        MyValueClass::class -> Scalars.GraphQLString\n        else -> null\n    }\n}\n\nclass MyValueUnboxer : IDValueUnboxer() {\n    override fun unbox(value: Any?): Any? = when (value) {\n        is MyValueClass -> `object`.value\n        else -> super.unbox(`object`)\n    }\n}\n\nval config = SchemaGeneratorConfig(\n    supportedPackages = listOf("com.example"),\n    hooks = MySchemaGeneratorHooks()\n)\nval schema = toSchema(\n    config = config,\n    queries = listOf(TopLevelObject(MyQuery()))\n)\nval graphQL = GraphQL.newGraphQL(graphQLSchema)\n    .valueUnboxer(MyValueUnboxer())\n    .build()\n'})}),"\n",(0,s.jsx)(n.p,{children:"This will generate a schema that exposes value classes as the corresponding wrapped type:"}),"\n",(0,s.jsx)(n.pre,{children:(0,s.jsx)(n.code,{className:"language-graphql",children:"type Query {\n  inlineValueClassQuery(value: String): String!\n}\n"})}),"\n",(0,s.jsxs)(n.admonition,{type:"note",children:[(0,s.jsxs)(n.p,{children:["GraphQL ID scalar type is represented using inline value class. When registering additional inline value classes you should extend the ",(0,s.jsx)(n.code,{children:"IDValueUnboxer"})," to ensure IDs will be correctly processed. Alternatively, extend ",(0,s.jsx)(n.code,{children:"DefaultValueUnboxer"})," and handle the ",(0,s.jsx)(n.code,{children:"ID"})," value class as above."]}),(0,s.jsxs)(n.p,{children:["If you are using ",(0,s.jsx)(n.code,{children:"graphql-kotlin-spring-server"})," you should create an instance of your bean as"]}),(0,s.jsx)(n.pre,{children:(0,s.jsx)(n.code,{className:"language-kotlin",children:"@Bean\nfun idValueUnboxer(): IDValueUnboxer = MyValueUnboxer()\n"})})]}),"\n",(0,s.jsx)(n.h4,{id:"representing-unwrapped-value-classes-in-the-schema-as-a-custom-scalar-type",children:"Representing Unwrapped Value Classes in the Schema as a Custom Scalar Type"}),"\n",(0,s.jsx)(n.p,{children:"In many cases, it may be useful to represent value classes in the schema as a custom scalar type, as the additional type information is often useful for clients. In this form, the value class is unwrapped, but uses a custom scalar type to preserve the extra type information."}),"\n",(0,s.jsx)(n.p,{children:"To do this, define a coercer for the value class that transforms it to and from the underlying type, and register it with the custom schema hooks:"}),"\n",(0,s.jsx)(n.pre,{children:(0,s.jsx)(n.code,{className:"language-kotlin",children:'val graphqlMyValueClassType: GraphQLScalarType = GraphQLScalarType.newScalar()\n  .name("MyValueClass")\n  .description(\n    """\n    |Represents my value class as a String value.\n    |""".trimMargin()\n  )\n  .coercing(MyValueClassCoercing)\n  .build()\n\nobject MyValueClassCoercing : Coercing<MyValueClass, String> {\n  override fun parseValue(input: Any, graphQLContext: GraphQLContext, locale: Locale): MyValueClass = ...\n  override fun parseLiteral(input: Value<*>, variables: CoercedVariables, graphQLContext: GraphQLContext, locale: Locale): MyValueClass = ...\n  override fun serialize(dataFetcherResult: Any, graphQLContext: GraphQLContext, locale: Locale): String = ...\n}\n\nclass CustomSchemaGeneratorHooks : SchemaGeneratorHooks {\n  override fun willGenerateGraphQLType(type: KType): GraphQLType? = when (type.classifier as? KClass<*>) {\n    MyValueClass::class -> graphqlMyValueClassType\n    else -> null\n  }\n}\n'})}),"\n",(0,s.jsx)(n.p,{children:"This will generate the schema that exposes value classes as a scalar type:"}),"\n",(0,s.jsx)(n.pre,{children:(0,s.jsx)(n.code,{className:"language-graphql",children:"scalar MyValueClass\n\ntype Query {\n  inlineValueClassQuery(value: MyValueClass): MyValueClass!\n}\n"})}),"\n",(0,s.jsx)(n.h4,{id:"representing-value-classes-in-the-schema-as-objects",children:"Representing Value Classes in the Schema as Objects"}),"\n",(0,s.jsx)(n.p,{children:"To do this, simply use the value class directly without defining any coercers or unboxers as in the previous sections."}),"\n",(0,s.jsx)(n.p,{children:"This will generate the schema that exposes value classes as a wrapped type, similar to a regular class:"}),"\n",(0,s.jsx)(n.pre,{children:(0,s.jsx)(n.code,{className:"language-graphql",children:"input MyValueClassInput {\n    value: String!\n}\n\ntype MyValueClass {\n    value: String!\n}\n\ntype Query {\n  inlineValueClassQuery(value: MyValueClassInput): MyValueClass!\n}\n"})}),"\n",(0,s.jsx)(n.h2,{id:"common-issues",children:"Common Issues"}),"\n",(0,s.jsx)(n.h3,{id:"extended-scalars",children:"Extended Scalars"}),"\n",(0,s.jsxs)(n.p,{children:["By default, ",(0,s.jsx)(n.code,{children:"graphql-kotlin"})," only supports the primitive scalar types listed above. If you are looking to use common java types as scalars, you need to include the ",(0,s.jsx)(n.a,{href:"https://github.com/graphql-java/graphql-java-extended-scalars",children:"graphql-java-extended-scalars"})," library and set up the hooks (see above), or write the logic yourself for how to resolve these custom scalars."]}),"\n",(0,s.jsxs)(n.p,{children:["The most popular types that require extra configuration are: ",(0,s.jsx)(n.code,{children:"LocalDate"}),", ",(0,s.jsx)(n.code,{children:"DateTime"}),", ",(0,s.jsx)(n.code,{children:"Instant"}),", ",(0,s.jsx)(n.code,{children:"ZonedDateTime"}),", ",(0,s.jsx)(n.code,{children:"URL"}),", ",(0,s.jsx)(n.code,{children:"UUID"})]}),"\n",(0,s.jsx)(n.h3,{id:"typenotsupportedexception",children:(0,s.jsx)(n.code,{children:"TypeNotSupportedException"})}),"\n",(0,s.jsxs)(n.p,{children:["If you see the following message ",(0,s.jsx)(n.code,{children:"Cannot convert ** since it is not a valid GraphQL type or outside the supported packages ***"}),". This means that you need to update the ",(0,s.jsx)(n.a,{href:"/graphql-kotlin/docs/9.x.x/schema-generator/customizing-schemas/generator-config",children:"generator configuration"})," to include the package of your type or you did not properly set up the hooks to register the new type."]})]})}function h(e={}){const{wrapper:n}={...(0,r.R)(),...e.components};return n?(0,s.jsx)(n,{...e,children:(0,s.jsx)(d,{...e})}):d(e)}},28453:(e,n,a)=>{a.d(n,{R:()=>t,x:()=>i});var s=a(96540);const r={},l=s.createContext(r);function t(e){const n=s.useContext(l);return s.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function i(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(r):e.components||r:t(e.components),s.createElement(l.Provider,{value:n},e.children)}}}]);