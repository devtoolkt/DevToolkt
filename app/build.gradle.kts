@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.allopen)
    alias(libs.plugins.kotlinx.benchmark)
}

repositories {
    mavenCentral()
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll(
            listOf(
                "-Xexpect-actual-classes",
            )
        )
    }

    jvm()

    js(IR) {
        browser()
        nodejs()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.benchmark.runtime)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        jvmMain.dependencies {}

        jvmTest.dependencies {}
    }
}

benchmark {
    targets {
        register("jvm")
        register("js")
    }

    configurations {
        named("main") {
            warmups = 2
            iterations = 4
            iterationTime = 1
            iterationTimeUnit = "s"
        }
    }
}

allOpen {
    annotation("org.openjdk.jmh.annotations.State")
}
