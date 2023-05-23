package com.rodaClone.driver.base

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.rodaClone.driver.ut.Utilz
import dagger.android.support.DaggerFragment

/**
 * This activity to be extended to All Fragment
 * */
abstract class BaseFragment<T : ViewDataBinding, V : ViewModel> : DaggerFragment() {
    lateinit var mbinding: T
    lateinit var mActivity: FragmentActivity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as FragmentActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        mbinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        return mbinding.root
    }

    fun showMessage(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    fun showSnackBar(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    fun isNetworkConnected(): Boolean {
        return Utilz.checkForInternet(mActivity)
    }

    fun showNetworkUnAvailable() {
        Toast.makeText(activity, "Network Unavailable, Connect to internet!", Toast.LENGTH_SHORT)
            .show()
    }

    open fun hideKeyboard(activity: Activity) {
        val imm: InputMethodManager =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //ViewModel is set as Binding Variable
        mbinding.apply {
            lifecycleOwner = this@BaseFragment
            setVariable(getBR(), getVMClass())
        }
    }

    fun hasPermissions(context: Context, permissions: Array<String>): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    fun getmBinding() = mbinding
    abstract fun getLayoutId(): Int
    abstract fun getBR(): Int
    abstract fun getVMClass(): V
}