package com.rodaClone.driver.ut

import android.app.Activity
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Environment
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import androidx.work.*
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.google.maps.android.PolyUtil
import com.rodaClone.driver.connection.responseModels.Step
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.connection.CustomException
import com.rodaClone.driver.connection.responseModels.Route
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * @source https://www.apriorit.com/dev-blog/612-mobile-cybersecurity-encryption-in-android
 * */
object Utilz {


    fun checkForInternet(context: Context): Boolean {

        // register activity with the connectivity manager service
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // if the android version is equal to M
        // or greater we need to use the
        // NetworkCapabilities to check what type of
        // network has the internet connection
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // Returns a Network object corresponding to
            // the currently active default data network.
            val network = connectivityManager.activeNetwork ?: return false

            // Representation of the capabilities of an active network.
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                // Indicates this network uses a Wi-Fi transport,
                // or WiFi has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

                // Indicates this network uses a Cellular transport. or
                // Cellular has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                // else return false
                else -> false
            }
        } else {
            // if the android version is below M
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }

    fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /* check empty and null  */
    fun isEmpty(value: String): Boolean {
        return value.isEmpty() || value.isBlank()
    }

    var data: BaseResponse.NewUser? = null
    fun convertToException(response: ResponseBody, code: Int): CustomException? {
        var value = ""
        var exception: CustomException? = null
        try {
            val baseResponse: BaseResponse =
                StringToObject(response.string(), BaseResponse::class.java) as BaseResponse

            if (code == 412) {
                val error =
                    Gson().fromJson(Gson().toJson(baseResponse.data), ValidationError::class.java)
                error?.let { e ->
                    e.firstname?.let {
                        if(it.isNotEmpty())
                            value = it[0]
                    }
                    e.lastName?.let {
                        if(it.isNotEmpty())
                            value = it[0]
                    }
                    e.email?.let {
                        if (it.isNotEmpty())
                            value = it[0]
                    }
                    e.phoneNumber?.let {
                        if(it.isNotEmpty())
                            value = it[0]
                    }
                    e.countryCode?.let {
                        if(it.isNotEmpty())
                            value =it[0]
                    }
                    e.vehicleTypeSlug?.let {
                        if(it.isNotEmpty())
                            value = it[0]
                    }
                    e.deviceInfoHash?.let {
                        if(it.isNotEmpty())
                            value = it[0]
                    }
                    e.deviceType?.let {
                        if(it.isNotEmpty())
                            value = it[0]
                    }
                    e.carNumber?.let {
                        if(it.isNotEmpty())
                            value = it[0]
                    }
                    e.isPrimary?.let {
                        if(it.isNotEmpty())
                            value = it[0]
                    }
                    e.serviceLocation?.let {
                        if(it.isNotEmpty())
                            value = it[0]
                    }
                    e.loginMethod?.let {
                        if(it.isNotEmpty())
                            value = it[0]
                    }
                    e.serviceCategory?.let {
                        if(it.isNotEmpty())
                            value = it[0]
                    }
                    e.brandLabel?.let {
                        if(it.isNotEmpty())
                            value = it[0]
                    }
                }
                if (value.isEmpty()) value = baseResponse.message ?: "Validation Error"

                exception = if (data != null && data!!.errorCode != null)
                    CustomException(data!!.errorCode!!, value)
                else
                    CustomException(0, value)
            } else {
                if (baseResponse.data != null)
                    data =
                        Gson().fromJson(
                            baseResponse.data.toString(),
                            BaseResponse.NewUser::class.java
                        )
                if (baseResponse.message != null)
                    value = baseResponse.message!!

                exception = if (data != null && data!!.errorCode != null)
                    CustomException(data!!.errorCode!!, value)
                else
                    CustomException(0, value)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return exception
    }

    class ValidationError {
        @SerializedName("firstname")
        @Expose
        val firstname: List<String>? = null

        @SerializedName("email")
        @Expose
        val email: List<String>? = null

        @SerializedName("lastname")
        @Expose
        val lastName: List<String>? = null

        @SerializedName("phone_number")
        @Expose
        val phoneNumber: List<String>? = null

        @SerializedName("country_code")
        @Expose
        val countryCode: List<String>? = null

        @SerializedName("vehicle_type_slug")
        @Expose
        val vehicleTypeSlug: List<String>? = null

        @SerializedName("device_info_hash")
        @Expose
        val deviceInfoHash: List<String>? = null

        @SerializedName("device_type")
        @Expose
        val deviceType: List<String>? = null

        @SerializedName("car_number")
        @Expose
        val carNumber: List<String>? = null

        @SerializedName("is_primary")
        @Expose
        val isPrimary: List<String>? = null

        @SerializedName("service_location")
        @Expose
        val serviceLocation: List<String>? = null

        @SerializedName("login_method")
        @Expose
        val loginMethod: List<String>? = null

        @SerializedName("service_category")
        @Expose
        val serviceCategory: List<String>? = null

        @SerializedName("brand_label")
        @Expose
        val brandLabel: List<String>? = null
    }

    fun StringToObject(message: String?, objectClassName: Class<*>?): Any? {
        return Gson().fromJson<Any>(message, objectClassName)
    }

    fun <T> getSingleObject(json: String?, modelClass: Class<T>?): T? {
        try {
            return Gson().fromJson(json, modelClass)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }


    /**
     * @param inImage is Bitmap image and get a absolute path of the Image.
     * @return
     */
    fun getImageUri(context: Context, inImage: Bitmap): String? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmm", Locale.ENGLISH).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return try {
            val realFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",  /* suffix */
                storageDir /* directory */
            )
            val out = FileOutputStream(realFile)
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
            realFile.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            ""
        }
    }

    /**
     * @param phoneNumber is string from user entered.
     * @return a string value of remove first zero.
     */
    fun removeFirstZeros(phoneNumber: String): String? {
        var resolvedPhoeNumber = phoneNumber
        do {
            if (resolvedPhoeNumber.startsWith("0")) resolvedPhoeNumber =
                resolvedPhoeNumber.substring(
                    1,
                    if (resolvedPhoeNumber.length == 1) resolvedPhoeNumber.length else resolvedPhoeNumber.length
                )
        } while (resolvedPhoeNumber.startsWith("0"))
        return resolvedPhoeNumber
    }

    fun isEmailValid(email: String?): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }


    //Check the google map is installed or not
    fun isGoogleMapsInstalled(activity: Context): Boolean {
        return try {
            val info: ApplicationInfo =
                activity.getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    /**
     * @param response  is array of latlng for draw a route.
     * @param routeBean
     * @return
     */
    fun parseRoute(response: JsonObject, routeBean: Route): Route? {
        var stepBean: Step
        //   JSONObject jObject = new JSONObject(response);
        val jArray = response.getAsJsonArray("routes")
        for (i in 0 until jArray.size()) {
            val innerjObject = jArray[i].asJsonObject
            if (innerjObject != null) {
                if (innerjObject.has("overview_polyline"))
                    routeBean.polyPoints =
                        innerjObject.getAsJsonObject("overview_polyline")["points"].asString

                val innerJarry = innerjObject.getAsJsonArray("legs")
                for (j in 0 until innerJarry.size()) {
                    val jObjectLegs = innerJarry[j].asJsonObject
                    routeBean.setDistanceText(jObjectLegs.getAsJsonObject("distance")["text"].asString)
                    routeBean.setDistanceValue(jObjectLegs.getAsJsonObject("distance")["value"].asInt)
                    routeBean.setDurationText(jObjectLegs.getAsJsonObject("distance")["text"].asString)
                    routeBean.setDurationValue(jObjectLegs.getAsJsonObject("duration")["value"].asInt)
                    routeBean.setStartAddress(jObjectLegs["start_address"].asString)
                    if (jObjectLegs.has("end_address")) routeBean.setEndAddress(jObjectLegs["end_address"].asString)
                    routeBean.setStartLat(jObjectLegs.getAsJsonObject("start_location")["lat"].asDouble)
                    routeBean.setStartLon(jObjectLegs.getAsJsonObject("start_location")["lng"].asDouble)
                    routeBean.setEndLat(jObjectLegs.getAsJsonObject("end_location")["lat"].asDouble)
                    routeBean.setEndLon(jObjectLegs.getAsJsonObject("end_location")["lng"].asDouble)
                    val jstepArray = jObjectLegs
                        .getAsJsonArray("steps")
                    if (jstepArray != null) {
                        for (k in 0 until jstepArray.size()) {
                            stepBean = Step()
                            val jStepObject = jstepArray[k].asJsonObject
                            if (jStepObject != null) {
                                stepBean.setHtml_instructions(
                                    jStepObject["html_instructions"].asString
                                )
                                stepBean.setStrPoint(
                                    jStepObject
                                        .getAsJsonObject("polyline")["points"].asString
                                )
                                stepBean.setStartLat(
                                    jStepObject
                                        .getAsJsonObject("start_location")["lat"].asDouble
                                )
                                stepBean.setStartLon(
                                    jStepObject
                                        .getAsJsonObject("start_location")["lng"].asDouble
                                )
                                stepBean.setEndLat(
                                    jStepObject
                                        .getAsJsonObject("end_location")["lat"].asDouble
                                )
                                stepBean.setEndLong(
                                    jStepObject
                                        .getAsJsonObject("end_location")["lng"].asDouble
                                )
                                stepBean.setListPoints(
                                    PolyLineUtils()
                                        .decodePoly(stepBean.getStrPoint()!!)
                                )
                                routeBean.getListStep()!!.add(stepBean)
                            }
                        }
                    }
                }
            }
        }
        return routeBean
    }

    fun getBitmapFromDrawable(context: Context, @DrawableRes drawableId: Int): Bitmap? {
//        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        val drawable: Drawable?
        drawable =
            ContextCompat.getDrawable(context, drawableId)
        return if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else if (drawable is VectorDrawableCompat || drawable is VectorDrawable) {
            val bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        } else {
            throw IllegalArgumentException("unsupported drawable type")
        }
    }

    fun hideKeyboard(activity: Activity) {
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

    fun hideKeyboard(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun openKeyBoard(activity: Activity) {
        var view = activity.currentFocus
        if (view == null) {
            view = View(activity)
        }
        val imm: InputMethodManager =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED)
    }

    fun startWorkManager(context: Context) {
        val pwr = PeriodicWorkRequest.Builder(MyWorkerClass::class.java, 15, TimeUnit.MINUTES)
            .setConstraints(Constraints.NONE)
            .build()
        WorkManager
            .getInstance(context)
            .enqueue(pwr)
    }

    fun decodeOverviewPolyLinePoints(encoded: String?): List<LatLng> {
        return try {
            PolyUtil.decode(encoded)
        } catch (e: java.lang.Exception) {
            val modifiedEncodedPath = "$encoded@"
            PolyUtil.decode(modifiedEncodedPath)
        }
    }

    /* The below method updateDriverTripStatus will update trip status in firebase request node
    1 for arrived
    2 for start
    3 for end
    4 for cancel
    */
    fun updateDriverTripStatus(value: Int, reqID: String) {
        if (reqID.isNotEmpty()){
            val requestData = HashMap<String, Any>()
            val requestRef: DatabaseReference?
            var database = FirebaseDatabase.getInstance()
            requestRef = database.getReference("requests")
            // Get the input data
            requestData["driver_trip_status"] = value
            requestRef.child(reqID)
                .updateChildren(requestData)

//        val updateConstraints = Constraints.Builder()
//            .setRequiresCharging(false)
//            .setRequiredNetworkType(NetworkType.CONNECTED)
//            .build()
//
//        var data = Data.Builder()
//        data.putInt("driver_trip_status", value)
//        data.putString("reqID", reqID)
//        val oneTimeWorkRequest = OneTimeWorkRequest.Builder(UpdateTripStatusWorker::class.java)
//            .setInputData(data.build())
//            .setConstraints(updateConstraints).build()
//        val workManager = WorkManager.getInstance(context)
//        workManager.enqueue(oneTimeWorkRequest)
//        workManager.getWorkInfoByIdLiveData(oneTimeWorkRequest.id).observeForever {
//            if (it != null) {
//                Log.d("oneTimeWorkRequest", "Status changed to ${it.state.isFinished}")
//            }
//        }

        }
    }

    fun isTimeBetweenTwoTime(
        initialTime: String, finalTime: String,
        currentTime: String
    ): Boolean {
        return try {
            val reg = "^([0-1][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$".toRegex()
            if (initialTime.matches(reg) && finalTime.matches(reg) &&
                currentTime.matches(reg)
            ) {
                var valid = false
                //Start Time
                //all times are from java.util.Date
                val inTime = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).parse(initialTime)
                val calendar1 = Calendar.getInstance()
                calendar1.time = inTime

                //Current Time
                val checkTime = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).parse(currentTime)
                val calendar3 = Calendar.getInstance()
                calendar3.time = checkTime

                //End Time
                val finTime = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).parse(finalTime)
                val calendar2 = Calendar.getInstance()
                calendar2.time = finTime
                if (finalTime.compareTo(initialTime) < 0) {
                    calendar2.add(Calendar.DATE, 1)
                    calendar3.add(Calendar.DATE, 1)
                }
                val actualTime = calendar3.time
                if ((actualTime.after(calendar1.time) ||
                            actualTime.compareTo(calendar1.time) == 0) &&
                    actualTime.before(calendar2.time)
                ) {
                    valid = true
                    valid
                } else {
                    false
                }
            } else false
        } catch (e: ParseException) {
            e.printStackTrace()
            false
        }
    }

    fun removeZero(value : String) : String{
        return if(value.contains(".")){
            val array = value.split(".")
            if(allCharactersZero(array[1]))
                array[0]
            else
                value
        }else
            value
    }

    private fun allCharactersZero(s: String): Boolean {
        var msg = 0
        val chars: MutableList<Char> = s.toMutableList()
        for (char in chars){
            if(char != '0')
                msg += 1
        }
        return msg==0
    }

}