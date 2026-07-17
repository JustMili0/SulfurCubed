plugins {
    alias(libs.plugins.fabric.loom)
    alias(libs.plugins.publish)
}

val mcVersion = libs.versions.minecraft.get()
val modId = rootProject.property("mod_id").toString()
val modVersion = rootProject.property("mod_version").toString()

base {
    archivesName.set("${rootProject.property("archives_base_name")}-${modVersion}+mc${mcVersion}-Fabric")
}

loom {
    accessWidenerPath = file("src/main/resources/$modId.accesswidener")
}

repositories {
    maven("https://maven.terraformersmc.com/") // Mod Menu
    maven("https://maven.caffeinemc.net/releases") // Sodium API
}

dependencies {
    minecraft(libs.minecraft.get())
    implementation(libs.fabric.loader.get())
    implementation(libs.fabric.api.get())

    // https://maven.caffeinemc.net/#/releases/net/caffeinemc/sodium-fabric-api
    compileOnly("net.caffeinemc:sodium-fabric-api:${rootProject.property("sodium")}+mc${mcVersion}")

    implementation("com.terraformersmc:modmenu:${rootProject.property("mod_menu")}") // Mod menu
}

tasks.processResources {
    filesMatching("fabric.mod.json") {
        expand(mapOf(
            "mod_id" to modId,
            "mod_name" to rootProject.property("mod_name"),
            "mod_version" to modVersion,
            "mod_description" to rootProject.property("mod_description"),
            "mod_authors" to rootProject.property("mod_authors"),
            "mod_license" to rootProject.property("mod_license"),
            "fabric_loader_version" to libs.versions.fabric.loader.get(),
            "fabric_api_version" to libs.versions.fabric.api.get(),
            "minecraft_version_constraint" to rootProject.property("minecraft_version_constraint_fabric")
        ))
    }
}

tasks.withType<JavaCompile>().configureEach {
	options.release = 25
}

java {
	sourceCompatibility = JavaVersion.VERSION_25
	targetCompatibility = JavaVersion.VERSION_25
}

tasks.jar {
	from("LICENSE") {
		rename { it }
	}
}

publishMods {
    file.set(tasks.jar.flatMap { it.archiveFile })
    modLoaders.add("fabric")
    version.set("$modVersion+mc$mcVersion-Fabric")
    displayName.set("Sulfur Cubed $modVersion")

    val changelogFile = rootProject.file(".Changelogs/${rootProject.property("mod_version")}-Changelog.md")
    val confirmedFile = if (changelogFile.exists()) changelogFile.readText(Charsets.UTF_8) else "No changelog file found for version $modVersion"
    changelog.set(confirmedFile)

    modrinth {
        accessToken.set(property("modrinth_token").toString())
        projectId.set("Ixw0NG5c")
        minecraftVersions.add("26.2")
        environment.set(CLIENT_AND_SERVER)
        type.set(STABLE)
    }
}