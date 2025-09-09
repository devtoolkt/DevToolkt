plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

group = "dev.toolkt"

kotlin {
    js(IR) {
        browser {}
        nodejs {}
    }

    sourceSets {
        jsTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
