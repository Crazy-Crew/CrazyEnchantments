package task

@SuppressWarnings("all")
abstract class BuildExtension {

    private val version = System.getProperty("version")

    private val beta = System.getProperty("isBeta").toBoolean()

    private val description = System.getProperty("description")

    private val name = System.getProperty("name")

    private val group = System.getProperty("group")

    private val extension = System.getProperty("extension")

    fun isBeta(): Boolean = this.beta

    fun getProjectVersion(): String = if (isBeta()) "${this.version}+Beta" else this.version

    fun getProjectName(): String = this.name

    fun getProjectDescription(): String = this.description

    fun getProjectGroup(): String = this.group

    fun getProjectType() = if (isBeta()) "beta" else "release"

    fun getExtension(): String = this.extension

    internal fun build(): Build {
        return Build(
            this.beta,
            this.version,
            this.name,
            this.group
        )
    }

    data class Build(
        val beta: Boolean,
        val version: String,
        val name: String,
        val group: String
    )
}