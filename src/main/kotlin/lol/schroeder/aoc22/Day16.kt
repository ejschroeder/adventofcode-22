package lol.schroeder.aoc22

import kotlinx.collections.immutable.*
import lol.schroeder.aoc22.util.readInputLines
import kotlin.collections.setOf
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@OptIn(ExperimentalTime::class)
fun main() {
    data class Valve(val id: String, val rate: Int, val tunnels: List<String>)

    fun parseValves(input: List<String>) = input.map { it.split("; ") }
        .map {
            val valveId = it.first().split(" ")[1]
            val flowRate = it.first().substringAfterLast("=").toInt()
            val tunnels = it.last().replace(",", "").split(" ").drop(4)
            Valve(valveId, flowRate, tunnels)
        }.associateBy { it.id }

    data class FlowState(val location: String = "AA", val flowRate: Int = 0, val totalFlow: Int = 0, val time: Int = 30) {
        fun next(location: String, newRate: Int = 0) = copy(location = location, flowRate = flowRate + newRate, totalFlow = totalFlow + flowRate, time = time - 1)
    }

    var maxFlowAtState = mutableMapOf<FlowState, Int>()

    fun getMaxFlow(valves: Map<String, Valve>, currentState: FlowState, opened: PersistentSet<String>): FlowState {
        if (currentState.time == 0) {
            return currentState
        }

        if (currentState in maxFlowAtState) {
            return currentState
        }

        val node = valves[currentState.location]!!
        val openedMax = if (node.rate != 0 && node.id !in opened) {
            getMaxFlow(valves, currentState.next(node.id, node.rate), opened.add(node.id))
        } else null

        val maxFlow = node.tunnels
            .map { getMaxFlow(valves, currentState.next(it), opened) }
            .maxBy { it.totalFlow }

        if (openedMax == null) {
            maxFlowAtState.put(currentState, maxFlow.totalFlow)
            return maxFlow
        }

        if (maxFlow.totalFlow > openedMax.totalFlow) {
            maxFlowAtState.put(currentState, maxFlow.totalFlow)
            return maxFlow
        }

        maxFlowAtState.put(currentState, openedMax.totalFlow)
        return openedMax
    }

    fun part1(input: List<String>): Int {
        val valves = parseValves(input)
        maxFlowAtState = mutableMapOf()
        val maxFlowState = getMaxFlow(valves, FlowState(), persistentSetOf())

        return maxFlowAtState[FlowState()]!!
    }



    data class ElephantFlowState(val locations: Set<String> = setOf("AA"), val flowRate: Int = 0, val totalFlow: Int = 0, val time: Int = 26) {
        fun next(location1: String, location2: String, newRate: Int = 0) = copy(locations = setOf(location1, location2), flowRate = flowRate + newRate, totalFlow = totalFlow + flowRate, time = time - 1)
    }

    var maxFlowAtStateWithElephant = mutableMapOf<ElephantFlowState, Int>()
    var currentMax = 0

    fun getMaxFlow(valves: Map<String, Valve>, currentState: ElephantFlowState, opened: PersistentSet<String>): Int {
        if (currentState.time == 0) {
            if (currentState.totalFlow > currentMax) currentMax = currentState.totalFlow
            return currentState.totalFlow
        }

        if (opened.size == valves.values.count { it.rate != 0 }) {
            val amt = currentState.copy(totalFlow = currentState.totalFlow + (currentState.flowRate * currentState.time)).totalFlow
            if (amt > currentMax) currentMax = amt
            return amt
        }

        if (currentState in maxFlowAtStateWithElephant) {
            return maxFlowAtStateWithElephant[currentState]!!
        }

        val theoreticalMaxFlow = valves.filter { it.key !in opened && it.value.rate != 0 }
            .map { it.value.rate }
            .sortedDescending()
            .take(currentState.time / 2)
            .foldIndexed(0) { index, acc, flow -> acc + (( (currentState.time) - (index * 2) ) * flow) }

        if (currentState.totalFlow + (currentState.flowRate * currentState.time) + theoreticalMaxFlow < currentMax)
            return 0

        val locations = currentState.locations.toList()
        val node1Id = locations.first()
        val node2Id = if (locations.size == 2) locations.last() else locations.first()

        val node1 = valves[node1Id]!!
        val node2 = valves[node2Id]!!

        val nextOpenAndOpen = if (node1.id !in opened && node2.id !in opened && node1.id != node2.id) {
            listOf(getMaxFlow(valves, currentState.next(node1.id, node2.id, node1.rate + node2.rate), opened.add(node1.id).add(node2.id)))
        } else emptyList()

        val nextOpenAndMoveMax = if (node1.id !in opened) {
            node2.tunnels.map { getMaxFlow(valves, currentState.next(node1.id, it, node1.rate), opened.add(node1.id)) }
        } else emptyList()

        val nextMoveAndOpenMax = if (node2.id !in opened) {
            node1.tunnels.map { getMaxFlow(valves, currentState.next(it, node2.id, node2.rate), opened.add(node2.id)) }
        } else emptyList()

        val nextMoveMax = node1.tunnels.flatMap { loc1 ->
            node2.tunnels.map { loc2 -> getMaxFlow(valves, currentState.next(loc1, loc2), opened) }
        }

        val maxFlow = (nextOpenAndOpen + nextOpenAndMoveMax + nextMoveAndOpenMax + nextMoveMax).maxBy { it }
        maxFlowAtStateWithElephant[currentState] = maxFlow
        return maxFlow
    }

    fun part2(input: List<String>): Int {
        val valves = parseValves(input)
        maxFlowAtStateWithElephant = mutableMapOf()
        return getMaxFlow(valves, ElephantFlowState(), persistentSetOf())
    }

    val input = readInputLines("day16")
    println("::: Day16 :::")
//    println("Part 1: ${part1(input)}")
    val (value, time) = measureTimedValue { part2(input) }
    println("Part 2: ${value} - Time: ${time.inWholeMilliseconds}ms")
}