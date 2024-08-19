package org.readutf.matchmaker.shared.result

class Result<T>(
    private val value: T?,
    private val error: String?,
) {
    fun isOk(): Boolean = error == null

    fun isError(): Boolean = error != null

    fun <U> map(f: (T) -> U): Result<U> = if (isOk()) ok(f(value!!)) else error(error!!)

    fun <U> flatMap(f: (T) -> Result<U>): Result<U> = if (isOk()) f(value!!) else error(error!!)

    fun get(): T = value!!

    fun getError(): String = error!!

    override fun toString(): String = "Result(value=$value, error=$error)"

    companion object {
        fun <T> error(error: String): Result<T> = Result(null, error)

        fun <T> ok(value: T): Result<T> = Result(value, null)
    }
}
