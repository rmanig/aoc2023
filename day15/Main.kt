import java.io.File

fun main() {
    val instructions = File("input.txt").useLines { line -> line.toList() }
        .flatMap { it.split(",") }
    val partOne = instructions.map { hash(it) }
    println("part one: ${partOne.sum()}")
    val partTwo = initSeq(instructions)
    println("part two: $partTwo")
}

fun initSeq(instructions: List<String>) : Int {
    val boxes = mutableMapOf<Int, MutableList<Pair<String, Int>>>()
    for (instr in instructions) {
        val (label, lens) = instr.split("-", "=")
        val box = hash(label)
        val labels = boxes.getOrDefault(box, listOf()).toMutableList()
        if (lens.isEmpty()) {
            boxes[box] = labels.filter { it.first != label }.toMutableList()
        } else {
            val entry = labels.find { it.first == label }
            if (entry != null) {
                boxes[box]?.set(labels.indexOf(entry), Pair(label, lens.toInt()))
            } else {
                boxes[box] = labels.toMutableList().also { it.add(Pair(label, lens.toInt())) }
            }
        }
    }
    return boxes.entries.map { (box, labels) ->
        labels.foldIndexed(0) { i, acc, label ->
            acc + (box + 1) * (i + 1) * label.second
        }
    }.sum()
}

fun hash(string: String) : Int {
    return string.fold(0) {acc, char ->
        (acc + char.code) * 17 % 256
    }
}