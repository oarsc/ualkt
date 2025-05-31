package org.oar.ualkt.ui

import javafx.application.Platform
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.scene.Scene
import javafx.scene.control.IndexRange
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.VBox
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.oar.ualkt.model.CommandWithSearchResults
import org.oar.ualkt.model.SearchLevel
import org.oar.ualkt.services.controller.Controller
import org.oar.ualkt.ui.themes.Themes
import org.oar.ualkt.ui.themes.Themes.themedBackground
import org.oar.ualkt.ui.themes.Themes.themedPosition
import org.oar.ualkt.ui.themes.Themes.themedSize
import org.oar.ualkt.ui.themes.Themes.themedStyle
import org.oar.ualkt.ui.themes.Themes.themedTextSize
import org.oar.ualkt.utils.Constants.APP_NAME

class MainUI(
    private val stage: Stage
) {
    lateinit var controller: Controller
    private val options = mutableListOf<OptionUI>()
    private val listener = OnChangeListener()

    private val inputText: TextField = TextField().apply {
        themedTextSize()
        themedSize()

        addEventFilter(KeyEvent.KEY_PRESSED) { event ->
            when (event.code) {
                KeyCode.DOWN -> {
                    if (Themes.stackOnTop) controller.onPrev() else controller.onNext()
                    event.consume()
                }
                KeyCode.UP -> {
                    if (Themes.stackOnTop) controller.onNext() else controller.onPrev()
                    event.consume()
                }
                KeyCode.ESCAPE -> controller.onEscape()
                KeyCode.ENTER -> {
                    val accepted = controller.onEnter(text)
                    if (accepted) controller.onEscape()
                }
                KeyCode.TAB -> {
                    if (selection.length == 0) {
                        val resolved = controller.resolve(text)
                        if (resolved == null) {
                            controller.onChangedInput(text, false)
                        } else {
                            text = resolved
                        }
                    } else {
                        selectRange(selection.end, selection.end)
                    }
                }
                else -> {}
            }
        }

        focusedProperty().addListener { _, oldValue, newValue ->
            if (oldValue && !newValue) {
                hideWindow()
            }
        }

        selectionProperty().addListener(listener)
    }
    private val optionsPanel: VBox = VBox()

    private val optionsScrollPane: ScrollPane = ScrollPane(optionsPanel).apply {
        isFitToWidth = true
        themedSize(0)
        themedStyle()

        hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
        vbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
    }
    private val scene: Scene


    init {
        val root = if (Themes.stackOnTop) {
            VBox(optionsScrollPane, inputText)
        } else {
            VBox(inputText, optionsScrollPane)
        }
        scene = Scene(root).apply {
            themedBackground()
        }
        stage.apply {
            themedSize(0)
            themedPosition()

            initStyle(StageStyle.UNDECORATED)
            scene = this@MainUI.scene
            title = APP_NAME
            isMaximized = false
            isIconified = true
            isResizable = false
            isAlwaysOnTop = true
        }
    }

    fun hideWindow() {
        stage.isIconified = true
        stage.hide()
    }

    fun showWindow() {
        inputText.text = ""
        updateOptions()

        stage.show()
        stage.isIconified = false
        inputText.requestFocus()
    }

    fun replaceOptions(options: List<CommandWithSearchResults>, selectIndex: Int = 0) {
        this.options.clear()
        this.options.addAll(
            options.mapIndexed { idx, it ->
                OptionUI(it, idx == selectIndex)
            }
        )
        updateOptions()
        scrollTo(selectIndex)
    }

    fun changeSelection(previousSelected: Int, selected: Int) {
        options[previousSelected].selected = false
        options[selected].selected = true
        scrollTo(selected)
    }

    private fun updateOptions() {
        Platform.runLater {
            optionsPanel.children.clear()
            options.forEach { optionUI ->
                if (Themes.stackOnTop) optionsPanel.children.add(0, optionUI)
                else optionsPanel.children.add(optionUI)
            }
        }
        optionsScrollPane.themedSize(options.size)
        stage.themedSize(options.size)
        stage.themedPosition()
    }

    fun updatePlaceholder(selected: Int) {
        val option = options[selected]
        val command = option.option.command
        val searchResultLevel = option.option.searchResults.level

        inputText.apply {
            val originText = unselectedText

            val newText =
                if (command.title == command.keyWord && searchResultLevel == SearchLevel.STARTING)
                    originText + command.title.substring(originText.length)
                else
                    originText

            selectionProperty().removeListener(listener)
            Platform.runLater {
                text = newText
                positionCaret(newText.length)
                selectRange(originText.length, newText.length)
                selectionProperty().addListener(listener)
            }
        }
    }

    private fun scrollTo(index: Int) {
        val endValue: Double
        if (index == 0) {
            endValue = if (Themes.stackOnTop) 1.0 else 0.0

        } else if (index >= 0 && index < options.size) {

            val selectedOptionComponent = options[index]

            val scrollPaneHeight = optionsScrollPane.height
            val currentScrollY = optionsScrollPane.vvalue

            val optionHeight = selectedOptionComponent.height
            val optionY = selectedOptionComponent.boundsInParent.minY

            val offset = (scrollPaneHeight - optionHeight) / 2

            val targetScrollY = when {
                optionY - offset < currentScrollY * (optionsPanel.height - scrollPaneHeight) -> {
                    // Scroll up to bring the option into view
                    (optionY - offset) / (optionsPanel.height - scrollPaneHeight)
                }
                optionY + offset + optionHeight > currentScrollY * (optionsPanel.height - scrollPaneHeight) + scrollPaneHeight -> {
                    // Scroll down to bring the option into view
                    (optionY + offset + optionHeight - scrollPaneHeight) / (optionsPanel.height - scrollPaneHeight)
                }
                else -> return
            }

            endValue = targetScrollY.coerceIn(0.0, 1.0)

        } else return

//            optionsScrollPane.vvalue = endValue
        // smoothScroll:
        val animationDuration = 50.0 // milliseconds
        val startValue = optionsScrollPane.vvalue

        val timeline = javafx.animation.Timeline(
            javafx.animation.KeyFrame(
                javafx.util.Duration.ZERO,
                javafx.animation.KeyValue(optionsScrollPane.vvalueProperty(), startValue)
            ),
            javafx.animation.KeyFrame(
                javafx.util.Duration(animationDuration),
                javafx.animation.KeyValue(optionsScrollPane.vvalueProperty(), endValue)
            )
        )
        timeline.play()
    }

    private val TextField.unselectedText: String get() = run {
        when {
            selection.length == 0 -> text
            selection.end == text.length -> text.substring(0, selection.start)
            else -> text
        }
    }


    inner class OnChangeListener: ChangeListener<IndexRange> {
        override fun changed(observableValue: ObservableValue<out IndexRange>, oldValue: IndexRange, newValue: IndexRange) {
            if (newValue.length == 0) {
                val removed =
                    if (oldValue.length > 0) // if removing selected text
                        oldValue.start == newValue.start
                    else                     // if removing character
                        newValue.start < oldValue.start

                controller.onChangedInput(inputText.text, removed)
            }
        }
    }
}