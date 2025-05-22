package ru.itport.bookcross.view.book

import com.vaadin.flow.router.Route
import io.jmix.flowui.view.EditedEntityContainer
import io.jmix.flowui.view.StandardDetailView
import io.jmix.flowui.view.ViewController
import io.jmix.flowui.view.ViewDescriptor
import ru.itport.bookcross.entity.Book
import ru.itport.bookcross.view.main.MainView

@Route(value = "books/:id", layout = MainView::class)
@ViewController(id = "Book.detail")
@ViewDescriptor(path = "book-detail-view.xml")
@EditedEntityContainer("bookDc")
class BookDetailView : StandardDetailView<Book>() {
}