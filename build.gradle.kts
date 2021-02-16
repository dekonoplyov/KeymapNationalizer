plugins {
    id("org.jetbrains.intellij") version "0.4.21"
    java
    kotlin("jvm") version "1.3.72"
}

group = "com.dekonoplyov"
version = "0.2.6"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8" ))
    implementation(kotlin("reflect"))

    testImplementation("junit", "junit", "4.12")
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version = "2020.1.2"
}
configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    publishPlugin {
        token(System.getenv("ORG_GRADLE_PROJECT_intellijPublishToken"))
    }
    patchPluginXml {
        untilBuild("213.*")
    }
}
tasks.getByName<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
    changeNotes("""
    - Fix failed to parse % key. Update intellij version.
    """)
}