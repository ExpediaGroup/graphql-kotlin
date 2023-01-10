import com.github.tomakehurst.wiremock.standalone.WireMockServerRunner
import java.net.ServerSocket

buildscript {
    dependencies {
        classpath(libs.wiremock.standalone)
    }
}

open class WireMockIntegrationExtension {
    var wireMockServer: WireMockServerRunner? = null
    var port: Int? = null
}

val extension = project.extensions.create("WireMockIntegrationExtension", WireMockIntegrationExtension::class.java)
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

project(":client-examples:gradle-client-example") {
    ext.set("wireMockServerPort", extension.port)

    project.afterEvaluate {
        tasks.getByName("graphqlDownloadSDL") {
            dependsOn(":client-examples:startWireMock")
            finalizedBy(":client-examples:stopWireMock")
        }
    }
}

project(":client-examples:maven-client-example") {
    ext.set("wireMockServerPort", extension.port)

    project.afterEvaluate {
        tasks.getByName("mavenBuild") {
            dependsOn(":client-examples:startWireMock")
            finalizedBy(":client-examples:stopWireMock")
        }
    }
}
