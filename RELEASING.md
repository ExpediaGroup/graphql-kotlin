In order to [release a new version](https://github.com/ExpediaGroup/graphql-kotlin/releases) we need to draft a new release
and tag the commit. Releases are following [semantic versioning](https://semver.org/) and specify major, minor and patch version.

Once release is published it will trigger corresponding [Github Action](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/.github/workflows/release.yml)
based on the published release event. Release workflow will then proceed to build and publish all library artifacts to [Maven Central](https://central.sonatype.org/).

### Release requirements

-   tag should specify newly released library version that is following [semantic versioning](https://semver.org/)
-   tag and release name should match
-   release should contain the information about all the change sets that were included in the given release. We are using `release-drafter` to help automatically
    collect this information and generate automatic release notes.
