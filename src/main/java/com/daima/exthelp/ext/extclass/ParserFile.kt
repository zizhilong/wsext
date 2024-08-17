package com.daima.exthelp.ext.extclass

import com.daima.exthelp.Exp.SExp.Parser
import com.daima.exthelp.Tools.StringHelper
import com.daima.exthelp.ext.extclass.extController.ExtViewController
import com.daima.exthelp.ext.extclass.extview.ExtViewClass
import com.daima.exthelp.ext.interfaces.ClassInterface
import com.intellij.lang.javascript.psi.*
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import java.util.concurrent.ConcurrentHashMap

object ParseFile {

    private var project: Project? = null
    private val baseClassCache = ConcurrentHashMap<String, String>()

    fun setProject(project: Project) {
        this.project = project
    }

    /**
     * 解析 JavaScript 文件并返回实现了 ClassInterface 的对象。
     * @param psiFile 要解析的 PSI 文件
     * @return 返回解析后的 ClassInterface 对象，若未找到则返回 null
     */
    fun parseFile(psiFile: JSFile): ClassInterface<*>? {
        val exp = Parser("SCR{value define}R{value Ext}<<a")
        val obj: PsiElement? = exp.Run(psiFile) ?: return null

        if (obj is JSArgumentList) {
            val className = extractClassName(obj) ?: return null
            val objectLiteral = extractObjectLiteral(obj)

            if (objectLiteral != null) {
                val baseClassName = findBaseClassName(objectLiteral)
                return createClassInstance(psiFile, className, objectLiteral, baseClassName)
            } else {
                println("Second argument is not an object literal.")
            }
        }
        return null
    }

    fun extractClassName(argumentList: JSArgumentList): String? {
        val firstArg = argumentList.arguments.getOrNull(0)
        return if (firstArg is JSLiteralExpression && firstArg.isStringLiteral) {
            StringHelper.trimQuotes(firstArg.value as? String ?: return null)
        } else {
            null
        }
    }

    fun extractObjectLiteral(argumentList: JSArgumentList): JSObjectLiteralExpression? {
        val secondArg = argumentList.arguments.getOrNull(1)
        return secondArg as? JSObjectLiteralExpression
    }

    fun findBaseClassName(objectLiteral: JSObjectLiteralExpression): String? {
        val extendProperty = objectLiteral.findProperty("extend")
        val extendClassName = (extendProperty?.value as? JSLiteralExpression)?.value as? String
        var upClass = extendClassName

        while (upClass != null && upClass.isNotEmpty()) {
            baseClassCache[upClass]?.let {
                return it // 从缓存中直接返回
            }

            val parentClass = Pool.findByClassName<Any>(project!!, upClass)

            if (upClass.startsWith("Ext")) {
                baseClassCache[upClass] = upClass // 添加到缓存中
                return upClass
            }

            upClass = parentClass?.getParent()?.getClassName()
        }
        return null
    }

    fun createClassInstance(
        psiFile: PsiFile,
        className: String,
        objectLiteral: JSObjectLiteralExpression,
        baseClassName: String?
    ): ClassInterface<*> {
        val parentClass = baseClassName?.let { Pool.findByClassName<Any>(project!!, it) }
        return when (baseClassName) {
            "Ext.window.Window" -> ExtViewClass(className, objectLiteral, parentClass)
            "Ext.container.Container" -> ExtViewClass(className, objectLiteral, parentClass)
            "Ext.app.ViewController" -> ExtViewController(className, objectLiteral, parentClass)
            else -> ExtViewClass(className, objectLiteral, parentClass)
        }
    }
}