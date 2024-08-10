package com.daima.exthelp.ext.extclass.extview

import com.daima.exthelp.Exp.PExp.Parser
import com.daima.exthelp.Tools.ExpHelper
import com.daima.exthelp.ext.interfaces.CODE_HELP_KEY
import com.daima.exthelp.ext.interfaces.CodeHelpInterface
import com.daima.exthelp.extdata.Load
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.lang.javascript.psi.JSObjectLiteralExpression
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext

class ExtViewListeners (
    val jsObject: JSObjectLiteralExpression,
    val className:String
): CodeHelpInterface
{
    init{
        jsObject.putUserData(CODE_HELP_KEY, this)
    }
    private var isRun=false;
    override fun RunIng():Boolean{
        //如果已经渲染过可以直接返回
        if(isRun)return true
        //TODO 处理渲染逻辑，需要拉取对应的class，主要的目的是封装内部函数调用处理一个作用域
        isRun=true
        return false
    }

    // 假设 getCodeSuggestions 是用于代码补全的函数
    override fun getCodeSuggestions(psiElement: PsiElement, context: ProcessingContext): List<LookupElementBuilder> {
        val obj = Parser("LREO").RunExp(psiElement)
        //如果属于属性设定
        //创建事件
        if(obj!=null&&obj.lastPsi===jsObject) {
            val loadInstance = Load.getInstance()
            val extclass = loadInstance.getExtclassByName(className) ?: return listOf()
            var list= extclass.getLookupElementsByPrefix(this.getPrefix(psiElement), context)



        }
        return listOf()
    }
}