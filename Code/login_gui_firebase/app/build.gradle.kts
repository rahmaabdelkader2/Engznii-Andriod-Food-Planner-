
plugins {
//    alias(libs.plugins.android.application)
//    alias(libs.plugins.google.gms.google.services)
    //    alias(libs.plugins.android.application)
    id("com.android.application")

    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")


}

android {
    namespace = "com.example.login_gui_firebase"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.login_gui_firebase"
        minSdk = 31
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

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.firestore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation ("com.google.android.material:material:1.9.0")
//    // Firebase
//    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
//    implementation("com.google.firebase:firebase-auth")
//    implementation("com.google.firebase:firebase-firestore")
////    implementation("com.google.android.gms:play-services-auth:20.7.0")
//
//
//
//    // Retrofit
//    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
//    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
//
//    // Room
//    implementation ("androidx.room:room-runtime:2.7.0")
//    annotationProcessor ("androidx.room:room-compiler:2.7.0")
//
//    //Glide
//    implementation ("com.github.bumptech.glide:glide:4.16.0")
//    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")
    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.google.android.material:material:1.9.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation ("junit:junit:4.13.2")
    androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")

    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")

    implementation("com.airbnb.android:lottie:6.3.0")

    implementation ("androidx.room:room-runtime:2.7.0")
    annotationProcessor ("androidx.room:room-compiler:2.7.0")

    implementation ("com.google.firebase:firebase-auth:23.2.0")
    implementation ("com.google.android.gms:play-services-auth:21.3.0")
    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.12.0"))
    implementation("com.google.firebase:firebase-analytics")


    implementation("com.google.android.material:material:1.9.0")
}