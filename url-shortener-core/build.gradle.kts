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
  implementation("com.fasterxml.jackson.core:jackson-annotations:2.18.1")

  testImplementation(platform("org.junit:junit-bom:5.11.3"))
  testImplementation("org.junit.jupiter:junit-jupiter")
  testImplementation("org.mockito:mockito-core:5.14.2")
  testImplementation("org.assertj:assertj-core:3.26.3")
}
