package org.example.org.example

private const val FORMAT = "%,10d"
private const val MEGA = 1024 * 1024

data class MemoryLoad(val max: Long = 0L,
                      val free: Long = 0L,
                      val total: Long = 0L) {

    val used: Long by lazy { total - free }
    val usedRelative: Double by lazy {
        used.toDouble() / max.toDouble()
    }

    val maxText: String by lazy {
        FORMAT.format(max / MEGA)
    }

    val freeText: String by lazy {
        FORMAT.format(free / MEGA)
    }

    val totalText: String by lazy {
        FORMAT.format(total / MEGA)
    }

    val usedText: String by lazy {
        FORMAT.format(used / MEGA)
    }
}
