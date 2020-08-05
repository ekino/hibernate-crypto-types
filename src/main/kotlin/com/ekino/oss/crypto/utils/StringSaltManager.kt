package com.ekino.oss.crypto.utils

interface StringSaltManager {

  fun addSalt(value: String): String
  fun removeSalt(value: String): String
}
