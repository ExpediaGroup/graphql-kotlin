"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[3258],{66279:(e,r,n)=>{n.r(r),n.d(r,{assets:()=>l,contentTitle:()=>i,default:()=>h,frontMatter:()=>o,metadata:()=>a,toc:()=>c});var t=n(74848),s=n(28453);const o={id:"release-proc",title:"Releasing a new version",original_id:"release-proc"},i=void 0,a={id:"contributors/release-proc",title:"Releasing a new version",description:"In order to release a new version we need to draft a new release",source:"@site/versioned_docs/version-3.x.x/contributors/release-proc.md",sourceDirName:"contributors",slug:"/contributors/release-proc",permalink:"/graphql-kotlin/docs/3.x.x/contributors/release-proc",draft:!1,unlisted:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-3.x.x/contributors/release-proc.md",tags:[],version:"3.x.x",lastUpdatedBy:"Samuel Vazquez",lastUpdatedAt:1744404742e3,frontMatter:{id:"release-proc",title:"Releasing a new version",original_id:"release-proc"},sidebar:"docs",previous:{title:"Maven Plugin",permalink:"/graphql-kotlin/docs/3.x.x/plugins/maven-plugin"}},l={},c=[{value:"Release requirements",id:"release-requirements",level:3}];function d(e){const r={a:"a",code:"code",h3:"h3",li:"li",p:"p",ul:"ul",...(0,s.R)(),...e.components};return(0,t.jsxs)(t.Fragment,{children:[(0,t.jsxs)(r.p,{children:["In order to ",(0,t.jsx)(r.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/releases",children:"release a new version"})," we need to draft a new release\nand tag the commit. Releases are following ",(0,t.jsx)(r.a,{href:"https://semver.org/",children:"semantic versioning"})," and specify major, minor and patch version."]}),"\n",(0,t.jsxs)(r.p,{children:["Once release is published it will trigger corresponding ",(0,t.jsx)(r.a,{href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/3.x.x/.github/workflows/release.yml",children:"Github Action"}),"\nbased on the published release event. Release workflow will then proceed to build and publish all library artifacts to ",(0,t.jsx)(r.a,{href:"https://central.sonatype.org/",children:"Maven Central"}),"."]}),"\n",(0,t.jsx)(r.h3,{id:"release-requirements",children:"Release requirements"}),"\n",(0,t.jsxs)(r.ul,{children:["\n",(0,t.jsxs)(r.li,{children:["tag should specify newly released library version that is following ",(0,t.jsx)(r.a,{href:"https://semver.org/",children:"semantic versioning"})]}),"\n",(0,t.jsx)(r.li,{children:"tag and release name should match"}),"\n",(0,t.jsxs)(r.li,{children:["release should contain the information about all the change sets that were included in the given release. We are using ",(0,t.jsx)(r.code,{children:"release-drafter"})," to help automatically\ncollect this information and generate automatic release notes."]}),"\n"]})]})}function h(e={}){const{wrapper:r}={...(0,s.R)(),...e.components};return r?(0,t.jsx)(r,{...e,children:(0,t.jsx)(d,{...e})}):d(e)}},28453:(e,r,n)=>{n.d(r,{R:()=>i,x:()=>a});var t=n(96540);const s={},o=t.createContext(s);function i(e){const r=t.useContext(o);return t.useMemo((function(){return"function"==typeof e?e(r):{...r,...e}}),[r,e])}function a(e){let r;return r=e.disableParentContext?"function"==typeof e.components?e.components(s):e.components||s:i(e.components),t.createElement(o.Provider,{value:r},e.children)}}}]);