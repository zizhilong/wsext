package com.daima.exthelp.rundebugplugin

import com.intellij.execution.configurations.RunConfigurationOptions
import com.intellij.openapi.components.StoredProperty

class MyRunConfigurationOptions : RunConfigurationOptions() {
    private val myOption: StoredProperty<String?> = string("").provideDelegate(this, "myOption")

    var myOptionValue: String?
        get() = myOption.getValue(this)
        set(value) = myOption.setValue(this, value)
}