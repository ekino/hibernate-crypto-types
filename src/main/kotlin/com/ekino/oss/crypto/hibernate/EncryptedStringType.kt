package com.ekino.oss.crypto.hibernate

import com.ekino.oss.crypto.EncryptDecrypt
import com.vladmihalcea.hibernate.type.ImmutableType
import org.hibernate.engine.spi.SharedSessionContractImplementor
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Types

/**
 * Maps a [String] on a encrypted text type.
 */
class EncryptedStringType : ImmutableType<String>(String::class.java) {

  override fun sqlTypes(): IntArray = intArrayOf(Types.VARCHAR)

  override fun get(rs: ResultSet, names: Array<out String>, session: SharedSessionContractImplementor, owner: Any): String? {
    val value = rs.getObject(names[0])
    return value?.let { EncryptDecrypt.decrypt(it.toString()) }
  }

  override fun set(st: PreparedStatement, value: String?, index: Int, session: SharedSessionContractImplementor) {
    val encode = value?.let { EncryptDecrypt.encrypt(it) }
    st.setObject(index, encode, Types.VARCHAR)
  }
}
