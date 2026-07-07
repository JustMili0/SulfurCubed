plugins {
    alias(libs.plugins.fabric.loom)
}

val mcVersion = libs.versions.minecraft.get()

base {
    archivesName.set("${rootProject.property("archives_base_name")}-${rootProject.property("mod_version")}+mc${mcVersion}-Fabric")
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
            "mod_id" to rootProject.property("mod_id"),
            "mod_name" to rootProject.property("mod_name"),
            "mod_version" to rootProject.property("mod_version"),
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