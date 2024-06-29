package org.readutf.matchmaker.api.queue.endpoints

import com.alibaba.fastjson2.JSON
import org.readutf.matchmaker.shared.entry.QueueEntry
import java.util.*

fun main() {

    class test : QueueEntry(listOf(UUID.randomUUID()))

    println(JSON.toJSONString(test()))


}