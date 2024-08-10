package com.daima.exthelp.extdata

import com.fasterxml.jackson.annotation.JsonProperty

data class OneParams(
    @JsonProperty("Name")
    var name: String? = null,

    @JsonProperty("ValueType")
    var valueType: String? = null,

    @JsonProperty("Optional")
    var optional: Boolean = false,

    @JsonProperty("Text")
    var text: String = ""
)