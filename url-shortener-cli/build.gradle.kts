plugins {
  java
  application
  jacoco
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(21))
  }
}

dependencies {
  implementation(project(":url-shortener-core"))
  implementation(project(":url-shortener-infra"))

  implementation("info.picocli:picocli:4.7.6")
  annotationProcessor("info.picocli:picocli-codegen:4.7.6")

  testImplementation(platform("org.junit:junit-bom:5.11.3"))
  testImplementation("org.junit.jupiter:junit-jupiter")
  testImplementation("org.assertj:assertj-core:3.26.3")
}

application {
  mainClass.set("dev.shorty.cli.Main")
}

tasks.jar {
  manifest {
    attributes["Main-Class"] = "dev.shorty.cli.Main"
  }
}
