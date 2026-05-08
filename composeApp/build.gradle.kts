import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.kotlinCompose)
}

val localProps = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) {
        f.inputStream().use { load(it) }
    }
}

val debugApiBaseUrl = (
    localProps.getProperty("MOBILE_DEBUG_API_BASE_URL")
        ?: (findProperty("MOBILE_DEBUG_API_BASE_URL") as String?)
    )
    ?: "http://10.0.2.2:8081"
val releaseApiBaseUrl = (
    localProps.getProperty("MOBILE_RELEASE_API_BASE_URL")
        ?: (findProperty("MOBILE_RELEASE_API_BASE_URL") as String?)
    )
    ?: "https://api.derdimet.com"

android {
    namespace = "com.derdimet.mobil.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.derdimet.mobil"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            buildConfigField("String", "API_BASE_URL", "\"$debugApiBaseUrl\"")
        }
        release {
            isMinifyEnabled = false
            buildConfigField("String", "API_BASE_URL", "\"$releaseApiBaseUrl\"")
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
}

dependencies {
    implementation(project(":shared"))
    implementation(libs.androidx.activity.compose)
}
