package org.oar.ualkt.ui

import org.oar.ualkt.commands.Command
import org.oar.ualkt.ui.themes.Themes.themedBorder
import org.oar.ualkt.ui.themes.Themes.themedSelectedBackground
import org.oar.ualkt.ui.themes.Themes.themedSize
import org.oar.ualkt.ui.themes.Themes.themedTextStyle
import java.awt.BorderLayout
import java.awt.Color
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.border.EmptyBorder

class OptionUI(
    val option: Command,
    selected: Boolean = false
): JPanel() {
    private val inputText: JLabel

    var selected: Boolean = selected
        set(value) {
            field = value
            inputText.themedSelectedBackground(value)
        }

    init {
        isOpaque = false
        layout = BorderLayout()

        inputText = JLabel().apply {
            text = option.title
            isOpaque = false

            themedTextStyle()
            themedSize()
            themedBorder()
            themedSelectedBackground(selected)

            font = font.deriveFont(20f)
            foreground = Color.WHITE
            border = EmptyBorder(5, 15, 5, 15)
        }

        add(inputText, BorderLayout.CENTER)
    }
}
