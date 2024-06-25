package org.readutf.matchmaker.api.utils

import com.alibaba.fastjson2.JSON
import io.javalin.json.JsonMapper
import java.lang.reflect.Type

object FastJsonMapper : JsonMapper {

    override fun <T : Any> fromJsonString(json: String, targetType: Type): T {
        return JSON.parseObject(json, targetType)
    }

    override fun toJsonString(obj: Any, type: Type): String {
        return JSON.toJSONString(obj)
    }

}