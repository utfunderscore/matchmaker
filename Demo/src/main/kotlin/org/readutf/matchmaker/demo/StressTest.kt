package org.readutf.matchmaker.demo

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.runBlocking
import org.readutf.matchmaker.shared.entry.QueueEntry
import org.readutf.matchmaker.shared.result.GameResult
import org.readutf.matchmaker.shared.result.QueueTickData
import org.readutf.matchmaker.wrapper.Queue
import org.readutf.matchmaker.wrapper.QueueListener
import org.readutf.matchmaker.wrapper.QueueManager
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.ceil

fun main() {
    val numberOfPlayers = 1500
    val playersPerThread = 100

    val shared = Executors.newSingleThreadExecutor()

    for (i in 0 until (1500 / 100)) {
        StressTest("localhost", 8280, 3, shared)
    }
}

class StressTest(
    host: String,
    port: Int,
    val maxRejoins: Int,
    val sharedExecutor: ExecutorService,
) {
    private val logger = KotlinLogging.logger { }

    private val joiningExecutor = Executors.newSingleThreadExecutor()

    val sessionId: String
    private val timer: Timer = Timer()
    private val queue: Queue

    val rejoins = mutableMapOf<UUID, AtomicInteger>()

    val timeToMatch = mutableMapOf<UUID, MutableList<Long>>()

    val inQueue = mutableSetOf<UUID>()

    init {

        val stressTestListener = StressTestListener(this)
        val queueManager =
            runBlocking {
                QueueManager.create(host, port, stressTestListener)
            }
        sessionId = queueManager.socketId
        queue = queueManager.getQueue("test")!!

        for (i in 0 until 100) {
            joinQueue(listOf(UUID.randomUUID()))
        }
    }

    fun joinQueue(players: List<UUID>) {
        joiningExecutor.submit {
            inQueue.addAll(players)
            queue.join(players).invokeOnCompletion {
                logger.info { "$players have joined the queue" }
            }
        }
    }
}

class StressTestListener(
    private val stressTest: StressTest,
) : QueueListener {
    private val logger = KotlinLogging.logger { }

    var statAnnounced = AtomicBoolean(false)

    override fun onQueueSuccess(
        queue: Queue,
        queueResult: QueueTickData,
        gameResult: GameResult,
    ) {
        val teams = queueResult.teams

        logger.info { "Received $teams queue result: avg:${average(teams)} min: ${min(teams)} max: ${max(teams)}" }

        teams.flatten().forEach { queueEntry ->
            queueEntry.playerIds.forEach {
                stressTest.timeToMatch
                    .getOrPut(
                        it,
                    ) { mutableListOf() }
                    .add(
                        Duration
                            .between(queueEntry.joinedAt, LocalDateTime.now())
                            .abs()
                            .toMillis(),
                    )
            }
        }

        val players =
            teams
                .asSequence()
                .flatten()
                .filter { it.sessionId == stressTest.sessionId }
                .map { it.playerIds }
                .flatten()
                .toSet()

        logger.info { "  - $players" }

        stressTest.inQueue.removeAll(players)

        players.forEach { stressTest.rejoins.getOrPut(it) { AtomicInteger(0) }.incrementAndGet() }

        logger.info { "Added players $players" }

        val rejoining = players.filter { stressTest.rejoins.getOrDefault(it, AtomicInteger(0)).get() < stressTest.maxRejoins }

        println("in queue: ${stressTest.inQueue}")
        println("in queue: ${statAnnounced.get()}")

        if (rejoining.isEmpty() && stressTest.inQueue.isEmpty()) {
            stressTest.sharedExecutor.submit {
                if (statAnnounced.getAndSet(true)) return@submit

                val times = stressTest.timeToMatch.values.flatten()

                println("avg: ${times.average()}")
                println("max: ${times.max()}")
                println("min: ${times.min()}")
                println("95th: ${percentile(times, 95.0)}")
                println("99th: ${percentile(times, 99.0)}")
            }
        }

        Timer().schedule(1500) {
            stressTest.joinQueue(rejoining)
        }
    }

    fun percentile(
        latencies: List<Long>,
        percentile: Double,
    ): Long {
        val sorted = latencies.sorted()

        val index = ceil(percentile / 100.0 * sorted.size).toInt()
        return sorted[index - 1]
    }

    override fun onMatchMakerError(
        queue: Queue,
        failureReason: String,
    ) {
        logger.info { "Matchmaker error: $failureReason" }
    }

    fun average(teams: List<List<QueueEntry>>): Double =
        teams
            .flatten()
            .map {
                Duration.between(LocalDateTime.now(), it.joinedAt).abs().toMillis()
            }.average()

    fun min(teams: List<List<QueueEntry>>): Long =
        teams.flatten().minOfOrNull {
            Duration.between(LocalDateTime.now(), it.joinedAt).abs().toMillis()
        } ?: 0

    fun max(teams: List<List<QueueEntry>>): Long =
        teams.flatten().maxOfOrNull {
            Duration.between(LocalDateTime.now(), it.joinedAt).abs().toMillis()
        } ?: 0
}

fun Timer.schedule(
    delay: Long,
    runnable: () -> Unit,
) {
    schedule(
        object : TimerTask() {
            override fun run() {
                runnable()
            }
        },
        delay,
    )
}
