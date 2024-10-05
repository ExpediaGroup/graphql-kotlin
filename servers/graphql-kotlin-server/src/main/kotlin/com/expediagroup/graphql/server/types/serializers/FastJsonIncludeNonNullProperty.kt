package com.expediagroup.graphql.server.types.serializers

import com.alibaba.fastjson2.filter.PropertyFilter

class FastJsonIncludeNonNullProperty : PropertyFilter {
    override fun apply(
        `object`: Any?,
        name: String?,
        value: Any?,
    ): Boolean = value != null
}
