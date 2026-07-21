package com.fghilmany.nufitai.core.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

actual fun currentTimestamp(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
    return sdf.format(Date())
}
