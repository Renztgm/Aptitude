// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}

buildscript {
    repositories {
        google() // Required for Firebase
        mavenCentral()
    }
    dependencies {
        classpath ("com.android.tools.build:gradle:8.1.2")
        classpath ("com.google.gms:google-services:4.3.15") // Firebase plugin
    }
}
