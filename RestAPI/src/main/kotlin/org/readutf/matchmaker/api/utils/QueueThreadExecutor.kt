package org.readutf.matchmaker.api.utils

import java.util.concurrent.*


class QueueThreadExecutor : ThreadPoolExecutor(1, 1, 1, TimeUnit.MILLISECONDS, PriorityBlockingQueue()) {

    override fun <T : Any?> newTaskFor(runnable: Runnable?, value: T): RunnableFuture<T> {
        return CustomFutureTask(runnable)
    }

}

// A comparable FutureTask
class CustomFutureTask<T>(task: Runnable?) : FutureTask<T>(task!!, null), Comparable<CustomFutureTask<T>?> {
    private val task: PriorityTask = task as PriorityTask

    override fun compareTo(other: CustomFutureTask<T>?): Int {
        return task.priority.compareTo(other?.task?.priority ?: 5)
    }


}