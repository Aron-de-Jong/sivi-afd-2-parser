package com.sivi_parser

import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper
import java.io.File

internal fun toJsonContent(map: Map<String, Any>): String =
    ObjectMapper()
        .writeValueAsString(map)

fun writeToFile(file: File, fileContent: String) {
    file
        .printWriter()
        .use { outputFile ->
            outputFile.print(fileContent)
        }
}

internal fun directory(directory: String): File =
    File(directory)
        .also(File::mkdir)

internal fun File.subdirectory(subDirectory: String): File =
    File(this.toURI().path, subDirectory)
        .also(File::mkdir)

internal fun File.jsonFile(fileName: String) =
    File(this.toURI().path, "$fileName.json")