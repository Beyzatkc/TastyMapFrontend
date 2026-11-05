package org.beem.tastymap.ui.tastyview.state

class TastyMutableState<T>(initialValue: T) {
    private val listeners = mutableListOf<(T) -> Unit>()

    var value: T = initialValue
        set(newValue) {
            field = newValue
            listeners.forEach { it(newValue) }
        }

    fun observe(listener: (T) -> Unit) {
        listeners.add(listener)
        listener(value)
    }
}