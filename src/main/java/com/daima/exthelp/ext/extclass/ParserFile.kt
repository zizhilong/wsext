package com.daima.exthelp.ext.extclass

import com.daima.exthelp.Exp.SExp.Parser
import com.daima.exthelp.Tools.StringHelper
import com.daima.exthelp.ext.extclass.extview.ExtViewClass
import com.daima.exthelp.ext.interfaces.ClassInterface
import com.intellij.lang.javascript.psi.*
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile

/**
 * 解析 JavaScript 文件并返回实现了 ClassInterface 的对象。
 * @param psiFile 要解析的 PSI 文件
 * @return 返回解析后的 ClassInterface 对象，若未找到则返回 null
 */
fun parseFile(psiFile: JSFile): ClassInterface<*>? {
    // 初始化自定义解析器，以识别特定的模式
    val exp = Parser("SCR{value define}R{value Ext}<<a")
    // 使用解析器执行，并获取结果
    val obj: PsiElement? = exp.Run(psiFile) ?: return null

    // 检查解析结果是否为 JSArgumentList 实例
    if (obj is JSArgumentList) {
        // 获取类名和对象字面量
        val className = extractClassName(obj) ?: return null
        val objectLiteral = extractObjectLiteral(obj)

        if (objectLiteral != null) {
            // 查找基类名
            val baseClassName = findBaseClassName(psiFile, objectLiteral)
            // 根据基类名创建类实例
            return createClassInstance(psiFile, className, objectLiteral, baseClassName)
        } else {
            println("Second argument is not an object literal.")
        }
    }
    return null
}

/**
 * 从 JSArgumentList 中提取类名。
 * @param argumentList JSArgumentList 实例
 * @return 去掉引号后的类名，若未找到则返回 null
 */
fun extractClassName(argumentList: JSArgumentList): String? {
    val firstArg = argumentList.arguments.getOrNull(0)
    return if (firstArg is JSLiteralExpression && firstArg.isStringLiteral) {
        StringHelper.trimQuotes(firstArg.value as? String ?: return null)
    } else {
        null
    }
}

/**
 * 从 JSArgumentList 中提取对象字面量。
 * @param argumentList JSArgumentList 实例
 * @return JSObjectLiteralExpression 实例，若未找到则返回 null
 */
fun extractObjectLiteral(argumentList: JSArgumentList): JSObjectLiteralExpression? {
    val secondArg = argumentList.arguments.getOrNull(1)
    return secondArg as? JSObjectLiteralExpression
}

/**
 * 查找基类名。
 * @param psiFile 当前的 PSI 文件
 * @param objectLiteral JSObjectLiteralExpression 实例
 * @return 基类名，若未找到则返回 null
 */
fun findBaseClassName(psiFile: PsiFile, objectLiteral: JSObjectLiteralExpression): String? {
    val extendProperty = objectLiteral.findProperty("extend")
    val extendClassName = (extendProperty?.value as? JSLiteralExpression)?.value as? String
    var upClass = extendClassName

    while (upClass != null && upClass.isNotEmpty()) {
        // 尝试从池中查找该类
        val parentClass = Pool.findByClassName<Any>(psiFile.project, upClass)

        if (upClass.startsWith("Ext")) {
            // 找到以 "Ext" 开头的类名，返回基类名
            return upClass
        }

        // 更新 upClass 为其父类名
        upClass = parentClass?.getParent()?.getClassName()
    }
    return null
}

/**
 * 根据基类名创建类实例。
 * @param psiFile 当前的 PSI 文件
 * @param className 当前类名
 * @param objectLiteral 对象字面量
 * @param baseClassName 基类名
 * @return 创建的 ClassInterface 实例
 */
fun createClassInstance(
    psiFile: PsiFile,
    className: String,
    objectLiteral: JSObjectLiteralExpression,
    baseClassName: String?
): ClassInterface<*> {
    val parentClass = baseClassName?.let { Pool.findByClassName<Any>(psiFile.project, it) }
    return when (baseClassName) {
        "Ext.window.Window" -> ExtViewClass(className, objectLiteral, parentClass)
        // 添加其他基类的处理逻辑
        else -> ExtClass(className, objectLiteral, parentClass)
    }
}