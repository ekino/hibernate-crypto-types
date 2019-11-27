package com.ekino.oss.hibernate

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.ekino.oss.hibernate.model.Person
import com.ekino.oss.hibernate.model.PrivateData
import com.ekino.oss.hibernate.repository.PersonRepository
import com.ekino.oss.jcv.db.jdbc.util.DBComparatorBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.jdbc.core.JdbcTemplate

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class PersonRepositoryTest(
  private val personRepository: PersonRepository,
  private val jdbcTemplate: JdbcTemplate
) {

  private val dBComparatorBuilder = DBComparatorBuilder
      .create()
      .connection(jdbcTemplate.dataSource!!.connection)

  @BeforeEach
  internal fun setUp() = jdbcTemplate.truncateAll()

  @Test
  internal fun `encrypt and decrypt password string field`() {

    val saveEntity = personRepository.save(Person(name = "test", password = "12345"))
    val fetchEntity = personRepository.findByIdOrNull(saveEntity.id!!)

    assertThat(fetchEntity)
        .isNotNull()
        .transform { it.password }
        .isEqualTo("12345")

    dBComparatorBuilder
        .build("select * from person")
        .isValidAgainst("""
          [{
            "id": "{#uuid#}",
            "name": "test",
            "password": "WZk6Copy\/bLwLVQpLY7DCQ==",
            "private_data": null
          }]
        """)
  }

  @Test
  internal fun `encrypt and decrypt private data object field`() {
    val privateData = PrivateData("manager", 9000)
    val saveEntity = personRepository.save(Person(name = "test", privateData = privateData))
    val fetchEntity = personRepository.findByIdOrNull(saveEntity.id!!)

    assertThat(fetchEntity)
        .isNotNull()
        .transform { it.privateData }
        .isEqualTo(privateData)

    dBComparatorBuilder
        .build("select * from person")
        .isValidAgainst("""
          [{
            "id": "{#uuid#}",
            "name": "test",
            "password": null,
            "private_data": "v+HbP1s+jxoztxFKa82EGXBCbwhVmxmpVLJbwm4\/LsSSen08M7XwY8nksISGaCkY"
          }]
        """)
  }
}

fun JdbcTemplate.truncateAll() {
  val tables = this.queryForList(
      "SELECT tablename FROM pg_tables WHERE schemaname = 'public' AND tablename != 'flyway_schema_history';", String::class.java)

  tables.forEach { table ->
    this.execute("set session_replication_role = replica;")
    this.execute("DELETE FROM $table ;")
    this.execute("set session_replication_role = default;")
  }
}
