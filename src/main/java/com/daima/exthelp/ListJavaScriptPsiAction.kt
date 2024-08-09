package com.daima.exthelp

import com.intellij.lang.javascript.psi.JSFile
import com.intellij.lang.javascript.psi.JSObjectLiteralExpression
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import javax.swing.SwingUtilities

class ListJavaScriptPsiAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {

        // 获取当前项目
        val project = e.project ?: return

        // 获取当前文件编辑器管理器实例
        val fileEditorManager = FileEditorManager.getInstance(project)

        // 获取当前选中的文件（虚拟文件）
        val virtualFile: VirtualFile = fileEditorManager.selectedFiles.firstOrNull() ?: return

        // 通过虚拟文件找到对应的 PsiFile
        val psiFile: PsiFile = PsiManager.getInstance(project).findFile(virtualFile) ?: return

        // 获取当前选中的编辑器
        val editor: Editor? = fileEditorManager.selectedTextEditor

        // 如果没有找到编辑器，显示错误信息并返回
        if (editor == null) {
            Messages.showMessageDialog(project, "No editor found", "Error", Messages.getErrorIcon())
            return
        }

        // 获取光标所在的位置
        val caretOffset = editor.caretModel.offset

        // 根据光标位置找到 PsiElement
        val elementAtCaret = psiFile.findElementAt(caretOffset) ?: return

        try {
            // 检查 PsiFile 是否是 JSFile 的实例
            if (psiFile is JSFile) {
                // 在一个新的线程中执行代码
                ApplicationManager.getApplication().executeOnPooledThread {
                    val psiElementTypes = StringBuilder()
                    val lineSeparator = System.lineSeparator()
                    var currentElement: PsiElement? =
                    elementAtCaret
                    // 向上遍历 Psi 树，直到根节点
                    while (currentElement != null) {
                        val elementType = currentElement::class.java.simpleName

                        // 忽略指定的类型
                        if (elementType != "PsiWhiteSpaceImpl" && elementType != "JSFileImpl" && elementType != "PsiJavaDirectoryImpl") {
                            // 添加类型到结果中，并在行末添加字符 |
                            psiElementTypes.append(elementType).append(" |").append(lineSeparator)

                            // 如果是 JSObjectLiteralExpression，输出其所有属性
                            if (currentElement is JSObjectLiteralExpression) {
                                currentElement.properties.forEach { property ->
                                    psiElementTypes.append("Property: ${property.name} = ${property.value?.text} |").append(lineSeparator)
                                }
                            }
                        }

                        currentElement = currentElement.parent
                    }

                    // 在事件调度线程上显示结果
                    SwingUtilities.invokeLater {
                        Messages.showMessageDialog(
                            project,
                            psiElementTypes.toString(),
                            "JavaScript PSI Element Types",
                            Messages.getInformationIcon()
                        )
                    }
                }
            } else {
                Messages.showMessageDialog(project, "Not a JavaScript file", "Error", Messages.getErrorIcon())
            }
        } catch (e: Exception) {
            // 捕获所有类型的异常并显示错误信息
            Messages.showMessageDialog(project, "Exception: ${e.message}", "Error", Messages.getErrorIcon())
        }
    }


}