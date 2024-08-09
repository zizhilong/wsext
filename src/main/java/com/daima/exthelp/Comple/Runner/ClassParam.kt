package com.daima.exthelp.Comple.Runner

import com.daima.exthelp.Exp.PExp.Parser
import com.daima.exthelp.Exp.PExp.Result
import com.daima.exthelp.extdata.Load
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.lang.javascript.psi.JSObjectLiteralExpression
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
import org.jetbrains.annotations.NotNull

class ClassParam : CompleBase(), CompleRunner {
    private val LOG: Logger = Logger.getInstance(Xtype::class.java)
    private val xtypeParser: Parser = Parser("REO{haveattr xtype}")

    override fun addCompletions(
        @NotNull parameters: CompletionParameters,
        @NotNull context: ProcessingContext,
        @NotNull result: CompletionResultSet
    ): Boolean {
        val position: PsiElement = parameters.position
        val ret: Result? = xtypeParser.RunExp(position.parent)
        if (ret != null) {
            //对象转换
            val objectLiteral = ret.lastPsi as? JSObjectLiteralExpression ?: return false
            //读取属性
            val xtypeProperty = objectLiteral.findProperty("xtype")
            //获得文本
            val classname = xtypeProperty?.value?.text
            return runResult(classname, parameters, context, result)
        }
        return false
    }

    //执行查询和插入
    private fun runResult(
        classname: String?,
        @NotNull parameters: CompletionParameters,
        @NotNull context: ProcessingContext,
        @NotNull result: CompletionResultSet
    ): Boolean {
        if (classname == null) {
            return false
        }

        val loadInstance = Load.getInstance()
        val extclass = loadInstance.getExtclassByName(classname) ?: return false
        val prefix = getPrefix(parameters.position, parameters.offset)
        val lookupElements = extclass.getLookupElementsByPrefix(prefix, context)
        //过滤所有方法类型
        if (lookupElements != null) {
            result.addAllElements(lookupElements.asList())
        }
        return true
    }
}