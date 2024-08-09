package com.daima.exthelp.rundebugplugin


import com.intellij.execution.configurations.*
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.execution.Executor
import com.intellij.execution.configurations.RunConfigurationOptions

class MyRunConfiguration(
    project: Project,
    factory: ConfigurationFactory,
    name: String
) : RunConfigurationBase<MyRunConfigurationOptions>(project, factory, name) {

    override fun getConfigurationEditor(): MyRunSettingsEditor {
        return MyRunSettingsEditor()
    }

    override fun checkConfiguration() {
        // Validate the configuration here
    }

    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState? {
        return MyRunProfileState(environment)
    }
}