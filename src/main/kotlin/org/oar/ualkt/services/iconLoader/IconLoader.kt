package org.oar.ualkt.services.iconLoader

import javafx.scene.image.Image
import java.io.InputStream
import java.net.URI
import javax.swing.SwingWorker

object IconLoader {
    private val imageCache = mutableMapOf<String, ImageWorkerNew>()

    fun loadIcon(iconUrl: String, callback: (Image) -> Unit) {
        val iconWorker = imageCache[iconUrl]
        if (iconWorker == null) {
            imageCache[iconUrl] = ImageWorkerNew(iconUrl, callback)
                .apply { execute() }
        } else if (iconWorker.isDone) {
            iconWorker.get()?.let(callback)
        } else {
            iconWorker.callbacks.add(callback)
        }
    }

    class ImageWorkerNew(
        private val iconUrl: String,
        callback: (Image) -> Unit
    ): SwingWorker<Image?, Unit>() {
        val callbacks = mutableListOf(callback)

        override fun doInBackground(): Image? {
            return try {
                val inputStream = when {
                    iconUrl.startsWith("http") -> onlineImage()
                    iconUrl.startsWith("fake-favicon-uri") -> null
                    else -> localImage()
                }
                inputStream?.let { Image(it) }
            } catch (e: Exception) {
                null
            }
        }

        private fun localImage(): InputStream? =
            IconLoader::class.java.getResourceAsStream("/icons/$iconUrl.png")

        private fun onlineImage(): InputStream =
            URI.create(iconUrl).toURL().openStream()

        override fun done() {
            val imageIcon = try { get() } catch (e: Exception) { null }
            if (imageIcon != null) {
                callbacks.forEach { it(imageIcon) }
            }
        }
    }
}