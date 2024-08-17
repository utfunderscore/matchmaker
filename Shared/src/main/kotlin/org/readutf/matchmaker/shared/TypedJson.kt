package org.readutf.matchmaker.shared

import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONObject

class TypedJson(
    val data: Any,
    val type: String = data::class.qualifiedName!!,
) {
    override fun toString(): String = "TypedJson(type=$type, data=$data)"

    companion object {
        fun fromString(jsonString: String): TypedJson {
            val jsonObject: JSONObject = JSON.parseObject(jsonString)

            val clazz = Class.forName(jsonObject.getString("type"))

            val dataJson = jsonObject.getString("data")

            val data = JSON.parseObject(dataJson, clazz)

            return TypedJson(data)
        }
    }
}
