package org.readutf.matchmaker.api.utils

import com.alibaba.fastjson2.JSON

fun Any.toString(pretty: Boolean = false): String = JSON.toJSONString(this)