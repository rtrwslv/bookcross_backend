package ru.itport.bookcross.entity

import io.jmix.core.entity.annotation.JmixGeneratedValue
import io.jmix.core.metamodel.annotation.InstanceName
import io.jmix.core.metamodel.annotation.JmixEntity
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import java.util.*

@JmixEntity
@Table(name = "TELEGRAM_CHAT", indexes = [
    Index(name = "IDX_TELEGRAM_CHAT_LOCATION_ID", columnList = "LOCATION_ID")
])
@Entity
open class TelegramChat {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    var id: UUID? = null

    @Column(name = "CHAT_ID", nullable = false, length = 20)
    @NotNull
    var chatID: String? = null

    @InstanceName
    @Column(name = "NAME", nullable = false, length = 20)
    @NotNull
    var name: String? = null

    @JoinColumn(name = "LOCATION_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    var location: Location? = null

    @Column(name = "COMMENT")
    var comment: String? = null

    @Column(name = "VERSION", nullable = false)
    @Version
    var version: Int? = null

}