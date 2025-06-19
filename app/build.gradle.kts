plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.sre"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.abc.sre"
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

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.database)
    implementation(libs.lifecycle.viewmodel.android)
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.preference)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // Exclude org.jetbrains:annotations-java5 dependency from libraries that bring it in
    implementation("androidx.core:core-ktx:1.9.0") {
        exclude(group = "org.jetbrains", module = "annotations-java5")
    }
    implementation("androidx.appcompat:appcompat:1.5.1") {
        exclude(group = "org.jetbrains", module = "annotations-java5")
    }
    implementation("com.github.mukeshsolanki:android-otpview-pinview:2.1.2") {
        exclude(group = "org.jetbrains", module = "annotations-java5")
    }
    implementation("com.github.wRorsjakz:Android-NumPad:1.0.1") {
        exclude(group = "org.jetbrains", module = "annotations-java5")
    }

    // You can also exclude annotations if needed
    implementation("androidx.core:core-ktx:1.9.0") {
        exclude(group = "org.jetbrains", module = "annotations")
    }

    implementation(platform("com.google.firebase:firebase-bom:33.8.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")

    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    implementation ("com.google.android.material:material:1.11.0")

        implementation("org.osmdroid:osmdroid-android:6.1.14")



}