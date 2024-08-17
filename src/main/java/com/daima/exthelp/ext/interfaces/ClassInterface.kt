package com.daima.exthelp.ext.interfaces

import com.daima.exthelp.ext.extclass.Context
import com.daima.exthelp.ext.extclass.ExtFunction
import com.intellij.psi.PsiElement


// 定义 ClassInterface 接口
interface ClassInterface<T> {
    // 获取父类，返回类型为 ClassInterface<T>? 表示可能为空
    fun getParent(): ClassInterface<T>?

    // 获取当前类名，返回 String
    fun getClassName(): String

    // 获取函数数组
    fun getFunctions(): List<ExtFunction>
    //获得一个子集元素
    fun getChildren(context:Context, name:String): CodeHelpInterface?

    //需要对当前页面进行PSI渲染
    fun renderPage()
}