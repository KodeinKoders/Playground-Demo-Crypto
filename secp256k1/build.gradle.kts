import com.android.build.gradle.LibraryExtension
import org.gradle.internal.os.OperatingSystem

val currentOs = OperatingSystem.current()
val bash = if (currentOs.isWindows) "bash.exe" else "bash"

val buildSecp256k1 by tasks.creating { group = "build" }

val buildSecp256k1Ios by tasks.creating {
    group = "build"
    buildSecp256k1.dependsOn(this)
}

fun creatingBuildSecp256k1Ios(arch: String) = tasks.creating(Exec::class) {
    group = "build"
    buildSecp256k1Ios.dependsOn(this)

    onlyIf { currentOs.isMacOsX }

    inputs.files("$projectDir/build-ios.sh", fileTree("$projectDir/secp256k1/src") {
        include("*.c", "*.h")
        exclude("*-config.h")
    })
    outputs.dir("$projectDir/build/ios/$arch")

    environment("ARCH", arch)
    commandLine(bash, "build-ios.sh")
}

val buildSecp256k1IosArm64 by creatingBuildSecp256k1Ios("arm64")
val buildSecp256k1IosArm64Sim by creatingBuildSecp256k1Ios("arm64-sim")
val buildSecp256k1IosX86_64Sim by creatingBuildSecp256k1Ios("x86_64-sim")


val buildSecp256k1Android by tasks.creating {
    group = "build"
    buildSecp256k1.dependsOn(this)
}

fun creatingBuildSecp256k1Android(arch: String) = tasks.creating(Exec::class) {
    group = "build"
    buildSecp256k1Android.dependsOn(this)

    inputs.files("build-android.sh", fileTree("$projectDir/secp256k1/src") {
        include("*.c", "*.h")
        exclude("*-config.h")
    })
    outputs.dir("$projectDir/build/android/$arch")

    workingDir = projectDir

    val toolchain = when {
        currentOs.isLinux -> "linux-x86_64"
        currentOs.isMacOsX -> "darwin-x86_64"
        currentOs.isWindows -> "windows-x86_64"
        else -> error("No Android toolchain defined for this OS: $currentOs")
    }
    environment("TOOLCHAIN", toolchain)
    environment("ARCH", arch)
    environment("ANDROID_NDK", (rootProject.extensions["android"] as LibraryExtension).ndkDirectory)
    commandLine(bash, "build-android.sh")
}
val buildSecp256k1AndroidX86_64 by creatingBuildSecp256k1Android("x86_64")
val buildSecp256k1AndroidX86 by creatingBuildSecp256k1Android("x86")
val buildSecp256k1AndroidArm64v8a by creatingBuildSecp256k1Android("arm64-v8a")
val buildSecp256k1AndroidArmeabiv7a by creatingBuildSecp256k1Android("armeabi-v7a")

val clean by tasks.creating {
    group = "build"
    doLast {
        delete("$projectDir/build")
    }
}
