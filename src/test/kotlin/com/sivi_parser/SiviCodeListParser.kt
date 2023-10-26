package com.sivi_parser

import com.sivi_parser.SiviParser.siviCodeList
import com.sivi_parser.codelist.CodeEntry
import com.sivi_parser.codelist.CodeListItem
import com.sivi_parser.codelist.SiviCodeList
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper
import java.io.File

class SiviCodeListParser {
    @Test
    fun `can parse codelist file`() {
        //when:
        val codeList: SiviCodeList = siviCodeList()

        //then
        assertThat(codeList.items).isNotEmpty
        with(codeList.items[0]) {
            assertThat(name).isEqualTo("ADNAAG")
            assertThat(description).isNotBlank()
            assertThat(codes).isEqualTo(
                listOf(
                    CodeEntry(value = "EA", description = "Aan eigen administratie"),
                    CodeEntry(value = "TP", description = "Aan tussenpersoon"),
                    CodeEntry(value = "VP", description = "Aan verzekeringnemer"),
                    CodeEntry(value = "WG", description = "Aan werkgever"),
                    CodeEntry(value = "WN", description = "Aan werknemer"),
                    CodeEntry(value = "ZZ", description = "Overig"),
                )
            )
        }

        //and: these entries have no Code(Values):
        assertThat(
            codeList.items
                .filter { it.codes == null }
                .map { it.name }
        ).isEqualTo(listOf("ADNBST", "ADNVIC", "SBIAFD", "UNAGSC"))
    }

    @Test
    fun `write every codelist to its own file`() {
        writeCodeLists(
            outputFileFormat = ::to_code_shortDescription,
            toFileContent = ObjectMapper()::writeValueAsString
        )
    }

    @Test
    @Disabled
    fun `write every codelist to its own file - every codeValue directly associated with description`() {
        writeCodeLists(
            outputFileFormat = ::to_countOfCodes_code_shortDescription,
            toFileContent = { item ->
                mapOf(
                    "name" to item.name,
                    "Description" to item.description,
                    "codes" to item.codes.orEmpty()
                        .map { it.value to it.description }
                        .toMap()
                ).let { ObjectMapper().writeValueAsString(it) }
            },
        )
    }

    private fun writeCodeLists(
        toFileContent: (CodeListItem) -> String,
        outputFileFormat: (CodeListItem, Directory) -> File
    ) {
        val directory = directory("sivi")
            .subdirectory("codelist")

        siviCodeList()
            .items
            .forEach { item ->
                writeToFile(
                    file = outputFileFormat(item, directory),
                    fileContent = toFileContent(item)
                )
            }
    }

    private fun to_countOfCodes_code_shortDescription(item: CodeListItem, directory: Directory): File =
        directory.jsonFile(
            toFileName(
                toFileCount(item),
                item.name,
                toShortDescription(item)
            )
        )

    private fun to_code_shortDescription(item: CodeListItem, directory: Directory): File =
        directory.jsonFile(
            toFileName(
                item.name,
                toShortDescription(item)
            )
        )

    private fun toFileCount(item: CodeListItem) =
        "${item.codes.orEmpty().size}"

    private fun toFileName(mandatory: String, vararg others: String): String =
        (sequenceOf(mandatory) + others.asSequence())
            .joinToString("_")

    private fun toShortDescription(item: CodeListItem) = item
        .description.substringBefore("(")
        .replace("/", "or")
        .trim()
}

typealias Directory = File