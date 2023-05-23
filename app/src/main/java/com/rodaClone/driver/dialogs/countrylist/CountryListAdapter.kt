package com.rodaClone.driver.dialogs.countrylist

import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.rodaClone.driver.R
import com.rodaClone.driver.connection.responseModels.Country
import com.rodaClone.driver.ut.Config
import java.util.*

class CountryListAdapter(
    var context: Context,
    var data: List<Country>,
    var countryListNavigator: CountryListNavigator
) :
    RecyclerView.Adapter<CountryListAdapter.ViewHolder>(), TextWatcher {
    var filteredDataList: List<Country>
    var primaryDataList: List<Country> = data
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val itemView: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.country_list_item, viewGroup, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.countryName.setText(filteredDataList[i].name)
        viewHolder.countryCode.text = "(" + filteredDataList[i].dialCode.toString() + ")"
    }

    override fun getItemCount(): Int {
        return filteredDataList.size
    }

    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
    override fun afterTextChanged(editable: Editable) {
        if (primaryDataList.isNotEmpty()) {
            val strFilterable = editable.toString()
            val newFilteredList: MutableList<Country> = ArrayList<Country>()
            if (!TextUtils.isEmpty(editable.toString())) {
                for (row in primaryDataList) {
                    if (row.name!!.lowercase(Locale.getDefault())
                            .startsWith(strFilterable) || row.dialCode!!.contains(strFilterable)
                    ) {
                        newFilteredList.add(row)
                    }
                }
                val intent = Intent(Config.RECEIVE_NO_ITEM_FOUND)
                if (newFilteredList.size == 0)
                    intent.putExtra(Config.RECEIVE_NO_ITEM_FOUND, true)
                else
                    intent.putExtra(Config.RECEIVE_NO_ITEM_FOUND, false)
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
                filteredDataList = newFilteredList
                notifyDataSetChanged()
            } else {
                val intent = Intent(Config.RECEIVE_NO_ITEM_FOUND)
                if (primaryDataList.isEmpty())
                    intent.putExtra(Config.RECEIVE_NO_ITEM_FOUND, true)
                else
                    intent.putExtra(Config.RECEIVE_NO_ITEM_FOUND, false)
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
                filteredDataList = primaryDataList
                notifyDataSetChanged()
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var countryName: TextView = itemView.findViewById(R.id.country_name)
        var countryCode: TextView = itemView.findViewById(R.id.countryCode)
        var countryFlag: ImageView? = null

        init {
            itemView.setOnClickListener {
                countryListNavigator.clickedItem(filteredDataList[adapterPosition])
            }
        }
    }

    init {
        filteredDataList = data
    }
}
