plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinx.serialization)
    id("de.mannodermaus.android-junit5") version "1.13.0.0"
}

android {
    namespace = "com.hfad.palamarchuksuperapp.feature.bone"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures {
        compose = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    ksp(libs.dagger.compiler)
    implementation(libs.bundles.networking)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.database)

    implementation("androidx.biometric:biometric-ktx:1.4.0-alpha02")
    implementation("androidx.security:security-crypto:1.0.0")

//    testImplementation("org.junit.jupiter:junit-jupiter-api:5.13.2")
//    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.13.2")
//    testImplementation("org.junit.jupiter:junit-jupiter-params:5.13.2")
//    testImplementation("junit:junit:4.13.2")
//    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.13.0")


//    testImplementation(libs.bundles.test)
//    testRuntimeOnly(libs.junit.platform.launcher)
//    testImplementation(libs.mockk)
//    testImplementation("androidx.test:core:${androidXTestVersion}")
//    testImplementation("io.mockk:mockk:${mockkVersion}")

    implementation(project(":core"))
}