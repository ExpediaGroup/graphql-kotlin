// package com.expedia.graphql.directives
//
// class KotlinSchemaDIrectiveWiringTest {
//
//    @Test
//    fun `wireOnEnvironment with no matching element returns the type back`() {
//        val mockWiring = spyk<SchemaDirectiveWiring>()
//        val mockEnvironment: SchemaDirectiveWiringEnvironment<GraphQLDirectiveContainer> = mockk()
//        val mockElement: GraphQLDirectiveContainer = mockk()
//        every { mockEnvironment.element } returns mockElement
//
//        val result = mockWiring.wireOnEnvironment(mockEnvironment)
//        assertEquals(mockElement, result)
//    }
//
//    @Test
//    fun `wireOnEnvironment with GraphQLArgument`() {
//        val mockWiring = spyk<SchemaDirectiveWiring>()
//        val mockEnvironment: SchemaDirectiveWiringEnvironment<GraphQLArgument> = mockk()
//        val mockElement: GraphQLArgument = mockk()
//        every { mockEnvironment.element } returns mockElement
//
//        val result = mockWiring.wireOnEnvironment(mockEnvironment)
//        assertTrue(result is GraphQLArgument)
//        verify(exactly = 1) { mockWiring.onArgument(mockEnvironment) }
//    }
//
//    @Test
//    fun `wireOnEnvironment with GraphQLEnumType`() {
//        val mockWiring = spyk<SchemaDirectiveWiring>()
//        val mockEnvironment: SchemaDirectiveWiringEnvironment<GraphQLEnumType> = mockk()
//        val mockElement: GraphQLEnumType = mockk()
//        every { mockEnvironment.element } returns mockElement
//
//        val result = mockWiring.wireOnEnvironment(mockEnvironment)
//        assertTrue(result is GraphQLEnumType)
//        verify(exactly = 1) { mockWiring.onEnum(mockEnvironment) }
//    }
//
//    @Test
//    fun `wireOnEnvironment with GraphQLEnumValueDefinition`() {
//        val mockWiring = spyk<SchemaDirectiveWiring>()
//        val mockEnvironment: SchemaDirectiveWiringEnvironment<GraphQLEnumValueDefinition> = mockk()
//        val mockElement: GraphQLEnumValueDefinition = mockk()
//        every { mockEnvironment.element } returns mockElement
//
//        val result = mockWiring.wireOnEnvironment(mockEnvironment)
//        assertTrue(result is GraphQLEnumValueDefinition)
//        verify(exactly = 1) { mockWiring.onEnumValue(mockEnvironment) }
//    }
//
//    @Test
//    fun `wireOnEnvironment with GraphQLFieldDefinition`() {
//        val mockWiring = spyk<SchemaDirectiveWiring>()
//        val mockEnvironment: SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition> = mockk()
//        val mockElement: GraphQLFieldDefinition = mockk()
//        every { mockEnvironment.element } returns mockElement
//
//        val result = mockWiring.wireOnEnvironment(mockEnvironment)
//        assertTrue(result is GraphQLFieldDefinition)
//        verify(exactly = 1) { mockWiring.onField(mockEnvironment) }
//    }
//
//    @Test
//    fun `wireOnEnvironment with GraphQLInputObjectField`() {
//        val mockWiring = spyk<SchemaDirectiveWiring>()
//        val mockEnvironment: SchemaDirectiveWiringEnvironment<GraphQLInputObjectField> = mockk()
//        val mockElement: GraphQLInputObjectField = mockk()
//        every { mockEnvironment.element } returns mockElement
//
//        val result = mockWiring.wireOnEnvironment(mockEnvironment)
//        assertTrue(result is GraphQLInputObjectField)
//        verify(exactly = 1) { mockWiring.onInputObjectField(mockEnvironment) }
//    }
//
//    @Test
//    fun `wireOnEnvironment with GraphQLInputObjectType`() {
//        val mockWiring = spyk<SchemaDirectiveWiring>()
//        val mockEnvironment: SchemaDirectiveWiringEnvironment<GraphQLInputObjectType> = mockk()
//        val mockElement: GraphQLInputObjectType = mockk()
//        every { mockEnvironment.element } returns mockElement
//
//        val result = mockWiring.wireOnEnvironment(mockEnvironment)
//        assertTrue(result is GraphQLInputObjectType)
//        verify(exactly = 1) { mockWiring.onInputObjectType(mockEnvironment) }
//    }
//
//    @Test
//    fun `wireOnEnvironment with GraphQLInterfaceType`() {
//        val mockWiring = spyk<SchemaDirectiveWiring>()
//        val mockEnvironment: SchemaDirectiveWiringEnvironment<GraphQLInterfaceType> = mockk()
//        val mockElement: GraphQLInterfaceType = mockk()
//        every { mockEnvironment.element } returns mockElement
//
//        val result = mockWiring.wireOnEnvironment(mockEnvironment)
//        assertTrue(result is GraphQLInterfaceType)
//        verify(exactly = 1) { mockWiring.onInterface(mockEnvironment) }
//    }
//
//    @Test
//    fun `wireOnEnvironment with GraphQLObjectType`() {
//        val mockWiring = spyk<SchemaDirectiveWiring>()
//        val mockEnvironment: SchemaDirectiveWiringEnvironment<GraphQLObjectType> = mockk()
//        val mockElement: GraphQLObjectType = mockk()
//        every { mockEnvironment.element } returns mockElement
//
//        val result = mockWiring.wireOnEnvironment(mockEnvironment)
//        assertTrue(result is GraphQLObjectType)
//        verify(exactly = 1) { mockWiring.onObject(mockEnvironment) }
//    }
//
//    @Test
//    fun `wireOnEnvironment with GraphQLScalarType`() {
//        val mockWiring = spyk<SchemaDirectiveWiring>()
//        val mockEnvironment: SchemaDirectiveWiringEnvironment<GraphQLScalarType> = mockk()
//        val mockElement: GraphQLScalarType = mockk()
//        every { mockEnvironment.element } returns mockElement
//
//        val result = mockWiring.wireOnEnvironment(mockEnvironment)
//        assertTrue(result is GraphQLScalarType)
//        verify(exactly = 1) { mockWiring.onScalar(mockEnvironment) }
//    }
//
//    @Test
//    fun `wireOnEnvironment with GraphQLUnionType`() {
//        val mockWiring = spyk<SchemaDirectiveWiring>()
//        val mockEnvironment: SchemaDirectiveWiringEnvironment<GraphQLUnionType> = mockk()
//        val mockElement: GraphQLUnionType = mockk()
//        every { mockEnvironment.element } returns mockElement
//
//        val result = mockWiring.wireOnEnvironment(mockEnvironment)
//        assertTrue(result is GraphQLUnionType)
//        verify(exactly = 1) { mockWiring.onUnion(mockEnvironment) }
//    }
// }
