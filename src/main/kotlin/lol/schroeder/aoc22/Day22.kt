package lol.schroeder.aoc22

import lol.schroeder.aoc22.util.*

fun main() {
    data class Position(val coordinate: Coordinate, val facing: Int = 0) {
        fun forward() = when (facing) {
            3 -> copy(coordinate = coordinate.move(Direction.SOUTH))
            0 -> copy(coordinate = coordinate.move(Direction.EAST))
            1 -> copy(coordinate = coordinate.move(Direction.NORTH))
            2 -> copy(coordinate = coordinate.move(Direction.WEST))
            else -> this
        }

        fun turn(dir: Char) = when (dir) {
            'L' -> copy(facing = (facing - 1).mod(4))
            'R' -> copy(facing = (facing + 1).mod(4))
            else -> this
        }
    }

    fun parseGrid(input: List<String>): Grid<Char> {
        val maxWidth = input.maxOf { it.length }
        return input.map { it.padEnd(maxWidth, ' ') }
            .map { it.toList() }
            .flatten()
            .toGrid(maxWidth)
    }

    fun parseInstructions(input: String): Sequence<String> {
        return sequence {
            var isMove = true
            var remainingInstruction = input
            while (remainingInstruction.isNotEmpty()) {
                val idx = remainingInstruction.indexOfFirst { if (isMove) it.isLetter() else it.isDigit() }
                    .takeUnless { it == -1 } ?: remainingInstruction.length
                yield(remainingInstruction.substring(0, idx))
                remainingInstruction = remainingInstruction.substring(idx)
                isMove = !isMove
            }
        }
    }

    fun wrap(pos: Position, grid: Grid<Char>): Position {
        if (pos.coordinate !in grid || grid[pos.coordinate] == ' ') {
            val wrappedCoord = when (pos.facing) {
                0 -> coordOf(grid.row(pos.coordinate.y).indexOfFirst { it != ' ' }, pos.coordinate.y)
                1 -> coordOf(pos.coordinate.x, grid.column(pos.coordinate.x).indexOfFirst { it != ' ' })
                2 -> coordOf(grid.row(pos.coordinate.y).indexOfLast { it != ' ' }, pos.coordinate.y)
                3 -> coordOf(pos.coordinate.x, grid.column(pos.coordinate.x).indexOfLast { it != ' ' })
                else -> throw RuntimeException()
            }
            return pos.copy(coordinate = wrappedCoord)
        }

        return pos
    }

    fun part1(input: List<String>): Int {
        val parts = input.splitOn { it.isBlank() }
        val instructions = parseInstructions(parts.last().first())

        val grid = parseGrid(parts.first())
        val startingCoordinate = grid.coordinateOfFirst { it == '.' }!!

        var position = Position(startingCoordinate)

        instr@for (instr in instructions) {
            if (instr.isNumeric()) {
                val amt = instr.toInt()

                for (step in 1..amt) {
                    val newPos = wrap(position.forward(), grid)
                    if (grid[newPos.coordinate] == '#') {
                        continue@instr
                    }
                    position = newPos
                }
            } else {
                position = position.turn(instr.first())
            }
        }

        return (1000 * (position.coordinate.y + 1)) + (4 * (position.coordinate.x + 1)) + position.facing
    }

    fun part2(input: List<String>) {
        val parts = input.splitOn { it.isBlank() }
        val grid = parseGrid(parts.first())

        println(grid)
    }

    val testInput = """
                ...#
                .#..
                #...
                ....
        ...#.......#
        ........#...
        ..#....#....
        ..........#.
                ...#....
                .....#..
                .#......
                ......#.
        
        10R5L5R10L4R5L5
    """.trimIndent().lines()

    println("Test Input:")
    println("Part 1: ${part1(testInput)}")
//    println("Part 2: ${part2(testInput)}")
    println()

    val input = readInputLines("day22")
    println("::: Day22 :::")
    println("Part 1: ${part1(input)}")
//    println("Part 2: ${part2(input)}")
}