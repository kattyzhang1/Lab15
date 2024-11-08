plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.google.devrel.Katty_Zhang_LAB143"
    compileSdk = 34
    buildToolsVersion = "30.0.3"

    aaptOptions {
        noCompress += "tflite" // Mantiene el modelo sin comprimir
    }

    defaultConfig {
        applicationId = "com.google.devrel.Katty_Zhang_LAB143"
        minSdk = 23
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // ML Kit para etiquetado de imágenes personalizadas
    implementation("com.google.mlkit:image-labeling-custom:16.3.1")

    // TensorFlow Lite, opcional si decides cambiar de ML Kit a TensorFlow Lite directamente
    implementation("org.tensorflow:tensorflow-lite:2.10.0")
}
