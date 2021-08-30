package com.expediagroup.graphql.plugin.gradle

import org.junit.jupiter.params.provider.Arguments
import java.io.File

internal fun locateTestCaseArguments(directory: String) = File(directory)
    .listFiles()
    ?.filter { it.isDirectory }
    ?.map {
        Arguments.of(it)
    } ?: emptyList()
