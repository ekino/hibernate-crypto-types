# Hibernate crypto types

[![Build Status](https://travis-ci.org/ekino/hibernate-crypto-types.svg?branch=master)](https://travis-ci.org/ekino/hibernate-crypto-types)
[![GitHub (pre-)release](https://img.shields.io/github/release/ekino/hibernate-crypto-types/all.svg)](https://github.com/ekino/hibernate-crypto-types/releases)
[![Maven Central](https://img.shields.io/maven-central/v/com.ekino.oss.crypto/hibernate-crypto-types)](https://search.maven.org/search?q=a:hibernate-crypto-types)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=ekino_hibernate-crypto-types&metric=alert_status)](https://sonarcloud.io/dashboard?id=ekino_hibernate-crypto-types)

Library adding custom encrypted hibernate types

## Requirements

JDK 8+


## Usage

For example with Gradle Kotlin DSL :

```kotlin
implementation("com.ekino.oss.crypto:hibernate-crypto-types:1.2.0-SNAPSHOT")
```

NB : if you want to use snapshots you need to add the following configuration to your Gradle build script :

```kotlin
repositories {
  maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
}
```
