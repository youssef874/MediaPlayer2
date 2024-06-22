@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
    kotlin("kapt")
}

android {
    namespace = "com.example.mpcore"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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

    testOptions.unitTests.isIncludeAndroidResources = true
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.android.startup)
    implementation(libs.androidx.rules)
    kapt("androidx.room:room-compiler:2.6.1")
    implementation(libs.androidx.room)
    implementation(libs.room.ktx)
    implementation(libs.data.store.core)
    implementation(libs.data.store.preference.core)
    implementation(libs.data.store.preference)
    testImplementation(libs.junit)
    testImplementation(libs.roboletric)
    testImplementation(libs.mokito)
    testImplementation (libs.test.coroutine)
    testImplementation(libs.room.test)
    testImplementation(libs.androidx.room)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.test.coroutine)
}
