package com.example.demo2.ui.toolsbar

import com.intellij.psi.PsiElement
import com.example.demo2.Exp.SExp.*

fun jstoNode(psi: PsiElement){
    //Y{name init}FBSCaO
    var exp = Parser("SCR{value define}R{value Ext}<<aO")
    //第一个节点
    val nodepsi=exp.Run(psi)

    return
}
