package ru.itport.bookcross.services

import io.jmix.core.DataManager
import io.jmix.core.FileRef
import io.jmix.core.querycondition.PropertyCondition
import io.jmix.core.security.CurrentAuthentication
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.itport.bookcross.entity.*
import ru.itport.bookcross.exception.ErrorDescriptor
import ru.itport.bookcross.exception.PlatformException
import ru.itport.bookcross.mapper.mapToString
import ru.itport.bookcross.models.BookDTO
import ru.itport.bookcross.models.CategoryDTO
import java.text.SimpleDateFormat
import java.util.*

@Service
open class BookService @Autowired constructor(
    private val dataManager: DataManager,
    private val currentAuthentication: CurrentAuthentication,
    private val notificationService: NotificationService,
    @Value("\${application.domain}") private val domain: String
) {

    open fun getAllBooks(): List<BookDTO> {
        val books = dataManager.load(Book::class.java)
            .all()
            .list()

        return books.map { book ->
            BookDTO(
                book.id!!,
                book.userId,
                book.name!!,
                author = book.author,
                year = book.year,
                category = CategoryDTO(
                    id = book.category!!.id!!,
                    name = book.category?.name
                ),
                description = book.description,
                cover = book.cover?.mapToString(domain) ?: "",
                uploadDate = book.uploadDate,
                maxReservationPeriod = book.maxReservationPeriod,
                pageSize = book.pageSize,
                isReserved = book.isReserved,
                isPublished = book.isPublished,
                isUserBook = book.isUserBook
            )
        }
    }

    open fun getBookById(id: UUID): BookDTO {
        val book = dataManager.load(Book::class.java)
            .id(id)
            .optional()
            .orElseThrow { PlatformException(ErrorDescriptor.INVALID_BOOK_ID) }

        return BookDTO(
            book.id!!,
            book.userId,
            book.name!!,
            author = book.author,
            year = book.year,
            category = CategoryDTO(
                id = book.category!!.id!!,
                name = book.category?.name
            ),
            description = book.description,
            cover = book.cover?.mapToString(domain) ?: "",
            uploadDate = book.uploadDate,
            maxReservationPeriod = book.maxReservationPeriod,
            pageSize = book.pageSize,
            isReserved = book.isReserved,
            isPublished = book.isPublished,
            isUserBook = book.isUserBook
        )
    }

    open fun getBooksByCategory(categoryId: UUID): List<BookDTO> {
        val books = (dataManager.load(Book::class.java))
            .query(
                "select b from Book b " +
                        "where (b.category.id) = :categoryId"
            )
            .parameter("categoryId", categoryId)
            .list()

        return books.map { book ->
            BookDTO(
                book.id!!,
                book.userId,
                book.name!!,
                author = book.author,
                year = book.year,
                category = CategoryDTO(
                    id = book.category!!.id!!,
                    name = book.category?.name
                ),
                description = book.description,
                cover = book.cover?.mapToString(domain) ?: "",
                uploadDate = book.uploadDate,
                maxReservationPeriod = book.maxReservationPeriod,
                pageSize = book.pageSize,
                isReserved = book.isReserved,
                isPublished = book.isPublished,
                isUserBook = book.isUserBook
            )
        }
    }

    @Transactional
    open fun reserveBook(bookId: UUID, startDate: Date, endDate: Date): String {
        // requireNotNull(dataManager) { "DataManager is not injected" }
        val user = dataManager.load(User::class.java).condition(
            PropertyCondition.equal("username", currentAuthentication.user.username)
        )
            .one()

        val book = dataManager.load(Book::class.java)
            .id(bookId)
            .optional()
            .orElseThrow { PlatformException(ErrorDescriptor.INVALID_BOOK_ID) }

        if (book.isReserved == true) {
            throw PlatformException(ErrorDescriptor.BOOK_ALREADY_RESERVED)
        }

        if (startDate > endDate) {
            throw PlatformException(ErrorDescriptor.INVALID_DATE_RANGE)
        }

        val bookingRecord = dataManager.create(BookingJournal::class.java).apply {
            this.user = user
            this.book = book
            this.start_time = startDate
            this.end_time = endDate
            this.setStatus(Status.BOOKED)
        }

        dataManager.save(bookingRecord)

        book.isReserved = true
        dataManager.save(book)

        logger.info("Книга с ID {} успешно забронирована", bookId)

        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("ru"))
        val startDateFormatted = dateFormat.format(startDate)
        val endDateFormatted = dateFormat.format(endDate)

        val notificationMessage = "Привет, ${user.username}! " +
                "Вы успешно забронировали книгу ${book.name}. " +
                "Срок бронирования: с $startDateFormatted по $endDateFormatted."

        if (user.telegramId.isNullOrEmpty()) {
            logger.warn("Ошибка: Telegram ID для пользователя ${user.username} не найден. Уведомление не отправлено.")
        } else {
            notificationService.sendMessage(user.telegramId!!, notificationMessage)
        }
        return "Книга успешно забронирована"
    }

    @Transactional
    open fun returnBook(bookId: UUID, endDate: Date, photoPath: FileRef, comment: String): String {
        val username = currentAuthentication.user.username
        val userId = dataManager.load(User::class.java)
            .condition(PropertyCondition.equal("username", username))
            .one()
            .id

        val book = dataManager.load(Book::class.java)
            .id(bookId)
            .optional()
            .orElseThrow { PlatformException(ErrorDescriptor.INVALID_BOOK_ID) }

        if (book.isReserved != true) {
            throw PlatformException(ErrorDescriptor.BOOK_NOT_RESERVED)
        }

        val bookingRecord = dataManager.load(BookingJournal::class.java)
            .query(
                """
            select bj from BookingJournal bj 
            where bj.book.id = :bookId and bj.user.id = :userId and bj.status = :reservedStatus 
            order by bj.start_time desc
            """.trimIndent()
            )
            .parameter("bookId", bookId)
            .parameter("userId", userId)
            .parameter("reservedStatus", Status.BOOKED)
            .maxResults(1)
            .optional()
            .orElseThrow {
                logger.error("Запись о бронировании не найдена: bookId=$bookId, userId=$userId, status=${Status.BOOKED}")
                PlatformException(ErrorDescriptor.BOOK_RETURN_FAILED)
            }

        logger.info("Запись о бронировании найдена: bookingRecordId=${bookingRecord.id}")

        if (bookingRecord.getStatus() != Status.BOOKED) {
            throw PlatformException(ErrorDescriptor.WRONG_BOOK_STATUS)
        }

        bookingRecord.end_time = endDate
        bookingRecord.photo = photoPath
        bookingRecord.comment = comment
        bookingRecord.setStatus(Status.RETURNED)
        dataManager.save(bookingRecord)

        book.isReserved = false
        dataManager.save(book)

        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("ru"))
        val formattedReturnDate = dateFormat.format(endDate)

        val notificationMessage = "Привет, ${bookingRecord.user?.username}! " +
                "Ваше бронирование книги ${book.name} завершено." +
                "Дата возврата: $formattedReturnDate."


        bookingRecord.user?.telegramId?.let { notificationService.sendMessage(it, notificationMessage) }

        return "Книга успешно возвращена"
    }

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    open fun offerBook(bookDTO: BookDTO, username: String): String {
        try {
            val user = dataManager.load(User::class.java)
                .condition(PropertyCondition.equal("username", username))
                .optional()
                .orElseThrow { IllegalStateException("Пользователь не найден.") }

            logger.debug("Поиск категории 'Не определена'...")
            val defaultCategory = dataManager.load(Category::class.java)
                .condition(PropertyCondition.equal("name", "Не определена"))
                .optional()
                .orElseGet {
                    logger.debug("Категория не найдена, создается новая...")
                    val newCategory = dataManager.create(Category::class.java).apply {
                        name = "Не определена"
                    }
                    dataManager.save(newCategory)
                    newCategory
                }

            logger.debug("Создается новая книга...")
            val newBook = dataManager.create(Book::class.java).apply {
                name = bookDTO.name
                author = bookDTO.author
                description = bookDTO.description
                cover = FileRef.fromString(bookDTO.cover)
                isUserBook = true
                this.userId = user.id
                category = defaultCategory
                year = null
                maxReservationPeriod = 7
                pageSize = null
                isReserved = false
                isPublished = false
                uploadDate = Date()
            }

            logger.debug("Создан новый объект Book: {}", newBook)

            requireNotNull(newBook.name) { "Название книги обязательно" }
            requireNotNull(newBook.author) { "Автор книги обязателен" }
            requireNotNull(newBook.description) { "Описание книги обязательно" }
            requireNotNull(newBook.cover) { "Фотография обложки обязательна" }

            logger.debug("Начинается сохранение книги...")
            newBook.isUserBook = true
            dataManager.save(newBook)

            logger.debug("Книга успешно сохранена: {}", newBook)

            return "Книга успешно предложена!"
        } catch (e: Exception) {
            logger.error("Ошибка при выполнении метода offerBook", e)
            throw e
        }
    }

}
