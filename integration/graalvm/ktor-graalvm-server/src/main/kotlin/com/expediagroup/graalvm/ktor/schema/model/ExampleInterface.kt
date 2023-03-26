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

package com.expediagroup.graalvm.ktor.schema.model

import com.expediagroup.graphql.generator.scalars.ID
import java.util.UUID
import kotlin.random.Random

interface ExampleInterface {
    fun common(): String
}

class FirstImpl : ExampleInterface {
    override fun common(): String = "common value from first implementation"
    fun specific(): Int = Random(100).nextInt()
}

class SecondImpl : ExampleInterface {
    override fun common(): String = "common value from second implementation"
    fun unique(): ID = ID(UUID.randomUUID().toString())
}
