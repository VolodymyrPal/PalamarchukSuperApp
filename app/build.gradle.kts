import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.safeargsKotlin)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinx.serialization)
}

android {
    namespace = "com.hfad.palamarchuksuperapp"
    compileSdk = 34

    val groqKey: String = gradleLocalProperties(rootDir, providers).getProperty("GROQ_KEY")
    val openAiKey: String = gradleLocalProperties(rootDir, providers).getProperty("OPEN_AI_KEY_USER")
    val geminiKey: String = gradleLocalProperties(rootDir, providers).getProperty("GEMINI_AI_KEY")

    defaultConfig {
        applicationId = "com.hfad.palamarchuksuperapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "0.6" //TODO update on release to follow version name in play market

        buildConfigField("String", "GROQ_KEY", groqKey)
        buildConfigField("String", "OPEN_AI_KEY_USER", openAiKey)
        buildConfigField("String", "GEMINI_AI_KEY", geminiKey)

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
        compose = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
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
composeCompiler {
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
    //stabilityConfigurationFile = rootProject.layout.projectDirectory.file("stability_config.conf")
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
    ksp(libs.dagger.compiler)

    implementation(libs.coil)
    implementation(libs.coil.compose)

    implementation(libs.zelory.compressor)

    implementation(libs.compose.tracing)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.room)
    implementation(libs.room.ktx)
    annotationProcessor(libs.room.annotation)
    //noinspection KaptUsageInsteadOfKsp
    ksp(libs.room.annotation)

    implementation(libs.moshi.kotlin)
    implementation(libs.converter.moshi)
    ksp(libs.moshi.kotlin.codegen)


    implementation(libs.numberpicker)

    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.cio)
    implementation(libs.app.update)
    implementation(libs.app.update.ktx)

    implementation(libs.kotlinx.collections.immutable) // Immutable collection for better compose handling

    implementation("androidx.compose.animation:animation:1.7.4")
    implementation("io.ktor:ktor-client-logging:2.3.12")

}