package com.ekino.oss.crypto.hibernate

import com.vladmihalcea.hibernate.type.AbstractHibernateType
import com.vladmihalcea.hibernate.type.util.Configuration
import org.hibernate.usertype.DynamicParameterizedType
import java.util.Properties

class EncryptedJsonSaltWrapType :
  AbstractHibernateType<Any?>(EncryptedJsonSqlTypeDescriptor.INSTANCE, EncryptedJsonSaltWrapTypeDescriptor(Configuration.INSTANCE.objectMapperWrapper)), DynamicParameterizedType {

  override fun getName(): String {
    return "encrypted-json-salt-wrap"
  }

  override fun setParameterValues(parameters: Properties) {
    (javaTypeDescriptor as EncryptedJsonSaltWrapTypeDescriptor).setParameterValues(parameters)
  }
}
