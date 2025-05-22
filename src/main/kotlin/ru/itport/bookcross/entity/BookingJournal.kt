package ru.itport.bookcross.entity

import io.jmix.core.DeletePolicy
import io.jmix.core.FileRef
import io.jmix.core.entity.annotation.JmixGeneratedValue
import io.jmix.core.entity.annotation.OnDeleteInverse
import io.jmix.core.metamodel.annotation.JmixEntity
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import java.util.*

@JmixEntity
@Table(name = "BOOKING_JOURNAL", indexes = [
    Index(name = "IDX_BOOKING_JOURNAL_BOOK", columnList = "BOOK_ID"),
    Index(name = "IDX_BOOKING_JOURNAL_USER", columnList = "USER_ID")
])
@Entity
open class BookingJournal {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    var id: UUID? = null

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @JoinColumn(name = "USER_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    var user: User? = null

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @JoinColumn(name = "BOOK_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    var book: Book? = null

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "START_TIME")
    var start_time: Date? = null

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "END_TIME")
    var end_time: Date? = null

    @Column(name = "STATUS")
    private var status: String? = null

    @Column(name = "PHOTO", length = 1024)
    var photo: FileRef? = null

    @Column(name = "COMMENT")
    var comment: String? = null

    fun getStatus(): Status? = status?.let { Status.fromId(it) }

    fun setStatus(status: Status?) {
        this.status = status?.id
    }
}