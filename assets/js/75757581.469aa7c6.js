"use strict";(self.webpackChunkgraphql_kotlin_docs=self.webpackChunkgraphql_kotlin_docs||[]).push([[9798],{1316:(e,t,r)=>{r.r(t),r.d(t,{assets:()=>d,contentTitle:()=>l,default:()=>h,frontMatter:()=>i,metadata:()=>p,toc:()=>c});var a=r(7462),n=r(3366),o=(r(7294),r(3905)),s=(r(8561),["components"]),i={id:"release-proc",title:"Releasing a new version",original_id:"release-proc"},l=void 0,p={unversionedId:"contributors/release-proc",id:"version-3.x.x/contributors/release-proc",title:"Releasing a new version",description:"In order to release a new version we need to draft a new release",source:"@site/versioned_docs/version-3.x.x/contributors/release-proc.md",sourceDirName:"contributors",slug:"/contributors/release-proc",permalink:"/graphql-kotlin/docs/3.x.x/contributors/release-proc",draft:!1,editUrl:"https://github.com/ExpediaGroup/graphql-kotlin/tree/master/website/versioned_docs/version-3.x.x/contributors/release-proc.md",tags:[],version:"3.x.x",lastUpdatedBy:"Samuel Vazquez",lastUpdatedAt:1677182897,formattedLastUpdatedAt:"Feb 23, 2023",frontMatter:{id:"release-proc",title:"Releasing a new version",original_id:"release-proc"},sidebar:"version-3.x.x/docs",previous:{title:"Maven Plugin",permalink:"/graphql-kotlin/docs/3.x.x/plugins/maven-plugin"}},d={},c=[{value:"Release requirements",id:"release-requirements",level:3}],u={toc:c};function h(e){var t=e.components,r=(0,n.Z)(e,s);return(0,o.kt)("wrapper",(0,a.Z)({},u,r,{components:t,mdxType:"MDXLayout"}),(0,o.kt)("p",null,"In order to ",(0,o.kt)("a",{parentName:"p",href:"https://github.com/ExpediaGroup/graphql-kotlin/releases"},"release a new version")," we need to draft a new release\nand tag the commit. Releases are following ",(0,o.kt)("a",{parentName:"p",href:"https://semver.org/"},"semantic versioning")," and specify major, minor and patch version."),(0,o.kt)("p",null,"Once release is published it will trigger corresponding ",(0,o.kt)("a",{parentName:"p",href:"https://github.com/ExpediaGroup/graphql-kotlin/blob/3.x.x/.github/workflows/release.yml"},"Github Action"),"\nbased on the published release event. Release workflow will then proceed to build and publish all library artifacts to ",(0,o.kt)("a",{parentName:"p",href:"https://central.sonatype.org/"},"Maven Central"),"."),(0,o.kt)("h3",{id:"release-requirements"},"Release requirements"),(0,o.kt)("ul",null,(0,o.kt)("li",{parentName:"ul"},"tag should specify newly released library version that is following ",(0,o.kt)("a",{parentName:"li",href:"https://semver.org/"},"semantic versioning")),(0,o.kt)("li",{parentName:"ul"},"tag and release name should match"),(0,o.kt)("li",{parentName:"ul"},"release should contain the information about all the change sets that were included in the given release. We are using ",(0,o.kt)("inlineCode",{parentName:"li"},"release-drafter")," to help automatically\ncollect this information and generate automatic release notes.")))}h.isMDXComponent=!0}}]);