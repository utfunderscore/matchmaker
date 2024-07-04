package org.readutf.matchmaker.api.queue.commands

import org.readutf.matchmaker.api.queue.QueueManager
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.Subcommand
import revxrsal.commands.command.CommandActor

@Command("queue")
class QueueCommand(val queueManager: QueueManager) {

    @Subcommand("info")
    fun join(actor: CommandActor, queueName: String) {

        val queue = queueManager.getQueue(queueName) ?: run {
            actor.error("Queue $queueName not found.")
            return
        }

        actor.reply("Queue $queueName:")
        actor.reply("  - Size: ${queue.getPlayersInQueue().size}")
        actor.reply("  - Settings: ${queue.getSettings()}")
        if(queue.getPlayersInQueue().isNotEmpty()) {
            for (queueEntry in queue.getPlayersInQueue()) {
                actor.reply("  - ${queueEntry.playerIds.joinToString(", ")}")
            }
        }
    }

}