package com.daima.exthelp.ext.ns

import com.daima.exthelp.Exp.SExp.Parser
import com.daima.exthelp.Tools.StringHelper
import com.intellij.lang.javascript.psi.JSObjectLiteralExpression
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager

// 用于存储命名空间名称到 PsiElement 的映射
val namespaceToPsiElementMap: MutableMap<String, PsiFile> = mutableMapOf()

// 用于存储顶级命名空间到其目录路径的映射
val namespacePathsMap: MutableMap<String, String> = mutableMapOf()

// 初始化命名空间路径的函数
fun loadNamespacePaths(project: Project) {
    // 检查 namespacePathsMap 是否为空
    if (namespacePathsMap.isNotEmpty()) {
        return
    }

    // 获取项目根目录
    val projectBaseDir = project.basePath ?: return

    // 定位 app.js 文件
    val appJsPath = "$projectBaseDir/app.js"
    val virtualFile: VirtualFile? = LocalFileSystem.getInstance().findFileByPath(appJsPath)

    if (virtualFile == null) {
        return
    }

    // 获取 PsiFile
    val psiFile: PsiFile? = PsiManager.getInstance(project).findFile(virtualFile)

    if (psiFile == null) {
        return
    }

    // 使用 Parser 提取 JSObjectLiteralExpression
    val exp = Parser("SCR{value setConfig}R{value Loader}<<aOY{name paths}O")
    val jsObjPsi = exp.Run(psiFile) as? JSObjectLiteralExpression ?: return

    // 遍历 JSObjectLiteralExpression 的属性
    for (property in jsObjPsi.properties) {
        val name = property.name ?: continue
        val value = property.value?.text ?: continue
        // 去掉值的左右空格和引号
        val trimmedValue = StringHelper.trimQuotes(value.trim())
        namespacePathsMap[name] = trimmedValue
    }
}

// 根据完全限定名称查找或创建 PsiFile 的函数
fun Class2Psi(name: String, project: Project): PsiFile? {
    loadNamespacePaths(project)
    // 检查 PsiFile 是否已在映射中
    namespaceToPsiElementMap[name]?.let {
        return it
    }

    // 拆分名称以获取顶级命名空间
    val topLevelNamespace = name.substringBefore('.')
    val remainingPath = name.substringAfter('.', "")

    // 从 namespacePathsMap 中确定基础目录
    val baseDir = namespacePathsMap[topLevelNamespace] ?: return null

    // 将命名空间和路径转换为 PsiFile 的逻辑
    val newPsiFile = findPsiFileForNamespace(baseDir, remainingPath, project) ?: return null

    // 将结果存储在映射中以供将来使用
    namespaceToPsiElementMap[name] = newPsiFile

    return newPsiFile
}

// 用于根据基础目录和路径查找 PsiFile 的函数
fun findPsiFileForNamespace(baseDir: String, path: String, project: Project): PsiFile? {
    // 获取项目根目录
    val projectBaseDir = project.basePath ?: return null

    // 构建完整文件路径
    val filePath = "$projectBaseDir/$baseDir/${path.replace('.', '/')}.js"

    // 获取 VirtualFile
    val virtualFile = LocalFileSystem.getInstance().findFileByPath(filePath) ?: return null

    // 使用 PsiManager 从 VirtualFile 获取 PsiFile
    return PsiManager.getInstance(project).findFile(virtualFile)
}

// 加载器函数的示例，用于设置路径
fun initializeNamespacePaths(psiFile: PsiElement) {
    // 此处可以调用 loadNamespacePaths 方法
}
