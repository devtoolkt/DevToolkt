val kotlinMultiplatformPluginId = "org.jetbrains.kotlin.multiplatform"

plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

repositories {
    mavenCentral()
}

kotlin {
    jvm()

    js(IR) {
        browser()
        nodejs()
    }

    sourceSets {
        commonMain.dependencies {
            implementation("dev.toolkt:core")
            implementation("dev.toolkt:reactive_impl")
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }

    compilerOptions {
        optIn.addAll(
            // Experimental JS-collections API
            // https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.js/-experimental-js-collections-api/
            "kotlin.js.ExperimentalJsCollectionsApi",
        )

        freeCompilerArgs.addAll(
            listOf(
                // Consistent `copy` visibility
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
