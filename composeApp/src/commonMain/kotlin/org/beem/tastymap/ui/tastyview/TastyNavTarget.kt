package org.beem.tastymap.ui.tastyview

interface TastyNavTarget {
    val key: String
    val displayMode: SheetDisplayMode
    val wrapperStyle: String
    val activeTransformStyle: String
    fun renderContent(): String
    fun onTopCame(domElement: Any)
    fun onPushedBack(domElement: Any)
}