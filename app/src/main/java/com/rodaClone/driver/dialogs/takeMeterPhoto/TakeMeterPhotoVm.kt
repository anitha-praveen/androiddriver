package com.rodaClone.driver.dialogs.takeMeterPhoto

import android.content.Intent
import android.graphics.Bitmap
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableField
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseVM
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.connection.ConnectionHelper
import com.rodaClone.driver.connection.CustomException
import com.rodaClone.driver.ut.Config
import com.rodaClone.driver.ut.SessionMaintainence
import com.rodaClone.driver.ut.Utilz
import retrofit2.Call
import java.io.File
import javax.inject.Inject

class TakeMeterPhotoVm @Inject constructor(
    val session: SessionMaintainence,

    val mConnect: ConnectionHelper

) : BaseVM<BaseResponse, TakeMeterPhotoNavigator>(session, mConnect) {
    var bitmapUrl = ObservableField("")
    var startKM = ObservableField("")
    val mode = ObservableField(0)
    val reckms = ObservableField(0)
    override fun onSuccessfulApi(response: BaseResponse?) {

    }

    override fun onFailureApi(call: Call<BaseResponse?>?, t: CustomException?) {

    }


    fun onClickCamera() {
        getNavigator().openCamera()
    }

    fun onSubmitClick() {
        if (validation()) {
            if (mode.get() == 0){
                val intent = Intent(Config.RECEIVE_TRIP_IMAGE)
                intent.putExtra(Config.trip_image, bitmapUrl.get())
                intent.putExtra(Config.start_km, startKM.get())
                getNavigator().getCtx()
                    ?.let { LocalBroadcastManager.getInstance(it).sendBroadcast(intent) }
                getNavigator().dissmissFrag()
            }
            else{
                if (startKM.get()!!.toInt() > reckms.get()!!){
                    val intent = Intent(Config.RECEIVE_TRIP_IMAGE)
                    intent.putExtra(Config.trip_image, bitmapUrl.get())
                    intent.putExtra(Config.start_km, startKM.get())
                    getNavigator().getCtx()
                        ?.let { LocalBroadcastManager.getInstance(it).sendBroadcast(intent) }
                    getNavigator().dissmissFrag()
                }
                else{
                    translationModel?.txt_endKms_error?.let { getNavigator().showMessage(it) }
                }
            }
        }
    }

    fun validation(): Boolean {
        var msg = ""
        if (bitmapUrl.get()!!.isEmpty())
            msg = "Upload meter image"
        else if (startKM.get()!!.isEmpty())
            msg = translationModel?.txt_enter_meterValue ?: "Enter meter value"

        return if (msg.isNotEmpty()) {
            getNavigator().showMessage(msg)
            false
        } else
            true
    }


    var realPath: String? = null
    var realFile: File? = null
    fun onCaptureImageResult(data: Intent?) {
        if (data != null) {
            if (data.hasExtra("data")) {
                realPath = getNavigator().getCtx()?.let {
                    Utilz.getImageUri(
                        it, data.extras?.get(
                            "data"
                        ) as Bitmap
                    )
                }
                realPath?.let { realFile = File(it) }
                bitmapUrl.set(realPath)
            }
        }

    }

}

@BindingAdapter("meterImage")
fun setImageUrl(imageView: ImageView, url: String?) {
    if (url != null) {
        val context = imageView.context
        Glide.with(context).load(url).apply(
            RequestOptions.centerCropTransform().error(R.drawable.shape)
                .placeholder(R.drawable.shape).diskCacheStrategy(DiskCacheStrategy.ALL)
        ).into(imageView)
    }
}