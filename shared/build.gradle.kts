import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
    kotlin("plugin.serialization") version "1.9.0"
}

kotlin {
    androidTarget()

    jvm("desktop")

    js(IR) {
        moduleName = "MyApplication"
        browser() {
            commonWebpackConfig {
                    outputFileName = "MyApplication.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer())
            }
            binaries.executable()
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {

                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
                implementation("com.darkrockstudios:mpfilepicker:3.1.0")
                implementation("media.kamel:kamel-image:0.6.0")
                implementation("io.ktor:ktor-client-core:2.3.1")
                implementation("io.ktor:ktor-client-content-negotiation:2.3.1")
                implementation("io.ktor:ktor-client-logging:2.3.1")
                implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.1")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
            }
        }
        val androidMain by getting {
            dependencies {
                api("androidx.activity:activity-compose:1.7.2")
                api("androidx.appcompat:appcompat:1.6.1")
                api("androidx.core:core-ktx:1.10.1")
                implementation("io.ktor:ktor-client-android:2.3.1")
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)

            dependencies {
                implementation("io.ktor:ktor-client-darwin:2.3.1")
            }
        }
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.common)
            }
        }
    }
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "com.myapplication.common"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        minSdk = (findProperty("android.minSdk") as String).toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
}

compose.desktop {

}

compose.experimental {
    web.application {}
}