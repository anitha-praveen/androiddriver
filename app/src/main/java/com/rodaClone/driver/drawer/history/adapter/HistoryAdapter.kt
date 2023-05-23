package com.rodaClone.driver.drawer.history.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.rodaClone.driver.R
import com.rodaClone.driver.connection.TranslationModel
import com.rodaClone.driver.connection.responseModels.HistoryModel
import com.rodaClone.driver.connection.responseModels.RequestResponseData
import com.rodaClone.driver.drawer.history.cancelledHistory.CancelledHistoryNavigator
import com.rodaClone.driver.drawer.history.completedHistory.CompletedHistoryNavigator
import com.rodaClone.driver.ut.SessionMaintainence
import com.rodaClone.driver.ut.Utilz
import kotlinx.coroutines.*
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


class HistoryAdapter(
    private val historyList: MutableList<HistoryModel.History>,
    private val session: SessionMaintainence,
    private val navigator: Any,
    private val translationModel: TranslationModel,
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateTime: TextView = view.findViewById(R.id.txt_history_date_time)
        val requestNumber: TextView = view.findViewById(R.id.history_tax_num)
        val totalAmount: TextView = view.findViewById(R.id.txt_total_amt)
        val pickAddress: TextView = view.findViewById(R.id.txt_address_from_history)
        val dropAddress: TextView = view.findViewById(R.id.txt_address_to_history)
        val profileImage: ImageView = view.findViewById(R.id.profile_history_helper)
        val relativeLayout: RelativeLayout = view.findViewById(R.id.lay_two)
        val disputeTxt:TextView = view.findViewById(R.id.dispute_txt)
        val date:TextView = view.findViewById(R.id.txt_history_date_time)
        val time:TextView = view.findViewById(R.id.time)
        val rideType:TextView = view.findViewById(R.id.ride_type)

    }


//    class ChildViewHolder(
//        private val mBinding: HistoryListHelperBinding,
//        private val historyList: MutableList<HistoryModel.History>,
//        private val mode: Int,
//        private val navigator: Any
//    ) : BaseViewHolder(mBinding.root), ItemHistoryVM.HistoryItemListener {
//
//        private var itemHistoryVM: ItemHistoryVM? = null
//
//        override fun onBind(position: Int) {
//            val history: HistoryModel.History = historyList[position]
//            itemHistoryVM = history.data?.let {
//                ItemHistoryVM(history,this)
//            }
//            mBinding.viewModel = itemHistoryVM
//            mBinding.executePendingBindings()
//        }
//
//        override fun itemSelected(history:RequestResponseData) {
////            when (mode) {
////                0 /* Completed */ -> (navigator as CompletedHistoryNavigator).invoice(history)
////                else /* Cancelled */ -> (navigator as CancelledHistoryNavigator).invoice(history)
////            }
//            if(navigator is CompletedHistoryNavigator)
//                navigator.invoice(history)
//            else
//                (navigator as CancelledHistoryNavigator).invoice(history)
//        }
//
//
//        override fun isNetworkConnected(): Boolean {
//            return   if(navigator is CompletedHistoryNavigator)
//                navigator.isNetworkConnected()
//            else
//                (navigator as CancelledHistoryNavigator).isNetworkConnected()
//        }
//
//        override fun showNetworkUnAvailable() {
//
//        }
//
//
//    }


    fun addData(listItems: MutableList<HistoryModel.History>) {
        var size = this.historyList.size
        this.historyList.addAll(listItems)
        var sizeNew = this.historyList.size
        notifyItemRangeChanged(size, sizeNew)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val item = LayoutInflater.from(parent.context)
            .inflate(R.layout.history_list_helper, parent, false)
        return ViewHolder(item)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var date = ""
        var time = ""
        if (historyList[position].data!!.tripStartTime != null) {
            try {
                @SuppressLint("SimpleDateFormat") val mDate =
                    historyList[position].data!!.tripStartTime?.let {
                        SimpleDateFormat("dd-MM-yyy hh:mm:ss").parse(
                            it
                        )
                    }
                val mtime = historyList[position].data!!.tripStartTime?.let {
                    SimpleDateFormat("dd-MM-yyy hh:mm:ss").parse(it)
                }
                if (mDate != null )
                    date =
                        (SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(mDate))
                else
                    date = ""
                if (mtime != null)
                    time = (SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(mtime))
            } catch (e: Exception) {
                e.printStackTrace()
                date= historyList[position].data!!.tripStartTime!!
                time = historyList[position].data!!.tripStartTime!!
            }
        } else {
            date = ""
            time = ""
        }

        var reqAmount = ""
        if (historyList[position].data?.requestBill == null || historyList[position].data!!.requestBill!!.billData?.totalAmount.toString()
                .isEmpty()
        ) reqAmount = ""
        else reqAmount =
            historyList[position].data!!.requestedCurrencySymbol + " " + Utilz.removeZero("${historyList[position].data!!.requestBill!!.billData?.totalAmount}")
        holder.disputeTxt.text = translationModel.txt_dispute
        holder.disputeTxt.visibility =  if(historyList[position].data?.disputeStatus == 1)View.VISIBLE else View.GONE
        holder.totalAmount.text = reqAmount
        holder.requestNumber.text = historyList[position].data?.requestNumber
        holder.pickAddress.text = historyList[position].data?.pickAddress
        holder.dropAddress.text = historyList[position].data?.dropAddress
        holder.date.text = date
        holder.time.text = time
        holder.rideType.text = if (historyList[position].data?.serviceCategory.equals("LOCAL"))"" else historyList[position].data?.serviceCategory

//        getImageFromS3(historyList[position].data?.user?.profilePic, holder)

        Glide.with(holder.itemView.context).load(historyList[position].data?.user?.profilePic)
            .apply(
                RequestOptions().error(R.drawable.profile_place_holder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_prof_avatar).circleCrop()
            )
            .into(holder.profileImage)



        //val url =


        holder.relativeLayout.setOnClickListener {
            itemSelected(historyList[position].data!!)
        }
        holder.disputeTxt.setOnClickListener{
            historyList[position].data?.id?.let { it1 -> gotComplaints(it1) }
        }


    }

    private fun getImageFromS3(image: String?, holder: ViewHolder) {
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
                    image,
                    oneHourLater
                )
                withContext(Dispatchers.Main) {
                    // update here in UI
                    Glide.with(holder.itemView.context).load(url)
                        .apply(
                            RequestOptions().error(R.drawable.profile_place_holder)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .placeholder(R.drawable.ic_prof_avatar).circleCrop()
                        )
                        .into(holder.profileImage)
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return historyList.size
    }

   private fun itemSelected(history: RequestResponseData) {
//            when (mode) {
//                0 /* Completed */ -> (navigator as CompletedHistoryNavigator).invoice(history)
//                else /* Cancelled */ -> (navigator as CancelledHistoryNavigator).invoice(history)
//            }
        if (navigator is CompletedHistoryNavigator)
            navigator.invoice(history)
        else
            (navigator as CancelledHistoryNavigator).invoice(history)
    }
    private fun gotComplaints(id:String){
        if (navigator is CompletedHistoryNavigator)
            navigator.goToComplaints(id)
    }


}
