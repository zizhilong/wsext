package com.daima.exthelp.ext.extclass.extview

fun ItemSelector(item: ExtViewItemClass?, selector: String): List<ExtViewItemClass> {
    if(item==null){
        return listOf()
    }
    // Define a list to hold matching items
    val matchingItems = mutableListOf<ExtViewItemClass>()
    val (xtype, seachKey, seachVal) = parseViewItemString(selector)
    // Define recursive function to traverse ViewItem and its sub-items
    fun findMatchingViewItems(item: ExtViewItemClass) {
        // Recursively check sub-items
        for (subItem in item.getSubItems()) {
            findMatchingViewItems(subItem)
        }
        // Check if the selector matches xtype
        if (xtype.isNotEmpty() && item.getXtypeName() != xtype) {
            return
        }
        if(seachKey.isNotEmpty()){
            var propertyValue = item.getPropertyTextValue(seachKey)

            //如果匹配的属性不存在
            if(propertyValue==null||propertyValue.isEmpty()){
                return
            }
            if(seachVal!="*"  && seachVal!=propertyValue){
                return
            }
        }
        matchingItems.add(item)
    }
    if(selector.contains("*")){
        var a=1
    }

    // Find matching ViewItems
    findMatchingViewItems(item)

    // Return all matching items
    return matchingItems
}
private fun parseViewItemString(input: String?): Triple<String, String, String> {
    if (input.isNullOrEmpty()) return Triple("", "", "")

    val cleanedInput = input.trim('\'', '"')
    val regex = """^\s*([a-zA-Z0-9_]+)?(?:\s*\[\s*([a-zA-Z0-9_]+)\s*=\s*([^\]]+)\s*\])?\s*$""".toRegex()
    val matchResult = regex.matchEntire(cleanedInput)

    if (matchResult != null) {
        val xtype = matchResult.groupValues.getOrNull(1) ?: ""
        val seachKey = matchResult.groupValues.getOrNull(2) ?: ""
        val seachVal = matchResult.groupValues.getOrNull(3) ?: ""
        return Triple(xtype, seachKey, seachVal)
    }

    return Triple("", "", "")
}