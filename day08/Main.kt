import java.io.File
import java.util.function.Predicate

fun main() {
    val lines = File("input.txt").useLines { line -> line.toList() }.toList()

    val directions = lines.take(1)[0]

    val nodes = lines.takeLast(lines.size - 2)
        .groupBy({ it.split(" =")[0] }, { it.split("= (")[1].split(", ").zipWithNext()[0] })
        .mapValues { Pair(it.value[0].first, it.value[0].second.replace(")", "")) }

    val partOneSum = countSteps("AAA", { it == "ZZZ"}, nodes, directions)
    println("part one: $partOneSum")

    val starterNodes = nodes.filterKeys { it.endsWith("A") }.keys.toList()
    val steps = starterNodes.map { countSteps(it, { it.endsWith("Z")}, nodes, directions) }
    val partTwoSum = lcm(steps.map { it.toLong() }.toList())
    println("part two: $partTwoSum")
}

fun countSteps(starterNode: String, target: Predicate<String>, nodes: Map<String, Pair<String, String>>, directions: String) : Int {
    var node = starterNode
    var counter = 0
    var zFound = false
    while (!zFound) {
        for (dir in directions) {
            node = if (dir == 'L') {
                nodes[node]!!.first
            } else {
                nodes[node]!!.second
            }
            counter++
            if (target.test(node)) {
                zFound = true
            }
        }
    }
    return counter
}

fun gcd(a: Long, b: Long) : Long {
    var a = a
    var b = b
    while (b > 0) {
        val temp = b
        b = a % b
        a = temp
    }
    return a
}

fun lcm(a: Long, b: Long) : Long {
    return a * (b / gcd(a, b))
}

fun lcm(input: List<Long>) : Long {
    var result = input[0]
    for (i in (1..<input.size)) {
        result = lcm(result, input[i])
    }
    return result
}