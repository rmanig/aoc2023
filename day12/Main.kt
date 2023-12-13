import java.io.File
import kotlin.math.min

val cache = mutableMapOf<Pair<List<Char>, List<Int>>, Long>()

fun main() {
    val lines = File("input.txt").useLines { lines ->
        lines.flatMap { line -> line.split(" ")
            .zipWithNext { a, b -> Pair(a, b.split(",").map { it.toInt() }.toList()) }}
            .toList() }
    val partOne = lines.map { arrangements(it.first.toList(), it.second) }
    println("part one: ${partOne.sum()}")
    val partTwo = lines.map { line -> arrangements(
        (0..4).map { line.first.toMutableList() }.reduce { acc, chars ->
            acc.add('?')
            acc.addAll(chars)
            acc },
        (0..4).flatMap { line.second }.toList()) }
    println("part two: ${partTwo.sum()}")
}

fun arrangements(row: List<Char>, damagedGroups: List<Int>) : Long {
    if (damagedGroups.isEmpty()) {
        return if (row.any { it == '#' }) 0 else 1
    }
    var maxStart = row.size - damagedGroups.sum() - damagedGroups.size + 1
    if (row.any { it == '#' }) {
        maxStart = min(row.indexOf('#'), maxStart)
    }
    val damagedGroup = damagedGroups.first()
    val remainingDamagedGroups = damagedGroups.drop(1)
    var sum = 0L
    for (groupStart in (0..maxStart)) {
        val groupEnd = groupStart + damagedGroup
        val group = row.subList(groupStart, groupEnd)
        val groupPossible = group.all { char -> char == '#' || char == '?' }
        val endOfRecord = groupEnd >= row.size
        val groupSeparated = endOfRecord || row[groupEnd] == '?' || row[groupEnd] == '.'
        if (!groupPossible || !groupSeparated) continue
        val remainingGroup = row.drop(groupEnd + 1)
        val cacheKey = Pair(remainingGroup, remainingDamagedGroups)
        sum += cache[cacheKey] ?: arrangements(remainingGroup, remainingDamagedGroups).also { cache[cacheKey] = it }
    }
    return sum
}