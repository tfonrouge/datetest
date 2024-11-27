package com.example.datetest

import io.kvision.annotations.KVService
import io.kvision.remote.RemoteData
import io.kvision.remote.RemoteFilter
import io.kvision.remote.RemoteSorter
import io.kvision.types.OffsetDateTime
import kotlinx.serialization.KSerializer
import model.Person

@KVService
interface IPersonService {
    suspend fun rowData(
        page: Int?,
        size: Int?,
        filter: List<RemoteFilter>?,
        sorter: List<RemoteSorter>?,
        state: String?
    ): RemoteData<Person>

    suspend fun updateBirthday(id: Int, birthday: OffsetDateTime): Boolean
}

expect object OffsetDateTimeSerializer : KSerializer<OffsetDateTime>