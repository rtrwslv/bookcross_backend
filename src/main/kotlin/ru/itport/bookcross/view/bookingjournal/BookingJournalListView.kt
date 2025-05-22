package ru.itport.bookcross.view.bookingjournal

import com.vaadin.flow.router.Route
import io.jmix.flowui.view.*
import ru.itport.bookcross.entity.BookingJournal
import ru.itport.bookcross.view.main.MainView


@Route(value = "bookingJournals", layout = MainView::class)
@ViewController(id = "BookingJournal.list")
@ViewDescriptor(path = "booking-journal-list-view.xml")
@LookupComponent("bookingJournalsDataGrid")
@DialogMode(width = "64em")
class BookingJournalListView : StandardListView<BookingJournal>() {
}