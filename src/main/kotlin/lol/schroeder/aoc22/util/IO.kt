package lol.schroeder.aoc22.util

fun readInputToList(name: String): List<String> = getResourceAsText(name).split("\n")

fun getResourceAsText(path: String): String {
    return object {}.javaClass.getResource("/$path.txt")?.readText() ?: throw ResourceNotFoundException(path)
}

class ResourceNotFoundException(path: String) : RuntimeException("Resource '$path' not found.")