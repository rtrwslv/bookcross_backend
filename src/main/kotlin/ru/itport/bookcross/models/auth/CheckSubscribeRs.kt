package ru.itport.bookcross.models.auth

import java.io.Serializable

open class CheckSubscribeRs(): Serializable {
    open var ok : Boolean = false
    open var result: Result = Result()

    class Result(): Serializable {
        var status: String = ""
    }

}