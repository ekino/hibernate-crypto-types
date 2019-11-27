package com.ekino.oss.hibernate.model

import org.hibernate.annotations.Type
import javax.persistence.Column
import javax.persistence.Entity

@Entity
class Person(

  var name: String,

  @Type(type = "encrypted-text")
  @Column(columnDefinition = "encrypted-text")
  var password: String? = null,

  @Type(type = "encrypted-json")
  @Column(columnDefinition = "encrypted-json")
  var privateData: PrivateData? = null

) : AbstractEntity()
