plugins {
    kotlin("multiplatform") version "2.0.20"
    kotlin("plugin.serialization") version "2.0.20"
    id("org.jetbrains.compose") version "1.6.11"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    application
}

group = "com.aimatrix"
version = "1.0.0"

repositories {
    mavenCentral()
    google()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "17"
        }
        withJava()
    }
    
    // Native targets for different platforms
    macosX64()
    macosArm64()
    linuxX64()
    mingwX64() // Windows
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                // Clikt for CLI parsing
                implementation("com.github.ajalt.clikt:clikt:4.4.0")
                
                // Kotlin coroutines for async operations
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
                
                // Serialization for config and data
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
                
                // Datetime for timestamps
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")
                
                // Okio for file operations
                implementation("com.squareup.okio:okio:3.9.0")
                
                // Ktor client for HTTP operations
                implementation("io.ktor:ktor-client-core:2.3.12")
                implementation("io.ktor:ktor-client-content-negotiation:2.3.12")
                implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.12")
                
                // Logging
                implementation("io.github.oshai:kotlin-logging:6.0.9")
            }
        }
        
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
            }
        }
        
        val jvmMain by getting {
            dependencies {
                // JVM specific dependencies
                implementation("io.ktor:ktor-client-okhttp:2.3.12")
                implementation("ch.qos.logback:logback-classic:1.5.6")
                
                // Process management
                implementation("com.github.pgreze:kotlin-process:1.5")
                
                // File watching
                implementation("io.methvin:directory-watcher:0.18.0")
                
                // Terminal colors
                implementation("com.github.ajalt.mordant:mordant:2.7.1")
                
                // Compose for future UI
                implementation(compose.desktop.currentOs)
            }
        }
        
        val nativeMain by creating {
            dependsOn(commonMain)
            dependencies {
                // Native specific dependencies
                implementation("io.ktor:ktor-client-curl:2.3.12")
            }
        }
        
        val macosMain by creating {
            dependsOn(nativeMain)
        }
        
        val macosX64Main by getting {
            dependsOn(macosMain)
        }
        
        val macosArm64Main by getting {
            dependsOn(macosMain)
        }
        
        val linuxX64Main by getting {
            dependsOn(nativeMain)
        }
        
        val mingwX64Main by getting {
            dependsOn(nativeMain)
        }
    }
}

application {
    mainClass.set("com.aimatrix.cli.AIMatrixMainKt")
}

compose.desktop {
    application {
        mainClass = "com.aimatrix.cli.AIMatrixMainKt"
        
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

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
    standardOutput = System.out
    errorOutput = System.err
}

// Shadow JAR configuration
tasks.shadowJar {
    archiveBaseName.set("aimatrix-cli")
    archiveClassifier.set("")
    archiveVersion.set(version.toString())
    
    manifest {
        attributes(
            "Main-Class" to "com.aimatrix.cli.AIMatrixMainKt",
            "Implementation-Title" to "AIMatrix CLI",
            "Implementation-Version" to version
        )
    }
}

// Documentation - Removed dokka for now to simplify build

// Test configuration
tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

// Custom tasks
tasks.register("buildAll") {
    group = "build"
    description = "Build all targets"
    dependsOn("jvmJar", "shadowJar")
}

tasks.register("setupDev") {
    group = "development"
    description = "Set up development environment"
    
    doLast {
        val configDir = File(System.getProperty("user.home"), ".aimatrix")
        if (!configDir.exists()) {
            configDir.mkdirs()
            println("Created configuration directory: ${configDir.absolutePath}")
        }
        
        println("Development environment setup complete!")
        println("Set your API keys as environment variables:")
        println("  export GEMINI_API_KEY=your_key_here")
        println("  export OPENAI_API_KEY=your_key_here")
        println("  export CLAUDE_API_KEY=your_key_here")
        println("  export DEEPSEEK_API_KEY=your_key_here")
    }
}