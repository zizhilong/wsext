package com.daima.exthelp.ext.extclass
//用于函数定义

// 定义一个函数参数的类
data class FunctionParameter(
    val name: String,
    val type: ClassInterface<*>?
)

// 定义一个函数类型的类
data class ExtFunction(
    val name: String,
    val text: String,
    val parameters: List<FunctionParameter>
)