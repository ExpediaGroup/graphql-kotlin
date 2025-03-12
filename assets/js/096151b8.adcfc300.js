"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[9861],{87908:(e,n,t)=>{t.r(n),t.d(n,{assets:()=>c,contentTitle:()=>s,default:()=>d,frontMatter:()=>a,metadata:()=>r,toc:()=>l});var o=t(74848),i=t(28453);const a={id:"custom-type-reference",title:"Custom Types"},s=void 0,r={id:"schema-generator/customizing-schemas/custom-type-reference",title:"Custom Types",description:"Code-first has many advantages and removes duplication. However, one downside is that the types defined have to match",source:"@site/versioned_docs/version-5.x.x/schema-generator/customizing-schemas/custom-type-reference.md",sourceDirName:"schema-generator/customizing-schemas",slug:"/schema-generator/customizing-schemas/custom-type-reference",permalink:"/graphql-kotlin/docs/5.x.x/schema-generator/customizing-schemas/custom-type-reference",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-5.x.x/schema-generator/customizing-schemas/custom-type-reference.md",tags:[],version:"5.x.x",lastUpdatedBy:"Daniel",lastUpdatedAt:1741819577e3,frontMatter:{id:"custom-type-reference",title:"Custom Types"},sidebar:"docs",previous:{title:"Deprecating Schema",permalink:"/graphql-kotlin/docs/5.x.x/schema-generator/customizing-schemas/deprecating-schema"},next:{title:"Restricting Input and Output Types",permalink:"/graphql-kotlin/docs/5.x.x/schema-generator/customizing-schemas/restricting-input-output"}},c={},l=[{value:"<code>@GraphQLType</code>",id:"graphqltype",level:2},{value:"Custom Type Configuration",id:"custom-type-configuration",level:3},{value:"Adding Missing Kotlin Types",id:"adding-missing-kotlin-types",level:2},{value:"Final Result",id:"final-result",level:2}];function h(e){const n={a:"a",code:"code",h2:"h2",h3:"h3",p:"p",pre:"pre",...(0,i.R)(),...e.components};return(0,o.jsxs)(o.Fragment,{children:[(0,o.jsx)(n.p,{children:"Code-first has many advantages and removes duplication. However, one downside is that the types defined have to match\ncompiled Kotlin code. In some cases, it is possible to define a schema that is valid in SDL but it would be impossible to\nreturn a Kotlin type that matches exactly that type. In these cases, you can pass in custom types in the schema\ngenerator config and annotate the schema with the type info."}),"\n",(0,o.jsx)(n.p,{children:"A common example is when you need to return a type or union defined in library JAR, but you can not change the code.\nFor example, let's say there is a type in a library. You can not change the fields, add annotations, or have it implement interfaces."}),"\n",(0,o.jsx)(n.pre,{children:(0,o.jsx)(n.code,{className:"language-kotlin",children:"// Defined in external library\nclass Foo(val number: Int)\n"})}),"\n",(0,o.jsx)(n.p,{children:"If you want to have this type be used in a new interface or union defined in your API, it is not possible to do in Kotlin code\nsince you can not modify the compiled code."}),"\n",(0,o.jsx)(n.pre,{children:(0,o.jsx)(n.code,{className:"language-kotlin",children:"// New interface\ninterface TypeWithNumber { val number: Int }\n// New union\ninterface TypeWithAnyField\n\n// Error: We are not able to return Foo for any of these functions\nfun customInterface(): TypeWithNumber = Foo(1)\nfun customUnion(): TypeWithAnyField = Foo(1)\n"})}),"\n",(0,o.jsx)(n.h2,{id:"graphqltype",children:(0,o.jsx)(n.code,{children:"@GraphQLType"})}),"\n",(0,o.jsx)(n.p,{children:"You can use this annotation to change the return type of a field. The annotation accepts the type name, which will be\nadded as a type reference in the schema. This means that you will have to define the type and its schema with the same name in the configuration."}),"\n",(0,o.jsx)(n.p,{children:"Doing this could still be serialization issues, so you should make sure that the data you return from the field matches the defined schema of the type."}),"\n",(0,o.jsx)(n.pre,{children:(0,o.jsx)(n.code,{className:"language-kotlin",children:'// Defined in external library or can not be modified\nclass Foo(val number: Int)\nclass Bar(val value: String)\n\n// Might return Foo or Bar\n@GraphQLType("FooOrBar")\nfun customUnion(): Any = if (Random.nextBoolean()) Foo(1) else Bar("hello")\n\n// Will throw runtime error when serialized data does not match the schema\n@GraphQLType("FooOrBar")\nfun invalidType(): String = "hello"\n'})}),"\n",(0,o.jsx)(n.h3,{id:"custom-type-configuration",children:"Custom Type Configuration"}),"\n",(0,o.jsxs)(n.p,{children:["In our above example there is no Kotlin code for the type ",(0,o.jsx)(n.code,{children:"FooOrBar"}),". It only exists by reference right now.\nTo add the type into the schema, specify the additional types in the ",(0,o.jsx)(n.a,{href:"./generator-config",children:"SchemaGeneratorConfiguration"}),".\nThis is using the ",(0,o.jsx)(n.a,{href:"https://www.graphql-java.com/documentation/schema#union",children:"grapqhl-java schema object builders"}),"."]}),"\n",(0,o.jsx)(n.pre,{children:(0,o.jsx)(n.code,{className:"language-kotlin",children:'val fooCustom = GraphQLUnionType.newUnionType()\n    .name("FooOrBar")\n    .possibleType(GraphQLTypeReference("Foo"))\n    .possibleType(GraphQLTypeReference("Bar"))\n    .typeResolver { /* Logic for how to resolve types */ }\n    .build()\nval config = SchemaGeneratorConfig(supportedPackages, additionalTypes = setOf(fooCustom))\n'})}),"\n",(0,o.jsx)(n.h2,{id:"adding-missing-kotlin-types",children:"Adding Missing Kotlin Types"}),"\n",(0,o.jsxs)(n.p,{children:["In our above example, since the return type of the Kotlin code did not reference the Kotlin types ",(0,o.jsx)(n.code,{children:"Foo"})," or ",(0,o.jsx)(n.code,{children:"Bar"}),",\nreflection will not pick those up by default. They will also need to be added as additional Kotlin types (",(0,o.jsx)(n.code,{children:"KType"}),") when generating the schema."]}),"\n",(0,o.jsx)(n.pre,{children:(0,o.jsx)(n.code,{className:"language-kotlin",children:"val generator = SchemaGenerator(config)\nval schema = generator.use {\n    it.generateSchema(\n        queries = listOf(TopLevelObject(Query())),\n        additionalTypes = setOf(\n            Foo::class.createType(),\n            Bar::class.createType(),\n        )\n    )\n}\n"})}),"\n",(0,o.jsx)(n.h2,{id:"final-result",children:"Final Result"}),"\n",(0,o.jsx)(n.p,{children:"With all the above code, the final resulting schema should like this:"}),"\n",(0,o.jsx)(n.pre,{children:(0,o.jsx)(n.code,{className:"language-graphql",children:"type Query {\n    customUnion: FooOrBar!\n}\n\nunion FooOrBar = Foo | Bar\ntype Foo { number: Int! }\ntype Bar { value: String! }\n"})})]})}function d(e={}){const{wrapper:n}={...(0,i.R)(),...e.components};return n?(0,o.jsx)(n,{...e,children:(0,o.jsx)(h,{...e})}):h(e)}},28453:(e,n,t)=>{t.d(n,{R:()=>s,x:()=>r});var o=t(96540);const i={},a=o.createContext(i);function s(e){const n=o.useContext(a);return o.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function r(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(i):e.components||i:s(e.components),o.createElement(a.Provider,{value:n},e.children)}}}]);