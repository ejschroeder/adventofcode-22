package lol.schroeder.aoc22

import lol.schroeder.aoc22.util.*

fun main() {
    data class Instruction(val amount: Int, val from: Int, val to: Int)

    fun <E> moveIndividual(cargoStacks: List<ArrayDeque<E>>, instruction: Instruction) {
        (0 until instruction.amount).forEach { _ ->
            val cargo = cargoStacks[instruction.from].removeLast()
            cargoStacks[instruction.to].add(cargo)
        }
    }

    fun <E> moveStack(cargoStacks: List<ArrayDeque<E>>, instruction: Instruction) {
        val removedCargo = (0 until instruction.amount).fold(mutableListOf<E>()) { acc, _ ->
            acc.add(cargoStacks[instruction.from].removeLast())
            acc
        }

        cargoStacks[instruction.to].addAll(removedCargo.reversed())
    }

    fun parseStacks(stacks: List<String>): List<ArrayDeque<Char>> {
        val chunkedStacks = stacks.reversed()
            .map { it.chunked(4) }
        val count = chunkedStacks.first().size

        return chunkedStacks.rest()
            .map { chunkedLine -> chunkedLine.map { it[1] } }
            .fold(List(count) { ArrayDeque<Char>() }) { acc, cargoLetters ->
                cargoLetters.withIndex()
                    .filter { it.value.isLetter() }
                    .forEach { acc[it.index].add(it.value) }
                acc
            }
    }

    fun parseInstructions(instructions: List<String>) = instructions.map { it.extractInts() }
            .map { Instruction(amount = it[0], from = it[1] - 1, to = it[2] - 1) }

    fun part1(input: List<List<String>>): String {
        val cargoStacks = parseStacks(input.first())
        val instructions = parseInstructions(input.last())

        instructions.forEach {
            moveIndividual(cargoStacks, it)
        }

        return cargoStacks.map { it.last() }.joinToString("")
    }

    fun part2(input: List<List<String>>): String {
        val cargoStacks = parseStacks(input.first())
        val instructions = parseInstructions(input.last())

        instructions.forEach {
            moveStack(cargoStacks, it)
        }

        return cargoStacks.map { it.last() }.joinToString("")
    }

    val input = readInputLines("day05")
        .splitOn { it.isBlank() }
        .toList()

    println("::: Day05 :::")
    println("Part 1: ${part1(input)}")
    println("Part 2: ${part2(input)}")
}