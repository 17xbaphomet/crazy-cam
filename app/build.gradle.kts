plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.github.willir.rust.cargo-ndk-android-gradle") version "0.8.5"
}

android {
    namespace = "com.example.crazycam"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.crazycam"
        minSdk = 26
        targetSdk = 35
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
        kotlinCompilerExtensionVersion = "1.7.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

cargoNdk {
    targets = listOf("arm64-v8a", "armeabi-v7a", "x86_64", "x86")
    // Output .so files to jniLibs
    outputDir = file("src/main/jniLibs")
}

// Task to generate UniFFI Kotlin bindings automatically
tasks.register("generateUniFFIBindings") {
    dependsOn("cargoNdkBuild")

    doLast {
        val rustDir = projectDir.parentFile.resolve("rust/crazy_cam_filters")
        val soFile = file("src/main/jniLibs/arm64-v8a/libcrazy_cam_filters.so")

        if (!soFile.exists()) {
            throw GradleException(".so file not found. Make sure cargoNdkBuild ran successfully.")
        }

        exec {
            workingDir = rustDir
            commandLine(
                "cargo", "run", "-p", "uniffi_bindgen", "--bin", "uniffi-bindgen", "--",
                "generate",
                "--library", soFile.absolutePath,
                "--language", "kotlin",
                "--out-dir", file("src/main/java/com/example/crazycam").absolutePath
            )
        }
    }
}

// Make assemble depend on binding generation
tasks.named("preBuild") {
    dependsOn("generateUniFFIBindings")
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}