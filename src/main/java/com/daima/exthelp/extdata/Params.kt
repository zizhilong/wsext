package com.daima.exthelp.extdata

import com.fasterxml.jackson.annotation.JsonProperty

data class Params(
    @JsonProperty("Name")
    var name: String? = null,

    @JsonProperty("ValueType")
    var valueType: List<String>? = null,

    @JsonProperty("Optional")
    var optional: Boolean = false
)