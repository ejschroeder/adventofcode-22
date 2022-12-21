package lol.schroeder.aoc22.alternate

import lol.schroeder.aoc22.util.readInputLines
import kotlin.math.max

fun main() {
    data class Valve(val id: String, val rate: Int, val tunnels: List<String>)

    fun parseValves(input: List<String>) = input.map { it.split("; ") }
        .map {
            val valveId = it.first().split(" ")[1]
            val flowRate = it.first().substringAfterLast("=").toInt()
            val tunnels = it.last().replace(",", "").split(" ").drop(4)
            Valve(valveId, flowRate, tunnels)
        }.associateBy { it.id }

    fun calculateDistancesToNonZeroValves(valves: Map<String, Valve>): Map<String, Map<String, Int>> {
        val distances: MutableMap<String, MutableMap<String, Int>> = mutableMapOf()

        for ((id, _) in valves) {
            distances[id] = mutableMapOf(id to 0)

            val queue = mutableListOf(0 to valves[id]!!)
            val visited = mutableSetOf(id)

            while (queue.isNotEmpty()) {
                val (distance, valve) = queue.removeFirst()
                valve.tunnels.filterNot { it in visited }
                    .forEach {
                        visited.add(it)
                        distances[id]!![it] = distance + 1
                        queue.add((distance + 1) to valves[it]!!)
                    }
            }
        }

        return distances
            .filter { it.key == "AA" || valves[it.key]!!.rate > 0 }
            .mapValues { source -> source.value.filter { it.value != 0 && valves[it.key]?.rate != 0 } }
    }

    data class SearchState(val time: Int, val valve: String, val openValves: Int)
    val cache = mutableMapOf<SearchState, Int>()

    fun search(valves: Map<String, Valve>, totalTime: Int, startingValves: Int = 0): Int {
        val distances = calculateDistancesToNonZeroValves(valves)
        val nonEmptyValves = valves.filterValues { it.rate > 0 }
            .keys
            .withIndex()
            .associate { it.value to it.index }

        fun dfs(time: Int, valve: String, openValves: Int): Int {
            val state = SearchState(time, valve, openValves)
            if (state in cache) {
                return cache[state]!!
            }

            var max = 0
            for ((neighbor, distance) in distances[valve]!!) {
                val nextOpen = 1 shl nonEmptyValves[neighbor]!!
                val remainingTime = time - distance - 1
                if (openValves and nextOpen > 0 || remainingTime <= 0)
                    continue

                val neighborValve = valves[neighbor]!!
                max = max(max, dfs(remainingTime, neighbor, openValves or nextOpen) + neighborValve.rate * remainingTime)
            }

            cache[state] = max
            return max
        }

        return dfs(totalTime, "AA", startingValves)
    }

    fun part1(input: List<String>): Int {
        cache.clear()
        val valves = parseValves(input)
        return search(valves, 30)
    }

    fun part2(input: List<String>): Int {
        cache.clear()
        val valves = parseValves(input)
        val nonEmpty = valves.count { it.value.rate > 0 }

        val openValves = (1 shl nonEmpty) - 1

        var max = 0
        for (i in 0 until ((openValves + 1) / 2)) {
            max = max(max, search(valves, 26, i) + search(valves, 26, openValves xor i))
        }
        return max
    }

    val testInput = """
        Valve AA has flow rate=0; tunnels lead to valves DD, II, BB
        Valve BB has flow rate=13; tunnels lead to valves CC, AA
        Valve CC has flow rate=2; tunnels lead to valves DD, BB
        Valve DD has flow rate=20; tunnels lead to valves CC, AA, EE
        Valve EE has flow rate=3; tunnels lead to valves FF, DD
        Valve FF has flow rate=0; tunnels lead to valves EE, GG
        Valve GG has flow rate=0; tunnels lead to valves FF, HH
        Valve HH has flow rate=22; tunnel leads to valve GG
        Valve II has flow rate=0; tunnels lead to valves AA, JJ
        Valve JJ has flow rate=21; tunnel leads to valve II
    """.trimIndent().lines()

    println("Test Input:")
    println("Part 1: ${part1(testInput)}")
    println("Part 2: ${part2(testInput)}")
    println()

    val input = readInputLines("day16")
    println("::: Day16DFS :::")
    println("Part 1: ${part1(input)}")
    println("Part 2: ${part2(input)}")
}