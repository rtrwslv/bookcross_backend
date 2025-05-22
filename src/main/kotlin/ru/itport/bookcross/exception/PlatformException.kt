package ru.itport.bookcross.exception

class PlatformException(private val error: ErrorDescriptor, throwable: Throwable?=null) : RuntimeException(error.message, throwable) {

    fun getError(): ErrorDescriptor {
        return error
    }

    fun getCode(): String {
        return error.code
    }
}