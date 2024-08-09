package com.daima.exthelp.ext.extclass

import com.daima.exthelp.Exp.SExp.Parser
import com.daima.exthelp.Tools.StringHelper
import com.intellij.lang.javascript.psi.*
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile

class ParserFile {

    // 解析文件并返回实现了 ClassInterface 的对象
    fun parseFile(psiFile: PsiFile): ClassInterface<*>? {
        // 初始化自定义解析器以识别模式
        val exp = Parser("SCR{value define}R{value Ext}<<a")
        val obj: PsiElement? = exp.Run(psiFile) ?: return null

        // 检查结果是否为 JSArgumentList 实例
        if (obj is JSArgumentList) {
            // 验证第一个参数是否为字符串字面量
            val firstArg = obj.arguments.getOrNull(0)
            val secondArg = obj.arguments.getOrNull(1)

            if (firstArg is JSLiteralExpression && firstArg.isStringLiteral) {
                // 提取字符串值，使用辅助方法确保非空
                val rawClassName = firstArg.value as? String ?: return null
                val className = StringHelper.trimQuotes(rawClassName)

                // 检查第二个参数是否为对象字面量
                if (secondArg is JSObjectLiteralExpression) {
                    // 创建 ExtClass 对象并返回
                    val extClass = ExtClass(className, secondArg)
                    return extClass
                }
            }
        }
        return null
    }
}