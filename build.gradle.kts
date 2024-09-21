import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType

plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.0.1"
}

group = "com.aurimasniekis.idea.typespec"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        webstorm("2024.2.1")
        bundledPlugin("JavaScript")
        bundledPlugin("NodeJS")

        bundledPlugin("org.jetbrains.plugins.textmate")

        pluginVerifier()
        zipSigner()
        instrumentationTools()
    }
}

intellijPlatform {
    pluginVerification {
        ides {
            ide(IntelliJPlatformType.WebStorm, "2024.2.1")
            recommended()
        }
    }
}
