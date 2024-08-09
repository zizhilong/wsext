package com.daima.exthelp.Tools

import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiUtilBase

fun selectAndHighlightPsiElement(project: Project, psiElement: PsiElement) {
        // 获取Psi文件和编辑器
        val psiFile = psiElement.containingFile
        val editor = PsiUtilBase.findEditor(psiFile) ?: return

        // 检查editor是否属于当前活动的编辑区
        val currentEditor = FileEditorManager.getInstance(project).selectedTextEditor
        if (currentEditor != editor) {
            // 获取VirtualFile
            val virtualFile: VirtualFile = psiFile.virtualFile
            // 跳转到该文件并激活编辑器
            FileEditorManager.getInstance(project).openTextEditor(OpenFileDescriptor(project, virtualFile), true)
        }

        // 获取PsiElement的文本范围
        val textRange: TextRange = psiElement.textRange

        // 选中并滚动到PsiElement
        editor.selectionModel.setSelection(textRange.startOffset, textRange.endOffset)
        editor.caretModel.moveToOffset(textRange.startOffset)
        editor.scrollingModel.scrollToCaret(ScrollType.CENTER)

        /*
        // 高亮PsiElement
        val highlighter = editor.markupModel.addRangeHighlighter(
            textRange.startOffset,
            textRange.endOffset,
            0,
            editor.colorsScheme.getAttributes(EditorEx.ERROR_STRIPE_MARKER),
            null
        )

        */

        // 你可以添加更多的高亮和样式配置
    }