"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[3239],{18318:(e,t,i)=>{i.r(t),i.d(t,{assets:()=>p,contentTitle:()=>s,default:()=>v,frontMatter:()=>l,metadata:()=>o,toc:()=>c});var n=i(87462),r=i(63366),a=(i(67294),i(3905)),d=(i(95657),["components"]),l={id:"federated-directives",title:"Federated Directives"},s=void 0,o={unversionedId:"schema-generator/federation/federated-directives",id:"version-4.x.x/schema-generator/federation/federated-directives",title:"Federated Directives",description:"graphql-kotlin supports a number of directives that can be used to annotate a schema and direct certain behaviors.",source:"@site/versioned_docs/version-4.x.x/schema-generator/federation/federated-directives.md",sourceDirName:"schema-generator/federation",slug:"/schema-generator/federation/federated-directives",permalink:"/graphql-kotlin/docs/4.x.x/schema-generator/federation/federated-directives",draft:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-4.x.x/schema-generator/federation/federated-directives.md",tags:[],version:"4.x.x",lastUpdatedBy:"Samuel Vazquez",lastUpdatedAt:1685659104,formattedLastUpdatedAt:"Jun 1, 2023",frontMatter:{id:"federated-directives",title:"Federated Directives"},sidebar:"version-4.x.x/docs",previous:{title:"Federated Schemas",permalink:"/graphql-kotlin/docs/4.x.x/schema-generator/federation/federated-schemas"},next:{title:"Federated Type Resolution",permalink:"/graphql-kotlin/docs/4.x.x/schema-generator/federation/type-resolution"}},p={},c=[{value:"<code>@extends</code> directive",id:"extends-directive",level:2},{value:"<code>@external</code> directive",id:"external-directive",level:2},{value:"<code>@key</code> directive",id:"key-directive",level:2},{value:"<code>@provides</code> directive",id:"provides-directive",level:2},{value:"<code>@requires</code> directive",id:"requires-directive",level:2}],u={toc:c},h="wrapper";function v(e){var t=e.components,i=(0,r.Z)(e,d);return(0,a.kt)(h,(0,n.Z)({},u,i,{components:t,mdxType:"MDXLayout"}),(0,a.kt)("p",null,(0,a.kt)("inlineCode",{parentName:"p"},"graphql-kotlin")," supports a number of directives that can be used to annotate a schema and direct certain behaviors."),(0,a.kt)("p",null,"For more details, see the ",(0,a.kt)("a",{parentName:"p",href:"https://www.apollographql.com/docs/apollo-server/federation/federation-spec/"},"Apollo Federation Specification"),"."),(0,a.kt)("h2",{id:"extends-directive"},(0,a.kt)("inlineCode",{parentName:"h2"},"@extends")," directive"),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-graphql"},"directive @extends on OBJECT | INTERFACE\n")),(0,a.kt)("p",null,(0,a.kt)("inlineCode",{parentName:"p"},"@extends")," directive is used to represent type extensions in the schema. Native type extensions are currently\nunsupported by the ",(0,a.kt)("inlineCode",{parentName:"p"},"graphql-kotlin")," libraries. Federated extended types should have corresponding ",(0,a.kt)("inlineCode",{parentName:"p"},"@key")," directive\ndefined that specifies primary key required to fetch the underlying object."),(0,a.kt)("p",null,"Example"),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-kotlin"},'@KeyDirective(FieldSet("id"))\n@ExtendsDirective\nclass Product(@ExternalDirective val id: String) {\n   fun newFunctionality(): String = "whatever"\n}\n')),(0,a.kt)("p",null,"will generate"),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-graphql"},'type Product @key(fields : "id") @extends {\n  id: String! @external\n  newFunctionality: String!\n}\n')),(0,a.kt)("h2",{id:"external-directive"},(0,a.kt)("inlineCode",{parentName:"h2"},"@external")," directive"),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-graphql"},"directive @external on FIELD_DEFINITION\n")),(0,a.kt)("p",null,"The ",(0,a.kt)("inlineCode",{parentName:"p"},"@external")," directive is used to mark a field as owned by another service. This allows service A to use fields from\nservice B while also knowing at runtime the types of that field. ",(0,a.kt)("inlineCode",{parentName:"p"},"@external")," directive is only applicable on federated\nextended types. All the external fields should either be referenced from the ",(0,a.kt)("inlineCode",{parentName:"p"},"@key"),", ",(0,a.kt)("inlineCode",{parentName:"p"},"@requires")," or ",(0,a.kt)("inlineCode",{parentName:"p"},"@provides"),"\ndirectives field sets."),(0,a.kt)("p",null,"Example"),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-kotlin"},'@KeyDirective(FieldSet("id"))\n@ExtendsDirective\nclass Product(@ExternalDirective val id: String) {\n  fun newFunctionality(): String = "whatever"\n}\n')),(0,a.kt)("p",null,"will generate"),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-graphql"},'type Product @key(fields : "id") @extends {\n  id: String! @external\n  newFunctionality: String!\n}\n')),(0,a.kt)("h2",{id:"key-directive"},(0,a.kt)("inlineCode",{parentName:"h2"},"@key")," directive"),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-graphql"},"directive @key(fields: _FieldSet!) on OBJECT | INTERFACE\n")),(0,a.kt)("p",null,"The ",(0,a.kt)("inlineCode",{parentName:"p"},"@key")," directive is used to indicate a combination of fields that can be used to uniquely identify and fetch an\nobject or interface. The specified field set can represent single field (e.g. ",(0,a.kt)("inlineCode",{parentName:"p"},'"id"'),"), multiple fields (e.g. ",(0,a.kt)("inlineCode",{parentName:"p"},'"id name"'),") or\nnested selection sets (e.g. ",(0,a.kt)("inlineCode",{parentName:"p"},'"id user { name }"'),")."),(0,a.kt)("p",null,"Key directives should be specified on the root base type as well as all the corresponding federated (i.e. extended)\ntypes. Key fields specified in the directive field set should correspond to a valid field on the underlying GraphQL\ninterface/object. Federated extended types should also instrument all the referenced key fields with ",(0,a.kt)("inlineCode",{parentName:"p"},"@external"),"\ndirective."),(0,a.kt)("p",null,">"," NOTE: The Federation spec specifies that multiple @key directives can be applied on the field. The GraphQL spec has been recently changed to allow this behavior,\n",">"," but we are currently blocked and are tracking progress in ",(0,a.kt)("a",{parentName:"p",href:"https://github.com/ExpediaGroup/graphql-kotlin/issues/590"},"this issue"),"."),(0,a.kt)("p",null,"Example"),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-kotlin"},'@KeyDirective(FieldSet("id"))\nclass Product(val id: String, val name: String)\n')),(0,a.kt)("p",null,"will generate"),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-graphql"},'type Product @key(fields: "id") {\n  id: String!\n  name: String!\n}\n')),(0,a.kt)("h2",{id:"provides-directive"},(0,a.kt)("inlineCode",{parentName:"h2"},"@provides")," directive"),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-graphql"},"directive @provides(fields: _FieldSet!) on FIELD_DEFINITION\n")),(0,a.kt)("p",null,"The ",(0,a.kt)("inlineCode",{parentName:"p"},"@provides")," directive is used to annotate the expected returned field set from a field on a base type that is\nguaranteed to be selectable by the gateway. This allows you to expose only a subset of fields from the underlying\nfederated object type to be selectable from the federated schema. Provided fields specified in the directive field set\nshould correspond to a valid field on the underlying GraphQL interface/object type. ",(0,a.kt)("inlineCode",{parentName:"p"},"@provides")," directive can only be\nused on fields returning federated extended objects."),(0,a.kt)("p",null,"Example:\nWe might want to expose only name of the user that submitted a review."),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-kotlin"},'@KeyDirective(FieldSet("id"))\nclass Review(val id: String) {\n  @ProvidesDirective(FieldSet("name"))\n  fun user(): User = getUserByReviewId(id)\n}\n\n@KeyDirective(FieldSet("userId"))\n@ExtendsDirective\nclass User(\n  @ExternalDirective val userId: String,\n  @ExternalDirective val name: String\n)\n')),(0,a.kt)("p",null,"will generate"),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-graphql"},'type Review @key(fields : "id") {\n  id: String!\n  user: User! @provides(fields : "name")\n}\n\ntype User @key(fields : "userId") @extends {\n  userId: String! @external\n  name: String! @external\n}\n')),(0,a.kt)("h2",{id:"requires-directive"},(0,a.kt)("inlineCode",{parentName:"h2"},"@requires")," directive"),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-graphql"},"directive @requires(fields: _FieldSet!) on FIELD_DEFINITON\n")),(0,a.kt)("p",null,"The ",(0,a.kt)("inlineCode",{parentName:"p"},"@requires")," directive is used to annotate the required input field set from a base type for a resolver. It is used\nto develop a query plan where the required fields may not be needed by the client, but the service may need additional\ninformation from other services. Required fields specified in the directive field set should correspond to a valid field\non the underlying GraphQL interface/object and should be instrumented with ",(0,a.kt)("inlineCode",{parentName:"p"},"@external")," directive. Since ",(0,a.kt)("inlineCode",{parentName:"p"},"@requires"),"\ndirective specifies additional fields (besides the one specified in ",(0,a.kt)("inlineCode",{parentName:"p"},"@key")," directive) that are required to resolve\nfederated type fields, this directive can only be specified on federated extended objects fields."),(0,a.kt)("p",null,"NOTE: fields specified in the ",(0,a.kt)("inlineCode",{parentName:"p"},"@requires")," directive will only be specified in the queries that reference those fields.\nThis is problematic for Kotlin as the non nullable primitive properties have to be initialized when they are declared.\nSimplest workaround for this problem is to initialize the underlying property to some dummy value that will be used if\nit is not specified. This approach might become problematic though as it might be impossible to determine whether fields\nwas initialized with the default value or the invalid/default value was provided by the federated query. Another\npotential workaround is to rely on delegation to initialize the property after the object gets created. This will ensure\nthat exception will be thrown if queries attempt to resolve fields that reference the uninitialized property."),(0,a.kt)("p",null,"Example:"),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-kotlin"},'@KeyDirective(FieldSet("id"))\n@ExtendsDirective\nclass Product(@ExternalDirective val id: String) {\n  @ExternalDirective\n  var weight: Double by Delegates.notNull()\n\n  @RequiresDirective(FieldSet("weight"))\n  fun shippingCost(): String { ... }\n\n  fun additionalInfo(): String { ... }\n}\n')),(0,a.kt)("p",null,"will generate"),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-graphql"},'type Product @key(fields : "id") @extends  {\n  additionalInfo: String!\n  id: String! @external\n  shippingCost: String! @requires(fields : "weight")\n  weight: Float! @external\n}\n')))}v.isMDXComponent=!0}}]);