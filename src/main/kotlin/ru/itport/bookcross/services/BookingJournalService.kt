package ru.itport.bookcross.services

import io.jmix.core.DataManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.itport.bookcross.entity.BookingJournal
import ru.itport.bookcross.entity.Status
import ru.itport.bookcross.models.BookingJournalDTO
import ru.itport.bookcross.exception.PlatformException
import ru.itport.bookcross.exception.ErrorDescriptor
import java.util.*

@Service
class BookingJournalService @Autowired constructor(
    private val dataManager: DataManager
) {
    fun getAllBookings(): List<BookingJournalDTO> {
        val bookings = dataManager.load(BookingJournal::class.java)
            .all()
            .list()

        return bookings.map { booking ->
            BookingJournalDTO(
                id = booking.id ?: throw PlatformException(ErrorDescriptor.INVALID_BOOKING_ID),
                userId = booking.user?.id ?: throw PlatformException(ErrorDescriptor.INVALID_USER_ID),
                bookId = booking.book?.id ?: throw PlatformException(ErrorDescriptor.INVALID_BOOK_ID),
                startTime = booking.start_time,
                endTime = booking.end_time,
                status = booking.getStatus() ?: Status.AVAILABLE,
                photo = booking.photo?.toString(),
                comment = booking.comment
            )
        }
    }

    fun getBookingById(id: UUID): BookingJournalDTO {
        val booking = dataManager.load(BookingJournal::class.java)
            .id(id)
            .optional()
            .orElseThrow { PlatformException(ErrorDescriptor.INVALID_BOOKING_ID) }

        return BookingJournalDTO(
            id = booking.id!!,
            userId = booking.user?.id!!,
            bookId = booking.book?.id!!,
            startTime = booking.start_time,
            endTime = booking.end_time,
            status = booking.getStatus() ?: Status.AVAILABLE,
            photo = booking.photo?.toString(),
            comment = booking.comment
        )
    }

    fun getActiveBookingsByUserId(userId: UUID): List<BookingJournalDTO> {
        val bookings = dataManager.load(BookingJournal::class.java)
            .query("""
            SELECT b
            FROM BookingJournal b
            WHERE b.user.id = :userId
            AND b.status = :status
        """)
            .parameter("userId", userId)
            .parameter("status", Status.BOOKED.id)
            .list()

        return bookings.map { booking ->
            BookingJournalDTO(
                id = booking.id!!,
                userId = booking.user?.id!!,
                bookId = booking.book?.id!!,
                startTime = booking.start_time,
                endTime = booking.end_time,
                status = booking.getStatus() ?: Status.AVAILABLE,
                photo = booking.photo?.toString(),
                comment = booking.comment
            )
        }
    }
}