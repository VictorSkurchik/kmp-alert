plugins {
    id("blealert.kmp.feature")
}

kotlin {
    android {
        namespace = "by.vsdev.blealert.ui"
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.compose.uiTooling)
        }
        commonMain.dependencies {
            api(project(":core-domain"))
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.compose.material.iconsExtended)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}

// core-ui's Res needs to be consumable from feature-* modules (e.g. no_alerts_yet).
compose.resources {
    publicResClass = true
}
