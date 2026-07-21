package com.fghilmany.nufitai.core.util

import platform.Foundation.NSUUID

actual fun generateUUID(): String = NSUUID().UUIDString()
