package com.daima.exthelp.Tools

import com.intellij.lang.javascript.JavaScriptSupportLoader
import com.intellij.lang.javascript.psi.JSElementFactory
import com.intellij.lang.javascript.psi.JSFile
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement

fun createComma(project:Project): PsiElement {
    val file = JSElementFactory.createExpressionCodeFragment(
        project, "return {1,2}", null,
        JavaScriptSupportLoader.ECMA_SCRIPT_L4, null,
        JSElementFactory.TopLevelCompletion.NO, null
    ) as JSFile
    return file.children[0].children[0].children[0].nextSibling
}
fun comment(project: Project, comment:String):PsiElement{
    val file = JSElementFactory.createExpressionCodeFragment(
        project, comment, null,
        JavaScriptSupportLoader.ECMA_SCRIPT_L4, null,
        JSElementFactory.TopLevelCompletion.NO, null
    ) as JSFile
    return file.children[0];
}