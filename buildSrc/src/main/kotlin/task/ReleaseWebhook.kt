package task

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.append
import io.ktor.serialization.gson.gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.extra

/** Task to send webhooks to discord. */
abstract class ReleaseWebhook : DefaultTask() {

    /** Configured extension. */
    @get:Input
    lateinit var extension: WebhookExtension

    /** Ktor client for easy requests. */
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            gson()
        }
    }

    @TaskAction
    fun webhook() {
        // The webhook url configured in the gradle.properties
        val url = System.getenv("DISCORD_WEBHOOK")

        runBlocking(Dispatchers.IO) {
            val response = client.post(url) {
                headers {
                    append(HttpHeaders.ContentType, ContentType.Application.Json)
                }

                setBody(extension.build())
            }

            // Should be using logger, but eh
            println("Webhook result: ${response.status}")
        }
    }
}