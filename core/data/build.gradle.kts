plugins {

    id("com.android.library")
    id(libs.plugins.kotlin.android.get().pluginId)
    id("org.jetbrains.kotlin.kapt")
    id("template.coroutines")
    id(libs.plugins.kotlin.serialization.get().pluginId)
}
android {
    compileSdk = libs.versions.sdk.compile.get().toInt()

    defaultConfig {
        minSdk = libs.versions.sdk.min.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    namespace = "com.vlamik.retask.data"
}
dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:commons"))
    implementation(libs.datastore)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)
    implementation(libs.bundles.javax)

}
