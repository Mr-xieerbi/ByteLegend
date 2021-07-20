package com.bytelegend.client.app.ui

import com.bytelegend.app.client.api.GameScene
import com.bytelegend.app.shared.objects.GameMapRegion
import com.bytelegend.app.shared.objects.GameObjectRole
import com.bytelegend.client.app.engine.GameMission
import kotlinext.js.assign
import kotlinext.js.jsObject
import kotlinx.browser.window
import org.w3c.dom.HTMLElement
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.div
import react.dom.jsStyle

interface EChartsRoadmapProps : RProps {
    var zIndex: Int
    var width: Int
    var height: Int
    var top: Int
    var left: Int

    // light/dark
    var theme: String

    // svg/canvas
    var renderer: String

    var gameScene: GameScene
}

interface EChartsRoadmapState : RState {
    var option: dynamic
}


val baseMinimapSeries: dynamic = JSON.parse(
    """
    {
        "type": "graph",
        "tooltip": { "show": false }
    }
""".trimIndent()
)

fun nativeJsArrayOf(vararg args: dynamic): dynamic {
    val ret = js("[]")
    for (arg in args) {
        ret.push(arg)
    }
    return ret
}

fun GameScene.getRoadmapMapFeatures(): dynamic {
    val regions: List<GameMapRegion> = objects.getByRole(GameObjectRole.MapRegion)
    val features = js("[]")
    val width = map.pixelSize.width
    val height = map.pixelSize.height
    for (region in regions) {
        // https://cdn.jsdelivr.net/gh/apache/echarts-website@asf-site/examples/data/asset/geo/HK.json
        // https://datatracker.ietf.org/doc/html/rfc7946
        val properties: dynamic = jsObject {
            name = region.id
        }
        val coordinateArray: dynamic = js("[]")
        for (point in region.vertices) {
            coordinateArray.push(nativeJsArrayOf(point.x, height - point.y))
        }
        val coordinateArrayArray = nativeJsArrayOf(coordinateArray)
        val geometry: dynamic = jsObject {
            type = "Polygon"
            coordinates = coordinateArrayArray
        }
        val feature: dynamic = jsObject {
            id = region.id
            type = "Feature"
            this.properties = properties
            this.geometry = geometry
        }

        features.push(feature)
    }

    // Placeholders at corners. We didn't find a good way to configure echarts NOT filling the container
    // This is a workaround
    features.push(
        JSON.parse(
            """
{"type":"Feature","id":"placeholder-1","properties":{"name":""},"geometry":{"type":"Polygon","coordinates":[[[0,0],[1,0],[0,1]]]}}
    """.trimIndent()
        )
    )
    features.push(
        JSON.parse(
            """
{"type":"Feature","id":"placeholder-2","properties":{"name":""},"geometry":{"type":"Polygon","coordinates":[[[0,${height - 1}],[1,${height - 1}],[0,${height - 2}]]]}}
    """.trimIndent()
        )
    )
    features.push(
        JSON.parse(
            """
{"type":"Feature","id":"placeholder-3","properties":{"name":""},"geometry":{"type":"Polygon","coordinates":[[[${width - 1},${height - 1}],[${width - 1},${height - 2}],[${width - 2},${height - 1}]]]}}
    """.trimIndent()
        )
    )
    features.push(
        JSON.parse(
            """
{"type":"Feature","id":"placeholder-4","properties":{"name":""},"geometry":{"type":"Polygon","coordinates":[[[${width - 1},0],[${width - 1},1],[${width - 2},0]]]}}
    """.trimIndent()
        )
    )

    return jsObject {
        type = "FeatureCollection"
        this.features = features
    }
}

fun GameScene.getRoadmapOptions(): dynamic {
    val missions: List<GameMission> = objects.getByRole(GameObjectRole.Mission)
    val nodes = js("[]")
    val edges = js("[]")
    for (mission in missions) {
        nodes.push(jsObject {
            id = mission.id
//            symbolSize = 10
            x = mission.gameMapMission.gridCoordinate.x * map.tileSize.width
            y = mission.gameMapMission.gridCoordinate.y * map.tileSize.height
            value = 0
            category = 0
        })
        if (mission.gameMapMission.next != null) {
            edges.push(jsObject {
                source = mission.id
                target = mission.gameMapMission.next
            })
        }
        for (child in mission.gameMapMission.children) {
            edges.push(jsObject {
                source = mission.id
                target = child
            })
        }
    }
    val graph = assign(baseSeriesOptions) {
        this.nodes = nodes
        this.edges = edges
    }
    val seriesArray = js("[]")
    seriesArray.push(graph)


    return jsObject {
        backgroundColor = "transparent"
        series = seriesArray
    }
}

val baseSeriesOptions: dynamic = JSON.parse(
    """
    {
        "type": "graph",
        "layout": "none",
        "edgeSymbol": ["none", "none"],
        "tooltip": { "show": false},
        "scaleLimit": {
                    "min": 0.3,
                    "max": 0.3
                },
                        "lineStyle": {
                            "color": "source",
                            "curveness": 0.3,
                            "width": 2,
                            "dashOffset": 2,
                            "type": "dashed"
                        }
    }
""".trimIndent()
)

val minimapGraphSeries: dynamic = JSON.parse(
    """
    {
        "left": 0,
        "right": 0,
        "top": 0,
        "bottom": 0,
        "type": "graph",
        "layout": "none",
        "edgeSymbol": ["none", "none"],
        "edgeSymbolSize": [0, 10],
        "tooltip": { "show": false},
                        "lineStyle": {
                            "color": "red",
                            "curveness": 0.3,
                            "width": 2
                        }

    }
""".trimIndent()
)


