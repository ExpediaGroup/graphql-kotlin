"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[1893],{82783:(e,t,r)=>{r.r(t),r.d(t,{assets:()=>d,contentTitle:()=>s,default:()=>h,frontMatter:()=>i,metadata:()=>a,toc:()=>l});var n=r(74848),o=r(28453);const i={id:"type-resolution",title:"Federated Type Resolution"},s=void 0,a={id:"schema-generator/federation/type-resolution",title:"Federated Type Resolution",description:"In traditional (i.e. non-federated) GraphQL servers, each one of the output types is accessible through a traversal of",source:"@site/versioned_docs/version-6.x.x/schema-generator/federation/type-resolution.md",sourceDirName:"schema-generator/federation",slug:"/schema-generator/federation/type-resolution",permalink:"/graphql-kotlin/docs/6.x.x/schema-generator/federation/type-resolution",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-6.x.x/schema-generator/federation/type-resolution.md",tags:[],version:"6.x.x",lastUpdatedBy:"Tasuku Nakagawa",lastUpdatedAt:1714237421,formattedLastUpdatedAt:"Apr 27, 2024",frontMatter:{id:"type-resolution",title:"Federated Type Resolution"},sidebar:"docs",previous:{title:"Federated Directives",permalink:"/graphql-kotlin/docs/6.x.x/schema-generator/federation/federated-directives"},next:{title:"Federation Tracing",permalink:"/graphql-kotlin/docs/6.x.x/schema-generator/federation/federation-tracing"}},d={},l=[{value:"<code>_entities</code> query",id:"_entities-query",level:2},{value:"Federated Type Resolver",id:"federated-type-resolver",level:3}];function c(e){const t={a:"a",admonition:"admonition",code:"code",h2:"h2",h3:"h3",p:"p",pre:"pre",...(0,o.R)(),...e.components};return(0,n.jsxs)(n.Fragment,{children:[(0,n.jsx)(t.p,{children:"In traditional (i.e. non-federated) GraphQL servers, each one of the output types is accessible through a traversal of\nthe GraphQL schema from a corresponding query, mutation or subscription root type. Since federated GraphQL types might\nbe accessed outside of the query path we need a mechanism to access them in a consistent manner."}),"\n",(0,n.jsxs)(t.h2,{id:"_entities-query",children:[(0,n.jsx)(t.code,{children:"_entities"})," query"]}),"\n",(0,n.jsxs)(t.p,{children:["A federated GraphQL server provides a custom ",(0,n.jsx)(t.code,{children:"_entities"})," query that allows retrieving any of the federated extended types.\nThe ",(0,n.jsx)(t.code,{children:"_entities"}),' query accept list of "representation" objects that provide all required fields to resolve the type and\nreturn an ',(0,n.jsx)(t.code,{children:"_Entity"})," union type of all supported federated types. Representation objects are just a map of all the fields\nreferenced in ",(0,n.jsx)(t.code,{children:"@key"})," directives as well as the target ",(0,n.jsx)(t.code,{children:"__typename"})," information. If federated query type fragments also\nreference fields with ",(0,n.jsx)(t.code,{children:"@requires"})," and ",(0,n.jsx)(t.code,{children:"@provides"})," directives, then those referenced fields should also be specified in\nthe target representation object."]}),"\n",(0,n.jsx)(t.admonition,{type:"note",children:(0,n.jsxs)(t.p,{children:[(0,n.jsx)(t.code,{children:"_entities"})," queries are automatically handled by the federated gateway and their usage is transparent for the gateway clients.\n",(0,n.jsx)(t.code,{children:"EntityResolver"})," provided by the ",(0,n.jsx)(t.code,{children:"graphql-kotlin-federation"})," module relies on the same coroutine scope propagation as the\ndefault ",(0,n.jsx)(t.code,{children:"FunctionDataFetcher"}),". See ",(0,n.jsx)(t.a,{href:"/graphql-kotlin/docs/6.x.x/schema-generator/execution/async-models",children:"asynchronous models documentation"})," for additional details."]})}),"\n",(0,n.jsx)(t.pre,{children:(0,n.jsx)(t.code,{className:"language-graphql",children:"query ($_representations: [_Any!]!) {\n  _entities(representations: $_representations) {\n    ... on SomeFederatedType {\n      fieldA\n      fieldB\n    }\n  }\n}\n"})}),"\n",(0,n.jsx)(t.h3,{id:"federated-type-resolver",children:"Federated Type Resolver"}),"\n",(0,n.jsxs)(t.p,{children:["In order to simplify the integrations, ",(0,n.jsx)(t.code,{children:"graphql-kotlin-federation"})," provides a default ",(0,n.jsx)(t.code,{children:"_entities"})," query resolver that\nretrieves the\n",(0,n.jsx)(t.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/master/generator/graphql-kotlin-federation/src/main/kotlin/com/expediagroup/graphql/generator/federation/execution/FederatedTypeResolver.kt",children:"FederatedTypeResolver"}),"\nthat is used to resolve the specified ",(0,n.jsx)(t.code,{children:"__typename"}),"."]}),"\n",(0,n.jsxs)(t.p,{children:[(0,n.jsx)(t.code,{children:"FederatedTypeResolver.typeName"})," specifies the GraphQL type name that should match up to the ",(0,n.jsx)(t.code,{children:"__typename"})," field in the ",(0,n.jsx)(t.code,{children:"_entities"})," query."]}),"\n",(0,n.jsxs)(t.p,{children:[(0,n.jsx)(t.code,{children:"FederatedTypeResolver.resolve"})," accepts a list of representations of the target types which should be resolved in the same order\nas they were specified in the list of representations. Each passed in representation should either be resolved to a\ntarget entity or ",(0,n.jsx)(t.code,{children:"NULL"})," if entity cannot be resolved."]}),"\n",(0,n.jsx)(t.pre,{children:(0,n.jsx)(t.code,{className:"language-kotlin",children:'// This service does not own the "Product" type but is extending it with new fields\n@KeyDirective(fields = FieldSet("id"))\n@ExtendsDirective\nclass Product(@ExternalDirective val id: String) {\n  fun newField(): String = getNewFieldByProductId(id)\n}\n\n// This is how the "Product" class is created from the "_entities" query\nclass ProductResolver : FederatedTypeResolver<Product> {\n    override val typeName: String = "Product"\n\n    override suspend fun resolve(representations: List<Map<String, Any>>): List<Product?> = representations.map {\n        val id = it["id"]?.toString()\n\n        // Instantiate product using id, otherwise return null\n        if (id != null) {\n            Product(id)\n        } else {\n            null\n        }\n    }\n}\n\n// If you are using "graphql-kotlin-spring-server", your FederatedTypeResolvers can be marked as Spring beans\n// and will automatically be added to the hooks\nval resolvers = listOf(productResolver)\nval hooks = FederatedSchemaGeneratorHooks(resolvers)\nval config = FederatedSchemaGeneratorConfig(supportedPackages = listOf("org.example"), hooks = hooks)\nval schema = toFederatedSchema(config)\n'})})]})}function h(e={}){const{wrapper:t}={...(0,o.R)(),...e.components};return t?(0,n.jsx)(t,{...e,children:(0,n.jsx)(c,{...e})}):c(e)}},28453:(e,t,r)=>{r.d(t,{R:()=>s,x:()=>a});var n=r(96540);const o={},i=n.createContext(o);function s(e){const t=n.useContext(i);return n.useMemo((function(){return"function"==typeof e?e(t):{...t,...e}}),[t,e])}function a(e){let t;return t=e.disableParentContext?"function"==typeof e.components?e.components(o):e.components||o:s(e.components),n.createElement(i.Provider,{value:t},e.children)}}}]);