package lol.schroeder.aoc22.util

fun <T> Collection<T>.findOrThrow(predicate: (T) -> Boolean): T = find(predicate) ?: throw IllegalStateException("Item was not found in the list")
fun String.isUpperCase(): Boolean = this.all { it.isUpperCase() }

fun <T> Iterable<T>.elementPairs(): Sequence<Pair<T, T>> = sequence {
    val list = toList()
    for(i in 0 until list.size-1)
        for(j in i+1 until list.size)
            yield(list[i] to list[j])
}

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