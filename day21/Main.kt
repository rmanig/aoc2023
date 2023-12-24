import Direction.*
import java.io.File
import kotlin.math.abs

fun main() {
    val field = File("input.txt").useLines { line -> line.toList() }
        .map { line -> line.toList() }

    val start = field.foldIndexed(Point(-1, -1)) { row, acc, line ->
        val col = line.indexOf('S')
        if ( col != -1) Point(row,col) else acc
    }

    println("part one: ${step(field, start, steps = 64)}")
    println("part two: ${partTwo(field, start)}")
}

fun partTwo(field: List<List<Char>>, start: Point) : Long {
    val x0 = step(field, start, steps = 65, expand = true)
    val x1 = step(field, start, steps = 65 + 131, expand = true)
    val x2 = step(field, start, steps = 65 + 2 * 131, expand = true)
    // Lagrange's interpolation for f(x) = ax^2 + bx + c
    val a = x0 / 2 - x1 + x2 / 2
    val b = -3 * x0 / 2 + 2 * x1 - x2 / 2
    val c = x0

    val x = (26501365L - 65) / 131
    return a * x * x + b * x + c
}

fun step(field: List<List<Char>>, start: Point, steps: Int, expand: Boolean = false) : Int {
    val positions = mutableSetOf<Point>()
    val queue = mutableSetOf<Point>().also { it.add(start) }
    for (step in (1..steps)) {
        while (queue.isNotEmpty()) {
            val next = queue.first().also { queue.remove(it) }
            val adjacent = adjacent(field, next, expand)
            positions.addAll(adjacent)
        }
        queue.addAll(positions)
        positions.removeAll { true }
    }
    return queue.size
}

fun adjacent(field: List<List<Char>>, point: Point, expand: Boolean = false) : List<Point> {
    val edges = mutableListOf<Point>()
    val garden = setOf('.', 'S')
    for (dir in Direction.entries) {
        val next = when (dir) {
            North -> Point(point.x - 1, point.y)
            South -> Point(point.x + 1, point.y)
            East -> Point(point.x, point.y + 1)
            West -> Point(point.x, point.y - 1)
        }
        if (expand && garden.contains(field[mod(next.x, field.size)][mod(next.y, field[0].size)])) {
            edges.add(next)

        } else if (next.x in 0..<field.size
            && next.y in 0..<field[0].size
            && garden.contains(field[next.x][next.y])) {
            edges.add(next)
        }
    }
    return edges
}

fun mod(coord: Int, fieldSize: Int) : Int {
    return when {
        coord in 0..<fieldSize -> coord
        coord < 0 && abs(coord) % fieldSize > 0 -> fieldSize + coord % fieldSize
        else -> coord % fieldSize
    }
}

enum class Direction { North, South, East, West }

data class Point(val x: Int, val y: Int)