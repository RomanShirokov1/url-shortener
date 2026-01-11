plugins {
  id("com.diffplug.spotless") version "7.0.0.BETA4"
}

allprojects {
  group = "dev.shorty"
  version = "0.1.0"

  repositories {
    mavenCentral()
  }
}

subprojects {
  tasks.withType<Test>().configureEach {
    useJUnitPlatform()
  }

  tasks.matching { it.name == "jacocoTestReport" }.configureEach {
    (this as? org.gradle.testing.jacoco.tasks.JacocoReport)?.reports?.let {
      it.xml.required.set(true)
      it.html.required.set(true)
    }
  }
}

spotless {
  java {
    target("**/*.java")
    googleJavaFormat()
    indentWithSpaces()
    trimTrailingWhitespace()
    endWithNewline()
  }
}
