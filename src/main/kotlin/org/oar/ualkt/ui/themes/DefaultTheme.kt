package org.oar.ualkt.ui.themes

import java.awt.Color
import java.awt.Dimension
import javax.swing.JComponent
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JTextField
import javax.swing.border.EmptyBorder

class DefaultTheme(
    private val width: Int,
    private val height: Int
): Theme {
    override val stackOnTop = false

    override fun setSize(frame: JFrame, items: Int) {
        frame.setSize(width, height + items * height)
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
        frame.background = Color(0.145f, 0.145f, 0.145f, 0.9f)
    }

    override fun setTextStyle(textField: JTextField) {
        textField.apply {
            font = font.deriveFont(20f)
            foreground = Color.WHITE
            setCaretColor(Color.WHITE)
            selectionColor = Color(0.4f, 0.7f, 0.1f)
        }
    }

    override fun setSize(label: JLabel) {
        val dimension = Dimension(width, height)
        label.apply {
            preferredSize = dimension
            maximumSize = dimension
            minimumSize = dimension
            size = dimension
        }
    }

    override fun setTextStyle(label: JLabel) {
        label.apply {
            font = font.deriveFont(20f)
            foreground = Color.WHITE
        }
    }

    override fun setSelectedBackground(label: JLabel, selected: Boolean) {
        label.apply {
            background = if (selected) Color(0.29f, 0.29f, 0.29f) else null
            isOpaque = selected
        }
    }

    override fun setBorder(component: JComponent) {
        component.border = EmptyBorder(5, 15, 5, 15)
    }
}