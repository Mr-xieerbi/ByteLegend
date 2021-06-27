package com.bytelegend.client.app.obj

import com.bytelegend.app.client.api.FixedIntervalAnimation
import com.bytelegend.app.client.api.ImageResourceData
import com.bytelegend.app.shared.PixelBlock
import com.bytelegend.app.shared.PixelSize
import org.w3c.dom.CanvasRenderingContext2D

// private val EFFECT_IMAGE_NAME = ".*[-_](\\d+)x(\\d+)".toRegex()

// A effect is in RRBD/img/effect/XXX_1x16.png
// the effect image is assumed to be 1xN , where N is frame number

/**
 * An effect is composed of several frames.
 */
class AnimationEffect(
    private val image: ImageResourceData,
    private val targetBlock: PixelBlock,
    fps: Int,
) {
    private val frameNumber = image.imageId.substringAfterLast("x").toInt()
    private val animation = FixedIntervalAnimation(frameNumber, fps)
    private val frameSize: PixelSize = PixelSize(image.size.width / frameNumber, image.size.height)

    fun draw(canvas: CanvasRenderingContext2D) {
        val frameIndex = animation.getNextFrameIndex()
        canvas.drawImage(
            image.htmlElement,
            PixelBlock(frameSize.width * frameIndex, frameSize.height, frameSize.width, frameSize.height),
            targetBlock
        )
    }
}
