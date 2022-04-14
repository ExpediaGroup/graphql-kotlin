package com.expediagroup.graphql.dataloader.instrumentation.fixture.extensions

import java.util.Optional

internal fun <T : Any> List<Optional<T>>.toListOfNullables(): List<T?> = map { optional ->
    when {
        optional.isPresent -> optional.get()
        else -> null
    }
}
