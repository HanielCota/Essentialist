plugins {
    `java-library`
    id("com.gradleup.shadow") version "9.4.1"
    id("com.diffplug.spotless") version "8.5.1"
}

group = providers.gradleProperty("group").get()
version = providers.gradleProperty("version").get()
description = "Modular essentials plugin for Paper"

base.archivesName = rootProject.name

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

val paperApi = providers.gradleProperty("paperApiVersion")
val commandFw = providers.gradleProperty("commandFrameworkVersion")
val menuFw = providers.gradleProperty("menuFrameworkVersion")
val minecraftVersion = providers.gradleProperty("minecraftVersion")
val configurate = providers.gradleProperty("configurateVersion")
val hikari = providers.gradleProperty("hikariVersion")
val sqlite = providers.gradleProperty("sqliteVersion")

dependencies {
    compileOnly("io.papermc.paper:paper-api:${paperApi.get()}")
    compileOnly("org.projectlombok:lombok:1.18.46")
    annotationProcessor("org.projectlombok:lombok:1.18.46")
    implementation("com.github.HanielCota.CommandFramework:command-paper:${commandFw.get()}")
    implementation("com.github.HanielCota:MenuFramework:${menuFw.get()}")
    implementation("org.spongepowered:configurate-yaml:${configurate.get()}")
    implementation("com.zaxxer:HikariCP:${hikari.get()}")
    implementation("org.xerial:sqlite-jdbc:${sqlite.get()}")
}

spotless {
    java {
        target("src/**/*.java")
        googleJavaFormat("1.35.0").reflowLongStrings()
        removeUnusedImports()
        formatAnnotations()
        trimTrailingWhitespace()
        endWithNewline()
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint()
    }
}

tasks {
    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release = 25
        options.compilerArgs.add("-parameters")
        options.compilerArgs.add("-Xlint:deprecation")
    }

    processResources {
        filteringCharset = "UTF-8"
        val tokens =
            mapOf(
                "name" to rootProject.name,
                "version" to project.version.toString(),
                "apiVersion" to minecraftVersion.get(),
            )
        inputs.properties(tokens)
        filesMatching("plugin.yml") { expand(tokens) }
    }

    shadowJar {
        archiveClassifier = ""
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
        mergeServiceFiles()

        val shadeBase = "${project.group}.essentials.libs"
        relocate("io.github.hanielcota.commandframework", "$shadeBase.commandframework")
        relocate("com.github.hanielcota.menuframework", "$shadeBase.menuframework")
        relocate("com.github.benmanes.caffeine", "$shadeBase.caffeine")
        relocate("it.unimi.dsi.fastutil", "$shadeBase.fastutil")
        relocate("org.spongepowered.configurate", "$shadeBase.configurate")
        relocate("io.leangen.geantyref", "$shadeBase.geantyref")
        relocate("net.kyori.option", "$shadeBase.kyorioption")
        relocate("com.zaxxer.hikari", "$shadeBase.hikari")

        exclude("META-INF/maven/**", "META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")

        manifest.attributes(
            "Implementation-Title" to rootProject.name,
            "Implementation-Version" to project.version,
        )
    }

    jar { enabled = false }
    assemble { dependsOn(shadowJar) }
}
