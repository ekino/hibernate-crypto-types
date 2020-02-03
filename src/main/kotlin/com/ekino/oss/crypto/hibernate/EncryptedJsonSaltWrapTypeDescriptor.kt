package com.ekino.oss.crypto.hibernate

import com.ekino.oss.crypto.EncryptDecrypt
import com.ekino.oss.crypto.model.JsonSaltWrap
import com.vladmihalcea.hibernate.type.json.internal.JsonTypeDescriptor
import com.vladmihalcea.hibernate.type.util.ObjectMapperWrapper
import java.util.UUID

class EncryptedJsonSaltWrapTypeDescriptor(private val objectMapperWrapper: ObjectMapperWrapper) : JsonTypeDescriptor(objectMapperWrapper) {

  override fun toString(value: Any?): String? = super.toString(value)
    ?.let { JsonSaltWrap(value = it, salt = UUID.randomUUID().toString()) }
    ?.let(objectMapperWrapper::toString)
    ?.let(EncryptDecrypt::encrypt)

  override fun fromString(string: String?): Any? {
    val wrappedValue = string
      ?.let(EncryptDecrypt::decrypt)
      ?.let { objectMapperWrapper.fromString(it, JsonSaltWrap::class.java) }
      ?.value
    return super.fromString(wrappedValue)
  }
}
