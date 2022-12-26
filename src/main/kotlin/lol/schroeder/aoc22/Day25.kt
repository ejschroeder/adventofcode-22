package lol.schroeder.aoc22

import lol.schroeder.aoc22.util.readInputLines
import kotlin.math.pow

fun main() {
    fun getSnafuPlace(idx: Int) = (5.0).pow(idx).toLong()

    fun getSnafuDigitValue(char: Char): Long = when (char) {
        '2' -> 2
        '1' -> 1
        '0' -> 0
        '-' -> -1
        '=' -> -2
        else -> throw IllegalArgumentException()
    }

    fun getSnafuDigitForRemainder(value: Long) = when (value) {
        2L -> '2'
        1L -> '1'
        0L -> '0'
        4L -> '-'
        3L -> '='
        else -> throw RuntimeException()
    }

    fun snafuToDecimal(value: String) = value
        .reversed()
        .mapIndexed { idx, c -> getSnafuPlace(idx) * getSnafuDigitValue(c) }
        .sum()

    tailrec fun decimalToSnafu(value: Long, snafu: String = ""): String {
        if (value <= 0)
            return snafu

        val remainder = value % 5
        val quotient = (value / 5).let { if (remainder > 2) it + 1 else it  }

        return decimalToSnafu(quotient, getSnafuDigitForRemainder(remainder) + snafu)
    }

    fun part1(input: List<String>): String {
        val sum = input.map(::snafuToDecimal)
            .sum()
        return decimalToSnafu(sum)
    }

    val input = readInputLines("day25")
    println("::: Day25 :::")
    println("Part 1: ${part1(input)}")
}