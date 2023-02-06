import task.ReleaseWebhook
import task.WebhookExtension

plugins {
    id("crazyenchantments.base-plugin")
}

repositories {
    maven("https://repo.triumphteam.dev/snapshots/")

    maven("https://repo.crazycrew.us/plugins/")

    maven("https://libraries.minecraft.net/")

    maven("https://jitpack.io/")

    mavenCentral()
}

tasks {
    // Creating the extension to be available on the root gradle
    val webhookExtension = extensions.create("webhook", WebhookExtension::class)

    // Register the task
    register<ReleaseWebhook>("releaseWebhook") {
        extension = webhookExtension
    }
}