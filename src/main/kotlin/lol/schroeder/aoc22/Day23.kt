package lol.schroeder.aoc22

import kotlinx.collections.immutable.persistentMapOf
import lol.schroeder.aoc22.util.*

fun main() {
    fun parseInput(input: List<String>): Set<Coordinate> {
        val width = input.first().length
        val grid = input.map { it.toList() }.reversed().flatten().toGrid(width)
        return grid.coordinates.filter { grid[it] == '#' }.toSet()
    }

    fun moveNorth(coordinate: Coordinate, elves: Set<Coordinate>): Coordinate? {
        val candidates = listOf(coordinate.move(Direction.NORTH), coordinate.move(Direction.NORTH_EAST), coordinate.move(Direction.NORTH_WEST))
        return if (candidates.none { it in elves }) coordinate.move(Direction.NORTH) else null
    }

    fun moveEast(coordinate: Coordinate, elves: Set<Coordinate>): Coordinate? {
        val candidates = listOf(coordinate.move(Direction.EAST), coordinate.move(Direction.NORTH_EAST), coordinate.move(Direction.SOUTH_EAST))
        return if (candidates.none { it in elves }) coordinate.move(Direction.EAST) else null
    }

    fun moveSouth(coordinate: Coordinate, elves: Set<Coordinate>): Coordinate? {
        val candidates = listOf(coordinate.move(Direction.SOUTH), coordinate.move(Direction.SOUTH_EAST), coordinate.move(Direction.SOUTH_WEST))
        return if (candidates.none { it in elves }) coordinate.move(Direction.SOUTH) else null
    }

    fun moveWest(coordinate: Coordinate, elves: Set<Coordinate>): Coordinate? {
        val candidates = listOf(coordinate.move(Direction.WEST), coordinate.move(Direction.NORTH_WEST), coordinate.move(Direction.SOUTH_WEST))
        return if (candidates.none { it in elves }) coordinate.move(Direction.WEST) else null
    }

    val moves = listOf(::moveNorth, ::moveSouth, ::moveWest, ::moveEast)

    fun getNextElfLocations(elves: Set<Coordinate>, round: Int): Set<Coordinate> {
        val startingDirection = round.mod(moves.size)
        val moveProposals = (0..3).map { moves[(it + startingDirection).mod(moves.size)] }

        val proposedLocations = elves.fold(persistentMapOf<Coordinate, Coordinate>()) { proposed, elf ->
            val neighbors = elf.getNeighbors(includeOrdinalDirections = true)
            val candidate = if (neighbors.none { it in elves }) {
                elf
            } else {
                val proposedLocations = moveProposals.map { it(elf, elves) }
                proposedLocations.firstOrNull { it != null } ?: elf
            }

            if (candidate in proposed) {
                val otherElf = proposed.getValue(candidate)
                proposed.remove(candidate).put(otherElf, otherElf).put(elf, elf)
            } else {
                proposed.put(candidate, elf)
            }
        }

        return proposedLocations.keys
    }

    tailrec fun findLastRound(elves: Set<Coordinate>, round: Int = 0): Int {
        val nextLocations = getNextElfLocations(elves, round)

        if (nextLocations == elves)
            return round + 1

        return findLastRound(nextLocations, round + 1)
    }

    fun part1(input: List<String>): Int {
        val startingElves = parseInput(input)

        val finalLocations = (0..9).fold(startingElves) { elves, round -> getNextElfLocations(elves, round) }

        val xRange = finalLocations.minMaxOf { it.x }.run { first..second }
        val yRange = finalLocations.minMaxOf { it.y }.run { first..second }

        return yRange.sumOf { y -> xRange.count { x -> coordOf(x, y) !in finalLocations } }
    }

    fun part2(input: List<String>): Int {
        val elves = parseInput(input)
        return findLastRound(elves)
    }

    val input = readInputLines("day23")
    println("::: Day23 :::")
    println("Part 1: ${part1(input)}")
    println("Part 2: ${part2(input)}")
}