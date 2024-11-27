package com.example.datetest

import io.kvision.*
import io.kvision.core.AlignItems
import io.kvision.core.onEvent
import io.kvision.form.FormPanel
import io.kvision.form.formPanel
import io.kvision.form.text.text
import io.kvision.form.time.dateTime
import io.kvision.panel.root
import io.kvision.panel.vPanel
import io.kvision.remote.getServiceManager
import io.kvision.tabulator.*
import io.kvision.toast.Toast
import io.kvision.utils.perc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.serialization.serializer
import model.Person
import kotlin.js.json

val AppScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

class App : Application() {

    override fun start(state: Map<String, Any>) {
//        KV_DEFAULT_DATE_FORMAT = "isoDateTime"
        lateinit var personFormPanel: FormPanel<Person>
        lateinit var tabulator: Tabulator<Person>
        root("kvapp") {
            vPanel(alignItems = AlignItems.CENTER) {
                personFormPanel = formPanel<Person> {
                    text(label = "Name") { disabled = true }.bind(Person::name)
                    dateTime(label = "Birthday") { disabled = true }.bind(Person::birthday)
                }
                tabulator = tabulatorRemote(
                    serviceManager = getServiceManager(),
                    function = IPersonService::rowData,
                    serializer = serializer(),
                    stateFunction = { "test" },
                    options = TabulatorOptions(
                        layout = Layout.FITCOLUMNS,
                        pagination = true,
                        paginationMode = PaginationMode.REMOTE,
                        paginationSize = 3,
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
                                field = Person::id.name,
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
                                    val person = tabulator.toKotlinObj(cell.getData())
                                    Toast.info("updating birthday with ${person.birthday}")
                                    AppScope.launch {
                                        PersonService().updateBirthday(person.id, person.birthday).also {
                                            if (it) Toast.success("birthday updated") else Toast.danger("birthday update failed")
                                        }
                                    }
                                }
                            )
                        )
                    )
                ) {
                    width = 50.perc
                    onEvent {
                        rowSelectedTabulator = {
                            self.getSelectedData().let {
                                if (it.isNotEmpty()) {
                                    personFormPanel.setData(it.first())
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun main() {
    startApplication(
        ::App,
        module.hot,
        BootstrapModule,
        BootstrapCssModule,
        DatetimeModule,
        TabulatorModule,
        TabulatorCssBootstrapModule,
        CoreModule
    )
}
