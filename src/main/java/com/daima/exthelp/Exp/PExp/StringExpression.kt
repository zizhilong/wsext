package com.daima.exthelp.Exp.PExp

class StringExpression(private var str: String?) : IStringExpression {

    // 方法1：输出字符串首字母
    override fun getFirst(): String {
        return if (str.isNullOrEmpty()) {
            ""
        } else {
            str!![0].toString()
        }
    }

    // 方法2：根据一个字符查询，并输出从字符开始到查询到目标字符的所有字符
    override fun getSubstringFromEnd(endChar: Char): String {
        if (str.isNullOrEmpty()) {
            return ""
        }
        val endIndex = str!!.indexOf(endChar)
        return if (endIndex == -1) {
            ""
        } else {
            str!!.substring(0, endIndex + 1)
        }
    }

    // 方法3：删除左侧若干长度的字符串
    override fun removeLeftCharacters(length: Int) {
        if (str.isNullOrEmpty()) {
            str = ""
            return
        }

        require(length >= 0) { "Length cannot be negative" }

        str = if (length >= str!!.length) {
            ""
        } else {
            str!!.substring(length)
        }
    }
    override fun getString(): String {
        return str!!
    }
}