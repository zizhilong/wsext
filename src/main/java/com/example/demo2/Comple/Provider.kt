package com.example.demo2.Comple
import com.example.demo2.Comple.Runner.ClassParam
import com.example.demo2.Comple.Runner.CompleRunner
import com.example.demo2.Comple.Runner.Xtype
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.util.ProcessingContext

class Provider : CompletionProvider<CompletionParameters>() {
    private val runners: List<CompleRunner> = listOf(ClassParam(),Xtype())
    // To add Xtype, uncomment the next line and add Xtype() to the list
    // runners = listOf(ClassParam(), Xtype())

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        for (instance in runners) {
            if (instance.addCompletions(parameters, context, result)) {
                return
            }
        }
    }
}