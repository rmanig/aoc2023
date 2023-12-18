import Direction.*
import java.io.File
import java.util.Comparator
import java.util.PriorityQueue
import kotlin.math.min

fun main() {
    val field = File("input.txt").useLines { line -> line.toList() }.map { it.toList() }
    val start = Point(0, 0)
    val target = Point(field.size - 1, field[field.size - 1].size - 1)

    val partOne = dijkstra(field, start, target)
    println("part one: $partOne")

    val partTwo = dijkstra(field, start, target, crucible = true)
    println("part two: $partTwo")
}

enum class Direction { North, South, East, West }

data class Point(val x: Int, val y: Int)

data class Edge(val cost: Int, val node: Node)

data class Node(val point: Point, val direction: Direction)

fun dijkstra(field: List<List<Char>>, start: Point, target: Point, crucible: Boolean = false) : Int {
    val distance = mutableMapOf<Point, Int>().also { it[start] = 0 }
    val visited = mutableSetOf<Node>()
    val queue = PriorityQueue<Edge>(Comparator.comparingInt{ it.cost }).also {
        it.add(Edge(0, Node(start, North)))
        it.add(Edge(0, Node(start, South)))
        it.add(Edge(0, Node(start, West)))
        it.add(Edge(0,  Node(start, East)))
    }
    while (queue.isNotEmpty()) {
        val (cost, node) = queue.poll()
        if (visited.contains(node)) {
            continue
        } else {
            visited.add(node)
        }
        for (adj in adjacent(field, node, crucible)) {
            distance[adj.node.point] = min(adj.cost + cost, distance[adj.node.point] ?: Int.MAX_VALUE)
            queue.add(Edge(adj.cost + cost, Node(adj.node.point, adj.node.direction)))
        }
    }
    return distance[target] ?: -1
}

fun adjacent(field: List<List<Char>>, node: Node, crucible: Boolean = false) : List<Edge> {
    val edges = mutableListOf<Edge>()
    val maxSteps = if (crucible) 10 else 3
    val minSteps = if (crucible) 4 else 1
    val p = node.point
    val nextDirections = when (node.direction) {
        North, South -> setOf(West, East)
        West, East -> setOf(North, South)
    }
    for (dir in nextDirections) {
        for (steps in (1..maxSteps)) {
            val move = mutableListOf<Point>()
            var cost = 0
            for (step in (1..steps)) {
                val nextPoint = when (dir) {
                    North -> Point(p.x - step, p.y)
                    South -> Point(p.x + step, p.y)
                    East -> Point(p.x, p.y + step)
                    West -> Point(p.x, p.y - step)
                }
                if (nextPoint.x in 0..<field.size && nextPoint.y in 0..<field[0].size) {
                    move.add(nextPoint)
                    cost += field[nextPoint.x][nextPoint.y].digitToInt()
                } else {
                    break
                }
            }
            if (move.size >= minSteps) {
                edges.add(Edge(cost, Node(move.last(), dir)))
            }
        }
    }
    return edges
}