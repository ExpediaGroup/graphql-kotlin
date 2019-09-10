/*
 * Copyright 2019 Expedia, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.expediagroup.graphql.generator.state

import com.expediagroup.graphql.directives.DeprecatedDirective
import graphql.Directives
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLType
import java.util.concurrent.ConcurrentHashMap

@Suppress("UseDataClass")
internal class SchemaGeneratorState(supportedPackages: List<String>) {
    val cache = TypesCache(supportedPackages)
    val additionalTypes = mutableSetOf<GraphQLType>()
    val directives = ConcurrentHashMap<String, GraphQLDirective>()

    init {
        // NOTE: @include and @defer query directives are added by graphql-java by default
        // adding them explicitly here to keep it consistent with missing deprecated directive
        directives[Directives.IncludeDirective.name] = Directives.IncludeDirective
        directives[Directives.SkipDirective.name] = Directives.SkipDirective

        // graphql-kotlin default directives
        // @deprecated directive is a built-in directive that each GraphQL server should provide bu currently it is not added by graphql-java
        //   see https://github.com/graphql-java/graphql-java/issues/1598
        directives[DeprecatedDirective.name] = DeprecatedDirective
    }
}
