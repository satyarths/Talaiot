package com.cdsap.talaiot.publisher.graphpublisher


import com.cdsap.talaiot.entities.TaskMeasurementAggregated
import com.cdsap.talaiot.entities.TaskMessageState
import com.cdsap.talaiot.logger.LogTracker
import com.cdsap.talaiot.writer.FileWriter
import guru.nidi.graphviz.attribute.RankDir
import guru.nidi.graphviz.engine.*
import guru.nidi.graphviz.model.Factory.*
import guru.nidi.graphviz.model.Node
import guru.nidi.graphviz.model.LinkSource
import java.util.concurrent.Executor

/**
 * Publisher used to publish with the Dot format. It's using Graphviz to render the graph
 */
class DotPublisher(
    override var logTracker: LogTracker,
    override var fileWriter: FileWriter,
    /**
     * Executor to schedule a task in Background
     */
    private val executor: Executor
) : DiskPublisher {
    /**
     * name of the file generated by the publisher
     */
    private val fileName: String = "dotTaskDependency.png"

    /**
     * DotPublisher follows a different approach of the DefaultDiskPublisher. It doesn't require to compose the different
     * parts of the file. The library expect a list of LinkSources to build the graph.
     * This function builds the list of LinkSources required.
     *
     * @param measurementAggregated Aggregated entity with the results of the build
     *
     * @return a list of LinkSources representing the relations of the tasks/nodes.
     */
    private fun getLinkSources(measurementAggregated: TaskMeasurementAggregated): List<LinkSource> {
        val mapNodes = mutableMapOf<String, Node>()
        val nodes = mutableListOf<LinkSource>()

        measurementAggregated.taskMeasurement.forEach {

            mapNodes[it.taskPath] = node(it.taskPath)

            val node = node(it.taskPath).with("module", it.module)
                .with("cached", it.state == TaskMessageState.FROM_CACHE)

            it.taskDependencies.forEach { dependency ->
                mapNodes[dependency]?.let { n ->
                    nodes.add(n.link(node))
                }

            }
        }
        return nodes.toList()
    }

    override fun publish(taskMeasurementAggregated: TaskMeasurementAggregated) {
        executor.execute {
            try {
                logTracker.log("DotPublisher: creating graph")
                val g = graph("Talaiot").directed()
                    .strict()
                    .graphAttr().with(RankDir.TOP_TO_BOTTOM)
                    .with(getLinkSources(taskMeasurementAggregated))

                logTracker.log("DotPublisher: writing png")
                fileWriter.prepareFile(
                    Graphviz.fromGraph(g).engine(Engine.DOT).fontAdjust(0.90)
                        .render(Format.PNG), fileName
                )

            } catch (e: Exception) {
                logTracker.log("DotPublisher: Error -> ${e.message}")
            }
        }
    }
}
