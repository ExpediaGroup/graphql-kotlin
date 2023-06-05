"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[3110],{14006:(e,n,a)=>{a.r(n),a.d(n,{assets:()=>p,contentTitle:()=>o,default:()=>u,frontMatter:()=>l,metadata:()=>c,toc:()=>m});var t=a(87462),s=a(63366),r=(a(67294),a(3905)),i=(a(95657),["components"]),l={id:"interfaces",title:"Interfaces",original_id:"interfaces"},o=void 0,c={unversionedId:"schema-generator/writing-schemas/interfaces",id:"version-3.x.x/schema-generator/writing-schemas/interfaces",title:"Interfaces",description:"Functions returning interfaces will automatically expose all the types implementing this interface that are available on",source:"@site/versioned_docs/version-3.x.x/schema-generator/writing-schemas/interfaces.md",sourceDirName:"schema-generator/writing-schemas",slug:"/schema-generator/writing-schemas/interfaces",permalink:"/graphql-kotlin/docs/3.x.x/schema-generator/writing-schemas/interfaces",draft:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-3.x.x/schema-generator/writing-schemas/interfaces.md",tags:[],version:"3.x.x",lastUpdatedBy:"Shane Myrick",lastUpdatedAt:1685995381,formattedLastUpdatedAt:"Jun 5, 2023",frontMatter:{id:"interfaces",title:"Interfaces",original_id:"interfaces"},sidebar:"version-3.x.x/docs",previous:{title:"Lists",permalink:"/graphql-kotlin/docs/3.x.x/schema-generator/writing-schemas/lists"},next:{title:"Unions",permalink:"/graphql-kotlin/docs/3.x.x/schema-generator/writing-schemas/unions"}},p={},m=[{value:"Abstract and Sealed Classes",id:"abstract-and-sealed-classes",level:2}],d={toc:m},g="wrapper";function u(e){var n=e.components,a=(0,s.Z)(e,i);return(0,r.kt)(g,(0,t.Z)({},d,a,{components:n,mdxType:"MDXLayout"}),(0,r.kt)("p",null,"Functions returning interfaces will automatically expose all the types implementing this interface that are available on\nthe classpath. Due to the GraphQL distinction between interface and a union type, interfaces need to specify at least\none common field (property or a function)."),(0,r.kt)("p",null,"Abstract and sealed classes will also be converted to a GraphQL Interface."),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},'\ninterface Animal {\n    val type: AnimalType\n    fun sound(): String\n}\n\nenum class AnimalType {\n    CAT,\n    DOG\n}\n\nclass Dog : Animal {\n    override val type = AnimalType.DOG\n\n    override fun sound() = "bark"\n\n    fun barkAtEveryone(): String = "bark at everyone"\n}\n\nclass Cat : Animal {\n    override val type = AnimalType.CAT\n\n    override fun sound() = "meow"\n\n    fun ignoreEveryone(): String = "ignore everyone"\n}\n\nclass PolymorphicQuery {\n\n    fun animal(type: AnimalType): Animal? = when (type) {\n        AnimalType.CAT -> Cat()\n        AnimalType.DOG -> Dog()\n        else -> null\n    }\n}\n\n')),(0,r.kt)("p",null,"The above code will produce the following GraphQL schema:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-graphql"},"\ninterface Animal {\n  type: AnimalType!\n  sound: String!\n}\n\nenum AnimalType {\n  CAT\n  DOG\n}\n\ntype Cat implements Animal {\n  type: AnimalType!\n  ignoreEveryone: String!\n  sound: String!\n}\n\ntype Dog implements Animal {\n  type: AnimalType!\n  barkAtEveryone: String!\n  sound: String!\n}\n\ntype TopLevelQuery {\n  animal(type: AnimalType!): Animal\n}\n\n\n")),(0,r.kt)("h2",{id:"abstract-and-sealed-classes"},"Abstract and Sealed Classes"),(0,r.kt)("p",null,(0,r.kt)("a",{parentName:"p",href:"https://kotlinlang.org/docs/reference/classes.html#abstract-classes"},"Abstract")," and ",(0,r.kt)("a",{parentName:"p",href:"https://kotlinlang.org/docs/reference/sealed-classes.html"},"sealed")," classes can also be used for interface types."),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},"\nabstract class Shape(val area: Double)\nclass Circle(radius: Double) : Shape(PI * radius * radius)\nclass Square(sideLength: Double) : Shape(sideLength * sideLength)\n\nsealed class Pet(val name: String) {\n    class Dog(name: String, val goodBoysReceived: Int) : Pet(name)\n    class Cat(name: String, val livesRemaining: Int) : Pet(name)\n}\n\n")))}u.isMDXComponent=!0}}]);