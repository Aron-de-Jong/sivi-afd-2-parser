package com.sivi_parser.datacat

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import java.time.Instant
import java.time.LocalDate
import java.time.Month

class SiviAttributeTest {
    @TestFactory
    fun `to JSON type`(): List<DynamicTest> {
        data class Scenario(
            val datatype: String,
            val codeList: String = "",
            val expected: Any,
        )
        return listOf(
            Scenario(datatype = "[string]", expected = ""),
            Scenario(datatype = "string", expected = ""),

            Scenario(datatype = "boolean", expected = false),
            Scenario(datatype = "[boolean]", expected = false),

            Scenario(datatype = "string", codeList = "CODELIST", expected = "code -> CODELIST"),
            Scenario(datatype = "boolean", codeList = "CODELIST", expected = "code -> CODELIST"),

            Scenario(datatype = "timestamp", expected = "2023-10-25T11:47:05.831Z"),
            Scenario(datatype = "date", expected = "2023-10-25"),
            Scenario(datatype = "time", expected = "13:47:05"),
        ).map { scenario ->
            dynamicTest(
                "datatype: ${scenario.datatype} ${
                    scenario.codeList.takeIf(String::isNotBlank)?.prependIndent(", codeList: ") ?: ""
                }"
            ) {
                val actual = siviAttribute(
                    datatype = scenario.datatype,
                    codelist = scenario.codeList,
                ).toJsonType(
                    toInstant = { Instant.ofEpochMilli(1698234425831) },
                    toDate = { LocalDate.of(2023, Month.OCTOBER, 25) }
                )
                assertThat(actual).isEqualTo(scenario.expected)
            }
        }
    }

    @Test
    fun `expect timestamp in Zulu time format`() {
        assertThat(siviAttribute(datatype = "timestamp", codelist = "").toJsonType() as String)
            .endsWith("Z")
    }

    @TestFactory
    fun `by default, toJsonType will use current time`(): List<DynamicTest> =
        listOf(
            "timestamp",
        ).map { datatype ->
            dynamicTest(datatype) {
                //first call
                val firstTime = siviAttribute(datatype = datatype, codelist = "")
                    .toJsonType() as String

                //second call
                val secondTime = siviAttribute(datatype = datatype, codelist = "")
                    .toJsonType() as String

                //expect: time of first call is before time of second call
                assertThat(firstTime).isLessThan(secondTime)
            }
        }

    private fun siviAttribute(
        datatype: String,
        codelist: String,
    ) =
        SiviAttribute(
            name = "",
            description = "",
            datatype = datatype,
            codelist = codelist,
            afd1 = ""
        )
}