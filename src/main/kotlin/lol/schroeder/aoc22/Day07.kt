package lol.schroeder.aoc22

import lol.schroeder.aoc22.util.readInputLines


fun main() {
    data class Filesystem(val workingDirectory: String = "", val directorySizes: Map<String, Int> = mapOf(workingDirectory to 0)) {
        val totalSize: Int
            get() = directorySizes.getOrElse("") { 0 }

        fun dive(directory: String): Filesystem {
            val newWorkingDirectory = if (directory == "/") "" else "$workingDirectory/$directory"
            return copy(workingDirectory = newWorkingDirectory, directorySizes = directorySizes + (newWorkingDirectory to 0))
        }

        fun ascend() = when (workingDirectory) {
            "/" -> this
            else -> copy(workingDirectory = workingDirectory.substringBeforeLast("/"))
        }

        fun addFile(size: Int): Filesystem {
            val updatedSizes = directorySizes.mapValues {
                when {
                    workingDirectory.startsWith(it.key) -> it.value + size
                    else -> it.value
                }
            }
            return copy(directorySizes = updatedSizes)
        }
    }

    fun buildFilesystemFromInput(input: List<String>) = input.fold(Filesystem()) { fs, cmd ->
        when {
            cmd.startsWith("$ cd ..") -> fs.ascend()
            cmd.startsWith("$ cd") -> fs.dive(cmd.substringAfterLast(" "))
            cmd.startsWith("$ ls") -> fs
            cmd.startsWith("dir") -> fs
            else -> fs.addFile(cmd.substringBefore(" ").toInt())
        }
    }

    fun part1(input: List<String>): Int {
        val fs = buildFilesystemFromInput(input)
        return fs.directorySizes.filterValues { it <= 100000 }.values.sum()
    }

    fun part2(input: List<String>): Int {
        val freeSpaceRequired = 30000000
        val totalSpace = 70000000

        val fs = buildFilesystemFromInput(input)
        val used = fs.totalSize
        val neededForDeletion = freeSpaceRequired - (totalSpace - used)
        return fs.directorySizes
            .filterValues { it >= neededForDeletion }
            .minOf { it.value }
    }

    val input = readInputLines("day07")

    println("::: Day07 :::")
    println("Part 1: ${part1(input)}")
    println("Part 2: ${part2(input)}")
}