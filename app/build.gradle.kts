plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.safeargsKotlin)
    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "com.hfad.palamarchuksuperapp"
    compileSdk = 34


    defaultConfig {
        applicationId = "com.hfad.palamarchuksuperapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.databinding.runtime)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.androidx.datastore.preferences)

    val composeBom = platform("androidx.compose:compose-bom:2024.03.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation(libs.compose.material3)
    implementation(libs.compose.ui.preview)
    debugImplementation(libs.compose.ui)
    implementation(libs.activity.compose)
    implementation(libs.viewmodel)
    implementation(libs.viemodel.compose)
    implementation(libs.compose.runtime)
    implementation(libs.compose.graphics)
    implementation(libs.runtime.compose)
    implementation(libs.constraintlayout.compose)

    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.navigation.compose)
    implementation(libs.framgent.navigation)

    implementation(libs.recyclerview)
    implementation(libs.recyclerview.selection)

    implementation(libs.dagger)
    kapt(libs.dagger.compiler)
}