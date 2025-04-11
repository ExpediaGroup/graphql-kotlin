"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[7094],{17140:(e,n,a)=>{a.r(n),a.d(n,{assets:()=>d,contentTitle:()=>s,default:()=>p,frontMatter:()=>l,metadata:()=>c,toc:()=>u});var r=a(74848),t=a(28453),i=a(11470),o=a(19365);const l={id:"apollo-federation",title:"Apollo Federation"},s=void 0,c={id:"schema-generator/federation/apollo-federation",title:"Apollo Federation",description:"In many cases, exposing single GraphQL API that exposes unified view of all the available data provides tremendous value",source:"@site/versioned_docs/version-4.x.x/schema-generator/federation/apollo-federation.mdx",sourceDirName:"schema-generator/federation",slug:"/schema-generator/federation/apollo-federation",permalink:"/graphql-kotlin/docs/4.x.x/schema-generator/federation/apollo-federation",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-4.x.x/schema-generator/federation/apollo-federation.mdx",tags:[],version:"4.x.x",lastUpdatedBy:"Samuel Vazquez",lastUpdatedAt:1744404742e3,frontMatter:{id:"apollo-federation",title:"Apollo Federation"},sidebar:"docs",previous:{title:"Introspection",permalink:"/graphql-kotlin/docs/4.x.x/schema-generator/execution/introspection"},next:{title:"Federated Schemas",permalink:"/graphql-kotlin/docs/4.x.x/schema-generator/federation/federated-schemas"}},d={},u=[{value:"Install",id:"install",level:2},{value:"Usage",id:"usage",level:2},{value:"<code>toFederatedSchema</code>",id:"tofederatedschema",level:3},{value:"Example",id:"example",level:2}];function h(e){const n={a:"a",code:"code",h2:"h2",h3:"h3",p:"p",pre:"pre",...(0,t.R)(),...e.components};return(0,r.jsxs)(r.Fragment,{children:[(0,r.jsx)(n.p,{children:"In many cases, exposing single GraphQL API that exposes unified view of all the available data provides tremendous value\nto their clients. As the underlying graph scales, managing single monolithic GraphQL server might become less and less\nfeasible making it much harder to manage and leading to unnecessary bottlenecks. Migrating towards federated model with\nan API gateway and a number of smaller GraphQL services behind it alleviates some of those problems and allows teams to\nscale their graphs more easily."}),"\n",(0,r.jsxs)(n.p,{children:[(0,r.jsx)(n.a,{href:"https://www.apollographql.com/docs/apollo-server/federation/introduction/",children:"Apollo Federation"})," is an architecture for\ncomposing multiple GraphQL services into a single graph. Federated schemas rely on a number of custom directives to\ninstrument the behavior of the underlying graph and convey the relationships between different schema types. Each individual\nGraphQL server generates a valid GraphQL schema and can be run independently. This is in contrast with a traditional schema\nstitching approach where relationships between individual services, i.e. linking configuration, is configured at the GraphQL\ngateway level."]}),"\n",(0,r.jsx)(n.h2,{id:"install",children:"Install"}),"\n",(0,r.jsxs)(n.p,{children:["Using a JVM dependency manager, link ",(0,r.jsx)(n.code,{children:"graphql-kotlin-federation"})," to your project."]}),"\n",(0,r.jsxs)(i.A,{defaultValue:"gradle",values:[{label:"Gradle Kotlin",value:"gradle"},{label:"Maven",value:"maven"}],children:[(0,r.jsx)(o.A,{value:"gradle",children:(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-kotlin",children:'implementation("com.expediagroup", "graphql-kotlin-federation", latestVersion)\n'})})}),(0,r.jsx)(o.A,{value:"maven",children:(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-xml",children:"<dependency>\n  <groupId>com.expediagroup</groupId>\n  <artifactId>graphql-kotlin-federation</artifactId>\n  <version>${latestVersion}</version>\n</dependency>\n"})})})]}),"\n",(0,r.jsx)(n.h2,{id:"usage",children:"Usage"}),"\n",(0,r.jsxs)(n.p,{children:[(0,r.jsx)(n.code,{children:"graphql-kotlin-federation"})," build on top of ",(0,r.jsx)(n.code,{children:"graphql-kotlin-schema-generator"})," and adds a few extra methods and class to use to generate federation\ncompliant schemas."]}),"\n",(0,r.jsx)(n.h3,{id:"tofederatedschema",children:(0,r.jsx)(n.code,{children:"toFederatedSchema"})}),"\n",(0,r.jsxs)(n.p,{children:["Just like the basic ",(0,r.jsx)(n.a,{href:"/graphql-kotlin/docs/4.x.x/schema-generator/schema-generator-getting-started",children:"toSchema"}),", ",(0,r.jsx)(n.code,{children:"toFederatedSchema"})," accepts four parameters: ",(0,r.jsx)(n.code,{children:"config"}),", ",(0,r.jsx)(n.code,{children:"queries"}),", ",(0,r.jsx)(n.code,{children:"mutations"})," and ",(0,r.jsx)(n.code,{children:"subscriptions"}),".\nThe difference is that the ",(0,r.jsx)(n.code,{children:"config"})," class is of type ",(0,r.jsx)(n.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/master/generator/graphql-kotlin-federation/src/main/kotlin/com/expediagroup/graphql/generator/federation/FederatedSchemaGeneratorConfig.kt",children:"FederatedSchemaGeneratorConfig"}),".\nThis class extends the ",(0,r.jsx)(n.a,{href:"/graphql-kotlin/docs/4.x.x/schema-generator/customizing-schemas/generator-config",children:"base configuration class"})," and adds some default logic. You can override the logic if needed, but do so with caution as you may no longer generate a spec compliant schema."]}),"\n",(0,r.jsxs)(n.p,{children:["You can see the definition for ",(0,r.jsx)(n.code,{children:"toFederatedSchema"})," ",(0,r.jsx)(n.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/master/generator/graphql-kotlin-federation/src/main/kotlin/com/expediagroup/graphql/generator/federation/toFederatedSchema.kt",children:"in the\nsource"})]}),"\n",(0,r.jsx)(n.h2,{id:"example",children:"Example"}),"\n",(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-kotlin",children:'@KeyDirective(fields = FieldSet("id"))\ndata class User(\n  val id: ID,\n  val name: String\n)\n\nclass Query {\n  fun getUsers(): List<User> = getUsersFromDB()\n}\n\nval config = FederatedSchemaGeneratorConfig(\n  supportedPackages = "com.example"\n)\n\ntoFederatedSchema(\n  config = config,\n  queries = listOf(TopLevelObject(Query()))\n)\n'})}),"\n",(0,r.jsx)(n.p,{children:"will generate"}),"\n",(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-graphql",children:'# Federation spec types\nscalar _Any\nscalar _FieldSet\n\nunion _Entity\n\ntype _Service {\n   sdl: String!\n}\n\ndirective @external on FIELD_DEFINITION\ndirective @requires(fields: _FieldSet) on FIELD_DEFINITION\ndirective @provides(fields: _FieldSet) on FIELD_DEFINITION\ndirective @key(fields: _FieldSet) on OBJECT | INTERFACE\ndirective @extends on OBJECT | INTERFACE\n\n# Schema types\ntype Query @extends {\n   getUsers: [User!]!\n\n   _entities(representations: [_Any!]!): [_Entity]!\n   _service: _Service!\n}\n\ntype User @key(fields : "id") {\n   id: ID!\n   name: String!\n}\n'})})]})}function p(e={}){const{wrapper:n}={...(0,t.R)(),...e.components};return n?(0,r.jsx)(n,{...e,children:(0,r.jsx)(h,{...e})}):h(e)}},19365:(e,n,a)=>{a.d(n,{A:()=>o});a(96540);var r=a(34164);const t={tabItem:"tabItem_Ymn6"};var i=a(74848);function o(e){var n=e.children,a=e.hidden,o=e.className;return(0,i.jsx)("div",{role:"tabpanel",className:(0,r.A)(t.tabItem,o),hidden:a,children:n})}},11470:(e,n,a)=>{a.d(n,{A:()=>k});var r=a(96540),t=a(34164),i=a(23104),o=a(56347),l=a(205),s=a(57485),c=a(31682),d=a(70679);function u(e){var n,a;return null!=(n=null==(a=r.Children.toArray(e).filter((function(e){return"\n"!==e})).map((function(e){if(!e||(0,r.isValidElement)(e)&&((n=e.props)&&"object"==typeof n&&"value"in n))return e;var n;throw new Error("Docusaurus error: Bad <Tabs> child <"+("string"==typeof e.type?e.type:e.type.name)+'>: all children of the <Tabs> component should be <TabItem>, and every <TabItem> should have a unique "value" prop.')})))?void 0:a.filter(Boolean))?n:[]}function h(e){var n=e.values,a=e.children;return(0,r.useMemo)((function(){var e=null!=n?n:function(e){return u(e).map((function(e){var n=e.props;return{value:n.value,label:n.label,attributes:n.attributes,default:n.default}}))}(a);return function(e){var n=(0,c.XI)(e,(function(e,n){return e.value===n.value}));if(n.length>0)throw new Error('Docusaurus error: Duplicate values "'+n.map((function(e){return e.value})).join(", ")+'" found in <Tabs>. Every value needs to be unique.')}(e),e}),[n,a])}function p(e){var n=e.value;return e.tabValues.some((function(e){return e.value===n}))}function f(e){var n=e.queryString,a=void 0!==n&&n,t=e.groupId,i=(0,o.W6)(),l=function(e){var n=e.queryString,a=void 0!==n&&n,r=e.groupId;if("string"==typeof a)return a;if(!1===a)return null;if(!0===a&&!r)throw new Error('Docusaurus error: The <Tabs> component groupId prop is required if queryString=true, because this value is used as the search param name. You can also provide an explicit value such as queryString="my-search-param".');return null!=r?r:null}({queryString:a,groupId:t});return[(0,s.aZ)(l),(0,r.useCallback)((function(e){if(l){var n=new URLSearchParams(i.location.search);n.set(l,e),i.replace(Object.assign({},i.location,{search:n.toString()}))}}),[l,i])]}function m(e){var n,a,t,i,o=e.defaultValue,s=e.queryString,c=void 0!==s&&s,u=e.groupId,m=h(e),g=(0,r.useState)((function(){return function(e){var n,a=e.defaultValue,r=e.tabValues;if(0===r.length)throw new Error("Docusaurus error: the <Tabs> component requires at least one <TabItem> children component");if(a){if(!p({value:a,tabValues:r}))throw new Error('Docusaurus error: The <Tabs> has a defaultValue "'+a+'" but none of its children has the corresponding value. Available values are: '+r.map((function(e){return e.value})).join(", ")+". If you intend to show no default tab, use defaultValue={null} instead.");return a}var t=null!=(n=r.find((function(e){return e.default})))?n:r[0];if(!t)throw new Error("Unexpected error: 0 tabValues");return t.value}({defaultValue:o,tabValues:m})})),v=g[0],x=g[1],b=f({queryString:c,groupId:u}),y=b[0],j=b[1],k=(n=function(e){return e?"docusaurus.tab."+e:null}({groupId:u}.groupId),a=(0,d.Dv)(n),t=a[0],i=a[1],[t,(0,r.useCallback)((function(e){n&&i.set(e)}),[n,i])]),I=k[0],w=k[1],q=function(){var e=null!=y?y:I;return p({value:e,tabValues:m})?e:null}();return(0,l.A)((function(){q&&x(q)}),[q]),{selectedValue:v,selectValue:(0,r.useCallback)((function(e){if(!p({value:e,tabValues:m}))throw new Error("Can't select invalid tab value="+e);x(e),j(e),w(e)}),[j,w,m]),tabValues:m}}var g=a(92303);const v={tabList:"tabList__CuJ",tabItem:"tabItem_LNqP"};var x=a(74848);function b(e){var n=e.className,a=e.block,r=e.selectedValue,o=e.selectValue,l=e.tabValues,s=[],c=(0,i.a_)().blockElementScrollPositionUntilNextRender,d=function(e){var n=e.currentTarget,a=s.indexOf(n),t=l[a].value;t!==r&&(c(n),o(t))},u=function(e){var n,a=null;switch(e.key){case"Enter":d(e);break;case"ArrowRight":var r,t=s.indexOf(e.currentTarget)+1;a=null!=(r=s[t])?r:s[0];break;case"ArrowLeft":var i,o=s.indexOf(e.currentTarget)-1;a=null!=(i=s[o])?i:s[s.length-1]}null==(n=a)||n.focus()};return(0,x.jsx)("ul",{role:"tablist","aria-orientation":"horizontal",className:(0,t.A)("tabs",{"tabs--block":a},n),children:l.map((function(e){var n=e.value,a=e.label,i=e.attributes;return(0,x.jsx)("li",Object.assign({role:"tab",tabIndex:r===n?0:-1,"aria-selected":r===n,ref:function(e){return s.push(e)},onKeyDown:u,onClick:d},i,{className:(0,t.A)("tabs__item",v.tabItem,null==i?void 0:i.className,{"tabs__item--active":r===n}),children:null!=a?a:n}),n)}))})}function y(e){var n=e.lazy,a=e.children,i=e.selectedValue,o=(Array.isArray(a)?a:[a]).filter(Boolean);if(n){var l=o.find((function(e){return e.props.value===i}));return l?(0,r.cloneElement)(l,{className:(0,t.A)("margin-top--md",l.props.className)}):null}return(0,x.jsx)("div",{className:"margin-top--md",children:o.map((function(e,n){return(0,r.cloneElement)(e,{key:n,hidden:e.props.value!==i})}))})}function j(e){var n=m(e);return(0,x.jsxs)("div",{className:(0,t.A)("tabs-container",v.tabList),children:[(0,x.jsx)(b,Object.assign({},n,e)),(0,x.jsx)(y,Object.assign({},n,e))]})}function k(e){var n=(0,g.A)();return(0,x.jsx)(j,Object.assign({},e,{children:u(e.children)}),String(n))}},28453:(e,n,a)=>{a.d(n,{R:()=>o,x:()=>l});var r=a(96540);const t={},i=r.createContext(t);function o(e){const n=r.useContext(i);return r.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function l(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(t):e.components||t:o(e.components),r.createElement(i.Provider,{value:n},e.children)}}}]);