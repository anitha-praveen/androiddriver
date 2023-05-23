package com.rodaClone.driver.loginSignup.tour

import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.rodaClone.driver.base.BaseVM
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.connection.ConnectionHelper
import com.rodaClone.driver.connection.CustomException
import com.rodaClone.driver.ut.SessionMaintainence
import retrofit2.Call
import javax.inject.Inject

class TourGuideVM  @Inject constructor(
    val session: SessionMaintainence,
    val mConnect: ConnectionHelper
) :
    BaseVM<BaseResponse, TourGuideNavigator>(session, mConnect) {
    var skipDisable = ObservableBoolean(false)
    var forwardtxt = ObservableField<String>("")
    var isFirst = ObservableBoolean(true)
    override fun onSuccessfulApi(response: BaseResponse?) {
        isLoading.value = false
    }

    override fun onFailureApi(call: Call<BaseResponse?>?, t: CustomException?) {
        isLoading.value = false
        getNavigator().showMessage(t!!.exception!!)
    }

    fun onClickNext() {
        getNavigator().forwardClick()
    }

}

@BindingAdapter("layout_width")
fun setLayoutWidth(view: CardView, width: Float) {
    val layoutParams: ViewGroup.LayoutParams = view.layoutParams
    layoutParams.width = width.toInt()
    view.layoutParams = layoutParams
}