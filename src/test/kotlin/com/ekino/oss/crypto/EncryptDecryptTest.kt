package com.ekino.oss.crypto

import assertk.all
import assertk.assertThat
import assertk.assertions.containsOnly
import assertk.assertions.hasMessage
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.Callable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

private const val sampleSecret = "my_very_long_secret_32_character"
private const val sampleIV = "initialization_V"

private const val sampleDecryptedValue = "value"
private const val sampleEncryptedValue = "SNtsQLpTO09uM81PsRc7rA=="

internal class EncryptDecryptTest {
  @BeforeEach
  @AfterEach
  internal fun resetCredentials() {
    EncryptCredentialsHolder.reset()
  }

  @Test
  internal fun `should fail to encrypt without secret`() {
    assertThat { EncryptDecrypt.encrypt("test") }
      .isFailure()
      .isInstanceOf(IllegalArgumentException::class.java)
      .hasMessage("SECRET has to be initialized")
  }

  @Test
  internal fun `should fail to encrypt without iv`() {
    EncryptCredentialsHolder.SECRET = sampleSecret

    assertThat { EncryptDecrypt.encrypt("test") }
      .isFailure()
      .isInstanceOf(IllegalArgumentException::class.java)
      .hasMessage("IV key has to be initialized")
  }

  @Test
  internal fun `should encrypt string`() {
    EncryptCredentialsHolder.init()

    assertThat(EncryptDecrypt.encrypt(sampleDecryptedValue)).isEqualTo(sampleEncryptedValue)
  }

  @Test
  internal fun `should decrypt string`() {
    EncryptCredentialsHolder.init()
    assertThat(EncryptDecrypt.decrypt(sampleEncryptedValue)).isEqualTo(sampleDecryptedValue)
  }

  @Test
  internal fun `should thread safely encrypt string`() {
    EncryptCredentialsHolder.init()

    val threadCount = 100
    val results = runInThreads(threadCount) {
      EncryptDecrypt.encrypt(sampleDecryptedValue)
    }
    assertThat(results).all {
      hasSize(threadCount)
      containsOnly(sampleEncryptedValue)
    }
  }

  @Test
  internal fun `should thread safely decrypt string`() {
    EncryptCredentialsHolder.init()

    val threadCount = 100
    val results = runInThreads(threadCount) {
      EncryptDecrypt.decrypt(sampleEncryptedValue)
    }
    assertThat(results).all {
      hasSize(threadCount)
      containsOnly(sampleDecryptedValue)
    }
  }

  @Suppress("SameParameterValue")
  private fun <T> runInThreads(threadCount: Int, block: (threadIndex: Int) -> T): List<T> {
    val latch = CountDownLatch(1)
    val executorService = Executors.newFixedThreadPool(threadCount)
    val futures = (1..threadCount).map {
      Callable {
        latch.await()
        block(it)
      }
    }
      .map { executorService.submit(it) }
    latch.countDown()
    return futures.map { it.get() }
  }

  private fun EncryptCredentialsHolder.reset() {
    SECRET = null
    IV = null
  }

  private fun EncryptCredentialsHolder.init() {
    SECRET = sampleSecret
    IV = sampleIV
  }
}
