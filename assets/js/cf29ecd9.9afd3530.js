"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[4218],{55025:(e,n,t)=>{t.r(n),t.d(n,{assets:()=>l,contentTitle:()=>r,default:()=>p,frontMatter:()=>o,metadata:()=>a,toc:()=>c});var i=t(74848),s=t(28453);const o={id:"introspection",title:"Introspection",original_id:"introspection"},r=void 0,a={id:"schema-generator/execution/introspection",title:"Introspection",description:"By default, GraphQL servers expose a built-in system, called introspection, that exposes details about the underlying schema.",source:"@site/versioned_docs/version-3.x.x/schema-generator/execution/introspection.md",sourceDirName:"schema-generator/execution",slug:"/schema-generator/execution/introspection",permalink:"/graphql-kotlin/docs/3.x.x/schema-generator/execution/introspection",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-3.x.x/schema-generator/execution/introspection.md",tags:[],version:"3.x.x",lastUpdatedBy:"Dariusz Kuc",lastUpdatedAt:1713385577,formattedLastUpdatedAt:"Apr 17, 2024",frontMatter:{id:"introspection",title:"Introspection",original_id:"introspection"},sidebar:"docs",previous:{title:"Subscriptions",permalink:"/graphql-kotlin/docs/3.x.x/schema-generator/execution/subscriptions"},next:{title:"Apollo Federation",permalink:"/graphql-kotlin/docs/3.x.x/federated/apollo-federation"}},l={},c=[{value:"Introspection types",id:"introspection-types",level:2},{value:"Disabling Introspection",id:"disabling-introspection",level:2}];function d(e){const n={a:"a",code:"code",em:"em",h2:"h2",li:"li",p:"p",pre:"pre",strong:"strong",ul:"ul",...(0,s.R)(),...e.components};return(0,i.jsxs)(i.Fragment,{children:[(0,i.jsxs)(n.p,{children:["By default, GraphQL servers expose a built-in system, called ",(0,i.jsx)(n.strong,{children:"introspection"}),", that exposes details about the underlying schema.\nClients can use introspection to obtain information about all the supported queries as well as all the types exposed in the schema."]}),"\n",(0,i.jsx)(n.h2,{id:"introspection-types",children:"Introspection types"}),"\n",(0,i.jsxs)(n.ul,{children:["\n",(0,i.jsxs)(n.li,{children:[(0,i.jsx)(n.em,{children:"__schema"})," - root level query field that provides information about all entry points (e.g. ",(0,i.jsx)(n.code,{children:"queryType"}),"), all types exposed\nby the schema (including built-in scalars and introspection types) as well as all directives supported by the system"]}),"\n",(0,i.jsxs)(n.li,{children:[(0,i.jsx)(n.em,{children:"__type(name: String!)"})," - root level query field that provides information about the requested type (if it exists)"]}),"\n",(0,i.jsxs)(n.li,{children:["**",(0,i.jsx)(n.em,{children:"typename"})," - field that can be added to ",(0,i.jsx)(n.em,{children:"ANY"})," selection and will return the name of the enclosing type, `**typename`\nis often used in polymorphic queries in order to easily determine underlying implementation type"]}),"\n",(0,i.jsxs)(n.li,{children:[(0,i.jsxs)(n.strong,{children:[(0,i.jsx)(n.em,{children:"Directive"}),", _"]}),"DirectiveLocation_, ",(0,i.jsxs)(n.strong,{children:[(0,i.jsx)(n.em,{children:"EnumValue"}),", _"]}),"Field_, ",(0,i.jsxs)(n.strong,{children:[(0,i.jsx)(n.em,{children:"InputValue"}),", _"]}),"Schema_, ",(0,i.jsxs)(n.strong,{children:[(0,i.jsx)(n.em,{children:"Type"}),", _"]}),"TypeKind_ - built-in\nintrospection types that are used to describe the schema."]}),"\n"]}),"\n",(0,i.jsx)(n.p,{children:"For example, the query below will return a root Query object name as well as names of all types and all directives."}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-graphql",children:"\nquery {\n  __schema {\n    queryType {\n      name\n    }\n    types {\n      name\n    }\n    directives {\n      name\n    }\n  }\n}\n\n"})}),"\n",(0,i.jsxs)(n.p,{children:["Additional information on introspection can be found on ",(0,i.jsx)(n.a,{href:"https://graphql.org/learn/introspection/",children:"GraphQL.org"}),"."]}),"\n",(0,i.jsx)(n.h2,{id:"disabling-introspection",children:"Disabling Introspection"}),"\n",(0,i.jsxs)(n.p,{children:["Introspection system can be disabled by specifying ",(0,i.jsx)(n.code,{children:"introspectionEnabled=false"})," configuration option on an instance of\n",(0,i.jsx)(n.code,{children:"SchemaGeneratorConfig"})," that will be used by the ",(0,i.jsx)(n.code,{children:"SchemaGenerator"})," to generate the GraphQL schema."]}),"\n",(0,i.jsxs)(n.p,{children:["Many GraphQL tools (e.g. ",(0,i.jsx)(n.a,{href:"https://github.com/prisma-labs/graphql-playground",children:"GraphQL Playground"})," or ",(0,i.jsx)(n.a,{href:"https://github.com/graphql/graphiql",children:"GraphiQL"}),")\nrely on introspection queries to function properly. Disabling introspection will prevent clients from accessing ",(0,i.jsx)(n.code,{children:"__schema"}),"\nand ",(0,i.jsx)(n.code,{children:"__type"})," fields. This may break some of the functionality that your clients might rely on and should be used with\nextreme caution."]})]})}function p(e={}){const{wrapper:n}={...(0,s.R)(),...e.components};return n?(0,i.jsx)(n,{...e,children:(0,i.jsx)(d,{...e})}):d(e)}},28453:(e,n,t)=>{t.d(n,{R:()=>r,x:()=>a});var i=t(96540);const s={},o=i.createContext(s);function r(e){const n=i.useContext(o);return i.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function a(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(s):e.components||s:r(e.components),i.createElement(o.Provider,{value:n},e.children)}}}]);