"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[333],{54881:(e,n,a)=>{a.r(n),a.d(n,{assets:()=>c,contentTitle:()=>o,default:()=>m,frontMatter:()=>r,metadata:()=>i,toc:()=>l});var t=a(74848),s=a(28453);const r={id:"enums",title:"Enums"},o=void 0,i={id:"schema-generator/writing-schemas/enums",title:"Enums",description:"Enums are automatically mapped to GraphQL enum type.",source:"@site/versioned_docs/version-8.x.x/schema-generator/writing-schemas/enums.md",sourceDirName:"schema-generator/writing-schemas",slug:"/schema-generator/writing-schemas/enums",permalink:"/graphql-kotlin/docs/schema-generator/writing-schemas/enums",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-8.x.x/schema-generator/writing-schemas/enums.md",tags:[],version:"8.x.x",lastUpdatedBy:"Samuel Vazquez",lastUpdatedAt:172791071e4,frontMatter:{id:"enums",title:"Enums"},sidebar:"docs",previous:{title:"Scalars",permalink:"/graphql-kotlin/docs/schema-generator/writing-schemas/scalars"},next:{title:"Lists",permalink:"/graphql-kotlin/docs/schema-generator/writing-schemas/lists"}},c={},l=[{value:"Converting a Java enum to a GraphQL Enum",id:"converting-a-java-enum-to-a-graphql-enum",level:3}];function u(e){const n={a:"a",code:"code",h3:"h3",p:"p",pre:"pre",strong:"strong",...(0,s.R)(),...e.components};return(0,t.jsxs)(t.Fragment,{children:[(0,t.jsx)(n.p,{children:"Enums are automatically mapped to GraphQL enum type."}),"\n",(0,t.jsx)(n.pre,{children:(0,t.jsx)(n.code,{className:"language-kotlin",children:"enum class MyEnumType {\n  ONE,\n  TWO\n}\n"})}),"\n",(0,t.jsx)(n.p,{children:"Above enum will be generated as following GraphQL object"}),"\n",(0,t.jsx)(n.pre,{children:(0,t.jsx)(n.code,{className:"language-graphql",children:"enum MyEnumType {\n  ONE\n  TWO\n}\n"})}),"\n",(0,t.jsx)(n.h3,{id:"converting-a-java-enum-to-a-graphql-enum",children:"Converting a Java enum to a GraphQL Enum"}),"\n",(0,t.jsxs)(n.p,{children:["If you want to use Java enums from another package, but you ",(0,t.jsx)(n.strong,{children:"don't"})," want\ninclude everything from that package using ",(0,t.jsx)(n.a,{href:"/graphql-kotlin/docs/schema-generator/customizing-schemas/generator-config",children:(0,t.jsx)(n.code,{children:"supportedPackages"})})," or you want\nto customize the GraphQL type, you can use ",(0,t.jsx)(n.a,{href:"/graphql-kotlin/docs/schema-generator/customizing-schemas/generator-config",children:"schema generator hooks"})," to\nassociate the Java enum with a runtime ",(0,t.jsx)(n.a,{href:"https://javadoc.io/doc/com.graphql-java/graphql-java/latest/index.html",children:(0,t.jsx)(n.code,{children:"GraphQLEnumType"})}),"."]}),"\n",(0,t.jsx)(n.p,{children:"Step 1: Create a GraphQLEnumType using the Java enum values"}),"\n",(0,t.jsx)(n.pre,{children:(0,t.jsx)(n.code,{className:"language-java",children:"// in some other package\npublic enum Status {\n  APPROVED,\n  DECLINED\n}\n"})}),"\n",(0,t.jsx)(n.pre,{children:(0,t.jsx)(n.code,{className:"language-kotlin",children:'val statusEnumType = GraphQLEnumType.newEnum()\n    .name("Status")\n    .values(Status.values().map {\n      GraphQLEnumValueDefinition.newEnumValueDefinition()\n          .value(it.name)\n          .build()\n    })\n    .build()\n'})}),"\n",(0,t.jsx)(n.p,{children:"Step 2: Add a schema generation hook"}),"\n",(0,t.jsx)(n.pre,{children:(0,t.jsx)(n.code,{className:"language-kotlin",children:"class CustomSchemaGeneratorHooks : SchemaGeneratorHooks {\n\n  override fun willGenerateGraphQLType(type: KType): GraphQLType? {\n    return when (type.classifier as? KClass<*>) {\n      Status::class.java -> statusEnumType\n      else -> super.willGenerateGraphQLType(type)\n    }\n  }\n}\n"})}),"\n",(0,t.jsx)(n.p,{children:"Step 3. Use your Java enum anywhere in your schema"}),"\n",(0,t.jsx)(n.pre,{children:(0,t.jsx)(n.code,{className:"language-kotlin",children:"@Component\nclass StatusQuery : Query {\n  fun currentStatus: Status = getCurrentStatus()\n}\n"})})]})}function m(e={}){const{wrapper:n}={...(0,s.R)(),...e.components};return n?(0,t.jsx)(n,{...e,children:(0,t.jsx)(u,{...e})}):u(e)}},28453:(e,n,a)=>{a.d(n,{R:()=>o,x:()=>i});var t=a(96540);const s={},r=t.createContext(s);function o(e){const n=t.useContext(r);return t.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function i(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(s):e.components||s:o(e.components),t.createElement(r.Provider,{value:n},e.children)}}}]);