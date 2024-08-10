package com.daima.exthelp.extdata

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true) // 忽略未知属性
data class Methods(
    @JsonProperty("Name")
    var name: String = "",

    @JsonProperty("ReadOnly")
    var readOnly: Boolean = false,

    @JsonProperty("BindAble")
    var bindAble: Boolean = false,

    @JsonProperty("Visibility")
    var visibility: Int = 0,

    @JsonProperty("Default")
    var defaultValue: String? = null,

    @JsonProperty("Chainable")
    var chainable: Boolean = false,

    @JsonProperty("Params")
    var params: List<Params>? = null,

    @JsonProperty("ReturnType")
    var returnType: String? = null,

    @JsonProperty("Html")
    var html: String? = null
)