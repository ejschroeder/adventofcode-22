package lol.schroeder.aoc22

import lol.schroeder.aoc22.util.extractInts
import lol.schroeder.aoc22.util.product
import lol.schroeder.aoc22.util.readInputLines
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@OptIn(ExperimentalTime::class)
fun main() {
    data class Blueprint(
        val id: Int,
        val oreBotOreCost: Int,
        val clayBotOreCost: Int,
        val obsidianBotOreCost: Int,
        val obsidianBotClayCost: Int,
        val geodeBotOreCost: Int,
        val geodeBotObsidianCost: Int
    ) {
        val maxOreSpend = max(oreBotOreCost, max(clayBotOreCost, max(obsidianBotOreCost, geodeBotOreCost)))
        val maxClaySpend = obsidianBotClayCost
        val maxObsidianSpend = geodeBotObsidianCost
    }

    data class Bots(val ore: Int = 1, val clay: Int = 0, val obsidian: Int = 0, val geode: Int = 0)
    data class Resources(val ore: Int = 0, val clay: Int = 0, val obsidian: Int = 0, val geode: Int = 0)

    data class State(
        val time: Int = 24,
        val bots: Bots = Bots(),
        val resources: Resources = Resources()
    )

    fun parseLineToBlueprint(line: String): Blueprint {
        val nums = line.extractInts()
        return Blueprint(nums[0], nums[1], nums[2], nums[3], nums[4], nums[5], nums[6])
    }

    fun findMax(blueprint: Blueprint, totalTime: Int): Int {
        val queue = ArrayDeque<State>()
        queue.add(State(time = totalTime))
        val seen = hashSetOf<State>()
        var max = 0
        while(queue.isNotEmpty()) {
            val state = queue.removeFirst()
            val (time, bots, resources) = state

            val geodesAtCurrentRate = resources.geode + (time * bots.geode)
            if (geodesAtCurrentRate > max)
                max = geodesAtCurrentRate

            if (time == 0 || state in seen)
                continue

            seen.add(state)

            // geode
            if (bots.obsidian > 0 && bots.ore > 0) {
                val obsidianTime = ceil((blueprint.geodeBotObsidianCost - resources.obsidian) / bots.obsidian.toDouble()).toInt()
                val oreTime = ceil((blueprint.geodeBotOreCost - resources.ore) / bots.ore.toDouble()).toInt()
                val timeToBuild = max(obsidianTime, oreTime).let { if (it >= 0) it else 0 }

                val oreCappedAmount = blueprint.maxOreSpend * (time - timeToBuild - 1)
                val clayCappedAmount = blueprint.maxClaySpend * (time - timeToBuild - 1)
                val obsidianCappedAmount = blueprint.maxObsidianSpend * (time - timeToBuild - 1)

                if (time - timeToBuild - 1 > 0) {
                    val nextState = state.copy(
                        time = time - timeToBuild - 1,
                        bots = bots.copy(geode = bots.geode + 1),
                        resources = resources.copy(
                            ore = min(resources.ore + (bots.ore * (timeToBuild + 1)) - blueprint.geodeBotOreCost, oreCappedAmount),
                            clay = min(resources.clay + (bots.clay * (timeToBuild + 1)), clayCappedAmount),
                            obsidian = min(resources.obsidian + (bots.obsidian * (timeToBuild + 1)) - blueprint.geodeBotObsidianCost, obsidianCappedAmount),
                            geode = resources.geode + (bots.geode * (timeToBuild + 1))
                        )
                    )
                    queue.add(nextState)
                }
            }

            // obsidian
            if (bots.obsidian < blueprint.maxObsidianSpend && bots.clay > 0 && bots.ore > 0) {
                val clayTime = ceil((blueprint.obsidianBotClayCost - resources.clay) / bots.clay.toDouble()).toInt()
                val oreTime = ceil((blueprint.obsidianBotOreCost - resources.ore) / bots.ore.toDouble()).toInt()
                val timeToBuild = max(clayTime, oreTime).let { if (it >= 0) it else 0 }

                val oreCappedAmount = blueprint.maxOreSpend * (time - timeToBuild - 1)
                val clayCappedAmount = blueprint.maxClaySpend * (time - timeToBuild - 1)
                val obsidianCappedAmount = blueprint.maxObsidianSpend * (time - timeToBuild - 1)

                if (time - timeToBuild - 1 > 0) {
                    val nextState = state.copy(
                        time = time - timeToBuild - 1,
                        bots = bots.copy(obsidian = bots.obsidian + 1),
                        resources = resources.copy(
                            ore = min(resources.ore + (bots.ore * (timeToBuild + 1)) - blueprint.obsidianBotOreCost, oreCappedAmount),
                            clay = min(resources.clay + (bots.clay * (timeToBuild + 1)) - blueprint.obsidianBotClayCost, clayCappedAmount),
                            obsidian = min(resources.obsidian + (bots.obsidian * (timeToBuild + 1)), obsidianCappedAmount),
                            geode = resources.geode + (bots.geode * (timeToBuild + 1))
                        )
                    )
                    queue.add(nextState)
                }
            }

            // clay
            if (bots.clay < blueprint.maxClaySpend && bots.ore > 0) {
                val oreTime = ceil((blueprint.clayBotOreCost - resources.ore) / bots.ore.toDouble()).toInt()
                val timeToBuild = max(0, oreTime)

                val oreCappedAmount = blueprint.maxOreSpend * (time - timeToBuild - 1)
                val clayCappedAmount = blueprint.maxClaySpend * (time - timeToBuild - 1)
                val obsidianCappedAmount = blueprint.maxObsidianSpend * (time - timeToBuild - 1)

                if (time - timeToBuild - 1 > 0) {
                    val nextState = state.copy(
                        time = time - timeToBuild - 1,
                        bots = bots.copy(clay = bots.clay + 1),
                        resources = resources.copy(
                            ore = min(resources.ore + (bots.ore * (timeToBuild + 1)) - blueprint.clayBotOreCost, oreCappedAmount),
                            clay = min(resources.clay + (bots.clay * (timeToBuild + 1)), clayCappedAmount),
                            obsidian = min(resources.obsidian + (bots.obsidian * (timeToBuild + 1)), obsidianCappedAmount),
                            geode = resources.geode + (bots.geode * (timeToBuild + 1))
                        )
                    )
                    queue.add(nextState)
                }
            }

            // ore
            if (bots.ore < blueprint.maxOreSpend && bots.ore > 0) {
                val oreTime = ceil((blueprint.oreBotOreCost - resources.ore) / bots.ore.toDouble()).toInt()
                val timeToBuild = max(0, oreTime)

                val oreCappedAmount = blueprint.maxOreSpend * (time - timeToBuild - 1)
                val clayCappedAmount = blueprint.maxClaySpend * (time - timeToBuild - 1)
                val obsidianCappedAmount = blueprint.maxObsidianSpend * (time - timeToBuild - 1)

                if (time - timeToBuild - 1 > 0) {
                    val nextState = state.copy(
                        time = time - timeToBuild - 1,
                        bots = bots.copy(ore = bots.ore + 1),
                        resources = resources.copy(
                            ore = min(resources.ore + (bots.ore * (timeToBuild + 1)) - blueprint.oreBotOreCost, oreCappedAmount),
                            clay = min(resources.clay + (bots.clay * (timeToBuild + 1)), clayCappedAmount),
                            obsidian = min(resources.obsidian + (bots.obsidian * (timeToBuild + 1)), obsidianCappedAmount),
                            geode = resources.geode + (bots.geode * (timeToBuild + 1))
                        )
                    )
                    queue.add(nextState)
                }
            }
        }
        return max
    }

    fun part1(input: List<String>): Int {
        val blueprints = input.map(::parseLineToBlueprint)
        return blueprints.sumOf { it.id * findMax(it, 24) }
    }

    fun part2(input: List<String>): Int {
        val blueprints = input.map(::parseLineToBlueprint).take(3)
        return blueprints.map { findMax(it, 32) }.product()
    }

    val input = readInputLines("day19")
    println("::: Day19 :::")
    val (part1Result, part1Time) = measureTimedValue { part1(input) }
    println("Part 1: $part1Result - Time: ${part1Time.inWholeMilliseconds}ms")
    val (part2Result, part2Time) = measureTimedValue { part2(input) }
    println("Part 2: $part2Result - Time: ${part2Time.inWholeMilliseconds}ms")
}