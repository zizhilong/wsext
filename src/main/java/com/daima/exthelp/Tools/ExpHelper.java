package com.daima.exthelp.Tools;

import com.intellij.lang.ecmascript6.psi.impl.ES6PropertyImpl;
import com.intellij.lang.javascript.psi.impl.JSObjectLiteralExpressionImpl;
import com.intellij.lang.javascript.psi.impl.JSPropertyImpl;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;

import java.nio.charset.StandardCharsets;

public class ExpHelper {
    public static final String SPECIFIC_CHARACTERS = "^DNVPCROAYTIJWBGLESFa.W";
    private static final Logger LOG = Logger.getInstance(ExpHelper.class);

    // SCR{value define}R{value Ext}<<AOP{value init}FBSCaO
    public static String Psi2TypeChar(PsiElement psi) {
        String classname = psi.getClass().getSimpleName();
        switch (classname) {
            case "JSDocCommentImpl":
                return "D"; // JSDoc 注释实现，通常用于描述 JavaScript 函数或变量的文档注释
            case "JSReferenceExpressionImpl":
                return "R"; // JavaScript 引用表达式实现，用于表示对变量、函数或对象的引用
            case "JSArrayLiteralExpressionImpl":
                return "A"; // JavaScript 数组字面量表达式实现，表示数组的字面量形式
            case "JSPropertyImpl":
                return "Y"; // JavaScript 属性实现，通常用于表示对象的属性
            case "JSObjectLiteralExpressionImpl":
                return "O"; // JavaScript 对象字面量表达式实现，表示对象的字面量形式
            case "JSExpressionStatementImpl":
                return "S"; // JavaScript 表达式语句实现，表示一个表达式作为语句执行
            case "ES6PropertyImpl":
                return "E"; // ECMAScript 6 属性实现，表示 ES6 中的对象属性
            case "JSLiteralExpressionImpl":
                return "L"; // JavaScript 字面量表达式实现，表示基本类型的字面量（如字符串、数字等）
            case "JSCallExpressionImpl":
                return "C"; // JavaScript 调用表达式实现，表示函数或方法的调用
            case "JSFunctionExpressionImpl":
                return "F"; // JavaScript 函数表达式实现，表示一个函数作为表达式
            case "JSBlockStatementImpl":
                return "B"; // JavaScript 代码块语句实现，表示由花括号括起来的语句块
            case "JSArgumentListImpl":
                return "a"; // JavaScript 参数列表实现，表示函数调用中的参数列表
            case "PsiWhiteSpaceImpl":
                return "W"; // Psi 空白符实现，表示代码中的空白字符
            case "LeafPsiElement":
                return "L"; // Psi 叶子节点元素，表示不再包含其他子元素的最小结构
            case "PsiDirectoryImpl":
                return "d"; // Psi 目录实现，表示一个文件系统目录
            case "FlowJSFunctionExpressionImpl":
                return "F";
        }
        LOG.warn("NoFind PSICLASS" + classname);
        return "X";
    }

    public String AllTypes(PsiElement psi) {
        String ret = "";
        while (psi != null) {
            ret = ret + Psi2TypeChar(psi);
            psi = psi.getParent();
        }
        return ret;
    }

    // 对象结构判定
    public static boolean O(JSObjectLiteralExpressionImpl obj, String[] where) {
        switch (where[0]) {
            case "haveattr": // 是否包含某个属性
                return obj.findProperty(where[1]) != null;
        }
        return true;
    }

    public static boolean E(ES6PropertyImpl obj, String[] where) {
        return true;
    }

    public static boolean Y(JSPropertyImpl obj, String[] where) {
        switch (where[0]) {
            case "name": // 是否包含某个属性
                String name = obj.getName();
                return name.equals(where[1]);
        }
        return true;
    }

    // 新方法，用于向上迭代并输出每层级的简写符号和完整类名
    public static void logPsiHierarchy(PsiElement psi) {
        while (psi != null) {
            // 获取简写符号
            String typeChar = Psi2TypeChar(psi);
            // 获取完整类名
            String fullClassName = psi.getClass().getName();
            // 获取文本内容并截取前10个字节
            String contentPreview = getContentPreview(psi, 10);
            // 输出到日志
            LOG.warn(typeChar + " | " + fullClassName + " | Content: " + contentPreview);
            // 获取父级元素
            psi = psi.getParent();
        }
    }

    public static String getContentPreview(PsiElement psi, int byteLimit) {
        String text = psi.getText();
        // 转换为字节数组并截取前 byteLimit 个字节
        byte[] textBytes = text.getBytes(StandardCharsets.UTF_8);
        int length = Math.min(textBytes.length, byteLimit);
        // 将截取的字节数组转换回字符串
        return new String(textBytes, 0, length, StandardCharsets.UTF_8);
    }
}