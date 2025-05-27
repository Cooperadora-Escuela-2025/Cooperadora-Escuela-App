plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.cooperadora_escuela"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.cooperadora_escuela"
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

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.recyclerview)
    implementation(libs.cardview)
    implementation("androidx.core:core:1.10.1")
    implementation ("com.google.android.material:material:1.9.0")
    implementation ("androidx.appcompat:appcompat:1.6.1")

    // Retrofit  realizar llamadas HTTP
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")

// Gson para convertir objetos Java a JSON y viceversa
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

// Logging para ver lo que Retrofit manda y recibe
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.1")

//Para cifrar la info del user
    implementation ("androidx.security:security-crypto:1.1.0-alpha06")

}