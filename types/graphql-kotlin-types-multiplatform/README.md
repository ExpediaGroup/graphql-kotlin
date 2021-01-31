# graphql-kotlin-types-multiplatform

[![Maven Central](https://img.shields.io/maven-central/v/com.expediagroup/graphql-kotlin-types.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.expediagroup%22%20AND%20a:%22graphql-kotlin-types-multiplatform%22)
[![Javadocs](https://img.shields.io/maven-central/v/com.expediagroup/graphql-kotlin-types.svg?label=javadoc&colorB=brightgreen)](https://www.javadoc.io/doc/com.expediagroup/graphql-kotlin-types-multiplatform)

The core types used by both client and server for communication over GraphQL. These types are annotated
with [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization) metadata so they can be serialized/deserialized. Instead of using this
package, you may want to look at the other fully featured client and server packages in our [main README](./../README.md).

### GraphQLRequest

Provides the common format for communicating with [GraphQL over HTTP](https://graphql.org/learn/serving-over-http/#post-request). This format is not
part of the specification but it is mostly standard at this point in all server implementations as the expected format.

### GraphQLResponse

Provides a [GraphQL specification compliant response](http://spec.graphql.org/June2018/#sec-Data) class. This can be used by clients or servers.


