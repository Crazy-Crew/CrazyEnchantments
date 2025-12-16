import org.gradle.kotlin.dsl.support.uppercaseFirstChar

plugins {
    id("com.gradleup.shadow")
    id("java-plugin")
}

tasks {
    shadowJar {
        archiveBaseName.set("${rootProject.name}-${project.name.uppercaseFirstChar()}-${rootProject.version}")
        archiveClassifier.set("")

        destinationDirectory.set(rootProject.layout.projectDirectory.dir("jars"))

        exclude("META-INF/**")
    }
}