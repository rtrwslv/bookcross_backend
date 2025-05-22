package ru.itport.bookcross.entity

import io.jmix.core.DeletePolicy
import io.jmix.core.FileRef
import io.jmix.core.entity.annotation.JmixGeneratedValue
import io.jmix.core.entity.annotation.OnDeleteInverse
import io.jmix.core.metamodel.annotation.InstanceName
import io.jmix.core.metamodel.annotation.JmixEntity
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import java.util.*


@JmixEntity
@Table(name = "BOOK", indexes = [
    Index(name = "IDX_BOOK_CATEGORY", columnList = "CATEGORY_ID")
])
@Entity
open class Book {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    var id: UUID? = null

    @Column(name = "USER_ID")
    var userId: UUID? = null

    @InstanceName
    @Column(name = "TITLE", nullable = false)
    @NotNull
    var name: String? = null

    @Column(name = "AUTHOR", nullable = false)
    var author: String? = null

    @Column(name = "YEAR")
    var year: Int? = null

    @OnDeleteInverse(DeletePolicy.UNLINK)
    @JoinColumn(name = "CATEGORY_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    var category: Category? = null

    @Lob
    @Column(name = "DESCRIPTION", nullable = false)
    var description: String? = null

    @Column(name = "COVER", nullable = false, length = 1024)
    var cover: FileRef? = null

    @Temporal(TemporalType.DATE)
    @Column(name = "UPLOAD_DATE", nullable = false)
    @NotNull
    var uploadDate: Date? = null

    @Column(name = "MAX_RESERVATION_PERIOD")
    var maxReservationPeriod: Int? = null

    @Column(name = "PAGE_SIZE")
    var pageSize: Int? = null

    @Column(name = "IS_RESERVED", nullable = false)
    @NotNull
    var isReserved: Boolean? = false

    @Column(name = "IS_PUBLISHED", nullable = false)
    @NotNull
    var isPublished: Boolean? = false

    @Column(name = "IS_USER_BOOK", nullable = false)
    @NotNull
    var isUserBook: Boolean? = false

    @PrePersist
    open fun initializeDefaultValue() {
        if (isPublished == null) {
            isPublished = true
        }
    }

}
