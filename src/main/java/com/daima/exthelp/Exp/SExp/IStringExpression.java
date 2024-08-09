package com.daima.exthelp.Exp.SExp;

public interface IStringExpression {
    // 方法1：输出字符串首字母
    String getFirst();

    // 方法2：根据一个字符查询，并输出从字符开始到查询到目标字符的所有字符
    String getSubstringFromEnd(char endChar);

    // 方法3：删除左侧若干长度的字符串
    void removeLeftCharacters(int length);

    // 获取当前字符串
    String getString();
}
