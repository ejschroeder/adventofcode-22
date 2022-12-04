package lol.schroeder.aoc22.util

import java.math.BigInteger
import java.security.MessageDigest

fun Int.isEven() = this % 2 == 0
fun Int.isOdd() = this % 2 == 1

fun String.isUpperCase(): Boolean = this.all { it.isUpperCase() }
fun String.md5(): String = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray())).toString(16)
fun String.extractInts() = Regex("([+\\-])?\\d+")
    .findAll(this)
    .mapNotNull { it.value.toIntOrNull() }
    .toList()

fun <T> String.mapGroups(pattern: String, transform: (MatchResult.Destructured) -> T) =
    mapGroups(Regex(pattern), transform)

fun <T> String.mapGroups(regex: Regex, transform: (MatchResult.Destructured) -> T): T {
    val destructuredResult = regex.matchEntire(this)?.destructured
        ?: throw IllegalStateException("No match found for given regex '${regex}'")
    return transform(destructuredResult)
}

fun <T> Iterable<T>.rest() = drop(1)

fun <K, V, T> Map<K, V>.merge(other: Map<K, V>, valueMapper: (List<V>) -> T): Map<K, T> {
    return (this.asSequence() + other.asSequence())
        .groupBy({ it.key }, { it.value })
        .mapValues { (_, values) -> valueMapper(values) }
}

fun <T> Iterable<T>.elementPairs(): Sequence<Pair<T, T>> = sequence {
    val list = toList()
    for(i in 0 until list.size-1)
        for(j in i+1 until list.size)
            yield(list[i] to list[j])
}

fun <T> Collection<T>.findOrThrow(predicate: (T) -> Boolean): T = find(predicate) ?: throw IllegalStateException("Item was not found in the list")

fun <T> List<T>.splitOn(predicate: (T) -> Boolean): Sequence<List<T>> {
    return sequence {
        var group = mutableListOf<T>()
        for(element in this@splitOn) {
            if (predicate(element)) {
                yield(group)
                group = mutableListOf()
            } else {
                group += element
            }
        }
        yield(group)
    }
}