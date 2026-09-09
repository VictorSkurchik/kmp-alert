plugins {
    id("blealert.kmp.library")
}

kotlin {
    android {
        namespace = "by.vsdev.blealert.data"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core-domain"))
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.cio)
            implementation(libs.ktor.client.websockets)
            implementation(libs.koin.core)
        }
    }
}
