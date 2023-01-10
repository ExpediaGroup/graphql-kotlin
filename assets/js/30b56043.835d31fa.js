"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[9765],{3905:(e,t,n)=>{n.d(t,{Zo:()=>c,kt:()=>m});var a=n(67294);function r(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function l(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);t&&(a=a.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,a)}return n}function i(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?l(Object(n),!0).forEach((function(t){r(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):l(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function o(e,t){if(null==e)return{};var n,a,r=function(e,t){if(null==e)return{};var n,a,r={},l=Object.keys(e);for(a=0;a<l.length;a++)n=l[a],t.indexOf(n)>=0||(r[n]=e[n]);return r}(e,t);if(Object.getOwnPropertySymbols){var l=Object.getOwnPropertySymbols(e);for(a=0;a<l.length;a++)n=l[a],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(r[n]=e[n])}return r}var s=a.createContext({}),u=function(e){var t=a.useContext(s),n=t;return e&&(n="function"==typeof e?e(t):i(i({},t),e)),n},c=function(e){var t=u(e.components);return a.createElement(s.Provider,{value:t},e.children)},p={inlineCode:"code",wrapper:function(e){var t=e.children;return a.createElement(a.Fragment,{},t)}},d=a.forwardRef((function(e,t){var n=e.components,r=e.mdxType,l=e.originalType,s=e.parentName,c=o(e,["components","mdxType","originalType","parentName"]),d=u(n),m=r,h=d["".concat(s,".").concat(m)]||d[m]||p[m]||l;return n?a.createElement(h,i(i({ref:t},c),{},{components:n})):a.createElement(h,i({ref:t},c))}));function m(e,t){var n=arguments,r=t&&t.mdxType;if("string"==typeof e||r){var l=n.length,i=new Array(l);i[0]=d;var o={};for(var s in t)hasOwnProperty.call(t,s)&&(o[s]=t[s]);o.originalType=e,o.mdxType="string"==typeof e?e:r,i[1]=o;for(var u=2;u<l;u++)i[u]=n[u];return a.createElement.apply(null,i)}return a.createElement.apply(null,n)}d.displayName="MDXCreateElement"},85162:(e,t,n)=>{n.d(t,{Z:()=>i});var a=n(67294),r=n(86010);const l="tabItem_Ymn6";function i(e){var t=e.children,n=e.hidden,i=e.className;return a.createElement("div",{role:"tabpanel",className:(0,r.Z)(l,i),hidden:n},t)}},65488:(e,t,n)=>{n.d(t,{Z:()=>m});var a=n(87462),r=n(67294),l=n(86010),i=n(72389),o=n(67392),s=n(7094),u=n(12466);const c="tabList__CuJ",p="tabItem_LNqP";function d(e){var t,n,i=e.lazy,d=e.block,m=e.defaultValue,h=e.values,v=e.groupId,f=e.className,g=r.Children.map(e.children,(function(e){if((0,r.isValidElement)(e)&&"value"in e.props)return e;throw new Error("Docusaurus error: Bad <Tabs> child <"+("string"==typeof e.type?e.type:e.type.name)+'>: all children of the <Tabs> component should be <TabItem>, and every <TabItem> should have a unique "value" prop.')})),k=null!=h?h:g.map((function(e){var t=e.props;return{value:t.value,label:t.label,attributes:t.attributes}})),b=(0,o.l)(k,(function(e,t){return e.value===t.value}));if(b.length>0)throw new Error('Docusaurus error: Duplicate values "'+b.map((function(e){return e.value})).join(", ")+'" found in <Tabs>. Every value needs to be unique.');var C=null===m?m:null!=(t=null!=m?m:null==(n=g.find((function(e){return e.props.default})))?void 0:n.props.value)?t:g[0].props.value;if(null!==C&&!k.some((function(e){return e.value===C})))throw new Error('Docusaurus error: The <Tabs> has a defaultValue "'+C+'" but none of its children has the corresponding value. Available values are: '+k.map((function(e){return e.value})).join(", ")+". If you intend to show no default tab, use defaultValue={null} instead.");var y=(0,s.U)(),N=y.tabGroupChoices,U=y.setTabGroupChoices,w=(0,r.useState)(C),x=w[0],z=w[1],T=[],D=(0,u.o5)().blockElementScrollPositionUntilNextRender;if(null!=v){var S=N[v];null!=S&&S!==x&&k.some((function(e){return e.value===S}))&&z(S)}var I=function(e){var t=e.currentTarget,n=T.indexOf(t),a=k[n].value;a!==x&&(D(t),z(a),null!=v&&U(v,String(a)))},q=function(e){var t,n=null;switch(e.key){case"Enter":I(e);break;case"ArrowRight":var a,r=T.indexOf(e.currentTarget)+1;n=null!=(a=T[r])?a:T[0];break;case"ArrowLeft":var l,i=T.indexOf(e.currentTarget)-1;n=null!=(l=T[i])?l:T[T.length-1]}null==(t=n)||t.focus()};return r.createElement("div",{className:(0,l.Z)("tabs-container",c)},r.createElement("ul",{role:"tablist","aria-orientation":"horizontal",className:(0,l.Z)("tabs",{"tabs--block":d},f)},k.map((function(e){var t=e.value,n=e.label,i=e.attributes;return r.createElement("li",(0,a.Z)({role:"tab",tabIndex:x===t?0:-1,"aria-selected":x===t,key:t,ref:function(e){return T.push(e)},onKeyDown:q,onClick:I},i,{className:(0,l.Z)("tabs__item",p,null==i?void 0:i.className,{"tabs__item--active":x===t})}),null!=n?n:t)}))),i?(0,r.cloneElement)(g.filter((function(e){return e.props.value===x}))[0],{className:"margin-top--md"}):r.createElement("div",{className:"margin-top--md"},g.map((function(e,t){return(0,r.cloneElement)(e,{key:t,hidden:e.props.value!==x})}))))}function m(e){var t=(0,i.Z)();return r.createElement(d,(0,a.Z)({key:String(t)},e))}},2686:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>d,contentTitle:()=>c,default:()=>v,frontMatter:()=>u,metadata:()=>p,toc:()=>m});var a=n(87462),r=n(63366),l=(n(67294),n(3905)),i=n(65488),o=n(85162),s=["components"],u={id:"client-customization",title:"Client Customization"},c=void 0,p={unversionedId:"client/client-customization",id:"version-4.x.x/client/client-customization",title:"Client Customization",description:"Ktor HTTP Client Customization",source:"@site/versioned_docs/version-4.x.x/client/client-customization.mdx",sourceDirName:"client",slug:"/client/client-customization",permalink:"/graphql-kotlin/docs/4.x.x/client/client-customization",draft:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-4.x.x/client/client-customization.mdx",tags:[],version:"4.x.x",lastUpdatedBy:"Dariusz Kuc",lastUpdatedAt:1673384839,formattedLastUpdatedAt:"Jan 10, 2023",frontMatter:{id:"client-customization",title:"Client Customization"},sidebar:"version-4.x.x/docs",previous:{title:"Client Features",permalink:"/graphql-kotlin/docs/4.x.x/client/client-features"},next:{title:"Client Serialization",permalink:"/graphql-kotlin/docs/4.x.x/client/client-serialization"}},d={},m=[{value:"Ktor HTTP Client Customization",id:"ktor-http-client-customization",level:2},{value:"Global Client Customization",id:"global-client-customization",level:3},{value:"Per Request Customization",id:"per-request-customization",level:3},{value:"Spring WebClient Customization",id:"spring-webclient-customization",level:2},{value:"Global Client Customization",id:"global-client-customization-1",level:3},{value:"Per Request Customization",id:"per-request-customization-1",level:3},{value:"Custom GraphQL Client",id:"custom-graphql-client",level:2},{value:"Deprecated Field Usage",id:"deprecated-field-usage",level:2},{value:"Custom GraphQL Scalars",id:"custom-graphql-scalars",level:2}],h={toc:m};function v(e){var t=e.components,n=(0,r.Z)(e,s);return(0,l.kt)("wrapper",(0,a.Z)({},h,n,{components:t,mdxType:"MDXLayout"}),(0,l.kt)("h2",{id:"ktor-http-client-customization"},"Ktor HTTP Client Customization"),(0,l.kt)("p",null,(0,l.kt)("inlineCode",{parentName:"p"},"GraphQLKtorClient")," is a thin wrapper on top of ",(0,l.kt)("a",{parentName:"p",href:"https://ktor.io/docs/client.html"},"Ktor HTTP Client")," and supports fully\nasynchronous non-blocking communication. It is highly customizable and can be configured with any supported Ktor HTTP\n",(0,l.kt)("a",{parentName:"p",href:"https://ktor.io/clients/http-client/engines.html"},"engine")," and ",(0,l.kt)("a",{parentName:"p",href:"https://ktor.io/clients/http-client/features.html"},"features"),"."),(0,l.kt)("p",null,"See ",(0,l.kt)("a",{parentName:"p",href:"https://ktor.io/clients/index.html"},"Ktor HTTP Client documentation")," for additional details."),(0,l.kt)("h3",{id:"global-client-customization"},"Global Client Customization"),(0,l.kt)("p",null,"A single instance of ",(0,l.kt)("inlineCode",{parentName:"p"},"GraphQLKtorClient")," can be used to handle many GraphQL operations. You can specify a custom instance\nof Ktor ",(0,l.kt)("inlineCode",{parentName:"p"},"HttpClient")," and a target ",(0,l.kt)("inlineCode",{parentName:"p"},"GraphQLClientSerializer"),"."),(0,l.kt)("p",null,"The below example configures a new ",(0,l.kt)("inlineCode",{parentName:"p"},"GraphQLKtorClient")," to use the ",(0,l.kt)("inlineCode",{parentName:"p"},"OkHttp")," engine with custom timeouts, adds a default ",(0,l.kt)("inlineCode",{parentName:"p"},"X-MY-API-KEY"),"\nheader to all requests, and enables basic logging of the requests."),(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-kotlin"},'val okHttpClient = HttpClient(engineFactory = OkHttp) {\n    engine {\n        config {\n            connectTimeout(10, TimeUnit.SECONDS)\n            readTimeout(60, TimeUnit.SECONDS)\n            writeTimeout(60, TimeUnit.SECONDS)\n        }\n    }\n    defaultRequest {\n        header("X-MY-API-KEY", "someSecretApiKey")\n    }\n    install(Logging) {\n        logger = Logger.DEFAULT\n        level = LogLevel.INFO\n    }\n}\nval client = GraphQLKtorClient(\n    url = URL("http://localhost:8080/graphql"),\n    httpClient = okHttpClient\n)\n')),(0,l.kt)("h3",{id:"per-request-customization"},"Per Request Customization"),(0,l.kt)("p",null,"Individual GraphQL requests can be customized through ",(0,l.kt)("a",{parentName:"p",href:"https://ktor.io/docs/request.html#customizing-requests"},"HttpRequestBuilder"),".\nYou can use this mechanism to specify custom headers, update target url to include custom query parameters, configure\nattributes that can be accessed from the pipeline features as well specify timeouts per request."),(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-kotlin"},'val helloWorldQuery = HelloWorldQuery(variables = HelloWorldQuery.Variables(name = "John Doe"))\nval result = client.execute(helloWorldQuery) {\n    header("X-B3-TraceId", "0123456789abcdef")\n}\n')),(0,l.kt)("h2",{id:"spring-webclient-customization"},"Spring WebClient Customization"),(0,l.kt)("p",null,(0,l.kt)("inlineCode",{parentName:"p"},"GraphQLWebClient")," is a thin wrapper on top of ",(0,l.kt)("a",{parentName:"p",href:"https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/reactive/function/client/WebClient.html"},"Spring WebClient"),"\nthat relies on Reactor Netty for fully asynchronous non-blocking communications. If you want to use Jetty instead you will\nneed to exclude provided ",(0,l.kt)("inlineCode",{parentName:"p"},"io.projectreactor.netty:reactor-netty")," dependency and instead add ",(0,l.kt)("inlineCode",{parentName:"p"},"org.eclipse.jetty:jetty-reactive-httpclient"),"\ndependency."),(0,l.kt)("h3",{id:"global-client-customization-1"},"Global Client Customization"),(0,l.kt)("p",null,"A single instance of ",(0,l.kt)("inlineCode",{parentName:"p"},"GraphQLWebClient")," can be used to handle many GraphQL operations and you can customize it by providing\na custom instance of ",(0,l.kt)("inlineCode",{parentName:"p"},"WebClient.Builder"),". See ",(0,l.kt)("a",{parentName:"p",href:"https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-webclient-customization"},"Spring documentation"),"\nfor additional details."),(0,l.kt)("p",null,"Example below configures ",(0,l.kt)("inlineCode",{parentName:"p"},"GraphQLWebClient")," with custom timeouts and adds a default ",(0,l.kt)("inlineCode",{parentName:"p"},"X-MY-API-KEY")," header to all requests."),(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-kotlin"},'val httpClient: HttpClient = HttpClient.create()\n    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10_000)\n    .responseTimeout(Duration.ofMillis(10_000))\nval connector: ClientHttpConnector = ReactorClientHttpConnector(httpClient.wiretap(true))\nval webClientBuilder = WebClient.builder()\n    .clientConnector(connector)\n    .defaultHeader("X-MY-API-KEY", "someSecretApiKey")\n\nval client = GraphQLWebClient(\n    url = "http://localhost:8080/graphql",\n    builder = webClientBuilder\n)\n')),(0,l.kt)("h3",{id:"per-request-customization-1"},"Per Request Customization"),(0,l.kt)("p",null,"Individual GraphQL requests can be customized by providing ",(0,l.kt)("inlineCode",{parentName:"p"},"WebClient.RequestBodyUriSpec")," lambda. You can use this mechanism\nto specify custom headers or include custom attributes or query parameters."),(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-kotlin"},'val helloWorldQuery = HelloWorldQuery(variables = HelloWorldQuery.Variables(name = "John Doe"))\nval result = client.execute(helloWorldQuery) {\n    header("X-B3-TraceId", "0123456789abcdef")\n}\n')),(0,l.kt)("h2",{id:"custom-graphql-client"},"Custom GraphQL Client"),(0,l.kt)("p",null,"GraphQL Kotlin libraries provide generic a ",(0,l.kt)("inlineCode",{parentName:"p"},"GraphQLClient")," interface as well as Ktor HTTP Client and Spring WebClient based\nreference implementations. Both ",(0,l.kt)("inlineCode",{parentName:"p"},"GraphQLKtorClient")," and ",(0,l.kt)("inlineCode",{parentName:"p"},"GraphQLWebClient")," are open classes which means you can also\nextend them to provide some custom ",(0,l.kt)("inlineCode",{parentName:"p"},"execute")," logic."),(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-kotlin"},"class CustomGraphQLClient(url: URL) : GraphQLKtorClient(url = url) {\n\n    override suspend fun <T: Any> execute(request: GraphQLClientRequest<T>, requestCustomizer: HttpRequestBuilder.() -> Unit): GraphQLClientResponse<T> {\n        // custom init logic\n        val result = super.execute(request, requestCustomizer)\n        // custom finalize logic\n        return result\n    }\n}\n")),(0,l.kt)("h2",{id:"deprecated-field-usage"},"Deprecated Field Usage"),(0,l.kt)("p",null,"Build plugins will automatically fail generation of a client if any of the specified query files are referencing\ndeprecated fields. This ensures that your clients have to explicitly opt-in into deprecated usage by specifying\n",(0,l.kt)("inlineCode",{parentName:"p"},"allowDeprecatedFields")," configuration option."),(0,l.kt)("h2",{id:"custom-graphql-scalars"},"Custom GraphQL Scalars"),(0,l.kt)("p",null,"By default, custom GraphQL scalars are serialized and ",(0,l.kt)("a",{parentName:"p",href:"https://kotlinlang.org/docs/reference/type-aliases.html"},"type-aliased"),"\nto a String. GraphQL Kotlin plugins also support custom serialization based on provided configuration."),(0,l.kt)("p",null,"In order to automatically convert between custom GraphQL ",(0,l.kt)("inlineCode",{parentName:"p"},"UUID")," scalar type and ",(0,l.kt)("inlineCode",{parentName:"p"},"java.util.UUID"),", we first need to create\nour custom ",(0,l.kt)("inlineCode",{parentName:"p"},"ScalarConverter"),"."),(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-kotlin"},"package com.example.client\n\nimport com.expediagroup.graphql.client.converter.ScalarConverter\nimport java.util.UUID\n\nclass UUIDScalarConverter : ScalarConverter<UUID> {\n    override fun toScalar(rawValue: Any): UUID = UUID.fromString(rawValue.toString())\n    override fun toJson(value: UUID): Any = value.toString()\n}\n")),(0,l.kt)("p",null,"And then configure build plugin by specifying"),(0,l.kt)("ul",null,(0,l.kt)("li",{parentName:"ul"},"Custom GraphQL scalar name"),(0,l.kt)("li",{parentName:"ul"},"Target class name"),(0,l.kt)("li",{parentName:"ul"},"Converter that provides logic to map between GraphQL and Kotlin type")),(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-kotlin"},'graphql {\n    packageName = "com.example.generated"\n    endpoint = "http://localhost:8080/graphql"\n    customScalars = listOf(GraphQLScalar("UUID", "java.util.UUID", "com.example.UUIDScalarConverter"))\n}\n')),(0,l.kt)("p",null,"Which will generate ",(0,l.kt)("inlineCode",{parentName:"p"},"UUID.kt")," wrapper class under ",(0,l.kt)("inlineCode",{parentName:"p"},"com.example.generated.scalars")," package."),(0,l.kt)(i.Z,{defaultValue:"jackson",values:[{label:"Jackson",value:"jackson"},{label:"kotlinx.serialization",value:"kotlinx"}],mdxType:"Tabs"},(0,l.kt)(o.Z,{value:"jackson",mdxType:"TabItem"},(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-kotlin"},"data class UUID(\n  val value: java.util.UUID\n) {\n  @JsonValue\n  fun rawValue() = converter.toJson(value)\n\n  companion object {\n    val converter: UUIDScalarConverter = UUIDScalarConverter()\n\n    @JsonCreator\n    @JvmStatic\n    fun create(rawValue: Any) = UUID(converter.toScalar(rawValue))\n  }\n}\n"))),(0,l.kt)(o.Z,{value:"kotlinx",mdxType:"TabItem"},(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-kotlin"},'@Serializable(with = UUIDSerializer::class)\ndata class UUID(\n  val value: java.util.UUID\n)\n\nclass UUIDSerializer : KSerializer<UUID> {\n  private val converter: UUIDScalarConverter = UUIDScalarConverter()\n\n  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("UUID", STRING)\n\n  override fun serialize(encoder: Encoder, value: UUID) {\n    val encoded = converter.toJson(value.value)\n    encoder.encodeString(encoded.toString())\n  }\n\n  override fun deserialize(decoder: Decoder): UUID {\n    val jsonDecoder = decoder as JsonDecoder\n    val element = jsonDecoder.decodeJsonElement()\n    val rawContent = element.jsonPrimitive.content\n    return UUID(value = converter.toScalar(rawContent))\n  }\n}\n')))),(0,l.kt)("p",null,"See ",(0,l.kt)("a",{parentName:"p",href:"/graphql-kotlin/docs/4.x.x/plugins/gradle-plugin-tasks"},"Gradle")," and ",(0,l.kt)("a",{parentName:"p",href:"/graphql-kotlin/docs/4.x.x/plugins/maven-plugin-goals"},"Maven")," plugin documentation for additional details."))}v.isMDXComponent=!0}}]);