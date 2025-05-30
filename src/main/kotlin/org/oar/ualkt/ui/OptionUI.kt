package org.oar.ualkt.ui

import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import org.oar.ualkt.model.CommandWithSearchResults
import org.oar.ualkt.services.iconLoader.IconLoader
import org.oar.ualkt.ui.themes.Themes
import org.oar.ualkt.ui.themes.Themes.themedBackground
import org.oar.ualkt.ui.themes.Themes.themedImageBorder
import org.oar.ualkt.ui.themes.Themes.themedSize
import org.oar.ualkt.ui.themes.Themes.themedStyle

class OptionUI(
    val option: CommandWithSearchResults,
    selected: Boolean = false
) : HBox() {
    var selected: Boolean = selected
        set(value) {
            field = value
            themedBackground(selected)
        }

    init {
        alignment = Pos.CENTER_LEFT

        val iconView = ImageView().apply {
            fitHeight = Themes.iconSize.toDouble()
            fitWidth = Themes.iconSize.toDouble()

            IconLoader.loadIcon(option.command.icon) { img ->
                image = img
            }

            themedImageBorder()
        }

        themedBackground(selected)
        themedSize()

        children.add(iconView)

        val title = option.command.title
        var prevStarting = 0
        option.searchResults.matchingIndexes.forEach {
            addText(title, prevStarting, it.first, false)
            addText(title, it.first, it.second, true)
            prevStarting = it.second
        }
        addText(title, prevStarting, title.length, false)
    }

    private fun addText(content: String, start: Int, end: Int, selected: Boolean) {
        if (start == end) return
        val textPart = content.substring(start, end)
        if (textPart.isEmpty()) return
        val label = Label(textPart).apply {
            themedStyle(selected)
        }
        children.add(label)
    }
}
