// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    kotlin("plugin.serialization") version "1.9.23"
    alias(libs.plugins.compose.compiler) apply false
    id("com.google.gms.google-services") version "4.4.3" apply false


}