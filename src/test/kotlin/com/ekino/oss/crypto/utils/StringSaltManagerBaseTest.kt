package com.ekino.oss.crypto.utils

import assertk.all
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEqualTo
import org.junit.jupiter.api.Test

internal abstract class StringSaltManagerBaseTest {

  abstract val saltManager: StringSaltManager

  @Test
  internal fun `should add and remove salt to existing value`() {

    val sourceValue = "Some value"

    val valueWithSalt = saltManager.addSalt(sourceValue)
    assertThat(valueWithSalt).all {
      isNotEqualTo(sourceValue)
      contains(sourceValue)
    }

    val valueWithoutSalt = saltManager.removeSalt(valueWithSalt)
    assertThat(valueWithoutSalt).isEqualTo(sourceValue)
  }
}
