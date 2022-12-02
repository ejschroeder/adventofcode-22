package lol.schroeder.aoc22.util

import java.io.File

fun readInputLines(path: String) = getInputResourceAsFile(path).readLines()

fun readInputText(path: String) = getInputResourceAsFile(path).readText()

fun getInputResourceAsFile(path: String): File {
    val uri = object {}.javaClass.getResource("/$path.txt")?.toURI()
        ?: throw ResourceNotFoundException(path)

    return File(uri)
}

class ResourceNotFoundException(path: String) : RuntimeException("Resource '$path' not found.")