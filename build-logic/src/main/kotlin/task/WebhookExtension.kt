package task

import com.google.gson.annotations.SerializedName
import java.awt.Color
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/** Extension to simplify customizing the webhook. */
abstract class WebhookExtension {

    private var content: String = ""
    private var username: String = ""
    private var avatar: String = ""
    private val embeds: MutableList<Embed> = mutableListOf()

    fun content(content: String) {
        this.content = content;
    }

    fun username(username: String) {
        this.username = username;
    }

    fun avatar(avatar: String) {
        this.avatar = avatar;
    }

    fun embeds(builder: EmbedsBuilder.() -> Unit) {
        embeds.addAll(EmbedsBuilder().apply(builder).build())
    }

    internal fun build(): Webhook {
        return Webhook(
            content,
            username,
            avatar,
            false,
            embeds.toList()
        )
    }

    class EmbedsBuilder {
        private val embeds: MutableList<Embed> = mutableListOf()

        fun embed(builder: EmbedBuilder.() -> Unit) {
            embeds.add(EmbedBuilder().apply(builder).build())
        }

        internal fun build() = embeds.toList()
    }

    class EmbedBuilder {
        private var title: String? = null
        private var description: String? = null
        private var url: String? = null
        private var timestamp: String = ""
        private var color: Int? = null
        private var footer: Footer? = null
        private var image: Image? = null
        private var thumbnail: Image? = null
        private var provider: Provider? = null
        private var author: Author? = null
        private var fields: List<Field>? = null

        fun title(title: String) {
            this.title = title
        }

        fun description(description: String) {
            this.description = description
        }

        fun url(url: String) {
            this.url = url
        }

        fun timestamp(date: LocalDateTime) {
            this.timestamp = date.format(DateTimeFormatter.ISO_DATE_TIME)
        }

        fun color(color: Color) {
            this.color = color.toInt()
        }

        fun footer(text: String, icon: String? = null) {
            this.footer = Footer(text, icon)
        }

        fun image(url: String) {
            this.image = Image(url)
        }

        fun thumbnail(url: String) {
            this.thumbnail = Image(url)
        }

        fun provider(name: String? = null, url: String? = null) {
            this.provider = Provider(name, url)
        }

        fun author(name: String, url: String? = null, icon: String? = null) {
            this.author = Author(name, url, icon)
        }

        fun fields(builder: FieldsBuilder.() -> Unit) {
            this.fields = FieldsBuilder().apply(builder).build()
        }

        internal fun build() = Embed(
            title,
            description,
            url,
            timestamp,
            color,
            footer,
            image,
            thumbnail,
            provider,
            author,
            fields,
        )
    }

    class FieldsBuilder {
        private val fields: MutableList<Field> = mutableListOf()

        fun field(name: String, value: String, inline: Boolean = false) {
            fields.add(Field(name, value, inline))
        }

        internal fun build() = fields.toList()
    }

    data class Webhook(
        val content: String,
        val username: String,
        @SerializedName("avatar_url") val avatarUrl: String,
        val tts: Boolean,
        val embeds: List<Embed>,
    )

    data class Embed(
        val title: String?,
        val description: String?,
        val url: String?,
        val timestamp: String,
        val color: Int?,
        val footer: Footer?,
        val image: Image?,
        val thumbnail: Image?,
        val provider: Provider?,
        val author: Author?,
        val fields: List<Field>?,
    )

    data class Image(
        val url: String,
    )

    data class Author(
        val name: String,
        val url: String?,
        @SerializedName("icon_url") val iconUrl: String?,
    )

    data class Provider(
        val name: String?,
        val url: String?,
    )

    data class Footer(
        val text: String,
        @SerializedName("icon_url") val iconUrl: String?,
    )

    data class Field(
        val name: String,
        val value: String,
        val inline: Boolean?,
    )
}

/** Turns color into integer for webhook, using this because [Color]'s rgb method returns negatives. */
private fun Color.toInt(): Int {
    val red = red shl 16 and 0xFF0000
    val green = green shl 8 and 0x00FF00
    val blue = blue and 0x0000FF

    return red or green or blue
}