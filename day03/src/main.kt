import java.io.File

fun main() {
    val lines: List<String> = File("src/input.txt").useLines { lines -> lines.toList() }
    val numbers: MutableMap<Pair<Int, Int>, Int> = mutableMapOf()
    val adjacents: List<Int> = mutableListOf()
    val gears: MutableMap<Pair<Int, Int>, MutableSet<Pair<Int, Int>>> = mutableMapOf()
    for ((i,line) in lines.withIndex()) {
        var number = StringBuilder()
        var numberPos: Pair<Int, Int> = Pair(0,0)
        var adjacent = false
        for ((j, symbol) in line.withIndex()) {
            if (symbol.isDigit()) {
                if (number.isEmpty()) {
                    numberPos = Pair(i,j)
                }
                number.append(symbol)
                val adjacentSymbols:Map<Pair<Int, Int>, Char> = adjacentSymbols(Pair(i,j), lines)
                if (!adjacent) {
                    adjacent = adjacentSymbols.isNotEmpty()
                }
                updateGears(gears, numberPos, adjacentSymbols)

            } else if (number.isNotEmpty()) {
                if (adjacent) {
                    adjacents.addLast(number.toString().toInt())
                }
                numbers[numberPos] = number.toString().toInt()
                number = StringBuilder()
                adjacent = false
            }
        }
        if (number.isNotEmpty()) {
            if (adjacent) {
                adjacents.addLast(number.toString().toInt())
            }
            numbers[numberPos] = number.toString().toInt()
        }
    }

    println("part one: ${adjacents.sum()}")
    println("part two: ${gearSum(numbers, gears)}")
}

fun adjacentSymbols(pos: Pair<Int, Int>, lines: List<String>) : Map<Pair<Int, Int>, Char> {
    val symbols:MutableMap<Pair<Int, Int>, Char> = mutableMapOf()
    for (x in pos.first -1 .. pos.first + 1) {
        for (y in pos.second -1 .. pos.second + 1) {
            val candidate = lines.getOrNull(x)?.getOrNull(y) ?: '.'
            if (candidate.isDigit().not().and(candidate != '.')) {
                symbols.put(Pair(x,y), candidate)
            }
        }
    }
    return symbols
}

fun updateGears(gears: MutableMap<Pair<Int, Int>, MutableSet<Pair<Int, Int>>>,
                number: Pair<Int, Int>,
                adjacentSymbols: Map<Pair<Int, Int>, Char>) {
    adjacentSymbols.forEach { (pos, char) ->
        if (char == '*') {
            val gearNumbers = gears.getOrDefault(pos, mutableSetOf())
            gearNumbers.add(number)
            gears[pos] = gearNumbers
        }
    }
}

fun gearSum(numbers: MutableMap<Pair<Int, Int>, Int>, gears: MutableMap<Pair<Int, Int>, MutableSet<Pair<Int, Int>>>) : Int {
    var gearSum = 0
    for (gear in gears) {
        if (gear.value.size == 2) {
            gearSum += numbers[gear.value.first()]?.times(numbers[gear.value.last()] ?: 0) ?: 0
        }
    }
    return gearSum
}