package ru.itport.benifits.models.auth

import java.util.*

data class JwtToken(
    val token: String,
    val expiration: Date? = null,
    val cityId: String? = null,
    val userId: UUID? = null
)
