import utils.convertList

plugins {
    id("com.ryderbelserion.feather.core")

    id("java-plugin") apply false
}

val libs = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")

val git = feather.builder
val utils = git.utils
val env: Map<String, String> = System.getenv()

val branch = utils.getRemoteBranch()
val hash: String = env.getOrDefault("commit_id", utils.getRemoteCommitHash())
val commit: String = env.getOrDefault("commit_message", utils.getRemoteCommitMessage(hash, "%B"))

val status: String = env.getOrDefault("version_status", "release").lowercase()

val buildNumber: String = env.getOrDefault("BUILD_NUMBER", "N/A")
val isJenkins: Boolean = buildNumber != "N/A"

val isRelease: Boolean = status == "release"
val isAlpha: Boolean = status == "alpha"
val isBeta: Boolean = status == "beta"

val commitHash: String = hash.subSequence(0, hash.length.coerceAtMost(7)).toString()
val changelog = rootProject.file("changelog.md").readText(Charsets.UTF_8)
val content: String = if (isRelease) changelog else if (isBeta || isAlpha || isJenkins) {
    "[$commitHash](https://github.com/${rootProject.property("repository_owner")}/${rootProject.name}/commit/$commitHash) $commit"
} else changelog

val minecraft = libs.findVersion("minecraft").get()

rootProject.description = rootProject.property("project_description").toString()
rootProject.version = if (isBeta || isJenkins) "$minecraft-$commitHash" else if (isAlpha) "${rootProject.property("plugin_version")}-SNAPSHOT" else rootProject.property("plugin_version").toString()
rootProject.group = rootProject.property("project_group").toString()

rootProject.ext {
    set("version_name", if (isBeta) "${rootProject.version}" else "${rootProject.name} ${rootProject.version}")
    set("release_type", status)

    set("current_commit", commitHash)
    set("previous_commit", env.getOrDefault("previous_commit", "N/A"))
    set("build_number", buildNumber)

    set("mc_changelog", content.lines().convertList())
}