plugins {
    id("blealert.kmp.library")
}

kotlin {
    jvm()

    android {
        namespace = "by.vsdev.blealert.domain"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.core)
        }
    }
}
