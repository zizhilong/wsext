package com.daima.exthelp.Exp.PExp

import com.intellij.psi.PsiElement

class Result {
    // 最后处理的PsiElement
    var nextPsi: PsiElement? = null
    var lastPsi: PsiElement? = null

    // 返回结果集合
    val result: ArrayList<PsiElement> = ArrayList()

    constructor() {
        // 初始化
    }
}