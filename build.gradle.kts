import java.net.URL
import java.nio.file.Files

group = "org.purpurmc.purpurextras"

version = "1.33.0"

description = "\"This should be a plugin\" features from Purpur"

plugins {
    id("java") // Tell gradle this is a java project.
    id("java-library") // Import helper for source-based libraries.
    id("com.diffplug.spotless") version "7.0.4" // Import auto-formatter.
    id("com.gradleup.shadow") version "8.3.6" // Import shadow API.
    eclipse // Import eclipse plugin for IDE integration.
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
        vendor.set(org.gradle.jvm.toolchain.JvmVendorSpec.GRAAL_VM)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.isFork = true
    options.compilerArgs.addAll(listOf("-parameters", "-Xlint:deprecation"))
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://jitpack.io")
    maven("https://repo.purpurmc.org/snapshots")
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://libraries.minecraft.net")
}

dependencies {
    api("com.github.YouHaveTrouble:Entiddy:v2.0.1")
    api("org.reflections:reflections:0.10.2")
    compileOnly("org.purpurmc.purpur:purpur-api:1.19.4-R0.1-SNAPSHOT") // Declare purpur API version to be packaged.
}

val serverDir: File = projectDir.resolve("run")
val pluginDir: File = serverDir.resolve("plugins")

tasks.named<ProcessResources>("processResources") {
    val props =
        mapOf(
            "name" to project.name,
            "version" to project.version.toString(),
            "description" to project.description!!.replace("\"", "\\\""),
        )
    inputs.properties(props)
    filesMatching("plugin.yml") { expand(props) }
    from("LICENSE") { into("/") }
}

tasks.withType<AbstractArchiveTask>().configureEach { // Ensure reproducible .jars
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
}

tasks.shadowJar {
    archiveClassifier.set("") // Use empty string instead of null.
    archiveFileName.set("PurpurExtras-${project.version}.jar")
    relocate("org.reflections", "org.purpurmc.purpurextras.reflections")
    relocate("me.youhavetrouble.entiddy", "org.purpurmc.purpurextras.entiddy")
    minimize()
}

tasks.clean { doLast { serverDir.deleteRecursively() } }

tasks.build {
    dependsOn(tasks.spotlessApply)
    dependsOn(tasks.shadowJar)
}

tasks.register("downloadServer") {
    group = "purpur"

    doFirst {
        serverDir.mkdirs()
        pluginDir.mkdirs()
        URL("https://api.purpurmc.org/v2/purpur/1.21/latest/download").openStream().use {
            Files.copy(it, serverDir.resolve("server.jar").toPath())
        }
    }
}

tasks.register<JavaExec>("runServer") {
    group = "purpur"
    dependsOn(tasks.named("spotlessApply"))
    dependsOn(tasks.named("shadowJar"))
    if (!serverDir.resolve("server.jar").exists()) dependsOn("downloadServer")

    doFirst {
        pluginDir.resolve("PurpurExtras.jar").delete()
        Files.copy(
            layout.buildDirectory.file("libs/PurpurExtras-${project.version}.jar").get().asFile.toPath(),
            pluginDir.resolve("PurpurExtras.jar").toPath(),
        )
    }

    classpath = files(serverDir.resolve("server.jar"))
    workingDir = serverDir
    jvmArgs = listOf("-Dcom.mojang.eula.agree=true")
    args = listOf("--nogui")
    standardInput = System.`in`
}

spotless {
    java {
        removeUnusedImports()
        palantirJavaFormat()
    }
    kotlinGradle {
        ktfmt().kotlinlangStyle().configure { it.setMaxWidth(120) }
        target("build.gradle.kts", "settings.gradle.kts")
    }
}
