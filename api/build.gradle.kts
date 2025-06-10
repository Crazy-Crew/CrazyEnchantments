plugins {
    alias(libs.plugins.fix.javadoc)

    `maven-publish`
    `config-java`
}

project.description = "The official api for CrazyEnchantments"
project.group = "${rootProject.group}.api"

val projectVersion = "0.1.0"

dependencies {
    compileOnly(libs.bundles.adventure)

    compileOnly(libs.fusion.core)
}

java {
    withSourcesJar()
    withJavadocJar()
}

tasks {
    javadoc {
        val name = rootProject.name.replaceFirstChar { it.uppercase() }
        val options = options as StandardJavadocDocletOptions

        options.encoding = Charsets.UTF_8.name()
        options.overview("src/main/javadoc/overview.html")
        options.use()
        options.isDocFilesSubDirs = true
        options.windowTitle("$name $projectVersion API Documentation")
        options.docTitle("<h1>$name $projectVersion API</h1>")
        options.header = """<img src="https://cdn.modrinth.com/data/IwVOgYiT/cd62e2f6dfe377838f4c387462c19dbb3ca5a39c.webp" style="height:100%">"""
        options.bottom("Copyright © 2025 Ryder Belserion")
        options.linkSource(true)
        options.addBooleanOption("html5", true)
    }

    withType<com.jeff_media.fixjavadoc.FixJavadoc> {
        configureEach {
            newLineOnMethodParameters.set(false)
            keepOriginal.set(false)
        }
    }
}

publishing {
    repositories {
        maven {
            url = uri("https://repo.crazycrew.us/releases/")

            credentials(PasswordCredentials::class)
            authentication.create<BasicAuthentication>("basic")
        }
    }

    publications {
        create<MavenPublication>("mavenJava") {
            groupId = "${project.group}"
            artifactId = "api"
            version = projectVersion

            from(components["java"])
        }
    }
}