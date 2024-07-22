package com.example.demo2.Tools

import com.intellij.lang.javascript.JavaScriptSupportLoader
import com.intellij.lang.javascript.psi.JSElementFactory
import com.intellij.lang.javascript.psi.JSFile
import com.intellij.lang.javascript.psi.JSObjectLiteralExpression
import com.intellij.lang.javascript.psi.JSProperty
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.PsiTreeUtil

class JSObjectLiteralUnit(val context: JSObjectLiteralExpression) {
    private val project: Project = context.project



    fun commentForProperty( prev: JSProperty,comment:String){
        val file = JSElementFactory.createExpressionCodeFragment(
            project, comment, context,
            JavaScriptSupportLoader.ECMA_SCRIPT_L4, null,
            JSElementFactory.TopLevelCompletion.NO, null
        ) as JSFile
        context.addBefore(file.children[0], prev)
        context.addBefore(createWhitespace(), prev)
    }

    /**
     * 添加一个新属性到 JSObjectLiteralExpression 中。
     *
     * @param name 新属性的名称
     * @param code 新属性的代码/值
     * @param prev 上一个属性，插入到其后面，如果为 null 则插入到最前面
     * @return 新增的 JSProperty
     */
    fun addProperty(name: String, code: String, prev: JSProperty?): JSProperty {
        // 创建一个新的 JSProperty
        val newProp = createJSProperty(name, code) ?: throw ArithmeticException("AddProperty Error: 无法创建新的 JSProperty")

        return if (context.properties.isEmpty()) {
            // 如果 context 中没有属性，将新属性插入为第一个子元素
            val insertedProp = context.addAfter(newProp, context.firstChild) as JSProperty
            // 在新属性前后添加换行以确保格式正确
            context.addBefore(createWhitespace(), insertedProp)
            if (insertedProp.nextSibling is LeafPsiElement) {
                context.addAfter(createWhitespace(), insertedProp)
            }
            insertedProp
        } else {

            // 在指定位置插入属性
            insertPropertyAtPosition(newProp, prev)
        }
    }

    /**
     * 创建一个新的 JSProperty 元素。
     *
     * @param name 属性的名称
     * @param code 属性的代码/值
     * @return 新创建的 JSProperty，如果创建失败则为 null
     */
    private fun createJSProperty(name: String, code: String): JSProperty? {
        // 创建一个包含单个 JSProperty 的 JSFile
        val file = JSElementFactory.createExpressionCodeFragment(
            project, "return {$name:$code};", context,
            JavaScriptSupportLoader.ECMA_SCRIPT_L4, null,
            JSElementFactory.TopLevelCompletion.NO, null
        ) as JSFile
        // 从 JSFile 中提取并返回 JSProperty
        return PsiTreeUtil.findChildOfType(file, JSProperty::class.java)
    }

    /**
     * 在指定位置插入一个属性。
     *
     * @param newProp 要插入的新属性
     * @param prev 上一个属性，插入到其后面，如果为 null 则插入到最前面
     * @return 新插入的 JSProperty
     */
    private fun insertPropertyAtPosition(newProp: JSProperty, prev: JSProperty?): JSProperty {
        var insertElement: PsiElement? = prev

        if (prev == null) {
            // 如果没有指定上一个属性，则插入到最前面
            val ret = context.addBefore(newProp, context.properties[0]) as JSProperty
            context.addAfter(createWhitespace(), ret.nextSibling)
            return ret
        } else {
            insertElement=context.properties[context.properties.size-1]


            // 如果上一个属性的下一个兄弟元素是 LeafPsiElement 并且包含换行，则添加逗号
            if (insertElement.nextSibling is LeafPsiElement && insertElement.nextSibling.text.contains("\n")) {
                insertElement=context.addAfter(createComma(), insertElement).nextSibling

            }
            // 确保插入元素不为 null
            //insertElement = insertElement ?: prev
            // 添加换行
            //insertElement = context.addAfter(createWhitespace(), insertElement)
            // 插入新属性
            return context.addAfter(newProp, insertElement) as JSProperty
        }
    }

    /**
     * 在 JSObjectLiteralExpression 中按名称查找属性。
     *
     * @param name 要查找的属性名称
     * @return 找到的 JSProperty，如果未找到则为 null
     */
    fun findProperty(name: String): JSProperty? {
        return context.properties.find { it.name == name }
    }

    /**
     * 从 JSObjectLiteralExpression 中按名称删除属性。
     *
     * @param name 要删除的属性名称
     */
    fun deleteProperty(name: String) {
        val property = findProperty(name)
        property?.let {
            ApplicationManager.getApplication().invokeLater {
                WriteCommandAction.runWriteCommandAction(project) {
                    it.delete()
                }
            }
        }
    }

    /**
     * 创建表示换行和缩进的 PsiElement。
     *
     * @return 创建的 PsiElement
     */
    private fun createWhitespace(): PsiElement {
        val file = JSElementFactory.createExpressionCodeFragment(
            project, "return {1,\n2}", context,
            JavaScriptSupportLoader.ECMA_SCRIPT_L4, null,
            JSElementFactory.TopLevelCompletion.NO, null
        ) as JSFile
        return file.children[0].children[0].children[0].nextSibling.nextSibling
    }

    /**
     * 创建表示逗号的 LeafPsiElement。
     *
     * @return 创建的 LeafPsiElement
     */
    private fun createComma(): PsiElement {
        val file = JSElementFactory.createExpressionCodeFragment(
            project, "return {1,2}", context,
            JavaScriptSupportLoader.ECMA_SCRIPT_L4, null,
            JSElementFactory.TopLevelCompletion.NO, null
        ) as JSFile
        return file.children[0].children[0].children[0].nextSibling
    }
}