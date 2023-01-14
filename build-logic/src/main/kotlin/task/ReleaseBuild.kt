package task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

abstract class ReleaseBuild : DefaultTask() {

    /** Configured extension. */
    @get:Input
    lateinit var extension: BuildExtension

    @TaskAction
    fun release() {
        extension.build()
    }
}