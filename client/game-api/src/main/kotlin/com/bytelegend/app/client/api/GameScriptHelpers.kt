/*
 * Copyright 2021 ByteLegend Technologies and the original author or authors.
 * 
 * Licensed under the GNU Affero General Public License v3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      https://github.com/ByteLegend/ByteLegend/blob/master/LICENSE
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bytelegend.app.client.api

import com.bytelegend.app.client.api.dsl.UnitFunction
import com.bytelegend.app.shared.Direction
import com.bytelegend.app.shared.objects.CoordinateAware

/**
 * We have to use instance method due to the defect of current module loading mechanism
 */
class GameScriptHelpers(val gameScene: GameScene) {
    fun distanceOf(character1Id: String, character2Id: String): Int {
        return getCharacter(character1Id).gridCoordinate.manhattanDistanceTo(
            getCharacter(character2Id).gridCoordinate
        )
    }

    fun getCharacter(characterId: String) = gameScene.objects.getById<Character>(characterId)

    fun faceDirectionOf(character1: CoordinateAware, character2: CoordinateAware): Direction {
        val npcCoordinate = character1.gridCoordinate
        val heroCoordinate = character2.gridCoordinate
        return when {
            npcCoordinate.x == heroCoordinate.x && npcCoordinate.y + 1 == heroCoordinate.y -> Direction.DOWN
            npcCoordinate.x == heroCoordinate.x && npcCoordinate.y - 1 == heroCoordinate.y -> Direction.UP
            npcCoordinate.y == heroCoordinate.y && npcCoordinate.x + 1 == heroCoordinate.x -> Direction.RIGHT
            npcCoordinate.y == heroCoordinate.y && npcCoordinate.x - 1 == heroCoordinate.x -> Direction.LEFT
            else -> throw IllegalStateException("Can't determine direction: $npcCoordinate, $heroCoordinate")
        }
    }

    /**
     * Standard NPC speech behaviour when clicked, including:
     *
     * 1. Display toast "you are not login" for anonymous user.
     * 2. Make hero move to the npc, make them face to face, then call `onInteraction`
     * 3. If hero can't reach NPC, call `onUnreachable`
     */
    fun standardNpcSpeech(
        npcId: String,
        onInteraction: UnitFunction
    ) = standardNpcSpeech(npcId, onInteraction) {}

    fun standardNpcSpeech(
        npcId: String,
        onInteraction: UnitFunction,
        onUnreachable: UnitFunction
    ) = click@{
        if (gameScene.gameRuntime.hero == null) {
            gameScene.gameRuntime.toastController.addToast(
                gameScene.gameRuntime.i("YouAreNotLoggedIn"),
                gameScene.gameRuntime.i("ClickHereToLogin"),
                5000
            )
            return@click
        }
        // hero is not in the current scene
        val hero = gameScene.objects.getByIdOrNull<Character>(HERO_ID) ?: return@click
        val npc = gameScene.objects.getById<Character>(npcId)

        if (distanceOf(HERO_ID, npcId) > 1) {
            // This is a bit tricky: if searching path from hero to NPC
            // it will be unreachable because NPC is a blocker
            // so we remove blocker first then restore
            val tmp = gameScene.blockers[npc.gridCoordinate.y][npc.gridCoordinate.x]
            try {
                gameScene.blockers[npc.gridCoordinate.y][npc.gridCoordinate.x] = 0
                val movePath = hero.searchPath(npc.gridCoordinate)
                if (movePath.isEmpty()) {
                    onUnreachable()
                } else {
                    hero.moveAlong(movePath.subList(0, movePath.size - 1)) {
                        faceToFaceThenInteract(hero, npc, onInteraction)
                    }
                }
            } finally {
                gameScene.blockers[npc.gridCoordinate.y][npc.gridCoordinate.x] = tmp
            }
        } else {
            faceToFaceThenInteract(hero, npc, onInteraction)
        }
    }

    private fun faceToFaceThenInteract(hero: Character, npc: Character, onInteraction: UnitFunction) {
        npc.direction = faceDirectionOf(npc, hero)
        hero.direction = faceDirectionOf(hero, npc)
        onInteraction()
    }
}
