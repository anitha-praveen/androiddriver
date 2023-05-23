package com.rodaClone.driver.drawer.profile

import android.app.Application
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import com.google.gson.Gson
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseVM
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.connection.ConnectionHelper
import com.rodaClone.driver.connection.CustomException
import com.rodaClone.driver.connection.responseModels.AvailableCountryAndKLang
import com.rodaClone.driver.connection.responseModels.DriverModel
import com.rodaClone.driver.ut.*
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import java.io.File
import java.net.URL
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

class ProfileFragVM @Inject constructor(
    val application: Application,
    val session: SessionMaintainence,
    val mConnect: ConnectionHelper
) :
    BaseVM<BaseResponse, ProfileFragNavigator>(session, mConnect) {

    var email = ObservableField<String>()
    var lastName = ObservableField("")
    var firstName = ObservableField("")
    var appliedLanguage = ObservableField("")
    var selectedLanguage = ObservableField("")
    var phone_Number = ObservableField<String>()


    /*
     apiCode 0 -> get profile api
             1 -> update profile image
             2 -> update language to db
             3 -> translation
             4 -> update profile details
      */
    var apiCode = -1
    var driver: DriverModel? = DriverModel()
    val gson = Gson()
    var realPath: String? = null
    var realFile: File? = null
    var genericsType: GenericsType? = null
    var bitmapProfilePicture: ObservableField<GenericsType> = ObservableField<GenericsType>()
    lateinit var data: AvailableCountryAndKLang
    var fname = ""
    var lname = ""
    var imageURL = ""

    /* click events */
    fun onClickCameraOrGallery() {
        if (isPhotoAvailable && imageURL.isNotEmpty())
            getNavigator().showProfileImg(imageURL)
        else
            getNavigator().alertSelectCameraGallery()
    }

    fun onClickVehicleInfo(v: View?) {
        getNavigator().openVehicleInfo()
    }

    fun onClickSave() {
        if (validation()) {
            if (getNavigator().isNetworkConnected()) {
                apiCode = 4
                isLoading.value = true
                requestbody.clear()
                if (firstName.get() != null && firstName.get()!!.isNotEmpty())
                    requestbody[Config.firstname] =
                        firstName.get()!!.toRequestBody("text/plain".toMediaTypeOrNull())
                if (lastName.get() != null && lastName.get()!!.isNotEmpty())
                    requestbody[Config.lastname] =
                        lastName.get()!!.toRequestBody("text/plain".toMediaTypeOrNull())
                if (email.get() != null)
                    requestbody[Config.email] =
                         email.get()!!.toRequestBody("text/plain".toMediaTypeOrNull())
                updateProfileBase()
            }
        }
    }

    /* API Calls */
    fun getProfileDetails() {
        if (getNavigator().isNetworkConnected()) {
            apiCode = 0
            driverGetProfileBase()
        } else
            getNavigator().showNetworkUnAvailable()
    }

    private fun uploadProfilePicture() {
        if (getNavigator().isNetworkConnected()) {
            apiCode = 1
            requestbody.clear()
            if (realPath != null) {
                val reqFile = realFile?.asRequestBody(("image/*".toMediaTypeOrNull()))
                body = reqFile?.let {
                    MultipartBody.Part.createFormData("profile_pic", realFile!!.name, it)
                }
                updateProfileImageApiBase()
            }
        } else getNavigator().showNetworkUnAvailable()
    }

    fun getSelectedLanguageTranslation(code: String) {
        if (getNavigator().isNetworkConnected()) {
            apiCode = 2
            val map = HashMap<String, String>()
            map[Config.language] = code
            getPostSelectedLanguage(map)
        } else getNavigator().showNetworkUnAvailable()
    }

    fun getLnaguages(code: String) {
        apiCode = 3
        getSelectedLangBase(code)
    }


    override fun onSuccessfulApi(response: BaseResponse?) {
        isLoading.value = false
        if (response!!.data != null) {
            when (apiCode) {
                0 -> {
                    driver = gson.fromJson(gson.toJson(response.data), DriverModel::class.java)
                    setUpProfileDetails(driver)
                }
                1 -> {
                    val data = gson.fromJson(
                        gson.toJson(response.data),
                        BaseResponse.DataObjectsAllApi::class.java
                    )
                    fname = data.user?.firstname ?: ""
                    lname = data.user?.lastname ?: ""
                    //val imageurl: String = data.user?.profilePic ?: ""
                    //getImageFromS3(data.user?.profilePic)
//                    data.user?.profilePic?.let { getNavigator().setImage(it) }
//                    getNavigator().showMessage(translationModel?.txt_profile_updated ?: "")
                    viewModelScope.launch {
                        withContext(Dispatchers.Main) {
                            data.user?.profilePic?.let {
                                getNavigator().setImage(it)
                                getNavigator().changeProfileDetails(fname, lname, it)
                            }
                        }
                    }


                }
                2 -> {
                    selectedLanguage.get()?.let { getLnaguages(it) }
                }
                3 -> {
                    session.saveString(
                        SessionMaintainence.TRANSLATED_DATA,
                        gson.toJson(response.data!!)
                    )
                    session.saveString(
                        SessionMaintainence.CURRENT_LANGUAGE,
                        selectedLanguage.get()!!
                    )
                    getNavigator().refresh()
                }
            }
        }
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
                    imageURL = url.toString()
                    getNavigator().setImage(url.toString())
                    getNavigator().changeProfileDetails(fname, lname, url.toString())
                }
            }
        }

    }

    override fun onFailureApi(call: Call<BaseResponse?>?, t: CustomException?) {
        isLoading.value = false
        getNavigator().showMessage(t!!.exception!!)
    }

    var isPhotoAvailable = false
    private fun setUpProfileDetails(driverModel: DriverModel?) {

        driverModel?.let {
            firstName.set(it.user?.firstname)
            lastName.set(it.user?.lastname)
            phone_Number.set(it.user?.phoneNumber)
            email.set(it.user?.email)
        }
        appliedLanguage.set(session.getString(SessionMaintainence.CURRENT_LANGUAGE))
        fname = firstName.get()!!
        lname = lastName.get()!!
        //getImageFromS3(driverModel?.user?.profilePic)
//        driverModel?.user?.profilePic?.let {
//            if (it.isNotEmpty())
//                isPhotoAvailable = true
//        }
        driverModel?.user?.profilePic?.let {
            if (it.isNotEmpty()) {
                isPhotoAvailable = true
                viewModelScope.launch {
                    withContext(Dispatchers.Main) {
                        getNavigator().setImage(it)
                    }
                }
            }
        }
    }


    fun onCaptureImageResult(data: Intent) {

        if (data != null) {
            if (data.hasExtra("data")) {
                realPath = Utilz.getImageUri( getNavigator().getCtx(), data.extras?.get("data") as Bitmap)
                realFile = realPath?.let { File(it) }
                genericsType = GenericsType()
                genericsType!!.set(realFile)
                bitmapProfilePicture.set(genericsType)
                uploadProfilePicture()
            }
        }

    }


    fun onSelectFromGalleryResult(data: Uri?) {
        if (data != null) {
            realPath = RealPathUtil.getRealPath(getNavigator().getCtx(), data)
            Log.v("fatal", realPath!!)
            realFile = (if (realPath == null) "" else realPath)?.let { File(it) }
            genericsType = GenericsType()
            genericsType!!.set(realFile)
            bitmapProfilePicture.set(genericsType)
            uploadProfilePicture()
        }
    }

    private fun validation(): Boolean {
        var msg = ""
        if (firstName.get()!!.isEmpty())
            msg = translationModel?.Validate_FirstName ?: getNavigator().getCtx()
                .getString(R.string.Validate_FirstName)
        else if (lastName.get()!!.isEmpty())
            msg = translationModel?.Validate_LastName ?: getNavigator().getCtx()
                .getString(R.string.Validate_LastName)
        else if (email.get() != null && email.get()!!
                .isNotEmpty() && !Utilz.isEmailValid(email.get())
        )
            msg = translationModel?.text_error_email_valid ?: getNavigator().getCtx()
                .getString(R.string.Validate_InvalidEmail)
        if (msg.isNotEmpty())
            getNavigator().showMessage(msg)
        return msg.isEmpty()
    }
}