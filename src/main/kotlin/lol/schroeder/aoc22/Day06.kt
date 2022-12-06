package lol.schroeder.aoc22

import lol.schroeder.aoc22.util.readInputLines

fun main() {
    fun String.findMarker(distinctCount: Int) = windowed(distinctCount, step = 1)
            .withIndex()
            .first { it.value.toSet().size == distinctCount }
            .index + distinctCount

    fun part1(input: String) = input.findMarker(distinctCount = 4)
    fun part2(input: String) = input.findMarker(distinctCount = 14)

    val input = readInputLines("day06")

    println("::: Day06 :::")
    println("Part 1: ${part1(input.first())}")
    println("Part 2: ${part2(input.first())}")
}