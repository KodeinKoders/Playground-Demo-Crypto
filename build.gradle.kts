plugins {
    kotlin("multiplatform") version "1.6.21"
    id("com.android.library")
}

group = "net.kodein.demo.crypto"
version = "1.0"

repositories {
    google()
    mavenCentral()
}

kotlin {
    explicitApi()

    android()

    ios()

    iosSimulatorArm64 {
        compilations["main"].defaultSourceSet.dependsOn(sourceSets["iosMain"])
        compilations["test"].defaultSourceSet.dependsOn(sourceSets["iosTest"])
    }

    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13.2")
                implementation("androidx.test.ext:junit:1.1.3")
                implementation("androidx.test.espresso:espresso-core:3.4.0")
            }
        }
        val iosMain by getting
        val iosTest by getting

        all {
            languageSettings {
                optIn("kotlin.ExperimentalUnsignedTypes")
            }
        }
    }
}

android {
    compileSdk = 32
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 24
        targetSdk = 32
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

afterEvaluate {
    tasks.withType<AbstractTestTask> {
        testLogging {
            events("passed", "skipped", "failed", "standard_out", "standard_error")
            showExceptions = true
            showStackTraces = true
        }
    }
}
