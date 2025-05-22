package ru.itport.bookcross.models.auth

data class CheckSubscribeBody(
    val chat_id: Long,
    val user_id: Long
)