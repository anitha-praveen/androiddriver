package com.rodaClone.driver.base

/**
 * Basic operations or common funtions are
 * declared over here..
 * Is Network connected, show Snackbar, show Toast message
 * */
interface BaseViewOperator {
    /**
     * show Toast message
     * */
    fun showMessage(message: String)

    /**
     * show Snackbar
     * */
    fun showSnackBar(message: String)

    /**
     * check if Network is connected
     * */
    fun isNetworkConnected(): Boolean

    /**
     * show Toast if network is unavailable
     * */
    fun showNetworkUnAvailable()
}