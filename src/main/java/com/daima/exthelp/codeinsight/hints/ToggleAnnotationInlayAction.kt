package com.daima.exthelp.codeinsight.hints

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ToggleAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiDocumentManager
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer

class ToggleAnnotationInlayAction : ToggleAction("Toggle Annotation Inlay"), DumbAware {

    override fun isSelected(e: AnActionEvent): Boolean {
        return AnnotationInlayProviderConfig.isEnabled
    }

    override fun setSelected(e: AnActionEvent, state: Boolean) {
        // 设置全局启用/禁用状态
        AnnotationInlayProviderConfig.isEnabled = state

        // 获取当前编辑器和项目
        val editor: Editor? = e.getData(com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR)
        val project = e.project

        if (editor != null && project != null) {
            // 获取对应的 PsiFile
            val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.document)

            // 清除当前的 Inlay 提示
            val inlays = editor.inlayModel.getInlineElementsInRange(0, editor.document.textLength)
            for (inlay in inlays) {
                inlay.dispose()  // 移除每个 Inlay 提示
            }

            // 重新绘制编辑器内容
            editor.contentComponent.repaint()

            // 触发重新检查和重新生成 Inlay 提示
            if (psiFile != null) {
                DaemonCodeAnalyzer.getInstance(project).restart(psiFile)
            }
        }
    }
}