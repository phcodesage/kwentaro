plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

import java.util.Properties

// Single source of truth for the app version: `appVersion` in gradle.properties.
// versionCode is derived from it (1.2.3 -> 10203), so it always increases with semver.
val appVersion = providers.gradleProperty("appVersion").get()
val semver = Regex("""(\d+)\.(\d+)\.(\d+)""").matchEntire(appVersion)
    ?: error("appVersion '$appVersion' must be MAJOR.MINOR.PATCH")
val (major, minor, patch) = semver.destructured.toList().map(String::toInt)
require(minor < 100 && patch < 100) { "minor and patch must be < 100 to keep versionCode monotonic" }
val appVersionCode = major * 10_000 + minor * 100 + patch

val gitSha: String = providers.exec {
    commandLine("git", "rev-parse", "--short", "HEAD")
    isIgnoreExitValue = true
}.standardOutput.asText.map { it.trim().ifEmpty { "unknown" } }.getOrElse("unknown")

// Release signing comes from keystore.properties locally, or KWENTARO_* env vars in CI.
val signing = Properties().apply {
    rootProject.file("keystore.properties").takeIf { it.exists() }?.inputStream()?.use(::load)
    System.getenv("KWENTARO_KEYSTORE")?.let {
        setProperty("storeFile", it)
        setProperty("storePassword", System.getenv("KWENTARO_KEYSTORE_PASSWORD"))
        setProperty("keyAlias", System.getenv("KWENTARO_KEY_ALIAS"))
        setProperty("keyPassword", System.getenv("KWENTARO_KEY_PASSWORD"))
    }
}

base {
    archivesName = "kwentaro-v$appVersion"
}

android {
    namespace = "com.phcodesage.kwentaro"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.phcodesage.kwentaro"
        minSdk = 26
        targetSdk = 36
        versionCode = appVersionCode
        versionName = appVersion
        buildConfigField("String", "GIT_SHA", "\"$gitSha\"")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        if (signing.getProperty("storeFile") != null) create("release") {
            storeFile = rootProject.file(signing.getProperty("storeFile"))
            storePassword = signing.getProperty("storePassword")
            keyAlias = signing.getProperty("keyAlias")
            keyPassword = signing.getProperty("keyPassword")
        }
    }

    buildTypes {
        debug {
            versionNameSuffix = "-debug"
        }
        release {
            signingConfig = signingConfigs.findByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

kotlin {
    jvmToolchain(17)
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.datastore.preferences)

    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.mlkit.vision)
    implementation(libs.mlkit.barcode.scanning)

    implementation(libs.coil.compose)
    implementation(libs.lottie.compose)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.kotlinx.coroutines.play.services)
}

tasks.register("printVersion") {
    val name = appVersion
    val code = appVersionCode
    doLast { println("$name ($code)") }
}
