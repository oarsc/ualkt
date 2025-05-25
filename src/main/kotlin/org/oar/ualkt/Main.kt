package org.oar.ualkt

import org.oar.ualkt.services.controller.Controller
import org.oar.ualkt.services.keysListener.GlobalKeyListener
import org.oar.ualkt.ui.MainUI
import org.oar.ualkt.ui.themes.DefaultTheme
import org.oar.ualkt.ui.themes.Themes
import org.oar.ualkt.utils.Constants.APP_NAME
import javax.swing.SwingUtilities

fun main() {
    System.setProperty("awtAppClassName", APP_NAME)
    Themes.theme = DefaultTheme(800, 50, 40, 17)

//    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

    SwingUtilities.invokeLater {
        val ui = MainUI()
        ui.controller = Controller(ui)

        GlobalKeyListener.register {
            ui.showWindow()
        }
    }
}
