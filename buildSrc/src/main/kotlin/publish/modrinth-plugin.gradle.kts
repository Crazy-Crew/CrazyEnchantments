plugins {
    id("com.modrinth.minotaur")

    id("shared-plugin")
}

modrinth {
    token = System.getenv("MODRINTH_TOKEN")

    projectId = "${rootProject.property("project_id")}"

    versionName = rootProject.ext.get("version_name").toString()
    versionNumber = "${rootProject.version}"
    versionType = rootProject.ext.get("release_type").toString()

    changelog = rootProject.ext.get("mc_changelog").toString()

    gameVersions.addAll(rootProject.property("project_versions").toString().split(",").map { it.trim() })

    uploadFile.set(tasks.named<Jar>("jar"))

    loaders.addAll(rootProject.property("project_platforms").toString().split(",").map { it.trim() })

    syncBodyFrom = rootProject.file("README.md").readText(Charsets.UTF_8)

    autoAddDependsOn = false
    detectLoaders = false

    dependencies {
        optional.project("DecentHolograms")
        optional.project("FancyHolograms")
        optional.project("PlaceholderAPI")
    }
}