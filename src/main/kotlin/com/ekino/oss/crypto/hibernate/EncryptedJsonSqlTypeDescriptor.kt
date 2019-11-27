package com.ekino.oss.crypto.hibernate

import com.vladmihalcea.hibernate.type.json.internal.JsonStringSqlTypeDescriptor
import java.sql.Types

class EncryptedJsonSqlTypeDescriptor : JsonStringSqlTypeDescriptor() {
  override fun getSqlType(): Int {
    return Types.VARCHAR
  }

  companion object {
    val INSTANCE = EncryptedJsonSqlTypeDescriptor()
  }
}
