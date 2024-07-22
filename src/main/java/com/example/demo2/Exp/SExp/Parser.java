package com.example.demo2.Exp.SExp;

import com.example.demo2.Tools.ExpHelper;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;

import java.util.ArrayList;
import java.util.List;
//向下查找解释器
public class Parser {
    private static final Logger LOG = Logger.getInstance(Parser.class);
    public boolean debug=false;
    List<Struct> enss = new ArrayList<>();
    public Parser(String expr) {
        //进行解析
        IStringExpression se=new StringExpression(expr);
        while(true){
            //如果字符串为空则直接退出
            if(se.getFirst().isEmpty()){
                return;
            }

            Struct struct=new Struct(se);
            enss.add(struct);
            se=struct.se;
        }
    }

    public PsiElement Run(PsiElement psi){

        //交给数量解析器处理
        for (Struct ens : enss) {
            if(ens.Parent){
                LOG.info("SExp Parent");
                psi=psi.getParent();
                continue;
            }
            LOG.info("SExp Run "+ens.Type);
            //循环所有下级
            boolean isfind=false;

            for(PsiElement child:psi.getChildren()){
                //如果命中
                if(ens.Run(child,debug)){
                    psi=child;
                    isfind=true;
                    break;
                }
            }
            if(!isfind){
                if(debug){
                    String sub="";
                    for(PsiElement child:psi.getChildren()){
                        sub+=ExpHelper.Psi2TypeChar(child);
                    }
                    LOG.warn("SExp Not Find Sub IS ["+sub+"]");
                }
                return null;
            }
        }
        return psi;
    }
}