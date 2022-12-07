package lol.schroeder.aoc22

import lol.schroeder.aoc22.util.readInputLines

fun main() {
    abstract class Node(open val name: String, open var parent: Node?) {
        abstract val size: Int
    }

    data class Dir(override val name: String, val files: MutableList<Node>, override var parent: Node? = null): Node(name, parent) {
        override val size: Int
            get() = files.sumOf { it.size }
    }
    class File(override val name: String, override val size: Int, override var parent: Node? = null): Node(name, parent)

    fun countSmallDirectories(root: Dir): Int {
        val rootSize = root.files.filterIsInstance<Dir>()
            .map { it.size }
            .filter { it <= 100000 }
            .sum()

        return rootSize + root.files.filterIsInstance<Dir>()
            .map { countSmallDirectories(it) }
            .sumOf { it }
    }

    fun exploreFileTree(input: List<String>): Dir {
        val root = Dir("/", mutableListOf())
        var location: Dir = root
        for (line in input) {
            val parts = line.split(" ")
            if (parts[0].startsWith("$")) {
                when (parts[1]) {
                    "cd" -> {
                        location = when (parts[2]) {
                            "/" -> root
                            ".." -> location.parent as Dir
                            else -> location.files.firstOrNull { it.name == parts[2] } as Dir
                        }
                    }

                    "ls" -> { // nothing, parsing output
                    }
                }
                // command
            } else {
                when (parts.first()) {
                    "dir" -> {
                        val newDir = Dir(parts.last(), mutableListOf(), location)
                        location.files.add(newDir)
                    }

                    else -> {
                        val size = parts.first().toInt()
                        location.files.add(File(parts.last(), size, location))
                    }
                }
            }
        }
        return root
    }

    fun part1(input: List<String>): Int {
        val root = exploreFileTree(input)

        return countSmallDirectories(root)
    }

    fun getAllDirectories(root: Dir): List<Dir> {
        return root.files.filterIsInstance<Dir>().toList() + root.files.filterIsInstance<Dir>().flatMap { getAllDirectories(it) }
    }

    fun part2(input: List<String>): Int {
        val root = exploreFileTree(input)

        val neededForDeletion = 30000000 - (70000000 - root.size)
        return getAllDirectories(root)
            .map { it.size }
            .sorted()
            .first { it > neededForDeletion }
    }

    val input = readInputLines("day07")

    println("::: Day07 :::")
    println("Part 1: ${part1(input)}")
    println("Part 2: ${part2(input)}")
}