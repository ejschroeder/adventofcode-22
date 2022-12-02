package lol.schroeder.aoc22

import lol.schroeder.aoc22.util.readInputLines
import lol.schroeder.aoc22.util.splitOn

fun part1(input: List<Long>) = input.maxOrNull()

fun part2(input: List<Long>) = input
        .sorted()
        .takeLast(3)
        .sum()

fun main() {
    val input = readInputLines("day01")
        .splitOn { it.isBlank() }
        .map { it.sumOf(String::toLong) }
        .toList()

    println("::: Day01 :::")
    println("Part 1: ${part1(input)}")
    println("Part 2: ${part2(input)}")
}