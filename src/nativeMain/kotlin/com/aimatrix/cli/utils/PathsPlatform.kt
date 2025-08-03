package com.aimatrix.cli.utils

import okio.Path
import okio.Path.Companion.toPath
import platform.posix.getenv
import kotlinx.cinterop.toKString

/**
 * Native platform-specific path implementations
 */

actual fun getPathSeparator(): String = "/"

actual fun getHomeDirectory(): Path {
    val home = getenv("HOME")?.toKString() 
        ?: getenv("USERPROFILE")?.toKString() // Windows fallback
        ?: "/"
    return home.toPath()
}

actual fun getTempDirectory(): Path {
    val temp = getenv("TMPDIR")?.toKString()
        ?: getenv("TMP")?.toKString()
        ?: getenv("TEMP")?.toKString()
        ?: "/tmp"
    return temp.toPath()
}