plugins {
    kotlin("multiplatform") version "2.0.20"
    kotlin("plugin.compose") version "2.0.20"
    kotlin("plugin.serialization") version "2.0.20"
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
                
                // Serialization
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
                
                // Coroutines
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
            }
        }
        
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                
                // CLI Framework
                implementation("com.github.ajalt.clikt:clikt:4.2.1")
                implementation("com.github.ajalt.mordant:mordant:2.2.0")
                
                // HTTP Client
                implementation("io.ktor:ktor-client-core:2.3.5")
                implementation("io.ktor:ktor-client-cio:2.3.5")
                implementation("io.ktor:ktor-client-content-negotiation:2.3.5")
                implementation("io.ktor:ktor-client-logging:2.3.5")
                implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.5")
                
                // Configuration
                implementation("com.sksamuel.hoplite:hoplite-core:2.7.5")
                implementation("com.sksamuel.hoplite:hoplite-yaml:2.7.5")
                
                // Logging
                implementation("org.slf4j:slf4j-api:2.0.9")
                implementation("ch.qos.logback:logback-classic:1.4.11")
                
                // Security/Encryption for storing credentials
                implementation("com.google.crypto.tink:tink:1.10.0")
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

// Task to run the CLI
tasks.register<JavaExec>("runCLI") {
    group = "application"
    description = "Run AIMatrix CLI"
    val jvmTarget = kotlin.targets["jvm"] as org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget
    classpath = jvmTarget.compilations["main"].output.allOutputs + 
                jvmTarget.compilations["main"].runtimeDependencyFiles
    mainClass.set("com.aimatrix.cli.MainKt")
    
    // Pass command line arguments
    if (project.hasProperty("cliArgs")) {
        args = (project.property("cliArgs") as String).split(" ")
    }
    
    // Make sure we depend on compiling the code
    dependsOn("jvmMainClasses")
}

// Create distribution task
tasks.register<Jar>("fatJar") {
    group = "build"
    description = "Create a fat JAR with all dependencies"
    
    val jvmTarget = kotlin.targets["jvm"] as org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget
    from(jvmTarget.compilations["main"].output.allOutputs)
    
    dependsOn(configurations.getByName("jvmRuntimeClasspath"))
    from({
        configurations.getByName("jvmRuntimeClasspath").map { if (it.isDirectory) it else zipTree(it) }
    })
    
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    
    manifest {
        attributes["Main-Class"] = "com.aimatrix.cli.MainKt"
    }
    
    archiveBaseName.set("amx-cli")
    archiveClassifier.set("all")
}