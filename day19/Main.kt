import java.io.File

var workflowsByName = mapOf<String, Workflow>()

fun main() {
    val lines = File("input.txt").useLines { line -> line.toList() }

    workflowsByName = lines.takeWhile { it.isNotEmpty() }
        .map { line ->
            val (name, ruleset) = line.substring(0, line.length - 1).split("{")
            val rules = ruleset.split(",").map { rule ->
                if (rule.contains(":")) {
                    val (expression, outcome) = rule.split(":")
                    val (part, number) = expression.split("<", ">")
                    val operator = expression.first { it == '<' || it == '>' }
                    Rule(Expression(part.first(), number.toLong(), operator), outcome)
                } else {
                    Rule(null, rule)
                }
            }
            Workflow(name, rules)
        }.associateBy { it.name }

    println("part one: ${partOne(lines)}")
    println("part two: ${partTwo()}")
}

fun partOne(lines: List<String>) : Long {
    val parts = lines.takeLastWhile { it.isNotEmpty() }
        .map { line ->
            line.substring(1, line.length - 1)
                .split(",", "=")
                .filterIndexed { i, _ -> i % 2 == 1 }
        }.map { Part(it[0].toLong(), it[1].toLong(), it[2].toLong(), it[3].toLong()) }
    val approved = parts.mapNotNull { part ->
        if (process(workflowsByName["in"]!!, part) == State.A) part
        else null
    }
    return approved.fold(0L) {acc, part ->  acc + part.x + part.m + part.a + part.s }
}

fun partTwo() : Long {
    val approved = mutableListOf<PartRange>()
    val startRange = LongRange(1, 4000)
    val starterMap = mutableMapOf('x' to startRange, 'm' to startRange, 'a' to startRange, 's' to startRange)
    val workflowQueue = mutableListOf<Pair<PartRange, Workflow>>()
     workflowQueue.add(Pair(PartRange(starterMap), workflowsByName["in"]!!))
    while (workflowQueue.isNotEmpty()) {
        var (partRange, workflow) = workflowQueue.removeFirst()
        for (rule in workflow.rules) {
            if (rule.expression == null) {
                when (rule.outcome) {
                    "A" -> approved.add(partRange)
                    "R" -> {}
                    else -> workflowQueue.add(Pair(partRange, workflowsByName[rule.outcome]!!))
                }
                break
            }
            val range = partRange.range[rule.expression.part]!!
            val resultRanges: Pair<LongRange, LongRange>
            if (rule.expression.operator == '>') {
                resultRanges = Pair(LongRange(rule.expression.number + 1, range.last), LongRange(range.first, rule.expression.number))
            } else {
                resultRanges = Pair(LongRange(range.first, rule.expression.number - 1), LongRange(rule.expression.number, range.last))
            }
            val posPartRange = PartRange(partRange.range.toMutableMap().also { it[rule.expression.part] = resultRanges.first })
            val negPartRange = PartRange(partRange.range.toMutableMap().also { it[rule.expression.part] = resultRanges.second })
            when (rule.outcome) {
                "A" -> approved.add(posPartRange)
                "R" -> {}
                else -> workflowQueue.add(Pair(posPartRange, workflowsByName[rule.outcome]!!))
            }
            partRange = negPartRange
        }
    }
    return approved.fold(0L) {acc, partRange -> acc + partRange.range.values
        .fold(1L) { product, range -> product * range.count()} }
}

fun process(workflow: Workflow, part: Part) : State {
    var next: Workflow = workflow
    while (true) {
        for (rule in next.rules) {
            if (rule.test(part)) {
                when (rule.outcome) {
                    "A" -> return State.A
                    "R" -> return State.R
                    else -> next = workflowsByName[rule.outcome]!!
                }
                break
            }
        }
    }
}

data class PartRange(val range: MutableMap<Char, LongRange>)

data class Part(val x: Long, val m: Long, val a: Long, val s: Long)

data class Workflow(val name: String, val rules: List<Rule>)

data class Rule(val expression: Expression?, val outcome: String) {

    fun test(part: Part) : Boolean = expression?.test(part) ?: true
}

data class Expression(val part: Char, val number: Long, val operator: Char) {

    fun test(part: Part) : Boolean {
        val numberToCompare = when (this.part) {
            'x' -> part.x
            'm' -> part.m
            'a' -> part.a
            's' -> part.s
            else -> throw Exception("unable to determine subpart")
        }
        val comp = numberToCompare.compareTo(number)
        return (operator == '<' && comp < 0) || (operator == '>' && comp > 0)
    }
}

enum class State { A, R }