package lol.schroeder.aoc22

import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentMap
import lol.schroeder.aoc22.util.Coordinate
import lol.schroeder.aoc22.util.coordOf
import lol.schroeder.aoc22.util.readInputLines

fun main() {
    fun inputToPoints(input: List<String>) = input.asSequence()
        .map { it.split(" -> ") }
        .map { line -> line.map { it.split(",") } }
        .map { line -> line.map { coordOf(it.first().toInt(), it.last().toInt()) } }
        .map { it.zipWithNext { a, b -> a lineTo b }.flatten() }
        .flatten()
        .associateWith { '#' }
        .toPersistentMap()

    tailrec fun getRestingPosition(pos: Coordinate, grid: Map<Coordinate, Char>, yBounds: IntRange): Coordinate {
        if (pos.y !in yBounds)
            return pos

        val down = pos.copy(y = pos.y + 1)
        val left = pos.copy(x = pos.x - 1, y = pos.y + 1)
        val right = pos.copy(x = pos.x + 1, y = pos.y + 1)

        return if (grid[down] == null) {
            getRestingPosition(down, grid, yBounds)
        } else if (grid[left] == null) {
            getRestingPosition(left, grid, yBounds)
        } else if (grid[right] == null) {
            getRestingPosition(right, grid, yBounds)
        } else {
            pos
        }
    }

    tailrec fun runSandSimUntil(grid: PersistentMap<Coordinate, Char>, yBounds: IntRange, predicate: (Coordinate) -> Boolean): Map<Coordinate, Char> {
        val nextPos = getRestingPosition(coordOf(500, 0), grid, yBounds)

        if (predicate(nextPos))
            return grid

        return runSandSimUntil(grid.put(nextPos, 'o'), yBounds, predicate)
    }

    fun part1(input: List<String>): Any {
        val gridCoords = inputToPoints(input)

        val yMax = gridCoords.keys.maxOf { it.y }
        val yBounds = 0..yMax

        return runSandSimUntil(gridCoords, yBounds) { it.y !in yBounds }
            .values.count { it == 'o' }
    }

    fun part2(input: List<String>): Any {
        val gridCoords = inputToPoints(input)

        val yMax = gridCoords.keys.maxOf { it.y }
        val yBounds = 0..yMax

        val sandSource = coordOf(500, 0)
        return runSandSimUntil(gridCoords, yBounds) { it == sandSource }
            .values.count { it == 'o' } + 1
    }

    println("::: Day14 :::")
    val input = readInputLines("day14")
    println("Part 1: ${part1(input)}")
    println("Part 2: ${part2(input)}")
}