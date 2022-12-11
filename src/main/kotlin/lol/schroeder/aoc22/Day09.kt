package lol.schroeder.aoc22

import kotlinx.collections.immutable.persistentSetOf
import lol.schroeder.aoc22.util.*
import kotlin.math.sign

fun main() {
    operator fun Coordinate.minus(other: Coordinate): Coordinate = coordOf(x - other.x, y - other.y)

    fun directionFor(dir: String) = when (dir) {
        "U" -> Direction.NORTH
        "R" -> Direction.EAST
        "D" -> Direction.SOUTH
        "L" -> Direction.WEST
        else -> throw RuntimeException("Unknown direction '$dir' in instruction!")
    }

    fun parseInstructions(instructions: List<String>) = instructions
        .map { it.split(" ") }
        .map { it[0] to it[1].toInt() }
        .flatMap { instruction -> List(instruction.second) { directionFor(instruction.first) } }

    fun Coordinate.isNeighborOf(other: Coordinate): Boolean {
        return other.x - 1 <= x && x <= other.x + 1 && other.y - 1 <= y && y <= other.y + 1
    }

    data class Rope(val knots: List<Coordinate>) {
        constructor(size: Int): this(List(size) { originCoord() })

        init { require(knots.size > 1) { "Must have more than one knot in the rope!" } }

        val tail: Coordinate
            get() = knots.first()

        fun move(direction: Direction) = Rope(knots = move(knots.first(), knots.rest(), direction))

        private fun move(knot: Coordinate, head: List<Coordinate>, direction: Direction): List<Coordinate> {
            if (head.isEmpty())
                return listOf(knot.move(direction))

            val updatedHead = move(head.first(), head.rest(), direction)

            val parentKnot = updatedHead.first()
            if (knot.isNeighborOf(parentKnot))
                return listOf(knot) + updatedHead

            val (dx, dy) = parentKnot - knot
            val newKnotPosition = coordOf(knot.x + dx.sign, knot.y + dy.sign)
            return listOf(newKnotPosition) + updatedHead
        }
    }

    fun getVisitedTailLocations(rope: Rope, instructions: List<Direction>) = instructions
        .fold(rope to persistentSetOf<Coordinate>()) { acc, direction ->
            val (r, visited) = acc
            val newRope = r.move(direction)
            newRope to visited.add(newRope.tail)
        }.second

    fun part1(input: List<String>): Int {
        val instructions = parseInstructions(input)
        return getVisitedTailLocations(Rope(2), instructions).size
    }

    fun part2(input: List<String>): Int {
        val instructions = parseInstructions(input)
        return getVisitedTailLocations(Rope(10), instructions).size
    }

    val input = readInputLines("day09")
    println("::: Day09 :::")
    println("Part 1: ${part1(input)}")
    println("Part 2: ${part2(input)}")
}
