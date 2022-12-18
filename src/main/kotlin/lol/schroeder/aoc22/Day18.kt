package lol.schroeder.aoc22

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import lol.schroeder.aoc22.util.extractInts
import lol.schroeder.aoc22.util.minMaxOf
import lol.schroeder.aoc22.util.readInputLines
import lol.schroeder.aoc22.util.rest

fun main() {
    data class Cube(val x: Int, val y: Int, val z: Int) {
        fun neighbors(): List<Cube> {
            return listOf(
                copy(x = x + 1),
                copy(x = x - 1),
                copy(y = y + 1),
                copy(y = y - 1),
                copy(z = z + 1),
                copy(z = z - 1)
            )
        }
    }

    tailrec fun countAllExposedSurfaces(remainingCubes: List<Cube>, counted: PersistentSet<Cube>, currentCount: Int): Int {
        if (remainingCubes.isEmpty())
            return currentCount

        val cube = remainingCubes.first()
        val neighbors = cube.neighbors()

        val numNeighbors = neighbors.count { it in counted }
        val exposedSides = 6 - numNeighbors

        return countAllExposedSurfaces(remainingCubes.rest(), counted.add(cube), currentCount - numNeighbors + exposedSides)
    }

    fun getMaxRanges(cubes: Iterable<Cube>): Triple<IntRange, IntRange, IntRange> {
        val (minX, maxX) = cubes.minMaxOf { it.x }
        val (minY, maxY) = cubes.minMaxOf { it.y }
        val (minZ, maxZ) = cubes.minMaxOf { it.z }

        return Triple(minX - 1..maxX + 1, minY - 1..maxY + 1, minZ - 1..maxZ + 1)
    }

    data class Scan(val lava: Set<Cube>) {
        val xRange: IntRange
        val yRange: IntRange
        val zRange: IntRange

        init {
            val ranges = getMaxRanges(lava)
            xRange = ranges.first
            yRange = ranges.second
            zRange = ranges.third
        }

        fun isCubeInBounds(cube: Cube) = cube.x in xRange && cube.y in yRange && cube.z in zRange
    }

    tailrec fun countExternalSurfaces(scan: Scan, queue: PersistentList<Cube>, seenAir: PersistentSet<Cube>, exposedCount: Int): Int {
        if (queue.isEmpty()) {
            return exposedCount
        }

        val air = queue.first()

        return if (air in seenAir) {
            countExternalSurfaces(scan, queue.removeAt(0), seenAir, exposedCount)
        } else {
            val neighbors = air.neighbors().filter(scan::isCubeInBounds)
            val (lavaNeighbors, airNeighbors) = neighbors.partition { it in scan.lava }
            countExternalSurfaces(scan, queue.removeAt(0).addAll(airNeighbors), seenAir.add(air), exposedCount + lavaNeighbors.count())
        }
    }

    fun part1(input: List<String>): Int {
        val cubes = input.map { it.extractInts() }
            .map { Cube(it[0], it[1], it[2]) }

        return countAllExposedSurfaces(cubes, persistentSetOf(), 0)
    }

    fun part2(input: List<String>): Int {
        val lava = input.map { it.extractInts() }
            .map { Cube(it[0], it[1], it[2]) }
            .toSet()

        val scan = Scan(lava)
        val startingCube = Cube(scan.xRange.first, scan.yRange.first, scan.zRange.first)
        return countExternalSurfaces(scan, persistentListOf(startingCube), persistentSetOf(), 0)
    }

    val input = readInputLines("day18")
    println("::: Day18 :::")
    println("Part 1: ${part1(input)}")
    println("Part 2: ${part2(input)}")
}