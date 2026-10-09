package com.phcodesage.kwentaro.util

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToLong

private val amountFormat = DecimalFormat("#,##0.00")

fun Long.formatMoney(symbol: String): String {
    val sign = if (this < 0) "-" else ""
    return "$sign$symbol${amountFormat.format(abs(this) / 100.0)}"
}

/** Parses user input like "1,250.5" into cents; null when it isn't a number. */
fun String.parseMoneyToCents(): Long? =
    replace(",", "").trim().toBigDecimalOrNull()?.let { (it.toDouble() * 100).roundToLong() }

fun Long.centsToInput(): String = if (this == 0L) "" else (this / 100.0).let {
    if (this % 100 == 0L) (this / 100).toString() else "%.2f".format(Locale.US, it)
}

fun Long.formatDateTime(): String =
    SimpleDateFormat("MMM d, yyyy · h:mm a", Locale.getDefault()).format(Date(this))

fun receiptNumber(id: Long, createdAt: Long): String =
    "KW-" + SimpleDateFormat("yyMMdd", Locale.US).format(Date(createdAt)) + "-" + id.toString().padStart(5, '0')
