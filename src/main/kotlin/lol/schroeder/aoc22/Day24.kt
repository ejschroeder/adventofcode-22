package lol.schroeder.aoc22

import lol.schroeder.aoc22.util.*
import kotlin.collections.ArrayDeque

typealias BlizzardDirection = Coordinate

fun main() {
    data class Position(val location: Coordinate, val time: Int)

    data class Blizzard(val startingPosition: Coordinate, val direction: BlizzardDirection) {
        fun locationAtTime(time: Int, width: Int, height: Int): Coordinate {
            val xOffset = direction.x * time
            val yOffset = direction.y * time
            return coordOf((startingPosition.x + xOffset).mod(width), (startingPosition.y + yOffset).mod(height))
        }
    }

    data class Basin(val blizzards: List<Blizzard>, val width: Int, val height: Int) {
        val xRange = 0 until width
        val yRange = 0 until height
        val start = coordOf(0, -1)
        val end = coordOf(width - 1, height)
        private val blizzardCycle = lcm(width, height)
        private val blizzardCache: List<Set<Coordinate>> = (0 until blizzardCycle).map { time ->
            blizzards.map { it.locationAtTime(time, width, height) }.toSet()
        }

        operator fun contains(coordinate: Coordinate) = coordinate.x in xRange && coordinate.y in yRange

        fun getBlizzardLocationsAtTime(time: Int) = blizzardCache[time.mod(blizzardCycle)]
    }

    fun parseInputToBasin(input: List<String>): Basin {
        val width = input.first().length - 2

        val grid = input.drop(1).dropLast(1)
            .map { it.removeSurrounding("#").toList() }
            .flatten()
            .toGrid(width)

        val blizzards = grid.coordinates.filter { grid[it] != '.' }
            .map {
                when (grid[it]) {
                    '^' -> Blizzard(it, BlizzardDirection(0, -1))
                    '>' -> Blizzard(it, BlizzardDirection(1, 0))
                    'v' -> Blizzard(it, BlizzardDirection(0, 1))
                    '<' -> Blizzard(it, BlizzardDirection(-1, 0))
                    else -> throw RuntimeException()
                }
            }

        return Basin(blizzards, grid.width, grid.height)
    }

    fun findSmallestTimeToDestination(basin: Basin, startPosition: Position, end: Coordinate): Position {
        val queue = ArrayDeque<Position>()
        queue.add(startPosition)
        val seen = mutableSetOf<Position>()

        while(queue.isNotEmpty()) {
            val pos = queue.removeFirst()

            if (pos in seen) {
                continue
            }
            seen.add(pos)

            if (pos.location == end)
                return pos

            val blizzardLocations = basin.getBlizzardLocationsAtTime(pos.time + 1)
            val potentialLocations = pos.location.getNeighbors() + pos.location
            val validMoves = potentialLocations
                .filter { (it in basin && it !in blizzardLocations) || it == basin.end || it == basin.start }
                .map { Position(location = it, time = pos.time + 1) }

            queue.addAll(validMoves)
        }

        throw RuntimeException("No path to end coordinate found!")
    }

    fun part1(input: List<String>): Int {
        val basin = parseInputToBasin(input)
        val position = Position(basin.start, time = 0)

        return findSmallestTimeToDestination(basin, position, basin.end).time
    }

    fun part2(input: List<String>): Int {
        val basin = parseInputToBasin(input)
        val position = Position(basin.start, time = 0)

        val endPos = findSmallestTimeToDestination(basin, position, basin.end)
        val startPos = findSmallestTimeToDestination(basin, endPos, basin.start)
        return findSmallestTimeToDestination(basin, startPos, basin.end).time
    }

    val input = readInputLines("day24")
    println("::: Day24 :::")
    println("Part 1: ${part1(input)}")
    println("Part 2: ${part2(input)}")
}