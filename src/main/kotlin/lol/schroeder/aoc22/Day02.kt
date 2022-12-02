package lol.schroeder.aoc22

import lol.schroeder.aoc22.util.readInputLines

fun main() {
    val input = readInputLines("day02")
        .map { it.split(" ") }
        .dropLast(1)

    println("::: Day02 :::")
    println("Part 1: ${part1(input)}")
    println("Part 2: ${part2(input)}")
}

fun part1(input: List<List<String>>) = input
    .map { it[0].toSymbol() to it[1].toSymbol() }
    .sumOf { scoreGame(it) }

fun part2(input: List<List<String>>) = input
    .map { it[0].toSymbol() to getSymbolForDesiredResult(it[0], it[1]) }
    .sumOf { scoreGame(it) }

enum class Result(val score: Int) { WIN(6), LOSS(0), DRAW(3) }

enum class Symbol(val score: Int) {
    ROCK(1), PAPER(2), SCISSORS(3);

    val beats: Symbol
        get() = when (this) {
            ROCK -> SCISSORS
            PAPER -> ROCK
            SCISSORS -> PAPER
        }
    val losesTo: Symbol
        get() = when (this) {
            ROCK -> PAPER
            PAPER -> SCISSORS
            SCISSORS -> ROCK
        }

    fun fight(other: Symbol) = when (this) {
        other.losesTo -> Result.WIN
        other.beats -> Result.LOSS
        else -> Result.DRAW
    }
}

fun getSymbolForDesiredResult(handOne: String, handTwo: String) = when (handTwo) {
    "X" -> handOne.toSymbol().beats
    "Z" -> handOne.toSymbol().losesTo
    else -> handOne.toSymbol()
}

fun scoreGame(hand: Pair<Symbol, Symbol>) = hand.second.fight(hand.first).score + hand.second.score

fun String.toSymbol(): Symbol = when(this) {
    "A", "X" -> Symbol.ROCK
    "B", "Y" -> Symbol.PAPER
    "C", "Z" -> Symbol.SCISSORS
    else -> throw RuntimeException("Not a valid symbol!")
}