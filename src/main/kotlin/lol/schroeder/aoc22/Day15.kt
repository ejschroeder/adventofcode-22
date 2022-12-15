package lol.schroeder.aoc22

import lol.schroeder.aoc22.util.*
import kotlin.math.abs
import kotlin.math.max

fun main() {
    data class Sensor(val position: Coordinate, val beacon: Coordinate) {
        val range: Int = position.getManhattanDistance(beacon)
        val yBounds = (position.y - range)..(position.y + range)
        fun xRangeAt(y: Int): IntRange {
            val dist = abs(position.y - y)

            if (dist > range) throw RuntimeException("Point is too far away!")

            val offset = range - dist
            return (position.x - offset)..(position.x + offset)
        }
    }

    fun part1(input: List<String>, row: Int): Int {
        val sensors = input.map { it.extractInts() }
            .map { Sensor(coordOf(it[0], it[1]), coordOf(it[2], it[3])) }

        val range = sensors.filter { row in it.yBounds }
            .map { it.xRangeAt(row) }
            .fold(listOf<IntRange>()) { acc, range ->
                val nonMerged = acc.filterNot { it.overlaps(range) }
                val mergedRange = acc.filter { it.overlaps(range) }.fold(range) { acc, r -> acc.merge(r)!! }

                nonMerged + listOf(mergedRange)
            }.first()

        return range.count() - sensors.map { it.beacon }.toSet().count { it.y == row }
    }

    fun part2(input: List<String>): Long {
        val sensors = input.map { it.extractInts() }
            .map { Sensor(coordOf(it[0], it[1]), coordOf(it[2], it[3])) }

        for (y in 0..4000000) {
            val mergedRange = sensors
                .filter { y in it.yBounds }
                .map { it.xRangeAt(y) }
                .sortedWith(compareBy<IntRange> { it.first }.thenBy { it.last })
                .fold(0..0) { acc, range -> if (range.first <= acc.last + 1) (acc.first..max(range.last, acc.last)) else acc }

            if (mergedRange.last < 4000000) {
                return (mergedRange.last.toLong() * 4000000) + y.toLong()
            }
        }

        return -1
    }

    val input = readInputLines("day15")
    println("::: Day15 :::")
    println("Part 1: ${part1(input, 2000000)}")
    println("Part 2: ${part2(input)}")

}