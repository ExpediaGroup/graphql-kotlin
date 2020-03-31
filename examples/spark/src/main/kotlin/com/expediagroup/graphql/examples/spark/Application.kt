/**
 * Copyright 2020 Expedia, Inc
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
package com.expediagroup.graphql.examples.spark

import org.apache.log4j.PropertyConfigurator
import org.slf4j.LoggerFactory
import spark.Spark.before
import spark.Spark.get
import spark.Spark.initExceptionHandler
import spark.Spark.internalServerError
import spark.Spark.options
import spark.Spark.port
import spark.Spark.post
import java.util.Properties

class Application {
    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)

        @JvmStatic
        fun main(args: Array<String>) {
            initLogger()
            port(5000)
            defineRoutes()
        }

        private fun initLogger() {
            val logProperties = Properties()
            logProperties.setProperty("log4j.rootLogger", "DEBUG,CONSOLE")
            logProperties.setProperty("log4j.appender.CONSOLE", "rg.apache.log4j.ConsoleAppender")
            logProperties.setProperty("log4j.appender.CONSOLE.layout", "rg.apache.log4j.PatternLayout")
            logProperties.setProperty("log4j.appender.CONSOLE.layout.conversionPattern", "%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%m%n")
            PropertyConfigurator.configure(logProperties)
        }

        private fun defineRoutes() {
            initExceptionHandler { e ->
                log.error(e.message)
                System.exit(100)
            }

            get("/hello") { _, _ -> "Hello, World" }

            val graphQLHandler = GraphQLHandler()
            post("/graphql") { request, response ->
                graphQLHandler.handle(request, response)
            }

            internalServerError() { _, response ->
                response.status(500)
                response.type("application/text")
                "Unable to process request"
            }

            options("/graphql") { _, _ -> "ok" }

            before("/graphql") { request, response ->
                response.header("Access-Control-Allow-Origin", "*")
                response.header("Access-Control-Allow-Methods", "GET,POST,OPTIONS")
            }
        }
    }
}
