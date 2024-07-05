package org.readutf.matchmaker.shared

import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONObject

class TypedJson(any: Any) {

    val type = any::class.qualifiedName
    val data = any

    override fun toString(): String {
        return "TypedJson(type=$type, data=$data)"
    }

    companion object {

        fun fromString(jsonString: String): TypedJson {

            val jsonObject: JSONObject = JSON.parseObject(jsonString)

            val clazz = Class.forName(jsonObject.getString("type"))

            val data = jsonObject.getObject("data", clazz)

            return TypedJson(data)
        }

    }

}