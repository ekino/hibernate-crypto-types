package com.ekino.oss.crypto.hibernate

import com.ekino.oss.crypto.EncryptDecrypt
import com.ekino.oss.crypto.utils.StringSaltManager
import com.ekino.oss.crypto.utils.StringUUIDSaltManager
import com.vladmihalcea.hibernate.type.ImmutableType
import org.hibernate.engine.spi.SharedSessionContractImplementor
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Types

/**
 * Maps a [String] on a encrypted text with salt type.
 */
class EncryptedStringWithSaltType : ImmutableType<String>(String::class.java) {

  private val saltManager: StringSaltManager = StringUUIDSaltManager()

  override fun sqlTypes(): IntArray = intArrayOf(Types.VARCHAR)

  override fun get(rs: ResultSet, names: Array<out String>, session: SharedSessionContractImplementor, owner: Any): String? {
    val value = rs.getObject(names[0])
    return value
      ?.toString()
      ?.let(EncryptDecrypt::decrypt)
      ?.let(saltManager::removeSalt)
  }

  override fun set(st: PreparedStatement, value: String?, index: Int, session: SharedSessionContractImplementor) {
    val encode = value
      ?.let(saltManager::addSalt)
      ?.let(EncryptDecrypt::encrypt)
    st.setObject(index, encode, Types.VARCHAR)
  }
}
