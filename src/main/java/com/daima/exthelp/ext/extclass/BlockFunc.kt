package com.daima.exthelp.ext.extclass

import com.intellij.lang.javascript.psi.JSFunctionExpression

// 定义 BlockFunc 类
data class BlockFunc(
    var context:Context<Any>,
    val name: String, // 函数名
    val functionExpression: JSFunctionExpression // 函数表达式
) {
    //应该最终要把自己给插入到PSI元素中
    //需要实现一些方法
    //应该要有一个内部渲染器
}