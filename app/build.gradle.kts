plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("kapt")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22" // تأكد من تطابق إصدار الكوتلن
}

android {
    namespace = "com.seif.salatukalyawm"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.seif.salatukalyawm"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8" // Updated for compatibility
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    kapt(libs.androidx.room.compiler)
    implementation(libs.androidx.room.common.jvm)
    implementation(libs.androidx.room.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.2")
    // Location Services
    implementation("com.google.android.gms:play-services-location:21.2.0") // تأكد من استخدام أحدث إصدار
    // Accompanist for Permissions
    implementation("com.google.accompanist:accompanist-permissions:0.34.0") // تأكد من استخدام أحدث إصدار
    // Retrofit for networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // OkHttp for logging
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
    // Kotlinx Serialization converter for Retrofit
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")
    // Kotlinx Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    // Jetpack DataStore for saving settings
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    // Coil for image loading
    implementation("io.coil-kt:coil-compose:2.6.0") // Updated to latest version
    implementation("io.coil-kt:coil:2.6.0")

    implementation("com.google.accompanist:accompanist-flowlayout:0.32.0") // <-- أضف هذا السطر



}

configurations.all {
    exclude(group = "com.intellij", module = "annotations")
}