package org.oar.ualkt.services.iconLoader

import org.oar.ualkt.ui.themes.Themes
import java.awt.Image
import java.net.URI
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.SwingWorker

object IconLoader {
    private val imageCache = mutableMapOf<String, ImageWorker>()

    fun loadIcon(iconUrl: String, callback: (ImageIcon) -> Unit) {
        val iconWorker = imageCache[iconUrl]
        if (iconWorker == null) {
            imageCache[iconUrl] = ImageWorker(iconUrl, callback)
                .apply { execute() }
        } else if (iconWorker.isDone) {
            iconWorker.get()?.let(callback)
        } else {
            iconWorker.callbacks.add(callback)
        }
    }

    class ImageWorker(
        private val iconUrl: String,
        callback: (ImageIcon) -> Unit
    ):SwingWorker<ImageIcon?, Unit>() {
        val callbacks = mutableListOf(callback)

        override fun doInBackground(): ImageIcon? {
            val iconSize = Themes.iconSize

            return try {
                val imageUrl = when {
                    iconUrl.startsWith("http") -> URI.create(iconUrl).toURL()
                    iconUrl.startsWith("fake-favicon-uri") -> return null
                    else -> javaClass.getResource("/icons/$iconUrl.png")
                }

                val originalImage = ImageIO.read(imageUrl)
                val scaledImage = originalImage.getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH)
                ImageIcon(scaledImage)

            } catch (e: Exception) {
                null
            }
        }

        override fun done() {
            val imageIcon = try { get() } catch (e: Exception) { null }
            if (imageIcon != null) {
                callbacks.forEach { it(imageIcon) }
            }
        }
    }
}