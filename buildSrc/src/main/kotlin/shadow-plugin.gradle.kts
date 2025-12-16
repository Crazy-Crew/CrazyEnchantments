plugins {
    id("com.gradleup.shadow")
    id("java-plugin")
}

tasks {
    shadowJar {
        archiveClassifier.set("")

        exclude("META-INF/**")
    }
}