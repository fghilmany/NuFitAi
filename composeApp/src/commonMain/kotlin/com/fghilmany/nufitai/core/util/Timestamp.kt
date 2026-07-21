package com.fghilmany.nufitai.core.util

/**
 * Returns the current timestamp as an ISO-8601-ish string.
 * Cross-platform: uses expect/actual for platform-specific time.
 */
expect fun currentTimestamp(): String
