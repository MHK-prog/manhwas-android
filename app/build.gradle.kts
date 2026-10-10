import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

plugins {
    id("com.android.application")
}

abstract class CopyWebAssetsTask : DefaultTask() {
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val sourceFiles: ConfigurableFileCollection

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @TaskAction
    fun copyAssets() {
        val destination = outputDirectory.get().dir("www").asFile
        destination.mkdirs()
        sourceFiles.files.forEach { source ->
            source.copyTo(destination.resolve(source.name), overwrite = true)
        }
    }
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
        versionCode = 3
        versionName = "2.1.0"
    }

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

androidComponents {
    onVariants { variant ->
        val copyWebAssets = tasks.register<CopyWebAssetsTask>("copyWebAssets${variant.name.replaceFirstChar { it.uppercase() }}") {
            sourceFiles.from(
                rootProject.file("index.html"),
                rootProject.file("manifest.webmanifest"),
                rootProject.file("sw.js"),
            )
        }
        variant.sources.assets?.addGeneratedSourceDirectory(copyWebAssets) { it.outputDirectory }
    }
}

dependencies {
    implementation("androidx.activity:activity-ktx:1.13.0")
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.documentfile:documentfile:1.1.0")
    implementation("androidx.webkit:webkit:1.17.1")
}
