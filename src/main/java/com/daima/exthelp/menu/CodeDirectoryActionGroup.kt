package com.daima.exthelp.menu

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.editor.EditorModificationUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VfsUtil
import java.io.IOException

class CodeGenerationActionGroup : ActionGroup() {

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        val project = e?.project ?: return emptyArray()
        val docsDirectory = getDocsCodeDirectory(project)

        // 如果 docs/code 目录不存在，则返回空
        if (docsDirectory == null || !docsDirectory.exists()) {
            return emptyArray()
        }

        // 创建“代码生成”主菜单
        val codeGenerationMenu = DefaultActionGroup("代码生成", true)

        // 递归遍历 docs/code 目录，生成子菜单树
        buildMenuTree(docsDirectory, codeGenerationMenu)

        return arrayOf(codeGenerationMenu)
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
}