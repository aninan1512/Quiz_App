plugins {
    application
    kotlin("jvm") version "1.9.24"
    id("org.openjfx.javafxplugin") version "0.1.0"
}

repositories {
    mavenCentral()
}

javafx {
    version = "21"
    modules = listOf("javafx.controls", "javafx.graphics")
}

application {
    // Kotlin main class ends with Kt
    mainClass.set("com.example.quizapp.QuizAppKt")
}

kotlin {
    jvmToolchain(21)
}

