package com.daima.exthelp.Exp.SExp;

public class StringExpression implements IStringExpression {
    private String str;

    // 构造方法
    public StringExpression(String str) {
        this.str = str;
    }

    // 方法1：输出字符串首字母
    public String getFirst() {
        if (str == null || str.isEmpty()) {
            return "";
        }
        return String.valueOf(str.charAt(0));
    }

    // 方法2：根据一个字符查询，并输出从字符开始到查询到目标字符的所有字符
    public String getSubstringFromEnd(char endChar) {
        if (str == null || str.isEmpty()) {
            return "";
        }


        int endIndex = str.indexOf(endChar);
        if (endIndex == -1) {
            return "";
        }
        return str.substring(0, endIndex + 1);
    }

    // 方法3：删除左侧若干长度的字符串
    public void removeLeftCharacters(int length) {
        if (str == null || str.isEmpty()) {
            str = "";
            return;
        }

        if (length < 0) {
            throw new IllegalArgumentException("Length cannot be negative");
        }

        if (length >= str.length()) {
            str = "";
        } else {
            str = str.substring(length);
        }
    }

    // 获取当前字符串（为了方便展示结果，添加一个getter方法）
    public String getString() {
        return str;
    }
}