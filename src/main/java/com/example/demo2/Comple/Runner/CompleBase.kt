package com.example.demo2.Comple.Runner

import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.PsiElement

open class CompleBase {
    private val LOG: Logger = Logger.getInstance(Xtype::class.java)

    protected fun getPrefix(position: PsiElement, offset: Int): Char {
        val text = position.containingFile.text
        var i = offset - 1
        while (i >= 0 && text[i].isLetterOrDigit()) {
            i--
        }
        return if (i + 1 < offset && text[i + 1].isLetterOrDigit()) text[i + 1] else '#'
    }
}