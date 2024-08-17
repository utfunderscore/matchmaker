package org.readutf.matchmaker.shared.entry

import com.alibaba.fastjson2.annotation.JSONField
import java.time.LocalDateTime
import java.util.UUID

open class QueueEntry(
    val entryId: UUID = UUID.randomUUID(),
    val sessionId: String,
    val playerIds: List<UUID>,
    @JSONField val joinedAt: LocalDateTime = LocalDateTime.now(),
) {
    @JSONField(serialize = false)
    fun size(): Int = playerIds.size

    @JSONField(serialize = false)
    override fun toString(): String = "QueueEntry(sessionId='$sessionId', playerIds=$playerIds, joinedAt=$joinedAt)"
}
