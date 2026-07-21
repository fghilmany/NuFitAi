package com.fghilmany.nufitai.core.util

import java.util.UUID

actual fun generateUUID(): String = UUID.randomUUID().toString()
