package lol.schroeder.aoc22

import lol.schroeder.aoc22.util.readInputLines
import lol.schroeder.aoc22.util.splitAtIndex
import lol.schroeder.aoc22.util.splitOn


sealed interface PacketData : Comparable<PacketData>
data class LiteralData(val value: Int): PacketData {
    override fun compareTo(other: PacketData) = when (other) {
        is LiteralData -> value compareTo other.value
        is ListData -> toListData() compareTo other
    }
    fun toListData() = ListData(listOf(this))
}

data class ListData(val value: List<PacketData>): PacketData {
    override fun compareTo(other: PacketData): Int = when (other) {
        is LiteralData -> this compareTo other.toListData()
        is ListData -> value.zip(other.value)
            .map { it.first compareTo it.second }
            .firstOrNull { it != 0 } ?: (value.size compareTo other.value.size)
    }
}

class PacketParser {
    fun parsePacket(data: String): PacketData {
        val strippedPacket = data.removeSurrounding("[", "]")

        if (strippedPacket.isEmpty())
            return ListData(emptyList())

        val packets = parseRest(listOf(), strippedPacket)
        return ListData(packets)
    }

    private tailrec fun parseRest(parsed: List<PacketData>, remainingPacket: String): List<PacketData> {
        if (remainingPacket.isEmpty()) return parsed

        return if (remainingPacket.startsWith("[")) {
            val closingBracePosition = findIndexOfClosingBrace(remainingPacket)
            val (listPacket, rest) = remainingPacket.splitAtIndex(closingBracePosition + 1)
            val packet = parsePacket(listPacket)
            parseRest(parsed + packet, rest.substringAfter(","))
        } else {
            val literal = remainingPacket.substringBefore(",")
            val packet = LiteralData(literal.toInt())
            val remaining = if (remainingPacket.contains(",")) remainingPacket.substringAfter(",") else ""
            parseRest(parsed + packet, remaining)
        }
    }

    private fun findIndexOfClosingBrace(string: String): Int {
        val workingString = string.takeIf { it.startsWith("[") }
            ?: throw RuntimeException("String must start with '['")

        return workingString.drop(1)
            .runningFold(1) { acc, c -> when (c) {
                '[' -> acc + 1
                ']' -> acc - 1
                else -> acc
            } }
            .indexOf(0)
            .takeUnless { it == -1 } ?: throw RuntimeException("No matching closing brace found for '$string'")
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        val parser = PacketParser()

        val pairs = input.splitOn { it.isBlank() }
            .map { it.first() to it.last() }
            .map { parser.parsePacket(it.first) to parser.parsePacket(it.second) }

        return pairs.map { (left, right) -> left <= right }
            .withIndex()
            .filter { it.value }
            .sumOf { it.index + 1 }
    }

    fun part2(input: List<String>): Int {
        val parser = PacketParser()

        val dividerPackets = listOf("[[2]]", "[[6]]")
            .map { parser.parsePacket(it) }

        val packets = dividerPackets + input.filter { it.isNotBlank() }.map { parser.parsePacket(it) }

        return packets.asSequence()
            .sorted()
            .withIndex()
            .filter { it.value in dividerPackets }
            .map { it.index + 1 }
            .reduce(Int::times)
    }

    val input = readInputLines("day13")

    println("::: Day13 :::")
    println("Part 1: ${part1(input)}")
    println("Part 2: ${part2(input)}")
}