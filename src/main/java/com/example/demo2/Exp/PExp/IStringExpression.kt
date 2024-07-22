package com.example.demo2.Exp.PExp

interface IStringExpression {
    // 方法1：输出字符串首字母
    fun getFirst(): String

    // 方法2：根据一个字符查询，并输出从字符开始到查询到目标字符的所有字符
    fun getSubstringFromEnd(endChar: Char): String

    // 方法3：删除左侧若干长度的字符串
    fun removeLeftCharacters(length: Int)

    // 获取当前字符串
    fun getString(): String?
}