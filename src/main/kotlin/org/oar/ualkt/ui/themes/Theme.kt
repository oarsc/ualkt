package org.oar.ualkt.ui.themes

import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.stage.Stage
import org.oar.ualkt.ui.OptionUI
import javafx.scene.control.ScrollPane as FXScrollPane
import javafx.scene.control.TextField as FXTextField

object Themes {
    lateinit var theme: FXTheme
    val stackOnTop: Boolean get() = theme.stackOnTop
    val iconSize: Int get() = theme.iconSize

    fun Stage.themedSize(items: Int = 0) = theme.setSize(this, items)
    fun Stage.themedPosition() = theme.setPosition(this)
    fun Scene.themedBackground() = theme.setBackground(this)
    fun TextField.themedTextSize() = theme.setTextStyle(this)
    fun TextField.themedSize() = theme.setSize(this)
    fun ScrollPane.themedSize(items: Int = 0) = theme.setSize(this, items)
    fun ScrollPane.themedStyle() = theme.setStyle(this)
    fun OptionUI.themedBackground(selected: Boolean) = theme.setBackground(this, selected)
    fun OptionUI.themedSize() = theme.setSize(this)
    fun ImageView.themedImageBorder() = theme.setImageBorder(this)
    fun Label.themedStyle(selected: Boolean) = theme.setStyle(this, selected)
}

interface FXTheme {
    val stackOnTop: Boolean
    val iconSize: Int
    fun setSize(stage: Stage, items: Int = 0)
    fun setPosition(stage: Stage)
    //    fun setMainPosition(stage: Stage)
    fun setBackground(scene: Scene)
    //    fun setBorder(component: FXPane)
    fun setTextStyle(textField: FXTextField)
    fun setSize(textField: FXTextField)
//    fun setTextStyle(label: FXLabel)
//    fun setFocusTextStyle(label: FXLabel)
//    fun setIconSize(label: FXLabel)
//    fun setSelectedBackground(panel: FXPane, selected: Boolean)
//    fun setSize(panel: FXPane)
    fun setSize(scrollPane: FXScrollPane, items: Int = 0)
    fun setStyle(scrollPane: FXScrollPane)
    fun setBackground(hBox: HBox, selected: Boolean)
    fun setSize(hBox: HBox)
    fun setImageBorder(imageView: ImageView)
    fun setStyle(label: Label, selected: Boolean)
}