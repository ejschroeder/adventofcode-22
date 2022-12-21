package lol.schroeder.aoc22

import lol.schroeder.aoc22.util.readInputLines

fun main() {
    fun mix(numbers: List<Long>, positions: IntArray) {
        for (i in positions.indices) {
            val currentIndex = positions[i]
            val num = numbers[i]

            val candidate = currentIndex + num
            val nextIndex = (
                    if (candidate > numbers.lastIndex)
                            (candidate).rem(numbers.lastIndex)
                    else if (candidate < 0)
                        numbers.lastIndex + candidate.rem(numbers.lastIndex)
                    else
                        candidate
            ).toInt()

            if (nextIndex > currentIndex) {
                val range = (currentIndex + 1)..nextIndex
                for (pos in positions.indices) {
                    if (positions[pos] in range) {
                        positions[pos] -= 1
                    }
                }
                positions[i] = nextIndex
            }

            if (nextIndex < currentIndex) {
                val range = (nextIndex) until currentIndex
                for (pos in positions.indices) {
                    if (positions[pos] in range) {
                        positions[pos] += 1
                    }
                }
                positions[i] = nextIndex
            }
        }
    }

    fun calculateCoordinateSum(numbers: List<Long>, positions: IntArray): Long {
        val zeroIdx = positions[numbers.indexOf(0)]

        val coordinates = listOf(
            numbers[positions.indexOf((zeroIdx + 1000) % numbers.size)],
            numbers[positions.indexOf((zeroIdx + 2000) % numbers.size)],
            numbers[positions.indexOf((zeroIdx + 3000) % numbers.size)]
        )
        return coordinates.sum()
    }

    fun part1(input: List<String>): Long {
        val nums = input.map { it.toLong() }
        val positions = IntArray(nums.size) { it }

        mix(nums, positions)

        return calculateCoordinateSum(nums, positions)
    }

    fun part2(input: List<String>): Long {
        val nums = input.map { it.toLong() * 811589153L }
        val positions = IntArray(nums.size) { it }

        repeat(10) {
            mix(nums, positions)
        }

        return calculateCoordinateSum(nums, positions)
    }

    val input = readInputLines("day20")
    println("::: Day20 :::")
    println("Part 1: ${part1(input)}")
    println("Part 2: ${part2(input)}")
}