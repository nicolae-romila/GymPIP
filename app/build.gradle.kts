plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")  // Firebase plugin
}

android {
    namespace = "com.example.gympip"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.gympip"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // AndroidX libraries
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Firebase (using BOM for consistent versions)
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))  // Updated BOM version
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-firestore")

    // FirebaseUI (make sure version matches BOM)
    implementation("com.firebaseui:firebase-ui-database:8.0.2")
    implementation ("com.google.android.gms:play-services-location:21.0.1")
    implementation ("com.google.android.gms:play-services-maps:18.2.0")
    implementation ("com.google.firebase:firebase-storage:20.3.0")
    implementation ("com.squareup.picasso:picasso:2.71828")

}