package ru.itport.bookcross.models

import ru.itport.bookcross.exception.ErrorDescriptor
import java.util.UUID

data class BaseResponse<T>(
    val success: Boolean = true,
    val body: T? = null,
    val error: Error? = null
) {
    companion object {
        fun <T> ok(body: T): BaseResponse<T> {
            return BaseResponse(
                success = true,
                body = body,
                error = null
            )
        }

        fun <T> fail(error: Error): BaseResponse<T> {
            return BaseResponse(
                success = false,
                body = null,
                error = error
            )
        }

        fun <T> fail(error: ErrorDescriptor): BaseResponse<T> {
            return BaseResponse(
                success = false,
                body = null,
                error = Error(
                    message = error.message,
                    code = error.code
                )
            )
        }
    }

    data class Error(
        val uuid: String? = UUID.randomUUID().toString(),
        val code: String,
        val message: String
    )

}


