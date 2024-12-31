package com.daima.exthelp.rename

import com.intellij.lang.javascript.psi.ecmal4.JSClass
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiNamedElement
import com.intellij.refactoring.rename.RenameDialog
import com.intellij.refactoring.rename.RenameHandler

// ExtJSRenameHandler 实现了 RenameHandler 接口，用于处理 ExtJS 的重命名逻辑
class ExtJSRenameHandler : RenameHandler {

    // 判断当前上下文中是否有可供重命名的元素
    override fun isAvailableOnDataContext(dataContext: DataContext): Boolean {
        // 从上下文中获取 PSI 元素（当前选中的代码元素）
        val element = CommonDataKeys.PSI_ELEMENT.getData(dataContext)
        // 如果选中的元素是一个 JSClass，则返回 true，表示支持重命名
        return element is JSClass
    }

    // 指定当前处理器是否支持重命名操作
    override fun isRenaming(dataContext: DataContext): Boolean = true

    // 定义在重命名操作开始时的行为
    override fun invoke(project: Project, editor: Editor?, file: PsiFile?, dataContext: DataContext) {
        val element = CommonDataKeys.PSI_ELEMENT.getData(dataContext)

        if (element is PsiDirectory) {
            //RenameDialog.showRenameDialog(dataContext, RenameDialog(project, element, null, editor))
        } else if (element is PsiNamedElement) {
            // 如果是代码元素，执行标准重命名逻辑
            // RenamePsiElementProcessor.renameElement(element, editor, project)
        }
    }
    // 另一个重载的 invoke 方法，支持一次性处理多个 PSI 元素的重命名
    override fun invoke(project: Project, elements: Array<out PsiElement>, dataContext: DataContext?) {
        if (elements.size == 1) {
            val element = elements[0]

            // 判断是否为目录
            if (element is PsiDirectory) {
                val handler = PathRenameHandler()
                handler.handlePathRename(element, project)
                return
            }

            // 如果是命名元素，则继续处理普通重命名
            val initialName = (element as? PsiNamedElement)?.name ?: "Unnamed"
            val dialog = CustomRenameDialog(project, initialName)
            if (dialog.showAndGet()) { // 用户点击 OK 按钮
                val newName = dialog.getNewName()
                if (newName.isNotBlank()) {
                    performRename(element, newName)
                } else {
                    println("New name is empty. Rename operation canceled.")
                }
            }
        }
    }
    //首先判定改名的路径
    //创建一个路径修改处理器
    //确定修改的命名空间.提供正则匹配项

    private fun performRename(element: PsiElement, newName: String) {
        // 确保对 PSI 的修改在写操作中执行
        WriteCommandAction.runWriteCommandAction(element.project) {
            (element as? PsiNamedElement)?.setName(newName)
            println("Element renamed to $newName")
        }
        //处理逻辑
        //处理逻辑
    }
    private fun handleDirectoryRename(directory: PsiDirectory, project: Project) {
        // 获取当前目录名
        val oldName = directory.name

        // 自定义逻辑，例如弹窗获取新目录名称
        val newName = "NewDirectoryName" // 假设用户输入了新名称

        // 重命名目录
        directory.setName(newName)

        // TODO: 更新与此目录相关的命名空间或其他依赖项
        println("Renamed directory $oldName to $newName")
    }

}