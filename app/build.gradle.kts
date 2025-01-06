plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
    alias(libs.plugins.google.firebase.crashlytics)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.aptitude"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.aptitude"
        minSdk = 33
        targetSdk = 33
        versionCode = 1
        versionName = "5.0beta"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        packagingOptions {
            exclude("META-INF/DEPENDENCIES")
            exclude("META-INF/LICENSE")
            exclude("META-INF/LICENSE.txt")
            exclude("META-INF/NOTICE")
            exclude("META-INF/NOTICE.txt")
        }
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
}

dependencies {

    // Core Android Libraries
    implementation(libs.appcompat)
    implementation(libs.material) // Check its version in your libs if needed.
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation(libs.places)

    // Firebase BOM (Manages Firebase dependencies)
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage:20.2.0")// Add only the necessary Firebase dependencies (if used with Firebase)
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-analytics")

    // Google Play Services
    implementation("com.google.android.gms:play-services-auth:20.4.0")
    implementation("com.google.android.gms:play-services-maps:17.0.1")

    // Glide (For image loading)
    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation(libs.common)
    implementation(libs.core.ktx)
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")

    // Volley (For network requests)
    implementation("com.android.volley:volley:1.2.1")

    // Testing Libraries
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation(libs.junit.v115)
    androidTestImplementation(libs.espresso.core.v351)

    implementation ("com.squareup.okhttp3:okhttp:4.9.3")
    implementation ("org.apache.pdfbox:pdfbox:2.0.27")
    implementation ("org.apache.poi:poi:5.2.2")
    implementation ("org.apache.poi:poi-ooxml:5.2.2")

    implementation("com.karumi:dexter:6.2.3")

    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")
    implementation("org.json:json:20231013")
}
