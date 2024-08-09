package com.daima.exthelp.rundebugplugin

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.project.Project

class MyConfigurationFactory(type: ConfigurationType) : ConfigurationFactory(type) {
    override fun createTemplateConfiguration(project: Project): RunConfiguration {
        return MyRunConfiguration(project, this, "MyRunConfiguration")
    }
    override fun getId(): String {
        return "EXT_RUNER_FACTORY"
    }
}