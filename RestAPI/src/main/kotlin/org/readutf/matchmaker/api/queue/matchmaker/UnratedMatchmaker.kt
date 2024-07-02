package org.readutf.matchmaker.api.queue.matchmaker

import org.readutf.matchmaker.api.queue.Matchmaker
import org.readutf.matchmaker.api.queue.exception.TeamBuildException
import org.readutf.matchmaker.shared.result.QueueResult
import org.readutf.matchmaker.api.utils.QueueEntryUtils
import org.readutf.matchmaker.shared.entry.QueueEntry
import java.util.*
import kotlin.collections.ArrayList


class UnratedMatchmaker(private val teamSize: Int, private val numberOfTeams: Int) : Matchmaker<QueueEntry> {

    @Throws(TeamBuildException::class)
    override fun buildTeams(queue: List<QueueEntry>): List<List<QueueEntry>> {
        if(QueueEntryUtils.getTotalPlayers(queue) < teamSize * numberOfTeams) {
            // Prevent unnecessary computation, when we know we don't have enough players
            return emptyList()
        }

        val mutableQueue = queue.toMutableList()

        val teams: MutableList<List<QueueEntry>> = ArrayList()

        for (i in 0 until numberOfTeams) {
            val combinations = findCombinations(queue, teamSize)
            if(combinations.isEmpty()) {
                throw TeamBuildException("Could not build teams")
            }
            val firstCombination = combinations.first()
            mutableQueue.removeAll(firstCombination)
            teams.add(firstCombination)
        }

        return teams
    }

    private fun findCombinations(teams: List<QueueEntry>, target: Int): List<List<QueueEntry>> {
        val result: MutableList<List<QueueEntry>> = ArrayList()
        teams.sortedBy { it.size() }
        backtrack(teams, target, 0, ArrayList(), result)
        return result
    }

    private fun backtrack(
        teams: List<QueueEntry>,
        target: Int,
        start: Int,
        currentCombination: MutableList<QueueEntry>,
        result: MutableList<List<QueueEntry>>,
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


            backtrack(teams, target - teams[i].size(), i + 1, currentCombination, result)
            currentCombination.removeAt(currentCombination.size - 1)
        }
    }



}