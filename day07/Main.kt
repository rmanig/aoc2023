import java.io.File

fun main() {
    val lines = File("input.txt").useLines { lines ->
        lines.flatMap { line -> line.split(" ")
            .zipWithNext() }
            .map { Pair(it.first, it.second.toInt()) }
            .toList() }
    val partOneSum = game(lines, joker = false)
    println("part one: $partOneSum")
    val partTwoSum = game(lines, joker = true)
    println("part two: $partTwoSum")
}

fun game(lines: List<Pair<String, Int>>, joker: Boolean) : Int {
    val types: MutableMap<CardType, List<Pair<String, Int>>> = mutableMapOf()
    for ((hand, bid) in lines) {
        val cardType = CardType.get(hand, joker)
        val type = types.getOrDefault(cardType, mutableListOf())
        type.addLast(Pair(hand, bid))
        types[cardType] = type
    }
    val sorted = CardType.entries
        .flatMap { types.getOrDefault(it, listOf())
            .sortedWith { o1, o2 -> compareHand(o1.first, o2.first, joker) } }

    return sorted.map { it.second }
        .reduceIndexed { index, acc, it ->  acc + ((index + 1) * it) }
}

fun compareHand(one: String, another: String, joker: Boolean = false) : Int {
    val result = rank(one[0], joker).compareTo(rank(another[0], joker))
    return if (result == 0) {
        compareHand(one.drop(1), another.drop(1), joker)
    } else {
        result
    }
}

fun rank(card: Char, joker: Boolean = false) : Int {
    return if (card.isDigit()) {
        card.digitToInt()
    } else {
        when (card) {
            'A' -> 14
            'K' -> 13
            'Q' -> 12
            'J' -> if (joker) 1 else 11
            'T' -> 10
            else -> 0
        }
    }
}

enum class CardType {

    High, OnePairs, TwoPairs, ThreeOfAKind, FullHouse, FourOfAKind, FiveOfAKind;

    companion object {
        fun get(hand: String, withJoker: Boolean = false) : CardType {
            val occurenceByCard = hand.groupBy({ it }, { 1 })
                .mapValues { it.value.sum() }.toMutableMap()
            if (withJoker) {
                occurenceByCard.filterNot { it.key == 'J' }.maxByOrNull { it.value }?.also {
                    occurenceByCard[it.key] = it.value + occurenceByCard.getOrDefault('J', 0)
                    occurenceByCard['J'] = 0
                }
            }
            val occurences = occurenceByCard.values
            return if (occurences.count { it == 2 } == 2) {
                TwoPairs
            } else if (occurences.toSet().containsAll(setOf(3, 2))) {
                FullHouse
            } else {
                listOf(High, OnePairs, ThreeOfAKind, FourOfAKind, FiveOfAKind)[occurences.max() - 1]
            }
        }
    }
}