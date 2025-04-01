"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[8789],{43405:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>o,contentTitle:()=>i,default:()=>h,frontMatter:()=>d,metadata:()=>s,toc:()=>c});var r=n(74848),a=n(28453);const d={id:"federated-schemas",title:"Federated Schemas"},i=void 0,s={id:"schema-generator/federation/federated-schemas",title:"Federated Schemas",description:"graphql-kotlin-federation library extends the functionality of the graphql-kotlin-schema-generator and allows you to",source:"@site/versioned_docs/version-5.x.x/schema-generator/federation/federated-schemas.md",sourceDirName:"schema-generator/federation",slug:"/schema-generator/federation/federated-schemas",permalink:"/graphql-kotlin/docs/5.x.x/schema-generator/federation/federated-schemas",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-5.x.x/schema-generator/federation/federated-schemas.md",tags:[],version:"5.x.x",lastUpdatedBy:"Templeton Peck",lastUpdatedAt:1743524928e3,frontMatter:{id:"federated-schemas",title:"Federated Schemas"},sidebar:"docs",previous:{title:"Apollo Federation",permalink:"/graphql-kotlin/docs/5.x.x/schema-generator/federation/apollo-federation"},next:{title:"Federated Directives",permalink:"/graphql-kotlin/docs/5.x.x/schema-generator/federation/federated-directives"}},o={},c=[{value:"Base Schema",id:"base-schema",level:3},{value:"Extended Schema",id:"extended-schema",level:4},{value:"Federated GraphQL schema",id:"federated-graphql-schema",level:4}];function l(e){const t={a:"a",admonition:"admonition",code:"code",h3:"h3",h4:"h4",p:"p",pre:"pre",strong:"strong",...(0,a.R)(),...e.components};return(0,r.jsxs)(r.Fragment,{children:[(0,r.jsxs)(t.p,{children:[(0,r.jsx)(t.code,{children:"graphql-kotlin-federation"})," library extends the functionality of the ",(0,r.jsx)(t.code,{children:"graphql-kotlin-schema-generator"})," and allows you to\neasily generate federated GraphQL schemas directly from the code. Federated schema is generated by calling\n",(0,r.jsx)(t.code,{children:"toFederatedSchema"})," function that accepts federated configuration as well as a list of regular queries, mutations and\nsubscriptions exposed by the schema."]}),"\n",(0,r.jsxs)(t.p,{children:["All ",(0,r.jsx)(t.a,{href:"federated-directives",children:"federated directives"})," are provided as annotations that are used to decorate your classes,\nproperties and functions. Since federated types might not be accessible through the regular query execution path, they\nare explicitly picked up by the schema generator based on their directives. Due to the above, we also need to provide\na way to instantiate the underlying federated objects by implementing corresponding ",(0,r.jsx)(t.code,{children:"FederatedTypeResolvers"}),". See\n",(0,r.jsx)(t.a,{href:"type-resolution",children:"type resolution wiki"})," for more details on how federated types are resolved. Final federated schema\nis then generated by invoking the ",(0,r.jsx)(t.code,{children:"toFederatedSchema"})," function\n(",(0,r.jsx)(t.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/master/generator/graphql-kotlin-federation/src/main/kotlin/com/expediagroup/graphql/generator/federation/toFederatedSchema.kt#L34",children:"link"}),")."]}),"\n",(0,r.jsxs)(t.p,{children:[(0,r.jsx)(t.strong,{children:"In order to generate valid federated schemas, you will need to annotate both your base schema and the one extending\nit"}),". Federated Gateway (e.g. Apollo) will then combine the individual graphs to form single federated graph."]}),"\n",(0,r.jsx)(t.admonition,{type:"caution",children:(0,r.jsxs)(t.p,{children:["If you are using custom ",(0,r.jsx)(t.code,{children:"Query"})," type then all of you federated GraphQL services have to use the same type. It is\nnot possible for federated services to have different definitions of ",(0,r.jsx)(t.code,{children:"Query"})," type."]})}),"\n",(0,r.jsx)(t.h3,{id:"base-schema",children:"Base Schema"}),"\n",(0,r.jsxs)(t.p,{children:["Base schema defines GraphQL types that will be extended by schemas exposed by other GraphQL services. In the example\nbelow, we define base ",(0,r.jsx)(t.code,{children:"Product"})," type with ",(0,r.jsx)(t.code,{children:"id"})," and ",(0,r.jsx)(t.code,{children:"description"})," fields. ",(0,r.jsx)(t.code,{children:"id"})," is the primary key that uniquely\nidentifies the ",(0,r.jsx)(t.code,{children:"Product"})," type object and is specified in ",(0,r.jsx)(t.code,{children:"@key"})," directive. Since it is a base schema that doesn't expose\nany extended functionality our FederatedTypeRegistry does not include any federated resolvers."]}),"\n",(0,r.jsx)(t.pre,{children:(0,r.jsx)(t.code,{className:"language-kotlin",children:'@KeyDirective(fields = FieldSet("id"))\ndata class Product(val id: Int, val description: String)\n\nclass ProductQuery {\n  fun product(id: Int): Product? {\n    // grabs product from a data source, might return null\n  }\n}\n\n// Generate the schema\nval hooks = FederatedSchemaGeneratorHooks(emptyList())\nval config = FederatedSchemaGeneratorConfig(supportedPackages = listOf("org.example"), hooks = hooks)\nval queries = listOf(TopLevelObject(ProductQuery()))\n\ntoFederatedSchema(config, queries)\n'})}),"\n",(0,r.jsx)(t.p,{children:"Example above generates the following schema with additional federated types:"}),"\n",(0,r.jsx)(t.pre,{children:(0,r.jsx)(t.code,{className:"language-graphql",children:'schema {\n  query: Query\n}\n\nunion _Entity = Product\n\ntype Product @key(fields : "id") {\n  description: String!\n  id: Int!\n}\n\ntype Query @extends {\n  _entities(representations: [_Any!]!): [_Entity]!\n  _service: _Service\n  product(id: Int!): Product\n}\n\ntype _Service {\n  sdl: String!\n}\n'})}),"\n",(0,r.jsx)(t.h4,{id:"extended-schema",children:"Extended Schema"}),"\n",(0,r.jsxs)(t.p,{children:["Extended federated GraphQL schemas provide additional functionality to the types already exposed by other GraphQL\nservices. In the example below, ",(0,r.jsx)(t.code,{children:"Product"})," type is extended to add new ",(0,r.jsx)(t.code,{children:"reviews"})," field to it. Primary key needed to\ninstantiate the ",(0,r.jsx)(t.code,{children:"Product"})," type (i.e. ",(0,r.jsx)(t.code,{children:"id"}),") has to match the ",(0,r.jsx)(t.code,{children:"@key"})," definition on the base type. Since primary keys are\ndefined on the base type and are only referenced from the extended type, all of the fields that are part of the field\nset specified in ",(0,r.jsx)(t.code,{children:"@key"})," directive have to be marked as ",(0,r.jsx)(t.code,{children:"@external"}),'. Finally, we also need to specify an "entry point"\nfor the federated type\u200a-\u200awe need to create a FederatedTypeResolver that will be used to instantiate the federated\n',(0,r.jsx)(t.code,{children:"Product"})," type when processing federated queries."]}),"\n",(0,r.jsx)(t.pre,{children:(0,r.jsx)(t.code,{className:"language-kotlin",children:'@KeyDirective(fields = FieldSet("id"))\n@ExtendsDirective\ndata class Product(@ExternalDirective val id: Int) {\n    // Add the "reviews" field to the type\n    suspend fun reviews(): List<Review> = getReviewByProductId(id)\n}\n\ndata class Review(val reviewId: String, val text: String)\n\n// Resolve a "Product" type from the _entities query\nclass ProductResolver : FederatedTypeResolver<Product> {\n    override val typeName = "Product"\n\n    override suspend fun resolve(environment: DataFetchingEnvironment, representations: List<Map<String, Any>>): List<Product?> = representations.map { keys ->\n        keys["id"]?.toString()?.toIntOrNull()?.let { id -> Product(id) }\n    }\n}\n\n// Generate the schema\nval resolvers = listOf(ProductResolver())\nval hooks = FederatedSchemaGeneratorHooks(resolvers)\nval config = FederatedSchemaGeneratorConfig(supportedPackages = listOf("org.example"), hooks = hooks)\n\ntoFederatedSchema(config)\n'})}),"\n",(0,r.jsx)(t.p,{children:"Our extended schema will then be generated as:"}),"\n",(0,r.jsx)(t.pre,{children:(0,r.jsx)(t.code,{className:"language-graphql",children:'schema {\n  query: Query\n}\n\nunion _Entity = Product\n\ntype Product @extends @key(fields : "id") {\n  id: Int! @external\n  reviews: [Review!]!\n}\n\ntype Query @extends {\n  _entities(representations: [_Any!]!): [_Entity]!\n  _service: _Service\n}\n\ntype Review {\n  reviewId: String!\n  text: String!\n}\n\ntype _Service {\n  sdl: String!\n}\n'})}),"\n",(0,r.jsx)(t.h4,{id:"federated-graphql-schema",children:"Federated GraphQL schema"}),"\n",(0,r.jsx)(t.p,{children:"Once we have both base and extended GraphQL services up and running, we will also need to configure Federated Gateway\nto combine them into a single schema. Using the examples above, our final federated schema will be generated as:"}),"\n",(0,r.jsx)(t.pre,{children:(0,r.jsx)(t.code,{className:"language-graphql",children:"schema {\n  query: Query\n}\n\ntype Product {\n  description: String!\n  id: Int!\n  reviews: [Review!]!\n}\n\ntype Review {\n  reviewId: String!\n  text: String!\n}\n\ntype Query {\n  product(id: String!): Product\n}\n"})}),"\n",(0,r.jsxs)(t.p,{children:["See our ",(0,r.jsx)(t.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/examples/federation",children:"federation example"})," for additional details."]})]})}function h(e={}){const{wrapper:t}={...(0,a.R)(),...e.components};return t?(0,r.jsx)(t,{...e,children:(0,r.jsx)(l,{...e})}):l(e)}},28453:(e,t,n)=>{n.d(t,{R:()=>i,x:()=>s});var r=n(96540);const a={},d=r.createContext(a);function i(e){const t=r.useContext(d);return r.useMemo((function(){return"function"==typeof e?e(t):{...t,...e}}),[t,e])}function s(e){let t;return t=e.disableParentContext?"function"==typeof e.components?e.components(a):e.components||a:i(e.components),r.createElement(d.Provider,{value:t},e.children)}}}]);