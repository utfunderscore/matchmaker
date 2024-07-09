package org.readutf.matchmaker.api.utils

fun findAllAddends(targetSize: Int): List<ComparableList> {
    val availableOptions = mutableListOf<Int>()

    for (i in 1 until targetSize + 1) {
        val amountOfNumber = targetSize / i

        availableOptions.addAll(List(amountOfNumber) { i })
    }

    return findCombinations(availableOptions, targetSize).distinct()
}

private fun findCombinations(
    teams: List<Int>,
    target: Int,
): List<ComparableList> {
    val result: MutableList<List<Int>> = ArrayList()
    teams.sortedBy { it }
    backtrack(teams, target, 0, ArrayList(), result)
    return result.map { ComparableList(it) }
}

private fun backtrack(
    teams: List<Int>,
    target: Int,
    start: Int,
    currentCombination: MutableList<Int>,
    result: MutableList<List<Int>>,
) {
    if (target == 0) {
        result.add(ArrayList(currentCombination))
        return
    }
    if (target < 0) {
        return
    }
    for (i in start until teams.size) {
        currentCombination.add(teams[i])

        backtrack(teams, target - teams[i], i + 1, currentCombination, result)
        currentCombination.removeAt(currentCombination.size - 1)
    }
}

class ComparableList(
    var groupSizes: List<Int>,
) {
    val numberOfSizes = mutableMapOf<Int, Int>()

    init {
        groupSizes = groupSizes.sorted()

        groupSizes.forEach {
            val count = numberOfSizes.getOrDefault(it, 0)
            numberOfSizes[it] = count + 1
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ComparableList) return false

        if (groupSizes != other.groupSizes) return false

        return true
    }

    override fun hashCode(): Int = groupSizes.hashCode()

    override fun toString(): String = "Team(groupSizes=$groupSizes)"
}
