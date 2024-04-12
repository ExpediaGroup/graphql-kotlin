"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[201],{14140:(e,i,n)=>{n.r(i),n.d(i,{assets:()=>l,contentTitle:()=>a,default:()=>h,frontMatter:()=>s,metadata:()=>d,toc:()=>c});var t=n(74848),r=n(28453);const s={id:"federated-directives",title:"Federated Directives"},a=void 0,d={id:"schema-generator/federation/federated-directives",title:"Federated Directives",description:"graphql-kotlin supports a number of directives that can be used to annotate a schema and direct certain behaviors.",source:"@site/versioned_docs/version-7.x.x/schema-generator/federation/federated-directives.md",sourceDirName:"schema-generator/federation",slug:"/schema-generator/federation/federated-directives",permalink:"/graphql-kotlin/docs/schema-generator/federation/federated-directives",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-7.x.x/schema-generator/federation/federated-directives.md",tags:[],version:"7.x.x",lastUpdatedBy:"Curtis Cook",lastUpdatedAt:1712948770,formattedLastUpdatedAt:"Apr 12, 2024",frontMatter:{id:"federated-directives",title:"Federated Directives"},sidebar:"docs",previous:{title:"Federated Schemas",permalink:"/graphql-kotlin/docs/schema-generator/federation/federated-schemas"},next:{title:"Federated Type Resolution",permalink:"/graphql-kotlin/docs/schema-generator/federation/type-resolution"}},l={},c=[{value:"<code>@authenticated</code> directive",id:"authenticated-directive",level:2},{value:"<code>@composeDirective</code> directive",id:"composedirective-directive",level:2},{value:"<code>@contact</code> directive",id:"contact-directive",level:2},{value:"Example usage on the schema class:",id:"example-usage-on-the-schema-class",level:4},{value:"<code>@extends</code> directive",id:"extends-directive",level:2},{value:"Example",id:"example",level:4},{value:"<code>@external</code> directive",id:"external-directive",level:2},{value:"Example",id:"example-1",level:4},{value:"<code>@inaccessible</code> directive",id:"inaccessible-directive",level:2},{value:"Example",id:"example-2",level:4},{value:"<code>@interfaceObject</code> directive",id:"interfaceobject-directive",level:2},{value:"<code>@key</code> directive",id:"key-directive",level:2},{value:"Basic Example",id:"basic-example",level:4},{value:"Referencing External Entities",id:"referencing-external-entities",level:4},{value:"<code>@link</code> directive",id:"link-directive",level:2},{value:"<code>@LinkedSpec</code> annotation",id:"linkedspec-annotation",level:3},{value:"<code>@override</code> directive",id:"override-directive",level:2},{value:"Example",id:"example-3",level:4},{value:"<code>@policy</code> directive",id:"policy-directive",level:2},{value:"<code>@provides</code> directive",id:"provides-directive",level:2},{value:"Example 1:",id:"example-1-1",level:4},{value:"Example 2:",id:"example-2-1",level:4},{value:"<code>@requires</code> directive",id:"requires-directive",level:2},{value:"Example",id:"example-4",level:4},{value:"<code>@requiresScopes</code> directive",id:"requiresscopes-directive",level:2},{value:"<code>@shareable</code> directive",id:"shareable-directive",level:2},{value:"Example",id:"example-5",level:4},{value:"<code>@tag</code> directive",id:"tag-directive",level:2},{value:"Example",id:"example-6",level:4}];function o(e){const i={a:"a",admonition:"admonition",code:"code",h2:"h2",h3:"h3",h4:"h4",li:"li",ol:"ol",p:"p",pre:"pre",strong:"strong",...(0,r.R)(),...e.components};return(0,t.jsxs)(t.Fragment,{children:[(0,t.jsxs)(i.p,{children:[(0,t.jsx)(i.code,{children:"graphql-kotlin"})," supports a number of directives that can be used to annotate a schema and direct certain behaviors."]}),"\n",(0,t.jsxs)(i.p,{children:["For more details, see the ",(0,t.jsx)(i.a,{href:"https://www.apollographql.com/docs/federation/subgraph-spec/",children:"Apollo Federation Specification"}),"."]}),"\n",(0,t.jsxs)(i.h2,{id:"authenticated-directive",children:[(0,t.jsx)(i.code,{children:"@authenticated"})," directive"]}),"\n",(0,t.jsx)(i.pre,{children:(0,t.jsx)(i.code,{className:"language-graphql",children:"directive @authenticated on\n    ENUM\n  | FIELD_DEFINITION\n  | INTERFACE\n  | OBJECT\n  | SCALAR\n"})}),"\n",(0,t.jsxs)(i.p,{children:["Directive that is used to indicate that the target element is accessible only to the authenticated supergraph users. For more granular access control, see the\n[",(0,t.jsx)(i.code,{children:"@requiresScopes"}),"[#requirescope-directive] directive usage. Refer to the ",(0,t.jsx)(i.a,{href:"https://www.apollographql.com/docs/router/configuration/authorization#authenticated",children:"Apollo Router documentation"}),"\nfor additional details."]}),"\n",(0,t.jsxs)(i.h2,{id:"composedirective-directive",children:[(0,t.jsx)(i.code,{children:"@composeDirective"})," directive"]}),"\n",(0,t.jsx)(i.pre,{children:(0,t.jsx)(i.code,{className:"language-graphql",children:"directive @composeDirective(name: String!) repeatable on SCHEMA\n"})}),"\n",(0,t.jsxs)(i.p,{children:["By default, Supergraph schema excludes all custom directives. The ",(0,t.jsx)(i.code,{children:"@composeDirective"})," is used to specify custom directives that should be exposed in the Supergraph schema."]}),"\n",(0,t.jsx)(i.p,{children:"In order to use composed directive, you subgraph needs to"}),"\n",(0,t.jsxs)(i.ol,{children:["\n",(0,t.jsx)(i.li,{children:"contain your custom directive definition"}),"\n",(0,t.jsx)(i.li,{children:"import your custom directive from a corresponding link spec"}),"\n",(0,t.jsxs)(i.li,{children:["apply ",(0,t.jsx)(i.code,{children:"@composeDirective"})," with custom directive name on your schema"]}),"\n"]}),"\n",(0,t.jsxs)(i.p,{children:["Example:\nGiven ",(0,t.jsx)(i.code,{children:"@custom"})," directive we can preserve it in the Supergraph schema"]}),"\n",(0,t.jsx)(i.pre,{children:(0,t.jsx)(i.code,{className:"language-kotlin",children:'// 1. directive definition\n@GraphQLDirective(name = "custom", locations = [Introspection.DirectiveLocation.FIELD_DEFINITION])\nannotation class CustomDirective\n\n@LinkDirective(url = "https://myspecs.dev/myCustomDirective/v1.0", import = ["@custom"]) // 2. import custom directive from a spec\n@ComposeDirective(name = "custom") // 3. apply @composeDirective to preserve it in the schema\nclass CustomSchema\n\nclass SimpleQuery {\n  @CustomDirective\n  fun helloWorld(): String = "Hello World"\n}\n'})}),"\n",(0,t.jsx)(i.p,{children:"it will generate following schema"}),"\n",(0,t.jsx)(i.pre,{children:(0,t.jsx)(i.code,{className:"language-graphql",children:'schema\n@composeDirective(name: "@custom")\n@link(import : ["@custom"], url: "https://myspecs.dev/myCustomDirective/v1.0")\n@link(url : "https://specs.apollo.dev/federation/v2.5")\n{\n   query: Query\n}\n\ndirective @custom on FIELD_DEFINITION\n\ntype Query {\n  helloWorld: String! @custom\n}\n'})}),"\n",(0,t.jsxs)(i.p,{children:["See ",(0,t.jsx)(i.a,{href:"https://www.apollographql.com/docs/federation/federated-types/federated-directives/#composedirective",children:"@composeDirective definition"})," for more information."]}),"\n",(0,t.jsxs)(i.h2,{id:"contact-directive",children:[(0,t.jsx)(i.code,{children:"@contact"})," directive"]}),"\n",(0,t.jsx)(i.pre,{children:(0,t.jsx)(i.code,{className:"language-graphql",children:'directive @contact(\n  "Contact title of the subgraph owner"\n  name: String!\n  "URL where the subgraph\'s owner can be reached"\n  url: String\n  "Other relevant notes can be included here; supports markdown links"\n  description: String\n) on SCHEMA\n'})}),"\n",(0,t.jsxs)(i.p,{children:["Contact schema directive can be used to provide team contact information to your subgraph schema. This information is automatically parsed and displayed by Apollo Studio.\nSee ",(0,t.jsx)(i.a,{href:"https://www.apollographql.com/docs/studio/federated-graphs/#subgraph-contact-info",children:"Subgraph Contact Information"})," for additional details."]}),"\n",(0,t.jsx)(i.h4,{id:"example-usage-on-the-schema-class",children:"Example usage on the schema class:"}),"\n",(0,t.jsx)(i.pre,{children:(0,t.jsx)(i.code,{className:"language-kotlin",children:'@ContactDirective(\n  name = "My Team Name",\n  url = "https://myteam.slack.com/archives/teams-chat-room-url",\n  description = "send urgent issues to [#oncall](https://yourteam.slack.com/archives/oncall)."\n)\nclass MySchema\n'})}),"\n",(0,t.jsx)(i.p,{children:"will generate"}),"\n",(0,t.jsx)(i.pre,{children:(0,t.jsx)(i.code,{className:"language-graphql",children:'schema @contact(description : "send urgent issues to [#oncall](https://yourteam.slack.com/archives/oncall).", name : "My Team Name", url : "https://myteam.slack.com/archives/teams-chat-room-url"){\n  query: Query\n}\n'})}),"\n",(0,t.jsxs)(i.h2,{id:"extends-directive",children:[(0,t.jsx)(i.code,{children:"@extends"})," directive"]}),"\n",(0,t.jsx)(i.admonition,{type:"caution",children:(0,t.jsxs)(i.p,{children:[(0,t.jsxs)(i.strong,{children:[(0,t.jsx)(i.code,{children:"@extends"})," directive is deprecated"]}),". Federation v2 no longer requires ",(0,t.jsx)(i.code,{children:"@extends"})," directive due to the smart entity type\nmerging. All usage of ",(0,t.jsx)(i.code,{children:"@extends"})," directive should be removed from your Federation v2 schemas."]})}),"\n",(0,t.jsx)(i.pre,{children:(0,t.jsx)(i.code,{className:"language-graphql",children:"directive @extends on OBJECT | INTERFACE\n"})}),"\n",(0,t.jsxs)(i.p,{children:[(0,t.jsx)(i.code,{children:"@extends"})," directive is used to represent type extensions in the schema. Native type extensions are currently\nunsupported by the ",(0,t.jsx)(i.code,{children:"graphql-kotlin"})," libraries. Federated extended types should have corresponding ",(0,t.jsx)(i.code,{children:"@key"})," directive\ndefined that specifies primary key required to fetch the underlying object."]}),"\n",(0,t.jsx)(i.h4,{id:"example",children:"Example"}),"\n",(0,t.jsx)(i.pre,{children:(0,t.jsx)(i.code,{className:"language-kotlin",children:'@KeyDirective(FieldSet("id"))\n@ExtendsDirective\nclass Product(@ExternalDirective val id: String) {\n   fun newFunctionality(): String = "whatever"\n}\n'})}),"\n",(0,t.jsx)(i.p,{children:"will generate"}),"\n",(0,t.jsx)(i.pre,{children:(0,t.jsx)(i.code,{className:"language-graphql",children:'type Product @key(fields : "id") @extends {\n  id: String! @external\n  newFunctionality: String!\n}\n'})}),"\n",(0,t.jsxs)(i.h2,{id:"external-directive",children:[(0,t.jsx)(i.code,{children:"@external"})," directive"]}),"\n",(0,t.jsx)(i.pre,{children:(0,t.jsx)(i.code,{className:"language-graphql",children:"# federation v1 definition\ndirective @external on FIELD_DEFINITION\n\n# federation v2 definition\ndirective @external on OBJECT | FIELD_DEFINITION\n"})}),"\n",(0,t.jsxs)(i.p,{children:["The ",(0,t.jsx)(i.code,{children:"@external"})," directive is used to mark a field as owned by another service. This allows service A to use fields from\nservice B while also knowing at runtime the types of that field. All the external fields should either be referenced from\nthe ",(0,t.jsx)(i.code,{children:"@key"}),", ",(0,t.jsx)(i.code,{children:"@requires"})," or ",(0,t.jsx)(i.code,{children:"@provides"})," directives field sets."]}),"\n",(0,t.jsxs)(i.p,{children:["Due to the smart merging of entity types, Federation v2 no longer requires ",(0,t.jsx)(i.code,{children:"@external"})," directive on ",(0,t.jsx)(i.code,{children:"@key"})," fields and can\nbe safely omitted from the schema. ",(0,t.jsx)(i.code,{children:"@external"})," directive is only required on fields referenced by the ",(0,t.jsx)(i.code,{children:"@requires"})," and\n",(0,t.jsx)(i.code,{children:"@provides"})," directive."]}),"\n",(0,t.jsx)(i.h4,{id:"example-1",children:"Example"}),"\n",(0,t.jsx)(i.pre,{children:(0,t.jsx)(i.code,{className:"language-kotlin",children:'@KeyDirective(FieldSet("id"))\nclass Product(val id: String) {\n  @ExternalDirective\n  var externalField: String by Delegates.notNull()\n\n  @RequiresDirective(FieldSet("externalField"))\n  fun newFunctionality(): String { ... }\n}\n'})}),"\n",(0,t.jsx)(i.p,{children:"will generate"}),"\n",(0,t.jsx)(i.pre,{children:(0,t.jsx)(i.code,{className:"language-graphql",children:'type Product @key(fields : "id") {\n  externalField: String! @external\n  id: String!\n  newFunctionality: String!\n}\n'})}),"\n",(0,t.jsxs)(i.h2,{id:"inaccessible-directive",children:[(0,t.jsx)(i.code,{children:"@inaccessible"})," directive"]}),"\n",(0,t.jsx)(i.admonition,{type:"note",children:(0,t.jsx)(i.p,{children:"Only available in Federation v2."})}),"\n",(0,t.jsx)(i.pre,{children:(0,t.jsx)(i.code,{className:"language-graphql",children:"directive @inaccessible on FIELD_DEFINITION\n    | OBJECT\n    | INTERFACE\n    | UNION\n    | ENUM\n    | ENUM_VALUE\n    | SCALAR\n    | INPUT_OBJECT\n    | INPUT_FIELD_DEFINITION\n    | ARGUMENT_DEFINITION\n"})}),"\n",(0,t.jsxs)(i.p,{children:["Inaccessible directive marks location within schema as inaccessible from the GraphQL Gateway. While ",(0,t.jsx)(i.code,{children:"@inaccessible"})," fields are not exposed by the gateway to the clients,\nthey are still available for query plans and can be referenced from ",(0,t.jsx)(i.code,{children:"@key"})," and ",(0,t.jsx)(i.code,{children:"@requires"})," directives. This allows you to not expose sensitive fields to your clients but\nstill make them available for computations. Inaccessible can also be used to incrementally add schema elements (e.g. fields) to multiple subgraphs without breaking composition."]}),"\n",(0,t.jsxs)(i.p,{children:["See ",(0,t.jsx)(i.a,{href:"https://specs.apollo.dev/inaccessible/v0.2",children:"@inaccessible specification"})," for additional details."]}),"\n",(0,t.jsx)(i.admonition,{type:"caution",children:(0,t.jsxs)(i.p,{children:["Location within schema will be inaccessible from the GraphQL Gateway as long as ",(0,t.jsx)(i.strong,{children:"ANY"})," of the subgraphs marks that location as ",(0,t.jsx)(i.code,{children:"@inacessible"}),"."]})}),"\n",(0,t.jsx)(i.h4,{id:"example-2",children:"Example"}),"\n",(0,t.jsx)(i.pre,{children:(0,t.jsx)(i.code,{className:"language-kotlin",children:"class Product(\n  val id: String,\n  @InaccessibleDirective\n  val secret: String\n)\n"})}),"\n",(0,t.jsx)(i.p,{children:"will be generated by the subgraph as"}),"\n",(0,t.jsx)(i.pre,{children:(0,t.jsx)(i.code,{className:"language-graphql",children:"type Product {\n  id: String!\n  secret: String! @inaccessible\n}\n"})}),"\n",(0,t.jsx)(i.p,{children:"but will be exposed on the GraphQL Gateway as"}),"\n",(0,t.jsx)(i.pre,{children:(0,t.jsx)(i.code,{className:"language-graphql",children:"type Product {\n  id: String!\n}\n"})}),"\n",(0,t.jsxs)(i.h2,{id:"interfaceobject-directive",children:[(0,t.jsx)(i.code,{children:"@interfaceObject"})," directive"]}),"\n",(0,t.jsx)(i.admonition,{type:"note",children:(0,t.jsx)(i.p,{children:"Only available in Federation v2."})}),"\n",(0,t.jsx)(i.pre,{children:(0,t.jsx)(i.code,{className:"language-graphql",children:"directive @interfaceObject on OBJECT\n"})}),"\n",(0,t.jsx)(i.p,{children:"This directive provides meta information to the router that this entity type defined within this subgraph is an interface in the supergraph. This allows you to extend functionality\nof an interface across the supergraph without having to implement (or even be aware of) all its implementing types."}),"\n",(0,t.jsx)(i.p,{children:"Example:\nGiven an interface that is defined somewhere in our supergraph"}),"\n",(0,t.jsx)(i.pre,{children:(0,t.jsx)(i.code,{className:"language-graphql",children:'interface Product @key(fields: "id") {\n  id: ID!\n  description: String\n}\n\ntype Book implements Product @key(fields: "id") {\n  id: ID!\n  description: String\n  pages: Int!\n}\n\ntype Movie implements Product @key(fields: "id") {\n  id: ID!\n  description: String\n  duration: Int!\n}\n'})}),"\n",(0,t.jsxs)(i.p,{children:["We can extend ",(0,t.jsx)(i.code,{children:"Product"})," entity in our subgraph and a new field directly to it. This will result in making this new field available to ALL implementing types."]}),"\n",(0,t.jsx)(i.pre,{children:(0,t.jsx)(i.code,{className:"language-kotlin",children:'@InterfaceObjectDirective\n@KeyDirective(fields = FieldSet("id"))\ndata class Product(val id: ID) {\n    fun reviews(): List<Review> = TODO()\n}\n'})}),"\n",(0,t.jsx)(i.p,{children:"Which generates the following subgraph schema"}),"\n",(0,t.jsx)(i.pre,{children:(0,t.jsx)(i.code,{className:"language-graphql",children:'type Product @key(fields: "id") @interfaceObject {\n  id: ID!\n  reviews: [Review!]!\n}\n'})}),"\n",(0,t.jsxs)(i.h2,{id:"key-directive",children:[(0,t.jsx)(i.code,{children:"@key"})," directive"]}),"\n",(0,t.jsx)(i.pre,{children:(0,t.jsx)(i.code,{className:"language-graphql",children:"# federation v1 definition\ndirective @key(fields: _FieldSet!) repeatable on OBJECT | INTERFACE\n\n# federation v2 definition\ndirective @key(fields: FieldSet!, resolvable: Boolean = true) repeatable on OBJECT | INTERFACE\n"})}),"\n",(0,t.jsxs)(i.p,{children:["The ",(0,t.jsx)(i.code,{children:"@key"})," directive is used to indicate a combination of fields that can be used to uniquely identify and fetch an\nobject or interface. The specified field set can represent single field (e.g. ",(0,t.jsx)(i.code,{children:'"id"'}),"), multiple fields (e.g. ",(0,t.jsx)(i.code,{children:'"id name"'}),") or\nnested selection sets (e.g. ",(0,t.jsx)(i.code,{children:'"id user { name }"'}),"). Multiple keys can be specified on a target type."]}),"\n",(0,t.jsx)(i.p,{children:"Key directives should be specified on all entities (objects that can resolve its fields across multiple subgraphs). Key\nfields specified in the directive field set should correspond to a valid field on the underlying GraphQL interface/object."}),"\n",(0,t.jsx)(i.h4,{id:"basic-example",children:"Basic Example"}),"\n",(0,t.jsx)(i.pre,{children:(0,t.jsx)(i.code,{className:"language-kotlin",children:'@KeyDirective(FieldSet("id"))\n@KeyDirective(FieldSet("upc"))\nclass Product(val id: String, val upc: String, val name: String)\n'})}),"\n",(0,t.jsx)(i.p,{children:"will generate"}),"\n",(0,t.jsx)(i.pre,{children:(0,t.jsx)(i.code,{className:"language-graphql",children:'type Product @key(fields: "id") @key(fields: "upc") {\n  id: String!\n  name: String!\n  upc: String!\n}\n'})}),"\n",(0,t.jsx)(i.h4,{id:"referencing-external-entities",children:"Referencing External Entities"}),"\n",(0,t.jsxs)(i.p,{children:["Entity types can be referenced from other subgraphs without contributing any additional fields, i.e. we can update type within our schema with a reference to a federated type. In order to generate\na valid schema, we need to define ",(0,t.jsx)(i.strong,{children:"stub"})," for federated entity that contains only key fields and also mark it as not resolvable within our subgraph. For example, if we have ",(0,t.jsx)(i.code,{children:"Review"})," entity defined\nin our supergraph, we can reference it in our product schema using following code"]}),"\n",(0,t.jsx)(i.pre,{children:(0,t.jsx)(i.code,{className:"language-kotlin",children:'@KeyDirective(fields = FieldSet("id"))\nclass Product(val id: String, val name: String, val reviews: List<Review>)\n\n// review stub referencing just the key fields\n@KeyDirective(fields = FieldSet("id"), resolvable = false)\nclass Review(val id: String)\n'})}),"\n",(0,t.jsx)(i.p,{children:"which will generate"}),"\n",(0,t.jsx)(i.pre,{children:(0,t.jsx)(i.code,{className:"language-graphql",children:'type Product @key(fields: "id") {\n  id: String!\n  name: String!\n  reviews: [Review!]!\n}\n\ntype Review @key(fields: "id", resolvable: false) {\n  id: String!\n}\n'})}),"\n",(0,t.jsx)(i.p,{children:"This allows end users to query GraphQL Gateway for any product review fields and they will be resolved by calling the appropriate subgraph."}),"\n",(0,t.jsxs)(i.h2,{id:"link-directive",children:[(0,t.jsx)(i.code,{children:"@link"})," directive"]}),"\n",(0,t.jsx)(i.admonition,{type:"note",children:(0,t.jsx)(i.p,{children:"Only available in Federation v2."})}),"\n",(0,t.jsx)(i.admonition,{type:"caution",children:(0,t.jsxs)(i.p,{children:["While both custom namespace (",(0,t.jsx)(i.code,{children:"as"}),") and ",(0,t.jsx)(i.code,{children:"import"})," arguments are optional in the schema definition, due to ",(0,t.jsx)(i.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/issues/1830",children:"#1830"}),"\nwe currently always require those values to be explicitly provided."]})}),"\n",(0,t.jsx)(i.pre,{children:(0,t.jsx)(i.code,{className:"language-graphql",children:"directive @link(url: String!, as: String, import: [Import]) repeatable on SCHEMA\nscalar Import\n"})}),"\n",(0,t.jsxs)(i.p,{children:["The ",(0,t.jsx)(i.code,{children:"@link"})," directive links definitions within the document to external schemas. See ",(0,t.jsx)(i.a,{href:"https://specs.apollo.dev/link/v1.0",children:"@link specification"})," for details."]}),"\n",(0,t.jsxs)(i.p,{children:["External schemas are identified by their ",(0,t.jsx)(i.code,{children:"url"}),", which ends with a name and version with the following format: ",(0,t.jsx)(i.code,{children:"{NAME}/v{MAJOR}.{MINOR}"}),",\ne.g. ",(0,t.jsx)(i.code,{children:'url = "https://specs.apollo.dev/federation/v2.5"'}),"."]}),"\n",(0,t.jsxs)(i.p,{children:["External types are associated with the target specification by annotating it with ",(0,t.jsx)(i.code,{children:"@LinkedSpec"})," meta annotation. External\ntypes defined in the specification will be automatically namespaced (prefixed with ",(0,t.jsx)(i.code,{children:"{NAME}__"}),") unless they are explicitly\nimported. Namespace should default to the specification name from the imported spec url. Custom namespace can be provided\nby specifying ",(0,t.jsx)(i.code,{children:"as"})," argument value."]}),"\n",(0,t.jsx)(i.p,{children:"External types can be imported using the same name or can be aliased to some custom name."}),"\n",(0,t.jsx)(i.pre,{children:(0,t.jsx)(i.code,{className:"language-kotlin",children:'@LinkDirective(`as` = "custom", imports = [LinkImport(name = "@foo"), LinkImport(name = "@bar", `as` = "@myBar")], url = "https://myspecs.dev/custom/v1.0")\nclass MySchema\n'})}),"\n",(0,t.jsx)(i.p,{children:"This will generate following schema:"}),"\n",(0,t.jsx)(i.pre,{children:(0,t.jsx)(i.code,{className:"language-graphql",children:'schema @link(as: "custom", import : ["@foo", { name: "@bar", as: "@myBar" }], url : "https://myspecs.dev/custom/v1.0") {\n    query: Query\n}\n'})}),"\n",(0,t.jsxs)(i.h3,{id:"linkedspec-annotation",children:[(0,t.jsx)(i.code,{children:"@LinkedSpec"})," annotation"]}),"\n",(0,t.jsxs)(i.p,{children:["When importing custom specifications, we need to be able to identify whether given element is part of the referenced specification.\n",(0,t.jsx)(i.code,{children:"@LinkedSpec"})," is a meta annotation that is used to indicate that given directive/type is associated with imported ",(0,t.jsx)(i.code,{children:"@link"})," specification."]}),"\n",(0,t.jsxs)(i.p,{children:["In order to ensure consistent behavior, ",(0,t.jsx)(i.code,{children:"@LinkedSpec"})," value have to match default specification name as it appears in the\nimported url and not the aliased value."]}),"\n",(0,t.jsx)(i.p,{children:"Example usage:"}),"\n",(0,t.jsx)(i.pre,{children:(0,t.jsx)(i.code,{children:'@LinkedSpec("custom")\n@GraphQLDirective(\n    name = "foo",\n    locations = [DirectiveLocation.FIELD_DEFINITION]\n)\nannotation class Foo\n'})}),"\n",(0,t.jsxs)(i.p,{children:["In the example above, we specify that ",(0,t.jsx)(i.code,{children:"@foo"})," directive is part of the ",(0,t.jsx)(i.code,{children:"custom"})," specification. We can then reference ",(0,t.jsx)(i.code,{children:"@foo"}),"\nin the ",(0,t.jsx)(i.code,{children:"@link"})," specification imports"]}),"\n",(0,t.jsx)(i.pre,{children:(0,t.jsx)(i.code,{className:"language-graphql",children:'schema @link(as: "custom", import : ["@foo"], url : "https://myspecs.dev/custom/v1.0") {\n    query: Query\n}\n\ndirective @foo on FIELD_DEFINITION\n'})}),"\n",(0,t.jsx)(i.p,{children:"If we don't import the directive, then it will automatically namespaced to the spec"}),"\n",(0,t.jsx)(i.pre,{children:(0,t.jsx)(i.code,{className:"language-graphql",children:'schema @link(as: "custom", url : "https://myspecs.dev/custom/v1.0") {\n    query: Query\n}\n\ndirective @custom__foo on FIELD_DEFINITION\n'})}),"\n",(0,t.jsxs)(i.h2,{id:"override-directive",children:[(0,t.jsx)(i.code,{children:"@override"})," directive"]}),"\n",(0,t.jsx)(i.admonition,{type:"note",children:(0,t.jsx)(i.p,{children:"Only available in Federation v2."})}),"\n",(0,t.jsx)(i.pre,{children:(0,t.jsx)(i.code,{className:"language-graphql",children:"directive @override(from: String!) on FIELD_DEFINITION\n"})}),"\n",(0,t.jsxs)(i.p,{children:["The ",(0,t.jsx)(i.code,{children:"@override"})," directive is used to indicate that the current subgraph is taking responsibility for resolving the marked field away from the subgraph specified in the ",(0,t.jsx)(i.code,{children:"from"})," argument,\ni.e. it is used for migrating a field from one subgraph to another. Name of the subgraph to be overriden has to match the name of the subgraph that was used to publish their schema. See\n",(0,t.jsx)(i.a,{href:"https://www.apollographql.com/docs/rover/subgraphs/#publishing-a-subgraph-schema-to-apollo-studio",children:"Publishing schema to Apollo Studio"})," for additional details."]}),"\n",(0,t.jsx)(i.admonition,{type:"caution",children:(0,t.jsxs)(i.p,{children:["Only one subgraph can ",(0,t.jsx)(i.code,{children:"@override"})," any given field. If multiple subgraphs attempt to ",(0,t.jsx)(i.code,{children:"@override"})," the same field, a composition error occurs."]})}),"\n",(0,t.jsx)(i.h4,{id:"example-3",children:"Example"}),"\n",(0,t.jsxs)(i.p,{children:["Given ",(0,t.jsx)(i.code,{children:"SubgraphA"})]}),"\n",(0,t.jsx)(i.pre,{children:(0,t.jsx)(i.code,{className:"language-graphql",children:'type Product @key(fields: "id") {\n    id: String!\n    description: String!\n}\n'})}),"\n",(0,t.jsxs)(i.p,{children:["We can override gateway ",(0,t.jsx)(i.code,{children:"description"})," field resolution to resolve it in the ",(0,t.jsx)(i.code,{children:"SubgraphB"})]}),"\n",(0,t.jsx)(i.pre,{children:(0,t.jsx)(i.code,{className:"language-graphql",children:'type Product @key(fields: "id") {\n    id: String!\n    name: String!\n    description: String! @override(from: "SubgraphA")\n}\n'})}),"\n",(0,t.jsxs)(i.h2,{id:"policy-directive",children:[(0,t.jsx)(i.code,{children:"@policy"})," directive"]}),"\n",(0,t.jsx)(i.pre,{children:(0,t.jsx)(i.code,{className:"language-graphql",children:"directive @policy(policies: [[Policy!]!]!) on\n    ENUM\n  | FIELD_DEFINITION\n  | INTERFACE\n  | OBJECT\n  | SCALAR\n"})}),"\n",(0,t.jsxs)(i.p,{children:["Directive that is used to indicate that access to the target element is restricted based on authorization policies that are evaluated in a Rhai script or coprocessor. Refer to the\n",(0,t.jsx)(i.a,{href:"https://www.apollographql.com/docs/router/configuration/authorization#policy",children:"Apollo Router documentation"})," for additional details."]}),"\n",(0,t.jsxs)(i.h2,{id:"provides-directive",children:[(0,t.jsx)(i.code,{children:"@provides"})," directive"]}),"\n",(0,t.jsx)(i.pre,{children:(0,t.jsx)(i.code,{className:"language-graphql",children:"# federation v1 definition\ndirective @provides(fields: _FieldSet!) on FIELD_DEFINITION\n\n# federation v2 definition\ndirective @provides(fields: FieldSet!) on FIELD_DEFINITION\n"})}),"\n",(0,t.jsxs)(i.p,{children:["The ",(0,t.jsx)(i.code,{children:"@provides"})," directive is a router optimization hint specifying field set that can be resolved locally at the given subgraph through this particular query path. This allows you to\nexpose only a subset of fields from the underlying entity type to be selectable from the federated schema without the need to call other subgraphs. Provided fields specified in the\ndirective field set should correspond to a valid field on the underlying GraphQL interface/object type. ",(0,t.jsx)(i.code,{children:"@provides"})," directive can only be used on fields returning entities."]}),"\n",(0,t.jsx)(i.admonition,{type:"info",children:(0,t.jsxs)(i.p,{children:["Federation v2 does not require ",(0,t.jsx)(i.code,{children:"@provides"})," directive if field can ",(0,t.jsx)(i.strong,{children:"always"})," be resolved locally. ",(0,t.jsx)(i.code,{children:"@provides"})," should be omitted in this situation."]})}),"\n",(0,t.jsx)(i.h4,{id:"example-1-1",children:"Example 1:"}),"\n",(0,t.jsx)(i.p,{children:"We might want to expose only name of the user that submitted a review."}),"\n",(0,t.jsx)(i.pre,{children:(0,t.jsx)(i.code,{className:"language-kotlin",children:'@KeyDirective(FieldSet("id"))\nclass Review(val id: String) {\n  @ProvidesDirective(FieldSet("name"))\n  fun user(): User = getUserByReviewId(id)\n}\n\n@KeyDirective(FieldSet("userId"))\nclass User(\n  val userId: String,\n  @ExternalDirective val name: String\n)\n'})}),"\n",(0,t.jsx)(i.p,{children:"will generate"}),"\n",(0,t.jsx)(i.pre,{children:(0,t.jsx)(i.code,{className:"language-graphql",children:'type Review @key(fields : "id") {\n  id: String!\n  user: User! @provides(fields : "name")\n}\n\ntype User @key(fields : "userId") {\n  userId: String!\n  name: String! @external\n}\n'})}),"\n",(0,t.jsx)(i.h4,{id:"example-2-1",children:"Example 2:"}),"\n",(0,t.jsx)(i.p,{children:"Within our service, one of the queries could resolve all fields locally while other requires resolution from other subgraph"}),"\n",(0,t.jsx)(i.pre,{children:(0,t.jsx)(i.code,{className:"language-graphql",children:'type Query {\n  remoteResolution: Foo\n  localOnly: Foo @provides("baz")\n}\n\ntype Foo @key("id") {\n  id: ID!\n  bar: Bar\n  baz: Baz @external\n}\n'})}),"\n",(0,t.jsxs)(i.p,{children:["In the example above, if user selects ",(0,t.jsx)(i.code,{children:"baz"})," field, it will be resolved locally from ",(0,t.jsx)(i.code,{children:"localOnly"})," query but will require another subgraph invocation from ",(0,t.jsx)(i.code,{children:"remoteResolution"})," query."]}),"\n",(0,t.jsxs)(i.h2,{id:"requires-directive",children:[(0,t.jsx)(i.code,{children:"@requires"})," directive"]}),"\n",(0,t.jsx)(i.pre,{children:(0,t.jsx)(i.code,{className:"language-graphql",children:"# federation v1 definition\ndirective @requires(fields: _FieldSet!) on FIELD_DEFINITON\n\n# federation v2 definition\ndirective @requires(fields: FieldSet!) on FIELD_DEFINITON\n"})}),"\n",(0,t.jsxs)(i.p,{children:["The ",(0,t.jsx)(i.code,{children:"@requires"})," directive is used to specify external (provided by other subgraphs) entity fields that are needed to resolve target field. It is used to develop a query plan where\nthe required fields may not be needed by the client, but the service may need additional information from other subgraphs. Required fields specified in the directive field set should\ncorrespond to a valid field on the underlying GraphQL interface/object and should be instrumented with ",(0,t.jsx)(i.code,{children:"@external"})," directive."]}),"\n",(0,t.jsxs)(i.p,{children:["All the leaf fields from the specified in the ",(0,t.jsx)(i.code,{children:"@requires"})," selection set have to be marked as ",(0,t.jsx)(i.code,{children:"@external"})," OR any of the parent fields on the path to the leaf is marked as ",(0,t.jsx)(i.code,{children:"@external"}),"."]}),"\n",(0,t.jsxs)(i.p,{children:["Fields specified in the ",(0,t.jsx)(i.code,{children:"@requires"})," directive will only be specified in the queries that reference those fields. This is problematic for Kotlin as the non-nullable primitive properties\nhave to be initialized when they are declared. Simplest workaround for this problem is to initialize the underlying property to some default value (e.g. null) that will be used if\nit is not specified. This approach might become problematic though as it might be impossible to determine whether fields was initialized with the default value or the invalid/default\nvalue was provided by the federated query. Another potential workaround is to rely on delegation to initialize the property after the object gets created. This will ensure that exception\nwill be thrown if queries attempt to resolve fields that reference the uninitialized property."]}),"\n",(0,t.jsx)(i.h4,{id:"example-4",children:"Example"}),"\n",(0,t.jsx)(i.pre,{children:(0,t.jsx)(i.code,{className:"language-kotlin",children:'@KeyDirective(FieldSet("id"))\nclass Product(val id: String) {\n  @ExternalDirective\n  var weight: Double by Delegates.notNull()\n\n  @RequiresDirective(FieldSet("weight"))\n  fun shippingCost(): String { ... }\n}\n'})}),"\n",(0,t.jsx)(i.p,{children:"will generate"}),"\n",(0,t.jsx)(i.pre,{children:(0,t.jsx)(i.code,{className:"language-graphql",children:'type Product @key(fields : "id") {\n  id: String!\n  shippingCost: String! @requires(fields : "weight")\n  weight: Float! @external\n}\n'})}),"\n",(0,t.jsxs)(i.h2,{id:"requiresscopes-directive",children:[(0,t.jsx)(i.code,{children:"@requiresScopes"})," directive"]}),"\n",(0,t.jsx)(i.pre,{children:(0,t.jsx)(i.code,{className:"language-graphql",children:"directive @requiresScopes(scopes: [[Scope!]!]!) on\n    ENUM\n  | FIELD_DEFINITION\n  | INTERFACE\n  | OBJECT\n  | SCALAR\n"})}),"\n",(0,t.jsxs)(i.p,{children:["Directive that is used to indicate that the target element is accessible only to the authenticated supergraph users with the appropriate JWT scopes. Refer to the\n",(0,t.jsx)(i.a,{href:"https://www.apollographql.com/docs/router/configuration/authorization#requiresscopes",children:"Apollo Router documentation"})," for additional details."]}),"\n",(0,t.jsxs)(i.h2,{id:"shareable-directive",children:[(0,t.jsx)(i.code,{children:"@shareable"})," directive"]}),"\n",(0,t.jsx)(i.admonition,{type:"note",children:(0,t.jsx)(i.p,{children:"Only available in Federation v2."})}),"\n",(0,t.jsx)(i.pre,{children:(0,t.jsx)(i.code,{className:"language-graphql",children:"directive @shareable repeatable on FIELD_DEFINITION | OBJECT\n"})}),"\n",(0,t.jsxs)(i.p,{children:["Shareable directive indicates that given object and/or field can be resolved by multiple subgraphs. If an object is marked as ",(0,t.jsx)(i.code,{children:"@shareable"})," then all its fields are automatically shareable without the\nneed for explicitly marking them with ",(0,t.jsx)(i.code,{children:"@shareable"})," directive. All fields referenced from ",(0,t.jsx)(i.code,{children:"@key"})," directive are automatically shareable as well."]}),"\n",(0,t.jsx)(i.admonition,{type:"caution",children:(0,t.jsxs)(i.p,{children:["Objects/fields have to specify same shareability (i.e. ",(0,t.jsx)(i.code,{children:"@shareable"})," or not) mode across ALL subgraphs."]})}),"\n",(0,t.jsx)(i.h4,{id:"example-5",children:"Example"}),"\n",(0,t.jsx)(i.pre,{children:(0,t.jsx)(i.code,{className:"language-graphql",children:'type Product @key(fields: "id") {\n  id: ID!                           # shareable because id is a key field\n  name: String                      # non-shareable\n  description: String @shareable    # shareable\n}\n\ntype User @key(fields: "email") @shareable {\n  email: String                    # shareable because User is marked shareable\n  name: String                     # shareable because User is marked shareable\n}\n'})}),"\n",(0,t.jsxs)(i.h2,{id:"tag-directive",children:[(0,t.jsx)(i.code,{children:"@tag"})," directive"]}),"\n",(0,t.jsx)(i.pre,{children:(0,t.jsx)(i.code,{className:"language-graphql",children:"directive @tag(name: String!) repeatable on FIELD_DEFINITION\n    | OBJECT\n    | INTERFACE\n    | UNION\n    | ARGUMENT_DEFINITION\n    | SCALAR\n    | ENUM\n    | ENUM_VALUE\n    | INPUT_OBJECT\n    | INPUT_FIELD_DEFINITION\n"})}),"\n",(0,t.jsxs)(i.p,{children:["Tag directive allows users to annotate fields and types with additional metadata information. Used by ",(0,t.jsx)(i.a,{href:"https://www.apollographql.com/docs/studio/contracts/",children:"Apollo Contracts"})," to expose\ndifferent graph variants to different customers. See ",(0,t.jsx)(i.a,{href:"https://specs.apollo.dev/tag/v0.2/",children:"@tag specification"})," for details."]}),"\n",(0,t.jsx)(i.h4,{id:"example-6",children:"Example"}),"\n",(0,t.jsx)(i.pre,{children:(0,t.jsx)(i.code,{className:"language-graphql",children:'type Product @tag(name: "MyCustomTag") {\n    id: String!\n    name: String!\n}\n'})}),"\n",(0,t.jsx)(i.admonition,{type:"caution",children:(0,t.jsxs)(i.p,{children:["Apollo Contracts behave slightly differently depending on which version of Apollo Federation your graph uses (1 or 2). See ",(0,t.jsx)(i.a,{href:"https://www.apollographql.com/docs/studio/contracts/#federation-1-limitations",children:"documentation"}),"\nfor details."]})})]})}function h(e={}){const{wrapper:i}={...(0,r.R)(),...e.components};return i?(0,t.jsx)(i,{...e,children:(0,t.jsx)(o,{...e})}):o(e)}},28453:(e,i,n)=>{n.d(i,{R:()=>a,x:()=>d});var t=n(96540);const r={},s=t.createContext(r);function a(e){const i=t.useContext(s);return t.useMemo((function(){return"function"==typeof e?e(i):{...i,...e}}),[i,e])}function d(e){let i;return i=e.disableParentContext?"function"==typeof e.components?e.components(r):e.components||r:a(e.components),t.createElement(s.Provider,{value:i},e.children)}}}]);