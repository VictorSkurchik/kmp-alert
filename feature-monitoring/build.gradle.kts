plugins {
    id("blealert.kmp.feature")
}

kotlin {
    android {
        namespace = "by.vsdev.blealert.feature.monitoring"
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":core-domain"))
            implementation(project(":core-ui"))
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
        }
    }
}
