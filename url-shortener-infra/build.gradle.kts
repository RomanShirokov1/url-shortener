plugins {
  java
  jacoco
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(21))
  }
}

dependencies {
  implementation(project(":url-shortener-core"))

  // JSON
  implementation("com.fasterxml.jackson.core:jackson-databind:2.18.1")
  implementation("com.fasterxml.jackson.core:jackson-annotations:2.18.1")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.18.1")

  // Логи
  implementation("org.slf4j:slf4j-api:2.0.16")
  runtimeOnly("ch.qos.logback:logback-classic:1.5.12")

  testImplementation(platform("org.junit:junit-bom:5.11.3"))
  testImplementation("org.junit.jupiter:junit-jupiter")
  testImplementation("org.mockito:mockito-core:5.14.2")
  testImplementation("org.assertj:assertj-core:3.26.3")
}
