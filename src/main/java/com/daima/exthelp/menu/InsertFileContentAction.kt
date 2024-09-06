package com.daima.exthelp.menu

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorModificationUtil
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.util.text.StringUtil
import java.io.IOException

class InsertFileContentAction(private val file: VirtualFile) : AnAction(file.nameWithoutExtension) {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = getCurrentEditor(project) ?: return

        try {
            // 读取文件内容
            var content = VfsUtil.loadText(file)

            // 将文件中的行分隔符转换为 \n
            content = StringUtil.convertLineSeparators(content)

            // 使用 WriteCommandAction 包裹文档修改操作
            WriteCommandAction.runWriteCommandAction(project) {
                EditorModificationUtil.insertStringAtCaret(editor, content)
            }

        } catch (ioException: IOException) {
            Messages.showErrorDialog("Failed to read file: ${file.name}", "Error")
        }
    }

    // 获取当前打开的编辑器
    private fun getCurrentEditor(project: Project): Editor? {
        return FileEditorManager.getInstance(project).selectedTextEditor
    }
}