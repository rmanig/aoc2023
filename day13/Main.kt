import java.io.File

fun main() {
    val lines = File("input.txt").useLines { line -> line.toList() }
    val patterns: List<MutableList<String>> = lines.fold(mutableListOf(mutableListOf())) { acc, line ->
        if (line.isEmpty()) acc.add(mutableListOf())
        else acc.last().add(line)
        acc
    }
    val partOne = patterns.map { findMaxReflection(it, smudges = 0) }
    println("part one: ${partOne.sum()}")
    val partTwo = patterns.map { findMaxReflection(it, smudges = 1) }
    println("part two: ${partTwo.sum()}")
}

fun findMaxReflection(pattern: List<String>, smudges: Int = 0) : Int {
    val vertical = findReflection(pattern, smudges)
    val horizontal = findReflection(transpose(pattern), smudges)
    return if (horizontal.second > vertical.second) {
        horizontal.first * 100
    } else {
        vertical.first
    }
}

fun findReflection(pattern: List<String>, smudges: Int) : Pair<Int, Int> {
    var distanceToEdge = 0
    var maxReflection = 0
    val maxWidth = pattern[0].length
    for (width in (2..maxWidth step 2).reversed()) {
        val half = width / 2
        if (half < maxReflection) {
            return Pair(distanceToEdge, maxReflection)
        }
        for (startPosition in (0..maxWidth - width)) {
            val atEdge = startPosition + width == maxWidth || startPosition == 0
            if (!atEdge) continue

            val matches = mutableListOf<Pair<String, String>>()
            val misses = mutableListOf<Pair<String, String>>()
            for (line in pattern) {
                val first = line.substring(startPosition, startPosition + half)
                val second = line.substring(startPosition + half, startPosition + width).reversed()
                if (first != second) {
                    misses.add(Pair(first, second))
                } else {
                    matches.add(Pair(first, second))
                }
            }

            var restSmudges = 0
            if (smudges > 0 && misses.size == smudges) {
                restSmudges = misses.fold(smudges) { acc, miss ->
                    acc - miss.first.filterIndexed { i, char ->
                        char != miss.second[i]
                    }.count()
                }
            }

            if (misses.size - smudges == 0 && restSmudges == 0) {
                maxReflection = half
                distanceToEdge = startPosition + half
            }
        }
    }
    return Pair(distanceToEdge, maxReflection)
}

fun transpose(pattern: List<String>) : List<String> {
    val width = pattern[0].length
    return (0..<width).fold(mutableListOf<String>()) {result, i ->
        val newLine = pattern.fold(StringBuilder()) {acc, line ->  acc.append(line[i])}
        result.add(newLine.toString())
        result
    }.reversed()
}