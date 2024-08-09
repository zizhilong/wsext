package com.daima.exthelp.ext

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.CaretModel
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.openapi.diagnostic.Logger
import com.daima.exthelp.Tools.ExpHelper

class FindPsiAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        // 获取当前项目
        val project: Project = event.project ?: return

        // 获取当前编辑器
        val editor: Editor? = event.getData(com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR)
        if (editor == null) {
            Logger.getInstance(FindPsiAction::class.java).warn("Editor not found")
            return
        }

        // 获取当前文档和光标位置
        val document = editor.document
        val caretModel: CaretModel = editor.caretModel
        val offset = caretModel.offset

        // 从文档中获取当前 PsiFile
        val psiFile: PsiFile? = PsiDocumentManager.getInstance(project).getPsiFile(document)
        if (psiFile == null) {
            Logger.getInstance(FindPsiAction::class.java).warn("PsiFile not found")
            return
        }

        // 获取光标位置的 PsiElement
        val psiElement: PsiElement? = psiFile.findElementAt(offset)
        if (psiElement == null) {
            Logger.getInstance(FindPsiAction::class.java).warn("PsiElement not found at caret position")
            return
        }

        // 调用之前编写的 logPsiHierarchy 方法进行查询
        ExpHelper.logPsiHierarchy(psiElement)
    }
}