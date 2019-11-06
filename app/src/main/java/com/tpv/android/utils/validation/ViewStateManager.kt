package com.tpv.android.utils.validation


/**
 * Manage the view's enable disable state based on applied validator
 * if all validator is valid then onStateChange callback is called with isEnable true
 */
class ViewStateManager(init: ViewStateManager.() -> Unit) {

    private val views = arrayListOf<BaseValidator>()
    private var viewStateChangeListener: ((isEnable: Boolean) -> Unit)? = null


    /**
     * Add validator of views to manage view state
     */
    fun addView(vararg validator: BaseValidator) {
        views.addAll(validator)
    }

    /***
     * Add single validator
     */
    fun addView(validator: BaseValidator) {
        views.add(validator)
    }


    /**
     * Check view state against all added validator
     */
    @Synchronized
    private fun manageViewState() {
        viewStateChangeListener?.invoke(views.find { !it.isValid } == null)
    }


    /**
     * Callback to handle target view state
     */
    fun onStateChange(function: (isEnable: Boolean) -> Unit) {
        viewStateChangeListener = function

        /**
         * Check initially view state
         */
        manageViewState()
    }


    init {
        init()
        views.forEach {
            it.onUpdateListener = {
                manageViewState()
            }
        }
    }
}