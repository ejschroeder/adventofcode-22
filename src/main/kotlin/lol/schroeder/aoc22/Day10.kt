package lol.schroeder.aoc22

import lol.schroeder.aoc22.util.Grid
import lol.schroeder.aoc22.util.readInputLines
import lol.schroeder.aoc22.util.toGrid
import kotlin.math.abs

fun main() {
    fun runInstruction(instruction: String): List<Int> {
        val splitInstruction = instruction.split(" ")
        return when (splitInstruction[0]) {
            "addx" -> listOf(0, splitInstruction[1].toInt())
            "noop" -> listOf(0)
            else -> listOf()
        }
    }

    fun part1(input: List<String>): Int {
        return input.asSequence()
            .flatMap(::runInstruction)
            .runningFold(1, Int::plus)
            .withIndex()
            .filter { it.index + 1 == 20 ||  (it.index - 20 + 1) % 40 == 0 }
            .toList()
            .sumOf { (it.index + 1) * it.value }
    }

    fun part2(input: List<String>): Grid<Char> {
        return input.asSequence()
            .flatMap(::runInstruction)
            .runningFold(1, Int::plus)
            .mapIndexed { index, register -> if (abs(register - (index % 40)) <= 1) '#' else '.' }
            .toList()
            .dropLast(1)
            .toGrid(40)
    }

    val input = readInputLines("day10")

    println("::: Day10 :::")
    println("Part 1: ${part1(input)}")
    println("Part 2: \n${part2(input)}")
}