package utils

fun String.updateMarkdown(): String {
    val list = split("\n")

    val builder = StringBuilder(list.size)

    list.forEach {
        if (it.contains("##")) {
            val text = it.replace("## ", "")

            builder.append("**${text}**").append("\n")
        } else {
            builder.append(it).append("\n")
        }
    }

    return builder.toString()
}

fun List<String>.convertList(): String {
    val builder = StringBuilder(size)

    forEach {
        builder.append(it).append("\n")
    }

    return builder.toString()
}