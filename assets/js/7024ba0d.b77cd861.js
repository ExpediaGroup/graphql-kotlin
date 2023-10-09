"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[8e3],{3905:(e,t,n)=>{n.d(t,{Zo:()=>c,kt:()=>f});var r=n(67294);function a(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function i(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function l(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?i(Object(n),!0).forEach((function(t){a(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):i(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function o(e,t){if(null==e)return{};var n,r,a=function(e,t){if(null==e)return{};var n,r,a={},i=Object.keys(e);for(r=0;r<i.length;r++)n=i[r],t.indexOf(n)>=0||(a[n]=e[n]);return a}(e,t);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(r=0;r<i.length;r++)n=i[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(a[n]=e[n])}return a}var u=r.createContext({}),s=function(e){var t=r.useContext(u),n=t;return e&&(n="function"==typeof e?e(t):l(l({},t),e)),n},c=function(e){var t=s(e.components);return r.createElement(u.Provider,{value:t},e.children)},p="mdxType",d={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},m=r.forwardRef((function(e,t){var n=e.components,a=e.mdxType,i=e.originalType,u=e.parentName,c=o(e,["components","mdxType","originalType","parentName"]),p=s(n),m=a,f=p["".concat(u,".").concat(m)]||p[m]||d[m]||i;return n?r.createElement(f,l(l({ref:t},c),{},{components:n})):r.createElement(f,l({ref:t},c))}));function f(e,t){var n=arguments,a=t&&t.mdxType;if("string"==typeof e||a){var i=n.length,l=new Array(i);l[0]=m;var o={};for(var u in t)hasOwnProperty.call(t,u)&&(o[u]=t[u]);o.originalType=e,o[p]="string"==typeof e?e:a,l[1]=o;for(var s=2;s<i;s++)l[s]=n[s];return r.createElement.apply(null,l)}return r.createElement.apply(null,n)}m.displayName="MDXCreateElement"},85162:(e,t,n)=>{n.d(t,{Z:()=>l});var r=n(67294),a=n(86010);const i={tabItem:"tabItem_Ymn6"};function l(e){var t=e.children,n=e.hidden,l=e.className;return r.createElement("div",{role:"tabpanel",className:(0,a.Z)(i.tabItem,l),hidden:n},t)}},74866:(e,t,n)=>{n.d(t,{Z:()=>k});var r=n(87462),a=n(67294),i=n(86010),l=n(12466),o=n(16550),u=n(91980),s=n(67392),c=n(50012);function p(e){return function(e){var t,n;return null!=(t=null==(n=a.Children.map(e,(function(e){if(!e||(0,a.isValidElement)(e)&&(t=e.props)&&"object"==typeof t&&"value"in t)return e;var t;throw new Error("Docusaurus error: Bad <Tabs> child <"+("string"==typeof e.type?e.type:e.type.name)+'>: all children of the <Tabs> component should be <TabItem>, and every <TabItem> should have a unique "value" prop.')})))?void 0:n.filter(Boolean))?t:[]}(e).map((function(e){var t=e.props;return{value:t.value,label:t.label,attributes:t.attributes,default:t.default}}))}function d(e){var t=e.values,n=e.children;return(0,a.useMemo)((function(){var e=null!=t?t:p(n);return function(e){var t=(0,s.l)(e,(function(e,t){return e.value===t.value}));if(t.length>0)throw new Error('Docusaurus error: Duplicate values "'+t.map((function(e){return e.value})).join(", ")+'" found in <Tabs>. Every value needs to be unique.')}(e),e}),[t,n])}function m(e){var t=e.value;return e.tabValues.some((function(e){return e.value===t}))}function f(e){var t=e.queryString,n=void 0!==t&&t,r=e.groupId,i=(0,o.k6)(),l=function(e){var t=e.queryString,n=void 0!==t&&t,r=e.groupId;if("string"==typeof n)return n;if(!1===n)return null;if(!0===n&&!r)throw new Error('Docusaurus error: The <Tabs> component groupId prop is required if queryString=true, because this value is used as the search param name. You can also provide an explicit value such as queryString="my-search-param".');return null!=r?r:null}({queryString:n,groupId:r});return[(0,u._X)(l),(0,a.useCallback)((function(e){if(l){var t=new URLSearchParams(i.location.search);t.set(l,e),i.replace(Object.assign({},i.location,{search:t.toString()}))}}),[l,i])]}function h(e){var t,n,r,i,l=e.defaultValue,o=e.queryString,u=void 0!==o&&o,s=e.groupId,p=d(e),h=(0,a.useState)((function(){return function(e){var t,n=e.defaultValue,r=e.tabValues;if(0===r.length)throw new Error("Docusaurus error: the <Tabs> component requires at least one <TabItem> children component");if(n){if(!m({value:n,tabValues:r}))throw new Error('Docusaurus error: The <Tabs> has a defaultValue "'+n+'" but none of its children has the corresponding value. Available values are: '+r.map((function(e){return e.value})).join(", ")+". If you intend to show no default tab, use defaultValue={null} instead.");return n}var a=null!=(t=r.find((function(e){return e.default})))?t:r[0];if(!a)throw new Error("Unexpected error: 0 tabValues");return a.value}({defaultValue:l,tabValues:p})})),b=h[0],v=h[1],g=f({queryString:u,groupId:s}),C=g[0],y=g[1],k=(t=function(e){return e?"docusaurus.tab."+e:null}({groupId:s}.groupId),n=(0,c.Nk)(t),r=n[0],i=n[1],[r,(0,a.useCallback)((function(e){t&&i.set(e)}),[t,i])]),w=k[0],N=k[1],T=function(){var e=null!=C?C:w;return m({value:e,tabValues:p})?e:null}();return(0,a.useLayoutEffect)((function(){T&&v(T)}),[T]),{selectedValue:b,selectValue:(0,a.useCallback)((function(e){if(!m({value:e,tabValues:p}))throw new Error("Can't select invalid tab value="+e);v(e),y(e),N(e)}),[y,N,p]),tabValues:p}}var b=n(72389);const v={tabList:"tabList__CuJ",tabItem:"tabItem_LNqP"};function g(e){var t=e.className,n=e.block,o=e.selectedValue,u=e.selectValue,s=e.tabValues,c=[],p=(0,l.o5)().blockElementScrollPositionUntilNextRender,d=function(e){var t=e.currentTarget,n=c.indexOf(t),r=s[n].value;r!==o&&(p(t),u(r))},m=function(e){var t,n=null;switch(e.key){case"Enter":d(e);break;case"ArrowRight":var r,a=c.indexOf(e.currentTarget)+1;n=null!=(r=c[a])?r:c[0];break;case"ArrowLeft":var i,l=c.indexOf(e.currentTarget)-1;n=null!=(i=c[l])?i:c[c.length-1]}null==(t=n)||t.focus()};return a.createElement("ul",{role:"tablist","aria-orientation":"horizontal",className:(0,i.Z)("tabs",{"tabs--block":n},t)},s.map((function(e){var t=e.value,n=e.label,l=e.attributes;return a.createElement("li",(0,r.Z)({role:"tab",tabIndex:o===t?0:-1,"aria-selected":o===t,key:t,ref:function(e){return c.push(e)},onKeyDown:m,onClick:d},l,{className:(0,i.Z)("tabs__item",v.tabItem,null==l?void 0:l.className,{"tabs__item--active":o===t})}),null!=n?n:t)})))}function C(e){var t=e.lazy,n=e.children,r=e.selectedValue,i=(Array.isArray(n)?n:[n]).filter(Boolean);if(t){var l=i.find((function(e){return e.props.value===r}));return l?(0,a.cloneElement)(l,{className:"margin-top--md"}):null}return a.createElement("div",{className:"margin-top--md"},i.map((function(e,t){return(0,a.cloneElement)(e,{key:t,hidden:e.props.value!==r})})))}function y(e){var t=h(e);return a.createElement("div",{className:(0,i.Z)("tabs-container",v.tabList)},a.createElement(g,(0,r.Z)({},e,t)),a.createElement(C,(0,r.Z)({},e,t)))}function k(e){var t=(0,b.Z)();return a.createElement(y,(0,r.Z)({key:String(t)},e))}},38769:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>c,contentTitle:()=>u,default:()=>f,frontMatter:()=>o,metadata:()=>s,toc:()=>p});var r=n(87462),a=n(63366),i=(n(67294),n(3905)),l=(n(74866),n(85162),["components"]),o={id:"client-customization",title:"Client Customization"},u=void 0,s={unversionedId:"client/client-customization",id:"client/client-customization",title:"Client Customization",description:"Ktor HTTP Client Customization",source:"@site/docs/client/client-customization.mdx",sourceDirName:"client",slug:"/client/client-customization",permalink:"/graphql-kotlin/docs/client/client-customization",draft:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/docs/client/client-customization.mdx",tags:[],version:"current",lastUpdatedBy:"Adrian Witaszak",lastUpdatedAt:1696885167,formattedLastUpdatedAt:"Oct 9, 2023",frontMatter:{id:"client-customization",title:"Client Customization"},sidebar:"docs",previous:{title:"Client Features",permalink:"/graphql-kotlin/docs/client/client-features"},next:{title:"Client Serialization",permalink:"/graphql-kotlin/docs/client/client-serialization"}},c={},p=[{value:"Ktor HTTP Client Customization",id:"ktor-http-client-customization",level:2},{value:"Global Client Customization",id:"global-client-customization",level:3},{value:"Per Request Customization",id:"per-request-customization",level:3},{value:"Spring WebClient Customization",id:"spring-webclient-customization",level:2},{value:"Global Client Customization",id:"global-client-customization-1",level:3},{value:"Per Request Customization",id:"per-request-customization-1",level:3},{value:"Custom GraphQL Client",id:"custom-graphql-client",level:2},{value:"Deprecated Field Usage",id:"deprecated-field-usage",level:2}],d={toc:p},m="wrapper";function f(e){var t=e.components,n=(0,a.Z)(e,l);return(0,i.kt)(m,(0,r.Z)({},d,n,{components:t,mdxType:"MDXLayout"}),(0,i.kt)("h2",{id:"ktor-http-client-customization"},"Ktor HTTP Client Customization"),(0,i.kt)("p",null,(0,i.kt)("inlineCode",{parentName:"p"},"GraphQLKtorClient")," is a thin wrapper on top of ",(0,i.kt)("a",{parentName:"p",href:"https://ktor.io/docs/client.html"},"Ktor HTTP Client")," and supports fully\nasynchronous non-blocking communication. It is highly customizable and can be configured with any supported Ktor HTTP\n",(0,i.kt)("a",{parentName:"p",href:"https://ktor.io/clients/http-client/engines.html"},"engine")," and ",(0,i.kt)("a",{parentName:"p",href:"https://ktor.io/clients/http-client/features.html"},"features"),"."),(0,i.kt)("p",null,"See ",(0,i.kt)("a",{parentName:"p",href:"https://ktor.io/clients/index.html"},"Ktor HTTP Client documentation")," for additional details."),(0,i.kt)("h3",{id:"global-client-customization"},"Global Client Customization"),(0,i.kt)("p",null,"A single instance of ",(0,i.kt)("inlineCode",{parentName:"p"},"GraphQLKtorClient")," can be used to handle many GraphQL operations. You can specify a custom instance\nof Ktor ",(0,i.kt)("inlineCode",{parentName:"p"},"HttpClient")," and a target ",(0,i.kt)("inlineCode",{parentName:"p"},"GraphQLClientSerializer"),"."),(0,i.kt)("p",null,"The below example configures a new ",(0,i.kt)("inlineCode",{parentName:"p"},"GraphQLKtorClient")," to use the ",(0,i.kt)("inlineCode",{parentName:"p"},"OkHttp")," engine with custom timeouts, adds a default ",(0,i.kt)("inlineCode",{parentName:"p"},"X-MY-API-KEY"),"\nheader to all requests, and enables basic logging of the requests."),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-kotlin"},'val okHttpClient = HttpClient(engineFactory = OkHttp) {\n    engine {\n        config {\n            connectTimeout(10, TimeUnit.SECONDS)\n            readTimeout(60, TimeUnit.SECONDS)\n            writeTimeout(60, TimeUnit.SECONDS)\n        }\n    }\n    defaultRequest {\n        header("X-MY-API-KEY", "someSecretApiKey")\n    }\n    install(Logging) {\n        logger = Logger.DEFAULT\n        level = LogLevel.INFO\n    }\n}\nval client = GraphQLKtorClient(\n    url = URL("http://localhost:8080/graphql"),\n    httpClient = okHttpClient\n)\n')),(0,i.kt)("h3",{id:"per-request-customization"},"Per Request Customization"),(0,i.kt)("p",null,"Individual GraphQL requests can be customized through ",(0,i.kt)("a",{parentName:"p",href:"https://ktor.io/docs/request.html#customizing-requests"},"HttpRequestBuilder"),".\nYou can use this mechanism to specify custom headers, update target url to include custom query parameters, configure\nattributes that can be accessed from the pipeline features as well specify timeouts per request."),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-kotlin"},'val helloWorldQuery = HelloWorldQuery(variables = HelloWorldQuery.Variables(name = "John Doe"))\nval result = client.execute(helloWorldQuery) {\n    header("X-B3-TraceId", "0123456789abcdef")\n}\n')),(0,i.kt)("h2",{id:"spring-webclient-customization"},"Spring WebClient Customization"),(0,i.kt)("p",null,(0,i.kt)("inlineCode",{parentName:"p"},"GraphQLWebClient")," is a thin wrapper on top of ",(0,i.kt)("a",{parentName:"p",href:"https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/reactive/function/client/WebClient.html"},"Spring WebClient"),"\nthat relies on Reactor Netty for fully asynchronous non-blocking communications. If you want to use Jetty instead you will\nneed to exclude provided ",(0,i.kt)("inlineCode",{parentName:"p"},"io.projectreactor.netty:reactor-netty")," dependency and instead add ",(0,i.kt)("inlineCode",{parentName:"p"},"org.eclipse.jetty:jetty-reactive-httpclient"),"\ndependency."),(0,i.kt)("h3",{id:"global-client-customization-1"},"Global Client Customization"),(0,i.kt)("p",null,"A single instance of ",(0,i.kt)("inlineCode",{parentName:"p"},"GraphQLWebClient")," can be used to handle many GraphQL operations and you can customize it by providing\na custom instance of ",(0,i.kt)("inlineCode",{parentName:"p"},"WebClient.Builder"),". See ",(0,i.kt)("a",{parentName:"p",href:"https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-webclient-customization"},"Spring documentation"),"\nfor additional details."),(0,i.kt)("p",null,"Example below configures ",(0,i.kt)("inlineCode",{parentName:"p"},"GraphQLWebClient")," with custom timeouts and adds a default ",(0,i.kt)("inlineCode",{parentName:"p"},"X-MY-API-KEY")," header to all requests."),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-kotlin"},'val httpClient: HttpClient = HttpClient.create()\n    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10_000)\n    .responseTimeout(Duration.ofMillis(10_000))\nval connector: ClientHttpConnector = ReactorClientHttpConnector(httpClient.wiretap(true))\nval webClientBuilder = WebClient.builder()\n    .clientConnector(connector)\n    .defaultHeader("X-MY-API-KEY", "someSecretApiKey")\n\nval client = GraphQLWebClient(\n    url = "http://localhost:8080/graphql",\n    builder = webClientBuilder\n)\n')),(0,i.kt)("h3",{id:"per-request-customization-1"},"Per Request Customization"),(0,i.kt)("p",null,"Individual GraphQL requests can be customized by providing ",(0,i.kt)("inlineCode",{parentName:"p"},"WebClient.RequestBodyUriSpec")," lambda. You can use this mechanism\nto specify custom headers or include custom attributes or query parameters."),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-kotlin"},'val helloWorldQuery = HelloWorldQuery(variables = HelloWorldQuery.Variables(name = "John Doe"))\nval result = client.execute(helloWorldQuery) {\n    header("X-B3-TraceId", "0123456789abcdef")\n}\n')),(0,i.kt)("h2",{id:"custom-graphql-client"},"Custom GraphQL Client"),(0,i.kt)("p",null,"GraphQL Kotlin libraries provide generic a ",(0,i.kt)("inlineCode",{parentName:"p"},"GraphQLClient")," interface as well as Ktor HTTP Client and Spring WebClient based\nreference implementations. Both ",(0,i.kt)("inlineCode",{parentName:"p"},"GraphQLKtorClient")," and ",(0,i.kt)("inlineCode",{parentName:"p"},"GraphQLWebClient")," are open classes which means you can also\nextend them to provide some custom ",(0,i.kt)("inlineCode",{parentName:"p"},"execute")," logic."),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-kotlin"},"class CustomGraphQLClient(url: URL) : GraphQLKtorClient(url = url) {\n\n    override suspend fun <T: Any> execute(request: GraphQLClientRequest<T>, requestCustomizer: HttpRequestBuilder.() -> Unit): GraphQLClientResponse<T> {\n        // custom init logic\n        val result = super.execute(request, requestCustomizer)\n        // custom finalize logic\n        return result\n    }\n}\n")),(0,i.kt)("h2",{id:"deprecated-field-usage"},"Deprecated Field Usage"),(0,i.kt)("p",null,"Build plugins will automatically fail generation of a client if any of the specified query files are referencing\ndeprecated fields. This ensures that your clients have to explicitly opt-in into deprecated usage by specifying\n",(0,i.kt)("inlineCode",{parentName:"p"},"allowDeprecatedFields")," configuration option."))}f.isMDXComponent=!0}}]);