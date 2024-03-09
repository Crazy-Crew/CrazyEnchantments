plugins {
    id("root-plugin")
}

dependencies {
    api(libs.configme) {
        exclude(group = "org.yaml", module = "snakeyaml")
    }
}