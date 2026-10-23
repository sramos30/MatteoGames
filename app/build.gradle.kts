import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.matteogames"
    compileSdk = 35

    defaultConfig {
        // Installed/updated through Literki on FireOS: same signer as com.literki.app.
        applicationId = "com.literki.matteogames"
        // API 19 = Android 4.4 / FireOS (Fire Phone). UI is plain Views + Canvas (no Compose).
        minSdk = 19
        targetSdk = 35
        // CI passes VERSION_CODE (the run number) so each build is an upgrade; 1 locally.
        versionCode = System.getenv("VERSION_CODE")?.toIntOrNull() ?: 1
        versionName = "1.0.${System.getenv("VERSION_CODE") ?: "0"}-fireos"
    }

    // Signed with the Literki key (outside the project and git):
    // ~/.literki/assinatura.properties -> storeFile (relative to that folder), storePassword, keyAlias, keyPassword.
    // Android only accepts updates signed with the SAME key, and Literki only installs apps with its signature.
    val assinatura = File(System.getProperty("user.home"), ".literki/assinatura.properties")
    if (!assinatura.exists()) {
        throw GradleException("Falta a chave de assinatura do Literki: $assinatura")
    }
    val sig = Properties().apply { assinatura.inputStream().use { load(it) } }
    signingConfigs {
        create("literki") {
            storeFile = File(assinatura.parentFile, sig.getProperty("storeFile"))
            storePassword = sig.getProperty("storePassword")
            keyAlias = sig.getProperty("keyAlias")
            keyPassword = sig.getProperty("keyPassword")
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("literki")
        }
        release {
            signingConfig = signingConfigs.getByName("literki")
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
    kotlinOptions {
        jvmTarget = "17"
    }
    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }
}

dependencies {
    // Intentionally no AndroidX: everything is framework API available on Android 4.4.
}
