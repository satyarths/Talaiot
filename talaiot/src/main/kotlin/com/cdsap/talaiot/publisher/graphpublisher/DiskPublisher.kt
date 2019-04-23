package com.cdsap.talaiot.publisher.graphpublisher

import com.cdsap.talaiot.logger.LogTracker
import com.cdsap.talaiot.publisher.Publisher
import com.cdsap.talaiot.writer.FileWriter


/**
 *
 */
interface DiskPublisher : Publisher {
    var logTracker: LogTracker
    var fileWriter: FileWriter
}
