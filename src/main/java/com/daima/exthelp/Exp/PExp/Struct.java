package com.daima.exthelp.Exp.PExp;

import com.daima.exthelp.Tools.ExpHelper;
import com.intellij.lang.javascript.psi.impl.JSObjectLiteralExpressionImpl;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;

import java.util.Objects;

class Struct {
    private static final Logger LOG = Logger.getInstance(Struct.class);
    private static final String SPECIFIC_CHARACTERS = ".NVPCROAYTIJWBGLE";
     public String Type;
     public boolean IsNot =false;
     String[] Wheres = new String[]{};
     public IStringExpression se;

     public Struct(IStringExpression argSe){
         se=argSe;
        if(se.getFirst().equals("^")){
            IsNot=true;
            se.removeLeftCharacters(0);
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
    public boolean runExp(PsiElement psi){
         if(Objects.equals(Type, ".")){
             return true;
         }
        //PsiElement
        String psiType= ExpHelper.Psi2TypeChar(psi);
         LOG.info("匹配前缀"+psiType+",当前前缀."+Type);
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
            }
            if(!ret){
                return false;
            }
        }
        return true;
    }
}
