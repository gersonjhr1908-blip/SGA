plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.sga.almacenes"

    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.sga.almacenes"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)

    implementation("androidx.room:room-runtime:2.8.3")
    annotationProcessor("androidx.room:room-compiler:2.8.3")
    implementation("androidx.recyclerview:recyclerview:1.3.2") /*guardar pro. aparece en pantalla*/

    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0") /*conectar api(retrofit)*/
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}