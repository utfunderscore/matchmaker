package org.readutf.matchmaker.demo

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.runBlocking
import org.readutf.matchmaker.shared.entry.QueueEntry
import org.readutf.matchmaker.wrapper.Queue
import org.readutf.matchmaker.wrapper.QueueListener
import org.readutf.matchmaker.wrapper.QueueManager
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.util.*
import kotlin.random.Random

fun main() {

    StressTest("localhost", 8280)

}

class StressTest(host: String, port: Int) {

    private val logger = KotlinLogging.logger {  }

    private val sessionId: String
    private val timer: Timer = Timer()
    private val queue: Queue

    val inQueue = mutableSetOf<UUID>()
    private val inMatch = mutableSetOf<UUID>()
    private val doingNothing = mutableSetOf<UUID>()

    init {

        val stressTestListener = StressTestListener(this)
        val queueManager = runBlocking {
            QueueManager.create(host, port, stressTestListener)
        }
        sessionId = queueManager.socketId
        queue = queueManager.getQueue("test")!!


        for (i in 0 until 10) {
            joinQueue(listOf(UUID.randomUUID()))
        }
    }

    fun joinQueue(players: List<UUID>) {
        inQueue.addAll(players)
        queue.join(players).invokeOnCompletion {
            logger.info { "$players have joined the queue" }
        }
    }

    private fun createTeams(teamSize: Int, numOfTeams: Int): List<List<UUID>> {
        return List(numOfTeams) {
            List(teamSize) { UUID.randomUUID() }
        }
    }

    fun simulateMatch(teams: List<QueueEntry>) {
        logger.info { "Simulating match with teams: $teams" }
        inMatch.addAll(teams.map { it.playerIds }.flatten())
        timer.schedule(object : TimerTask() {
            override fun run() {
                logger.info { "Match complete, re entering queue" }
                for (team in teams) {
                    joinQueue(team.playerIds)
                }
            }
        }, Random.nextLong(1000, 3000))
    }

}

class StressTestListener(val stressTest: StressTest) : QueueListener {

    private val logger = KotlinLogging.logger {  }

    override fun onQueueSuccess(queue: Queue, teams: List<List<QueueEntry>>) {
        logger.info { "Received queue result: avg:${average(teams)} min: ${min(teams)} max: ${max(teams)}" }
        stressTest.inQueue.removeAll(teams.flatten().map { it.playerIds }.flatten().toSet())
        stressTest.simulateMatch(teams.flatten())
    }

    override fun onMatchMakerError(queue: Queue, failureReason: String) {
        logger.info { "Matchmaker error: $failureReason" }
    }

    fun average(teams: List<List<QueueEntry>>): Double {
        return teams.flatten().map { Duration.between(LocalDateTime.now(), it.joinedAt).abs().toMillis() }.average()
    }

    fun min(teams: List<List<QueueEntry>>): Long {
        return teams.flatten().minOfOrNull { Duration.between(LocalDateTime.now(), it.joinedAt).abs().toMillis() } ?: 0
    }

    fun max(teams: List<List<QueueEntry>>): Long {
        return teams.flatten().maxOfOrNull { Duration.between(LocalDateTime.now(), it.joinedAt).abs().toMillis() } ?: 0
    }


}