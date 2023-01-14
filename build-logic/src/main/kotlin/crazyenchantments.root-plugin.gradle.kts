import task.BuildExtension
import task.ReleaseBuild
import task.ReleaseWebhook
import task.WebhookExtension

plugins {
    id("crazyenchantments.common-plugin")
}

tasks {
    // Creating the extension to be available on the root gradle
    val webhookExtension = extensions.create("webhook", WebhookExtension::class)

    val buildExtension = extensions.create("releaseBuild", BuildExtension::class)

    // Register the task
    register<ReleaseWebhook>("releaseWebhook") {
        extension = webhookExtension
    }

    register<ReleaseBuild>("releaseBuild") {
        extension = buildExtension
    }
}