package org.readutf.matchmaker.api

import com.alibaba.fastjson2.JSON
import java.util.UUID

fun main() {

    println(JSON.toJSONString(listOf(listOf(UUID.randomUUID()), listOf(UUID.randomUUID()))))

}