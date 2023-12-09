import java.io.File

fun main() {
    val lines = File("input.txt").useLines { line -> line.toList() }
        .map { line -> line.split(" ").map { it.toInt() } }.toList()
    val histories = lines.map { sequence -> history(sequence) }
    val forecasts = histories.map { forecast(it) }
    println("part one: ${forecasts.sum()}")
    val backcasts = histories.map { forecast(it, backwards = true) }
    println("part two: ${backcasts.sum()}")
}

fun history(sequence: List<Int>) : List<List<Int>> {
    val history = mutableListOf<List<Int>>().also { it.addLast(sequence) }
    var i = 0
    while (true) {
        val seq = history[i++]
        if (seq.all { it == 0 }) {
            return history
        }
        val newSeq = mutableListOf<Int>()
        for ((a, b) in seq.windowed(2, 1)) {
            newSeq.add(b - a)
        }
        history.add(newSeq)
    }
}

fun forecast(history: List<List<Int>>, backwards: Boolean = false) : Int {
    history[history.size - 1].addLast(0)
    for (i in history.size - 2 downTo 0) {
        val seq = history[i]
        val downSeq = history[i + 1]
        if (backwards) seq.addFirst(seq[0] - downSeq[0])
        else seq.addLast(downSeq[downSeq.size - 1] + seq[seq.size - 1])
    }
    return if (backwards) history[0][0] else history[0][history[0].size - 1]
}