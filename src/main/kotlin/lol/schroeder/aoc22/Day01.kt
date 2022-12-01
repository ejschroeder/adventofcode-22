package lol.schroeder.aoc22

import lol.schroeder.aoc22.util.getResourceAsText

fun part1(input: List<Long>) = input.max()

fun part2(input: List<Long>) = input
        .sorted()
        .takeLast(3)
        .sum()

fun main() {
    val input = getResourceAsText("day01")
        .split("\n\n")
        .map { it.trim().split("\n").map(String::toLong) }
        .map { it.sum() }

    println("::: Day01 :::")
    println("Part 1: ${part1(input)}")
    println("Part 2: ${part2(input)}")
}