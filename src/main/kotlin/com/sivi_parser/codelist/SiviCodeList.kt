package com.sivi_parser.codelist

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@JsonIgnoreProperties(ignoreUnknown = false)
data class SiviCodeList(
    @field:JsonProperty("Items")
    val items: List<CodeListItem>,
)

data class CodeListItem(
    @field:JsonProperty("Name")
    val name: String,
    @field:JsonProperty("Description")
    val description: String,

    @field:JsonProperty("Code")
    val codes: List<CodeEntry>?,
)

data class CodeEntry(
    @field:JsonProperty("CodeValue")
    val value: String,

    @field:JsonProperty("Description")
    val description: String,
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

    fun toJsonType(): Any {
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
            "[timestamp]" -> Instant.now().toString()

            "date",
            "[date]",
            -> LocalDate.now().toString()

            "time" -> Instant.now()
                .atZone(ZoneId.of("Z"))
                .toLocalTime()

            else -> throw IllegalArgumentException("Uncovered datatype: $datatype")
        }
    }
}