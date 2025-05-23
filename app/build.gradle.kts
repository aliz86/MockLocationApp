plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.dataliz.gpsmock"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.dataliz.gpsmock"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
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
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {


    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.1")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")


    //Compose Navcontroller
    implementation("androidx.navigation:navigation-compose:2.8.3")

    //Animation
    implementation ("com.airbnb.android:lottie-compose:5.2.0")

    implementation ("androidx.compose.runtime:runtime-livedata:1.7.4")

    implementation ("androidx.lifecycle:lifecycle-extensions:2.2.0")
    kapt ("androidx.lifecycle:lifecycle-common-java8:2.8.6")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.8.6")

    // for collectAsStateWithLifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.6")

    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")

    // Dagger 2
    implementation("com.google.dagger:dagger:2.49")
    kapt("com.google.dagger:dagger-compiler:2.49")


    // Google Maps Compose
    implementation("com.google.maps.android:maps-compose:2.11.4")
    implementation("com.google.android.gms:play-services-maps:19.0.0")

    implementation("com.google.android.gms:play-services-location:21.3.0")
}