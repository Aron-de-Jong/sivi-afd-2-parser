package com.sivi_parser

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.sivi_parser.codelist.SiviCodeList
import com.sivi_parser.datacat.SiviDataCat
import java.io.FileNotFoundException
import java.net.URL

object SiviParser {
    private val siviCodeList by lazy { deserialize<SiviCodeList>("/sivi/codelist.json") }
    private val siviDataCat by lazy { deserialize<SiviDataCat>("/sivi/datacat.json") }

    val objectMapper = ObjectMapper()
        .registerKotlinModule()

    fun siviDataCat(): SiviDataCat =
        siviDataCat

    fun siviCodeList(): SiviCodeList =
        siviCodeList

    private inline fun <reified T> deserialize(filePath: String): T {
        val xmlFile = readFile(filePath)
        return objectMapper.readValue(xmlFile)
    }

    fun readFile(filePath: String): URL =
        javaClass.getResource(filePath)
            ?: throw FileNotFoundException("File not found at path: $filePath")
}

