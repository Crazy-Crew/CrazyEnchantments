plugins {
    `config-paper`
}

project.description = "Paper version of CrazyEnchantments!!"
project.group = "${rootProject.group}.paper"

dependencies {
    implementation(project(":crazyenchantments-common"))

    implementation(libs.fusion.paper)

    implementation(libs.yardwatch)
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    runPaper.folia.registerTask()

    runServer {
        jvmArgs("-Dnet.kyori.ansi.colorLevel=truecolor")

        defaultCharacterEncoding = Charsets.UTF_8.name()

        minecraftVersion(libs.versions.minecraft.get())
    }
}