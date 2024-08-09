package com.daima.exthelp.Tools

import com.intellij.lang.javascript.psi.*
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiParserFacade
import com.intellij.psi.util.PsiTreeUtil

class JSArrayLiteralUnit(val context: JSArrayLiteralExpression) {
    private val project: Project = context.project

    fun findInString(str: String): Boolean {
        val expressions = context.expressions
        for (expression in expressions) {
            if (expression is JSLiteralExpression) {
                val literalExpression = expression
                if (literalExpression.isStringLiteral && str == literalExpression.value) {
                    return true
                }
            }
        }
        return false
    }

    fun addString(str: String, prev: JSExpression?) {
        val newLiteral = JSElementFactory.createExpressionCodeFragment(
                project, "\"${escapeJavaScriptString(str)}\"",
                context
        ) as JSExpressionCodeFragment
        val txt = PsiTreeUtil.findChildOfType(newLiteral, JSLiteralExpression::class.java) ?: return
                val expressions = context.expressions
        //if(expressions.isEmpty()){
        //context.add(txt)
        val inElement = context.addAfter(txt, context.firstChild)
        val psiParserFacade = PsiParserFacade.getInstance(project)
        context.addBefore(psiParserFacade.createWhiteSpaceFromText("\n  "), inElement)
        //}else{
        //    if(prev==null){
        //        context.addAfter(txt,expressions[0])
        //    }
        //}

    }
    companion object {
        fun escapeJavaScriptString(input: String?): String? {
            if (input == null) {
                return null
            }

            val escapedString = StringBuilder()

            for (c in input.toCharArray()) {
                when (c) {
                    '"' -> escapedString.append("\\\"")
                    '\'' -> escapedString.append("\\'")
                    '\\' -> escapedString.append("\\\\")
                    '\n' -> escapedString.append("\\n")
                    '\r' -> escapedString.append("\\r")
                    '\t' -> escapedString.append("\\t")
                    else -> escapedString.append(c)
                }
            }

            return escapedString.toString()
        }
    }
}