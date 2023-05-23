package com.rodaClone.driver.base

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import com.google.android.material.snackbar.Snackbar
import com.rodaClone.driver.R
import com.rodaClone.driver.ut.Utilz
import dagger.android.support.DaggerDialogFragment

/**
 * This activity to be extended to All DialogFragment
 * */
abstract class BaseDialog<T : ViewDataBinding, V> : DaggerDialogFragment(), BaseViewOperator {
    var mBinding: ViewDataBinding? = null
    var mActivity: FragmentActivity? = null
    var mView: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, getLayout(), container, false)
        (mBinding as ViewDataBinding).lifecycleOwner = this
        mView = (mBinding as ViewDataBinding).root
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (mBinding as ViewDataBinding).setVariable(getBindingVariable(), getViewModel())
    }

    abstract fun getLayout(): Int;
    abstract fun getBindingVariable(): Int;
    abstract fun getViewModel(): V;
    fun getmBinding(): T {
        return mBinding as T
    }

    override fun showMessage(message: String) {
        Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show()
    }

    fun showMessage(message: Int) {
        Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show()
    }

    fun showSnackBar(message: Int, view: View) {
        Snackbar.make(mActivity!!.findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
            .show()
    }

    fun showSnackBar(message: String, view: View?) {
        Snackbar.make(mActivity!!.findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
            .show()
    }


    /**
     * set the dialog fragment to fullscreen
     */
    open fun setDialogFullSCreen() {
        if (dialog!!.window != null) {
            dialog!!.window!!.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    mActivity as Context,
                    R.color.clr_trans
                )
            )
            dialog!!.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        dialog!!.setCanceledOnTouchOutside(false)
    }

    /**
     * set the dialog fragment to fullscreen
     */
    open fun setDialogStretchScreen() {
        if (dialog!!.window != null) {
            dialog!!.window!!.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    mActivity as Context,
                    R.color.clr_trans
                )
            )
            dialog!!.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        dialog!!.setCanceledOnTouchOutside(false)
    }

    open fun setDialogFitWidth() {
        if (dialog!!.window != null) {
            dialog!!.window!!.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    mActivity as Context,
                    R.color.clr_trans
                )
            )
            dialog!!.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        dialog!!.setCanceledOnTouchOutside(false)
    }

    open fun setClearBackground() {
        if (dialog!!.window != null)
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }


    fun changeStatusBarClor(color: Int) {
        if (Build.VERSION.SDK_INT >= 21) {
            val window: Window = mActivity!!.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = ContextCompat.getColor(mActivity!!, color)
        }
    }

    fun isNetworkAvailable() = Utilz.checkForInternet(mActivity as FragmentActivity)

    override fun showNetworkUnAvailable() {
//        (mActivity as BaseAppActivity).showNetworkDialog()
    }

    fun hasPermissions(context: Context, permissions: Array<String>): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    fun hideKeyBoard(view: View){
        val imm = mActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0);
    }

}