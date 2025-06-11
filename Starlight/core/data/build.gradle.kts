plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp")
    id ("com.google.dagger.hilt.android")
    alias(libs.plugins.detekt)
}

android {
    namespace = "ru.fav.starlight.data"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    kotlinOptions {
        jvmTarget = "11"
    }
}

detekt {
    toolVersion = libs.versions.detekt.get()
    config.setFrom(files("$rootDir/detekt/detekt.yml"))
    buildUponDefaultConfig = true
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    jvmTarget = "11"
}

dependencies {
    implementation(project(path = ":core:network"))
    implementation(project(path = ":core:domain"))

    implementation (libs.retrofit)
    implementation(libs.retrofit.gson.converter)
    implementation (libs.gson)
    implementation (libs.okhttp)

    implementation(libs.androidx.datastore.preferences)
    implementation(libs.hilt)
    ksp(libs.hilt.compiler)
}