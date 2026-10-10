package com.phcodesage.kwentaro.ui.camera

import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.ReaderException
import com.google.zxing.common.HybridBinarizer

/** Pure-JVM barcode decoding over a luminance (Y) plane, so it can be unit tested. */
class ZxingDecoder {
    private val reader = MultiFormatReader().apply {
        setHints(
            mapOf(
                DecodeHintType.POSSIBLE_FORMATS to listOf(
                    BarcodeFormat.EAN_13, BarcodeFormat.EAN_8, BarcodeFormat.UPC_A, BarcodeFormat.UPC_E,
                    BarcodeFormat.CODE_128, BarcodeFormat.CODE_39, BarcodeFormat.ITF, BarcodeFormat.QR_CODE,
                ),
                DecodeHintType.TRY_HARDER to true,
            ),
        )
    }

    /**
     * Decodes a frame whose content is rotated by [rotationDegrees] relative to upright. 1D codes
     * are only read along rows, so the frame is turned upright first, then also tried at 90°
     * for codes held sideways.
     */
    fun decode(luma: ByteArray, width: Int, height: Int, rotationDegrees: Int): String? {
        var frame = Frame(luma, width, height)
        repeat((rotationDegrees / 90) % 4) { frame = frame.rotated90() }
        return tryDecode(frame) ?: tryDecode(frame.rotated90())
    }

    private fun tryDecode(f: Frame): String? = try {
        val source = PlanarYUVLuminanceSource(f.data, f.width, f.height, 0, 0, f.width, f.height, false)
        reader.decodeWithState(BinaryBitmap(HybridBinarizer(source))).text
    } catch (_: NotFoundException) {
        null
    } catch (_: ReaderException) {
        null
    } finally {
        reader.reset()
    }

    internal class Frame(val data: ByteArray, val width: Int, val height: Int) {
        /** Clockwise rotation by 90°. */
        fun rotated90(): Frame {
            val out = ByteArray(data.size)
            for (y in 0 until height) {
                for (x in 0 until width) {
                    out[x * height + (height - 1 - y)] = data[y * width + x]
                }
            }
            return Frame(out, height, width)
        }
    }
}
