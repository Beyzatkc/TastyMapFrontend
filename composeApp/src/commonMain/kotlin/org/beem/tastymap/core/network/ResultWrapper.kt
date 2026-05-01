package org.beem.tastymap.core.network


sealed class ResultWrapper<out T> {

    data class Success<T>(val data: T) : ResultWrapper<T>()

    data class Error(
        val message: String,
        val type: ErrorType
    ) : ResultWrapper<Nothing>()
}

enum class ErrorType {
    EMAIL_NOT_VERIFIED,
    WRONG_REGISTER,
    SERVER_ERROR,
    NETWORK_ERROR,
    EMPTY_RESPONSE
}


inline fun <T, R> ResultWrapper<T>.mapSuccess(
    transform: (T) -> R
): ResultWrapper<R> =
    when (this) {
        is ResultWrapper.Success -> ResultWrapper.Success(transform(data))
        is ResultWrapper.Error -> this
    }
