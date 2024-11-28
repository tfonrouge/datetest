package com.example.datetest

import io.kvision.core.Container
import io.kvision.remote.getServiceManager
import io.kvision.tabulator.*
import io.kvision.toast.Toast
import io.kvision.utils.Serialization
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.serializer
import model.Person
import model.PersonFilter
import kotlin.js.Date
import kotlin.js.json

fun Container.myTabulator(init: TabulatorRemote<Person, IPersonService>.() -> Unit): TabulatorRemote<Person, IPersonService> {
    lateinit var tab: TabulatorRemote<Person, IPersonService>
    tab = tabulatorRemote<Person, IPersonService>(
        serviceManager = getServiceManager(),
        function = IPersonService::rowData,
        serializer = serializer(),
        stateFunction = { Serialization.plain.encodeToString(PersonFilter(birthday = Date())) },
        options = TabulatorOptions(
            layout = Layout.FITCOLUMNS,
            pagination = true,
            paginationMode = PaginationMode.REMOTE,
            filterMode = FilterMode.REMOTE,
            sortMode = SortMode.REMOTE,
            selectableRows = 1,
            columns = listOf(
                ColumnDefinition(
                    title = "",
                    field = "selectedRow",
                    formatter = Formatter.ROWSELECTION,
                    width = "2.rem"
                ),
                ColumnDefinition(
                    title = "Id",
                    field = Person::_id.name,
                ),
                ColumnDefinition(
                    title = "Name",
                    field = Person::name.name,
                ),
                ColumnDefinition(
                    title = "Birthday",
                    field = Person::birthday.name,
                    formatter = Formatter.DATETIME,
                    formatterParams = json(
                        "inputFormat" to "iso",
                        "outputFormat" to "EEE dd MMM y HH:mm",
                        "invalidPlaceholder" to "(invalid date)",
                    ),
                    editor = Editor.DATETIME,
                    editorParams = json(
                        "format" to "iso"
                    ),
                    cellEdited = { cell ->
                        val person = tab.toKotlinObj(cell.getData())
                        Toast.info("updating birthday with ${person.birthday}")
                        AppScope.launch {
                            PersonService().updateBirthday(person._id, person.birthday).also {
                                if (it) Toast.success("birthday updated") else Toast.danger("birthday update failed")
                            }
                        }
                    }
                )
            )
        ),
        init = init
    )
    return tab
}