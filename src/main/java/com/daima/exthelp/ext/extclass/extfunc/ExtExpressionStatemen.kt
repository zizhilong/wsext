package com.daima.exthelp.ext.extclass.extfunc

import com.daima.exthelp.ext.extclass.Context
import com.daima.exthelp.ext.log.Info
import com.daima.exthelp.ext.interfaces.CODE_HELP_KEY
import com.daima.exthelp.ext.interfaces.ClassInterface
import com.daima.exthelp.ext.interfaces.CodeHelpInterface
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.lang.javascript.psi.JSCallExpression
import com.intellij.lang.javascript.psi.JSExpressionStatement
import com.intellij.lang.javascript.psi.JSReferenceExpression
import com.intellij.lang.javascript.psi.JSThisExpression
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext

// 语法解释器。用于处理函数中的执行语法
class ExtExpressionStatemen(
    private val jsExp: JSExpressionStatement,
    private val jscontext: Context
) : CodeHelpInterface {
    private var parse=false;
    init{
        Info("init")
        jsExp.putUserData(CODE_HELP_KEY,this)
    }
    override fun reParse(psi:PsiElement): Boolean {
        Info("reParse")

        // 遍历JSExpressionStatement的子元素
        jsExp.children.forEach { element ->
        //处理表达式赋值
            getExpressionClass(jscontext,element)
        }
        // 返回true表示运行中
        return true
    }

    // 用于代码补全的函数
    override fun getCodeSuggestions(psiElement: PsiElement, context: ProcessingContext): List<LookupElementBuilder> {
         Info("getCodeSuggestions")
        // 返回空列表作为占位符
        return listOf()
    }

}