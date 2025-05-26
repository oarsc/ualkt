package org.oar.ualkt.ui

import org.oar.ualkt.model.CommandWithSearchResults
import org.oar.ualkt.services.iconLoader.IconLoader
import org.oar.ualkt.ui.themes.Themes.themedFocusTextStyle
import org.oar.ualkt.ui.themes.Themes.themedIconSize
import org.oar.ualkt.ui.themes.Themes.themedSelectedBackground
import org.oar.ualkt.ui.themes.Themes.themedSize
import org.oar.ualkt.ui.themes.Themes.themedTextStyle
import java.awt.BorderLayout.CENTER
import javax.swing.BoxLayout
import javax.swing.ImageIcon
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants


class OptionUI(
    val option: CommandWithSearchResults,
    selected: Boolean = false
) : JPanel() {
    var selected: Boolean = selected
        set(value) {
            field = value
            themedSelectedBackground(value)
        }

    init {
        layout = BoxLayout(this, BoxLayout.X_AXIS)

        themedSelectedBackground(selected)
        themedSize()

        JLabel().apply {
            horizontalAlignment = SwingConstants.CENTER

            themedIconSize()

            IconLoader.loadIcon(option.command.icon) {
                icon = it
            }

            this@OptionUI.add(this, CENTER)
        }

        val title = option.command.title
        var prevStarting = 0

        option.searchResults.matchingIndexes.forEach {
            addText(title, prevStarting, it.first)
            addFocusedText(title, it.first, it.second)
            prevStarting = it.second
        }
        addText(title, prevStarting, title.length)
    }

    private fun addText(content: String, start: Int, end: Int) {
        if (start == end) return

        val textPart = content.substring(start, end)
        if (textPart.isEmpty()) return

        val component = JLabel().apply {
            text = textPart
            isOpaque = false

            themedTextStyle()
        }

        add(component)
    }

    private fun addFocusedText(content: String, start: Int, end: Int) {
        if (start == end) return

        val textPart = content.substring(start, end)
        if (textPart.isEmpty()) return

        val component = JLabel().apply {
            text = textPart
            isOpaque = false

            themedTextStyle()
            themedFocusTextStyle()
            //        themedSize()
        }

        add(component)
    }

    companion object {
        private val imageCache = mutableMapOf<String, ImageIcon>()
    }
}
