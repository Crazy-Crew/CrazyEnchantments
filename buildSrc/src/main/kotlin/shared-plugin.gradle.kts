plugins {
    id("com.ryderbelserion.feather.core")

    id("java-plugin") apply false
}

val libs = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")

val git = feather.getGit()

val isBeta: Boolean = git.getCurrentBranch() == rootProject.property("beta_branch").toString()
val isAlpha: Boolean = git.getCurrentBranch() == rootProject.property("alpha_branch").toString()

val commitHash: String = git.getCurrentCommitHash().subSequence(0, 7).toString()
val content: String = if (isBeta) "[$commitHash](https://github.com/${rootProject.property("repository_owner")}/${rootProject.name}/commit/$commitHash) ${git.getCurrentCommit()}" else rootProject.file("changelog.md").readText(Charsets.UTF_8)

val minecraft = libs.findVersion("minecraft").get()

rootProject.description = rootProject.property("project_description").toString()
rootProject.version = if (isBeta) "$minecraft-$commitHash" else if (isAlpha) "${rootProject.property("plugin_version")}-SNAPSHOT" else rootProject.property("plugin_version").toString()
rootProject.group = rootProject.property("project_group").toString()

rootProject.ext {
    set("version_name", if (isBeta) "${rootProject.version}" else "${rootProject.name} ${rootProject.version}")
    set("release_type", if (isBeta) "beta" else if (isAlpha) "alpha" else "release")
    set("mc_changelog", content)
}