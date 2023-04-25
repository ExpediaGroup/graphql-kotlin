# GraphQL Kotlin GraalVM Metadata Generator

[![Maven Central](https://img.shields.io/maven-central/v/com.expediagroup/graphql-kotlin-graalvm-metadata-generator.svg?label=Maven%20Central)](https://central.sonatype.com/search?namespace=com.expediagroup&q=name%3Agraphql-kotlin-graalvm-metadata-generator)
[![Javadocs](https://img.shields.io/maven-central/v/com.expediagroup/graphql-kotlin-graalvm-metadata-generator.svg?label=javadoc&colorB=brightgreen)](https://www.javadoc.io/doc/com.expediagroup/graphql-kotlin-graalvm-metadata-generator)

`graphql-kotlin-graalvm-metadata-generator` is used to generate [GraalVM reachability metadata](https://www.graalvm.org/22.2/reference-manual/native-image/metadata/)
for `graphql-kotlin` servers. This metadata can then be used by GraalVM native [Gradle](https://graalvm.github.io/native-build-tools/latest/gradle-plugin.html)
and [Maven](https://graalvm.github.io/native-build-tools/latest/maven-plugin.html) plugins to generate native images.

**This module is not intended to be consumed directly. Instead, you should rely on the functionality provided by the build plugins.**

When generating GraalVM native images, we need to provide information about all dynamic features (e.g. reflections) features
that we will be relying on. This module automatically generates following metadata files that should be placed under
`META-INF/native-image/<groupId>/<artifactId>` directory:

 * `native-image.properties` - additional arguments passed to the native image generation process
 * `reflect-config.json` - all reflection information used by the schema generator process
 * `resource-config.json` - list of resource files and bundles that should be included in the native image
