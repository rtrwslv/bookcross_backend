package ru.itport.bookcross.entity

import io.jmix.core.DeletePolicy
import io.jmix.core.entity.annotation.JmixGeneratedValue
import io.jmix.core.entity.annotation.OnDeleteInverse
import io.jmix.core.metamodel.annotation.JmixEntity
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import java.util.*

@JmixEntity
@Table(name = "USER_PROFILE", indexes = [
    Index(name = "IDX_USER_PROFILE_USER", columnList = "USER_ID"),
    Index(name = "IDX_USER_PROFILE_CITY", columnList = "CITY_ID")
])
@Entity
open class UserProfile {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    var id: UUID? = null

    @OnDeleteInverse(DeletePolicy.UNLINK)
    @JoinColumn(name = "USER_ID")
    @OneToOne(fetch = FetchType.LAZY)
    var user: User? = null

    @Column(name = "VERSION", nullable = false)
    @Version
    var version: Int? = null

    @Column(name = "TELEGRAM_ID", nullable = false, length = 20)
    @NotNull
    var telegramId: String? = null

    @OnDeleteInverse(DeletePolicy.UNLINK)
    @JoinColumn(name = "CITY_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    var city: Location? = null
}