import Direction.*
import java.io.File
import kotlin.math.abs

fun main() {
    val lines = File("input.txt").useLines { line -> line.toList() }
        .map { val row = it.split(" ")
            Triple(Direction.valueOf(row[0]), row[1].toLong(), row[2].substring(2, row[2].length - 1))
        }

   val partOne = capacity(lines.map { Pair(it.first, it.second) })
    println("part one: $partOne")

    val partTwo = capacity(lines.map {
        val distance = it.third.substring(0, it.third.length - 1).toLong(16)
        val direction = translate(it.third.substring(it.third.length - 1)[0])!!
        Pair(direction, distance)
    })
    println("part two: $partTwo")
}

enum class Direction { U, D, L, R }

data class Point(val x: Long, val y: Long)

fun capacity(instructions: List<Pair<Direction, Long>>) : Long {
    val shoe = shoelace(points(instructions))
    val perimeter = points(instructions, accumulating = false)
        .fold(0L) { acc, point ->  acc + abs(point.x) + abs(point.y)}
    return pick(shoe, perimeter)
}

fun pick(area: Long, perimeter: Long) : Long {
    return area + perimeter / 2 + 1
}

fun shoelace(points: List<Point>) : Long {
    var result = 0L
    val iter = points.toMutableList()
    for ((a,b) in iter.windowed(2, 1)) {
        result += (a.x * b.y) - (a.y * b.x)
    }
    return (result / 2)
}

fun points(lines: List<Pair<Direction, Long>>, accumulating: Boolean = true) : List<Point> {
    val result = mutableListOf<Point>()
    var p = Point(0,0)
    for (line in lines) {
        val step = line.second
        val next = when (line.first) {
            L -> Point(p.x - step, p.y)
            R -> Point(p.x + step, p.y)
            D -> Point(p.x, p.y + step)
            U -> Point(p.x, p.y - step)
        }
        result.add(next)
        if (accumulating) {
            p = next
        }
    }
    return result
}

fun translate(direction: Char) : Direction? {
    return when (direction)  {
        '0' -> R
        '1' -> D
        '2' -> L
        '3' -> U
        else -> null
    }
}