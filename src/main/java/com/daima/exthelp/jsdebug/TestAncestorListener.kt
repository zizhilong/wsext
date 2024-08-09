package com.daima.exthelp.jsdebug

import javax.swing.event.AncestorEvent
import javax.swing.event.AncestorListener

class TestAncestorListener: AncestorListener {
    override fun ancestorAdded(event: AncestorEvent?) {
        var a=1

    }

    /**
     * Called when the source or one of its ancestors is made invisible
     * either by setVisible(false) being called or by its being
     * removed from the component hierarchy.  The method is only called
     * if the source has actually become invisible.  For this to be true
     * at least one of its parents must by invisible or it is not in
     * a hierarchy rooted at a Window
     *
     * @param event an `AncestorEvent` signifying a change in an
     * ancestor-component's display-status
     */
    override fun ancestorRemoved(event: AncestorEvent?) {
        var a=1
    }

    /**
     * Called when either the source or one of its ancestors is moved.
     *
     * @param event an `AncestorEvent` signifying a change in an
     * ancestor-component's display-status
     */
    override fun ancestorMoved(event: AncestorEvent?) {
        var a=1
    }
}