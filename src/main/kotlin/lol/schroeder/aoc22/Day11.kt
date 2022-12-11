package lol.schroeder.aoc22

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import lol.schroeder.aoc22.util.extractInts
import lol.schroeder.aoc22.util.readInputLines
import lol.schroeder.aoc22.util.rest
import lol.schroeder.aoc22.util.splitOn

fun main() {
    data class Monkey(val items: PersistentList<Long>, val op: List<String>, val divisorTest: Long, val trueTo: Int, val falseTo: Int, val inspectionCount: Long = 0) {
        fun inspectItems(mapping: (Long) -> Long): List<Long>  = items
            .map(this::operate)
            .map(mapping)
        fun catch(item: Long) = copy(items = items.add(item))
        fun getThrownTo(item: Long) = if (item % divisorTest == 0L) trueTo else falseTo
        fun completeTurn() = copy(items = items.clear(), inspectionCount = inspectionCount + items.size)

        private fun operate(item: Long): Long {
            val operand = when (op[1]) {
                "old" -> item
                else -> op[1].toLong()
            }

            return when (op[0]) {
                "+" -> item + operand
                "*" -> item * operand
                else -> throw RuntimeException()
            }
        }
    }

    fun parseMonkeys(input: List<String>): MutableList<Monkey> {
        return input.splitOn { it.isBlank() }
            .map { monkey ->
                val data = monkey.rest()
                val items = data[0].extractInts().map { it.toLong() }.toPersistentList()
                val op = data[1].substringAfter(": ").split(" ").takeLast(2)
                val divisibleBy = data[2].extractInts().first().toLong()
                val trueTo = data[3].extractInts().first()
                val falseTo = data[4].extractInts().first()

                Monkey(items, op, divisibleBy, trueTo, falseTo)
            }
            .toMutableList()
    }

    fun runMonkeyRoundPt1(monkeyList: PersistentList<Monkey>): PersistentList<Monkey> {
        return monkeyList.foldIndexed(monkeyList) { index, monkeys, _ ->
            val monkey = monkeys[index]
            val updatedMonkeys = monkey.inspectItems { it / 3 }
                .fold(monkeys) { acc, l ->
                    val throwTo = monkey.getThrownTo(l)
                    acc.set(throwTo, acc[throwTo].catch(l))
                }
            updatedMonkeys.set(index, updatedMonkeys[index].completeTurn())
        }
    }

    fun runMonkeyRoundPt2(monkeyList: PersistentList<Monkey>, commonMultiple: Long): PersistentList<Monkey> {
        return monkeyList.foldIndexed(monkeyList) { index, monkeys, _ ->
            val monkey = monkeys[index]
            val updatedMonkeys = monkey.inspectItems { it % commonMultiple }
                .fold(monkeys) { acc, l ->
                    val throwTo = monkey.getThrownTo(l)
                    acc.set(throwTo, acc[throwTo].catch(l))
                }
            updatedMonkeys.set(index, updatedMonkeys[index].completeTurn())
        }
    }

    fun part1(initialMonkeys: MutableList<Monkey>): Long {
        val finalMonkeys = 0.until(20)
            .fold(initialMonkeys.toPersistentList()) { monkeys, _ -> runMonkeyRoundPt1(monkeys) }

        return finalMonkeys.map { it.inspectionCount }.sorted().takeLast(2).reduce(Long::times)
    }

    fun part2(initialMonkeys: MutableList<Monkey>): Long {
        val commonMultiple = initialMonkeys.map { it.divisorTest }.reduce(Long::times)

        val finalMonkeys = 0.until(10000)
            .fold(initialMonkeys.toPersistentList()) { monkeys, _ -> runMonkeyRoundPt2(monkeys, commonMultiple) }

        return finalMonkeys.map { it.inspectionCount }.sorted().takeLast(2).reduce(Long::times)
    }

    val input = readInputLines("day11")

    println("::: Day11 :::")
    println("Part 1: ${part1(parseMonkeys(input))}")
    println("Part 2: ${part2(parseMonkeys(input))}")
}