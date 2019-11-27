package com.ekino.oss.crypto

import java.nio.charset.StandardCharsets
import java.security.GeneralSecurityException
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object EncryptDecrypt {

  private const val CIPHER_TRANSFORMATION = "AES/CBC/PKCS5PADDING"
  private const val ALGORITHM = "AES"

  private val secretKeySpec: SecretKeySpec by lazy {
    val secretKey = requireNotNull(EncryptCredentialsHolder.SECRET) { "SECRET has to be initialized" }
    SecretKeySpec(secretKey.toByteArray(StandardCharsets.UTF_8), ALGORITHM)
  }
  private val ivParameterSpec: IvParameterSpec by lazy {
    val ivKey = requireNotNull(EncryptCredentialsHolder.IV) { "IV key has to be initialized" }
    IvParameterSpec(ivKey.toByteArray(StandardCharsets.UTF_8))
  }

  private val encrypter: Cipher by lazy {
    Cipher.getInstance(CIPHER_TRANSFORMATION).apply { init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec) }
  }

  private val decrypter: Cipher by lazy {
    Cipher.getInstance(CIPHER_TRANSFORMATION).apply { init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec) }
  }

  @Throws(GeneralSecurityException::class)
  fun encrypt(toBeEncrypt: String): String {
    val encrypted = encrypter.doFinal(toBeEncrypt.toByteArray())
    return Base64.getEncoder().encodeToString(encrypted)
  }

  @Throws(GeneralSecurityException::class)
  fun decrypt(encrypted: String): String {
    val decryptedBytes = decrypter.doFinal(Base64.getDecoder().decode(encrypted))
    return String(decryptedBytes)
  }
}
