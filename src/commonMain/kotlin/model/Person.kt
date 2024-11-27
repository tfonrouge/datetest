package model

import com.example.datetest.OffsetDateTimeSerializer
import io.kvision.types.OffsetDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Person(
    val id: Int,
    val name: String,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val birthday: OffsetDateTime,
)
