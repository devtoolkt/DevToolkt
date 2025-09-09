import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsSubTargetDsl

plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.allopen)
    alias(libs.plugins.kotlinx.benchmark)
}

group = "dev.toolkt"

kotlin {
    jvm()

    js(IR) {
        browser {
            testWithExtendedTimeout()
        }

        nodejs {
            testWithExtendedTimeout()
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.benchmark.runtime)
            implementation(libs.kotlinx.coroutines.core)
        }

        commonTest.dependencies {
            implementation(project(":coreTestUtils"))
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }

        jsMain.dependencies {
            implementation(project(":jsApiCompat"))
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

fun KotlinJsSubTargetDsl.testWithExtendedTimeout() {
    testTask {
        useMocha {
            timeout = "15s"
        }
    }
}
