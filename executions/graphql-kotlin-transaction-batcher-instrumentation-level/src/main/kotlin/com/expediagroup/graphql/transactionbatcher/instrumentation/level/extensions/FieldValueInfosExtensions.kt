package com.expediagroup.graphql.transactionbatcher.instrumentation.level.extensions

import graphql.execution.FieldValueInfo

fun List<FieldValueInfo>.getExpectedStrategyCalls(): Int {
    var count = 0
    this.forEach { fieldValueInfo ->
        if (fieldValueInfo.completeValueType == FieldValueInfo.CompleteValueType.OBJECT)
            count++
        else if (fieldValueInfo.completeValueType == FieldValueInfo.CompleteValueType.LIST)
            count += fieldValueInfo.fieldValueInfos.getExpectedStrategyCalls()
    }
    return count
}
