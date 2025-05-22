package ru.itport.bookcross.controller

import org.springframework.web.bind.annotation.*
import ru.itport.bookcross.models.BaseResponse
import ru.itport.bookcross.models.BookingJournalDTO
import ru.itport.bookcross.services.BookingJournalService
import ru.itport.bookcross.utils.ControllerUtils.Companion.serviceCall
import java.util.*

@RestController
@CrossOrigin
@RequestMapping("custom/api/v1/bookings")
class BookingJournalController(
    private val bookingJournalService: BookingJournalService
) {
    @GetMapping("/all")
    fun getAllBookings(): BaseResponse<List<BookingJournalDTO>> {
        return serviceCall { bookingJournalService.getAllBookings() }
    }

    @GetMapping("/{id}")
    fun getBookingById(@PathVariable id: UUID): BaseResponse<BookingJournalDTO> {
        return serviceCall { bookingJournalService.getBookingById(id) }
    }

    @GetMapping("/user/{userId}/active")
    fun getActiveBookingsByUserId(@PathVariable userId: UUID): BaseResponse<List<BookingJournalDTO>> {
        return serviceCall { bookingJournalService.getActiveBookingsByUserId(userId) }
    }

}
