plugins {
  val kotlinVersion = "1.3.72"
  kotlin("jvm") version kotlinVersion
  kotlin("plugin.spring") version kotlinVersion
  kotlin("plugin.jpa") version kotlinVersion
  `maven-publish`
  signing

  id("org.unbroken-dome.test-sets") version "3.0.1"
  id("com.ekino.oss.plugin.kotlin-quality") version "2.0.0"
}

group = "com.ekino.oss.crypto"
version = "1.3.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
  mavenCentral()
  jcenter()
}

dependencies {
  implementation(platform("org.springframework.boot:spring-boot-dependencies:2.2.4.RELEASE"))
  compileOnly("org.hibernate:hibernate-core")
  implementation("com.vladmihalcea:hibernate-types-52:2.9.3")

  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

  testImplementation("org.springframework.boot:spring-boot-starter-web")
  testImplementation("org.springframework.boot:spring-boot-starter-data-jpa")
  testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  testImplementation("org.flywaydb:flyway-core")
  testImplementation("org.springframework.boot:spring-boot-starter-test") {
    exclude(module = "junit-vintage-engine")
    exclude(group = "org.mockito")
    exclude(module = "assertj-core")
  }
  testRuntimeOnly("org.postgresql:postgresql")
  testImplementation("com.ekino.oss.jcv:jcv-core:1.4.2")
  testImplementation("com.ekino.oss.jcv-db:jcv-db-jdbc:0.0.4")
  testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.22")
}

tasks {
  withType<Test>().configureEach {
    useJUnitPlatform()
    jvmArgs(
      "-Dspring.test.constructor.autowire.mode=ALL",
      "-Duser.language=en"
    )
  }

  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
      freeCompilerArgs = freeCompilerArgs + listOf("-Xjsr305=strict")
      jvmTarget = JavaVersion.VERSION_11.toString()
    }
  }

  named("build").configure {
    dependsOn("integrationTest")
  }

  jacocoTestReport {
    executionData.from(file("$buildDir/jacoco/integrationTest.exec"))
  }

  register("printVersion") {
    doLast {
      val version: String by project
      println(version)
    }
  }

  withType<GenerateModuleMetadata> {
    enabled = false
  }
}

testSets {
  "integrationTest"()
}

val publicationName = "mavenJava"

java {
  withJavadocJar()
  withSourcesJar()
}

publishing {
  publications {
    register<MavenPublication>(publicationName) {
      pom {
        name.set("hibernate-crypto-types")
        description.set("hibernate-crypto-types add encrypted string and json datatypes")
        url.set("https://github.com/ekino/hibernate-crypto-types")
        licenses {
          license {
            name.set("MIT License (MIT)")
            url.set("https://opensource.org/licenses/mit-license")
          }
        }
        developers {
          developer {
            name.set("Philippe Agra")
            email.set("philippe.agra@ekino.com")
            organization.set("ekino")
            organizationUrl.set("https://www.ekino.com/")
          }
        }
        scm {
          connection.set("scm:git:git://github.com/ekino/hibernate-crypto-types.git")
          developerConnection.set("scm:git:ssh://github.com:ekino/hibernate-crypto-types.git")
          url.set("https://github.com/ekino/hibernate-crypto-types")
        }
        organization {
          name.set("ekino")
          url.set("https://www.ekino.com/")
        }
      }

      from(components["java"])
    }
  }

  repositories {
    maven {
      val ossrhUrl: String? by project
      val ossrhUsername: String? by project
      val ossrhPassword: String? by project

      url = uri(ossrhUrl ?: "")

      credentials {
        username = ossrhUsername
        password = ossrhPassword
      }
    }
  }
}

signing {
  sign(publishing.publications[publicationName])
}
