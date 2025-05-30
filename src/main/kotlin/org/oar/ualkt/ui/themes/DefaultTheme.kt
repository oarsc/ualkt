package org.oar.ualkt.ui.themes

import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.HBox.setMargin
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.scene.control.Label as FXLabel
import javafx.scene.control.ScrollPane as FXScrollPane
import javafx.scene.control.TextField as FXTextField
import javafx.scene.layout.Pane as FXPane

class FXDefaultTheme(
    private val width: Int,
    private val inputHeight: Int,
    private val height: Int,
    private val maxElements: Int
): FXTheme {
    override val stackOnTop = false
    override val iconSize = 32
    private val backgroundColor = "#252525"

    override fun setSize(stage: Stage, items: Int) {
        stage.width = width.toDouble()
        stage.height = (inputHeight + items.coerceAtMost(maxElements) * height).toDouble()
    }

    override fun setPosition(stage: Stage) {
        stage.centerOnScreen()
//        val screen = javafx.stage.Screen.getPrimary().visualBounds
//        stage.x = (screen.width - width) / 2
//        stage.y = (screen.height - height) / 2 - 200
    }

    override fun setBackground(scene: Scene) {
        scene.apply {
            root.style = "-fx-background-color: $backgroundColor;"
            fill = Color.web(backgroundColor)
        }
    }

    override fun setTextStyle(textField: FXTextField) {
        textField.style = "-fx-padding: 0 15px; -fx-font-size: 18px; -fx-text-fill: white; -fx-background-color: $backgroundColor;"
    }

    override fun setSize(textField: FXTextField) {
        textField.prefHeight = inputHeight.toDouble()
    }

    private fun setTextStyle(label: FXLabel) {
        label.style = "-fx-font-size: 16px; -fx-text-fill: #c8c8c8;"
    }

    private fun setFocusTextStyle(label: FXLabel) {
        label.style = "-fx-font-weight: bold; -fx-text-fill: white;"
    }

    private fun setSelectedBackground(panel: FXPane, selected: Boolean) {
        panel.style = if (selected) "-fx-background-color: #4a4a4a;" else "-fx-background-color: $backgroundColor;"
    }

    private fun setSize(panel: FXPane) {
        panel.prefWidth = width.toDouble()
        panel.prefHeight = height.toDouble()
    }

    private fun setIconSize(label: FXLabel) {
        label.prefWidth = (iconSize + 30).toDouble()
        label.prefHeight = 32.0
    }

    private fun setBorder(component: FXPane) {
        component.padding = Insets(5.0, 15.0, 5.0, 15.0)
    }

    override fun setSize(scrollPane: FXScrollPane, items: Int) {
        scrollPane.prefWidth = width.toDouble()
        scrollPane.prefHeight = (items.coerceAtMost(maxElements) * height).toDouble()
        scrollPane.minHeight = 0.0
    }

    override fun setStyle(scrollPane: FXScrollPane) {
        scrollPane.style = "-fx-padding: 0;"
    }

    override fun setBackground(hBox: HBox, selected: Boolean) {
        hBox.style = if (selected) "-fx-background-color: #4a4a4a;" else "-fx-background-color: $backgroundColor;"
    }
    override fun setSize(hBox: HBox) {
        val doubleHeight = height.toDouble()
        hBox.apply {
            setPrefSize(Int.MAX_VALUE.toDouble(), doubleHeight)
            setMinSize(Int.MAX_VALUE.toDouble(), doubleHeight)
        }
    }
    override fun setImageBorder(imageView: ImageView) {
        setMargin(imageView, Insets(0.0, 15.0, 0.0, 15.0))
    }

    override fun setStyle(label: Label, selected: Boolean) {
        label.style = buildString {
//            append("-fx-font-size: 16px; -fx-text-fill: #c0c0c0; -fx-text-overrun: clip;")
            append("-fx-font-size: 16px;")
            append(
                if (selected)
                    " -fx-text-fill: white; -fx-font-weight: bold;"
                else
                    "-fx-text-fill: #c0c0c0;"
            )
        }
    }
}