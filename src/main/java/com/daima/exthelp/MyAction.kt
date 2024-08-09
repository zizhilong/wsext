package com.daima.exthelp

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.ui.Messages

class MyAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        // 获取编辑器实例并显示提示
        val editor = EditorFactory.getInstance().createEditor(EditorFactory.getInstance().createDocument(""))
        Messages.showMessageDialog(project, "This is an editor message", "Editor Prompt", Messages.getInformationIcon())
    }
}