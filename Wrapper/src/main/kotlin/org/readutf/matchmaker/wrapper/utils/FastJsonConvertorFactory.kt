package org.readutf.matchmaker.wrapper.utils

import com.alibaba.fastjson2.JSON
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class FastJsonConvertorFactory : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *> {
        return FastJsonResponseBodyConverter<Any>(type)
    }

    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<out Annotation>,
        methodAnnotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<*, RequestBody> {
        return FastJsonRequestBodyConverter<Any>()
    }

    class FastJsonResponseBodyConverter<T>(private var type: Type) : Converter<ResponseBody, T> {
        override fun convert(value: ResponseBody): T {
            val inputSteam = value.byteStream()
            return JSON.parseObject(inputSteam, type)
        }
    }

    class FastJsonRequestBodyConverter<T> : Converter<T, RequestBody> {
        override fun convert(value: T): RequestBody {
            return RequestBody.create(MediaType.parse("application/json"), JSON.toJSONString(value))
        }
    }

}