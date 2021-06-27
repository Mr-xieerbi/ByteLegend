package com.bytelegend.client.app.engine

import com.bytelegend.app.client.api.GameScene
import com.bytelegend.app.shared.protocol.LogStreamEventData
import com.bytelegend.app.shared.protocol.logStreamEvent
import com.bytelegend.client.app.engine.resource.ImageResource
import com.bytelegend.client.app.obj.LogStreamEffect

class LogStreamEffectController(
    private val gameScene: GameScene
) {
    private val resourceLoader = gameScene.gameRuntime.unsafeCast<Game>().resourceLoader
    private val effectResource by lazy {
        resourceLoader.loadAsync(
            ImageResource(
                "dropping-fire_1x16",
                gameScene.gameRuntime.resolve("img/effect/dropping-fire_1x16.png")
            )
        )
    }

    init {
        gameScene.gameRuntime.eventBus.on(logStreamEvent(gameScene.map.id), this::onLogStreamEvent)
    }

    private fun onLogStreamEvent(logStreamEventData: LogStreamEventData) {
        val objectId = "${logStreamEventData.missionId}-log-stream"
        var currentEffect = gameScene.objects.getByIdOrNull<LogStreamEffect>(objectId)
        if (currentEffect == null) {
            currentEffect = LogStreamEffect(objectId, gameScene, gameScene.objects.getById(logStreamEventData.missionId), effectResource)
            gameScene.objects.add(currentEffect)
        }
        currentEffect.addLines(logStreamEventData.lines)
    }
}
