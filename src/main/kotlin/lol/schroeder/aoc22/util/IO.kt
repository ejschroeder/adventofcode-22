package lol.schroeder.aoc22.util

import java.net.URL

fun readInputLines(path: String) = readInputText(path).split("\n")

fun readInputText(path: String) = getResource(path).readText()

fun getResource(path: String): URL {
    return object {}.javaClass.getResource("/$path.txt")
        ?: throw ResourceNotFoundException(path)
}

class ResourceNotFoundException(path: String) : RuntimeException("Resource '$path' not found.")