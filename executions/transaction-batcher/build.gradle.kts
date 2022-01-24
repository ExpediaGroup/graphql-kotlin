description = "Transaction Batcher"

val reactiveStreamsVersion: String by project
val junitVersion: String by project
val slf4jVersion: String by project
val mockkVersion: String by project
val reactorVersion: String by project
val reactorExtensionsVersion: String by project

dependencies {
    implementation("org.reactivestreams:reactive-streams:$reactiveStreamsVersion")
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("io.projectreactor:reactor-core:$reactorVersion")
    testImplementation("io.projectreactor.kotlin:reactor-kotlin-extensions:$reactorExtensionsVersion")
    testImplementation("io.projectreactor:reactor-test:$reactorVersion")
}
