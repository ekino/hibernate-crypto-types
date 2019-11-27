package com.ekino.oss.hibernate.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "ekino.encrypt")
data class EncryptProperties(val secret: String, val iv: String)
