"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[3087],{42619:(e,n,t)=>{t.r(n),t.d(n,{assets:()=>u,contentTitle:()=>s,default:()=>h,frontMatter:()=>o,metadata:()=>c,toc:()=>d});var r=t(74848),i=t(28453),a=t(11470),l=t(19365);const o={id:"client-customization",title:"Client Customization"},s=void 0,c={id:"client/client-customization",title:"Client Customization",description:"Ktor HTTP Client Customization",source:"@site/versioned_docs/version-5.x.x/client/client-customization.mdx",sourceDirName:"client",slug:"/client/client-customization",permalink:"/graphql-kotlin/docs/5.x.x/client/client-customization",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-5.x.x/client/client-customization.mdx",tags:[],version:"5.x.x",lastUpdatedBy:"mykevinjung",lastUpdatedAt:1721694712e3,frontMatter:{id:"client-customization",title:"Client Customization"},sidebar:"docs",previous:{title:"Client Features",permalink:"/graphql-kotlin/docs/5.x.x/client/client-features"},next:{title:"Client Serialization",permalink:"/graphql-kotlin/docs/5.x.x/client/client-serialization"}},u={},d=[{value:"Ktor HTTP Client Customization",id:"ktor-http-client-customization",level:2},{value:"Global Client Customization",id:"global-client-customization",level:3},{value:"Per Request Customization",id:"per-request-customization",level:3},{value:"Spring WebClient Customization",id:"spring-webclient-customization",level:2},{value:"Global Client Customization",id:"global-client-customization-1",level:3},{value:"Per Request Customization",id:"per-request-customization-1",level:3},{value:"Custom GraphQL Client",id:"custom-graphql-client",level:2},{value:"Deprecated Field Usage",id:"deprecated-field-usage",level:2},{value:"Custom GraphQL Scalars",id:"custom-graphql-scalars",level:2}];function p(e){const n={a:"a",code:"code",h2:"h2",h3:"h3",li:"li",p:"p",pre:"pre",ul:"ul",...(0,i.R)(),...e.components};return(0,r.jsxs)(r.Fragment,{children:[(0,r.jsx)(n.h2,{id:"ktor-http-client-customization",children:"Ktor HTTP Client Customization"}),"\n",(0,r.jsxs)(n.p,{children:[(0,r.jsx)(n.code,{children:"GraphQLKtorClient"})," is a thin wrapper on top of ",(0,r.jsx)(n.a,{href:"https://ktor.io/docs/client.html",children:"Ktor HTTP Client"})," and supports fully\nasynchronous non-blocking communication. It is highly customizable and can be configured with any supported Ktor HTTP\n",(0,r.jsx)(n.a,{href:"https://ktor.io/clients/http-client/engines.html",children:"engine"})," and ",(0,r.jsx)(n.a,{href:"https://ktor.io/clients/http-client/features.html",children:"features"}),"."]}),"\n",(0,r.jsxs)(n.p,{children:["See ",(0,r.jsx)(n.a,{href:"https://ktor.io/clients/index.html",children:"Ktor HTTP Client documentation"})," for additional details."]}),"\n",(0,r.jsx)(n.h3,{id:"global-client-customization",children:"Global Client Customization"}),"\n",(0,r.jsxs)(n.p,{children:["A single instance of ",(0,r.jsx)(n.code,{children:"GraphQLKtorClient"})," can be used to handle many GraphQL operations. You can specify a custom instance\nof Ktor ",(0,r.jsx)(n.code,{children:"HttpClient"})," and a target ",(0,r.jsx)(n.code,{children:"GraphQLClientSerializer"}),"."]}),"\n",(0,r.jsxs)(n.p,{children:["The below example configures a new ",(0,r.jsx)(n.code,{children:"GraphQLKtorClient"})," to use the ",(0,r.jsx)(n.code,{children:"OkHttp"})," engine with custom timeouts, adds a default ",(0,r.jsx)(n.code,{children:"X-MY-API-KEY"}),"\nheader to all requests, and enables basic logging of the requests."]}),"\n",(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-kotlin",children:'val okHttpClient = HttpClient(engineFactory = OkHttp) {\n    engine {\n        config {\n            connectTimeout(10, TimeUnit.SECONDS)\n            readTimeout(60, TimeUnit.SECONDS)\n            writeTimeout(60, TimeUnit.SECONDS)\n        }\n    }\n    defaultRequest {\n        header("X-MY-API-KEY", "someSecretApiKey")\n    }\n    install(Logging) {\n        logger = Logger.DEFAULT\n        level = LogLevel.INFO\n    }\n}\nval client = GraphQLKtorClient(\n    url = URL("http://localhost:8080/graphql"),\n    httpClient = okHttpClient\n)\n'})}),"\n",(0,r.jsx)(n.h3,{id:"per-request-customization",children:"Per Request Customization"}),"\n",(0,r.jsxs)(n.p,{children:["Individual GraphQL requests can be customized through ",(0,r.jsx)(n.a,{href:"https://ktor.io/docs/request.html#customizing-requests",children:"HttpRequestBuilder"}),".\nYou can use this mechanism to specify custom headers, update target url to include custom query parameters, configure\nattributes that can be accessed from the pipeline features as well specify timeouts per request."]}),"\n",(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-kotlin",children:'val helloWorldQuery = HelloWorldQuery(variables = HelloWorldQuery.Variables(name = "John Doe"))\nval result = client.execute(helloWorldQuery) {\n    header("X-B3-TraceId", "0123456789abcdef")\n}\n'})}),"\n",(0,r.jsx)(n.h2,{id:"spring-webclient-customization",children:"Spring WebClient Customization"}),"\n",(0,r.jsxs)(n.p,{children:[(0,r.jsx)(n.code,{children:"GraphQLWebClient"})," is a thin wrapper on top of ",(0,r.jsx)(n.a,{href:"https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/reactive/function/client/WebClient.html",children:"Spring WebClient"}),"\nthat relies on Reactor Netty for fully asynchronous non-blocking communications. If you want to use Jetty instead you will\nneed to exclude provided ",(0,r.jsx)(n.code,{children:"io.projectreactor.netty:reactor-netty"})," dependency and instead add ",(0,r.jsx)(n.code,{children:"org.eclipse.jetty:jetty-reactive-httpclient"}),"\ndependency."]}),"\n",(0,r.jsx)(n.h3,{id:"global-client-customization-1",children:"Global Client Customization"}),"\n",(0,r.jsxs)(n.p,{children:["A single instance of ",(0,r.jsx)(n.code,{children:"GraphQLWebClient"})," can be used to handle many GraphQL operations and you can customize it by providing\na custom instance of ",(0,r.jsx)(n.code,{children:"WebClient.Builder"}),". See ",(0,r.jsx)(n.a,{href:"https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-webclient-customization",children:"Spring documentation"}),"\nfor additional details."]}),"\n",(0,r.jsxs)(n.p,{children:["Example below configures ",(0,r.jsx)(n.code,{children:"GraphQLWebClient"})," with custom timeouts and adds a default ",(0,r.jsx)(n.code,{children:"X-MY-API-KEY"})," header to all requests."]}),"\n",(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-kotlin",children:'val httpClient: HttpClient = HttpClient.create()\n    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10_000)\n    .responseTimeout(Duration.ofMillis(10_000))\nval connector: ClientHttpConnector = ReactorClientHttpConnector(httpClient.wiretap(true))\nval webClientBuilder = WebClient.builder()\n    .clientConnector(connector)\n    .defaultHeader("X-MY-API-KEY", "someSecretApiKey")\n\nval client = GraphQLWebClient(\n    url = "http://localhost:8080/graphql",\n    builder = webClientBuilder\n)\n'})}),"\n",(0,r.jsx)(n.h3,{id:"per-request-customization-1",children:"Per Request Customization"}),"\n",(0,r.jsxs)(n.p,{children:["Individual GraphQL requests can be customized by providing ",(0,r.jsx)(n.code,{children:"WebClient.RequestBodyUriSpec"})," lambda. You can use this mechanism\nto specify custom headers or include custom attributes or query parameters."]}),"\n",(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-kotlin",children:'val helloWorldQuery = HelloWorldQuery(variables = HelloWorldQuery.Variables(name = "John Doe"))\nval result = client.execute(helloWorldQuery) {\n    header("X-B3-TraceId", "0123456789abcdef")\n}\n'})}),"\n",(0,r.jsx)(n.h2,{id:"custom-graphql-client",children:"Custom GraphQL Client"}),"\n",(0,r.jsxs)(n.p,{children:["GraphQL Kotlin libraries provide generic a ",(0,r.jsx)(n.code,{children:"GraphQLClient"})," interface as well as Ktor HTTP Client and Spring WebClient based\nreference implementations. Both ",(0,r.jsx)(n.code,{children:"GraphQLKtorClient"})," and ",(0,r.jsx)(n.code,{children:"GraphQLWebClient"})," are open classes which means you can also\nextend them to provide some custom ",(0,r.jsx)(n.code,{children:"execute"})," logic."]}),"\n",(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-kotlin",children:"class CustomGraphQLClient(url: URL) : GraphQLKtorClient(url = url) {\n\n    override suspend fun <T: Any> execute(request: GraphQLClientRequest<T>, requestCustomizer: HttpRequestBuilder.() -> Unit): GraphQLClientResponse<T> {\n        // custom init logic\n        val result = super.execute(request, requestCustomizer)\n        // custom finalize logic\n        return result\n    }\n}\n"})}),"\n",(0,r.jsx)(n.h2,{id:"deprecated-field-usage",children:"Deprecated Field Usage"}),"\n",(0,r.jsxs)(n.p,{children:["Build plugins will automatically fail generation of a client if any of the specified query files are referencing\ndeprecated fields. This ensures that your clients have to explicitly opt-in into deprecated usage by specifying\n",(0,r.jsx)(n.code,{children:"allowDeprecatedFields"})," configuration option."]}),"\n",(0,r.jsx)(n.h2,{id:"custom-graphql-scalars",children:"Custom GraphQL Scalars"}),"\n",(0,r.jsxs)(n.p,{children:["By default, custom GraphQL scalars are serialized and ",(0,r.jsx)(n.a,{href:"https://kotlinlang.org/docs/reference/type-aliases.html",children:"type-aliased"}),"\nto a String. GraphQL Kotlin plugins also support custom serialization based on provided configuration."]}),"\n",(0,r.jsxs)(n.p,{children:["In order to automatically convert between custom GraphQL ",(0,r.jsx)(n.code,{children:"UUID"})," scalar type and ",(0,r.jsx)(n.code,{children:"java.util.UUID"}),", we first need to create\nour custom ",(0,r.jsx)(n.code,{children:"ScalarConverter"}),"."]}),"\n",(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-kotlin",children:"package com.example.client\n\nimport com.expediagroup.graphql.client.converter.ScalarConverter\nimport java.util.UUID\n\nclass UUIDScalarConverter : ScalarConverter<UUID> {\n    override fun toScalar(rawValue: Any): UUID = UUID.fromString(rawValue.toString())\n    override fun toJson(value: UUID): Any = value.toString()\n}\n"})}),"\n",(0,r.jsx)(n.p,{children:"And then configure build plugin by specifying"}),"\n",(0,r.jsxs)(n.ul,{children:["\n",(0,r.jsx)(n.li,{children:"Custom GraphQL scalar name"}),"\n",(0,r.jsx)(n.li,{children:"Target JVM class name"}),"\n",(0,r.jsx)(n.li,{children:"Converter that provides logic to map between GraphQL and Kotlin type"}),"\n"]}),"\n",(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-kotlin",children:'graphql {\n    packageName = "com.example.generated"\n    endpoint = "http://localhost:8080/graphql"\n    customScalars = listOf(GraphQLScalar("UUID", "java.util.UUID", "com.example.UUIDScalarConverter"))\n}\n'})}),"\n",(0,r.jsxs)(n.p,{children:["Custom scalar fields will then be automatically converted to a ",(0,r.jsx)(n.code,{children:"java.util.UUID"})," type using appropriate converter/serializer."]}),"\n",(0,r.jsxs)(a.A,{defaultValue:"jackson",values:[{label:"Jackson",value:"jackson"},{label:"kotlinx.serialization",value:"kotlinx"}],children:[(0,r.jsxs)(l.A,{value:"jackson",children:[(0,r.jsxs)(n.p,{children:["Following converters will be generated under ",(0,r.jsx)(n.code,{children:"com.example.generated.scalars"})," package."]}),(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-kotlin",children:"@Generated\npublic class AnyToUUIDConverter : StdConverter<Any, UUID>() {\n  private val converter: UUIDScalarConverter = UUIDScalarConverter()\n\n  public override fun convert(`value`: Any): UUID = converter.toScalar(value)\n}\n\n@Generated\npublic class UUIDToAnyConverter : StdConverter<UUID, Any>() {\n  private val converter: UUIDScalarConverter = UUIDScalarConverter()\n\n  public override fun convert(`value`: UUID): Any = converter.toJson(value)\n}\n"})}),(0,r.jsx)(n.p,{children:"Custom scalars fields will then be annotated with Jackson annotations referencing the above converters."}),(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-kotlin",children:"@Generated\npublic data class Result(\n  @JsonSerialize(converter = UUIDToAnyConverter::class)\n  @JsonDeserialize(converter = AnyToUUIDConverter::class)\n  public val custom: UUID,\n  @JsonSerialize(contentConverter = UUIDToAnyConverter::class)\n  @JsonDeserialize(contentConverter = AnyToUUIDConverter::class)\n  public val customList: List<UUID>\n)\n"})})]}),(0,r.jsxs)(l.A,{value:"kotlinx",children:[(0,r.jsxs)(n.p,{children:["Following serializer will be generated under ",(0,r.jsx)(n.code,{children:"com.example.generated.scalars"})," package."]}),(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-kotlin",children:'@Generated\npublic object UUIDSerializer : KSerializer<UUID> {\n  private val converter: UUIDScalarConverter = UUIDScalarConverter()\n\n  public override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("UUID", STRING)\n\n  public override fun serialize(encoder: Encoder, `value`: UUID): Unit {\n    val encoded = converter.toJson(value)\n    encoder.encodeString(encoded.toString())\n  }\n\n  public override fun deserialize(decoder: Decoder): UUID {\n    val jsonDecoder = decoder as JsonDecoder\n    val element = jsonDecoder.decodeJsonElement()\n    val rawContent = element.jsonPrimitive.content\n    return converter.toScalar(rawContent)\n  }\n}\n'})}),(0,r.jsxs)(n.p,{children:["Custom scalars fields will then be annotated with ",(0,r.jsx)(n.code,{children:"@Serializable"})," annotation referencing the above serializer."]}),(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-kotlin",children:"@Generated\n@Serializable\npublic data class Result(\n  @Serializable(with = UUIDSerializer::class)\n  public val custom: UUID,\n  public val customList: List<@Serializable(with = UUIDSerializer::class) UUID>\n)\n"})})]})]}),"\n",(0,r.jsxs)(n.p,{children:["See ",(0,r.jsx)(n.a,{href:"/graphql-kotlin/docs/5.x.x/plugins/gradle-plugin-tasks",children:"Gradle"})," and ",(0,r.jsx)(n.a,{href:"/graphql-kotlin/docs/5.x.x/plugins/maven-plugin-goals",children:"Maven"})," plugin documentation for additional details."]})]})}function h(e={}){const{wrapper:n}={...(0,i.R)(),...e.components};return n?(0,r.jsx)(n,{...e,children:(0,r.jsx)(p,{...e})}):p(e)}},19365:(e,n,t)=>{t.d(n,{A:()=>l});t(96540);var r=t(34164);const i={tabItem:"tabItem_Ymn6"};var a=t(74848);function l(e){var n=e.children,t=e.hidden,l=e.className;return(0,a.jsx)("div",{role:"tabpanel",className:(0,r.A)(i.tabItem,l),hidden:t,children:n})}},11470:(e,n,t)=>{t.d(n,{A:()=>y});var r=t(96540),i=t(34164),a=t(23104),l=t(56347),o=t(205),s=t(57485),c=t(31682),u=t(70679);function d(e){var n,t;return null!=(n=null==(t=r.Children.toArray(e).filter((function(e){return"\n"!==e})).map((function(e){if(!e||(0,r.isValidElement)(e)&&((n=e.props)&&"object"==typeof n&&"value"in n))return e;var n;throw new Error("Docusaurus error: Bad <Tabs> child <"+("string"==typeof e.type?e.type:e.type.name)+'>: all children of the <Tabs> component should be <TabItem>, and every <TabItem> should have a unique "value" prop.')})))?void 0:t.filter(Boolean))?n:[]}function p(e){var n=e.values,t=e.children;return(0,r.useMemo)((function(){var e=null!=n?n:function(e){return d(e).map((function(e){var n=e.props;return{value:n.value,label:n.label,attributes:n.attributes,default:n.default}}))}(t);return function(e){var n=(0,c.X)(e,(function(e,n){return e.value===n.value}));if(n.length>0)throw new Error('Docusaurus error: Duplicate values "'+n.map((function(e){return e.value})).join(", ")+'" found in <Tabs>. Every value needs to be unique.')}(e),e}),[n,t])}function h(e){var n=e.value;return e.tabValues.some((function(e){return e.value===n}))}function m(e){var n=e.queryString,t=void 0!==n&&n,i=e.groupId,a=(0,l.W6)(),o=function(e){var n=e.queryString,t=void 0!==n&&n,r=e.groupId;if("string"==typeof t)return t;if(!1===t)return null;if(!0===t&&!r)throw new Error('Docusaurus error: The <Tabs> component groupId prop is required if queryString=true, because this value is used as the search param name. You can also provide an explicit value such as queryString="my-search-param".');return null!=r?r:null}({queryString:t,groupId:i});return[(0,s.aZ)(o),(0,r.useCallback)((function(e){if(o){var n=new URLSearchParams(a.location.search);n.set(o,e),a.replace(Object.assign({},a.location,{search:n.toString()}))}}),[o,a])]}function v(e){var n,t,i,a,l=e.defaultValue,s=e.queryString,c=void 0!==s&&s,d=e.groupId,v=p(e),g=(0,r.useState)((function(){return function(e){var n,t=e.defaultValue,r=e.tabValues;if(0===r.length)throw new Error("Docusaurus error: the <Tabs> component requires at least one <TabItem> children component");if(t){if(!h({value:t,tabValues:r}))throw new Error('Docusaurus error: The <Tabs> has a defaultValue "'+t+'" but none of its children has the corresponding value. Available values are: '+r.map((function(e){return e.value})).join(", ")+". If you intend to show no default tab, use defaultValue={null} instead.");return t}var i=null!=(n=r.find((function(e){return e.default})))?n:r[0];if(!i)throw new Error("Unexpected error: 0 tabValues");return i.value}({defaultValue:l,tabValues:v})})),f=g[0],b=g[1],x=m({queryString:c,groupId:d}),j=x[0],C=x[1],y=(n=function(e){return e?"docusaurus.tab."+e:null}({groupId:d}.groupId),t=(0,u.Dv)(n),i=t[0],a=t[1],[i,(0,r.useCallback)((function(e){n&&a.set(e)}),[n,a])]),U=y[0],I=y[1],S=function(){var e=null!=j?j:U;return h({value:e,tabValues:v})?e:null}();return(0,o.A)((function(){S&&b(S)}),[S]),{selectedValue:f,selectValue:(0,r.useCallback)((function(e){if(!h({value:e,tabValues:v}))throw new Error("Can't select invalid tab value="+e);b(e),C(e),I(e)}),[C,I,v]),tabValues:v}}var g=t(92303);const f={tabList:"tabList__CuJ",tabItem:"tabItem_LNqP"};var b=t(74848);function x(e){var n=e.className,t=e.block,r=e.selectedValue,l=e.selectValue,o=e.tabValues,s=[],c=(0,a.a_)().blockElementScrollPositionUntilNextRender,u=function(e){var n=e.currentTarget,t=s.indexOf(n),i=o[t].value;i!==r&&(c(n),l(i))},d=function(e){var n,t=null;switch(e.key){case"Enter":u(e);break;case"ArrowRight":var r,i=s.indexOf(e.currentTarget)+1;t=null!=(r=s[i])?r:s[0];break;case"ArrowLeft":var a,l=s.indexOf(e.currentTarget)-1;t=null!=(a=s[l])?a:s[s.length-1]}null==(n=t)||n.focus()};return(0,b.jsx)("ul",{role:"tablist","aria-orientation":"horizontal",className:(0,i.A)("tabs",{"tabs--block":t},n),children:o.map((function(e){var n=e.value,t=e.label,a=e.attributes;return(0,b.jsx)("li",Object.assign({role:"tab",tabIndex:r===n?0:-1,"aria-selected":r===n,ref:function(e){return s.push(e)},onKeyDown:d,onClick:u},a,{className:(0,i.A)("tabs__item",f.tabItem,null==a?void 0:a.className,{"tabs__item--active":r===n}),children:null!=t?t:n}),n)}))})}function j(e){var n=e.lazy,t=e.children,i=e.selectedValue,a=(Array.isArray(t)?t:[t]).filter(Boolean);if(n){var l=a.find((function(e){return e.props.value===i}));return l?(0,r.cloneElement)(l,{className:"margin-top--md"}):null}return(0,b.jsx)("div",{className:"margin-top--md",children:a.map((function(e,n){return(0,r.cloneElement)(e,{key:n,hidden:e.props.value!==i})}))})}function C(e){var n=v(e);return(0,b.jsxs)("div",{className:(0,i.A)("tabs-container",f.tabList),children:[(0,b.jsx)(x,Object.assign({},n,e)),(0,b.jsx)(j,Object.assign({},n,e))]})}function y(e){var n=(0,g.A)();return(0,b.jsx)(C,Object.assign({},e,{children:d(e.children)}),String(n))}},28453:(e,n,t)=>{t.d(n,{R:()=>l,x:()=>o});var r=t(96540);const i={},a=r.createContext(i);function l(e){const n=r.useContext(a);return r.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function o(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(i):e.components||i:l(e.components),r.createElement(a.Provider,{value:n},e.children)}}}]);