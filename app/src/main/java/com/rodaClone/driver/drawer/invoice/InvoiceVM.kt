package com.rodaClone.driver.drawer.invoice

import android.app.Application
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseVM
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.connection.ConnectionHelper
import com.rodaClone.driver.connection.CustomException
import com.rodaClone.driver.connection.responseModels.RequestResponseData
import com.rodaClone.driver.connection.responseModels.SingleHistoryResponse
import com.rodaClone.driver.ut.Config
import com.rodaClone.driver.ut.SessionMaintainence
import com.rodaClone.driver.ut.Utilz
import kotlinx.coroutines.*
import retrofit2.Call
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

class InvoiceVM @Inject constructor(
    val application: Application,
    val session: SessionMaintainence,
    val mConnect: ConnectionHelper
) :
    BaseVM<BaseResponse, InvoiceNavigator>(session, mConnect) {
    val title = ObservableField("")
    var pickup = ObservableField("")
    var drop = ObservableField("")
    var userProfilePic = ObservableField("")
    var rating = ObservableField("")
    var userName = ObservableField("")
    var vehType = ObservableField("")
    val recReqId = ObservableField("")
    var reqId = ObservableField("")
    var duration = ObservableField("")
    var total = ObservableField("")
    var distance = ObservableField("")
    var baseprice = ObservableField("")
    var distanceCost = ObservableField("")
    var hillStationFee = ObservableField("")
    var timeCost = ObservableField("")
    var waitingPrice = ObservableField("")
    var refferalBonus = ObservableField("")
    var promoBonus = ObservableField("")
    var serviceTax = ObservableField("")
    var total_trip_cost = ObservableField("")
    var cancellation_fees = ObservableField("")
    var custom_captain_fee = ObservableField("")
    var walletAmount = ObservableField("")
    var customCaptainShown = ObservableBoolean(false)
    var isAddnlChargeAvailable = ObservableBoolean(false)
    var isWalletTrip = ObservableBoolean(false)
    var requestData: RequestResponseData? = null
    var currency = ObservableField("")
    var bookingFee = ObservableField("")
    var mode = -1
    var mAdminCommission = ObservableField("")
    var mDriverCommission = ObservableField("")
    var showProfile = ObservableBoolean(false)

    var showStops = ObservableBoolean(false)
    var stopAddress = ObservableField("")

    val outZonetotal = ObservableField("")
    val isZone = ObservableBoolean(false)

    val isOutstation = ObservableBoolean(false)
    val startKm = ObservableField("")
    val endKm = ObservableField("")

    val showBillData = ObservableField(false)
    val distaCalTxt = ObservableField("")
    val tripStartTime = ObservableField("")
    val tripEndTime = ObservableField("")
    val tripDate = ObservableField("")
    val isRental = ObservableBoolean(false)

    /*
    mode 0 -> Invoice called from trip
         1 -> Invoice called from History
     */
    var showDispute = ObservableBoolean(false)
    var showTimeCost = ObservableBoolean(false)
    var showWaitingCost = ObservableBoolean(false)
    var showBonus = ObservableBoolean(false)
    var showHillStationPrice = ObservableBoolean(false)
    var outTripType = ObservableField("")
    var tripEndDate = ObservableField("")
    var textBasePrice = ObservableField("")
    var serviceCategory = ""
    var pkgTxt = ObservableField("")
    var showPkgTxt = ObservableBoolean(false)
    var showDistanceLayout = ObservableBoolean(false)
    var showDistanceDescLayout = ObservableBoolean(true)
    var distanceCostTxt = ObservableField("")
    var timeCostTxt = ObservableField("")
    override fun onSuccessfulApi(response: BaseResponse?) {
        isLoading.value = false
        if (response != null) {
            val json = Gson().toJson(response.data)
            val obj = Gson().fromJson(json, SingleHistoryResponse::class.java)
            requestData = obj.requestRespone
            if (requestData?.requestBill == null)
                showBillData.set(false)
            else
                showBillData.set(true)
            setData()
        }
    }

    override fun onFailureApi(call: Call<BaseResponse?>?, t: CustomException?) {
        isLoading.value = false
        getNavigator().showMessage(t!!.exception!!)
    }

    fun callSingleHistoryApi() {
        val map = HashMap<String, String>()
        map[Config.request_id] = recReqId.get().toString()
        getSingleHistoryBase(map)
    }

    private fun setData() {
        requestData?.let { request ->
            serviceCategory = request.serviceCategory ?: ""
            when (serviceCategory) {
                "LOCAL" -> {
                    title.set(translationModel?.text_invoice)
                    showBonus.set(true)
                    textBasePrice.set(translationModel?.text_base_price)
                    showPkgTxt.set(false)
                    distanceCostTxt.set(translationModel?.text_distance_cost)
                    showDistanceDescLayout.set(false)
                    request.requestBill?.billData?.distancePrice?.let {
                        if (it > 0)
                            showDistanceLayout.set(true)
                    }
                    timeCostTxt.set(translationModel?.text_time_cost)
                }
                "RENTAL" -> {
                    title.set(translationModel?.txt_rental_invoice)
                    isRental.set(true)
                    request.requestBill?.billData?.pendingKm?.let { pendingKm ->
                        if ( pendingKm != "0.0" && pendingKm != "0") {
                            showDistanceLayout.set(true)
                            distaCalTxt.set(
                                "(${Utilz.removeZero(pendingKm)} ${translationModel?.txt_km} x ${request.requestBill.billData.requestedCurrencySymbol} ${
                                    Utilz.removeZero(
                                        "${request.requestBill.billData.pricePerDistance}"
                                    )
                                })"
                            )
                        }
                    }
                    textBasePrice.set(translationModel?.txt_package_cost)
                    pkgTxt.set("(${request.requestBill?.billData?.packageHours} ${translationModel?.txt_hr} ${Utilz.removeZero("${request.requestBill?.billData?.packageKm}")} ${translationModel?.txt_km})")
                    showPkgTxt.set(true)
                    distanceCostTxt.set(translationModel?.txt_extra_distance_cost)
                    timeCostTxt.set(translationModel?.txt_extra_time_cost)
                }
                "OUTSTATION" -> {
                    title.set(translationModel?.txt_outstation_invoice)
                    isOutstation.set(true)
                    request.outstationTripType?.let { tripType ->
                        outTripType.set(
                            if (tripType.equals(
                                    "TWO",
                                    true
                                )
                            ) translationModel?.txt_two_way else translationModel?.txt_one_way
                        )
                        request.requestBill?.billData?.totalDistance?.let { totalDist ->
                            showDistanceLayout.set(true)
                            distaCalTxt.set(
                                "(${Utilz.removeZero("$totalDist")} ${translationModel?.txt_km} x ${request.requestBill.billData.requestedCurrencySymbol} ${
                                    Utilz.removeZero(
                                        "${request.requestBill.billData.pricePerDistance}"
                                    )
                                })"
                            )
                        }
                    }
                    textBasePrice.set(translationModel?.text_base_price)
                    showPkgTxt.set(false)
                    distanceCostTxt.set(translationModel?.text_distance_cost)
                    timeCostTxt.set(translationModel?.text_time_cost)
                }
            }
            if (mode != 0 && request.disputeStatus == 1) {
                showDispute.set(true)
            }
            showProfile.set(request.is_instant_trip == 0)
            request.vehName?.let {
                vehType.set(it)
            }
            request.pickAddress?.let { pickup.set(it) }
            request.dropAddress?.let { drop.set(it) }
            request.requestNumber?.let { reqId.set(it) }
            if (request.stops != null) {
                showStops.set(true)
            } else
                showStops.set(false)
            request.stops?.address?.let { stopAddress.set(it) }
            request.startKM?.let { startKm.set("$it ${translationModel?.txt_km}") }
            request.endKM?.let { endKm.set("$it ${translationModel?.txt_km}") }
            request.totalTime?.let {
                try {
                    val hours: Int =
                        it.toDouble().toInt() / 60 //since both are ints, you get an int
                    val minutes: Int = it.toDouble().toInt() % 60
                    if (hours >= 1 && minutes >= 1) {
                        duration.set("$hours ${translationModel?.txt_hrs} $minutes ${translationModel?.txt_min}")
                    } else if (hours >= 1 && minutes < 1) {
                        duration.set("$hours ${translationModel?.txt_hrs}")
                    } else if (hours < 1 && minutes >= 1) {
                        duration.set("$minutes ${translationModel?.txt_min}")
                    } else if (hours < 1 && minutes < 1) {
                        duration.set(it + " ${translationModel?.txt_min}")
                    }
                } catch (e: NumberFormatException) {
                    duration.set(it + " ${translationModel?.txt_min}")
                }
            }
            request.totalDistance?.let {
                distance.set(
                    "${
                        Utilz.removeZero(
                            "${
                                round(
                                    it,
                                    2
                                )
                            }"
                        )
                    } " + (request.unit ?: "")
                )
            }
            request.user?.let { user ->
                userProfilePic.set(user.profilePic)
                user.firstname?.let { first ->
                    user.lastname?.let {
                        userName.set("$first $it")
                    }
                }
            }
            request.tripStartTime?.let {
                try {
                    val mDate =
                        SimpleDateFormat("dd-MM-yyy HH:mm:ss", Locale.ENGLISH).parse(it)
                    if (mDate != null) {
                        tripDate.set(SimpleDateFormat("dd-MM-yy", Locale.ENGLISH).format(mDate))
                        tripStartTime.set(SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(mDate))
                    }


                } catch (e: Exception) {
                    e.printStackTrace()
                    tripDate.set(it)
                }
            }
            request.completedAt.let {
                try {
                    val mDate =
                        SimpleDateFormat("dd-MM-yyy HH:mm:ss", Locale.ENGLISH).parse(it)
                    SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.ENGLISH).format(mDate)
                    if (mDate != null) {
                        tripEndDate.set(
                            SimpleDateFormat(
                                "dd-MM-yy",
                                Locale.ENGLISH
                            ).format(mDate)
                        )
                        tripEndTime.set(SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(mDate))
                    }


                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            request.userOverallRating?.let { rating.set("" + round(it, 1)) }
            request.requestBill?.let { bill ->
                bill.billData?.let { data ->
                    data.requestedCurrencySymbol?.let { currency.set(it) }
                    data.bookingFee?.let {
                        bookingFee.set(Utilz.removeZero("$it"))
                    }
                    data.totalAmount?.let {
                        total.set("${currency.get()} ${Utilz.removeZero("${round(it, 2)}")}")
                        total_trip_cost.set(
                            "${currency.get()} ${
                                Utilz.removeZero(
                                    "${
                                        round(
                                            it,
                                            2
                                        )
                                    }"
                                )
                            }"
                        )
                    }
                    data.serviceTax?.let {
                        serviceTax.set(
                            "${currency.get()} ${
                                Utilz.removeZero(
                                    "${
                                        round(
                                            it,
                                            2
                                        )
                                    }"
                                )
                            }"
                        )
                    }
                    data.basePrice?.let { baseprice.set("${currency.get()} ${Utilz.removeZero("$it")}") }
                    data.distancePrice?.let {
                        distanceCost.set(
                            "${currency.get()} ${
                                Utilz.removeZero("${round(it, 2)}")
                            }"
                        )
                    }

                    data.timePrice?.let {
                        if (it > 0)
                            showTimeCost.set(true)
                        else
                            showTimeCost.set(false)
                        timeCost.set("${currency.get()} ${Utilz.removeZero("${round(it, 2)}")}")
                    }
                    data.waitingCharge?.let {
                        if (it > 0)
                            showWaitingCost.set(true)
                        else
                            showWaitingCost.set(false)
                        waitingPrice.set("${currency.get()} ${Utilz.removeZero("${round(it, 2)}")}")
                    }
                    /* refferalBonus not set and hidden in ui  */
                    data.promoDiscount?.let {
                        promoBonus.set(
                            "${currency.get()} ${
                                Utilz.removeZero(
                                    "${
                                        round(
                                            it,
                                            2
                                        )
                                    }"
                                )
                            }"
                        )
                    }
                    /* serviceTax not set and hidden in ui */
                    data.cancellationFee?.let {
                        cancellation_fees.set(
                            "${currency.get()} ${
                                Utilz.removeZero(
                                    "${
                                        round(
                                            it,
                                            2
                                        )
                                    }"
                                )
                            }"
                        )
                    }
                    data.adminCommision?.let {
                        mAdminCommission.set(
                            "${currency.get()} ${
                                Utilz.removeZero(
                                    "${
                                        round(
                                            it,
                                            2
                                        )
                                    }"
                                )

                            }"
                        )
                    }

                    if (data.hillStationPrice == null || data.hillStationPrice <= 0) {
                        showHillStationPrice.set(false)
                    } else {
                        hillStationFee.set(
                            "${currency.get()} ${
                                Utilz.removeZero("${round(data.hillStationPrice, 2)}")
                            }"
                        )
                        showHillStationPrice.set(true)
                    }

                    data.driverCommision?.let {
                        mDriverCommission.set(
                            "${currency.get()} ${
                                Utilz.removeZero(
                                    "${
                                        round(
                                            it,
                                            2
                                        )
                                    }"
                                )
                            }"
                        )
                    }

                    if (bill.billData.outZonePrice != null && bill.billData.outZonePrice > 0) {
                        isZone.set(true)
                        outZonetotal.set("${currency.get()} ${Utilz.removeZero("${bill.billData.outZonePrice}")}")
                    } else
                        isZone.set(false)

                }
            }


        }
    }

    fun onConfirm() {
        getNavigator().chooseDirection()
    }

    fun onClickDispute() {
        getNavigator().gotoComplaints()
    }

    private fun getImageFromS3(profilePic: String?) {
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
                withContext(Dispatchers.Main) {
                    userProfilePic.set(url.toString())
                }
            }
        }

    }

}


@BindingAdapter("invoiceUserImg")
fun setImage(imageView: ImageView, url: String?) {
    Glide.with(imageView.context).load(url)
        .apply(
            RequestOptions().error(R.drawable.profile_place_holder)
                .placeholder(R.drawable.profile_place_holder)
        )
        .into(imageView)
}
