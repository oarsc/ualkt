package org.oar.ualkt.ui.themes

import javax.swing.JComponent
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JTextField

object Themes {
    lateinit var theme: Theme

    val stackOnTop: Boolean get() = theme.stackOnTop
    fun JFrame.themedSize(items: Int = 0) = theme.setSize(this, items)
    fun JFrame.themedPosition() = theme.setMainPosition(this)
    fun JFrame.themedBackground() = theme.setBackground(this)
    fun JComponent.themedBorder() = theme.setBorder(this)
    fun JTextField.themedTextStyle() = theme.setTextStyle(this)
    fun JLabel.themedSize() = theme.setSize(this)
    fun JLabel.themedTextStyle() = theme.setTextStyle(this)
    fun JLabel.themedSelectedBackground(selected: Boolean) = theme.setSelectedBackground(this, selected)
}

interface Theme {
    val stackOnTop: Boolean
    fun setSize(frame: JFrame, items: Int = 0)
    fun setMainPosition(frame: JFrame)
    fun setBackground(frame: JFrame)
    fun setBorder(component: JComponent)
    fun setTextStyle(textField: JTextField)
    fun setSize(label: JLabel)
    fun setTextStyle(label: JLabel)
    fun setSelectedBackground(label: JLabel, selected: Boolean)
}