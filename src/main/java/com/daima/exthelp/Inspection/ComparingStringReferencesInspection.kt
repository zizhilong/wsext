package com.daima.exthelp.Inspection

import com.daima.exthelp.Inspection.Contorler.EventBind
import com.intellij.codeInspection.*
import com.intellij.lang.javascript.psi.JSFile
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.PsiRecursiveElementVisitor
import org.jetbrains.annotations.NotNull

/**
 * 实现一个检查工具，检测字符串引用是否使用 'a==b' 或 'a!=b' 进行比较。
 * 快速修复会将这些比较转换为 'a.equals(b)' 或 '!a.equals(b)'。
 */
class ComparingStringReferencesInspection : LocalInspectionTool() {


    /**
     * 重写此方法以提供自定义的访问器，
     * 检查包含关系运算符 '==' 和 '!=' 的表达式。
     * 访问器必须是线程安全的且不可递归。
     *
     * @param holder 持有器，用于访问器注册发现的问题
     * @param isOnTheFly 如果检查在非批处理模式下运行，则为 true
     * @return 此检查的非空访问器
     */
    @NotNull
    override fun buildVisitor(@NotNull holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PsiRecursiveElementVisitor() {

            /**
             * 评估二进制 PSI 表达式，查看它们是否包含关系运算符 '==' 和 '!=',
             * 且类型为字符串。
             * 忽略与 null 进行比较的表达式。
             * 如果满足这些条件，则在 ProblemsHolder 中注册问题。
             *
             * @param expression 要评估的二进制表达式
             */
            override fun visitFile(file: PsiFile) {
                //super.visitFile(file)
                if (file !is JSFile) {
                    return
                }
            }
        }
    }
}