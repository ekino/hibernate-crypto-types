package com.ekino.oss.hibernate

import assertk.assertThat
import assertk.assertions.hasSameSizeAs
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.ekino.oss.hibernate.model.Person
import com.ekino.oss.hibernate.model.PrivateData
import com.ekino.oss.hibernate.model.SomeEnum
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
            "private_data": null,
            "some_value": null
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
            "private_data": "v+HbP1s+jxoztxFKa82EGXBCbwhVmxmpVLJbwm4\/LsSSen08M7XwY8nksISGaCkY",
            "some_value": null
          }]
        """)
  }

  @Test
  internal fun `encrypt and decrypt wrapped data object field`() {
    val savedEntity1 = personRepository.save(Person(name = "test1", someValue = SomeEnum.VALUE_1))
    val fetchEntity1 = personRepository.findByIdOrNull(savedEntity1.id!!)

    val savedEntity2 = personRepository.save(Person(name = "test2", someValue = SomeEnum.VALUE_1))
    val fetchEntity2 = personRepository.findByIdOrNull(savedEntity2.id!!)

    assertThat(fetchEntity1)
        .isNotNull()
        .transform { it.someValue }
        .isEqualTo(SomeEnum.VALUE_1)

    assertThat(fetchEntity2)
        .isNotNull()
        .transform { it.someValue }
        .isEqualTo(SomeEnum.VALUE_1)

    dBComparatorBuilder
        .build("select * from person")
        .isValidAgainst("""
          [{
            "id": "{#uuid#}",
            "name": "test1",
            "password": null,
            "private_data": null,
            "some_value": "{#not_empty#}"
          }, {
            "id": "{#uuid#}",
            "name": "test2",
            "password": null,
            "private_data": null,
            "some_value": "{#not_empty#}"
          }]
        """)

    val someValueResults = jdbcTemplate.queryForList("select some_value from person", String::class.java)
    assertThat(someValueResults).hasSameSizeAs(someValueResults.distinct())
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
