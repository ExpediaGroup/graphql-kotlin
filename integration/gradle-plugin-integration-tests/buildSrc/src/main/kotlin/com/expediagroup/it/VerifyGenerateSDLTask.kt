package com.expediagroup.it

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction

abstract class VerifyGenerateSDLTask : DefaultTask() {

    @get:InputFile
    abstract val actualSchema: RegularFileProperty

    @get:InputFile
    abstract val expectedSchema: RegularFileProperty

    @TaskAction
    fun test() {
        if (!actualSchema.get().asFile.exists()) {
            throw RuntimeException("schema file was not generated")
        }

        val expected = expectedSchema.get().asFile.readText()
        val actual = actualSchema.get().asFile.readText()
        if (expected != actual) {
            throw RuntimeException("generated schema file was different.\n---expected---\n$expected\n\n---actual---\n$actual")
        }
    }
}
