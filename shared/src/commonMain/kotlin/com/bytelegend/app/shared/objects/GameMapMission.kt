package com.bytelegend.app.shared.objects

import com.bytelegend.app.shared.GridCoordinate
import com.bytelegend.app.shared.entities.mission.MapMissionSpec
import kotlinx.serialization.Serializable

/**
 * GameMapMission doesn't contain all information of missions, just partial of them
 * (id, title) to display them. The whole information is loaded via AJAX.
 */
class GameMapMission(
    override val id: String,
    val title: String,
    val totalStar: Int,
    override val map: String,
    override val sprite: String,
    override val gridCoordinate: GridCoordinate,
    override val children: List<String>,
    // Next mission id
    override val next: String?,
    val region: String?,
) : GameMapObject, MapMissionSpec, GridCoordinateAware {
    override val layer: Int = 0
    override val type: GameMapObjectType = GameMapObjectType.GameMapMission
    override fun compress() = CompressedGameMapMission(
        id,
        title,
        totalStar,
        map,
        sprite,
        gridCoordinate.toCompressedList(),
        children,
        next,
        region
    )

    override val roadmap: Boolean = true
}

@Serializable
data class CompressedGameMapMission(
    override val id: String,
    val title: String,
    val totalStar: Int,
    val map: String,
    val sprite: String,
    val point: List<Int>,
    val children: List<String>,
    val next: String? = null,
    val region: String? = null
) : CompressedGameMapObject {
    override val layer: Int = 0
    override val type: Int = GameMapObjectType.GameMapMission.index

    override fun decompress() = GameMapMission(
        id,
        title,
        totalStar,
        map,
        sprite,
        GridCoordinate(point),
        children,
        next,
        region,
    )
}
