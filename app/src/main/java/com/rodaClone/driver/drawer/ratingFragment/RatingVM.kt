package com.rodaClone.driver.drawer.ratingFragment

import android.app.Application
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableField
import androidx.databinding.ObservableFloat
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseVM
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.connection.ConnectionHelper
import com.rodaClone.driver.connection.CustomException
import com.rodaClone.driver.connection.responseModels.RequestResponseData
import com.rodaClone.driver.ut.Config
import com.rodaClone.driver.ut.SessionMaintainence
import kotlinx.coroutines.*
import retrofit2.Call
import java.net.URL
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

class RatingVM @Inject constructor(
    val application: Application,
    val session: SessionMaintainence,
    val mConnect: ConnectionHelper
) :
    BaseVM<BaseResponse, RatingNavigator>(session, mConnect) {
    var requestId = ""
    var userPic = ObservableField("")
    var userName = ObservableField("")
    var savedRating = ObservableField("")
    var txt_comments = ObservableField("")
    var driverReview = ObservableFloat(5f)
    var hashMap = HashMap<String, String>()
    var requestData: RequestResponseData? = null
    lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onSuccessfulApi(response: BaseResponse?) {
        isLoading.value = false
        getNavigator().goToHome()

    }

    override fun onFailureApi(call: Call<BaseResponse?>?, t: CustomException?) {
        isLoading.value = false
        getNavigator().showMessage(t!!.exception!!)
    }

    fun setData() {
        requestData?.let { data ->
            data.userOverallRating?.let { savedRating.set("$it") }
            data.id?.let { requestId = it }
            data.user?.let { user ->
                user.profilePic?.let {
                    //getImageFromS3(it)
                    userPic.set(it)
                }
                user.firstname?.let { first ->
                    user.lastname?.let { userName.set("$first $it") }
                }
            }

        }
    }

    fun hideKeyboard(){

    }

    fun getImageFromS3(profilePic: String?) {
        val viewModelJob = Job()
        val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

        uiScope.launch {
            withContext(Dispatchers.IO) {
                val cal = Calendar.getInstance()
                cal.time = Date()
                cal.add(Calendar.HOUR, +1)
                val oneHourLater: Date = cal.time
                val s3: AmazonS3 = AmazonS3Client(
                    BasicAWSCredentials(
                        session.getString(SessionMaintainence.AWS_ACCESS_KEY_ID),
                        session.getString(SessionMaintainence.AWS_SECRET_ACCESS_KEY)
                    )
                )
                s3.setRegion(Region.getRegion(session.getString(SessionMaintainence.AWS_DEFAULT_REGION)))
                val url: URL = s3.generatePresignedUrl(
                    session.getString(SessionMaintainence.AWS_BUCKET),
                    profilePic,
                    oneHourLater
                )
                //setImage(url.toString())
                //vm.refURL.set(url.toString())
                withContext(Dispatchers.Main) {
                    // update here in UI
                    userPic.set(url.toString())

                }
            }
        }

    }

    fun updateReview() {
        //            getNavigator().showCustomDialog(translationModel.txt_rate_driver!!)

        if (driverReview.get().toString()=="0.0")
            getNavigator().showMessage(translationModel?.txt_rate_driver!!)
        else {
            if (getNavigator().isNetworkConnected()) {
                hashMap.clear()
                hashMap[Config.request_id] = requestId
                hashMap[Config.rating] = "${driverReview.get()}"
                if (txt_comments.get()!!.isNotEmpty())
                    hashMap[Config.feedback] = txt_comments.get()!!
                rateUserBase(hashMap)
            } else
                getNavigator().showNetworkUnAvailable()
        }
    }

    fun moveCamera(googleMap: GoogleMap, latLng: LatLng?, zoomLevel: Float) {
        googleMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(latLng!!, zoomLevel)
        )
    }



}



@BindingAdapter("imageUrlFeedback")
fun setImageUrl(imageView: ImageView, url: String?) {
    val context = imageView.context
    Glide.with(context).load(url).apply(
        RequestOptions().error(R.drawable.profile_place_holder)
            .placeholder(R.drawable.profile_place_holder)
    )
        .into(imageView)
}