package com.example.demo2

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.lang.javascript.psi.JSObjectLiteralExpression
import com.intellij.lang.javascript.psi.JSProperty
import com.intellij.openapi.diagnostic.Logger
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext

class MyCompletionContributor : CompletionContributor() {
    init {
        extend(CompletionType.BASIC,
            PlatformPatterns.psiElement().withParent(JSProperty::class.java),
            MyCompletionProvider()
        )
    }
}

class MyCompletionProvider : CompletionProvider<CompletionParameters>() {
    private val logger = Logger.getInstance(MyCompletionProvider::class.java)

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        resultSet: CompletionResultSet
    ) {
        val position = parameters.position


        // 输出调试信息
        logger.info("addCompletions called for position: ${position.text}")
        val property = position.parent as? JSProperty ?: return
        val objectLiteral = property.parent as? JSObjectLiteralExpression ?: return
        var currentElement: PsiElement? = position

        // 向上遍历 Psi 树，直到根节点
        while (currentElement != null) {
            val elementType = currentElement::class.java.simpleName

            // 忽略指定的类型
            if (elementType != "PsiWhiteSpaceImpl" && elementType != "JSFileImpl" && elementType != "PsiJavaDirectoryImpl") {
                // 添加类型到结果中，并在行末添加字符 |
                //psiElementTypes.append(elementType).append(" |").append(lineSeparator)

                // 如果是 JSObjectLiteralExpression，输出其所有属性
                if (currentElement is JSObjectLiteralExpression) {
                    //currentElement.properties.forEach { property ->
                        //psiElementTypes.append("Property: ${property.name} = ${property.value?.text} |").append(lineSeparator)
                    //}
                }
            }

            currentElement = currentElement.parent
        }


        // 检查当前对象字面量中是否有 xtype 属性且值为 textfield
        val xtypeProperty = objectLiteral.findProperty("xtype")
        if (xtypeProperty?.value?.text == "\"textfield\"" || xtypeProperty?.value?.text == "'textfield'") {
            // 提供属性补全项
            logger.info("xtype is textfield, providing completions")
            resultSet.addElement(LookupElementBuilder.create("length"))
            resultSet.addElement(LookupElementBuilder.create("max"))
            resultSet.addElement(LookupElementBuilder.create("min"))
            resultSet.addElement(LookupElementBuilder.create("label"))
            resultSet.addElement(LookupElementBuilder.create("cuixu"))
        }
    }
}