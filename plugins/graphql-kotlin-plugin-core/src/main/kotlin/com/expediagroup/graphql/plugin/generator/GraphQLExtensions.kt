package com.expediagroup.graphql.plugin.generator

import graphql.language.Node

internal fun Node<*>.graphQLComments(): String? = if (comments.isEmpty()) {
    null
} else {
    this.comments.joinToString("\n") { it.content }
}
