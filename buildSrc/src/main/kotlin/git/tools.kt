package git

import org.gradle.api.Project
import java.io.ByteArrayOutputStream

fun formatLog(hash: String, message: String, project: String): String {
    return "[$hash](https://github.com/Crazy-Crew/$project/commit/$hash) $message"
}

fun Project.latestCommitHash(): String {
    return runGitCommand(listOf("rev-parse", "--short", "HEAD"))
}

fun Project.latestCommitMessage(): String {
    return runGitCommand(listOf("log", "-1", "--pretty=%B"))
}

fun Project.branchName(): String {
    return runGitCommand(listOf("rev-parse", "--abbrev-ref", "HEAD"))
}

fun Project.runGitCommand(value: List<String>): String {
    val output: String = ByteArrayOutputStream().use {
        exec {
            executable("git")
            args(value)
            standardOutput = it
        }

        it.toString(Charsets.UTF_8).trim()
    }

    return output
}