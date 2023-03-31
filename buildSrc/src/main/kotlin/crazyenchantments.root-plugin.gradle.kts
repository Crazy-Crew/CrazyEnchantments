import task.ReleaseWebhook
import task.WebhookExtension

plugins {
    `java-library`
    `maven-publish`

    id("com.github.johnrengelman.shadow")
}

repositories {
    maven("https://repo.triumphteam.dev/snapshots/")

    maven("https://repo.crazycrew.us/api/")

    maven("https://jitpack.io/")

    mavenCentral()
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of("17"))
}

tasks {
    // Creating the extension to be available on the root gradle
    val webhookExtension = extensions.create("webhook", WebhookExtension::class)

    // Register the task
    register<ReleaseWebhook>("webhook") {
        extension = webhookExtension
    }

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