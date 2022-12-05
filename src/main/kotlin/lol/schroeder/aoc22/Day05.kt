package lol.schroeder.aoc22

import lol.schroeder.aoc22.util.*

fun main() {
    data class Instruction(val amount: Int, val from: Int, val to: Int)

    fun parseInstructions(instructions: List<String>) = instructions.map { it.extractInts() }
        .map { Instruction(amount = it[0], from = it[1] - 1, to = it[2] - 1) }

    fun parseLineToContainers(line: String) = line
        .extractAll("[A-Z]|\\s{4}")
        .map { if (it.isBlank()) null else it.first() }

    fun addContainersToStack(stacks: List<ArrayDeque<Char>>, containers: List<Char?>): List<ArrayDeque<Char>> {
        containers.forEachIndexed { idx, container ->
            container?.let { stacks[idx].add(it) }
        }
        return stacks
    }

    fun parseStacks(stacksInput: List<String>): List<ArrayDeque<Char>> {
        val parsedStacks = stacksInput.dropLast(1)
            .map(::parseLineToContainers)
        val count = parsedStacks.first().size

        return parsedStacks.reversed()
            .fold(List(count) { ArrayDeque() }, ::addContainersToStack)
    }

    fun <E> moveIndividual(cargoStacks: List<ArrayDeque<E>>, instruction: Instruction) = repeat(instruction.amount) {
        val cargo = cargoStacks[instruction.from].removeLast()
        cargoStacks[instruction.to].add(cargo)
    }

    fun <E> moveStack(cargoStacks: List<ArrayDeque<E>>, instruction: Instruction) {
        val removedCargo = cargoStacks[instruction.from].takeLast(instruction.amount)
        repeat(instruction.amount) { cargoStacks[instruction.from].removeLast() }
        cargoStacks[instruction.to].addAll(removedCargo)
    }

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