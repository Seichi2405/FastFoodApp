plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.fastfood"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.fastfood"
        minSdk = 27
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // AndroidX libraries
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.navigation:navigation-fragment:2.7.6")
    implementation("androidx.navigation:navigation-ui:2.7.6")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("androidx.cardview:cardview:1.0.0")
    implementation ("androidx.recyclerview:recyclerview:1.3.2")



// Firebase dependencies
    implementation(platform("com.google.firebase:firebase-bom:32.6.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-core:10.2.0")
    implementation("com.google.firebase:firebase-database:10.2.0")
    implementation("com.firebaseui:firebase-ui-database:8.0.0")
    implementation("com.paypal.sdk:paypal-android-sdk:2.16.0")
// thu vien khac
    implementation("com.github.mancj:MaterialSearchBar:0.7.1")
    implementation("info.hoang8f:fbutton:1.0.5")
    implementation("com.rengwuxian.materialedittext:library:2.1.4")
    implementation("com.squareup.picasso:picasso:2.71828")
    implementation("com.amulyakhare:com.amulyakhare.textdrawable:1.0.1")
    implementation("com.readystatesoftware.sqliteasset:sqliteassethelper:+")




}