package com.expedia.graphql.schema.exceptions

class TypeNotSupportedException(typeName: String, packageName: String) :
    RuntimeException("Cannot convert $typeName since it is outside the supported package $packageName")
