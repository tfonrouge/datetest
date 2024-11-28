package com.example.datetest

import io.kvision.remote.RemoteData
import io.kvision.remote.RemoteFilter
import io.kvision.remote.RemoteSorter
import model.Person
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import org.litote.kmongo.set
import org.litote.kmongo.setTo
import java.time.OffsetDateTime

actual class PersonService : IPersonService {
    companion object {
        val collection = KMongo.createClient("mongodb://test:test@localhost:27017/test")
            .getDatabase("test").getCollection("persons", Person::class.java)
        val firstPersons = mutableListOf(
            Person(_id = 1, name = "Lucy", birthday = OffsetDateTime.parse("2013-09-13T00:00:00-06:00")),
            Person(_id = 2, name = "Nathalie", birthday = OffsetDateTime.parse("2014-10-25T00:00:00-06:00")),
            Person(_id = 3, name = "Michal", birthday = OffsetDateTime.parse("2016-09-19T00:00:00-06:00")),
        )
    }

    override suspend fun rowData(
        page: Int?,
        size: Int?,
        filter: List<RemoteFilter>?,
        sorter: List<RemoteSorter>?,
        state: String?
    ): RemoteData<Person> {
        if (collection.coroutine.countDocuments() == 0L) {
            val r = collection.coroutine.insertMany(firstPersons)
            println(r)
        }
        val persons = collection.coroutine.find()
            .skip((page?.minus(1) ?: 0) * (size ?: 10))
            .limit(size ?: 10)
            .toList()
//        val s = Json.encodeToString(ListSerializer(Person.serializer()), persons)
//        println(s)
        return RemoteData(
            data = persons,
            last_page = 1,
            last_row = persons.size
        )
    }

    override suspend fun updateBirthday(id: Int, birthday: OffsetDateTime): Boolean {
        val r = collection.coroutine.updateOneById(id, set(Person::birthday setTo birthday))
        return r.wasAcknowledged()
    }
}
