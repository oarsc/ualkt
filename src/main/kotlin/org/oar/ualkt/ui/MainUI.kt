package org.oar.ualkt.ui

import org.oar.ualkt.commands.Command
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
import javax.swing.JTextField
import javax.swing.event.DocumentEvent

class MainUI {
    lateinit var controller: Controller

    private val options = mutableListOf<OptionUI>()

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

        document.addDocumentListener(object : javax.swing.event.DocumentListener {
            override fun insertUpdate(e: DocumentEvent) = onChange()
            override fun removeUpdate(e: DocumentEvent) = onChange()
            override fun changedUpdate(e: DocumentEvent) = onChange()

            private fun onChange() {
                controller.onChangedInput(unselectedText)
            }
        })

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
                }
            }
        })

        frame.contentPane.add(this)
    }

    private val optionsPanel = JPanel().apply {
        isOpaque = false
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        frame.contentPane.add(this)
    }

    fun hideWindow() {
        frame.isVisible = false
    }

    fun showWindow() {
        if (frame.isVisible) return
        frame.isVisible = true
        inputText.text = ""
        inputText.grabFocus()
        updateOptions()
    }

    fun replaceOptions(options: List<Command>, selectIndex: Int = 0) {
        this.options.clear()
        this.options.addAll(
            options.mapIndexed { idx, it ->
                OptionUI(it, idx == selectIndex)
            }
        )
        updateOptions()
    }

    fun changeSelection(previousSelected: Int, selected: Int) {
        options[previousSelected].selected = false
        options[selected].selected = true
    }

    private fun updateOptions() {
        optionsPanel.apply {
            removeAll()
            options.forEach {
                if (Themes.stackOnTop) add(it, 0)
                else add(it)
            }
        }
        frame.themedSize(options.size)
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
}
