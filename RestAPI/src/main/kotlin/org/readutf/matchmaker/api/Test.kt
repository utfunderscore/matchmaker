package org.readutf.matchmaker.api

import org.readutf.matchmaker.api.utils.PriorityTask
import org.readutf.matchmaker.api.utils.QueueThreadExecutor

fun main() {

    val executor = QueueThreadExecutor()

    executor.submit(createTask(5))
    executor.submit(createTask(4))
    executor.submit(createTask(3))
    executor.submit(createTask(2))


}

fun createTask(id: Int): PriorityTask {

    return object : PriorityTask(id) {
        override fun run() {
            println(priority)
        }

    }

}