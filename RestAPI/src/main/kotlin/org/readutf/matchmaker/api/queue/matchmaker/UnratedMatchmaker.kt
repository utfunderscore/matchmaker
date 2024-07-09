package org.readutf.matchmaker.api.queue.matchmaker

import org.readutf.matchmaker.api.queue.Matchmaker
import org.readutf.matchmaker.api.queue.exception.TeamBuildException
import org.readutf.matchmaker.api.utils.findAllAddends
import org.readutf.matchmaker.shared.entry.QueueEntry
import java.util.*
import kotlin.collections.ArrayDeque

class UnratedMatchmaker(
    teamSize: Int,
    private val numberOfTeams: Int,
) : Matchmaker<QueueEntry> {
    private val addends = findAllAddends(teamSize)

    @Throws(TeamBuildException::class)
    override fun buildTeams(queue: List<QueueEntry>): List<List<QueueEntry>> {
        val sizeToEntry = mutableMapOf<Int, ArrayDeque<QueueEntry>>()

        queue.forEach {
            val teams = sizeToEntry.getOrDefault(it.size(), ArrayDeque())
            teams.add(it)
            sizeToEntry[it.size()] = teams
        }

        val teams = mutableListOf<List<QueueEntry>>()
        for (i in 0 until numberOfTeams) {
            val team = buildTeam(sizeToEntry)
            if (team.isEmpty()) return emptyList()

            teams.add(team)

            for (queueEntry in team) {
                sizeToEntry[queueEntry.size()]?.remove(queueEntry)
            }
        }
        return teams
    }

    private fun buildTeam(sizeToEntry: MutableMap<Int, ArrayDeque<QueueEntry>>): MutableList<QueueEntry> {
        addends.map { it.numberOfSizes }.forEach { sizes ->

            val allValid =
                sizes.entries.all {
                    sizeToEntry.getOrDefault(it.key, emptyList<Int>()).size >= it.value
                }

            if (allValid) {
                val teams = mutableListOf<QueueEntry>()

                sizes.forEach { (size, numOfSize) ->
                    for (i in 0 until numOfSize) {
                        teams.add(sizeToEntry[size]!!.removeFirst())
                    }
                }
                return teams
            }
        }
        return mutableListOf()
    }
}
