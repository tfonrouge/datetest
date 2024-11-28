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
import io.kvision.tabulator.Tabulator
import io.kvision.utils.Serialization
import io.kvision.utils.perc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.encodeToString
import model.Person

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
                tabulator = myTabulator {
                    width = 50.perc
                    onEvent {
                        rowSelectedTabulator = {
                            self.getSelectedData().let {
                                it.firstOrNull()?.let { person ->
                                    personFormPanel.setData(person)
                                    val s = Serialization.plain.encodeToString(person)
//                                    val s = Json.encodeToString(Person.serializer(), person)
                                    console.warn("s", s)
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
