package com.bytelegend.utils

import com.bytelegend.app.shared.objects.GameMapMission
import com.bytelegend.app.shared.objects.GameMapRegion
import com.bytelegend.github.utils.generated.TiledMap
import java.io.File

data class RoadmapJsonData(
    val nodes: List<RoadmapMissionNode>,
    val links: List<RoadmapMissionLink>,
    val categories: List<RoadmapMissionRegion>
)

data class RoadmapMissionNode(
    val id: String,
    val name: String,
    val symbolSize: Number,
    val x: Int,
    val y: Int,
    val value: Int,
    val category: Int
)

data class RoadmapMissionLink(
    val source: String,
    val target: String,
    // https://github.com/apache/echarts/issues/4062
    val label: Map<String, Any> = mapOf("show" to true, "formatter" to "fuck")
)

data class RoadmapMissionRegion(
    val name: String
)

class RoadmapJsonGenerator(
    val tiledMap: TiledMap,
    val regions: List<GameMapRegion>,
    val missions: List<GameMapMission>,
    private val outputJson: File
) {
    fun generate() {
        val categories = regions.map {
            RoadmapMissionRegion(it.id)
        }

        val nodes = missions.map {
            RoadmapMissionNode(
                it.id,
                it.title,
                20,
                it.gridCoordinate.x * 32,
                it.gridCoordinate.y * 32,
                123,
                0
            )
        }

        val links = mutableListOf<RoadmapMissionLink>()

        missions.forEach {
            if (it.next != null) {
                links.add(RoadmapMissionLink(it.id, it.next!!))
            }
        }

        prettyObjectMapper.writeValue(outputJson, RoadmapJsonData(nodes, links, categories))
    }


}
