import java.net.URL
import java.nio.file.Files

/* ------------------------------ Plugins ------------------------------ */
plugins {
    id("java") // Import Java plugin.
    id("java-library") // Import Java Library plugin.
    id("com.diffplug.spotless") version "7.0.4" // Import Spotless plugin.
    id("com.gradleup.shadow") version "8.3.6" // Import Shadow plugin.
    id("checkstyle") // Import Checkstyle plugin.
    eclipse // Import Eclipse plugin.
    kotlin("jvm") version "2.1.21" // Import Kotlin JVM plugin.
}

/* --------------------------- JDK / Kotlin ---------------------------- */
java {
    sourceCompatibility = JavaVersion.VERSION_17 // Compile with JDK 17 compatibility.
    toolchain { // Select Java toolchain.
        languageVersion.set(JavaLanguageVersion.of(17)) // Use JDK 17.
        vendor.set(JvmVendorSpec.GRAAL_VM) // Use GraalVM CE.
    }
}

kotlin { jvmToolchain(17) }

/* ----------------------------- Metadata ------------------------------ */
group = "org.purpurmc.purpurextras"

version = "1.33.0"

val apiVersion = "1.19" // Declare minecraft server target version.

val pluginName = "PurpurExtras-OG"

description = "\"This should be a plugin\" features from Purpur"

/* ----------------------------- Resources ----------------------------- */
tasks.named<ProcessResources>("processResources") {
    val props =
        mapOf(
            "name" to project.name,
            "version" to project.version.toString(),
            "apiVersion" to apiVersion,
            "description" to project.description.orEmpty().replace("\"", "\\\""),
            "pluginName" to pluginName,
        )
    inputs.properties(props) // Indicates to rerun if version changes.
    filesMatching("plugin.yml") { expand(props) }
    from("LICENSE") { into("/") } // Bundle licenses into jarfiles.
}

/* ---------------------------- Repos ---------------------------------- */
repositories {
    mavenCentral() // Import the Maven Central Maven Repository.
    gradlePluginPortal() // Import the Gradle Plugin Portal Maven Repository.
    maven("https://oss.sonatype.org/content/groups/public/") // Import the OSS Sonatype Repository.
    maven("https://jitpack.io") // Import the Jitpack Maven Repository.
    maven { url = uri("https://repo.purpurmc.org/snapshots") } // Import the PurpurMC Maven Repository.
    maven("https://papermc.io/repo/repository/maven-public/") // Import the PaperMC Maven Repository.
    maven("https://libraries.minecraft.net") // Import the Mojang Maven Repository.
}

/* ---------------------- Java project deps ---------------------------- */
dependencies {
    compileOnly("org.purpurmc.purpur:purpur-api:1.19.4-R0.1-SNAPSHOT") // Declare Purpur API version to be packaged.
    api("com.github.YouHaveTrouble:Entiddy:v2.0.1") // Import Entiddy API.
    api("org.reflections:reflections:0.10.2") // Import Reflections API.
}

val serverDir: File = projectDir.resolve("run")
val pluginDir: File = serverDir.resolve("plugins")

/* ---------------------- Reproducible jars ---------------------------- */
tasks.withType<AbstractArchiveTask>().configureEach { // Ensure reproducible .jars
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
}

/* ----------------------------- Shadow -------------------------------- */
tasks.shadowJar {
    exclude("io.github.miniplaceholders.*") // Exclude the MiniPlaceholders package from being shadowed.
    archiveClassifier.set("") // Use empty string instead of null.
    archiveFileName.set("PurpurExtras-OG-${project.version}.jar")
    relocate("org.reflections", "org.purpurmc.purpurextras.reflections")
    relocate("me.youhavetrouble.entiddy", "org.purpurmc.purpurextras.entiddy")
    minimize()
}

tasks.clean { doLast { serverDir.deleteRecursively() } }

tasks.jar { archiveClassifier.set("part") } // Applies to root jarfile only.

tasks.build { dependsOn(tasks.spotlessApply, tasks.shadowJar) } // Build depends on spotless and shadow.

/* ----------------------------- Server -------------------------------- */
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

/* --------------------------- Javac opts ------------------------------- */
tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-parameters") // Enable reflection for java code.
    options.isFork = true // Run javac in its own process.
    options.compilerArgs.add("-Xlint:deprecation") // Trigger deprecation warning messages.
    options.encoding = "UTF-8" // Use UTF-8 file encoding.
}

/* ----------------------------- Auto Formatting ------------------------ */
spotless {
    java {
        eclipse().configFile("config/formatter/eclipse-java-formatter.xml") // Eclipse java formatting.
        leadingTabsToSpaces() // Convert leftover leading tabs to spaces.
        removeUnusedImports() // Remove imports that aren't being called.
    }
    kotlinGradle {
        ktfmt().kotlinlangStyle().configure { it.setMaxWidth(120) } // JetBrains Kotlin formatting.
        target("build.gradle.kts", "settings.gradle.kts") // Gradle files to format.
    }
}

checkstyle {
    toolVersion = "10.18.1" // Declare checkstyle version to use.
    configFile = file("config/checkstyle/checkstyle.xml") // Point checkstyle to config file.
    isIgnoreFailures = true // Don't fail the build if checkstyle does not pass.
    isShowViolations = true // Show the violations in any IDE with the checkstyle plugin.
}

tasks.named("compileJava") {
    dependsOn("spotlessApply") // Run spotless before compiling with the JDK.
}

tasks.named("spotlessCheck") {
    dependsOn("spotlessApply") // Run spotless before checking if spotless ran.
}
