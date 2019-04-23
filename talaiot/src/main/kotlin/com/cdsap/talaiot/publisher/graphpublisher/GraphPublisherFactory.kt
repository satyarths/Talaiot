package com.cdsap.talaiot.publisher.graphpublisher

import com.cdsap.talaiot.logger.LogTracker
import com.cdsap.talaiot.writer.FileWriter
import java.util.concurrent.Executor

interface GraphPublisherFactory {
    fun createPublisher(
        graphType: GraphPublisherType,
        logTracker: LogTracker,
        fileWriter: FileWriter,
        executor: Executor
    ): DiskPublisher
}