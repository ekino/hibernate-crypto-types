package com.ekino.oss.crypto.hibernate

import com.vladmihalcea.hibernate.type.AbstractHibernateType
import com.vladmihalcea.hibernate.type.util.Configuration
import org.hibernate.usertype.DynamicParameterizedType
import java.util.Properties

class EncryptedJsonType :
  AbstractHibernateType<Any?>(EncryptedJsonSqlTypeDescriptor.INSTANCE, EncryptedJsonTypeDescriptor(Configuration.INSTANCE.objectMapperWrapper)), DynamicParameterizedType {

  override fun getName(): String {
    return "encrypted-json"
  }

  override fun setParameterValues(parameters: Properties) {
    (javaTypeDescriptor as EncryptedJsonTypeDescriptor).setParameterValues(parameters)
  }
}