fun GameScene.getRoadmapMinimapOptions(): dynamic {
    val mapWidth = map.pixelSize.width
    val mapHeight = map.pixelSize.height
//    val style: dynamic = JSON.parse("""{"itemStyle: { "color": "red"}} """)
    val series: dynamic = JSON.parse(
        """
                    {
                        "name": "fuck",
                        "type": "map",
                        "map": "minimap",

                        "itemStyle": {"areaColor":"#f3edd9", "borderWidth":2, "borderColor": "#828282"
                         },
                                 "left": 0,
                                 "right": 0,
                                 "top": 0,
                                 "bottom": 0,

                                 "data": [
                                    {
                                        "name":"JavaNewbieVillageRegion",
                                        "value": 4
                                    },
                                    {
                                        "name":"BasicSkillRegion",
                                        "value": 2
                                    },
                                    {
                                        "name":"JavaBasicStructureRegion",
                                        "value": 3
                                    }
                                 ]
                    }
    """.trimIndent()
    )

    val seriesArray = nativeJsArrayOf(series)
    val tooltip = jsObject<dynamic> {
        trigger = "item"
        formatter = "fuck"
    }

    val regions: List<GameMapRegion> = objects.getByRole(GameObjectRole.MapRegion)
    val nodes: dynamic = js("[]")
    val edges: dynamic = js("[]")
    regions.forEachIndexed { index, region ->
        val xSum = region.vertices.sumOf { it.x }
        val ySum = region.vertices.sumOf { it.y }
        nodes.push(jsObject {
            id = region.id
            x = xSum / region.vertices.size
            y = ySum / region.vertices.size
            value = 0
            category = 0
            symbolSize = 0
        })
        if (index != 0) {
            edges.push(jsObject {
                source = regions[index - 1].id
                target = regions[index].id
            })
        }
    }

    nodes.push(JSON.parse("""{"id": "placeholder1", "x": 0, "y": 0, "symbolSize": 0 }"""))
    nodes.push(JSON.parse("""{"id": "placeholder2", "x": ${mapWidth - 1}, "y": 0, "symbolSize": 0 }"""))
    nodes.push(JSON.parse("""{"id": "placeholder3", "x": 0, "y": ${mapHeight - 1}, "symbolSize": 0 }"""))
    nodes.push(JSON.parse("""{"id": "placeholder4", "x": ${mapWidth - 1}, "y": ${mapHeight - 1}, "symbolSize": 0 }"""))

    seriesArray.push(assign(minimapGraphSeries) {
        this.nodes = nodes
        this.edges = edges
    })

    val grid: dynamic = jsObject {
        left = "0"
        right = "0"
        top = "0"
        bottom = "0"
        width = "100%"
        height = "100%"
    }
    val visualMap: dynamic = JSON.parse("""
                                                                  {
                                                                  "show": false,
                                                                     "type": "piecewise",
                                                                     "pieces": [
                                                                        {
                                                                            "min": 1,
                                                                            "max": 1,
                                                                            "color": "#fbd7d6"
                                                                        },
                                                                        {
                                                                            "min": 2,
                                                                            "max": 2,
                                                                            "color": "#e4e4f7"
                                                                        },
                                                                        {
                                                                            "min": 3,
                                                                            "max": 3,
                                                                            "color": "#f3edd9"
                                                                        },
                                                                        {
                                                                            "min": 4,
                                                                            "max": 4,
                                                                            "color": "#D8E4FD"
                                                                        }
                                                                     ]
                                                                 }
    """.trimIndent())
    return jsObject {
//        backgroundColor = "transparent"
//        this.grid = grid
//        this.center = center
//        this.zoom = 2
        this.tooltip = tooltip
        this.visualMap = visualMap
        this.series = seriesArray
//        this.xAxis = xAxis
//        this.yAxis = yAxis
    }
}

class EChartsRoadmap(props: EChartsRoadmapProps) : RComponent<EChartsRoadmapProps, EChartsRoadmapState>(props) {
    var htmlElement: dynamic = undefined

    // https://echarts.apache.org/en/api.html#echarts.init
    var echarts: dynamic = undefined

    override fun EChartsRoadmapState.init(props: EChartsRoadmapProps) {
//        option = props.gameScene.getRoadmapOptions()
        option = props.gameScene.getRoadmapMinimapOptions()
        console.log(JSON.stringify(option))
    }

    override fun RBuilder.render() {
        div {
            attrs.jsStyle {
                width = "${props.width}px"
                height = "${props.height}px"
                top = "${props.top}px"
                left = "${props.left}px"
//                backgroundImage = """url("${game.resolve("/img/minimap.png")}")"""
//                backgroundSize = "100% 100%"
//                backgroundRepeat = "no-repeat"
//                backgroundPosition = "0 0px"
            }
            ref {
                if (it != null) {
                    console.log("set ref", it)
                    htmlElement = it as HTMLElement
                }
            }
            console.log("echarts/dom", echarts, htmlElement)
            if (echarts == undefined && htmlElement != undefined) {
                console.log("Init!")
                echarts = window.asDynamic().echarts.init(htmlElement, props.theme, jsObject {
                    renderer = props.renderer
                })
                val features = props.gameScene.getRoadmapMapFeatures()
                console.log(JSON.stringify(features))
                window.asDynamic().echarts.registerMap("minimap", features)
            }
            if (echarts != undefined) {
                console.log("Set option!")
                echarts.setOption(state.option)
            }
        }
    }

//    override fun componentDidMount() {
//        window.asDynamic().echarts.setOption()
//    }
//
//    override fun componentDidUpdate(prevProps: EChartsRoadmapProps, prevState: RState, snapshot: Any) {
//        window.asDynamic().Prism.highlightElement(element)
//    }
}
