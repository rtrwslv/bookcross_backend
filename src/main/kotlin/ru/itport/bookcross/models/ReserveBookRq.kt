package ru.itport.bookcross.models

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class ReserveBookRq(
    @JsonProperty("bookId")
    val bookId: UUID,
    @JsonProperty("startDate")
    val startDate: Date,
    @JsonProperty("endDate")
    val endDate: Date
)