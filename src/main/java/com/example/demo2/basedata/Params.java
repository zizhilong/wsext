package com.example.demo2.basedata;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/*
Copyright (c) 2024 Kotlin Data Classes Generated from JSON powered by http://www.json2kotlin.com

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

For support, please feel free to contact me at https://www.linkedin.com/in/syedabsar
*/

public class Params {

    @JsonProperty("Name")
    private String name;

    @JsonProperty("ValueType")
    private List<String> valueType;

    @JsonProperty("Optional")
    private boolean optional;
    public Params() {
    }
    // Constructor
    public Params(String name, List<String> valueType, boolean optional) {
        this.name = name;
        this.valueType = valueType;
        this.optional = optional;
    }

    // Getters
    public String getName() {
        return name;
    }

    public List<String> getValueType() {
        return valueType;
    }

    public boolean isOptional() {
        return optional;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setValueType(List<String> valueType) {
        this.valueType = valueType;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }
}