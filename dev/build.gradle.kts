plugins {
    id("paper-plugin")
}

repositories {
    maven("https://repo.triumphteam.dev/snapshots/")
}

dependencies {
    implementation(libs.triumph.cmds)
}

tasks {
    shadowJar {
        archiveClassifier.set("")

        exclude("META-INF/**")

        listOf(
            "dev.triumphteam"
        ).forEach {
            relocate(it, "libs.$it")
        }
    }

    processResources {
        val properties = hashMapOf(
            "name" to rootProject.name,
            "version" to rootProject.version,
            "group" to "${project.group}.paper",
            "description" to rootProject.description,
            "apiVersion" to rootProject.properties["apiVersion"],
            "authors" to rootProject.properties["authors"],
            "website" to rootProject.properties["website"]
        )

        inputs.properties(properties)

        filesMatching("plugin.yml") {
            expand(properties)
        }
    }
}