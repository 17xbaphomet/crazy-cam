package com.example.crazycam

import android.graphics.Bitmap
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import java.nio.ByteBuffer

class CameraProcessor(
    private val onProcessedFrame: (Bitmap) -> Unit
) : ImageAnalysis.Analyzer {

    override fun analyze(image: ImageProxy) {
        val width = image.width
        val height = image.height

        // Simple RGB extraction for basic structure
        val buffer: ByteBuffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)

        // Convert to RGB byte array (simplified)
        val rgbBytes = ByteArray(width * height * 3)
        // For production: implement proper YUV to RGB conversion here or in Rust

        // Call UniFFI generated function
        val processedBytes = CrazyCamFilters.processFrame(width, height, rgbBytes)

        // Convert processed bytes back to Bitmap
        val processedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(width * height)
        for (i in pixels.indices) {
            val r = processedBytes.getOrElse(i * 3) { 0 }.toInt() and 0xFF
            val g = processedBytes.getOrElse(i * 3 + 1) { 0 }.toInt() and 0xFF
            val b = processedBytes.getOrElse(i * 3 + 2) { 0 }.toInt() and 0xFF
            pixels[i] = (0xFF shl 24) or (r shl 16) or (g shl 8) or b
        }
        processedBitmap.setPixels(pixels, 0, width, 0, 0, width, height)

        onProcessedFrame(processedBitmap)
        image.close()
    }
}