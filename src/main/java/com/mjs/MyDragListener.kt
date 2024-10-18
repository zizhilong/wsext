package com.mjs

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.editor.event.EditorMouseListener
import com.intellij.openapi.editor.event.EditorMouseEventArea
import com.intellij.openapi.project.Project
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.vfs.VfsUtilCore

class MyDragListener : EditorMouseListener {

    private var draggedFunctionName: String? = null
    private var sourceFile: PsiFile? = null

    override fun mouseReleased(event: EditorMouseEvent) {
        val editor = event.editor
        val project = editor.project ?: return
        val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.document) ?: return

        if (event.area == EditorMouseEventArea.EDITING_AREA) {
            val caretOffset = editor.caretModel.offset
            val elementAtCaret = psiFile.findElementAt(caretOffset)

            elementAtCaret?.let { element ->
                val parent = element.parent
                if (parent is PsiElement) {
                    draggedFunctionName = getFunctionNameIfExported(parent)
                    if (draggedFunctionName != null) {
                        sourceFile = psiFile
                    }
                }
            }
        }
    }

    private fun getFunctionNameIfExported(element: PsiElement): String? {
        // 通过 PSI 解析函数，并确认它是否为导出的函数
        return if (isExportedFunction(element)) {
            PsiTreeUtil.getParentOfType(element, PsiElement::class.java)?.text // 解析出函数名
        } else {
            null
        }
    }

    private fun isExportedFunction(element: PsiElement): Boolean {
        // 检查 PSI 元素是否为导出的函数 (类似 export function ...)
        // 根据具体的 PSI 层次结构实现解析
        return element.text.startsWith("export")
    }

    fun onDragFinished(editor: Editor) {
        val project = editor.project ?: return
        val targetFile = getTargetFile(editor) ?: return

        sourceFile?.let { source ->
            val relativePath = calculateRelativePath(targetFile, source.virtualFile)
            relativePath?.let {
                val importStatement = "import { $draggedFunctionName } from \"$it\";"
                insertImportStatement(project, editor, importStatement)
            }
        }
    }

    private fun getTargetFile(editor: Editor): VirtualFile? {
        return FileDocumentManager.getInstance().getFile(editor.document)
    }

    private fun calculateRelativePath(targetFile: VirtualFile, sourceFile: VirtualFile): String? {
        return VfsUtilCore.findRelativePath(targetFile, sourceFile, '/')
    }

    private fun insertImportStatement(project: Project, editor: Editor, importStatement: String) {
        WriteCommandAction.runWriteCommandAction(project) {
            val document = editor.document
            document.insertString(0, "$importStatement\n")
        }
    }
}