package lol.schroeder.aoc22.util

import kotlin.math.abs

enum class Direction {
    NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST;

    companion object {
        val cardinals
            get() = listOf(NORTH, EAST, SOUTH, WEST)
    }
}

fun coordOf(x: Int, y: Int) = Coordinate(x, y)
fun originCoord() = Coordinate.ORIGIN

data class Coordinate(val x: Int, val y: Int) {
    companion object {
        val ORIGIN = Coordinate(0, 0)
        fun fromIndex(i: Int, width: Int) = Coordinate(i % width, i / width)
    }

    fun getNeighbors(includeOrdinalDirections: Boolean = false) =
        if (includeOrdinalDirections)
            Direction.values().map(::move)
        else
            Direction.cardinals.map(::move)

    fun move(direction: Direction, distance: Int = 1) = when (direction) {
        Direction.NORTH -> copy(y = y + distance)
        Direction.NORTH_EAST -> copy(x = x + distance, y = y + distance)
        Direction.EAST -> copy(x = x + distance)
        Direction.SOUTH_EAST -> copy(x = x + distance, y = y - distance)
        Direction.SOUTH -> copy(y = y - distance)
        Direction.SOUTH_WEST -> copy(x = x - distance, y = y - distance)
        Direction.WEST -> copy(x = x - distance)
        Direction.NORTH_WEST -> copy(x = x - distance, y = y + distance)
    }

    fun getManhattanDistance(from: Coordinate = ORIGIN) = abs(x - from.x) + abs(y - from.y)

    override fun toString() = "($x, $y)"
}

interface Grid<E> : Collection<E> {
    val width: Int
    val height: Int
    val xIndices: IntRange
    val yIndices: IntRange
    val coordinates: Iterable<Coordinate>

    operator fun contains(coordinate: Coordinate): Boolean
    operator fun get(coordinate: Coordinate): E
    operator fun get(x: Int, y: Int): E
    fun rows(): List<List<E>>
    fun row(index: Int): List<E>
    fun columns(): List<List<E>>
    fun column(index: Int): List<E>
    fun coordinateOfFirst(predicate: (E) -> Boolean): Coordinate?
    fun coordinateOfLast(predicate: (E) -> Boolean): Coordinate?
    fun getNeighborCoordinates(coordinate: Coordinate, includeDiagonals: Boolean = false, wrap: Boolean = false): List<Coordinate>
    fun getNeighborValues(coordinate: Coordinate, includeDiagonals: Boolean = false, wrap: Boolean = false): List<E>
    fun indexOf(x: Int, y: Int) = y * width + x
    fun indexOf(coordinate: Coordinate) = indexOf(coordinate.x, coordinate.y)
    fun coordinateOf(index: Int) = Coordinate.fromIndex(index, width)
}

interface MutableGrid<E> : Grid<E> {
    operator fun set(coordinate: Coordinate, element: E): E
    operator fun set(x: Int, y: Int, element: E): E
}

class MutableListGrid<E> private constructor(private val elements: MutableList<E>, override val width: Int): MutableGrid<E>, Collection<E> by elements {
    constructor(elements: Iterable<E>, width: Int): this(elements.toMutableList(), width)

    init { require(width * height == size) { "Width does not match size: $width * $height != $size" } }

    override val height get() = size / width
    override val xIndices = 0 until width
    override val yIndices get() = 0 until height
    override val coordinates get() = elements.indices.map { coordinateOf(it) }


    override operator fun contains(coordinate: Coordinate) = coordinate.x in xIndices && coordinate.y in yIndices

    override operator fun get(coordinate: Coordinate) = elements.get(indexOf(coordinate))

    override operator fun get(x: Int, y: Int) = elements.get(indexOf(x, y))

    override operator fun set(coordinate: Coordinate, element: E) = elements.set(indexOf(coordinate), element)

    override operator fun set(x: Int, y: Int, element: E) = elements.set(indexOf(x, y), element)

    override fun coordinateOfFirst(predicate: (E) -> Boolean) = indexOfFirst(predicate)
        .takeUnless { it < 0 }?.let { coordinateOf(it) }
    override fun coordinateOfLast(predicate: (E) -> Boolean) = indexOfLast(predicate)
        .takeUnless { it < 0 }?.let { coordinateOf(it) }

    override fun getNeighborCoordinates(coordinate: Coordinate, includeDiagonals: Boolean, wrap: Boolean): List<Coordinate> {
        require(contains(coordinate)) { "Coordinate $coordinate is out of bounds for ${width}x${height} grid" }

        return coordinate.getNeighbors(includeOrdinalDirections = includeDiagonals)
            .map { if (wrap) it.copy(x = it.x.mod(width), y = it.y.mod(height)) else it }
            .filter { contains(it) }
    }

    override fun getNeighborValues(coordinate: Coordinate, includeDiagonals: Boolean, wrap: Boolean) =
        getNeighborCoordinates(coordinate, includeDiagonals, wrap)
            .map { get(it) }

    override fun rows() = yIndices.map { elements.subList(it * width, it * width + width) }

    override fun row(index: Int) = elements.subList(index * width, index * width + width)

    override fun columns() = xIndices.map { col -> List(height) { idx -> elements[col + idx * width] } }

    override fun column(index: Int) = List(height) { idx -> elements[index + idx * width] }

    override fun toString() = rows().joinToString("\n") { it.joinToString(" ") }
}

fun <T> Iterable<T>.toGrid(width: Int): Grid<T> = MutableListGrid(this, width)
fun <T> Iterable<T>.toMutableGrid(width: Int): MutableGrid<T> = MutableListGrid(this, width)

fun String.toGrid(width: Int): Grid<Char> = MutableListGrid(asIterable(), width)
fun String.toMutableGrid(width: Int): MutableGrid<Char> = MutableListGrid(asIterable(), width)