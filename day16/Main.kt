import Direction.*
import java.io.File

fun main() {
    val field = File("input.txt").useLines { line -> line.toList() }
        .map { it.toList() }
    val partOne = beam(Pair(Point(0, -1), Right), field)
    println("part one: $partOne")
    val partTwo = beamFromEachDirection(field)
    println("part two: ${partTwo.max()}")
}

fun beamFromEachDirection(field: List<List<Char>>) : List<Int> {
    val directions = setOf(Left, Right).flatMap { direction ->
        field.indices.map { col -> Pair(Point(col, -1), direction) } }
        .toMutableList()
    directions.addAll(setOf(Up, Down).flatMap { direction ->
        (0..<field[0].size).map { row -> Pair(Point(-1, row), direction) } })
    return directions.map { beam(it, field) }
}

fun beam(start: Pair<Point, Direction>, field: List<List<Char>>) : Int {
    val seen = mutableSetOf<Pair<Point, Direction>>()
    val energized = mutableSetOf<Point>()
    val queue = mutableListOf<Pair<Point, Direction>>().also { it.add(start) }
    while (queue.isNotEmpty()) {
        val (point, direction) = queue.removeAt(0)
        val next = when (direction) {
            Down -> Point(point.x + 1, point.y)
            Up ->  Point(point.x - 1, point.y)
            Right ->  Point(point.x, point.y + 1)
            Left ->  Point(point.x, point.y - 1)
        }
        if (next.x < 0 || next.x >= field.size || next.y < 0 || next.y >= field[0].size ) continue
        energized.add(next)
        val nextDirections = mutableSetOf<Direction>()
        when (field[next.x][next.y]) {
            '.' -> nextDirections.add(direction)
            '|' -> if (setOf(Left, Right).contains(direction)) nextDirections.addAll(setOf(Up, Down)) else nextDirections.add(direction)
            '-' -> if (setOf(Up, Down).contains(direction)) nextDirections.addAll(setOf(Left, Right)) else nextDirections.add(direction)
            '/' -> when (direction) {
                Left -> nextDirections.add(Down)
                Right -> nextDirections.add(Up)
                Up -> nextDirections.add(Right)
                Down -> nextDirections.add(Left) }
            '\\' -> when (direction) {
                Left -> nextDirections.add(Up)
                Right -> nextDirections.add(Down)
                Up -> nextDirections.add(Left)
                Down -> nextDirections.add(Right) }
        }
        for (nextDirection in nextDirections) {
            val value = Pair(next, nextDirection)
            if (seen.contains(value)) continue else seen.add(value)
            queue.add(value)
        }
    }
    return energized.count()
}

enum class Direction { Left, Right, Up, Down }

data class Point(val x: Int, val y: Int)