package org.oar.ualkt.ui

import org.oar.ualkt.model.CommandWithSearchResults
import org.oar.ualkt.model.SearchLevel
import org.oar.ualkt.services.controller.Controller
import org.oar.ualkt.ui.themes.Themes
import org.oar.ualkt.ui.themes.Themes.themedBackground
import org.oar.ualkt.ui.themes.Themes.themedBorder
import org.oar.ualkt.ui.themes.Themes.themedPosition
import org.oar.ualkt.ui.themes.Themes.themedSize
import org.oar.ualkt.ui.themes.Themes.themedTextStyle
import org.oar.ualkt.utils.Constants.APP_NAME
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.BoxLayout
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextField
import javax.swing.SwingUtilities
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener


class MainUI {
    lateinit var controller: Controller

    private val options = mutableListOf<OptionUI>()
    private var skipNextOnChange = 0
    private val listener = OnChangeDocumentListener()

    private val frame = JFrame(APP_NAME).apply {
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        isResizable = false
        isUndecorated = true
        contentPane.layout = BoxLayout(contentPane, BoxLayout.PAGE_AXIS)

        themedSize()
        themedPosition()
        themedBackground()
    }

    private val inputText = JTextField().apply {
        preferredSize = frame.size
        isOpaque = false

        themedTextStyle()
        themedBorder()

        addFocusListener(object : FocusListener {
            override fun focusGained(event: FocusEvent) {}
            override fun focusLost(event: FocusEvent) {
                controller.onEscape()
            }
        })

        focusTraversalKeysEnabled = false
        document.addDocumentListener(listener)

        addKeyListener(object : KeyAdapter() {
            override fun keyPressed(event: KeyEvent) {
                when (event.keyCode) {
                    KeyEvent.VK_ESCAPE -> controller.onEscape()
                    KeyEvent.VK_ENTER -> {
                        val accepted = controller.onEnter(text)
                        if (accepted) {
                            controller.onEscape()
                        } else {
                            // animate wrong input
                        }
                    }
                    KeyEvent.VK_DOWN -> {
                        if (Themes.stackOnTop) controller.onPrev()
                        else controller.onNext()
                    }
                    KeyEvent.VK_UP -> {
                        if (Themes.stackOnTop) controller.onNext()
                        else controller.onPrev()
                    }
                    KeyEvent.VK_TAB -> {
                        selectionStart = selectionEnd
                    }
                }
            }
        })

        frame.contentPane.add(this)
    }

    private val optionsPanel = JPanel().apply {
        isOpaque = false
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
//        frame.contentPane.add(this)
    }

    private val optionsScrollPane = JScrollPane(optionsPanel).apply {
        isOpaque = false
        viewport.isOpaque = false
        border = null

        horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER

        themedSize(0)

        frame.contentPane.add(this)
    }

    fun hideWindow() {
        frame.isVisible = false
    }

    fun showWindow() {
        if (frame.isVisible) return
        inputText.text = ""
        updateOptions()
        frame.isVisible = true
        inputText.grabFocus()
    }

    fun replaceOptions(options: List<CommandWithSearchResults>, selectIndex: Int = 0) {
        this.options.clear()
        this.options.addAll(
            options.mapIndexed { idx, it ->
                OptionUI(it, idx == selectIndex)
            }
        )
        updateOptions()
        optionsPanel.scrollTo(selectIndex)
    }

    fun changeSelection(previousSelected: Int, selected: Int) {
        options[previousSelected].selected = false
        options[selected].selected = true

        optionsPanel.scrollTo(selected)
    }

    private fun updateOptions() {
        optionsPanel.apply {
            removeAll()
            options.forEach { optionUI ->
                if (Themes.stackOnTop) add(optionUI, 0)
                else add(optionUI)
            }
        }
        optionsScrollPane.themedSize(options.size)
        frame.themedSize(options.size)
    }

    fun updatePlaceholder(selected: Int) {
        val option = options[selected]
        val command = option.option.command
        val searchResultLevel = option.option.searchResults.level

        inputText.apply {
            if (selectionStart == selectionEnd && caretPosition < text.length - 1) return

            val originText = unselectedText
            val text =
                if (command.title == command.keyWord && searchResultLevel == SearchLevel.STARTING)
                    originText + command.title.substring(originText.length)
                else
                    originText

            SwingUtilities.invokeLater {
                inputText.document.removeDocumentListener(listener)
                inputText.text = text
                inputText.caretPosition = text.length          // startPosition
                inputText.moveCaretPosition(originText.length) // endPosition
                inputText.document.addDocumentListener(listener)
            }
        }
    }


    private val JTextField.unselectedText: String get() {
        val start = selectionStart
        val end = selectionEnd
        return when {
            start == end -> text
            end == text.length -> text.substring(0, start)
            else -> text
        }
    }

    private fun JPanel.scrollTo(index: Int) {
        if (index >= 0 && index < this@MainUI.options.size) {
            val selectedOptionComponent = options[index]
            SwingUtilities.invokeLater {
                if (selectedOptionComponent.isShowing) {
                    scrollRectToVisible(selectedOptionComponent.bounds)
                }
            }
        }
    }

    inner class OnChangeDocumentListener: DocumentListener {
        override fun insertUpdate(e: DocumentEvent) = onChange()
        override fun removeUpdate(e: DocumentEvent) = onChange(true)
        override fun changedUpdate(e: DocumentEvent) = onChange()

        private fun onChange(remove: Boolean = false) {
            controller.onChangedInput(inputText.unselectedText, remove)
        }
    }
}