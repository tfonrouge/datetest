package model

import io.kvision.types.OffsetDateTime
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class PersonFilter(
    @Contextual val birthday: OffsetDateTime? = null,
)
