package ru.itport.bookcross.entity

import io.jmix.core.metamodel.datatype.EnumClass

enum class Status(private val id: String) : EnumClass<String> {
    AVAILABLE("A"),
    BOOKED("B"),
    RETURNED("C");

    override fun getId() = id

    companion object {

        @JvmStatic
        fun fromId(id: String): Status? = entries.find { it.id == id }
    }
}