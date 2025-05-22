package ru.itport.bookcross.view.bookingjournal

import com.vaadin.flow.router.Route
import io.jmix.flowui.view.EditedEntityContainer
import io.jmix.flowui.view.StandardDetailView
import io.jmix.flowui.view.ViewController
import io.jmix.flowui.view.ViewDescriptor
import ru.itport.bookcross.entity.BookingJournal
import ru.itport.bookcross.view.main.MainView

@Route(value = "bookingJournals/:id", layout = MainView::class)
@ViewController(id = "BookingJournal.detail")
@ViewDescriptor(path = "booking-journal-detail-view.xml")
@EditedEntityContainer("bookingJournalDc")
class BookingJournalDetailView : StandardDetailView<BookingJournal>() {
}