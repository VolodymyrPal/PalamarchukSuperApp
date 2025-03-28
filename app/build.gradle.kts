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
    compileSdk = 35

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

    //Base block
    implementation(libs.bundles.base)
    implementation(libs.material)

    //Test block
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Database bundles
    implementation(libs.bundles.database)
    annotationProcessor(libs.room.annotation)
    ksp(libs.room.annotation)

    // Network bundles
    implementation(libs.bundles.networking)

    // Image loading bundle
    implementation(libs.bundles.image.loading)

    // Navigation bundle
    implementation(libs.bundles.navigation)

    // Compose bundle
    implementation(libs.bundles.compose)
    val composeBom = platform("androidx.compose:compose-bom:2025.03.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // ViewModel bundle
    implementation(libs.bundles.viewmodel)


    implementation(libs.recyclerview)
    implementation(libs.recyclerview.selection)

    implementation(libs.dagger)
    ksp(libs.dagger.compiler)

    implementation(libs.compose.tracing)

    implementation(libs.numberpicker)

    implementation(libs.app.update)
    implementation(libs.app.update.ktx)

    implementation(libs.kotlinx.collections.immutable) // Immutable collection for better compose handling

    implementation("io.github.theapache64:rebugger:1.0.0-rc03")   //TODO using for checking number of recompositions
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.2")
    implementation("com.github.jeziellago:compose-markdown:0.5.7")

    implementation(project(":feature_bone"))
    implementation(project(":core"))

}