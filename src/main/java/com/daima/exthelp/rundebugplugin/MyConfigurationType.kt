package com.daima.exthelp.rundebugplugin


import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.configurations.ConfigurationTypeBase
import com.intellij.execution.configurations.ConfigurationTypeUtil
import com.intellij.icons.AllIcons

class MyConfigurationType : ConfigurationTypeBase(
    "MY_RUN_CONFIGURATION",
    "ExtJs Runer",
    "调试单页面",
    AllIcons.General.Information
) {
    init {
        addFactory(MyConfigurationFactory(this))
    }

    companion object {
        fun getInstance(): MyConfigurationType {
            return ConfigurationTypeUtil.findConfigurationType(MyConfigurationType::class.java)
        }
    }
}