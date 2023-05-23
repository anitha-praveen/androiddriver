    package com.rodaClone.driver.drawer.history.adapter

import android.annotation.SuppressLint
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableField
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseViewOperator
import com.rodaClone.driver.connection.responseModels.HistoryModel
import com.rodaClone.driver.connection.responseModels.RequestResponseData
import java.text.SimpleDateFormat
import java.util.*

class ItemHistoryVM(
    private val history: HistoryModel.History,
    private val adapterLister: HistoryItemListener
) {
    var userProfile = ObservableField(history.data?.user?.profilePic ?: "")
    var dateTime = ObservableField(
        if (history.data?.tripStartTime != null) {
            try {
                @SuppressLint("SimpleDateFormat") val mDate =
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(history.data.tripStartTime)
                if (mDate != null)
                    (SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.ENGLISH).format(mDate))
                else
                    ""
            } catch (e: Exception) {
                e.printStackTrace()
                history.data.tripStartTime
            }
        } else ""
    )
    var requestNumber = ObservableField(history.data?.requestNumber ?: "")
    var total = ObservableField(
        if (history.data?.requestBill == null || history.data.requestBill.billData?.totalAmount.toString()
                .isEmpty()
        ) ""
        else history.data.requestedCurrencySymbol + " " + history.data.requestBill.billData?.totalAmount.toString()
    )
    var pickup = ObservableField(history.data?.pickAddress ?: "")
    var drop = ObservableField(history.data?.dropAddress ?: "")
    fun onItemSelected() {
        history.data?.let { adapterLister.itemSelected(it) }
    }

    interface HistoryItemListener : BaseViewOperator {
        fun itemSelected(history: RequestResponseData)
    }

}

@BindingAdapter("imageUrlUserIcon")
fun setDriverImage(imageView: ImageView, url: String?) {
    Glide.with(imageView.context).load(url)
        .apply(
            RequestOptions().error(R.drawable.ic_prof_avatar).placeholder(R.drawable.ic_prof_avatar)
        )
        .into(imageView)
}