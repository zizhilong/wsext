package com.daima.exthelp.rename

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import javax.swing.*

class CustomRenameDialog(project: Project, private val initialName: String) : DialogWrapper(project) {

    private val newNameField = JTextField(initialName)

    init {
        title = "Rename Element" // 对话框标题
        init()
    }

    override fun createCenterPanel(): JComponent {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

        val label = JLabel("Enter new name:")
        panel.add(label)
        panel.add(newNameField)

        return panel
    }

    fun getNewName(): String {
        return newNameField.text
    }
}