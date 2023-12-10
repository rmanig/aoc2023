import java.io.File
import java.util.stream.Stream

fun main() {
    val lines = File("input.txt").useLines { line -> line.toList() }.toMutableList()
    val loopDistances = walkLoop(lines)
    println("part one: ${loopDistances.values.max()}")
    val enclosed = enclosedByLoop(loopDistances.keys, lines)
    println("part two: ${enclosed.count()}")
}

fun enclosedByLoop(loop: Set<Pair<Int, Int>>, board: List<String>) : Set<Pair<Int, Int>> {
    val enclosed = mutableSetOf<Pair<Int, Int>>()
    var inside = false
    var last = '?'
    for ((i, line) in board.withIndex()) {
        for ((j, _) in line.withIndex()) {
            val pos = Pair(i, j)
            if (loop.contains(pos)) {
                var loopChar = board[pos.first][pos.second]
                if (loopChar == 'S') loopChar = replaceStart(pos, board)
                when (loopChar) {
                    '|' -> inside = !inside
                    '7' -> if (last == 'L') inside = !inside
                    'J' -> if (last == 'F') inside = !inside
                    'L','F' -> last = loopChar
                }
            } else if (inside) {
                enclosed.add(pos)
            }
        }
    }
    return enclosed
}

fun replaceStart(start: Pair<Int, Int>, board: List<String>) : Char {
    val around = positionsAround(start, board.size, board[0].length)
    val (first, second) = around.filter { walkable(start, it, board) }.toList()
    val directionsToChar = mapOf(Pair(setOf(Direction.North, Direction.South), '|'),
        Pair(setOf(Direction.North, Direction.East), 'L'),
        Pair(setOf(Direction.North, Direction.West), 'J'),
        Pair(setOf(Direction.South, Direction.West), '7'),
        Pair(setOf(Direction.South, Direction.East), 'F'),
        Pair(setOf(Direction.East, Direction.West), '-'))
    return directionsToChar[setOf(direction(start, first), direction(start, second))]!!
}

fun walkLoop(board: List<String>) :  Map<Pair<Int, Int>, Int> {
    var pos = Pair(-1,-1)
    for ((i,_) in board.withIndex().toMutableList()) {
        val startPos = board[i].indexOf('S')
        pos = if (startPos != -1) Pair(i, startPos) else pos
    }
    val visited = mutableMapOf<Pair<Int, Int>, Int>().also { it[pos] = 0 }
    val queue = mutableMapOf<Pair<Int, Int>, Int>()
    while (true) {
        val around = positionsAround(pos, board.size, board[0].length)
        val candidates = around.filter { !visited.containsKey(it) && walkable(pos, it, board) }.toList()
        candidates.forEach { queue[it] = visited[pos]!! + 1 }
        if (queue.isEmpty()) return visited
        val next = queue.toList().first()
        visited[next.first] = next.second
        pos = next.first
        queue.remove(next.first)
    }
}

enum class Direction { North, East, South, West }

fun walkable(from: Pair<Int, Int>, to: Pair<Int, Int>, board: List<String>) : Boolean {
    val fromChar = board[from.first][from.second]
    val toChar = board[to.first][to.second]
    if (toChar == '.') {
        return false
    }
    return when (direction(from, to)) {
        Direction.North -> (toChar == '|' || toChar == 'F' || toChar == '7') && (fromChar == 'S' || fromChar == '|' || fromChar == 'J' || fromChar == 'L')
        Direction.South -> (toChar == '|' || toChar == 'L' || toChar == 'J') && (fromChar == 'S' || fromChar == '|' || fromChar == 'F' || fromChar == '7')
        Direction.West -> (toChar == '-' || toChar == 'L' || toChar == 'F') && (fromChar == 'S' || fromChar == '-' || fromChar == '7' || fromChar == 'J')
        Direction.East -> (toChar == '-' || toChar == '7' || toChar == 'J') && (fromChar == 'S' || fromChar == '-' || fromChar == 'F' || fromChar == 'L')
    }
}

fun direction(from: Pair<Int, Int>, to: Pair<Int, Int>) : Direction {
    return if (to.first > from.first) Direction.South
    else if (to.first < from.first) Direction.North
    else if (to.second < from.second) Direction.West
    else Direction.East
}

fun positionsAround(pos: Pair<Int, Int>, maxX: Int, maxY: Int) : List<Pair<Int, Int>> {
    return Stream.of(
        Pair(pos.first + 1, pos.second),
        Pair(pos.first - 1, pos.second),
        Pair(pos.first, pos.second + 1),
        Pair(pos.first, pos.second - 1))
        .filter{ it.first in 0..<maxX }
        .filter{ it.second in 0..<maxY }
        .toList()
}