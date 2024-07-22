package com.example.demo2.Tools;

import com.intellij.lang.ecmascript6.psi.impl.ES6PropertyImpl;
import com.intellij.lang.javascript.psi.impl.JSObjectLiteralExpressionImpl;
import com.intellij.lang.javascript.psi.impl.JSPropertyImpl;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
public class ExpHelper {
    public static final String SPECIFIC_CHARACTERS = "^DNVPCROAYTIJWBGLESFa.W";
    private static final Logger LOG = Logger.getInstance(ExpHelper.class);
    //SCR{value define}R{value Ext}<<AOP{value init}FBSCaO
    public static String  Psi2TypeChar(PsiElement psi){
        String classname=psi.getClass().getSimpleName();
        switch (classname){
            case "JSDocCommentImpl":
                return "D";
            case "JSReferenceExpressionImpl":
                return "R";//语法异常
            case "JSArrayLiteralExpressionImpl":
                return "A";
            case "JSPropertyImpl":
                return "Y";
            case "JSObjectLiteralExpressionImpl":
                return "O";
            case "JSExpressionStatementImpl":
                return "S";
            case "ES6PropertyImpl":
                return "E";
            case "JSLiteralExpressionImpl":
                return "L";
            case "JSCallExpressionImpl":
                return "C";
            case "JSFunctionExpressionImpl":
                return "F";
            case "JSBlockStatementImpl":
                return "B";
            case "JSArgumentListImpl":
                return "a";
            case "PsiWhiteSpaceImpl":
                return "W";

        }
        LOG.warn("NoFind PSICLASS"+classname);
        return "X";
    }
    public String AllTypes(PsiElement psi){
        String ret="";
        while(psi!=null){
            ret=ret+Psi2TypeChar(psi);
            psi=psi.getParent();
        }
        return ret;
    }
    //对象结构判定
    public static boolean O(JSObjectLiteralExpressionImpl obj,String[] where){
        switch(where[0]){
            case "haveattr"://是否包含某个属性
                return obj.findProperty(where[1])!=null;
        }
        return true;
    }
    public static boolean E(ES6PropertyImpl obj,String[] where){
        return true;
    }
    public static boolean Y(JSPropertyImpl obj,String[] where){
        switch(where[0]){
            case "name"://是否包含某个属性
                String name=obj.getName();
                return name.equals(where[1]);
        }
        return true;
    }
}

                    /*
            case "F": return element instanceof JSFile;
            case "N": return element instanceof JSFunction;
            case "V": return element instanceof JSVariable;
            case "P": return element instanceof JSParameter;
            case "C": return element instanceof JSCallExpression;
            case "R": return element instanceof JSReferenceExpression;
            case "L": return element instanceof JSLiteralExpression;
            case "O": return element instanceof JSObjectLiteralExpression;
            case "A": return element instanceof JSArrayLiteralExpression;
            case "Y": return element instanceof JSProperty;
            case "T": return element instanceof JSThisExpression;
            case "I": return element instanceof JSIfStatement;
            case "J": return element instanceof JSForStatement;
            case "W": return element instanceof JSWhileStatement;
            case "U": return element instanceof JSReturnStatement;
            case "B": return element instanceof JSBlockStatement;
            case "G": return element instanceof JSArgumentList;
                 */