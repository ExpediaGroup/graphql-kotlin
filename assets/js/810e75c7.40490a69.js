"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[7791],{3633:(t,e,n)=>{n.r(e),n.d(e,{assets:()=>d,contentTitle:()=>l,default:()=>c,frontMatter:()=>s,metadata:()=>p,toc:()=>m});var a=n(7462),r=n(3366),i=(n(7294),n(3905)),o=(n(8561),["components"]),s={id:"lists",title:"Lists",original_id:"lists"},l=void 0,p={unversionedId:"schema-generator/writing-schemas/lists",id:"version-3.x.x/schema-generator/writing-schemas/lists",title:"Lists",description:"Both kotlin.Array and kotlin.collections.List are automatically mapped to the GraphQL List type (for unsupported",source:"@site/versioned_docs/version-3.x.x/schema-generator/writing-schemas/lists.md",sourceDirName:"schema-generator/writing-schemas",slug:"/schema-generator/writing-schemas/lists",permalink:"/graphql-kotlin/docs/3.x.x/schema-generator/writing-schemas/lists",draft:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-3.x.x/schema-generator/writing-schemas/lists.md",tags:[],version:"3.x.x",lastUpdatedBy:"Samuel Vazquez",lastUpdatedAt:1677182897,formattedLastUpdatedAt:"Feb 23, 2023",frontMatter:{id:"lists",title:"Lists",original_id:"lists"},sidebar:"version-3.x.x/docs",previous:{title:"Enums",permalink:"/graphql-kotlin/docs/3.x.x/schema-generator/writing-schemas/enums"},next:{title:"Interfaces",permalink:"/graphql-kotlin/docs/3.x.x/schema-generator/writing-schemas/interfaces"}},d={},m=[{value:"Primitive Arrays",id:"primitive-arrays",level:2},{value:"Unsupported Collection Types",id:"unsupported-collection-types",level:2}],h={toc:m};function c(t){var e=t.components,n=(0,r.Z)(t,o);return(0,i.kt)("wrapper",(0,a.Z)({},h,n,{components:e,mdxType:"MDXLayout"}),(0,i.kt)("p",null,"Both ",(0,i.kt)("inlineCode",{parentName:"p"},"kotlin.Array")," and ",(0,i.kt)("inlineCode",{parentName:"p"},"kotlin.collections.List")," are automatically mapped to the GraphQL ",(0,i.kt)("inlineCode",{parentName:"p"},"List")," type (for unsupported\nuse cases see below). Type arguments provided to Kotlin collections are used as the type arguments in the GraphQL ",(0,i.kt)("inlineCode",{parentName:"p"},"List"),"\ntype. Kotlin specialized classes representing arrays of Java primitive types without boxing overhead (e.g. ",(0,i.kt)("inlineCode",{parentName:"p"},"IntArray"),")\nare also supported."),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-kotlin"},"\nclass SimpleQuery {\n    fun generateList(): List<Int> {\n        // some logic here that generates list\n    }\n\n    fun doSomethingWithIntArray(ints: IntArray): String {\n        // some logic here that processes array\n    }\n\n    fun doSomethingWithIntList(ints: List<Int>): String {\n        // some logic here that processes list\n    }\n}\n\n")),(0,i.kt)("p",null,"The above Kotlin class would produce the following GraphQL schema:"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-graphql"},"\ntype Query {\n    generateList: [Int!]!\n    doSomethingWithIntArray(ints: [Int!]!): String!\n    doSomethingWithIntList(ints: [Int!]!): String!\n}\n\n")),(0,i.kt)("h2",{id:"primitive-arrays"},"Primitive Arrays"),(0,i.kt)("p",null,(0,i.kt)("inlineCode",{parentName:"p"},"graphql-kotlin-schema-generator")," supports the following primitive array types without autoboxing overhead. Similarly to\nthe ",(0,i.kt)("inlineCode",{parentName:"p"},"kotlin.Array")," of objects the underlying type is automatically mapped to GraphQL ",(0,i.kt)("inlineCode",{parentName:"p"},"List")," type."),(0,i.kt)("table",null,(0,i.kt)("thead",{parentName:"table"},(0,i.kt)("tr",{parentName:"thead"},(0,i.kt)("th",{parentName:"tr",align:null},"Kotlin Type"))),(0,i.kt)("tbody",{parentName:"table"},(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:null},(0,i.kt)("inlineCode",{parentName:"td"},"kotlin.IntArray"))),(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:null},(0,i.kt)("inlineCode",{parentName:"td"},"kotlin.LongArray"))),(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:null},(0,i.kt)("inlineCode",{parentName:"td"},"kotlin.ShortArray"))),(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:null},(0,i.kt)("inlineCode",{parentName:"td"},"kotlin.FloatArray"))),(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:null},(0,i.kt)("inlineCode",{parentName:"td"},"kotlin.DoubleArray"))),(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:null},(0,i.kt)("inlineCode",{parentName:"td"},"kotlin.CharArray"))),(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:null},(0,i.kt)("inlineCode",{parentName:"td"},"kotlin.BooleanArray"))))),(0,i.kt)("p",null,">"," NOTE: Underlying GraphQL types of primitive arrays will be corresponding to the built-in scalar types or extended\n",">"," scalar types provided by ",(0,i.kt)("inlineCode",{parentName:"p"},"graphql-java"),"."),(0,i.kt)("h2",{id:"unsupported-collection-types"},"Unsupported Collection Types"),(0,i.kt)("p",null,"Currently GraphQL spec only supports ",(0,i.kt)("inlineCode",{parentName:"p"},"Lists"),". Therefore even though Java and Kotlin support number of other collection\ntypes, ",(0,i.kt)("inlineCode",{parentName:"p"},"graphql-kotlin-schema-generator")," only explicitly supports ",(0,i.kt)("inlineCode",{parentName:"p"},"Lists")," and primitive arrays. Other collection types\nsuch as ",(0,i.kt)("inlineCode",{parentName:"p"},"Sets")," (see ",(0,i.kt)("a",{parentName:"p",href:"https://github.com/ExpediaGroup/graphql-kotlin/issues/201"},"#201"),") and arbitrary ",(0,i.kt)("inlineCode",{parentName:"p"},"Map")," data\nstructures are not supported."))}c.isMDXComponent=!0}}]);