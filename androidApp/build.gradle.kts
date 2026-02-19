plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidTarget {
        compilations.all {
            @Suppress("DEPRECATION")
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(project(":shared"))

            // Compose (versions managed by BOM in dependencies block)
            implementation(libs.compose.ui)
            implementation(libs.compose.ui.graphics)
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.compose.material3)
            implementation(libs.compose.material.icons)
            implementation(libs.navigation.compose)
            implementation(libs.activity.compose)

            // AndroidX
            implementation(libs.core.ktx)
            implementation(libs.webkit)

            // Koin
            implementation(libs.koin.android)
            implementation(libs.koin.compose)

            // Coroutines
            implementation(libs.kotlinx.coroutines.android)
        }
    }
}

android {
    namespace = "com.statichumstudio.slipshell"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.statichumstudio.slipshell"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
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

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/versions/9/OSGI-INF/MANIFEST.MF"
        }
    }
}

dependencies {
    implementation(platform(libs.compose.bom))
    debugImplementation(libs.compose.ui.tooling)
}
