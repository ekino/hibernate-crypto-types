# Hibernate crypto types

[![Build Status](https://travis-ci.org/ekino/hibernate-crypto-types.svg?branch=master)](https://travis-ci.org/ekino/hibernate-crypto-types)
[![GitHub (pre-)release](https://img.shields.io/github/release/ekino/hibernate-crypto-types/all.svg)](https://github.com/ekino/hibernate-crypto-types/releases)
[![Maven Central](https://img.shields.io/maven-central/v/com.ekino.oss.crypto/hibernate-crypto-types)](https://search.maven.org/search?q=a:hibernate-crypto-types)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=ekino_hibernate-crypto-types&metric=alert_status)](https://sonarcloud.io/dashboard?id=ekino_hibernate-crypto-types)

Library adding custom encrypted hibernate types

## Requirements

JDK 8+

## Installation

For example with Gradle Kotlin DSL :

```kotlin
implementation("com.ekino.oss.crypto:hibernate-crypto-types:1.3.0-SNAPSHOT")
```

NB : if you want to use snapshots you need to add the following configuration to your Gradle build script :

```kotlin
repositories {
  maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
}
```

## Usage

Following the example you can find in the `integrationTest` source set, you need:

1. To define the secrets configuration to encrypt your data
2. To define the new hibernate types
3. Specify the column type to use for each field you want to encrypt

### Secrets configuration

Create a bean to deserialize you configuration properties:
```kotlin
@ConstructorBinding
@ConfigurationProperties(prefix = "ekino.encrypt")
data class EncryptProperties(val secret: String, val iv: String)
```
_See `com.ekino.oss.hibernate.config.EncryptProperties` in the `integrationTest` source set._

Add your secrets in your `application.yml` configuration:
```yaml
ekino:
  encrypt:
    secret: "my_very_long_secret_32_character"
    iv: "initialization_V"
```
_See `resources/application.yml` in the `integrationTest` source set._

Then enable the configuration with the following configuration bean:
```kotlin
@Configuration
@EnableConfigurationProperties(EncryptProperties::class)
class EncryptConfig(val encryptProperties: EncryptProperties) : CommandLineRunner {

  override fun run(vararg args: String?) {
    require(EncryptCredentialsHolder.SECRET == null)
    require(EncryptCredentialsHolder.IV == null)
    EncryptCredentialsHolder.SECRET = encryptProperties.secret
    EncryptCredentialsHolder.IV = encryptProperties.iv
  }
}
```
_See `com.ekino.oss.hibernate.config.EncryptConfig` in the `integrationTest` source set._

### Type definitions

Here is a list of the new hibernate types provided by this library:

Type name|Target column type to encrypt|Comment
---|---|---|
`encrypted-text`|`String`|As the resulting encrypted text will still be the same for the same raw source, you may want to use `encrypted-text-with-salt` to change the result
`encrypted-text-with-salt`|`String`|The resulting encrypted text will always be different for the same raw source
`encrypted-json`|Any **non-primitive** `Object`|As the resulting encrypted text will still be the same for the same raw source, you may want to use `encrypted-json-salt-wrap` to change the result. **Warning**: if use with a `String` target type, the data won't be encrypted, please use `encrypted-text` or `encrypted-text-with-salt` instead
`encrypted-json-salt-wrap`|Dedicated to `Enum` (or simple objects) but works for any **non-primitive** `Object`|The resulting encrypted text will always be different for the same raw source. **Warning**: if use with a `String` target type, the data won't be encrypted, please use `encrypted-text` or `encrypted-text-with-salt` instead


#### Using an abstract entity

You can create a super abstract entity that will be extended by all your entities:
````kotlin
/**
 * Common superclass for all the entities.
 */
@MappedSuperclass
@TypeDefs(
  TypeDef(name = "encrypted-text", typeClass = EncryptedStringType::class),
  TypeDef(name = "encrypted-text-with-salt", typeClass = EncryptedStringWithSaltType::class),
  TypeDef(name = "encrypted-json", typeClass = EncryptedJsonType::class),
  TypeDef(name = "encrypted-json-salt-wrap", typeClass = EncryptedJsonSaltWrapType::class)
)
abstract class AbstractEntity(
  // Put here some common field you may want to share for your sub-entities
)
````
_See `com.ekino.oss.hibernate.model.AbstractEntity` in the `integrationTest` source set._

### Specify your fields to encrypt

Extends your previously defined abstract entity to apply your column type on demand:
````kotlin
@Entity
class Person(

  var name: String,

  @Type(type = "encrypted-text")
  @Column(columnDefinition = "encrypted-text")
  var password: String? = null,

  @Type(type = "encrypted-json")
  @Column(columnDefinition = "encrypted-json")
  var privateData: PrivateData? = null,

  @Type(type = "encrypted-json-salt-wrap")
  @Column(columnDefinition = "encrypted-json-salt-wrap")
  var someValue: SomeEnum? = null,

  @Type(type = "encrypted-text-with-salt")
  @Column(columnDefinition = "encrypted-text-with-salt")
  var simpleValue: String? = null

) : AbstractEntity()
````
_See `com.ekino.oss.hibernate.model.Person` in the `integrationTest` source set._
