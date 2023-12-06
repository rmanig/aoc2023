import java.io.File

fun main() {
    val lines = File("input.txt").useLines { lines ->
        lines.filter { it.isNotEmpty() }.toList() }
    println("part one: ${partOne(lines)}")
    println("part two: ${partTwo(lines)}")
}

fun partOne(lines: List<String>) : Long {
    val seeds = lines.first().split(": ")[1].split(" ").map { it.toLong() }
    return seeds.map { lookup(it, maps(lines)) }.min()
}

// https://www.reddit.com/media?url=https%3A%2F%2Fi.redd.it%2F8v91f39b0f4c1.png
fun partTwo(lines: List<String>) : Long {
    val maps = maps(lines)
    var min = Long.MAX_VALUE
    lines.first().split(": ")[1].split(" ").map { it.toLong() }
        .zipWithNext()
        .filterIndexed { index, _ ->  index % 2 == 0}
        .forEach { (seedStart, range) ->
            (seedStart..<(seedStart+range)).forEach {seed ->
                val result = lookup(seed, maps)
                if (result < min) {
                    min = result
                }
            }
        }
    return min
}

fun maps(lines: List<String>) : List<List<Triple<Long, Long, Long>>> {
    return lines.takeLast(lines.size - 1)
        .fold(mutableListOf<MutableList<Triple<Long, Long, Long>>>()) { acc, item ->
            if (item.contains("map:")) {
                acc.addLast(mutableListOf())
            } else {
                val (target, source, range) = item.split(" ").map { it.toLong() }
                acc[acc.size - 1].addLast(Triple(target, source, range))
            }
            acc
        }
}

fun lookup(seed: Long, maps: List<List<Triple<Long, Long, Long>>>) : Long {
    var s = seed
    for (map in maps) {
        for ((target, source, range) in map) {
            if (s in source..<source + range) {
                s += (target - source)
                break
            }
        }
    }
    return s
}