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
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation("dev.toolkt:core")
            implementation("dev.toolkt:reactive_impl")
        }
    }

    compilerOptions {
        freeCompilerArgs.addAll(
            listOf(
                // Consistent `copy` visibility
                // https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-consistent-copy-visibility/
                "-Xconsistent-data-class-copy-visibility",

                // Context parameters
                // https://kotlinlang.org/docs/context-parameters.html
                "-Xcontext-parameters",
            ),
        )
    }
}
