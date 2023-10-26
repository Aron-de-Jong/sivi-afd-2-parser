package com.sivi_parser

import com.sivi_parser.SiviParser.siviDataCat
import com.sivi_parser.datacat.SiviDataCat
import com.sivi_parser.datacat.SiviEntity
import com.sivi_parser.datacat.SiviEntityType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper
import java.io.File

class SiviDataCatParser {
    @Test
    fun `can read Sivi datacat file`() {
        //when:
        val dataCatalog: SiviDataCat = siviDataCat()

        //when
        with(dataCatalog) {
            assertThat(this.version).isNull()
            assertThat(this.entities).hasSizeGreaterThanOrEqualTo(1)
        }
    }

    @Test
    fun `write every entity as json file`() {
        siviDataCat()
            .entities
            .forEach { entity ->
                val jsonObject =
                    mapOf(entity.name to entity.toJson())

                val jsonContent =
                    ObjectMapper()
                        .writeValueAsString(jsonObject)
                        .also(::println)

                writeToFile(
                    file = directory("sivi")
                        .subdirectory("entities")
                        .jsonFile(entity.name),
                    fileContent = jsonContent
                )
            }
    }

    @TestFactory
    fun `Write every entityType for every entity to its own file`() =
        siviDataCat()
            .entities
            .map { entity ->
                dynamicTest(entity.name) {
                    val entityFolder =
                        File("sivi")
                            .also(File::mkdir)
                            .subdirectory("entity_types")
                            .subdirectory("${entity.entityTypes.size}_${entity.name}")

                    entity.entityTypes.forEach { entityType ->
                        writeToFile(
                            file = entityFolder.jsonFile(entityType.name),
                            fileContent = toJsonContent(entityType.toJson())
                        )
                    }
                    assertThat(entityFolder.listFiles().orEmpty().size).isEqualTo(entity.entityTypes.size)
                }
            }

    private fun SiviEntity.toJson(): List<Map<String, Any>> {
        val entity = this
        return entity
            .entityTypes
            .map { it.toJson() }
    }

    private fun SiviEntityType.toJson(): Map<String, Any> {
        val entityType = this

        val entityTypePair =
            listOf("entityType" to entityType.name)

        val otherEntities =
            entityType.attributes
                .filterNot { it.name.equals("entityType", ignoreCase = true) }
                .map { attribute ->
                    attribute.name to attribute.toJsonType()
                }

        return (entityTypePair + otherEntities).toMap()
    }
}
