plugins {
    `java-library`
}

tasks {
    assemble {
        subprojects.forEach { project -> dependsOn(":${project.name}:build") }

        doLast {
            val directory = File(rootDir, "jars");

            if (directory.exists()) directory.delete()

            directory.mkdirs()

            copy {
                from(project("paper").layout.buildDirectory.file("libs/${rootProject.name}-${rootProject.version}.jar").get())
                into(directory)
            }
        }
    }
}

subprojects {
    apply(plugin = "java-library")

    repositories {
        maven("https://repo.crazycrew.us/releases")

        maven("https://jitpack.io/")

        mavenCentral()
    }

    if (name == "paper") {
        repositories {
            maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

            maven("https://repo.codemc.io/repository/maven-public/")

            maven("https://repo.triumphteam.dev/snapshots/")

            maven("https://repo.oraxen.com/releases/")

            flatDir { dirs("libs") }
        }
    }

    tasks {
        compileJava {
            options.encoding = Charsets.UTF_8.name()
            options.release.set(17)
        }

        javadoc {
            options.encoding = Charsets.UTF_8.name()
        }

        processResources {
            filteringCharset = Charsets.UTF_8.name()
        }
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of("17"))
    }
}