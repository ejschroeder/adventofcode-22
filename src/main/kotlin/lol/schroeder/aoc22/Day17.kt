package lol.schroeder.aoc22

import lol.schroeder.aoc22.util.Coordinate
import lol.schroeder.aoc22.util.coordOf
import lol.schroeder.aoc22.util.readInputLines
import kotlin.math.max


fun main() {
    val shapes = listOf(
        setOf(coordOf(2, 3), coordOf(3, 3), coordOf(4, 3), coordOf(5, 3)),
        setOf(coordOf(3, 3), coordOf(2, 4), coordOf(3, 4), coordOf(4, 4), coordOf(3, 5)),
        setOf(coordOf(2, 3), coordOf(3, 3), coordOf(4, 3), coordOf(4, 4), coordOf(4, 5)),
        setOf(coordOf(2, 3), coordOf(2, 4), coordOf(2, 5), coordOf(2, 6)),
        setOf(coordOf(2, 3), coordOf(3, 3), coordOf(2, 4), coordOf(3, 4))
    )

    fun push(shape: List<Coordinate>, dir: Char) = when (dir) {
        '<' -> shape.map { it.copy(x = it.x - 1) }
        '>' -> shape.map { it.copy(x = it.x + 1) }
        else -> shape
    }

    fun fall(shape: List<Coordinate>) = shape.map { it.copy(y = it.y - 1) }

    fun part1(input: List<String>): Int {
        val jets = input.first().toList()

        var highest = 0
        var jet = 0
        val settled = mutableSetOf<Coordinate>()

        val seen = mutableMapOf<Pair<Int, Int>, Int>()

        repeat(2022) { iter ->
            var shape = shapes[iter % 5].map { it.copy(y = it.y + highest) }
            var shapePlusJet = (iter % 5) to jet

            while (true) {
                val newLoc = push(shape, jets[jet])
                jet = (jet + 1) % jets.size

                val finalShifted = if (newLoc.any { it.x < 0 || it.x > 6 || it in settled }) {
                    shape
                } else {
                    newLoc
                }

                val fallenShape = fall(finalShifted)

                if (fallenShape.any { it.y < 0 || it in settled }) {
                    settled.addAll(finalShifted)
                    val maxHeightOfSettled = finalShifted.maxOf { it.y }
                    highest = max(highest, maxHeightOfSettled + 1)
                    break
                }
                shape = fallenShape
            }
            seen[shapePlusJet] = iter
        }

        return highest
    }

    fun part2(input: List<String>): Long {
        val jets = input.first().toList()
        val heights = mutableListOf(0)
        val settled = mutableSetOf<Coordinate>()
        val seenStates = mutableMapOf<Pair<Int, Int>, Int>()

        var consecutiveMatches = 0
        val consecutiveMatchThreshold = 5

        var iteration = 0
        var jet = 0

        while (true) {
            val shapeIdx = iteration % shapes.size
            val maxHeight = heights.last()

            var shape = shapes[shapeIdx].map { it.copy(y = it.y + maxHeight) }
            val shapePlusJet = shapeIdx to jet

            while (true) {
                val newLoc = push(shape, jets[jet])
                jet = (jet + 1) % jets.size

                val finalShifted = if (newLoc.any { it.x < 0 || it.x > 6 || it in settled }) {
                    shape
                } else {
                    newLoc
                }

                val fallenShape = fall(finalShifted)

                if (fallenShape.any { it.y < 0 || it in settled }) {
                    settled.addAll(finalShifted)
                    val maxHeightOfSettled = finalShifted.maxOf { it.y }
                    heights.add(max(maxHeight, maxHeightOfSettled + 1))
                    break
                }
                shape = fallenShape
            }

            if (shapePlusJet in seenStates) {
                consecutiveMatches++
            } else {
                consecutiveMatches = 0
            }

            if (consecutiveMatches >= consecutiveMatchThreshold) {
                val cycleSize = iteration - seenStates[shapePlusJet]!!
                val cycleStart = seenStates[shapePlusJet]!! - consecutiveMatches

                val remaining = 1_000_000_000_000 - (cycleStart + 1)
                val fullCycles = remaining / cycleSize
                val remainingSteps = remaining % cycleSize
                val sizeBeforeCycleStart = heights[cycleStart + 1]
                val heightOfCycle = heights[cycleStart + cycleSize + 1] - heights[cycleStart + 1]
                val heightOfRemainingSteps = heights[cycleStart + remainingSteps.toInt() + 1] - heights[cycleStart + 1]

                return sizeBeforeCycleStart + (fullCycles * heightOfCycle) + heightOfRemainingSteps
            }

            seenStates[shapePlusJet] = iteration

            iteration++
        }
    }

    val testInput = """
        >>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>
    """.trimIndent().lines()

    println("Test Input:")
    println("Part 1: ${part1(testInput)}")
    println("Part 2: ${part2(testInput)}")
    println()

    val input = readInputLines("day17")
    println("::: Day17 :::")
    println("Part 1: ${part1(input)}")
    println("Part 2: ${part2(input)}")
}