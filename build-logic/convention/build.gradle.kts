plugins {
    `kotlin-dsl`
}

group = "by.vsdev.blealert.buildlogic"

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    // `implementation`, not `compileOnly`: these plugins are applied by id()
    // in consuming projects, so they must be on the convention plugin's
    // runtime classpath, not just available at compile time.
    implementation("com.android.tools.build:gradle:${libs.versions.agp.get()}")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.versions.kotlin.get()}")
    implementation("org.jetbrains.kotlin:kotlin-serialization:${libs.versions.kotlin.get()}")
    implementation("org.jetbrains.kotlin:compose-compiler-gradle-plugin:${libs.versions.kotlin.get()}")
    implementation("org.jetbrains.compose:compose-gradle-plugin:${libs.versions.composeMultiplatform.get()}")
}

// Plugin ids are derived automatically from the file names under
// src/main/kotlin/*.gradle.kts (precompiled script plugins) — no need to
// register them manually in a `gradlePlugin { plugins { ... } }` block.
