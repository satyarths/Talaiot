package com.cdsap.talaiot.publisher.graphpublisher

import com.cdsap.talaiot.entities.TaskMeasurementAggregated
import com.cdsap.talaiot.logger.LogTracker
import com.cdsap.talaiot.publisher.graphpublisher.resources.ResourcesHtml
import com.cdsap.talaiot.publisher.graphpublisher.resources.ResourcesHtml.LEGEND_HEADER
import com.cdsap.talaiot.writer.FileWriter
import java.util.concurrent.Executor

class HtmlPublisher(
    override var logTracker: LogTracker,
    override var fileWriter: FileWriter,
    private val executor: Executor
) : DefaultDiskPublisher(logTracker, fileWriter) {
    private val fileName: String = "htmlTaskDependency.html"

    override fun publish(taskMeasurementAggregated: TaskMeasurementAggregated) {
        executor.execute {
            val content = contentComposer(
                task = buildGraph(taskMeasurementAggregated),
                legend = legend(taskMeasurementAggregated),
                header = ResourcesHtml.HEADER,
                footer = ResourcesHtml.FOOTER
            )
            logTracker.log("HtmlPublisher: writing file")
            writeFile(content, fileName)
        }
    }

    override fun formatNode(
        internalId: Int,
        module: String,
        taskName: String,
        numberDependencies: Int,
        cached: Boolean
    ): String = "      nodes.push({id: $internalId, title:'$taskName', group:'$module', " +
            "label: '$taskName', " +
            "value: $numberDependencies});\n"

    override fun formatEdge(
        from: Int,
        to: Int?
    ): String = "      edges.push({from: $from, to: $to});\n"

    private fun legend(taskMeasurementAggregated: TaskMeasurementAggregated): String {
        var count = 10000
        var nodes = LEGEND_HEADER
        taskMeasurementAggregated.taskMeasurement.distinctBy {
            it.module
        }.forEach {
            count++
            nodes += "      nodes.push({id: $count, x: x, y: y, label: '${it.module}', group: '${it.module}', value: 1, " +
                    "fixed: true, physics:false});\n"
        }
        return nodes
    }


}
