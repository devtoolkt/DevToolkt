plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

group = "dev.toolkt"

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
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
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
