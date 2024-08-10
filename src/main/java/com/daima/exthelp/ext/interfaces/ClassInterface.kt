package com.daima.exthelp.ext.interfaces

import com.daima.exthelp.ext.extclass.ExtFunction


// 定义 ClassInterface 接口
interface ClassInterface<T> {
    // 获取父类，返回类型为 ClassInterface<T>? 表示可能为空
    fun getParent(): ClassInterface<T>?

    // 获取当前类名，返回 String
    fun getClassName(): String

    // 获取函数数组
    fun getFunctions(): List<ExtFunction>
    //需要对当前页面进行PSI渲染
    fun renderPage()
}