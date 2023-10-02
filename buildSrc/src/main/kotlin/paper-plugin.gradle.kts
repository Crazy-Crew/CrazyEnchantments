plugins {
    id("io.papermc.paperweight.userdev")

    id("root-plugin")
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    paperweight.paperDevBundle("1.20.2-R0.1-SNAPSHOT")
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }

    reobfJar {
        outputJar.set(file("${project.layout.buildDirectory.get()}/libs/${rootProject.name}-${rootProject.version}.jar"))
    }
}