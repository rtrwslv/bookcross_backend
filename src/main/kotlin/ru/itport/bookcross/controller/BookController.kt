package ru.itport.bookcross.controller
import io.jmix.core.security.CurrentAuthentication
import org.springframework.web.bind.annotation.*
import ru.itport.bookcross.models.BaseResponse
import ru.itport.bookcross.models.BookDTO
import ru.itport.bookcross.models.ReserveBookRq
import ru.itport.bookcross.models.ReturnBookRq
import ru.itport.bookcross.services.BookService
import ru.itport.bookcross.utils.ControllerUtils.Companion.serviceCall
import java.util.*


@RestController
@CrossOrigin
@RequestMapping("custom/api/v1/books")
class BookController(
    private val bookService: BookService,
    private val currentAuthentication: CurrentAuthentication
) {

    @GetMapping("/")
    @ResponseBody
    fun getBooks(@RequestParam categoryId: String?): BaseResponse<List<BookDTO>> {
        return if (!categoryId.isNullOrBlank()) {
            serviceCall { (bookService.getBooksByCategory(UUID.fromString(categoryId))) }
        } else {
            serviceCall { (bookService.getAllBooks()) }
        }
    }

    @GetMapping("/{id}")
    fun getBookById(@PathVariable id: UUID): BaseResponse<BookDTO> {
        return serviceCall { bookService.getBookById(id) }
    }

    @PostMapping("/{id}/reserveBook")
    fun makeReservation(@RequestBody bookRq: ReserveBookRq): BaseResponse<String> {
        return serviceCall { bookService.reserveBook(bookRq.bookId, bookRq.startDate, bookRq.endDate) }
//        return bookService.reserveBook(bookRq.bookId, bookRq.startDate, bookRq.endDate)
    }

    @PostMapping("/{bookId}/returnBook")
    fun returnBook(@RequestBody bookRq: ReturnBookRq): BaseResponse<String> {
        return serviceCall { bookService.returnBook(bookRq.bookId, bookRq.endDate, bookRq.photoPath, bookRq.comment) }
//        return bookService.returnBook(bookRq.bookId, bookRq.endDate, bookRq.photoPath)
    }

    @PostMapping("/offerBook")
    fun offerBook(@RequestBody bookDTO: BookDTO): BaseResponse<String> {
        val userName = currentAuthentication.user.username
        return serviceCall { bookService.offerBook(bookDTO, userName) }
//        return bookService.offerBook(bookDTO, userId)
    }
}
