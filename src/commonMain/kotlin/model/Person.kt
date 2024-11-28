package model

import io.kvision.types.OffsetDateTime
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class Person(
    val _id: Int,
    val name: String,
//    @Serializable(with = OffsetDateTimeSerializer::class)
    @Contextual val birthday: OffsetDateTime,
)
