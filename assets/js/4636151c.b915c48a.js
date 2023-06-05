"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[1514],{22183:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>p,contentTitle:()=>d,default:()=>m,frontMatter:()=>s,metadata:()=>l,toc:()=>c});var r=n(87462),a=n(63366),i=(n(67294),n(3905)),o=(n(95657),["components"]),s={id:"type-resolution",title:"Federated Type Resolution"},d=void 0,l={unversionedId:"schema-generator/federation/type-resolution",id:"version-4.x.x/schema-generator/federation/type-resolution",title:"Federated Type Resolution",description:"In traditional (i.e. non-federated) GraphQL servers, each one of the output types is accessible through a traversal of",source:"@site/versioned_docs/version-4.x.x/schema-generator/federation/type-resolution.md",sourceDirName:"schema-generator/federation",slug:"/schema-generator/federation/type-resolution",permalink:"/graphql-kotlin/docs/4.x.x/schema-generator/federation/type-resolution",draft:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-4.x.x/schema-generator/federation/type-resolution.md",tags:[],version:"4.x.x",lastUpdatedBy:"Shane Myrick",lastUpdatedAt:1685995381,formattedLastUpdatedAt:"Jun 5, 2023",frontMatter:{id:"type-resolution",title:"Federated Type Resolution"},sidebar:"version-4.x.x/docs",previous:{title:"Federated Directives",permalink:"/graphql-kotlin/docs/4.x.x/schema-generator/federation/federated-directives"},next:{title:"Federation Tracing",permalink:"/graphql-kotlin/docs/4.x.x/schema-generator/federation/federation-tracing"}},p={},c=[{value:"<code>_entities</code> query",id:"_entities-query",level:2},{value:"Federated Type Resolver",id:"federated-type-resolver",level:3}],h={toc:c},u="wrapper";function m(e){var t=e.components,n=(0,a.Z)(e,o);return(0,i.kt)(u,(0,r.Z)({},h,n,{components:t,mdxType:"MDXLayout"}),(0,i.kt)("p",null,"In traditional (i.e. non-federated) GraphQL servers, each one of the output types is accessible through a traversal of\nthe GraphQL schema from a corresponding query, mutation or subscription root type. Since federated GraphQL types might\nbe accessed outside of the query path we need a mechanism to access them in a consistent manner."),(0,i.kt)("h2",{id:"_entities-query"},(0,i.kt)("inlineCode",{parentName:"h2"},"_entities")," query"),(0,i.kt)("p",null,"A federated GraphQL server provides a custom ",(0,i.kt)("inlineCode",{parentName:"p"},"_entities")," query that allows retrieving any of the federated extended types.\nThe ",(0,i.kt)("inlineCode",{parentName:"p"},"_entities"),' query accept list of "representation" objects that provide all required fields to resolve the type and\nreturn an ',(0,i.kt)("inlineCode",{parentName:"p"},"_Entity")," union type of all supported federated types. Representation objects are just a map of all the fields\nreferenced in ",(0,i.kt)("inlineCode",{parentName:"p"},"@key")," directives as well as the target ",(0,i.kt)("inlineCode",{parentName:"p"},"__typename")," information. If federated query type fragments also\nreference fields with ",(0,i.kt)("inlineCode",{parentName:"p"},"@requires")," and ",(0,i.kt)("inlineCode",{parentName:"p"},"@provides")," directives, then those referenced fields should also be specified in\nthe target representation object."),(0,i.kt)("admonition",{type:"note"},(0,i.kt)("p",{parentName:"admonition"},(0,i.kt)("inlineCode",{parentName:"p"},"_entities")," queries are automatically generated by the federated gateway and their usage is transparent for the gateway clients.")),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-graphql"},"query ($_representations: [_Any!]!) {\n  _entities(representations: $_representations) {\n    ... on SomeFederatedType {\n      fieldA\n      fieldB\n    }\n  }\n}\n")),(0,i.kt)("h3",{id:"federated-type-resolver"},"Federated Type Resolver"),(0,i.kt)("p",null,"In order to simplify the integrations, ",(0,i.kt)("inlineCode",{parentName:"p"},"graphql-kotlin-federation")," provides a default ",(0,i.kt)("inlineCode",{parentName:"p"},"_entities")," query resolver that\nretrieves the\n",(0,i.kt)("a",{parentName:"p",href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/master/generator/graphql-kotlin-federation/src/main/kotlin/com/expediagroup/graphql/generator/federation/execution/FederatedTypeResolver.kt"},"FederatedTypeResolver"),"\nthat is used to resolve the specified ",(0,i.kt)("inlineCode",{parentName:"p"},"__typename"),"."),(0,i.kt)("p",null,(0,i.kt)("inlineCode",{parentName:"p"},"FederatedTypeResolver.typeName")," specifies the GraphQL type name that should match up to the ",(0,i.kt)("inlineCode",{parentName:"p"},"__typename")," field in the ",(0,i.kt)("inlineCode",{parentName:"p"},"_entities")," query."),(0,i.kt)("p",null,(0,i.kt)("inlineCode",{parentName:"p"},"FederatedTypeResolver.resolve")," accepts a list of representations of the target types which should be resolved in the same order\nas they were specified in the list of representations. Each passed in representation should either be resolved to a\ntarget entity or ",(0,i.kt)("inlineCode",{parentName:"p"},"NULL")," if entity cannot be resolved."),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-kotlin"},'// This service does not own the "Product" type but is extending it with new fields\n@KeyDirective(fields = FieldSet("id"))\n@ExtendsDirective\nclass Product(@ExternalDirective val id: String) {\n  fun newField(): String = getNewFieldByProductId(id)\n}\n\n// This is how the "Product" class is created from the "_entities" query\nclass ProductResolver : FederatedTypeResolver<Product> {\n    override val typeName: String = "Product"\n\n    override suspend fun resolve(representations: List<Map<String, Any>>): List<Product?> = representations.map {\n        val id = it["id"]?.toString()\n\n        // Instantiate product using id, otherwise return null\n        if (id != null) {\n            Product(id)\n        } else {\n            null\n        }\n    }\n}\n\n// If you are using "graphql-kotlin-spring-server", your FederatedTypeResolvers can be marked as Spring beans\n// and will automatically be added to the hooks\nval resolvers = listOf(productResolver)\nval hooks = FederatedSchemaGeneratorHooks(resolvers)\nval config = FederatedSchemaGeneratorConfig(supportedPackages = listOf("org.example"), hooks = hooks)\nval schema = toFederatedSchema(config)\n')))}m.isMDXComponent=!0}}]);