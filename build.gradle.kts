import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.31"
    antlr
}

version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation("net.pearx.kasechange", "kasechange-jvm", "1.0.9")

    antlr("org.antlr", "antlr4", "4.7")
    implementation("org.antlr", "antlr4-runtime", "4.7")

    testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.3.1")
    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", "5.3.1")

    testCompile("org.mockito", "mockito-core", "2.1.0")
}

val generatedSrc = "build/generated-src"

tasks.withType<AntlrTask> {
    val grammarSrc = "$generatedSrc/antlr/main/larl/grammar"

    delete(grammarSrc)

    outputDirectory = file(grammarSrc)

    arguments.addAll(listOf(
        "-package", "larl.grammar"
    ))
}

tasks.withType<KotlinCompile> {
    dependsOn(":generateGrammarSource")

    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Test> {
    useJUnitPlatform()
}
