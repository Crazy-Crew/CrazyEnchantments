import org.gradle.kotlin.dsl.support.uppercaseFirstChar

plugins {
    `root-plugin`
}

tasks {
    assemble {
        val jarsDir = File("$rootDir/jars")

        doFirst {
            delete(jarsDir)

            jarsDir.mkdirs()
        }

        subprojects.filter { it.name == "paper" || it.name == "fabric" }.forEach { project ->
            dependsOn(":${project.name}:build")

            doLast {
                runCatching {
                    val file = File("$jarsDir/${project.name.lowercase()}")

                    file.mkdirs()

                    copy {
                        from(project.layout.buildDirectory.file("libs/${rootProject.name}-${rootProject.version}.jar"))
                        into(file)
                    }
                }.onSuccess {
                    File("$jarsDir/${project.name.lowercase()}").list()?.let { it1 -> println(it1.size) }

                    // Delete to save space on jenkins.
                    delete(project.layout.buildDirectory.get())
                    delete(rootProject.layout.buildDirectory.get())
                }.onFailure {
                    println("Failed to copy file out of build folder into jars directory: Likely does not exist.")
                }
            }
        }
    }
}