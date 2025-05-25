package org.oar.ualkt.ui.themes

import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import javax.swing.JComponent
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextField
import javax.swing.border.EmptyBorder


class DefaultTheme(
    private val width: Int,
    private val inputHeight: Int,
    private val height: Int,
    private val maxElements: Int
): Theme {
    override val stackOnTop = false
    override val iconSize = 32

    override fun setSize(frame: JFrame, items: Int) {
        frame.setSize(width, inputHeight + items.coerceAtMost(maxElements) * height)
    }

    override fun setMainPosition(frame: JFrame) {
        frame.apply {
            toolkit.screenSize.also {
                val x = (it.width - width) / 2
                val y = (it.height - height) / 2 - 200
                setLocation(x, y)
            }
        }
    }

    override fun setBackground(frame: JFrame) {
        frame.background = Color(37, 37, 37, 254)
    }

    override fun setTextStyle(textField: JTextField) {
        textField.apply {
            font = font.deriveFont(20f)
            foreground = Color.WHITE
            setCaretColor(Color.WHITE)
            selectionColor = Color(102, 179, 26)
        }
    }

    override fun setTextStyle(label: JLabel) {
        label.apply {
            foreground = Color(200, 200, 200)
            font = font
                .deriveFont(16f)
                .deriveFont(Font.PLAIN)
        }
    }

    override fun setFocusTextStyle(label: JLabel) {
        label.apply {
            font = font
                .deriveFont(Font.BOLD)
//                .deriveFont(font.attributes.toMutableMap() + (TextAttribute.UNDERLINE to TextAttribute.UNDERLINE_ON))
            foreground = Color.WHITE
        }
    }

    override fun setSelectedBackground(panel: JPanel, selected: Boolean) {
        panel.apply {
            background = if (selected) Color(74, 74, 74) else null
            isOpaque = selected
        }
    }

    override fun setSize(panel: JPanel) {
        val dimension = Dimension(width, height)
        panel.apply {
            preferredSize = dimension
            maximumSize = dimension
            minimumSize = dimension
            size = dimension
        }
    }

    override fun setIconSize(label: JLabel) {
        val dimension = Dimension(iconSize + 30, 32)
        label.apply {
            maximumSize = dimension
            minimumSize = dimension
        }
    }

    override fun setBorder(component: JComponent) {
        component.border = EmptyBorder(5, 15, 5, 15)
    }

    override fun setSize(scrollPane: JScrollPane, items: Int) {
        scrollPane.preferredSize = Dimension(width, items.coerceAtMost(maxElements) * height)
    }
}