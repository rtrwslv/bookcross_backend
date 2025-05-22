package ru.itport.bookcross.models

import ru.itport.bookcross.entity.Status
import java.util.*

data class BookingJournalDTO(
    val id: UUID,
    val userId: UUID,
    val bookId: UUID,
    val startTime: Date?,
    val endTime: Date?,
    val status: Status,
    val photo: String?,
    val comment: String?
)