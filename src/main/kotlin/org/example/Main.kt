package org.example

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.seconds

fun main() = runBlocking {
    MemoryTestSever().use { server ->
        server.start()
        delay(duration = Int.MAX_VALUE.seconds)
    }
}