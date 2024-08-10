package com.daima.exthelp.extdata

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true) // 忽略未知属性
data class Events(
    @JsonProperty("Name")
    var name: String = "",
    @JsonProperty("Html")
    var html: String = "",
    @JsonProperty("Params")
    var params: List<OneParams>? = emptyList()
)