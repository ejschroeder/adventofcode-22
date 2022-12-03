package lol.schroeder.aoc22

import lol.schroeder.aoc22.util.readInputLines

fun main() {
    fun Char.priorityScore() = if (isUpperCase()) code - 38 else code - 96

    fun findCommonItem(bags: List<String>) = bags.first()
        .first { item -> bags.drop(1).all { item in it } }

    fun part1(input: List<String>) = input
        .map { it.chunked(it.length / 2) }
        .map { findCommonItem(it) }
        .sumOf { it.priorityScore() }

    fun part2(input: List<String>) = input
        .chunked(3)
        .map { findCommonItem(it) }
        .sumOf { it.priorityScore() }

    val input = readInputLines("day03")
    println("::: Day03 :::")
    println("Part 1: ${part1(input)}")
    println("Part 2: ${part2(input)}")
}