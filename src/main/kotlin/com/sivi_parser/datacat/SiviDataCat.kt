package com.sivi_parser.datacat

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@JsonIgnoreProperties(ignoreUnknown = false)
data class SiviDataCat(
    @field:JsonProperty("Entity")
    val entities: List<SiviEntity>,

    @field:JsonProperty("versie")
    val version: String?,
)

data class SiviEntity(
    @field:JsonProperty("Name")
    val name: String,
    @field:JsonProperty("Description")
    val description: String,

    @field:JsonProperty("EntityType")
    val entityTypes: List<SiviEntityType>,
)

data class SiviEntityType(
    @field:JsonProperty("Name")
    val name: String,
    @field:JsonProperty("Description")
    val description: String,

    @field:JsonProperty("Attribute")
    val attributes: List<SiviAttribute>,
)

data class SiviAttribute(
    @field:JsonProperty("Name")
    val name: String,
    @field:JsonProperty("Description")
    val description: String,

    @field:JsonProperty("Datatype")
    val datatype: String,
    @field:JsonProperty("Codelist")
    val codelist: String,
    @field:JsonProperty("AFD1")
    val afd1: String,
) {
    fun withCodeLIst() =
        codelist.isNotBlank()

    fun toJsonType(
        toInstant: () -> Instant = Instant::now,
        toDate: () -> LocalDate = LocalDate::now

    ): Any {
        if (codelist.isNotBlank()) {
            return "code -> $codelist"
        }
        return when (datatype) {
            "integer",
            "[integer]" -> 2

            "boolean",
            "[boolean]" -> false

            "string",
            "[string]" -> ""

            "decimal",
            "[decimal]" -> 0.98

            "timestamp",
            "[timestamp]" -> toInstant().toString()

            "date",
            "[date]",
            -> toDate().toString()

            "time" -> toInstant()
                .atZone(ZoneId.of("Europe/Amsterdam"))
                .toLocalTime()
                .format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                .toString()

            else -> throw IllegalArgumentException("Uncovered datatype: $datatype")
        }
    }
}