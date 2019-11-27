package com.ekino.oss.crypto.hibernate

import com.ekino.oss.crypto.EncryptDecrypt
import com.vladmihalcea.hibernate.type.json.internal.JsonTypeDescriptor
import com.vladmihalcea.hibernate.type.util.ObjectMapperWrapper

class EncryptedJsonTypeDescriptor(objectMapperWrapper: ObjectMapperWrapper) : JsonTypeDescriptor(objectMapperWrapper) {

  override fun toString(value: Any?): String? {
    val jsonString = super.toString(value)
    return jsonString?.let { EncryptDecrypt.encrypt(it) }
  }

  override fun fromString(string: String?): Any? {
    val jsonString = string?.let { EncryptDecrypt.decrypt(it) }
    return super.fromString(jsonString)
  }
}
