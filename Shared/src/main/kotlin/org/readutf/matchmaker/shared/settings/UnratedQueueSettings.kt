package org.readutf.matchmaker.shared.settings

data class UnratedQueueSettings(
    override var queueName: String,
    var teamSize: Int,
    var numberOfTeams: Int,
) : QueueSettings(queueName) {

    override fun toString(): String {
        return "UnratedQueueSettings(queueName='$queueName', teamSize=$teamSize, numberOfTeams=$numberOfTeams)"
    }
}
