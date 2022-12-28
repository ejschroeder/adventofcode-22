package lol.schroeder.aoc22

import lol.schroeder.aoc22.util.*

fun main() {
    fun <T> Grid<T>.above(coordinate: Coordinate) = column(coordinate.x).subList(0, coordinate.y).reversed()
    fun <T> Grid<T>.below(coordinate: Coordinate) = column(coordinate.x).subList(coordinate.y + 1, height)
    fun <T> Grid<T>.left(coordinate: Coordinate) = row(coordinate.y).subList(0, coordinate.x).reversed()
    fun <T> Grid<T>.right(coordinate: Coordinate) = row(coordinate.y).subList(coordinate.x + 1, width)

    fun Grid<Char>.isTreeVisible(coordinate: Coordinate): Boolean {
        val tree = get(coordinate)
        return above(coordinate).all { it < tree }
                || below(coordinate).all { it < tree }
                || left(coordinate).all { it < tree }
                || right(coordinate).all { it < tree }
    }

    fun Grid<Char>.getTreeScore(coordinate: Coordinate): Int {
        val tree = get(coordinate)
        return listOf(above(coordinate), below(coordinate), left(coordinate), right(coordinate))
            .map { trees -> trees.takeWhileInclusive { it < tree } }
            .map { it.count() }
            .product()
    }

    fun part1(input: List<String>): Int {
        val grid = input.flatMap { it.toList() }
            .toGrid(input.first().length)

        return grid.coordinates.count(grid::isTreeVisible)
    }

    fun part2(input: List<String>): Int {
        val grid = input.flatMap { it.toList() }
            .toGrid(input.first().length)

        return grid.coordinates.maxOf(grid::getTreeScore)
    }

    val input = readInputLines("day08")
    println("::: Day08 :::")
    println("Part 1: ${part1(input)}")
    println("Part 2: ${part2(input)}")
}