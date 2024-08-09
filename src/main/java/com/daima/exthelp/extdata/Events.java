package com.daima.exthelp.extdata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/*
Copyright (c) 2024 Kotlin Data Classes Generated from JSON powered by http://www.json2kotlin.com

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

For support, please feel free to contact me at https://www.linkedin.com/in/syedabsar
*/
@JsonIgnoreProperties(ignoreUnknown = true) // 添加此注解以忽略未知属性

public class Events {

    @JsonProperty("Name")
    private String name;
    @JsonProperty("Html")
    private String html;
    @JsonProperty("Params")
    private List<OneParams> params;
    public Events(){
            }
    // Constructor
    public Events(String name, boolean readOnly, boolean bindAble, int visibility, String defaultValue, boolean chainable, List<OneParams> params, String returnType, String html) {
        this.name = name;
        this.params = params;
    }

    // Getters
    public String getName() {
        return name;
    }
    public String getHtml() {
        return html;
    }
    public List<OneParams> getParams() {
        return params;
    }
    // Setters
    public void setName(String name) {
        this.name = name;
    }

}