package com.rodaClone.driver.drawer.wallet.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.rodaClone.driver.R
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.connection.responseModels.CustomWalletModel

import com.rodaClone.driver.drawer.wallet.WalletNavigator

class WalletHistoryAdapter(private val historyList:ArrayList<CustomWalletModel>,
                           private val currType:BaseResponse.DataObjectsAllApi,private val navigator:WalletNavigator):RecyclerView.Adapter<WalletHistoryAdapter.ViewModel>() {
    inner class ViewModel(view:View):RecyclerView.ViewHolder(view) {
         val sLayout : RelativeLayout = view.findViewById(R.id.serviceCommissionLayout)
         val aLayout : RelativeLayout = view.findViewById(R.id.adminCommissionLayout)
         val nLayout : RelativeLayout = view.findViewById(R.id.normalLayout)

        val sText : TextView = view.findViewById(R.id.serviceCommissionTxt)
        val aText : TextView = view.findViewById(R.id.adminCommissionTxt)
        val nText : TextView = view.findViewById(R.id.normalTxt)

        val sAmount : TextView = view.findViewById(R.id.serviceAmount)
        val aAmount : TextView = view.findViewById(R.id.adminAmount)
        val amount : TextView = view.findViewById(R.id.amount)

        val createdAt:TextView = view.findViewById(R.id.created_at_wallet_helper)
        val cardView:CardView = view.findViewById(R.id.main_wallet_card)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WalletHistoryAdapter.ViewModel {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.wallet_histroy_list_helper, parent, false)
        return ViewModel(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: WalletHistoryAdapter.ViewModel, position: Int) {
        if(historyList[position].purpose.equals("Admin Commission",true) || historyList[position].purpose.equals("Service Tax",true)){
            holder.aLayout.visibility = View.VISIBLE
            holder.sLayout.visibility = View.VISIBLE
            holder.nLayout.visibility = View.GONE

            holder.aText.text= "Admin commission"
            holder.aAmount.text = "${currType.currency} ${historyList[position].adminCommissionAmount}"

            holder.sText.text = "Service Tax"
            holder.sAmount.text = "${currType.currency} ${historyList[position].serviceTaxAmount}"
            holder.cardView.setOnClickListener{ historyList[position].requestId?.let { it1 ->
                navigator.openInvoice(
                    it1
                )
            } }

        }else{
            holder.aLayout.visibility = View.GONE
            holder.sLayout.visibility = View.GONE
            holder.nLayout.visibility = View.VISIBLE

            holder.nText.text= historyList[position].purpose
            holder.amount.text = "${currType.currency} ${historyList[position].amount}"

        }
        holder.createdAt.text = historyList[position].date

    }

    override fun getItemCount(): Int {
        return historyList.size
    }

}