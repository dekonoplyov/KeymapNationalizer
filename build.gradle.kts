plugins {
    id("org.jetbrains.intellij") version "0.4.21"
    java
    kotlin("jvm") version "1.6.20"
}

group = "com.dekonoplyov"
version = "0.2.7"

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
    version = "2021.1"
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
        untilBuild("223.*")
    }
}
tasks.getByName<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
    changeNotes("""
    - Update intellij and kotlin plugin versions
    """)
}