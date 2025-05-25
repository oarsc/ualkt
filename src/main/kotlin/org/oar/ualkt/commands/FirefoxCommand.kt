package org.oar.ualkt.commands

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.commons.compress.compressors.lz4.BlockLZ4CompressorInputStream
import org.oar.ualkt.model.FirefoxBookmark
import org.oar.ualkt.model.FirefoxConfig
import org.oar.ualkt.model.FirefoxConfigElement
import org.oar.ualkt.model.SearchLevel
import org.oar.ualkt.model.SearchResult
import org.oar.ualkt.services.controller.Controller
import org.oar.ualkt.utils.IO.listFolderContent
import java.awt.Desktop
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.URI
import java.util.*

class FirefoxCommand(
    private val config : FirefoxConfigElement,
) : Command() {
    override val keyWord: String = config.bookmark.title
    override val title: String = config.bookmark.title
    override val icon: String = config.bookmark.iconUri ?: "firefox"

    private val url = config.bookmark.uri!!
    private val profile = config.profile

    init {
        caseInsensitive = true
        startsWith = false

        generateMd5Id()
    }

    override fun perform(argsList: List<String>, controller: Controller) {
        if (bin != null) {
            ProcessBuilder(bin, "-p", profile, url)
                .start()
                .apply {
                    this.waitFor(0, java.util.concurrent.TimeUnit.MILLISECONDS)
                }
//                .inheritIO()
//                .start()
        } else {
            Desktop.getDesktop().browse(URI(url))
        }
    }

    override fun match(inputText: String): SearchResult {
        val keyLevel = search(this.keyWord, inputText, true)
        return if (keyLevel.level == SearchLevel.NOT_FOUND)
            search(this.url, inputText, caseInsensitive = true, split = false, recordIndexes = false)
        else
            keyLevel
    }

    companion object {
        private const val WIN_PATH: String = "~/AppData/Roaming/Mozilla/Firefox/Profiles/"
        private const val LINUX_PATH: String = "~/.mozilla/firefox/"
        lateinit var pathFolder: String
        var bin: String? = null

        fun generate(data: FirefoxConfig): List<FirefoxCommand> {
            pathFolder = data.profileFolder ?:
                if (System.getProperty("os.name").lowercase(Locale.getDefault()) == "win") WIN_PATH else LINUX_PATH
            bin = data.bin

            val path = pathFolder.replace("~", System.getProperty("user.home"))

            return File(path).listFolderContent()
                .filter { it.isDirectory && filterExcludes(it, data.exclude) }
                .flatMap { profileFolder ->
                    val bookmarks = File(profileFolder, "bookmarkbackups")
                    if (!bookmarks.exists() || !bookmarks.isDirectory) return@flatMap emptyList()

                    val profile = getProfileName(profileFolder.name)
                    val bookmarkFile = bookmarks.listFolderContent()
                        .maxByOrNull { it.name } ?: return@flatMap emptyList()

                    recollect(readMozLZ4A(bookmarkFile).children)
                        .map { FirefoxCommand(FirefoxConfigElement(profile, it)) }
                }
        }

        private fun recollect(bookmarks: List<FirefoxBookmark>): List<FirefoxBookmark> {
            return bookmarks.filter { it.type == "text/x-moz-place" } + bookmarks.flatMap { recollect(it.children) }
        }

        private fun filterExcludes(it: File, excludes: List<String>): Boolean {
            if (excludes.isNotEmpty()) {
                return excludes.indexOf(getProfileName(it.name)) < 0;
            }
            return true;
        }

        private fun getProfileName(profile: String): String =
            profile.split(".").drop(1).joinToString(".")

        private val objectMapper = jacksonObjectMapper()

        private fun readMozLZ4A(file: File): FirefoxBookmark {
            FileInputStream(file).use { fis ->
                // Read and verify headers "mozLz40\0" (8 bytes)
                val headerBytes = ByteArray(8)
                val headerBytesRead = fis.read(headerBytes)
                if (headerBytesRead < 8) {
                    throw IOException("File is too short to hold mozLz4 headers: ${file.path}")
                }

                // The header is "mozLz40" followed by a null byte
                val expectedHeaderSignature = "mozLz40\u0000" // \u0000 null character
                val actualHeaderSignature = String(headerBytes, Charsets.US_ASCII)

                if (actualHeaderSignature != expectedHeaderSignature) {
                    throw IOException(
                        "mozLz4 header invalid: ${file.path}. Expected: '$expectedHeaderSignature', Got: '$actualHeaderSignature'"
                    )
                }

                // 2. Read uncompressed size (4 bytes, little-endian).
                val sizeBytes = ByteArray(4)
                val sizeBytesRead = fis.read(sizeBytes)
                if (sizeBytesRead < 4) {
                    throw IOException("El archivo es demasiado corto para contener el tamaño descomprimido: ${file.path}")
                }

                // 3. The rest of the flow ('fis') now contains the comprimed data LZ4.
                BlockLZ4CompressorInputStream(fis).use { lz4Stream ->
                    return InputStreamReader(lz4Stream, Charsets.UTF_8).readText()
                        .let {
                            objectMapper.readValue(it, FirefoxBookmark::class.java)
                        }
                }
            }
        }
    }
}