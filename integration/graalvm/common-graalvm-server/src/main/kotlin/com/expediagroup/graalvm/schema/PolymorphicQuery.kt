/*
 * Copyright 2023 Expedia, Inc
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

package com.expediagroup.graalvm.schema

import com.expediagroup.graalvm.schema.model.ExampleInterface
import com.expediagroup.graalvm.schema.model.ExampleUnion
import com.expediagroup.graalvm.schema.model.FirstImpl
import com.expediagroup.graalvm.schema.model.FirstUnionMember
import com.expediagroup.graalvm.schema.model.SecondImpl
import com.expediagroup.graalvm.schema.model.SecondUnionMember
import com.expediagroup.graphql.server.operations.Query
import kotlin.random.Random

private val random: Random = Random(100)

/**
 * Queries verifying polymorphism.
 */
class PolymorphicQuery : Query {
    fun interfaceQuery(): ExampleInterface = if (random.nextBoolean()) {
        FirstImpl()
    } else {
        SecondImpl()
    }

    fun unionQuery(): ExampleUnion = if (random.nextBoolean()) {
        FirstUnionMember()
    } else {
        SecondUnionMember()
    }

//    fun sealedQuery(): SealedUnion = if (random.nextBoolean()) {
//        SealedUnion.SealedValueA(random.nextInt())
//    } else {
//        SealedUnion.SealedValueB(random.nextDouble())
//    }
}

//sealed class SealedUnion(val common: String) {
//    data class SealedValueA(val a: Int) : SealedUnion(UUID.randomUUID().toString())
//
//    data class SealedValueB(val b: Double) : SealedUnion(UUID.randomUUID().toString())
//}
