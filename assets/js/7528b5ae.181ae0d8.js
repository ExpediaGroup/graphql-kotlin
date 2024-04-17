"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[5508],{84706:(e,n,o)=>{o.r(n),o.d(n,{assets:()=>l,contentTitle:()=>i,default:()=>p,frontMatter:()=>t,metadata:()=>s,toc:()=>c});var a=o(74848),r=o(28453);const t={id:"maven-plugin-usage-sdl",title:"Maven Plugin SDL Usage",sidebar_label:"Generating SDL"},i=void 0,s={id:"plugins/maven-plugin-usage-sdl",title:"Maven Plugin SDL Usage",description:"GraphQL Kotlin follows a code-first approach where schema is auto generated from your source code at runtime. GraphQL Kotlin",source:"@site/docs/plugins/maven-plugin-usage-sdl.md",sourceDirName:"plugins",slug:"/plugins/maven-plugin-usage-sdl",permalink:"/graphql-kotlin/docs/8.x.x/plugins/maven-plugin-usage-sdl",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/docs/plugins/maven-plugin-usage-sdl.md",tags:[],version:"current",lastUpdatedBy:"Dariusz Kuc",lastUpdatedAt:1713385577,formattedLastUpdatedAt:"Apr 17, 2024",frontMatter:{id:"maven-plugin-usage-sdl",title:"Maven Plugin SDL Usage",sidebar_label:"Generating SDL"},sidebar:"docs",previous:{title:"Generating Client",permalink:"/graphql-kotlin/docs/8.x.x/plugins/maven-plugin-usage-client"},next:{title:"GraalVM Native Image",permalink:"/graphql-kotlin/docs/8.x.x/plugins/maven-plugin-usage-graalvm"}},l={},c=[{value:"Generating SDL",id:"generating-sdl",level:2},{value:"Using Custom Hooks Provider",id:"using-custom-hooks-provider",level:2}];function d(e){const n={a:"a",code:"code",h2:"h2",p:"p",pre:"pre",...(0,r.R)(),...e.components};return(0,a.jsxs)(a.Fragment,{children:[(0,a.jsx)(n.p,{children:"GraphQL Kotlin follows a code-first approach where schema is auto generated from your source code at runtime. GraphQL Kotlin\nplugins can be used to generate schema as a build time artifact. This allows you to seamlessly integrate with various\nGraphQL tools that may require a schema artifact as an input (e.g. to perform backwards compatibility checks, etc)."}),"\n",(0,a.jsx)(n.h2,{id:"generating-sdl",children:"Generating SDL"}),"\n",(0,a.jsxs)(n.p,{children:["GraphQL schema can be generated directly from your source code using reflections. ",(0,a.jsx)(n.code,{children:"generate-sdl"})," mojo will scan your\nclasspath looking for classes implementing ",(0,a.jsx)(n.code,{children:"Query"}),", ",(0,a.jsx)(n.code,{children:"Mutation"})," and ",(0,a.jsx)(n.code,{children:"Subscription"})," marker interfaces and then generates the\ncorresponding GraphQL schema using ",(0,a.jsx)(n.code,{children:"graphql-kotlin-schema-generator"})," and default ",(0,a.jsx)(n.code,{children:"NoopSchemaGeneratorHooks"}),". In order to\nlimit the amount of packages to scan, this mojo requires users to provide a list of ",(0,a.jsx)(n.code,{children:"packages"})," that can contain GraphQL\ntypes."]}),"\n",(0,a.jsx)(n.pre,{children:(0,a.jsx)(n.code,{className:"language-xml",children:"<plugin>\n    <groupId>com.expediagroup</groupId>\n    <artifactId>graphql-kotlin-maven-plugin</artifactId>\n    <version>${graphql-kotlin.version}</version>\n    <executions>\n        <execution>\n            <goals>\n                <goal>generate-sdl</goal>\n            </goals>\n            <configuration>\n                <packages>\n                    <package>com.example</package>\n                </packages>\n            </configuration>\n        </execution>\n    </executions>\n</plugin>\n"})}),"\n",(0,a.jsx)(n.h2,{id:"using-custom-hooks-provider",children:"Using Custom Hooks Provider"}),"\n",(0,a.jsxs)(n.p,{children:["Plugin will default to use ",(0,a.jsx)(n.code,{children:"NoopSchemaGeneratorHooks"})," to generate target GraphQL schema. If your project uses custom hooks\nor needs to generate the federated GraphQL schema, you will need to provide an instance of ",(0,a.jsx)(n.code,{children:"SchemaGeneratorHooksProvider"}),"\nservice provider that will be used to create an instance of your custom hooks."]}),"\n",(0,a.jsxs)(n.p,{children:[(0,a.jsx)(n.code,{children:"generate-sdl"})," mojo utilizes ",(0,a.jsx)(n.a,{href:"https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/ServiceLoader.html",children:"ServiceLoader"}),"\nmechanism to dynamically load available ",(0,a.jsx)(n.code,{children:"SchemaGeneratorHooksProvider"})," service providers from the classpath. Service provider\ncan be provided as part of your project, included in one of your project dependencies or through explicitly provided artifact.\nSee ",(0,a.jsx)(n.a,{href:"/graphql-kotlin/docs/8.x.x/plugins/hooks-provider",children:"Schema Generator Hooks Provider"})," for additional details on how to create custom hooks service provider.\nConfiguration below shows how to configure GraphQL Kotlin plugin with explicitly provided artifact to generate federated\nGraphQL schema."]}),"\n",(0,a.jsx)(n.pre,{children:(0,a.jsx)(n.code,{className:"language-xml",children:"<plugin>\n    <groupId>com.expediagroup</groupId>\n    <artifactId>graphql-kotlin-maven-plugin</artifactId>\n    <version>${graphql-kotlin.version}</version>\n    <executions>\n        <execution>\n            <goals>\n                <goal>generate-sdl</goal>\n            </goals>\n            <configuration>\n                <packages>\n                    <package>com.example</package>\n                </packages>\n            </configuration>\n        </execution>\n    </executions>\n    <dependencies>\n        <dependency>\n            <groupId>com.expediagroup</groupId>\n            <artifactId>graphql-kotlin-federated-hooks-provider</artifactId>\n            <version>${graphql-kotlin.version}</version>\n        </dependency>\n    </dependencies>\n</plugin>\n"})})]})}function p(e={}){const{wrapper:n}={...(0,r.R)(),...e.components};return n?(0,a.jsx)(n,{...e,children:(0,a.jsx)(d,{...e})}):d(e)}},28453:(e,n,o)=>{o.d(n,{R:()=>i,x:()=>s});var a=o(96540);const r={},t=a.createContext(r);function i(e){const n=a.useContext(t);return a.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function s(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(r):e.components||r:i(e.components),a.createElement(t.Provider,{value:n},e.children)}}}]);