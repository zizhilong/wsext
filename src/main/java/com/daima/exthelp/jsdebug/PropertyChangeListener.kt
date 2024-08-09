package com.daima.exthelp.jsdebug

import java.beans.PropertyChangeListener

class MyPropertyChangeListener: PropertyChangeListener {
    override fun propertyChange(evt: java.beans.PropertyChangeEvent?) {
        println("Property changed: ${evt?.propertyName}")
    }
}