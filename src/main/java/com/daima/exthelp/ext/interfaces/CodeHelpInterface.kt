package com.daima.exthelp.ext.interfaces

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.lang.javascript.psi.JSObjectLiteralExpression
import com.intellij.lang.javascript.psi.JSProperty
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
import com.jetbrains.rd.generator.nova.PredefinedType

// 定义 CodeHelp 接口
interface CodeHelpInterface {
    //返回是否需要准备好准备，如果准备则调用，否则会从新执行一次循环getCodeSuggestions
    fun reParse(psiElement: PsiElement):Boolean
    // 返回代码提示的列表
    fun getCodeSuggestions(psiElement: PsiElement,context: ProcessingContext): List<LookupElementBuilder>
    fun getPrefix(position: PsiElement): Char {
        // 检查position是否是JSIdentifier类型
            // 获取position的文本
            val text = position.text
            // 检查文本是否为空，并返回第一个字符或默认字符
            return if (text.isNotEmpty()) text.first() else '#'
        // 如果position不是JSIdentifier，返回默认字符
        return '#'
    }
    fun getObjProperty(obj:JSObjectLiteralExpression,name:String):JSProperty?{
        for(p in obj.properties){
            if(p.name==name){
                return p
            }
        }
        return null
    }

}