package lol.schroeder.aoc22

import lol.schroeder.aoc22.util.*
import kotlin.math.abs

fun main() {

    fun part1(input: List<String>) {
        val instructions = input.map { it.split(" ") }
            .map { it[0] to it[1].toInt() }

        var headLoc = originCoord()
        var tailLoc = originCoord()

        val visitedCoordinates = mutableSetOf(tailLoc)

        for ((dir, amount) in instructions) {
            val direction = when (dir) {
                "R" -> Direction.EAST
                "L" -> Direction.WEST
                "U" -> Direction.NORTH
                "D" -> Direction.SOUTH
                else -> throw RuntimeException()
            }

            repeat(amount) {
                headLoc = headLoc.move(direction)

                when (direction) {
                    Direction.NORTH -> { if (headLoc.y - tailLoc.y > 1) tailLoc = headLoc.copy(x = headLoc.x, y = headLoc.y - 1) }
                    Direction.EAST -> { if (headLoc.x - tailLoc.x > 1) tailLoc = headLoc.copy(x = headLoc.x - 1, y = headLoc.y) }
                    Direction.SOUTH -> { if (tailLoc.y - headLoc.y > 1) tailLoc = headLoc.copy(x = headLoc.x, y = headLoc.y + 1) }
                    Direction.WEST -> { if (tailLoc.x - headLoc.x > 1) tailLoc = headLoc.copy(x = headLoc.x + 1, y = headLoc.y) }
                    else -> {}
                }
                visitedCoordinates.add(tailLoc)
            }
        }

        println(visitedCoordinates.size)
    }

    fun part2(input: List<String>) {
        val instructions = input.map { it.split(" ") }
            .map { it[0] to it[1].toInt() }

        operator fun Coordinate.minus(other: Coordinate): Coordinate {
            return coordOf(x - other.x, y - other.y)
        }

        val knotPositions = MutableList(10) { originCoord() }

        val visitedCoordinates = mutableSetOf(knotPositions.last())

        for ((dir, amount) in instructions) {
            val direction = when (dir) {
                "R" -> Direction.EAST
                "L" -> Direction.WEST
                "U" -> Direction.NORTH
                "D" -> Direction.SOUTH
                else -> throw RuntimeException()
            }

            repeat(amount) {
                knotPositions[0] = knotPositions[0].move(direction)

                for (i in 1..knotPositions.lastIndex) {
                    val headLoc = knotPositions[i - 1]
                    val tailLoc = knotPositions[i]

                    val xDist = headLoc.x - tailLoc.x
                    val yDist = headLoc.y - tailLoc.y

                    if (abs(xDist) > 1 && abs(xDist) > abs(yDist)) {
                        val offset = if (xDist < 0) xDist + 1 else xDist - 1
                        knotPositions[i] = tailLoc.copy(x = tailLoc.x + offset, y = headLoc.y)
                    } else if (abs(yDist) > 1 && abs(yDist) > abs(xDist)) {
                        val offset = if (yDist < 0) yDist + 1 else yDist - 1
                        knotPositions[i] = tailLoc.copy(x = headLoc.x, y = tailLoc.y + offset)
                    } else if (abs(xDist) > 1 && abs(xDist) == abs(yDist)) {
                        val yOffset = if (yDist < 0) yDist + 1 else yDist - 1
                        val xOffset = if (xDist < 0) xDist + 1 else xDist - 1
                        knotPositions[i] = tailLoc.copy(x = tailLoc.x + xOffset, y = tailLoc.y + yOffset)
                    }

                    if (i == knotPositions.lastIndex)
                        visitedCoordinates.add(knotPositions.last())
                }
            }

        }

        println(visitedCoordinates.size)
    }

    val testInput = """
        R 5
        U 8
        L 8
        D 3
        R 17
        D 10
        L 25
        U 20
    """.trimIndent().split("\n")
    println("Test: ${part2(testInput)}")

    val input = readInputLines("day09")
    println("::: Day09 :::")
    println("Part 1: ${part1(input)}")
    println("Part 2: ${part2(input)}")
}

