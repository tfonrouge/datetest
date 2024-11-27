package com.example.datetest

import io.kvision.remote.RemoteData
import io.kvision.remote.RemoteFilter
import io.kvision.remote.RemoteSorter
import model.Person
import java.time.OffsetDateTime

actual class PersonService : IPersonService {

    companion object {
        val persons = mutableListOf(
            Person(id = 1, name = "Lucy", birthday = OffsetDateTime.parse("2013-09-13T00:00:00-06:00")),
            Person(id = 2, name = "Nathalie", birthday = OffsetDateTime.parse("2014-10-25T00:00:00-06:00")),
            Person(id = 3, name = "Michal", birthday = OffsetDateTime.parse("2016-09-19T00:00:00-06:00")),
        )
    }

    override suspend fun rowData(
        page: Int?,
        size: Int?,
        filter: List<RemoteFilter>?,
        sorter: List<RemoteSorter>?,
        state: String?
    ): RemoteData<Person> {
        return RemoteData(
            data = persons,
            last_page = 1,
            last_row = persons.size
        )
    }

    override suspend fun updateBirthday(id: Int, birthday: OffsetDateTime): Boolean {
        return persons.firstOrNull { it.id == id }?.let { person ->
            persons.remove(person)
            persons.add(person.copy(birthday = birthday))
            persons.sortBy { it.id }
            true
        } ?: false
    }
}
