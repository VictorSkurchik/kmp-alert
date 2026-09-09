plugins {
    id("blealert.kmp.feature")
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "SharedUI"
            isStatic = true
        }
    }

    android {
       namespace = "by.vsdev.blealert.sharedUI"

       withDeviceTestBuilder {
           sourceSetTreeName = "test"
       }.configure {
           instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
       }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.compose.uiTooling)
        }
        commonMain.dependencies {
            implementation(project(":core-domain"))
            implementation(project(":core-ui"))
            implementation(project(":core-data"))
            implementation(project(":feature-dashboard"))
            implementation(project(":feature-monitoring"))
            implementation(project(":feature-settings"))
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.jetbrains.navigation3.ui)
            implementation(libs.kotlinx.serialization.core)
            implementation(libs.compose.material.iconsExtended)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}
