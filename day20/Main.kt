import ModuleType.*
import java.io.File

fun main() {
    val lines = File("input.txt").useLines { line -> line.toList() }

    val types = mutableMapOf<String, ModuleType>()
    val modules: Map<String, List<String>> = lines.flatMap { line ->
        line.split(" -> ")
            .zipWithNext { a, b ->
                var key = a.drop(1)
                when (a.first()) {
                    '%' -> types[key] = FlipFlop
                    '&' -> types[key] = Conjunction
                    'b' ->  {
                        key = a
                        types[key] = Broadcast
                    }
                }
                Pair(key, b.split(", "))
            }
    }.associate { it }

    val flipStates = modules.keys.associateWith { false }.toMutableMap()
    val moduleInputs = modules.flatMap { entry ->
        val y = entry.value.map { value -> Pair(value, entry.key) }
        y
    }.fold(mutableMapOf<String, List<String>>()) { acc, pair ->
        acc.merge(pair.first, listOf(pair.second)) { a, b -> a.toMutableList().also { it.add(pair.second) } }
        acc
    }
    val conjunctionStates = moduleInputs.entries.fold(mutableMapOf<String, MutableMap<String, Boolean>>()) { acc, entry ->
        acc[entry.key] = entry.value.associateWith { false }.toMutableMap()
        acc
    }

    var partOne = -1
    var buttonPressed = 1
    var lowPulsesSend = 1
    var highPulsesSend = 0

    val cyclesStarted = mutableMapOf<String, Boolean>()
    val cycleCounters = mutableMapOf<String, Long>()

    val queue = mutableListOf<Signal>()
    queue.add(Signal(false, "button", "broadcaster"))
    while (queue.isNotEmpty()) {
        val signal = queue.removeFirst()
        val module = signal.receiver
        val pulse = signal.pulse
        val receivers = modules[signal.receiver]
        when (types[module]) {
            Broadcast -> {
                receivers?.forEach {
                    queue.add(Signal(pulse, module, it))
                    if (pulse) highPulsesSend++ else lowPulsesSend++
                }
            }
            FlipFlop -> {
                if (!pulse) {
                    val flipState = !flipStates[module]!!
                    flipStates[module] = flipState
                    receivers?.forEach {
                        queue.add(Signal(flipState, module, it))
                        if (flipState) highPulsesSend++ else lowPulsesSend++
                    }
                }
            }
            Conjunction -> {
                conjunctionStates[module]!![signal.sender] = pulse
                // partTwo
                if (moduleInputs["rx"]!!.contains(module) && pulse) {
                    if (!cyclesStarted.getOrDefault(signal.sender, false) && cycleCounters.getOrDefault(signal.sender, 0L) == 0L) {
                        cyclesStarted[signal.sender] = true
                    } else if (cycleCounters.getOrDefault(signal.sender, 0) > 0) {
                        cyclesStarted[signal.sender] = false
                        if (cyclesStarted.values.all { !it } && cycleCounters.values.all { it > 0 }) {
                            break
                        }
                    }
                }
                if (conjunctionStates[module]!!.values.all { it }) {
                    receivers?.forEach {
                        queue.add(Signal(false, module, it))
                        lowPulsesSend++
                    }
                } else {
                    receivers?.forEach {
                        queue.add(Signal(true, module, it))
                        highPulsesSend++
                    }
                }
            }
            else -> {}
        }

        if (queue.isEmpty()) {
            queue.add(Signal(false, "button", "broadcaster"))
            if (buttonPressed == 1000) {
                partOne = lowPulsesSend * highPulsesSend
            }
            buttonPressed++
            lowPulsesSend++

            cyclesStarted.filter { it.value }.forEach {
                cycleCounters.merge(it.key, 1) { a, b -> a + b }
            }
        }
    }

    println("part one: $partOne")
    println("part two: ${cycleCounters.values.fold(1L) {acc, l -> acc * l}}")
}

enum class ModuleType { Broadcast, Conjunction, FlipFlop }

data class Signal(val pulse: Boolean, val sender: String, val receiver: String)