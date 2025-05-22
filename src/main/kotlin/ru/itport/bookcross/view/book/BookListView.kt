package ru.itport.bookcross.view.book

import com.vaadin.flow.router.Route
import io.jmix.flowui.view.*
import ru.itport.bookcross.entity.Book
import ru.itport.bookcross.view.main.MainView


@Route(value = "books", layout = MainView::class)
@ViewController(id = "Book.list")
@ViewDescriptor(path = "book-list-view.xml")
@LookupComponent("booksDataGrid")
@DialogMode(width = "64em")
class BookListView : StandardListView<Book>() {
}