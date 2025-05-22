package ru.itport.bookcross.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.itport.bookcross.exception.ErrorDescriptor
import ru.itport.bookcross.exception.PlatformException
import ru.itport.bookcross.models.BaseResponse

class ControllerUtils {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(ControllerUtils::class.java)
        fun <T> serviceCall(function: () -> T): BaseResponse<T> {
            runCatching {
                return BaseResponse.ok(function() )
            }.getOrElse {
                logger.error("ERROR: ${it.message}")
                return when (it) {
                    is PlatformException -> BaseResponse.fail<T>(it.getError())
                    else -> BaseResponse.fail<T>(ErrorDescriptor.INTERNAL_ERROR)
                }
            }
        }
    }
}