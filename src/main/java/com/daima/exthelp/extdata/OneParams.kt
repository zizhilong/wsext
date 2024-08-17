package com.daima.exthelp.extdata

import com.fasterxml.jackson.annotation.JsonProperty

data class OneParams(
    @JsonProperty("Name")
    var name: String? = null,

    @JsonProperty("ValueType")
    private var _valueType: String? = null, // 使用私有字段来存储原始值

    @JsonProperty("Optional")
    var optional: Boolean = false,

    @JsonProperty("Text")
    var text: String = ""
) {
    var valueType: String?
        get()  {
            return _valueType?.trim()
        }
        set(value) {
            // 设置时自动去除首尾空格
            _valueType = value?.trim()
        }
}