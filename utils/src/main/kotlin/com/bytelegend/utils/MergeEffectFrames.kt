package com.bytelegend.utils

import com.bytelegend.app.shared.RGBA
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/**
 * Merge a list of frames into a single png image. Transparent borders will be trimmed.
 */
fun main(args: Array<String>) {
    val inputDir = File(args[0])
    val outputDir = File(args[1])
    val scale = if (args.size > 2) args[2].toDouble() else 1.0
    MergeEffectFrames().merge(inputDir
        .listFiles { _, name -> name.endsWith("png") }
        .sortedBy { it.name },
        outputDir.resolve("${inputDir.name}.png"),
        scale
    )
}

class MergeEffectFrames {
    private val imageReader = ImageReader()
    fun merge(srcFrames: List<File>, outputImageFile: File, scale: Double) {
        val corners = srcFrames.map { findFourCornerOfOpaquePixels(it) }
        // find the largest common opaque region
        val firstCommonOpaqueColumn = corners.minOf { it[0] }
        val lastCommonOpaqueColumn = corners.maxOf { it[1] }
        val firstCommonOpaqueRow = corners.minOf { it[2] }
        val lastCommonOpaqueRow = corners.maxOf { it[3] }

        val srcFrameWidth = lastCommonOpaqueColumn - firstCommonOpaqueColumn + 1
        val srcFrameHeight = lastCommonOpaqueRow - firstCommonOpaqueRow + 1
        val destFrameWidth = (srcFrameWidth * scale).toInt()
        val destFrameHeight = (srcFrameHeight * scale).toInt()
        val outputImage = BufferedImage(destFrameWidth * srcFrames.size, destFrameHeight, BufferedImage.TYPE_INT_ARGB)
        val graphics = outputImage.graphics
        srcFrames.forEachIndexed { index, frame ->
            graphics.drawImage(
                imageReader.getImage(frame),
                destFrameWidth * index, 0, destFrameWidth * index + destFrameWidth, destFrameHeight,
                firstCommonOpaqueColumn, firstCommonOpaqueRow, firstCommonOpaqueColumn + srcFrameWidth, firstCommonOpaqueRow + srcFrameHeight,
                null
            )
        }
        graphics.dispose()
        ImageIO.write(outputImage, "PNG", outputImageFile)
    }

    /**
     * Find top-left/top-right/bottom-left/bottom-right corner of opaque pixels for a image
     */
    private fun findFourCornerOfOpaquePixels(image: File): List<Int> {
        val pixels = imageReader.read(image)
        val firstOpaqueColumn = pixels[0].indices
            .firstOrNull { !pixels.columnIsTransparent(it) } ?: throw IllegalStateException("All columns are transparent? $image")
        val lastOpaqueColumn = (pixels[0].size - 1 downTo 0)
            .firstOrNull { !pixels.columnIsTransparent(it) } ?: throw IllegalStateException("All columns are transparent? $image")
        val firstOpaqueRow = pixels.indices
            .firstOrNull { !pixels.rowIsTransparent(it) } ?: throw IllegalStateException("All columns are transparent? $image")
        val lastOpaqueRow = (pixels.size - 1 downTo 0)
            .firstOrNull { !pixels.rowIsTransparent(it) } ?: throw IllegalStateException("All columns are transparent? $image")
        return listOf(firstOpaqueColumn, lastOpaqueColumn, firstOpaqueRow, lastOpaqueRow)
    }

    private fun List<List<RGBA>>.columnIsTransparent(column: Int): Boolean {
        return all { it[column].isTransparent() }
    }

    private fun List<List<RGBA>>.rowIsTransparent(row: Int): Boolean {
        return get(row).all { it.isTransparent() }
    }
}



