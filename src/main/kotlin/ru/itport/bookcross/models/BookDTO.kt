package ru.itport.bookcross.models

import java.util.*

data class BookDTO(
    val id: UUID? = null,
    val userId: UUID?,
    val name: String,
    val author: String?,
    val year: Int?,
    val category: CategoryDTO? = null,
    val description: String?,
    val cover: String,
    val uploadDate: Date?,
    val maxReservationPeriod: Int?,
    val pageSize: Int?,
    val isReserved: Boolean?,
    val isPublished: Boolean?,
    val isUserBook: Boolean?
)
