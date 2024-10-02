"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[1717],{69611:(e,n,a)=>{a.r(n),a.d(n,{assets:()=>c,contentTitle:()=>s,default:()=>p,frontMatter:()=>l,metadata:()=>d,toc:()=>u});var r=a(74848),t=a(28453),o=a(11470),i=a(19365);const l={id:"apollo-federation",title:"Apollo Federation"},s=void 0,d={id:"schema-generator/federation/apollo-federation",title:"Apollo Federation",description:"In many cases, exposing single GraphQL API that exposes unified view of all the available data provides tremendous value",source:"@site/versioned_docs/version-7.x.x/schema-generator/federation/apollo-federation.mdx",sourceDirName:"schema-generator/federation",slug:"/schema-generator/federation/apollo-federation",permalink:"/graphql-kotlin/docs/7.x.x/schema-generator/federation/apollo-federation",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-7.x.x/schema-generator/federation/apollo-federation.mdx",tags:[],version:"7.x.x",lastUpdatedBy:"Samuel Vazquez",lastUpdatedAt:172791071e4,frontMatter:{id:"apollo-federation",title:"Apollo Federation"},sidebar:"docs",previous:{title:"Introspection",permalink:"/graphql-kotlin/docs/7.x.x/schema-generator/execution/introspection"},next:{title:"Federated Schemas",permalink:"/graphql-kotlin/docs/7.x.x/schema-generator/federation/federated-schemas"}},c={},u=[{value:"Federation v1 vs Federation v2",id:"federation-v1-vs-federation-v2",level:2},{value:"Install",id:"install",level:2},{value:"Usage",id:"usage",level:2},{value:"<code>toFederatedSchema</code>",id:"tofederatedschema",level:3},{value:"Example",id:"example",level:3}];function h(e){const n={a:"a",admonition:"admonition",code:"code",h2:"h2",h3:"h3",p:"p",pre:"pre",...(0,t.R)(),...e.components};return(0,r.jsxs)(r.Fragment,{children:[(0,r.jsx)(n.p,{children:"In many cases, exposing single GraphQL API that exposes unified view of all the available data provides tremendous value\nto their clients. As the underlying graph scales, managing single monolithic GraphQL server might become less and less\nfeasible making it much harder to manage and leading to unnecessary bottlenecks. Migrating towards federated model with\nan API gateway and a number of smaller GraphQL services behind it alleviates some of those problems and allows teams to\nscale their graphs more easily."}),"\n",(0,r.jsxs)(n.p,{children:[(0,r.jsx)(n.a,{href:"https://www.apollographql.com/docs/federation/",children:"Apollo Federation"})," is an architecture for composing multiple GraphQL\nservices into a single graph. Federated schemas rely on a number of custom directives to instrument the behavior of the\nunderlying graph and convey the relationships between different schema types. Each individual GraphQL server generates a\nvalid GraphQL schema and can be run independently. This is in contrast with a traditional schema stitching approach where\nrelationships between individual services, i.e. linking configuration, is configured at the GraphQL gateway level."]}),"\n",(0,r.jsx)(n.h2,{id:"federation-v1-vs-federation-v2",children:"Federation v1 vs Federation v2"}),"\n",(0,r.jsxs)(n.p,{children:["Federation v2 is an evolution of the Federation spec to make it more powerful, flexible and easier to adapt. While v1 and\nv2 schemas are similar in many ways, Federation v2 relaxes some of the constraints and adds additional capabilities. See\n",(0,r.jsx)(n.a,{href:"https://www.apollographql.com/docs/federation/federation-2/new-in-federation-2/",children:"Apollo documentation"})," for details."]}),"\n",(0,r.jsxs)(n.p,{children:["By default, ",(0,r.jsx)(n.code,{children:"graphql-kotlin-federation"})," library will generate Federation v2 compatible schema. In order to generate v1\ncompatible schema you have to explicitly opt-out by specifying ",(0,r.jsx)(n.code,{children:"optInFederationV2 = false"})," on your instance of\n",(0,r.jsx)(n.code,{children:"FederatedSchemaGeneratorHooks"}),"."]}),"\n",(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-kotlin",children:'val myHooks = FederatedSchemaGeneratorHooks(resolvers = myFederatedResolvers)\nval myConfig = FederatedSchemaGeneratorConfig(\n  supportedPackages = "com.example",\n  hooks = myHooks\n)\n\ntoFederatedSchema(\n  config = myConfig,\n  queries = listOf(TopLevelObject(MyQuery()))\n)\n'})}),"\n",(0,r.jsx)(n.admonition,{type:"note",children:(0,r.jsxs)(n.p,{children:["When generating federated schemas, ",(0,r.jsx)(n.code,{children:"graphql-kotlin-spring-server"})," defaults to Federation v2. If you want to generate Federation\nv1 schema, you have to explicitly opt-out by configuring ",(0,r.jsx)(n.code,{children:"graphql.federation.optInV2 = false"})," property."]})}),"\n",(0,r.jsx)(n.h2,{id:"install",children:"Install"}),"\n",(0,r.jsxs)(n.p,{children:["Using a JVM dependency manager, link ",(0,r.jsx)(n.code,{children:"graphql-kotlin-federation"})," to your project."]}),"\n",(0,r.jsxs)(o.A,{defaultValue:"gradle",values:[{label:"Gradle Kotlin",value:"gradle"},{label:"Maven",value:"maven"}],children:[(0,r.jsx)(i.A,{value:"gradle",children:(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-kotlin",children:'implementation("com.expediagroup", "graphql-kotlin-federation", latestVersion)\n'})})}),(0,r.jsx)(i.A,{value:"maven",children:(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-xml",children:"<dependency>\n  <groupId>com.expediagroup</groupId>\n  <artifactId>graphql-kotlin-federation</artifactId>\n  <version>${latestVersion}</version>\n</dependency>\n"})})})]}),"\n",(0,r.jsx)(n.h2,{id:"usage",children:"Usage"}),"\n",(0,r.jsxs)(n.p,{children:[(0,r.jsx)(n.code,{children:"graphql-kotlin-federation"})," is build on top of ",(0,r.jsx)(n.code,{children:"graphql-kotlin-schema-generator"})," and adds a few extra methods and class to use to generate federation compliant schemas."]}),"\n",(0,r.jsx)(n.h3,{id:"tofederatedschema",children:(0,r.jsx)(n.code,{children:"toFederatedSchema"})}),"\n",(0,r.jsxs)(n.p,{children:["Just like the basic ",(0,r.jsx)(n.a,{href:"/graphql-kotlin/docs/7.x.x/schema-generator/schema-generator-getting-started",children:"toSchema"}),", ",(0,r.jsx)(n.code,{children:"toFederatedSchema"})," accepts five parameters: ",(0,r.jsx)(n.code,{children:"config"}),", ",(0,r.jsx)(n.code,{children:"queries"}),", ",(0,r.jsx)(n.code,{children:"mutations"}),", ",(0,r.jsx)(n.code,{children:"subscriptions"})," and ",(0,r.jsx)(n.code,{children:"schemaObject"}),".\nThe difference is that the ",(0,r.jsx)(n.code,{children:"config"})," class is of type ",(0,r.jsx)(n.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/master/generator/graphql-kotlin-federation/src/main/kotlin/com/expediagroup/graphql/generator/federation/FederatedSchemaGeneratorConfig.kt",children:"FederatedSchemaGeneratorConfig"}),".\nThis class extends the ",(0,r.jsx)(n.a,{href:"/graphql-kotlin/docs/7.x.x/schema-generator/customizing-schemas/generator-config",children:"base configuration class"})," and adds some default logic. You can override the logic if needed, but do so with caution as you may no longer generate a spec compliant schema."]}),"\n",(0,r.jsxs)(n.p,{children:["You can see the definition for ",(0,r.jsx)(n.code,{children:"toFederatedSchema"})," ",(0,r.jsx)(n.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/master/generator/graphql-kotlin-federation/src/main/kotlin/com/expediagroup/graphql/generator/federation/toFederatedSchema.kt",children:"in the\nsource"}),"."]}),"\n",(0,r.jsx)(n.h3,{id:"example",children:"Example"}),"\n",(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-kotlin",children:'@KeyDirective(fields = FieldSet("id"))\ndata class User(\n  val id: ID,\n  val name: String\n)\n\nclass Query {\n  fun getUsers(): List<User> = getUsersFromDB()\n}\n\nval config = FederatedSchemaGeneratorConfig(\n  supportedPackages = "com.example",\n  hooks = FederatedSchemaGeneratorHooks(emptyList())\n)\n\ntoFederatedSchema(\n  config = config,\n  queries = listOf(TopLevelObject(Query()))\n)\n'})}),"\n",(0,r.jsx)(n.p,{children:"will generate"}),"\n",(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-graphql",children:'schema @link(import : ["@key", "FieldSet"], url : "https://specs.apollo.dev/federation/v2.6"){\n  query: Query\n}\n\ndirective @key(fields: FieldSet!, resolvable: Boolean = true) repeatable on OBJECT | INTERFACE\ndirective @link(import: [String], url: String!) repeatable on SCHEMA\n\ntype Query {\n   getUsers: [User!]!\n\n   _entities(representations: [_Any!]!): [_Entity]!\n   _service: _Service!\n}\n\ntype User @key(fields : "id", resolvable : true) {\n   id: ID!\n   name: String!\n}\n\nunion _Entity = User\n\ntype _Service {\n   sdl: String!\n}\n\nscalar FieldSet\nscalar _Any\n'})})]})}function p(e={}){const{wrapper:n}={...(0,t.R)(),...e.components};return n?(0,r.jsx)(n,{...e,children:(0,r.jsx)(h,{...e})}):h(e)}},19365:(e,n,a)=>{a.d(n,{A:()=>i});a(96540);var r=a(34164);const t={tabItem:"tabItem_Ymn6"};var o=a(74848);function i(e){var n=e.children,a=e.hidden,i=e.className;return(0,o.jsx)("div",{role:"tabpanel",className:(0,r.A)(t.tabItem,i),hidden:a,children:n})}},11470:(e,n,a)=>{a.d(n,{A:()=>k});var r=a(96540),t=a(34164),o=a(23104),i=a(56347),l=a(205),s=a(57485),d=a(31682),c=a(70679);function u(e){var n,a;return null!=(n=null==(a=r.Children.toArray(e).filter((function(e){return"\n"!==e})).map((function(e){if(!e||(0,r.isValidElement)(e)&&((n=e.props)&&"object"==typeof n&&"value"in n))return e;var n;throw new Error("Docusaurus error: Bad <Tabs> child <"+("string"==typeof e.type?e.type:e.type.name)+'>: all children of the <Tabs> component should be <TabItem>, and every <TabItem> should have a unique "value" prop.')})))?void 0:a.filter(Boolean))?n:[]}function h(e){var n=e.values,a=e.children;return(0,r.useMemo)((function(){var e=null!=n?n:function(e){return u(e).map((function(e){var n=e.props;return{value:n.value,label:n.label,attributes:n.attributes,default:n.default}}))}(a);return function(e){var n=(0,d.XI)(e,(function(e,n){return e.value===n.value}));if(n.length>0)throw new Error('Docusaurus error: Duplicate values "'+n.map((function(e){return e.value})).join(", ")+'" found in <Tabs>. Every value needs to be unique.')}(e),e}),[n,a])}function p(e){var n=e.value;return e.tabValues.some((function(e){return e.value===n}))}function f(e){var n=e.queryString,a=void 0!==n&&n,t=e.groupId,o=(0,i.W6)(),l=function(e){var n=e.queryString,a=void 0!==n&&n,r=e.groupId;if("string"==typeof a)return a;if(!1===a)return null;if(!0===a&&!r)throw new Error('Docusaurus error: The <Tabs> component groupId prop is required if queryString=true, because this value is used as the search param name. You can also provide an explicit value such as queryString="my-search-param".');return null!=r?r:null}({queryString:a,groupId:t});return[(0,s.aZ)(l),(0,r.useCallback)((function(e){if(l){var n=new URLSearchParams(o.location.search);n.set(l,e),o.replace(Object.assign({},o.location,{search:n.toString()}))}}),[l,o])]}function m(e){var n,a,t,o,i=e.defaultValue,s=e.queryString,d=void 0!==s&&s,u=e.groupId,m=h(e),g=(0,r.useState)((function(){return function(e){var n,a=e.defaultValue,r=e.tabValues;if(0===r.length)throw new Error("Docusaurus error: the <Tabs> component requires at least one <TabItem> children component");if(a){if(!p({value:a,tabValues:r}))throw new Error('Docusaurus error: The <Tabs> has a defaultValue "'+a+'" but none of its children has the corresponding value. Available values are: '+r.map((function(e){return e.value})).join(", ")+". If you intend to show no default tab, use defaultValue={null} instead.");return a}var t=null!=(n=r.find((function(e){return e.default})))?n:r[0];if(!t)throw new Error("Unexpected error: 0 tabValues");return t.value}({defaultValue:i,tabValues:m})})),v=g[0],x=g[1],b=f({queryString:d,groupId:u}),y=b[0],j=b[1],k=(n=function(e){return e?"docusaurus.tab."+e:null}({groupId:u}.groupId),a=(0,c.Dv)(n),t=a[0],o=a[1],[t,(0,r.useCallback)((function(e){n&&o.set(e)}),[n,o])]),w=k[0],q=k[1],S=function(){var e=null!=y?y:w;return p({value:e,tabValues:m})?e:null}();return(0,l.A)((function(){S&&x(S)}),[S]),{selectedValue:v,selectValue:(0,r.useCallback)((function(e){if(!p({value:e,tabValues:m}))throw new Error("Can't select invalid tab value="+e);x(e),j(e),q(e)}),[j,q,m]),tabValues:m}}var g=a(92303);const v={tabList:"tabList__CuJ",tabItem:"tabItem_LNqP"};var x=a(74848);function b(e){var n=e.className,a=e.block,r=e.selectedValue,i=e.selectValue,l=e.tabValues,s=[],d=(0,o.a_)().blockElementScrollPositionUntilNextRender,c=function(e){var n=e.currentTarget,a=s.indexOf(n),t=l[a].value;t!==r&&(d(n),i(t))},u=function(e){var n,a=null;switch(e.key){case"Enter":c(e);break;case"ArrowRight":var r,t=s.indexOf(e.currentTarget)+1;a=null!=(r=s[t])?r:s[0];break;case"ArrowLeft":var o,i=s.indexOf(e.currentTarget)-1;a=null!=(o=s[i])?o:s[s.length-1]}null==(n=a)||n.focus()};return(0,x.jsx)("ul",{role:"tablist","aria-orientation":"horizontal",className:(0,t.A)("tabs",{"tabs--block":a},n),children:l.map((function(e){var n=e.value,a=e.label,o=e.attributes;return(0,x.jsx)("li",Object.assign({role:"tab",tabIndex:r===n?0:-1,"aria-selected":r===n,ref:function(e){return s.push(e)},onKeyDown:u,onClick:c},o,{className:(0,t.A)("tabs__item",v.tabItem,null==o?void 0:o.className,{"tabs__item--active":r===n}),children:null!=a?a:n}),n)}))})}function y(e){var n=e.lazy,a=e.children,o=e.selectedValue,i=(Array.isArray(a)?a:[a]).filter(Boolean);if(n){var l=i.find((function(e){return e.props.value===o}));return l?(0,r.cloneElement)(l,{className:(0,t.A)("margin-top--md",l.props.className)}):null}return(0,x.jsx)("div",{className:"margin-top--md",children:i.map((function(e,n){return(0,r.cloneElement)(e,{key:n,hidden:e.props.value!==o})}))})}function j(e){var n=m(e);return(0,x.jsxs)("div",{className:(0,t.A)("tabs-container",v.tabList),children:[(0,x.jsx)(b,Object.assign({},n,e)),(0,x.jsx)(y,Object.assign({},n,e))]})}function k(e){var n=(0,g.A)();return(0,x.jsx)(j,Object.assign({},e,{children:u(e.children)}),String(n))}},28453:(e,n,a)=>{a.d(n,{R:()=>i,x:()=>l});var r=a(96540);const t={},o=r.createContext(t);function i(e){const n=r.useContext(o);return r.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function l(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(t):e.components||t:i(e.components),r.createElement(o.Provider,{value:n},e.children)}}}]);