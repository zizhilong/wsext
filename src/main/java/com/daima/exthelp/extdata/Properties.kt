package com.daima.exthelp.extdata

import com.fasterxml.jackson.annotation.JsonProperty

data class Properties(
    @JsonProperty("Name")
    var name: String = "",

    @JsonProperty("ValueType")
    var valueType: List<String> = listOf(),

    @JsonProperty("ReadOnly")
    var readOnly: Boolean = false,

    @JsonProperty("BindAble")
    var bindAble: Boolean = false,

    @JsonProperty("Visibility")
    var visibility: Int = 0,

    @JsonProperty("Html")
    var html: String? = null,

    @JsonProperty("Default")
    var defaultValue: String? = null,

    @JsonProperty("Chainable")
    var chainable: Boolean = false
)