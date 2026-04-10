plugins {
    `java-library`
    id("net.minecrell.plugin-yml.paper") version "0.6.0"
    id("com.gradleup.shadow") version "8.3.5"
    id("xyz.jpenilla.run-paper") version "2.3.0"
}

group = "pl.svhard.christmas"
version = "1.0.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    maven("https://maven-central.storage-download.googleapis.com/maven2")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-releases/")
    maven("https://repo.eternalcode.pl/releases")
    maven("https://repo.panda-lang.org/releases")
    maven("https://storehouse.okaeri.eu/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")

    // PacketEvents
    compileOnly("com.github.retrooper:packetevents-spigot:2.7.0")

    // Multification & Commons
    paperLibrary("com.eternalcode:multification-okaeri:1.1.4")
    paperLibrary("com.eternalcode:multification-bukkit:1.1.4")
    paperLibrary("com.eternalcode:eternalcode-commons-bukkit:1.3.0")
    paperLibrary("com.eternalcode:eternalcode-commons-adventure:1.3.0")

    // LiteCommands
    paperLibrary("dev.rollczi:litecommands-bukkit:3.10.0")
    paperLibrary("dev.rollczi:litecommands-adventure:3.10.0")

    // Okaeri Configs
    paperLibrary("eu.okaeri:okaeri-configs-yaml-snakeyaml:5.0.5")
    paperLibrary("eu.okaeri:okaeri-configs-serdes-commons:5.0.5")

    // Adventure
    paperLibrary("net.kyori:adventure-platform-bukkit:4.3.4")
    paperLibrary("net.kyori:adventure-text-minimessage:4.17.0")

    // Database
    paperLibrary("org.mariadb.jdbc:mariadb-java-client:3.5.1")
    paperLibrary("org.postgresql:postgresql:42.7.5")
    paperLibrary("com.h2database:h2:2.1.214")
    paperLibrary("com.j256.ormlite:ormlite-core:6.1")
    paperLibrary("com.j256.ormlite:ormlite-jdbc:6.1")
    paperLibrary("com.zaxxer:HikariCP:5.1.0")

    paperLibrary("dev.triumphteam:triumph-gui:3.1.13")
}

paper {
    name = "svhard-christmas-presents"
    version = "${project.version}"
    prefix = "svhard-christmas-presents"
    author = "svhard"

    main = "pl.svhard.christmas.presents.ChristmasPresentsPlugin"
    bootstrapper = "pl.svhard.christmas.presents.ChristmasPresentsBootstrap"
    loader = "pl.svhard.christmas.presents.ChristmasPresentsLoader"

    generateLibrariesJson = true
    apiVersion = "1.19"

    serverDependencies {
        register("packetevents") {
            required = true
        }
    }
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveFileName.set("svhard-christmas-presents-${project.version}.jar")

        exclude(
            "org/intellij/lang/annotations/**",
            "org/jetbrains/annotations/**",
        )
    }

    runServer {
        minecraftVersion("1.21.11")
        downloadPlugins {
            modrinth("packetevents", "2.11.1+spigot")
        }
    }
}
