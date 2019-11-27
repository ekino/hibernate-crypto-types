package com.ekino.oss.hibernate.repository

import com.ekino.oss.hibernate.model.Person
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface PersonRepository : CrudRepository<Person, UUID>
