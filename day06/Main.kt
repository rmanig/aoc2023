import java.io.File

fun main() {
    val lines = File("input.txt").useLines { lines ->
        lines.map { line ->
            line.split(":")[1]
                .split(" ")
                .filter { it.isNotEmpty() }
        }.toList()
    }

    val partOneSum = partOne(lines)
    println("part one: $partOneSum")

    val partTwoSum = partTwo(lines)
    println("part two: $partTwoSum")
}

fun partOne(lines: List<List<String>>) : Long {
    val longs = lines.map { line -> line.map { it.toLong() } }
    val races = longs[0].zip(longs[1])
    return races.map { race -> calcRaceWays(race) }.reduce { acc, l -> acc * l }
}

fun partTwo(lines: List<List<String>>) : Long {
    return lines.map { line -> line.joinToString("") { it }.toLong()}
        .zipWithNext()[0]
        .run { calcRaceWays(this) }
}

fun calcRaceWays(race: Pair<Long, Long>) : Long {
    val (duration, distance) = race
    var numberOfWays = 0L
    for (startup in (1..duration)) {
        val result = startup * (duration - startup)
        if (result > distance) {
            numberOfWays++
        }
    }
    return numberOfWays
}