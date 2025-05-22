package ru.itport.bookcross.services

import io.jmix.core.DataManager
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import ru.itport.bookcross.entity.BookingJournal
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

@Service
class BookingNotificationService(
    private val dataManager: DataManager,
    private val notificationService: NotificationService
) {
    @Scheduled(cron = "0 0 9 * * ?") // Каждый день в 9 утра
    fun notifyUsers() {
        val tomorrow = LocalDate.now().plusDays(1)
        val dateTomorrow = Date.from(tomorrow.atStartOfDay(ZoneId.systemDefault()).toInstant())

        val bookingRecords = dataManager.load(BookingJournal::class.java)
            .query("select b from BookingJournal b where b.end_time = :endDate")
            .parameter("endDate", dateTomorrow)
            .list()

        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("ru"))

        for (booking in bookingRecords) {
            val returnDate = dateFormat.format(booking.end_time)
            val notificationMessage = buildString {
                append("Привет, ${booking.user?.username}! ")
                append("Напоминаем, что срок бронирования книги ${booking.book?.name} заканчивается завтра, $returnDate. ")
                append("Пожалуйста, верните книгу вовремя.")
            }

            booking.user?.telegramId?.let { telegramId ->
                notificationService.sendMessage(telegramId, notificationMessage)
            }
        }
    }
}
