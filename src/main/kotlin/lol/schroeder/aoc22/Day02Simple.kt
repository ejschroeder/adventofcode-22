package lol.schroeder.aoc22

import lol.schroeder.aoc22.util.readInputLines

const val WIN = 6
const val DRAW = 3
const val LOSE = 0

const val ROCK = 1
const val PAPER = 2
const val SCISSORS = 3

fun main() {
    val input = readInputLines("day02")

    println("::: Day02 :::")
    println("Part 1: ${part1(input)}")
    println("Part 2: ${part2(input)}")
}

fun part1(input: List<String>) = input
    .sumOf { scorePart1(it) }

fun part2(input: List<String>) = input
    .sumOf { scorePart2(it) }

fun scorePart1(hands: String) = when (hands) {
    "A X" -> DRAW + ROCK
    "A Y" -> WIN + PAPER
    "A Z" -> LOSE + SCISSORS
    "B X" -> LOSE + ROCK
    "B Y" -> DRAW + PAPER
    "B Z" -> WIN + SCISSORS
    "C X" -> WIN + ROCK
    "C Y" -> LOSE + PAPER
    "C Z" -> DRAW + SCISSORS
    else -> 0
}

fun scorePart2(hands: String) = when (hands) {
    "A X" -> LOSE + SCISSORS
    "A Y" -> DRAW + ROCK
    "A Z" -> WIN + PAPER
    "B X" -> LOSE + ROCK
    "B Y" -> DRAW + PAPER
    "B Z" -> WIN + SCISSORS
    "C X" -> LOSE + PAPER
    "C Y" -> DRAW + SCISSORS
    "C Z" -> WIN + ROCK
    else -> 0
}