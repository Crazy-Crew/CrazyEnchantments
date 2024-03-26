plugins {
    id("root-plugin")
}

dependencies {
    compileOnly(libs.cluster.api)

    api(libs.config.me) {
        exclude(group = "org.yaml", module = "snakeyaml")
    }
}