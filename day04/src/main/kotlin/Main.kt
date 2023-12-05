import java.io.File
import kotlin.math.pow

fun main() {
    val lines: List<String> = File("src/main/resources/input.txt").useLines { lines -> lines.toList() }
    val games = lines.map { line ->
        line.split(": ")[1]
            .split(" | ")
    }.flatMap { sets ->
            sets.map { set -> set.split(" ") }
                .map { numbers -> numbers.filter { number -> number.isNotEmpty() } }
                .zipWithNext()
        }.toList()
    println("part one: ${partOne(games)}")
    println("part two: ${partTwo(games)}")
}

fun partOne(games: List<Pair<List<String>, List<String>>>): Int {
    val winning = games.map { game -> game.second.filter { number -> game.first.contains(number) } }.toList()
    return winning.filter { game -> game.isNotEmpty() }
        .sumOf { game -> 2.0.pow(game.count() - 1) }.toInt()
}

fun partTwo(games: List<Pair<List<String>, List<String>>>): Int {
    val cards = games.map { 0 }.toMutableList()
    for ((i, game) in games.withIndex()) {
        val matching = game.second.filter { number -> game.first.contains(number) }
        (1..1 + cards[i]).forEach { _ ->
            (1..matching.count()).forEach { match ->
                cards[i + match]++
            }
        }
    }
    return games.size + cards.sum()
}