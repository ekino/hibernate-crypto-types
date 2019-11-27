package com.ekino.oss.hibernate.config

import com.ekino.oss.crypto.EncryptCredentialsHolder
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(EncryptProperties::class)
class EncryptConfig(val encryptProperties: EncryptProperties) : CommandLineRunner {

  override fun run(vararg args: String?) {
    require(EncryptCredentialsHolder.SECRET == null)
    require(EncryptCredentialsHolder.IV == null)
    EncryptCredentialsHolder.SECRET = encryptProperties.secret
    EncryptCredentialsHolder.IV = encryptProperties.iv
  }
}
