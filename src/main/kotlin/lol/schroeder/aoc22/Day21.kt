package lol.schroeder.aoc22

import lol.schroeder.aoc22.util.isNumeric
import lol.schroeder.aoc22.util.readInputLines

sealed interface Monkey {
    val name: String
    fun eval(monkeys: Map<String, Monkey>): Long
    fun hasHuman(monkeys: Map<String, Monkey>): Boolean
    fun solveFor(value: Long, monkeys: Map<String, Monkey>): Long
}

data class ExpressionMonkey(override val name: String, val operand1: String, val operation: String, val operand2: String) : Monkey {
    override fun eval(monkeys: Map<String, Monkey>): Long {
        return when (operation) {
            "+" -> monkeys[operand1]!!.eval(monkeys) + monkeys[operand2]!!.eval(monkeys)
            "-" -> monkeys[operand1]!!.eval(monkeys) - monkeys[operand2]!!.eval(monkeys)
            "*" -> monkeys[operand1]!!.eval(monkeys) * monkeys[operand2]!!.eval(monkeys)
            "/" -> monkeys[operand1]!!.eval(monkeys) / monkeys[operand2]!!.eval(monkeys)
            else -> throw RuntimeException()
        }
    }

    override fun hasHuman(monkeys: Map<String, Monkey>): Boolean {
        return monkeys[operand1]!!.hasHuman(monkeys) || monkeys[operand2]!!.hasHuman(monkeys)
    }

    override fun solveFor(value: Long, monkeys: Map<String, Monkey>): Long {
        val monkey1 = monkeys[operand1]!!
        val monkey2 = monkeys[operand2]!!

        if (!monkey1.hasHuman(monkeys) && !monkey2.hasHuman(monkeys))
            throw RuntimeException("No human in this branch")

        if (monkey1.hasHuman(monkeys)) {
            val monkey2Value = monkey2.eval(monkeys)
            return when(operation) {
                "+" -> monkey1.solveFor(value - monkey2Value, monkeys)
                "-" -> monkey1.solveFor(value + monkey2Value, monkeys)
                "*" -> monkey1.solveFor(value / monkey2Value, monkeys)
                "/" -> monkey1.solveFor(value * monkey2Value, monkeys)
                else -> throw RuntimeException()
            }
        } else {
            val monkey1Value = monkey1.eval(monkeys)
            return when(operation) {
                "+" -> monkey2.solveFor(value - monkey1Value, monkeys)
                "-" -> monkey2.solveFor(monkey1Value - value, monkeys)
                "*" -> monkey2.solveFor(value / monkey1Value, monkeys)
                "/" -> monkey2.solveFor(monkey1Value / value, monkeys)
                else -> throw RuntimeException()
            }
        }
    }
}

data class LiteralMonkey(override val name: String, val value: Long) : Monkey {
    override fun eval(monkeys: Map<String, Monkey>) = value
    override fun hasHuman(monkeys: Map<String, Monkey>) = name == "humn"
    override fun solveFor(value: Long, monkeys: Map<String, Monkey>) = value
}

fun main() {
    fun parseMonkeys(input: List<String>): Map<String, Monkey> {
        return input.map {
            val pieces = it.split(": ")
            if (pieces[1].isNumeric())
                LiteralMonkey(pieces[0], pieces[1].toLong())
            else {
                val expression = pieces[1].split(" ")
                ExpressionMonkey(pieces[0], expression[0], expression[1], expression[2])
            }
        }.associateBy { it.name }
    }

    fun part1(input: List<String>): Long {
        val monkeys = parseMonkeys(input)
        return monkeys["root"]!!.eval(monkeys)
    }

    fun part2(input: List<String>): Long {
        val monkeys = parseMonkeys(input)

        val root = monkeys["root"]!! as ExpressionMonkey

        return if (monkeys[root.operand1]!!.hasHuman(monkeys)) {
            monkeys[root.operand1]!!.solveFor(monkeys[root.operand2]!!.eval(monkeys), monkeys)
        } else {
            monkeys[root.operand2]!!.solveFor(monkeys[root.operand1]!!.eval(monkeys), monkeys)
        }
    }

    val input = readInputLines("day21")
    println("::: Day21 :::")
    println("Part 1: ${part1(input)}")
    println("Part 2: ${part2(input)}")
}