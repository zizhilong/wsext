package com.daima.exthelp.rundebugplugin

import com.intellij.openapi.options.SettingsEditor
import javax.swing.JComponent
import javax.swing.JPanel


class MyRunSettingsEditor : SettingsEditor<MyRunConfiguration>() {
    override fun resetEditorFrom(s: MyRunConfiguration) {
        // Reset the editor state from the configuration
    }

    override fun applyEditorTo(s: MyRunConfiguration) {
        // Apply the editor state to the configuration
    }

    override fun createEditor(): JComponent {
        return JPanel()
    }
}