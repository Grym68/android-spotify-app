package co.uk.android.spotify.network

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import com.squareup.moshi.Moshi
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException


typealias ValidationErrors = Map<String, Array<String>>

@JsonClass(generateAdapter = true)
@Parcelize
data class ErrorBody(
    @Json(name = "message")
    val message: String,
    @Json(name = "errors")
    val validationErrors: ValidationErrors? = emptyMap(),
) : Parcelable {
    val messages: List<String>
        get() = validationErrors?.values?.flatMap { it.toList() } ?: emptyList()
}

@Parcelize
data class ApiError(
    var statusCode: Int,
    override var message: String,
    var underlyingError: Throwable?,
    var errors: ErrorBody? = null,
) : Throwable(), Parcelable {
    constructor(message: String): this(ERROR_CODE_MESSAGE, message, IllegalArgumentException())

    fun isNetworkError() = statusCode == ERROR_CODE_NETWORK
    fun isInternalError() = statusCode == ERROR_CODE_INTERNAL

    companion object {

        private const val ERROR_CODE_INTERNAL = -1
        private const val ERROR_CODE_NETWORK = -2
        private const val ERROR_CODE_MESSAGE = -3

        fun wrap(exception: Exception) = ApiError(ERROR_CODE_INTERNAL, exception.message ?: "", exception).apply {
            when (exception) {
                is HttpException -> {
                    this.statusCode = exception.response()?.code() ?: -1
                    val response = exception.response()
                    try {
                        val fromResponse = wrap(response.code(), response.errorBody()?.string() ?: "")
                        this.errors = fromResponse.errors
                        this.message = fromResponse.message
                    } catch (e: IllegalStateException) {
                        e.printStackTrace()
                    } catch (e: NullPointerException) {
                        e.printStackTrace()
                    }
                }
                is HttpException -> {
                    this.statusCode = exception.response()?.code() ?: -1
                    exception.response()?.let {
                        val fromResponse = wrap(it.code(), it.errorBody()?.string() ?: "")
                        this.errors = fromResponse.errors
                        this.message = fromResponse.message
                    }
                }
                is SocketException, is SocketTimeoutException, is UnknownHostException -> {
                    this.statusCode = ERROR_CODE_NETWORK
                }
            }
            stackTrace = exception.stackTrace
        }

        fun wrap(throwable: Throwable): ApiError {
            if (throwable is ApiError) return throwable
            if (throwable is Exception) return wrap(throwable)
            return ApiError(
                statusCode = ERROR_CODE_INTERNAL,
                message = throwable.message ?: "",
                underlyingError = throwable,
            ).apply {
                stackTrace = throwable.stackTrace
            }
        }

        fun wrap(statusCode: Int, body: String): ApiError {
            try {
                val errors = Moshi.Builder().build().adapter(ErrorBody::class.java).fromJson(body)
                return ApiError(
                    statusCode = statusCode,
                    message = errors?.message ?: "",
                    underlyingError = null,
                    errors = errors
                )
            } catch (e: Exception) {
            }
            return ApiError(statusCode, "", Exception())
        }
    }
}