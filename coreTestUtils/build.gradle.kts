plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

group = "dev.toolkt"

kotlin {
    jvm()

    js(IR) {
        browser()
        nodejs()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.coroutines.test)
            implementation(project(":core"))
        }
    }
}
