package com.bytelegend.client.app.ui

import com.bytelegend.app.client.api.GameScene
import com.bytelegend.client.app.obj.uuid
import com.bytelegend.client.app.ui.minimap.getMinimapEChartsOptions
import com.bytelegend.client.app.ui.minimap.getMinimapMapFeatures
import kotlinext.js.jsObject
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.html.id
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState

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


//val baseMinimapSeries: dynamic = JSON.parse(
//    """
//    {
//        "type": "graph",
//        "tooltip": { "show": false }
//    }
//""".trimIndent()
//)
//
//
//fun GameScene.getRoadmapOptions(): dynamic {
//    val missions: List<GameMission> = objects.getByRole(GameObjectRole.Mission)
//    val nodes = js("[]")
//    val edges = js("[]")
//    for (mission in missions) {
//        nodes.push(jsObject {
//            id = mission.id
////            symbolSize = 10
//            x = mission.gameMapMission.gridCoordinate.x * map.tileSize.width
//            y = mission.gameMapMission.gridCoordinate.y * map.tileSize.height
//            value = 0
//            category = 0
//        })
//        mission.gameMapMission.next.forEach {
////            edges.push(jsObject {
////                source = mission.id
////                target = mission.gameMapMission.next
////            })
//        }
////        for (child in mission.gameMapMission.children) {
////            edges.push(jsObject {
////                source = mission.id
////                target = child
////            })
////        }
//    }
//    val graph = assign(baseSeriesOptions) {
//        this.nodes = nodes
//        this.edges = edges
//    }
//    val seriesArray = js("[]")
//    seriesArray.push(graph)
//
//
//    return jsObject {
//        backgroundColor = "transparent"
//        series = seriesArray
//    }
//}

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
    "type": [10, 3],
    "color": "#423019",
    "curveness": 0.3,
    "width": 5
  }
}
""".trimIndent()
)


class EChartsRoadmap(props: EChartsRoadmapProps) : RComponent<EChartsRoadmapProps, RState>(props) {
    private val echartsContainerElementId = "echarts-container-${uuid()}"

//    var htmlElement: dynamic = undefined

    // https://echarts.apache.org/en/api.html#echarts.init
    var echarts: dynamic = undefined
    var options: dynamic = undefined

//    override fun EChartsRoadmapState.init(props: EChartsRoadmapProps) {
//        option = props.gameScene.getMinimapEChartsOptions()
//        console.log(JSON.stringify(option))
//    }

    override fun RBuilder.render() {
        absoluteDiv(left = props.left, top = props.top, width = props.width, height = props.height) {
            attrs.id = echartsContainerElementId

//            ref {
//                if (it != null) {
//                    console.log("set ref", it)
//                    htmlElement = it as HTMLElement
//                }
//            }
//            console.log("echarts/dom", echarts, htmlElement)
//            if (echarts == undefined && htmlElement != undefined) {
//                console.log("Init!")
//                echarts = window.asDynamic().echarts.init(htmlElement, props.theme, jsObject {
//                    renderer = props.renderer
//                })
//                val features = props.gameScene.getMinimapMapFeatures()
//                console.log(JSON.stringify(features))
//                window.asDynamic().echarts.registerMap("minimap", features)
//            }
//            if (echarts != undefined) {
//                echarts.setOption(state.option)
//            }
        }
    }

    private fun init() {
        if (echarts == undefined) {
            document.getElementById(echartsContainerElementId)?.apply {
                echarts = window.asDynamic().echarts.init(this, props.theme, jsObject {
                    renderer = props.renderer
                })
                val features = props.gameScene.getMinimapMapFeatures()
                console.log("features:", JSON.stringify(features))
                window.asDynamic().echarts.registerMap("minimap", features)
            }
        }
        if (options == undefined) {
            options = props.gameScene.getMinimapEChartsOptions()
            console.log("options:", JSON.stringify(options))
        }
    }

    override fun componentDidMount() {
        init()
        echarts.setOption(options)
    }

    override fun componentDidUpdate(prevProps: EChartsRoadmapProps, prevState: RState, snapshot: Any) {
        init()
        echarts.setOption(options)
    }

    override fun shouldComponentUpdate(nextProps: EChartsRoadmapProps, nextState: RState): Boolean {
        return props.gameScene !== nextProps.gameScene
    }
}
