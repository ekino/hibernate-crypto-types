package com.ekino.oss.crypto.utils

internal class StringUUIDSaltManagerTest : StringSaltManagerBaseTest() {
  override val saltManager: StringSaltManager = StringUUIDSaltManager()
}
