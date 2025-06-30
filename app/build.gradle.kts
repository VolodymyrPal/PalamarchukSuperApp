import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

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
    kotlin {
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_21
            targetCompatibility = JavaVersion.VERSION_21
        }
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }
}
composeCompiler {
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
    //stabilityConfigurationFile = rootProject.layout.projectDirectory.file("stability_config.conf")
}

dependencies {

    // Modules block
    implementation(project(":feature_bone"))
    implementation(project(":core"))

    //Test block

    // Database bundles
    implementation(libs.bundles.database)
    annotationProcessor(libs.room.annotation)
    ksp(libs.room.annotation)

    // Network bundles
    implementation(libs.bundles.networking)

    // Image loading bundle
    implementation(libs.bundles.image.loading)

    // XML bundles
    implementation(libs.bundles.xml)

    // Compose bundles
    implementation(libs.bundles.compose)

    ksp(libs.dagger.compiler)

    implementation(libs.compose.tracing)

    implementation(libs.numberpicker)

    implementation(libs.app.update)
    implementation(libs.app.update.ktx)

    implementation(libs.kotlinx.collections.immutable) // Immutable collection for better compose handling

    implementation("io.github.theapache64:rebugger:1.0.0-rc03")   //TODO using for checking number of recompositions
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.2")
    implementation("com.github.jeziellago:compose-markdown:0.5.7")

}