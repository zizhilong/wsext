package com.daima.exthelp.ext.interfaces

import com.intellij.psi.PsiElement

class CodeHelpBase {
    protected fun getPrefix(position: PsiElement, offset: Int): Char {
        val text = position.containingFile.text
        var i = offset - 1
        while (i >= 0 && text[i].isLetterOrDigit()) {
            i--
        }
        return if (i + 1 < offset && text[i + 1].isLetterOrDigit()) text[i + 1] else '#'
    }
}