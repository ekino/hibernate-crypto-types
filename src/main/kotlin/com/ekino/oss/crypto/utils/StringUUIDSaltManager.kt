package com.ekino.oss.crypto.utils

import java.util.UUID

/**
 * Salt is a UUID (36 chars)
 */
private const val SALT_LENGTH = 36

class StringUUIDSaltManager : StringSaltManager {

  override fun addSalt(value: String): String =
    "${UUID.randomUUID()}$value"

  override fun removeSalt(value: String): String =
    value.removeRange(0 until SALT_LENGTH)
}
