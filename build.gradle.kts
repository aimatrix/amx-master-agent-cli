plugins {
    kotlin("multiplatform") version "2.0.20"
    kotlin("plugin.compose") version "2.0.20"
    id("org.jetbrains.compose") version "1.6.11"
}

group = "com.aimatrix"
version = "1.0.0"

repositories {
    mavenCentral()
    google()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

kotlin {
    jvm {
        compilations.all {
            compilerOptions.configure {
                jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
            }
        }
        withJava()
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
            }
        }
        
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.aimatrix.cli.SimpleGUIKt"
        
        nativeDistributions {
            targetFormats(
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb
            )
            
            packageName = "AIMatrix CLI"
            packageVersion = version.toString()
            description = "Multi-LLM Command Line Interface"
            copyright = "© 2024 AIMatrix Team. All rights reserved."
            vendor = "AIMatrix"
        }
    }
}

// Task to run the GUI
tasks.register<JavaExec>("runGUI") {
    group = "application"
    description = "Run AIMatrix CLI in GUI mode"
    val jvmTarget = kotlin.targets["jvm"] as org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget
    classpath = jvmTarget.compilations["main"].output.allOutputs + 
                jvmTarget.compilations["main"].runtimeDependencyFiles
    mainClass.set("com.aimatrix.cli.SimpleGUIKt")
    
    // Make sure we depend on compiling the code
    dependsOn("jvmMainClasses")
}