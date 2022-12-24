package lol.schroeder.aoc22

import lol.schroeder.aoc22.util.*

const val E = 0
const val S = 1
const val W = 2
const val N = 3

fun main() {
    data class Position(val coordinate: Coordinate, val facing: Int = 0) {
        fun forward() = when (facing) {
            N -> copy(coordinate = coordinate.move(Direction.SOUTH))
            E -> copy(coordinate = coordinate.move(Direction.EAST))
            S -> copy(coordinate = coordinate.move(Direction.NORTH))
            W -> copy(coordinate = coordinate.move(Direction.WEST))
            else -> this
        }

        fun turn(dir: Char) = when (dir) {
            'L' -> copy(facing = (facing - 1).mod(4))
            'R' -> copy(facing = (facing + 1).mod(4))
            else -> this
        }
    }

    data class CubeDirection(val face: Int, val direction: Int)
    fun cd(face: Int, direction: Int) = CubeDirection(face, direction)
    data class CubeNet(val id: Int, val mappings: Map<CubeDirection, CubeDirection>)

    data class JungleMap(val grid: Grid<Char>, val cubeNet: CubeNet) {
        val cubeSize = grid.width / 4
        fun getLocalCoordinate(coord: Coordinate) = coordOf(coord.x % cubeSize, coord.y % cubeSize)
        fun getGlobalCoordinate(face: Int, coordinate: Coordinate): Coordinate {
            val faceX = face % 4
            val faceY = face / 4
            return coordOf(coordinate.x + (cubeSize * faceX), coordinate.y + (cubeSize * faceY))
        }
        fun getFace(coord: Coordinate) = (coord.y / cubeSize) * 4 + (coord.x / cubeSize)
        fun getNextPosition(position: Position): Position {
            val currentLocalCoordinate = getLocalCoordinate(position.coordinate)

            val willWrap = position.facing == E && currentLocalCoordinate.x == cubeSize - 1
                    || position.facing == S && currentLocalCoordinate.y == cubeSize - 1
                    || position.facing == W && currentLocalCoordinate.x == 0
                    || position.facing == N && currentLocalCoordinate.y == 0

            if (!willWrap) {
                return position.forward()
            }

            val localCoordinates = getLocalCoordinate(position.coordinate)
            val face = getFace(position.coordinate)
            val direction = position.facing
            val cubeDir = cubeNet.mappings[cd(face, direction)]!!

            val newLocalX = when (cubeDir.direction) {
                E -> 0
                S -> when (position.facing) {
                    E -> cubeSize - 1 - localCoordinates.y
                    S -> localCoordinates.x
                    W -> localCoordinates.y
                    N -> cubeSize - 1 - localCoordinates.x
                    else -> throw RuntimeException()
                }
                W -> cubeSize - 1
                N -> when (position.facing) {
                    E -> localCoordinates.y
                    S -> cubeSize - 1 - localCoordinates.x
                    W -> cubeSize - 1 - localCoordinates.y
                    N -> localCoordinates.x
                    else -> throw RuntimeException()
                }
                else -> throw RuntimeException()
            }

            val newLocalY = when (cubeDir.direction) {
                E -> when (position.facing) {
                    E -> localCoordinates.y
                    S -> cubeSize - 1 - localCoordinates.x
                    W -> cubeSize - 1 - localCoordinates.y
                    N -> localCoordinates.x
                    else -> throw RuntimeException()
                }
                S -> 0
                W -> when (position.facing) {
                    E -> cubeSize - 1 - localCoordinates.y
                    S -> localCoordinates.x
                    W -> localCoordinates.y
                    N -> cubeSize - 1 - localCoordinates.x
                    else -> throw RuntimeException()
                }
                N -> cubeSize - 1
                else -> throw RuntimeException()
            }

            val globalCoordinate = getGlobalCoordinate(cubeDir.face, coordOf(newLocalX, newLocalY))
            return Position(globalCoordinate, cubeDir.direction)
        }
    }

    val cubeNets = listOf(
        CubeNet(id = 0b0010111000110000, mappings = mapOf( // Test Net
            cd(2,  E) to cd(11, W), cd(2,  S) to cd(6,  S), cd(2,  W) to cd(5,  S), cd(2,  N) to cd(4, S),
            cd(4,  E) to cd(5,  E), cd(4,  S) to cd(10, N), cd(4,  W) to cd(11, N), cd(4,  N) to cd(2, S),
            cd(5,  E) to cd(6,  E), cd(5,  S) to cd(10, E), cd(5,  W) to cd(4,  W), cd(5,  N) to cd(2, E),
            cd(6,  E) to cd(11, S), cd(6,  S) to cd(10, S), cd(6,  W) to cd(5,  W), cd(6,  N) to cd(2, N),
            cd(10, E) to cd(11, E), cd(10, S) to cd(4,  N), cd(10, W) to cd(5,  N), cd(10, N) to cd(6, N),
            cd(11, E) to cd(2,  W), cd(11, S) to cd(4,  E), cd(11, W) to cd(10, W), cd(11, N) to cd(6, W),
        )),
        CubeNet(id = 0b0110010011001000, mappings = mapOf( // Real Net
            cd(1,  E) to cd(2,  E), cd(1,  S) to cd(5,  S), cd(1,  W) to cd(8, E), cd(1,  N) to cd(12, E),
            cd(2,  E) to cd(9,  W), cd(2,  S) to cd(5,  W), cd(2,  W) to cd(1, W), cd(2,  N) to cd(12, N),
            cd(5,  E) to cd(2,  N), cd(5,  S) to cd(9,  S), cd(5,  W) to cd(8, S), cd(5,  N) to cd(1,  N),
            cd(8,  E) to cd(9,  E), cd(8,  S) to cd(12, S), cd(8,  W) to cd(1, E), cd(8,  N) to cd(5,  E),
            cd(9,  E) to cd(2,  W), cd(9,  S) to cd(12, W), cd(9,  W) to cd(8, W), cd(9,  N) to cd(5,  N),
            cd(12, E) to cd(9,  N), cd(12, S) to cd(2,  S), cd(12, W) to cd(1, S), cd(12, N) to cd(8,  N),
        ))
    )

    fun getCubeNet(grid: Grid<Char>): CubeNet {
        val cubeSize = grid.width / 4
        val netType = (0..15).fold(0) { acc, idx ->
            val x = (idx % 4) * cubeSize
            val y = (idx / 4) * cubeSize
            val tile = if (grid[x, y] == ' ') 0 else 1
            (acc shl 1) or tile
        }
        return cubeNets.findOrThrow { it.id == netType }
    }

    fun parseGrid(input: List<String>): Grid<Char> {
        val cubeSize = input.minOf { it.substringAfterLast(" ").length }
        val maxWidth = cubeSize * 4
        return input.joinToString("") { it.padEnd(maxWidth, ' ') }
            .padEnd(maxWidth * maxWidth, ' ')
            .toGrid(maxWidth)
    }

    tailrec fun parseInstructions(remainingInstructions: String, instructions: List<String> = listOf(), isMove: Boolean = true): List<String> {
        if (remainingInstructions.isEmpty())
            return instructions

        val indexAfterNextInstruction = remainingInstructions.indexOfFirst { if (isMove) it.isLetter() else it.isDigit() }
            .takeUnless { it == -1 } ?: remainingInstructions.length

        val nextInstruction = remainingInstructions.substring(0, indexAfterNextInstruction)
        return parseInstructions(remainingInstructions.substring(indexAfterNextInstruction), instructions + nextInstruction, !isMove)
    }

    fun wrap(pos: Position, grid: Grid<Char>): Position {
        if (pos.coordinate !in grid || grid[pos.coordinate] == ' ') {
            val wrappedCoord = when (pos.facing) {
                E -> coordOf(grid.row(pos.coordinate.y).indexOfFirst { it != ' ' }, pos.coordinate.y)
                S -> coordOf(pos.coordinate.x, grid.column(pos.coordinate.x).indexOfFirst { it != ' ' })
                W -> coordOf(grid.row(pos.coordinate.y).indexOfLast { it != ' ' }, pos.coordinate.y)
                N -> coordOf(pos.coordinate.x, grid.column(pos.coordinate.x).indexOfLast { it != ' ' })
                else -> throw RuntimeException()
            }
            return pos.copy(coordinate = wrappedCoord)
        }
        return pos
    }

    tailrec fun move(pos: Position, grid: Grid<Char>, amount: Int): Position {
        if (amount == 0)
            return pos

        val nextPos = wrap(pos.forward(), grid)

        if (grid[nextPos.coordinate] == '#')
            return pos

        return move(nextPos, grid, amount - 1)
    }

    tailrec fun moveCube(pos: Position, map: JungleMap, amount: Int): Position {
        if (amount == 0)
            return pos

        val nextPos = map.getNextPosition(pos)

        if (map.grid[nextPos.coordinate] == '#')
            return pos

        return moveCube(nextPos, map, amount - 1)
    }

    tailrec fun runInstructions(pos: Position, grid: Grid<Char>, remainingInstructions: List<String>): Position {
        if (remainingInstructions.isEmpty())
            return pos

        val instr = remainingInstructions.first()

        val nextPosition = if (instr.isNumeric())
            move(pos, grid, instr.toInt())
        else
            pos.turn(instr.first())

        return runInstructions(nextPosition, grid, remainingInstructions.rest())
    }

    tailrec fun runCubeInstructions(pos: Position, map: JungleMap, remainingInstructions: List<String>): Position {
        if (remainingInstructions.isEmpty())
            return pos

        val instr = remainingInstructions.first()

        val nextPosition = if (instr.isNumeric())
            moveCube(pos, map, instr.toInt())
        else
            pos.turn(instr.first())

        return runCubeInstructions(nextPosition, map, remainingInstructions.rest())
    }

    fun part1(input: List<String>): Int {
        val parts = input.splitOn { it.isBlank() }
        val instructions = parseInstructions(parts.last().first())
        val grid = parseGrid(parts.first())
        val startingCoordinate = grid.coordinateOfFirst { it == '.' }!!
        val finalPosition = runInstructions(Position(startingCoordinate), grid, instructions)
        return (1000 * (finalPosition.coordinate.y + 1)) + (4 * (finalPosition.coordinate.x + 1)) + finalPosition.facing
    }

    fun part2(input: List<String>): Int {
        val parts = input.splitOn { it.isBlank() }
        val instructions = parseInstructions(parts.last().first())

        val grid = parseGrid(parts.first())
        val net = getCubeNet(grid)

        val jungleMap = JungleMap(grid, net)
        val startingCoordinate = grid.coordinateOfFirst { it == '.' }!!

        val finalPosition = runCubeInstructions(Position(startingCoordinate), jungleMap, instructions)
        return (1000 * (finalPosition.coordinate.y + 1)) + (4 * (finalPosition.coordinate.x + 1)) + finalPosition.facing
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
    println("Part 2: ${part2(testInput)}")
    println()

    val input = readInputLines("day22")
    println("::: Day22 :::")
    println("Part 1: ${part1(input)}")
    println("Part 2: ${part2(input)}")
}

/*
          111
          111
          111
    222333444
    222333444
    222333444
          555666
          555666

    0E -> 5W  0S -> 3S  0W -> 2S  0N -> 1S
    1E -> 2E  1S -> 4N  1W -> 5N  1N -> 0S
    2E -> 3E  2S -> 4E  2W -> 1W  2N -> 0E
    3E -> 5S  3S -> 4S  3W -> 2W  3N -> 0N
    4E -> 5E  4S -> 1N  4W -> 2N  4N -> 3N
    5E -> 0W  5S -> 1E  5W -> 4W  5N -> 3W

       111222
       111222
       111222
       333
       333
       333
    444555
    444555
    444555
    666
    666
    666

    0E -> 1E  0S -> 2S  0W -> 3E  0N -> 5E
    1E -> 4W  1S -> 2W  1W -> 0W  1N -> 5N
    2E -> 1N  2S -> 4S  2W -> 3S  2N -> 0N
    3E -> 4E  3S -> 5S  3W -> 0E  3N -> 2E
    4E -> 1W  4S -> 5W  4W -> 3W  4N -> 2N
    5E -> 4N  5S -> 1S  5W -> 0S  5N -> 3N
 */