import java.io.File
import kotlin.math.abs

// https://i.redd.it/y3d99mti5m5c1.png
fun main() {
    val galaxyMap = File("input.txt").useLines { line -> line.map { it.toCharArray() }.toList() }.toMutableList()
    val emptyRows = galaxyMap.mapIndexedNotNull { i, row -> if (row.all { it == '.' }) i else null }.toSet()
    val emptyColumns = galaxyMap.mapIndexedNotNull { col, _ -> if (galaxyMap.all { row -> row[col] == '.' }) col else null }.toSet()
    val galaxies = findGalaxies(galaxyMap)

    val partOne = pairs(galaxies.map { expand(it, emptyRows, emptyColumns, 2) })
        .map { set -> distance(set.first(), set.last()) }
        .sum()
    println("part one: $partOne")

    val partTwo = pairs(galaxies.map { expand(it, emptyRows, emptyColumns, 1000000) })
        .map { set -> distance(set.first(), set.last()) }
        .sum()
    println("part two: $partTwo")
}

fun pairs(galaxies: List<Pair<Int, Int>>) : Set<Set<Pair<Int, Int>>> {
    return galaxies.flatMap { one -> galaxies
        .filter { another -> another != one }
        .map { another -> setOf(one, another) } }
        .toSet()
}

fun findGalaxies(galaxyMap: List<CharArray>): List<Pair<Int, Int>> {
    val galaxies = mutableListOf<Pair<Int, Int>>()
    galaxyMap.forEachIndexed { i, line ->
        line.forEachIndexed { j, char ->
            if (char == '#') galaxies.add(Pair(i, j)) } }
    return galaxies
}

fun expand(galaxy: Pair<Int, Int>, emptyRows: Set<Int>, emptyColumns: Set<Int>, multiplier: Int = 2) : Pair<Int, Int> {
    var expandedX = 0
    for (empty in emptyRows) {
        if (galaxy.first > empty) expandedX += (multiplier - 1)
    }
    var expandedY = 0
    for (empty in emptyColumns) {
        if (galaxy.second > empty) expandedY += (multiplier - 1)
    }
    return Pair(galaxy.first + expandedX, galaxy.second + expandedY)
}

fun distance(from: Pair<Int, Int>, to: Pair<Int, Int>) : Long {
    return abs(from.first - to.first).toLong() + abs(from.second - to.second)
}