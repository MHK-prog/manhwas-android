plugins {
    id("com.android.application")
}

val keystoreFile = providers.environmentVariable("ANDROID_KEYSTORE_FILE").orNull
val keystorePassword = providers.environmentVariable("ANDROID_KEYSTORE_PASSWORD").orNull
val keyAliasValue = providers.environmentVariable("ANDROID_KEY_ALIAS").orNull
val keyPasswordValue = providers.environmentVariable("ANDROID_KEY_PASSWORD").orNull
val hasReleaseSigning = listOf(keystoreFile, keystorePassword, keyAliasValue, keyPasswordValue)
    .all { !it.isNullOrBlank() }

android {
    namespace = "com.mhkprog.manhwas"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.manhwatracker.app"
        minSdk = 24
        targetSdk = 36
        versionCode = 2
        versionName = "2.0.0"
    }

    sourceSets["main"].assets.srcDir(layout.buildDirectory.dir("generated/web-assets"))

    signingConfigs {
        if (hasReleaseSigning) {
            create("release") {
                storeFile = file(keystoreFile!!)
                storeType = "JKS"
                storePassword = keystorePassword
                keyAlias = keyAliasValue
                keyPassword = keyPasswordValue
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            if (hasReleaseSigning) signingConfig = signingConfigs.getByName("release")
        }
    }
}

val copyWebApp by tasks.registering(Copy::class) {
    from(rootProject.projectDir) {
        include("index.html", "manifest.webmanifest", "sw.js")
    }
    into(layout.buildDirectory.dir("generated/web-assets/www"))
}

tasks.named("preBuild").configure { dependsOn(copyWebApp) }

dependencies {
    implementation("androidx.activity:activity-ktx:1.13.0")
    implementation("androidx.documentfile:documentfile:1.1.0")
    implementation("androidx.webkit:webkit:1.17.1")
}
