package org.readutf.matchmaker.shared.settings

open class QueueSettings(open var queueName: String) {

    override fun toString(): String {
        return "QueueSettings(queueName='$queueName')"
    }
}