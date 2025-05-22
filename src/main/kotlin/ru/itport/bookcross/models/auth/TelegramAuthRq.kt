package ru.itport.bookcross.models.auth

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaGetter

@JsonIgnoreProperties(ignoreUnknown = true)
data class TelegramAuthRq(
    @JsonProperty("auth_date")
    val authDate: Long,

    @JsonProperty("first_name")
    val firstName: String?,

    @JsonProperty("last_name")
    val lastName: String?,

    val hash: String,

    @JsonProperty("id")
    val telegramId: String,

    @JsonProperty("photo_url")
    val photoUrl: String?,

    val username: String?
)

fun Any.forceSerialize(separator: String, sorted: Boolean = false): String {
    var fieldNameToAnnotatedNameMap = this.javaClass.declaredFields.map { it.name }.associateWith { fieldName ->
        val jsonFieldName =
            this::class.primaryConstructor?.parameters?.first { it.name == fieldName }?.annotations?.firstOrNull { it is JsonProperty }
        val serializedName = if (jsonFieldName != null) (jsonFieldName as JsonProperty).value else fieldName
        serializedName
    }
    if (sorted)
        fieldNameToAnnotatedNameMap = fieldNameToAnnotatedNameMap.toList().sortedBy { (_, value) -> value}.toMap()
    return fieldNameToAnnotatedNameMap.entries.joinToString(separator) { e ->
        val field = this::class.memberProperties.first { it.name == e.key }
        "${e.value}=${field.javaGetter?.invoke(this)}"
    }
}

