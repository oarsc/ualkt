package org.oar.ualkt.utils

import java.io.File

object IO {
    fun File.listFolderContent(): List<File>  {
        if (!exists() || !isDirectory) {
            println("Error: File not found at $this")
            return emptyList()
        }

        return listFiles()?.toList() ?: emptyList()
    }
}