---
id: release-proc
title: Releasing a new version
---
1. The `pom.xml` should already be `${currentVersion}-SNAPSHOT`

2. Draft a new release and tag the commit (usually just `master`) you want to release as `${currentVersion}` or
   increment to a new major/minor version https://github.com/ExpediaDotCom/graphql-kotlin/releases
    - When drafting a new release, look at the commit history and comment any new features that were made
    - Travis will automatically build on a new tag, change the pom to remove `-SNAPSHOT`, and push the library to Maven
      Central
    - Travis will create a new branch that bumps the version in both the library and example to
      `${nextVersion}-SNAPSHOT`

3. Create a new PR for the Travis branch for the snapshot updates
