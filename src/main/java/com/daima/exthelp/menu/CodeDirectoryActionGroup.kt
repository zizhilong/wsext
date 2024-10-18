package com.daima.exthelp.menu

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorModificationUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.openapi.ide.CopyPasteManager
import java.awt.datatransfer.StringSelection

class CodeGenerationActionGroup : ActionGroup() {

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        val project = e?.project ?: return emptyArray()
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return emptyArray()
        val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.document) ?: return emptyArray()

        val docsDirectory = getDocsCodeDirectory(project)

        // 创建“代码生成”主菜单
        val codeGenerationMenu = DefaultActionGroup("代码生成", true)

        // 如果 docs/code 目录存在，则递归遍历，构建子菜单
        docsDirectory?.takeIf { it.exists() }?.let {
            buildMenuTree(it, codeGenerationMenu)
        }

        // 动态构建 "复制为 import" 菜单项，且确保不重复添加
        val menuItems = mutableListOf<AnAction>()
        menuItems.add(codeGenerationMenu)

        // 确保 "复制为 import" 只添加一次，避免重复
        if (isExportFunctionAtCaret(editor, psiFile) && !menuItems.any { it is CopyAsImportAction }) {
            menuItems.add(CopyAsImportAction(editor, psiFile))
        }

        // 返回菜单项
        return menuItems.toTypedArray()
    }

    // 递归构建子菜单树
    private fun buildMenuTree(directory: VirtualFile, parentGroup: DefaultActionGroup) {
        directory.children.forEach { file ->
            if (file.isDirectory) {
                // 如果是目录，创建子菜单并递归处理子目录
                val subGroup = DefaultActionGroup(file.name, true)
                buildMenuTree(file, subGroup)
                parentGroup.add(subGroup)
            } else if (file.extension == "txt") {
                // 如果是 .txt 文件，将其作为叶子菜单项
                parentGroup.add(InsertFileContentAction(file))
            }
        }
    }

    // 获取项目根目录下的 docs/code 目录
    private fun getDocsCodeDirectory(project: Project): VirtualFile? {
        return VfsUtil.findRelativeFile(project.baseDir, "docs", "code")
    }

    // 检查当前光标是否位于 `export function` 上
    private fun isExportFunctionAtCaret(editor: Editor, psiFile: PsiFile): Boolean {
        val caretOffset = editor.caretModel.offset
        val elementAtCaret = psiFile.findElementAt(caretOffset) ?: return false

        // 检查是否为 `export function`
        val functionElement = PsiTreeUtil.getParentOfType(elementAtCaret, PsiElement::class.java) ?: return false
        return functionElement.text.startsWith("export function")
    }
}

// 动态创建“复制为 import”的菜单项
class CopyAsImportAction(private val editor: Editor, private val psiFile: PsiFile) : AnAction("复制为 import") {

    override fun actionPerformed(e: AnActionEvent) {
        // 获取当前光标的位置
        val caretOffset = editor.caretModel.offset
        // 获取光标所在位置的 PsiElement
        val elementAtCaret = psiFile.findElementAt(caretOffset)

        elementAtCaret?.let { element ->
            // 获取父节点，确保是 PsiNamedElement，可以准确获取函数名
            val functionElement = PsiTreeUtil.getParentOfType(element, com.intellij.psi.PsiNamedElement::class.java)
            val functionName = functionElement?.name  // 直接获取函数名

            // 获取项目相对路径（不包含项目根路径）
            val currentFile = psiFile.virtualFile
            val project = e.project ?: return
            val relativePath = VfsUtil.getRelativePath(currentFile, project.baseDir!!) ?: return

            functionName?.let { name ->
                // 生成相对于项目根目录的 import 语句
                val importStatement = "import { $name } from '/$relativePath';"

                // 将 import 语句复制到剪贴板
                CopyPasteManager.getInstance().setContents(StringSelection(importStatement))

                // 插入 import 语句到当前光标位置
                EditorModificationUtil.insertStringAtCaret(editor, importStatement)
            }
        }
    }
}
