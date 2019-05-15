package com.cdsap.talaiot.logger

/**
 * Logger interface
 */
interface LogTracker {
    /**
     * Main modes of for the LogTrackers implementations.
     */
    enum class Mode {
        SILENT,
        INFO
    }

    fun log(message: String)
}