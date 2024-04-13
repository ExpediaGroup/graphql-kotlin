/*
 * Copyright 2022 Expedia, Inc
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

package com.expediagroup.graphql.generator.internal.extensions

import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.expediagroup.graphql.generator.annotations.GraphQLUnion
import com.expediagroup.graphql.generator.exceptions.CouldNotGetNameOfKClassException
import com.expediagroup.graphql.generator.hooks.NoopSchemaGeneratorHooks
import com.expediagroup.graphql.generator.hooks.SchemaGeneratorHooks
import org.junit.jupiter.api.Test
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Suppress("Detekt.UnusedPrivateClass")
open class KClassExtensionsTest {

    interface SomeInterface {
        val someField: String?

        fun someFunction(): String?
    }

    abstract class SomeAbstractClass : SomeInterface {
        override val someField: String = "Hello"

        override fun someFunction(): String? = "Goodbye"

        abstract val justAnotherField: String?
    }

    class SomeConcreteClass : SomeAbstractClass() {
        override val justAnotherField: String? = "Default value"
    }

    @Suppress("Detekt.FunctionOnlyReturningConstant", "Detekt.UnusedPrivateMember")
    private class MyTestClass(
        val publicProperty: String = "public",
        val filteredProperty: String = "filtered",
        private val privateVal: String = "hidden"
    ) : TestUnion {
        fun publicFunction() = "public function"

        fun filteredFunction() = "filtered function"

        private fun privateTestFunction() = "private function"
    }

    @GraphQLName("MyTestClassRenamed")
    private class MyTestClassCustomName

    internal class MyInternalClass

    class MyClassInput

    @GraphQLName("MyClassRenamedInput")
    class MyClassCustomNameInput

    protected class MyProtectedClass

    class MyPublicClass

    internal class UnionSuperclass : TestUnion

    @GraphQLIgnore
    internal interface IgnoredInterface {
        val id: String
    }

    @GraphQLIgnore
    interface IgnoredSecondLevelInterface : SomeInterface

    @GraphQLIgnore
    class IgnoredClass(val value: String)

    internal class ClassWithSecondLevelInterface : IgnoredSecondLevelInterface {
        override val someField: String = "hello"

        override fun someFunction(): String? = null
    }

    @GraphQLIgnore
    abstract class IgnoredAbstractClass : SomeInterface

    @GraphQLIgnore
    abstract class IgnoredSecondAbstractClass : IgnoredAbstractClass()

    internal class ClassWithNoValidSuperclass(override val id: String) : IgnoredInterface

    internal class ClassWithSecondLevelAbstractClass : IgnoredAbstractClass() {
        override val someField: String = "foo"
        override fun someFunction(): String? = "bar"
    }

    internal class ClassWithThirdLevelAbstractClass : IgnoredSecondAbstractClass() {
        override val someField: String = "foo"
        override fun someFunction(): String? = "bar"
    }

    internal class InterfaceSuperclass : InvalidFunctionUnionInterface {
        override fun getTest() = 2
    }

    private enum class MyTestEnum {
        ONE,
        TWO
    }

    private class EmptyConstructorClass {
        val id = 1
    }

    interface TestUnion

    sealed class Pet(val name: String) {
        class Dog(name: String, val goodBoysReceived: Int) : Pet(name)
        class Cat(name: String, val livesRemaining: Int) : Pet(name)
    }

    private interface InvalidPropertyUnionInterface {
        val test: Int
            get() = 1
    }

    @Suppress("Detekt.FunctionOnlyReturningConstant")
    interface InvalidFunctionUnionInterface {
        fun getTest() = 1
    }

    class One(val value: String)
    class Two(val value: String)

    class TestQuery {
        @GraphQLUnion(name = "Number", possibleTypes = [One::class, Two::class])
        fun customUnion(): Any = One("1")

        @GraphQLUnion(name = "InvalidUnion", possibleTypes = [One::class, Two::class])
        fun invalidCustomUnion(): Int = 1

        @MetaUnion
        fun customMetaUnion(): Any = One("1")

        @MetaUnion
        fun invalidCustomMetaUnion(): Int = 1
    }

    @GraphQLUnion(name = "MetaUnion", possibleTypes = [One::class, Two::class])
    annotation class MetaUnion

    private class FilterHooks : SchemaGeneratorHooks {
        override fun isValidProperty(kClass: KClass<*>, property: KProperty<*>) =
            property.name.contains("filteredProperty").not()

        override fun isValidFunction(kClass: KClass<*>, function: KFunction<*>) =
            function.name.contains("filteredFunction").not()

        override fun isValidSuperclass(kClass: KClass<*>) =
            kClass.simpleName?.contains("InvalidFunctionUnionInterface")?.not().isTrue()
    }

    private val noopHooks = NoopSchemaGeneratorHooks

    @Test
    fun `test getting valid properties with no hooks`() {
        val properties = MyTestClass::class.getValidProperties(noopHooks)
        assertEquals(listOf("filteredProperty", "publicProperty"), properties.map { it.name })
    }

    @Test
    fun `test getting valid properties with filter hooks`() {
        val properties = MyTestClass::class.getValidProperties(FilterHooks())
        assertEquals(listOf("publicProperty"), properties.map { it.name })
    }

    @Test
    fun `test getting valid properties from abstract classes`() {
        val concreteProperties = SomeConcreteClass::class.getValidProperties(noopHooks)
        assertEquals(listOf("justAnotherField", "someField"), concreteProperties.map { it.name })
    }

    @Test
    fun `test getting valid functions with no hooks`() {
        val properties = MyTestClass::class.getValidFunctions(noopHooks)
        assertEquals(listOf("filteredFunction", "publicFunction"), properties.map { it.name })
    }

    @Test
    fun `test getting valid functions with filter hooks`() {
        val properties = MyTestClass::class.getValidFunctions(FilterHooks())
        assertEquals(listOf("publicFunction"), properties.map { it.name })
    }

    @Test
    fun `test getting functions from abstract classes`() {
        val properties = SomeConcreteClass::class.getValidFunctions(noopHooks)
        assertEquals(listOf("someFunction"), properties.map { it.name })
    }

    @Test
    fun `test getting valid superclass with no hooks`() {
        val superclasses = InterfaceSuperclass::class.getValidSuperclasses(noopHooks)
        assertEquals(listOf("InvalidFunctionUnionInterface"), superclasses.map { it.simpleName })
    }

    @Test
    fun `test getting valid superclass with filter hooks`() {
        val superclasses = InterfaceSuperclass::class.getValidSuperclasses(FilterHooks())
        assertTrue(superclasses.isEmpty())
    }

    @Test
    fun `test getting invalid superclass with no hooks`() {
        val superclasses = UnionSuperclass::class.getValidSuperclasses(noopHooks)
        assertTrue(superclasses.isEmpty())
    }

    @Test
    fun `Superclasses are not included when marked as ignored`() {
        val superclasses = ClassWithNoValidSuperclass::class.getValidSuperclasses(noopHooks)
        assertTrue(superclasses.isEmpty())
    }

    @Test
    fun `Return superclasses from abstract class two levels deep`() {
        val superclasses = ClassWithSecondLevelAbstractClass::class.getValidSuperclasses(noopHooks)
        assertEquals(expected = 1, actual = superclasses.size)
    }

    @Test
    fun `Return superclasses from abstract class three levels deep`() {
        val superclasses = ClassWithThirdLevelAbstractClass::class.getValidSuperclasses(noopHooks)
        assertEquals(expected = 1, actual = superclasses.size)
    }

    @Test
    fun `Return superclasses from interface two levels deep`() {
        val superclasses = ClassWithSecondLevelInterface::class.getValidSuperclasses(noopHooks)
        assertEquals(expected = 1, actual = superclasses.size)
    }

    @Test
    fun `test findConstructorParamter`() {
        assertNotNull(MyTestClass::class.findConstructorParameter("publicProperty"))
        assertNull(MyTestClass::class.findConstructorParameter("foobar"))
        assertNull(EmptyConstructorClass::class.findConstructorParameter("id"))
        assertNull(TestUnion::class.findConstructorParameter("foobar"))
    }

    @Test
    fun `test enum extension`() {
        assertTrue(MyTestEnum::class.isEnum())
        assertFalse(MyTestClass::class.isEnum())
    }

    @Test
    fun `test listType extension`() {
        assertTrue(listOf(1)::class.isListType())
        assertFalse(MyTestClass::class.isListType())
    }

    @Test
    fun `test graphql interface extension`() {
        assertTrue(TestUnion::class.isInterface())
        assertTrue(SomeAbstractClass::class.isInterface())
        assertTrue(Pet::class.isInterface())
        assertFalse(MyTestClass::class.isInterface())
    }

    @Test
    fun `test graphql union extension`() {
        assertTrue(TestUnion::class.isUnion())
        val customAnnotationUnion = TestQuery::customUnion
        assertTrue(customAnnotationUnion.returnType.getKClass().isUnion(customAnnotationUnion.annotations))
        val metaAnnotationUnion = TestQuery::customMetaUnion
        assertTrue(metaAnnotationUnion.returnType.getKClass().isUnion(metaAnnotationUnion.annotations))
        val metaUnionAnnotationClass = MetaUnion::class
        assertTrue(metaUnionAnnotationClass.isUnion(metaAnnotationUnion.annotations))
        assertFalse(InvalidPropertyUnionInterface::class.isUnion())
        assertFalse(InvalidFunctionUnionInterface::class.isUnion())
        assertFalse(Pet::class.isUnion())
        val invalidAnnotationUnion = TestQuery::invalidCustomUnion
        assertFalse(invalidAnnotationUnion.returnType.getKClass().isUnion(invalidAnnotationUnion.annotations))
        val invalidMetaAnnotationUnion = TestQuery::invalidCustomMetaUnion
        assertFalse(invalidMetaAnnotationUnion.returnType.getKClass().isUnion(invalidMetaAnnotationUnion.annotations))
    }

    @Test
    fun `test isAnnotation extension`() {
        assertTrue(MetaUnion::class.isAnnotation())
        assertFalse(TestUnion::class.isAnnotation())
    }

    @Test
    fun `test class simple name`() {
        assertEquals("MyTestClass", MyTestClass::class.getSimpleName())
        assertFailsWith(CouldNotGetNameOfKClassException::class) {
            object { }::class.getSimpleName()
        }
    }

    @Test
    fun `test class simple name with GraphQLName`() {
        assertEquals("MyTestClassRenamed", MyTestClassCustomName::class.getSimpleName())
    }

    @Test
    fun `test input class name`() {
        assertEquals("MyTestClassInput", MyTestClass::class.getSimpleName(true))
        assertEquals("MyClassInput", MyClassInput::class.getSimpleName(true))
    }

    @Test
    fun `test input class name with GraphQLName`() {
        assertEquals("MyTestClassRenamedInput", MyTestClassCustomName::class.getSimpleName(true))
        assertEquals("MyClassRenamedInput", MyClassCustomNameInput::class.getSimpleName(true))
    }

    @Test
    fun getQualifiedName() {
        assertEquals("com.expediagroup.graphql.generator.internal.extensions.KClassExtensionsTest.MyTestClass", MyTestClass::class.getQualifiedName())
        assertEquals("", object { }::class.getQualifiedName())
    }

    @Test
    fun isPublic() {
        assertTrue(MyPublicClass::class.isPublic())
        assertFalse(MyInternalClass::class.isPublic())
        assertFalse(MyProtectedClass::class.isPublic())
        assertFalse(MyTestClass::class.isPublic())
    }

    @Test
    fun isNotPublic() {
        assertFalse(MyPublicClass::class.isNotPublic())
        assertTrue(MyInternalClass::class.isNotPublic())
        assertTrue(MyProtectedClass::class.isNotPublic())
        assertTrue(MyTestClass::class.isNotPublic())
    }

    @Test
    fun isValidAdditionalType() {
        // Valid cases
        assertTrue(MyPublicClass::class.isValidAdditionalType(false))
        assertTrue(MyPublicClass::class.isValidAdditionalType(true))
        assertTrue(SomeInterface::class.isValidAdditionalType(false))
        assertTrue(TestUnion::class.isValidAdditionalType(false))
        assertTrue(MyTestEnum::class.isValidAdditionalType(false))
        assertTrue(MyTestEnum::class.isValidAdditionalType(true))

        // Invalid cases
        assertFalse(SomeInterface::class.isValidAdditionalType(true))
        assertFalse(TestUnion::class.isValidAdditionalType(true))
        assertFalse(IgnoredInterface::class.isValidAdditionalType(true))
        assertFalse(IgnoredInterface::class.isValidAdditionalType(false))
        assertFalse(IgnoredSecondLevelInterface::class.isValidAdditionalType(true))
        assertFalse(IgnoredSecondLevelInterface::class.isValidAdditionalType(false))
        assertFalse(IgnoredClass::class.isValidAdditionalType(true))
        assertFalse(IgnoredClass::class.isValidAdditionalType(false))
    }
}
