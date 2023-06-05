"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[1628],{14930:(e,n,t)=>{t.r(n),t.d(n,{assets:()=>m,contentTitle:()=>c,default:()=>h,frontMatter:()=>r,metadata:()=>d,toc:()=>l});var a=t(87462),i=t(63366),s=(t(67294),t(3905)),o=(t(95657),["components"]),r={id:"documenting-schema",title:"Documenting Schema"},c=void 0,d={unversionedId:"schema-generator/customizing-schemas/documenting-schema",id:"version-6.x.x/schema-generator/customizing-schemas/documenting-schema",title:"Documenting Schema",description:"Since Javadocs are not available at runtime for introspection, graphql-kotlin-schema-generator includes an annotation",source:"@site/versioned_docs/version-6.x.x/schema-generator/customizing-schemas/documenting-schema.md",sourceDirName:"schema-generator/customizing-schemas",slug:"/schema-generator/customizing-schemas/documenting-schema",permalink:"/graphql-kotlin/docs/schema-generator/customizing-schemas/documenting-schema",draft:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-6.x.x/schema-generator/customizing-schemas/documenting-schema.md",tags:[],version:"6.x.x",lastUpdatedBy:"Shane Myrick",lastUpdatedAt:1685995381,formattedLastUpdatedAt:"Jun 5, 2023",frontMatter:{id:"documenting-schema",title:"Documenting Schema"},sidebar:"docs",previous:{title:"Generator Configuration & Hooks",permalink:"/graphql-kotlin/docs/schema-generator/customizing-schemas/generator-config"},next:{title:"Excluding Fields",permalink:"/graphql-kotlin/docs/schema-generator/customizing-schemas/excluding-fields"}},m={},l=[],g={toc:l},u="wrapper";function h(e){var n=e.components,t=(0,i.Z)(e,o);return(0,s.kt)(u,(0,a.Z)({},g,t,{components:n,mdxType:"MDXLayout"}),(0,s.kt)("p",null,"Since Javadocs are not available at runtime for introspection, ",(0,s.kt)("inlineCode",{parentName:"p"},"graphql-kotlin-schema-generator")," includes an annotation\nclass ",(0,s.kt)("inlineCode",{parentName:"p"},"@GraphQLDescription")," that can be used to add schema descriptions to ",(0,s.kt)("em",{parentName:"p"},"any")," GraphQL schema element. The string value can be in the Markdown format."),(0,s.kt)("pre",null,(0,s.kt)("code",{parentName:"pre",className:"language-kotlin"},'@GraphQLDescription("A useful widget")\ndata class Widget(\n  @GraphQLDescription("The widget\'s value that can be `null`")\n  val value: Int?\n)\n\nclass WidgetQuery {\n  @GraphQLDescription("Creates new widget for given ID")\n  fun widgetById(@GraphQLDescription("The special ingredient") id: Int): Widget? = Widget(id)\n}\n')),(0,s.kt)("p",null,"The above query would produce the following GraphQL schema:"),(0,s.kt)("pre",null,(0,s.kt)("code",{parentName:"pre",className:"language-graphql"},'schema {\n  query: Query\n}\n\ntype Query {\n  """Creates new widget for given ID"""\n  widgetById(\n    """The special ingredient"""\n    id: Int!\n  ): Widget\n}\n\n"""A useful widget"""\ntype Widget {\n  """The widget\'s value that can be `null`"""\n  value: Int\n}\n')))}h.isMDXComponent=!0}}]);