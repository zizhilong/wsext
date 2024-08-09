package com.daima.exthelp.Inspection.Contorler

import com.daima.exthelp.Exp.SExp.Parser
import com.daima.exthelp.Inspection.model.ControllerModel
import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.lang.javascript.psi.JSFile
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
private val LOG: Logger = Logger.getInstance("EventBind")
fun EventBind(file: JSFile, holder: ProblemsHolder, isOnTheFly: Boolean){
    if(isOnTheFly){
        return
    }
    if (file.virtualFile == null || !file.virtualFile.name.endsWith("Controller.js")) {
        return
    }

    val cm=ControllerModel(file)
    for(ebg in cm.EventBindGroup()){
        for(eb in ebg.EventBind()){
            //LOG.info(eb.name);
            //LOG.info(eb.funName);
            val pfun=cm.GetFunctionByName(eb.funName)
            if (pfun==null){
                //manager.createProblemDescriptor(eb.psi,)
                holder.registerProblem(eb.psi!!,"函数未实现", ProblemHighlightType.ERROR)
            }

        }
    }

}