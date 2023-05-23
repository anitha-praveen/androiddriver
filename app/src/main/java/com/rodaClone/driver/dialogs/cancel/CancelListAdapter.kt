package com.rodaClone.driver.dialogs.cancel

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rodaClone.driver.R
import com.rodaClone.driver.connection.BaseResponse

class CancelListAdapter(
    var context: Context,
    data: List<BaseResponse.CancelReason>,
    var cancelListNavigator: CancelListNavigator
) : RecyclerView.Adapter<CancelListAdapter.ViewHolder>() {

    private var primaryDataList: List<BaseResponse.CancelReason> = data
    var pos: Int = -1

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val itemView: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.cancelation_item, viewGroup, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.radioButton.isChecked = pos == i
        viewHolder.cancelItem.text = primaryDataList[i].reason
    }

    override fun getItemCount(): Int {
        return primaryDataList.size
    }

    fun getChoosedPosition(): Int {
        return pos
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cancelItem: TextView = itemView.findViewById(R.id.cancel_txt)
        var radioButton: RadioButton = itemView.findViewById(R.id.radio_cancel_desc)

        init {
            radioButton.setOnClickListener {
                pos = adapterPosition
                notifyDataSetChanged()
            }
            itemView.setOnClickListener {
                radioButton.performClick()
            }

        }
    }
}
