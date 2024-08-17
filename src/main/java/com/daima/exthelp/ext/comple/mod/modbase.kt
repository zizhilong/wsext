package com.daima.exthelp.ext.comple.mod

import com.intellij.lang.javascript.psi.JSExpressionStatement
import com.intellij.lang.javascript.psi.JSReferenceExpression
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.PsiTreeUtil

open class modbase {

    /**
     * 对输入的 PSI 元素进行连续字符判定
     * @param element 输入的 PSI 元素
     * @return 判定后的字符串
     */

    protected fun getContinuousText(element: PsiElement): String {
        // 检查传入的元素是否为 LeafPsiElement 类型
        if (element !is LeafPsiElement) {
            return ""
        }

        val builder = StringBuilder()
        var currentElement: PsiElement? = element

        // 向上查找父元素，直到找到 JSExpressionStatement 或遇到不合要求的父元素
        while (currentElement != null) {
            val parent = currentElement.parent
            if (parent is JSExpressionStatement) {
                builder.insert(0, parent.text)
                break
            } else if (parent !is JSReferenceExpression) {
                return "" // 如果父元素不是 JSExpressionStatement 或 JSReferenceExpression，则返回空字符串
            }
            // 移动到父元素继续检查
            currentElement = parent
        }

        // 最终存在字符串内容时，使用 '.' 拆分数组并删除最后一组数组元素
        val result = builder.toString().trim()
        if (result.isNotEmpty()) {
            val parts = result.split('.').toMutableList()
            if (parts.isNotEmpty()) {
                parts.removeAt(parts.size - 1)
            }
            return parts.joinToString(".")
        }

        return ""
    }

    /**
     * 将连续字符判定后的字符串拆解为数组
     * @param text 连续字符的字符串
     * @param delimiter 拆解字符串的分隔符
     * @return 拆解后的字符串数组
     */
    protected fun splitTextIntoArray(text: String, delimiter: String = "."): List<String> {
        return text.split(delimiter).map { it.trim() }
    }
}