package com.ekino.oss.hibernate.model

import com.ekino.oss.crypto.hibernate.EncryptedJsonType
import com.ekino.oss.crypto.hibernate.EncryptedStringType
import org.hibernate.annotations.TypeDef
import org.hibernate.annotations.TypeDefs
import java.util.UUID
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.MappedSuperclass

/**
 * Common superclass for all the entities.
 */
@MappedSuperclass
@TypeDefs(
  TypeDef(name = "encrypted-text", typeClass = EncryptedStringType::class),
  TypeDef(name = "encrypted-json", typeClass = EncryptedJsonType::class)
)
abstract class AbstractEntity(
  @Id
  @GeneratedValue
  open var id: UUID? = null
)
