package com.ekino.oss.crypto

import assertk.assertThat
import assertk.assertions.hasMessage
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder

@TestMethodOrder(value = MethodOrderer.OrderAnnotation::class)
internal class EncryptDecryptTest {

  @Test
  @Order(1)
  internal fun `should fail to encrypt without secret`() {
    assertThat { EncryptDecrypt.encrypt("test") }
      .isFailure()
      .isInstanceOf(IllegalArgumentException::class.java)
      .hasMessage("SECRET has to be initialized")
  }

  @Test
  @Order(2)
  internal fun `should fail to encrypt without iv`() {
    EncryptCredentialsHolder.SECRET = "my_very_long_secret_32_character"

    assertThat { EncryptDecrypt.encrypt("test") }
      .isFailure()
      .isInstanceOf(IllegalArgumentException::class.java)
      .hasMessage("IV key has to be initialized")
  }

  @Test
  @Order(3)
  internal fun `should encrypt string`() {
    EncryptCredentialsHolder.IV = "initialization_V"

    assertThat(EncryptDecrypt.encrypt("value")).isEqualTo("SNtsQLpTO09uM81PsRc7rA==")
  }

  @Test
  @Order(4)
  internal fun `should decrypt string`() {
    assertThat(EncryptDecrypt.decrypt("SNtsQLpTO09uM81PsRc7rA==")).isEqualTo("value")
  }
}
