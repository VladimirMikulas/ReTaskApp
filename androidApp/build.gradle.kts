plugins {
    id(libs.plugins.android.application.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
    kotlin("kapt")
    id(libs.plugins.hilt.android.get().pluginId)
    id("template.coroutines")
    alias(libs.plugins.compose.compiler)
}

android {
    compileSdk = libs.versions.sdk.compile.get().toInt()
    defaultConfig {
        applicationId = "com.vlamik.retask"

        minSdk = libs.versions.sdk.min.get().toInt()
        targetSdk = libs.versions.sdk.target.get().toInt()

        versionCode = 1
        versionName = "1.0"
        buildFeatures {
            compose = true
        }
        composeOptions {
            kotlinCompilerExtensionVersion = "1.7.5"
        }

        testInstrumentationRunner = "com.vlamik.retask.TestRunner"
        vectorDrawables { useSupportLibrary = true }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    hilt { enableAggregatingTask = true }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
        )
    }

    kotlin {
        jvmToolchain(17)
    }

    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
            excludes.add("/META-INF/LICENSE*")
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
    namespace = "com.vlamik.retask"
}

dependencies {
    // Project Dependencies
    implementation(project(":core:domain"))
    implementation(project(":core:commons"))

    // Dependencies
    // Compose
    implementation(libs.bundles.compose)
    implementation(libs.hilt.navigation.compose)
    // Hilt
    implementation(libs.hilt.android)
    implementation(project(":core:data"))
    kapt(libs.hilt.compiler)


    // SplashScreen
    implementation(libs.splashscreen)

    // Datastore
    implementation(libs.datastore.android)

    //Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)

    // Debug Dependencies
    debugImplementation(libs.bundles.compose.debug)

    // Android Test Dependencies
    androidTestImplementation(libs.hilt.test)
    kaptAndroidTest(libs.hilt.test.compiler)

    androidTestImplementation(libs.test.core)
    androidTestImplementation(libs.bundles.compose.test)

    // Test Dependencies
    testImplementation(libs.bundles.test.core)
    testImplementation(libs.androidx.junit.ktx)
    testImplementation(libs.mockk.agent)
    testImplementation(libs.mockk.android)
}
