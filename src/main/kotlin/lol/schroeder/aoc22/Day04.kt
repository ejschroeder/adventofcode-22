package lol.schroeder.aoc22

import lol.schroeder.aoc22.util.*

fun main() {
    fun parseSections(input: String): Pair<IntRange, IntRange> {
        return input.mapGroups("(\\d+)-(\\d+),(\\d+)-(\\d+)") { (i1, j1, i2, j2) ->
            i1.toInt()..j1.toInt() to i2.toInt()..j2.toInt() }
    }

    fun part1(input: List<String>) = input
        .map(::parseSections)
        .count { it.first in it.second || it.second in it.first }

    fun part2(input: List<String>) = input
        .map(::parseSections)
        .count { it.first.overlaps(it.second) || it.second.overlaps(it.first) }

    val input = readInputLines("day04")
    println("::: Day04 :::")
    println("Part 1: ${part1(input)}")
    println("Part 2: ${part2(input)}")
}