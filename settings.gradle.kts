pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

rootProject.name = "CrazyEnchantments"

fun includeProject(pair: Pair<String, String>): Unit = includeProject(pair.first, pair.second)

fun includeProject(name: String, block: ProjectDescriptor.() -> Unit) {
    include(name)
    project(":$name").apply(block)
}

fun includeProject(path: String, name: String) {
    includeProject(name) {
        this.name = "${rootProject.name.lowercase()}-$name"
        this.projectDir = File(path)
    }
}

listOf(
    "common" to "common",
    "paper" to "paper",
    "api" to "api"
).forEach(::includeProject)