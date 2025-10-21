plugins {
    id("com.gradleup.shadow")

    id("config-java")
}

tasks {
    shadowJar {
        archiveClassifier.set("")

        exclude("META-INF/**")
    }
}