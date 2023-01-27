import java.util.Properties
import com.github.tomakehurst.wiremock.standalone.WireMockServerRunner
import java.net.ServerSocket

buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath(libs.wiremock.standalone)
    }
}

allprojects {
    repositories {
        mavenCentral()
        mavenLocal {
            content {
                includeGroup("com.expediagroup")
            }
        }
    }

    val properties = Properties()
    properties.load(File(rootDir.parentFile.parent, "gradle.properties").inputStream())
    for ((key, value) in properties) {
        if (!project.ext.has(key.toString())) {
            project.ext[key.toString()] = value
        }
    }
}

open class WireMockIntegrationExtension {
    var wireMockServer: WireMockServerRunner? = null
    var port: Int? = null
}

val extension: WireMockIntegrationExtension = project.extensions.create("WireMockIntegrationExtension", WireMockIntegrationExtension::class.java)
project.extensions.configure(WireMockIntegrationExtension::class.java) {
    val serverSocket = ServerSocket(0)
    port = serverSocket.localPort
    serverSocket.close()
}

tasks {
    // workaround to be able to build clients without running server
    val startWireMock by register("startWireMock") {
        finalizedBy("stopWireMock")

        doLast {
            val wireMockConfig = arrayOf(
                "--root-dir=${project.projectDir}/src/integration/wiremock",
                "--port=${extension.port}"
            )
            val server = WireMockServerRunner()
            server.run(*wireMockConfig)

            extension.wireMockServer = server
            logger.info("wiremock started at port ${extension.port}")
        }
    }
    val stopWireMock by register("stopWireMock") {
        mustRunAfter("startWireMock")

        doLast {
            val server = extension.wireMockServer
            if (server?.isRunning == true) {
                logger.info("attempting to stop wiremock server")
                server.stop()
                logger.info("wiremock server stopped")
            }
        }
    }
}

for (projectName in listOf(":download-sdl-groovy-it", ":download-sdl-kotlin-it")) {
    project(projectName) {
        ext.set("wireMockServerPort", extension.port)

        project.afterEvaluate {
            tasks.getByName("graphqlDownloadSDL") {
                dependsOn(":startWireMock")
                finalizedBy(":stopWireMock")
            }
        }
    }
}

for (projectName in listOf(":introspection-groovy-it", ":introspection-kotlin-it")) {
    project(projectName) {
        ext.set("wireMockServerPort", extension.port)

        project.afterEvaluate {
            tasks.getByName("graphqlIntrospectSchema") {
                dependsOn(":startWireMock")
                finalizedBy(":stopWireMock")
            }
        }
    }
}
