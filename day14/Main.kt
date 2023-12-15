import java.io.File

fun main() {
    val field = File("input.txt").useLines { line -> line.toList() }
        .map { it.toList() }
    val partOne = sum(tilt(field))
    println("part one: $partOne")
    val partTwo = sum(cycle(field,1000000000))
    println("part two: $partTwo")
}

fun sum(field: List<List<Char>>) : Int {
    return field.foldIndexed(0) { row, acc, chars ->
        acc + (field.size - row) * chars.count { it == 'O' }
    }
}

fun cycle(field: List<List<Char>>, cycles: Int) : List<List<Char>> {
    var result = field
    val seenCycles = mutableListOf<List<List<Char>>>()
    for (cycle in (0..<cycles)) {
        for (i in 0..<4) {
            result = turnClockwise(tilt(result))
        }
        val seenCycle = seenCycles.lastOrNull { it == result }
        seenCycle?.also {
            val index = seenCycles.indexOf(it)
            val cycleLength = seenCycles.size - index
            val finalCycle = index + (cycles - index) % cycleLength - 1
            return seenCycles[finalCycle]
        }
        seenCycles.add(result)
    }
    return result
}

fun tilt(field: List<List<Char>>) : List<List<Char>> {
    val result = field.map { it.toMutableList() }.toMutableList()
    for (column in (0..<result[0].size)) {
        for (row in (1..<field.size)) {
            if (result[row][column] == 'O') {
                for (targetRow in (0..<row)) {
                    val betweenRows = targetRow + 1..<row
                    if (result[targetRow][column] == '.'
                        && betweenRows.none { betweenRow -> result[betweenRow][column] == '#' }) {
                        result[targetRow][column] = 'O'
                        result[row][column] = '.'
                        break
                    }
                }
            }
        }
    }
    return result
}

fun turnClockwise(field: List<List<Char>>) : List<List<Char>> {
    val width = field[0].size
    return (0..<width).fold(mutableListOf<MutableList<Char>>()) {result, i ->
        val newLine = field.fold(StringBuilder()) {acc, line ->  acc.append(line[i])}
        result.add(newLine.reversed().toMutableList())
        result
    }
}