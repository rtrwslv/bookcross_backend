package ru.itport.bookcross.exception

enum class ErrorDescriptor(val code: String, val message: String) {
    INTERNAL_ERROR("SERVER_ERROR", "Возника ошибка при выполнении запроса"),
    SERVER_ERROR("SERVER_ERROR", "Ошибка сервера"),
    INVALID_BOOK_ID("INVALID_BOOK", "Книга не существует"),
    INVALID_CATEGORY_ID("INVALID_CATEGORY", "Категория не существует"),
    BOOK_ALREADY_RESERVED("BOOK_ALREADY_RESERVED", "Книга уже забронирована"),
    BOOK_NOT_RESERVED("BOOK_NOT_RESERVED", "Книга не была забронирована"),
    BOOK_RETURN_FAILED("BOOK_RETURN_FAILED", "Не удалось вернуть книгу"),
    INVALID_DATE_RANGE("INVALID_DATE_RANGE", "Ошибка ввода даты"),
    WRONG_BOOK_STATUS("WRONG_BOOK_STATUS", "Неверный статус книги"),
    PHOTO_NOT_FOUND("PHOTO_NOT_FOUND", "PHOTO_NOT_FOUND"),
    INVALID_BOOKING_ID("INVALID_BOOKING_ID", "Неверный ID бронирования"),
    INVALID_USER_ID("INVALID_USER_ID", "Неверный ID пользователя"),
}
