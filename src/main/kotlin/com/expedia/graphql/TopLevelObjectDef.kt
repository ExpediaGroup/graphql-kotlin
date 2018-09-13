package com.expedia.graphql

import kotlin.reflect.KClass

class TopLevelObjectDef(val obj: Any, val klazz: KClass<*> = obj::class)
