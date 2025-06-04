// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" apply false
    id("com.google.devtools.ksp") version "2.0.21-1.0.26" apply false
    id ("com.google.dagger.hilt.android") version "2.56" apply false
    id ("androidx.navigation.safeargs.kotlin") version "2.7.7" apply false
    alias(libs.plugins.detekt)
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.compose.compiler.plugin) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.crashlytics) apply false
}

private val versionMajor = 1
private val versionMinor = 0

val versionName by extra(initialValue = "$versionMajor.$versionMinor")
val versionCode by extra(initialValue = versionMajor * 1000 + versionMinor * 10)