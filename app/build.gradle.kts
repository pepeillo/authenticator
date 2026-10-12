import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

android {
    signingConfigs {
        create("release") {
            storeFile = file("keystore")
            storePassword = "keystore_password"
            keyAlias = "key_alias"
            keyPassword = "key_password"
        }
    }
    namespace = "es.jaf.example.authenticator"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "es.jaf.example.authenticator"
        minSdk = 31
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true

            signingConfig = signingConfigs.getByName("release")
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation("net.lingala.zip4j:zip4j:2.11.5")
    implementation("androidx.security:security-crypto:1.0.0")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation("com.google.zxing:core:3.5.3")
    implementation("com.github.woxthebox:draglistview:1.7.2")
    implementation("androidx.biometric:biometric:1.1.0")
    implementation("androidx.preference:preference:1.2.1")
    implementation("commons-codec:commons-codec:1.11")
}

// =================================================================
// ¡PON EL BLOQUE AQUÍ ABAJO! (Fuera de 'android' y al final del archivo)
// =================================================================
if (project.hasProperty("myproject.properties")) {
    val props = Properties()
    val propFileName = project.property("myproject.properties") as String
    val propFile = file(propFileName)

    if (propFile.canRead()) {
        FileInputStream(propFile).use { props.load(it) }

        val hasAllKeys = props.containsKey("keystore") &&
                props.containsKey("keystore_password") &&
                props.containsKey("key_alias") &&
                props.containsKey("key_password")

        if (hasAllKeys) {
            android.signingConfigs.getByName("release").apply {
                storeFile = file(props.getProperty("keystore"))
                storePassword = props.getProperty("keystore_password")
                keyAlias = props.getProperty("key_alias")
                keyPassword = props.getProperty("key_password")
            }
        } else {
            android.buildTypes.getByName("release").signingConfig = null
        }
    } else {
        android.buildTypes.getByName("release").signingConfig = null
    }
}