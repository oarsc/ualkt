package org.oar.ualkt

import javafx.application.Application
import javafx.application.Platform
import javafx.stage.Stage
import org.oar.ualkt.services.controller.Controller
import org.oar.ualkt.services.keysListener.GlobalKeyListener
import org.oar.ualkt.ui.MainUI
import org.oar.ualkt.ui.themes.FXDefaultTheme
import org.oar.ualkt.ui.themes.Themes

class UalktApp : Application() {
    private lateinit var ui: MainUI
    override fun start(primaryStage: Stage) {
        Themes.theme = FXDefaultTheme(800, 50, 40, 17)

        ui = MainUI(primaryStage)

        ui.controller = Controller(ui)

        Platform.setImplicitExit(false)
        GlobalKeyListener.register {
            Platform.runLater { ui.showWindow() }
        }
    }
}

fun main(args: Array<String>) {
    Application.launch(UalktApp::class.java, *args)
}
