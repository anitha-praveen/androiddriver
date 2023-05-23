package com.rodaClone.driver.base

import android.graphics.Color
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.rodaClone.driver.R
import com.rodaClone.driver.connection.CustomException


abstract class BaseViewHolder(itemView: View?) : RecyclerView.ViewHolder(
    itemView!!
),
    BaseViewOperator {
    abstract fun onBind(position: Int)

    /** shows toast msg  */
    fun showMessage(resId: Int) {
        Toast.makeText(itemView.context, itemView.context.getString(resId), Toast.LENGTH_SHORT)
            .show()
    }

    /** shows toast msg  */
    fun showMessage(e: CustomException) {
        Toast.makeText(itemView.context, e.message, Toast.LENGTH_SHORT).show()
    }


    /** shows toast msg  */
    override fun showMessage(message: String) {
        Toast.makeText(itemView.context, message, Toast.LENGTH_SHORT).show()
    }

    /** shows snackbar msg  */
    override fun showSnackBar(message: String) {
        val snackbar = Snackbar.make(itemView, message, Snackbar.LENGTH_LONG)
        snackbar.setActionTextColor(Color.RED)
        snackbar.show()
    }

    /** shows snackbar msg  */
    fun showSnackBar(view: View, message: String?) {
        val snackbar = Snackbar.make(view, message!!, Snackbar.LENGTH_LONG)
        snackbar.setActionTextColor(Color.RED)
        snackbar.show()
    }

    /** shows network connection error msg  */
    fun showNetworkMessage() {
        Toast.makeText(
            itemView.context,
            itemView.context.getString(R.string.txt_NoInternet),
            Toast.LENGTH_SHORT
        ).show()
    }


}
