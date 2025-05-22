package ru.itport.bookcross.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import io.jmix.core.FileRef
import java.util.*

class FileRefDeserializer : JsonDeserializer<FileRef>() {
    override fun deserialize(parser: JsonParser, context: DeserializationContext): FileRef {
        val value = parser.text
        return FileRef.fromString(value)
    }
}

data class ReturnBookRq(
    @JsonProperty("bookId")
    val bookId: UUID,
    @JsonProperty("endDate")
    val endDate: Date,
    @JsonDeserialize(using = FileRefDeserializer::class)
    val photoPath: FileRef,
    @JsonProperty("comment")
    val comment: String,

)