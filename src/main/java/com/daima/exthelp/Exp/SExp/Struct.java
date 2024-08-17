package com.daima.exthelp.Exp.SExp;

import com.daima.exthelp.Tools.ExpHelper;
import com.intellij.lang.javascript.psi.JSExpressionStatement;
import com.intellij.lang.javascript.psi.impl.JSExpressionStatementImpl;
import com.intellij.lang.javascript.psi.impl.JSObjectLiteralExpressionImpl;
import com.intellij.lang.javascript.psi.impl.JSPropertyImpl;
import com.intellij.lang.javascript.psi.impl.JSReferenceExpressionImpl;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;

import java.util.Objects;

import static com.daima.exthelp.Tools.ExpHelper.SPECIFIC_CHARACTERS;

class Struct {
    private static final Logger LOG = Logger.getInstance(Struct.class);
     public String Type;
     public boolean IsNot =false;
     String[] Wheres = new String[]{};
     public IStringExpression se;
     public boolean Parent=false;

     public Struct(IStringExpression argSe){
         //Parent argSe;
        se=argSe;
        //如果是向上返回获取器
         if(se.getFirst().equals("<")){
             Parent=true;
             se.removeLeftCharacters(1);
             return;
         }
        if(!SPECIFIC_CHARACTERS.contains(se.getFirst())){
            throw new RuntimeException("获取表达对象错误，不是表达字母");
        }
        Type=se.getFirst();
        se.removeLeftCharacters(1);
        while (true){
            if(se.getFirst().isEmpty()){
                return;
            }
            //条件表达式
            if(se.getFirst().equals("{")){
                if (IsNot) {
                    throw new RuntimeException("在否定表达式时不能够设置条件");
                }
                //条件
                String whereStr =se.getSubstringFromEnd('}');
                if(whereStr.isEmpty()){
                    throw new RuntimeException("条件表达式异常");
                }
                //去掉大括号
                whereStr=whereStr.substring(1, whereStr.length() - 1);
                //设置Where条件
                Wheres=whereStr.split(",");
                //删除字符
                se.removeLeftCharacters(whereStr.length()+2);
            }else{
                return;
            }
        }
    }
    public boolean Run(PsiElement psi,boolean debug){
         if(Objects.equals(Type, ".")){
             return true;
         }
        //PsiElement
        String psiType= ExpHelper.Psi2TypeChar(psi);
         if(debug){
            LOG.info("PSI EQ "+Type+"="+psiType);
         }
        //
        if(!Objects.equals(Type, psiType)){
            return false;
        }

        if(Wheres.length==0){
            return true;
        }

        for (String wherestr : Wheres) {
            boolean ret=true;
                String[] whereargs = wherestr.split(" ");
            switch (psiType){
                case "O":
                    ret= ExpHelper.O((JSObjectLiteralExpressionImpl) psi,whereargs);
                    break;
                case "S":
                    ret= ExpHelper.S((JSExpressionStatementImpl) psi,whereargs);
                    break;
                case "Y":
                    ret= ExpHelper.Y((JSPropertyImpl) psi,whereargs);
                    break;
                case "R":
                    ret= ExpHelper.R((JSReferenceExpressionImpl) psi,whereargs);
                default:
                    LOG.warn("SExp NOT WHERE TYPE");
            }
            if(!ret){
                return false;
            }
        }
        return true;
    }
}
