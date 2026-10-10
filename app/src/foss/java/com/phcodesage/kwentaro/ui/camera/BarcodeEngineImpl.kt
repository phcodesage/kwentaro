package com.phcodesage.kwentaro.ui.camera

import android.content.Context
import androidx.camera.core.ImageProxy
import androidx.core.content.ContextCompat

/** ZXing (Apache-2.0): no proprietary code and no network or telemetry. */
fun createBarcodeAnalyzer(context: Context, onCode: (String) -> Unit): BarcodeAnalyzer {
    val main = ContextCompat.getMainExecutor(context)
    val decoder = ZxingDecoder()
    return object : BarcodeAnalyzer {
        override fun analyze(image: ImageProxy) {
            image.use {
                // Plane 0 of YUV_420_888 is luminance, which is all ZXing needs.
                val plane = it.planes[0]
                val width = it.width
                val height = it.height
                val luma = ByteArray(width * height)
                val buffer = plane.buffer.apply { rewind() }
                if (plane.rowStride == width) {
                    buffer.get(luma, 0, luma.size)
                } else {
                    for (row in 0 until height) {
                        buffer.position(row * plane.rowStride)
                        buffer.get(luma, row * width, width)
                    }
                }
                decoder.decode(luma, width, height, it.imageInfo.rotationDegrees)?.let { code -> main.execute { onCode(code) } }
            }
        }

        override fun close() = Unit
    }
}
