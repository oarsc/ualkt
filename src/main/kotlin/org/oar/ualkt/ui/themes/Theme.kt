package org.oar.ualkt.ui.themes

import javax.swing.JComponent
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextField

object Themes {
    lateinit var theme: Theme

    val stackOnTop: Boolean get() = theme.stackOnTop
    val iconSize: Int get() = theme.iconSize

    fun JFrame.themedSize(items: Int = 0) = theme.setSize(this, items)
    fun JFrame.themedPosition() = theme.setMainPosition(this)
    fun JFrame.themedBackground() = theme.setBackground(this)
    fun JComponent.themedBorder() = theme.setBorder(this)
    fun JTextField.themedTextStyle() = theme.setTextStyle(this)
    fun JLabel.themedTextStyle() = theme.setTextStyle(this)
    fun JLabel.themedFocusTextStyle() = theme.setFocusTextStyle(this)
    fun JLabel.themedIconSize() = theme.setIconSize(this)
    fun JPanel.themedSelectedBackground(selected: Boolean) = theme.setSelectedBackground(this, selected)
    fun JPanel.themedSize() = theme.setSize(this)
    fun JScrollPane.themedSize(items: Int = 0) = theme.setSize(this, items)
}

interface Theme {
    val stackOnTop: Boolean
    val iconSize: Int
    fun setSize(frame: JFrame, items: Int = 0)
    fun setMainPosition(frame: JFrame)
    fun setBackground(frame: JFrame)
    fun setBorder(component: JComponent)
    fun setTextStyle(textField: JTextField)
    fun setTextStyle(label: JLabel)
    fun setFocusTextStyle(label: JLabel)
    fun setIconSize(label: JLabel)
    fun setSelectedBackground(panel: JPanel, selected: Boolean)
    fun setSize(panel: JPanel)
    fun setSize(scrollPane: JScrollPane, items: Int = 0)
}