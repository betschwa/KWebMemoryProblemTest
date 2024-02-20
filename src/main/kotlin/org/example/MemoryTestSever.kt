package org.example

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kweb.InputType
import kweb.Kweb
import kweb.div
import kweb.h3
import kweb.input
import kweb.label
import kweb.plugins.fomanticUI.fomantic
import kweb.plugins.fomanticUI.fomanticUIPlugin
import kweb.route
import kweb.state.KVar
import kweb.state.render
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class MemoryTestSever(private val port: Int = 8080) : AutoCloseable {

    private val scope = CoroutineScope(context = Dispatchers.IO)
    private lateinit var server: Kweb
    private lateinit var webServerMemoryLoadJob: Job
    private lateinit var randomDataJob: Job

    suspend fun start() {
        val webServerMemoryLoad = KVar(initialValue = MemoryLoad())
        webServerMemoryLoadJob = scope.launch {
            val runtime = Runtime.getRuntime()

            while (isActive) {
                val load = MemoryLoad(max = runtime.maxMemory(),
                                      free = runtime.freeMemory(),
                                      total = runtime.totalMemory())

                webServerMemoryLoad.value = load

                delay(duration = 1.seconds)
            }
        }

        val randomData = KVar(initialValue = 0.0)
        randomDataJob = scope.launch {
            while (isActive) {
                randomData.value = Random.nextDouble(from = -Double.MAX_VALUE,
                                                     until = Double.MAX_VALUE)

                delay(duration = 100.milliseconds)
            }
        }

        server = Kweb(port = port,
                      debug = false,
                      plugins = listOf(fomanticUIPlugin)) {
            doc.body {
                route {
                    path(template = "/") {
                        div(attributes = fomantic.ui.container) {
                            div(attributes = fomantic.ui.grid) {
                                div(attributes = fomantic.eight.wide.column) {
                                    div(attributes = fomantic.ui.form) {
                                        h3(attributes = fomantic.ui.dividing.header).addText(value = "Memory")

                                        div(attributes = fomantic.field) {
                                            label().addText(value = "Used [MB]")
                                            div(attributes = fomantic.ui.input) {
                                                render(webServerMemoryLoad) { memoryLoad ->
                                                    input(type = InputType.text) { element ->
                                                        element.setReadOnly(true)
                                                    }.value.value = memoryLoad.usedText
                                                }
                                            }
                                        }
                                        div(attributes = fomantic.field) {
                                            label().addText(value = "Free [MB]")
                                            div(attributes = fomantic.ui.input) {
                                                render(webServerMemoryLoad) { memoryLoad ->
                                                    input(type = InputType.text) { element ->
                                                        element.setReadOnly(true)
                                                    }.value.value = memoryLoad.freeText
                                                }
                                            }
                                        }
                                        div(attributes = fomantic.field) {
                                            label().addText(value = "Total [MB]")
                                            div(attributes = fomantic.ui.input) {
                                                render(webServerMemoryLoad) { memoryLoad ->
                                                    input(type = InputType.text) { element ->
                                                        element.setReadOnly(true)
                                                    }.value.value = memoryLoad.totalText
                                                }
                                            }
                                        }
                                        div(attributes = fomantic.field) {
                                            label().addText(value = "Max. [MB]")
                                            div(attributes = fomantic.ui.input) {
                                                render(webServerMemoryLoad) { memoryLoad ->
                                                    input(type = InputType.text) { element ->
                                                        element.setReadOnly(true)
                                                    }.value.value = memoryLoad.maxText
                                                }
                                            }
                                        }
                                    }
                                }
                                div(attributes = fomantic.eight.wide.column) {
                                    div(attributes = fomantic.ui.form) {
                                        h3(attributes = fomantic.ui.dividing.header).addText(value = "Random Data")

                                        div(attributes = fomantic.field) {
                                            label().addText(value = "Random Double")
                                            div(attributes = fomantic.ui.input) {
                                                render(randomData) { randomData ->
                                                    input(type = InputType.text) { element ->
                                                        element.setReadOnly(true)
                                                    }.value.value = randomData.toString()
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun close() {
        webServerMemoryLoadJob.cancel()
        randomDataJob.cancel()
        scope.cancel()
        server.close()
    }
}