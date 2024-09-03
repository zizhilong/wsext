package com.daima.exthelp.codeinsight.hints

import com.daima.exthelp.Tools.Dbdata.getColumnComment
import com.daima.exthelp.Tools.Dbdata.getTableComment
import com.intellij.codeInsight.hints.declarative.*
import com.intellij.lang.javascript.psi.impl.JSLiteralExpressionImpl
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.Key
import com.intellij.psi.*
import java.util.regex.Pattern

object AnnotationInlayProviderConfig {
    var isEnabled: Boolean = true
}

class AnnotationInlayProvider : InlayHintsProvider {

    companion object {
        const val PROVIDER_ID: String = "custom.annotation.hints"
        private val PROCESSED_KEY = Key.create<Boolean>("AnnotationInlayProvider.Processed")
        // 改进后的正则表达式：匹配全大写字母和下划线的部分
        private val PATTERN = Pattern.compile("\\b[A-Z_]+\\b")
    }

    override fun createCollector(file: PsiFile, editor: Editor): InlayHintsCollector? {
        // 判断全局开关是否启用
        if (!AnnotationInlayProviderConfig.isEnabled) {
            return null
        }
        return Collector()
    }

    private class Collector : SharedBypassCollector {

        override fun collectFromElement(element: PsiElement, sink: InlayTreeSink) {
            // 只处理字面量表达式
            if (element is JSLiteralExpressionImpl) {
                // 获取文本内容
                val rawText = element.text.trim()

                // 判断文本内容是否被单引号或双引号包围
                val isQuoted = (rawText.startsWith("'") && rawText.endsWith("'")) ||
                        (rawText.startsWith("\"") && rawText.endsWith("\""))

                if (isQuoted) {
                    // 去掉首尾的引号，提取内容
                    val content = rawText.substring(1, rawText.length - 1)

                    // 如果内容长度小于100字符，并且没有非法字符，继续处理
                    if (content.length <= 100 && content.none { it == '\'' || it == '"' }) {

                        // 使用改进后的正则表达式匹配内容
                        val matcher = PATTERN.matcher(content)
                        while (matcher.find()) {
                            val matchedText = matcher.group()

                            // 先尝试获取列注释
                            var memo = getColumnComment(matchedText)
                            if (memo == null) {
                                // 如果没有找到列注释，尝试获取表注释
                                memo = getTableComment(matchedText)
                            }

                            if (memo != null) {
                                // 计算提示位置
                                val startOffset = element.textRange.startOffset + matcher.start() + 1
                                val endOffset = startOffset + matchedText.length

                                // 添加 Inlay 提示
                                sink.addPresentation(InlineInlayPosition(endOffset, true), hasBackground = true) {
                                    text(memo)
                                }

                                // 标记该元素已经处理过
                                element.putUserData(PROCESSED_KEY, true)
                            }
                        }
                    }
                }
            }
        }
    }
}