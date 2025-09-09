import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

val kotlinMultiplatformPluginId = "org.jetbrains.kotlin.multiplatform"

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
}

subprojects {
    // Use Maven Central repository for all subprojects
    repositories {
        mavenCentral()
    }

    // Enable some experimental Kotlin features for all subprojects
    plugins.withId(kotlinMultiplatformPluginId) {
        configure<KotlinMultiplatformExtension> {
            compilerOptions {
                optIn.addAll(
                    // Experimental JS-collections API
                    // https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.js/-experimental-js-collections-api/
                    "kotlin.js.ExperimentalJsCollectionsApi",
                )

                freeCompilerArgs.addAll(
                    listOf(
                        // Consistent `copy` visiblity
                        // https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-consistent-copy-visibility/
                        "-Xconsistent-data-class-copy-visibility",

                        // Expected and actual declarations﻿
                        // https://www.jetbrains.com/help/kotlin-multiplatform-dev/multiplatform-expect-actual.html
                        "-Xexpect-actual-classes",

                        // Context parameters
                        // https://kotlinlang.org/docs/context-parameters.html
                        "-Xcontext-parameters",
                    ),
                )
            }
        }
    }
}
