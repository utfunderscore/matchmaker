package org.readutf.matchmaker.api.queue.commands

import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONWriter
import org.readutf.matchmaker.api.queue.QueueManager
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.Subcommand
import revxrsal.commands.command.CommandActor

@Command("queue")
class QueueCommand(
    val queueManager: QueueManager,
) {
    @Subcommand("list")
    fun list(actor: CommandActor) {
        actor.reply("Queues:")
        for (queue in queueManager.getQueues()) {
            actor.reply("  - ${queue.queueName}")
        }
    }

    @Subcommand("info")
    fun join(
        actor: CommandActor,
        queueName: String,
    ) {
        val queue =
            queueManager.getQueue(queueName) ?: run {
                actor.error("Queue $queueName not found.")
                return
            }

        actor.reply("Queue $queueName:")
        actor.reply(JSON.toJSONString(queue, JSONWriter.Feature.PrettyFormat))
    }
}
