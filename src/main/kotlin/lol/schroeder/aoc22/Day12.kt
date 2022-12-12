package lol.schroeder.aoc22

import lol.schroeder.aoc22.util.Coordinate
import lol.schroeder.aoc22.util.Grid
import lol.schroeder.aoc22.util.readInputLines
import lol.schroeder.aoc22.util.toGrid
import java.util.PriorityQueue

fun main() {
    data class Vertex(val data: Coordinate, val cost: Int) : Comparable<Vertex> {
        override fun compareTo(other: Vertex) = compareValues(cost, other.cost)
    }

    fun elevationOf(c: Char) = when(c) {
        'S' -> 'a'
        'E' -> 'z'
        else -> c
    }

    fun findShortestPath(grid: Grid<Char>, start: Coordinate, isValidNeighbor: (Char, Char) -> Boolean): Map<Coordinate, Int> {
        val distances = mutableMapOf(start to 0)
        val queue = PriorityQueue<Vertex>().apply { add(Vertex(start, 0)) }

        while (queue.isNotEmpty()) {
            val vertex = queue.poll()
            grid.getNeighborCoordinates(vertex.data)
                .filter { isValidNeighbor(grid[vertex.data], grid[it]) }
                .filter { distances[vertex.data]!! + 1 < distances.getOrElse(it) { Int.MAX_VALUE } }
                .forEach {
                    val newDist = distances[vertex.data]!! + 1
                    distances[it] = newDist
                    queue.add(Vertex(it, newDist))
                }
        }

        return distances
    }

    fun part1(grid: Grid<Char>): Int {
        val startingCoordinate = grid.coordinateOfFirst { it == 'S' }!!

        val distances = findShortestPath(grid, startingCoordinate) { current, neighbor ->
            elevationOf(neighbor) - elevationOf(current) <= 1
        }

        val endingCoordinate = grid.coordinateOfFirst { it == 'E' }!!
        return distances[endingCoordinate]!!
    }

    fun part2(grid: Grid<Char>): Int {
        val startingCoordinate = grid.coordinateOfFirst { it == 'E' }!!

        val distances = findShortestPath(grid, startingCoordinate) { current, neighbor ->
            elevationOf(current) - elevationOf(neighbor) <= 1
        }

        val endingPoints = grid.withIndex().filter { it.value == 'a' || it.value == 'S' }
            .map { grid.coordinateOf(it.index) }

        return distances.filterKeys { it in endingPoints }
            .minOf { it.value }
    }

    val input = readInputLines("day12")
    val grid = input
        .flatMap { it.toList() }
        .toGrid(input.first().length)

    println("::: Day12 :::")
    println("Part 1: ${part1(grid)}")
    println("Part 2: ${part2(grid)}")
}