plugins {
    id("root-plugin")

    id("com.modrinth.minotaur") version "2.8.2"
}

defaultTasks("build")

rootProject.group = "com.badbones69.crazyenchantments"
rootProject.description = "Adds over 80 unique enchantments to your server and more!"
rootProject.version = "2.0.2"

val combine = tasks.register<Jar>("combine") {
    mustRunAfter("build")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    val jarFiles = subprojects.flatMap { subproject ->
        files(subproject.layout.buildDirectory.file("libs/${rootProject.name}-${subproject.name}-${subproject.version}.jar").get())
    }.filter { it.name != "MANIFEST.MF" }.map { file ->
        if (file.isDirectory) file else zipTree(file)
    }

    from(jarFiles)
}

tasks {
    assemble {
        subprojects.forEach { project ->
            dependsOn(":${project.name}:build")
        }

        finalizedBy(combine)
    }
}

val description = """

## New Enchantments:
|Enchant | Type | Description|
|---------|-------|-------------|
DemonForged  | Axe | Deal more durability damage.
Crouch | Armor | While sneaking, take less damage.
SystemReboot | Armor | An attack that would normally kill you can instead heal you to full HP.
Adrenaline | Boots | When on low HP, chance to get a huge speed boost.
ShockWave | Armor | Chance to Knockback Enemy when they damage you.
Maneuver | Armor | Chance to dodge an attack.
VeinMiner | Pickaxe | For each level, will mine 1 more block from ore veins.

## Config Options Added:
* `VeinMiner-Full-Durability: true`
* `Drop-Blocks-For-VeinMiner: true`

## Misc:
* New Default Category added for the new enchants. `Mythical`
* Updated default CrazyEnchantment colour.

## Fix:
* Fixed tinkerer scaling not working properly for books.

## Other:
 * [Feature Requests](https://github.com/Crazy-Crew/${rootProject.name}/discussions/categories/features)
 * [Bug Reports](https://github.com/Crazy-Crew/${rootProject.name}/issues)
""".trimIndent()

val versions = listOf(
    "1.20",
    "1.20.1"
)

val isSnapshot = rootProject.version.toString().contains("snapshot")
val type = if (isSnapshot) "beta" else "release"

modrinth {
    autoAddDependsOn.set(false)

    token.set(System.getenv("MODRINTH_TOKEN"))

    projectId.set(rootProject.name.lowercase())

    versionName.set("${rootProject.name} ${rootProject.version}")
    versionNumber.set("${rootProject.version}")

    uploadFile.set(combine.get())

    gameVersions.addAll(versions)

    changelog.set(description)

    loaders.addAll("paper", "purpur")
}