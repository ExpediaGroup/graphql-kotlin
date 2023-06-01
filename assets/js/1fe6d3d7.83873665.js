"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[629],{57901:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>p,contentTitle:()=>s,default:()=>m,frontMatter:()=>o,metadata:()=>l,toc:()=>c});var a=n(87462),r=n(63366),i=(n(67294),n(3905)),d=(n(95657),["components"]),o={id:"federated-schemas",title:"Federated Schemas"},s=void 0,l={unversionedId:"schema-generator/federation/federated-schemas",id:"version-6.x.x/schema-generator/federation/federated-schemas",title:"Federated Schemas",description:"graphql-kotlin-federation library extends the functionality of the graphql-kotlin-schema-generator and allows you to",source:"@site/versioned_docs/version-6.x.x/schema-generator/federation/federated-schemas.md",sourceDirName:"schema-generator/federation",slug:"/schema-generator/federation/federated-schemas",permalink:"/graphql-kotlin/docs/schema-generator/federation/federated-schemas",draft:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-6.x.x/schema-generator/federation/federated-schemas.md",tags:[],version:"6.x.x",lastUpdatedBy:"Samuel Vazquez",lastUpdatedAt:1685659104,formattedLastUpdatedAt:"Jun 1, 2023",frontMatter:{id:"federated-schemas",title:"Federated Schemas"},sidebar:"docs",previous:{title:"Apollo Federation",permalink:"/graphql-kotlin/docs/schema-generator/federation/apollo-federation"},next:{title:"Federated Directives",permalink:"/graphql-kotlin/docs/schema-generator/federation/federated-directives"}},p={},c=[{value:"Base Subgraph",id:"base-subgraph",level:3},{value:"Extended Subgraph",id:"extended-subgraph",level:3},{value:"Federated Supergraph",id:"federated-supergraph",level:3}],h={toc:c},u="wrapper";function m(e){var t=e.components,n=(0,r.Z)(e,d);return(0,i.kt)(u,(0,a.Z)({},h,n,{components:t,mdxType:"MDXLayout"}),(0,i.kt)("p",null,(0,i.kt)("inlineCode",{parentName:"p"},"graphql-kotlin-federation")," library extends the functionality of the ",(0,i.kt)("inlineCode",{parentName:"p"},"graphql-kotlin-schema-generator")," and allows you to\neasily generate federated GraphQL schemas directly from the code. Federated schema is generated by calling\n",(0,i.kt)("inlineCode",{parentName:"p"},"toFederatedSchema")," function that accepts federated configuration as well as a list of regular queries, mutations and\nsubscriptions exposed by the schema."),(0,i.kt)("p",null,"All ",(0,i.kt)("a",{parentName:"p",href:"federated-directives"},"federated directives")," are provided as annotations that are used to decorate your classes,\nproperties and functions. Since federated types might not be accessible through the regular query execution path, they\nare explicitly picked up by the schema generator based on their directives. Due to the above, we also need to provide\na way to instantiate the underlying federated objects by implementing corresponding ",(0,i.kt)("inlineCode",{parentName:"p"},"FederatedTypeResolvers"),". See\n",(0,i.kt)("a",{parentName:"p",href:"type-resolution"},"type resolution wiki")," for more details on how federated types are resolved. Final federated schema\nis then generated by invoking the ",(0,i.kt)("inlineCode",{parentName:"p"},"toFederatedSchema")," function\n(",(0,i.kt)("a",{parentName:"p",href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/master/generator/graphql-kotlin-federation/src/main/kotlin/com/expediagroup/graphql/generator/federation/toFederatedSchema.kt#L34"},"link"),")."),(0,i.kt)("p",null,(0,i.kt)("strong",{parentName:"p"},"In order to generate valid federated schemas, you will need to annotate both your base schema and the one extending\nit"),". Federated Gateway (e.g. Apollo) will then combine the individual graphs to form single federated graph."),(0,i.kt)("admonition",{type:"caution"},(0,i.kt)("p",{parentName:"admonition"},"If you are using custom ",(0,i.kt)("inlineCode",{parentName:"p"},"Query")," type then all of you federated GraphQL services have to use the same type. It is\nnot possible for federated services to have different definitions of ",(0,i.kt)("inlineCode",{parentName:"p"},"Query")," type.")),(0,i.kt)("h3",{id:"base-subgraph"},"Base Subgraph"),(0,i.kt)("p",null,"Base schema defines GraphQL types that will be extended by schemas exposed by other GraphQL services. In the example\nbelow, we define base ",(0,i.kt)("inlineCode",{parentName:"p"},"Product")," type with ",(0,i.kt)("inlineCode",{parentName:"p"},"id")," and ",(0,i.kt)("inlineCode",{parentName:"p"},"description")," fields. ",(0,i.kt)("inlineCode",{parentName:"p"},"id")," is the primary key that uniquely\nidentifies the ",(0,i.kt)("inlineCode",{parentName:"p"},"Product")," type object and is specified in ",(0,i.kt)("inlineCode",{parentName:"p"},"@key")," directive. Since it is a base schema that doesn't expose\nany extended functionality our FederatedTypeRegistry does not include any federated resolvers."),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-kotlin"},'@KeyDirective(fields = FieldSet("id"))\ndata class Product(val id: Int, val description: String)\n\nclass ProductQuery {\n    fun product(id: Int): Product? {\n        // grabs product from a data source, might return null\n    }\n}\n\n// Generate the schema\nval hooks = FederatedSchemaGeneratorHooks(emptyList())\nval config = FederatedSchemaGeneratorConfig(supportedPackages = listOf("org.example"), hooks = hooks)\nval queries = listOf(TopLevelObject(ProductQuery()))\n\ntoFederatedSchema(config, queries)\n')),(0,i.kt)("p",null,"Example above generates the following schema with additional federated types:"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-graphql"},'schema {\n    query: Query\n}\n\nunion _Entity = Product\n\ntype Product @key(fields : "id") {\n    description: String!\n    id: Int!\n}\n\ntype Query @extends {\n    _entities(representations: [_Any!]!): [_Entity]!\n    _service: _Service!\n    product(id: Int!): Product\n}\n\ntype _Service {\n    sdl: String!\n}\n')),(0,i.kt)("h3",{id:"extended-subgraph"},"Extended Subgraph"),(0,i.kt)("p",null,"Extended federated GraphQL schemas provide additional functionality to the types already exposed by other GraphQL\nservices. In the example below, ",(0,i.kt)("inlineCode",{parentName:"p"},"Product")," type is extended to add new ",(0,i.kt)("inlineCode",{parentName:"p"},"reviews")," field to it. Primary key needed to\ninstantiate the ",(0,i.kt)("inlineCode",{parentName:"p"},"Product")," type (i.e. ",(0,i.kt)("inlineCode",{parentName:"p"},"id"),") has to match the ",(0,i.kt)("inlineCode",{parentName:"p"},"@key")," definition on the base type. Since primary keys are\ndefined on the base type and are only referenced from the extended type, all of the fields that are part of the field\nset specified in ",(0,i.kt)("inlineCode",{parentName:"p"},"@key")," directive have to be marked as ",(0,i.kt)("inlineCode",{parentName:"p"},"@external"),'. Finally, we also need to specify an "entry point"\nfor the federated type - we need to create a FederatedTypeResolver that will be used to instantiate the federated\n',(0,i.kt)("inlineCode",{parentName:"p"},"Product")," type when processing federated queries."),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-kotlin"},'@KeyDirective(fields = FieldSet("id"))\n@ExtendsDirective\ndata class Product(@ExternalDirective val id: Int) {\n    // Add the "reviews" field to the type\n    suspend fun reviews(): List<Review> = getReviewByProductId(id)\n}\n\ndata class Review(val reviewId: String, val text: String)\n\n// Resolve a "Product" type from the _entities query\nclass ProductResolver : FederatedTypeResolver<Product> {\n    override val typeName = "Product"\n\n    override suspend fun resolve(environment: DataFetchingEnvironment, representations: List<Map<String, Any>>): List<Product?> = representations.map { keys ->\n        keys["id"]?.toString()?.toIntOrNull()?.let { id -> Product(id) }\n    }\n}\n\n// Generate the schema\nval resolvers = listOf(ProductResolver())\nval hooks = FederatedSchemaGeneratorHooks(resolvers)\nval config = FederatedSchemaGeneratorConfig(supportedPackages = listOf("org.example"), hooks = hooks)\n\ntoFederatedSchema(config)\n')),(0,i.kt)("p",null,"Our extended schema will then be generated as:"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-graphql"},'schema {\n  query: Query\n}\n\nunion _Entity = Product\n\ntype Product @extends @key(fields : "id") {\n  id: Int! @external\n  reviews: [Review!]!\n}\n\ntype Query @extends {\n  _entities(representations: [_Any!]!): [_Entity]!\n  _service: _Service!\n}\n\ntype Review {\n  reviewId: String!\n  text: String!\n}\n\ntype _Service {\n  sdl: String!\n}\n')),(0,i.kt)("h3",{id:"federated-supergraph"},"Federated Supergraph"),(0,i.kt)("p",null,"Once we have both base and extended GraphQL services up and running, we will also need to configure Federated Gateway\nto combine them into a single supergraph schema. Using the examples above, our final federated schema will be generated as:"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-graphql"},"schema {\n  query: Query\n}\n\ntype Product {\n  description: String!\n  id: Int!\n  reviews: [Review!]!\n}\n\ntype Review {\n  reviewId: String!\n  text: String!\n}\n\ntype Query {\n  product(id: String!): Product\n}\n")),(0,i.kt)("p",null,"See our ",(0,i.kt)("a",{parentName:"p",href:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/examples/federation"},"federation example")," for additional details."))}m.isMDXComponent=!0}}]);