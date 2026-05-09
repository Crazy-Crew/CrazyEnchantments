import utils.convertList

plugins {
    id("com.ryderbelserion.feather.core")

    id("java-plugin") apply false
}

val libs = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")

val git = feather.getBuilder()
val utils = git.utils

val branch = utils.getRemoteBranch()
val hash = utils.getRemoteCommitHash()
val commit = utils.getRemoteCommitMessage(hash, "%B")

val isBeta: Boolean = branch == rootProject.property("beta_branch").toString()
val isAlpha: Boolean = branch == rootProject.property("alpha_branch").toString()
val buildNumber: String = System.getenv("BUILD_NUMBER") ?: "N/A"
val isJenkins: Boolean = buildNumber != "N/A"

val commitHash: String = hash.subSequence(0, 7).toString()
val content: String = if (isBeta || isJenkins) {
    "[$commitHash](https://github.com/${rootProject.property("repository_owner")}/${rootProject.name}/commit/$commitHash) $commit"
} else rootProject.file("changelog.md").readText(Charsets.UTF_8)

val minecraft = libs.findVersion("minecraft").get()

rootProject.description = rootProject.property("project_description").toString()
rootProject.version = if (isBeta) "$minecraft-$commitHash" else if (isAlpha) "${rootProject.property("plugin_version")}-SNAPSHOT" else rootProject.property("plugin_version").toString()
rootProject.group = rootProject.property("project_group").toString()

rootProject.ext {
    set("version_name", if (isBeta) "${rootProject.version}" else "${rootProject.name} ${rootProject.version}")
    set("release_type", if (isBeta || isJenkins) "beta" else if (isAlpha) "alpha" else "release")

    set("current_commit", commitHash)
    set("previous_commit", System.getenv("GIT_PREVIOUS_SUCCESSFUL_COMMIT") ?: "N/A")
    set("build_number", buildNumber)

    set("mc_changelog", content.lines().convertList())
}