package org.readutf.matchmaker.shared.settings

data class UnratedQueueSettings(
    override var queueName: String,
    var teamSize: Int,
    var numberOfTeams: Int,
) : QueueSettings()
