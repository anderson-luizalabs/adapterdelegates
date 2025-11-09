plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kover)
}

android {
    namespace = "com.hannesdorfmann.adapterdelegates4.sample"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.hannesdorfmann.adapterdelegates4.sample"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlinOptions {
        jvmTarget = "21"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = false
    }

    lint {
        checkReleaseBuilds = false
        disable += "GoogleAppIndexingWarning"
        abortOnError = false
    }
}

dependencies {
    // AdapterDelegates modules
    implementation(project(":library"))
    implementation(project(":paging"))
    implementation(project(":kotlin-dsl"))
    implementation(project(":kotlin-dsl-viewbinding"))

    // AndroidX
    implementation(libs.bundles.androidx)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.livedata)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.androidx.activity) // Edge-to-Edge support

    // Kotlin
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines.android)

    // Image loading
    implementation(libs.glide)

    // Testing
    testImplementation(libs.bundles.testing.unit)
    androidTestImplementation(libs.bundles.testing.android)
}
