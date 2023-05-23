package com.rodaClone.driver.drawer.subsription.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.rodaClone.driver.R
import com.rodaClone.driver.connection.SubscriptionModel
import com.rodaClone.driver.connection.TranslationModel
import com.rodaClone.driver.drawer.subsription.SubscriptionNavigator
import java.util.*


class SubscriptionAdapter(
    private val list: ArrayList<SubscriptionModel>,
    private val model: TranslationModel?,
    private val allowSubscription: Boolean,
    private val currency: String?,
    val mNavigator: SubscriptionNavigator
) : RecyclerView.Adapter<SubscriptionAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val amountTv: TextView = view.findViewById(R.id.cost_sub)
        val monthTv: TextView = view.findViewById(R.id.amount_sub)
        val descTv: TextView = view.findViewById(R.id.desc_sub)
        val planTv: TextView = view.findViewById(R.id.name_sub)
        val validTv: TextView = view.findViewById(R.id.valid_upto_sub)
        val days: TextView = view.findViewById(R.id.days_sub)
        val cardView: CardView = view.findViewById(R.id.card_sub)
        val currency_symbol: TextView = view.findViewById(R.id.currency_symbol_sub)
        val subscribeText: TextView = view.findViewById(R.id.subscribe)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.subscription_list_helper, parent, false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.amountTv.text = list[position].amount.toString()
        holder.monthTv.text = list[position].validity
        holder.descTv.text = list[position].description
        holder.planTv.text = list[position].name
        holder.validTv.text = model?.txt_valid_upto ?: ""
        holder.days.text = model?.txt_day ?: ""
        holder.currency_symbol.text = currency

        if (allowSubscription)
            holder.subscribeText.visibility = View.VISIBLE
        else holder.subscribeText.visibility = View.GONE

//        if(position==2){
//            holder.cardView.foreground =AppCompatResources.getDrawable(context,R.drawable.subscription_card_stroke)
//        }
        holder.itemView.setOnClickListener {
            mNavigator.clickedItem(list[position].slug)
        }


    }

    override fun getItemCount(): Int {
        return list.size
    }
//   fun getRandomColor(holder:ViewHolder,pos:Int): Int {
////        val rnd = Random()
////        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
//       val androidColors: IntArray = holder.itemView.resources.getIntArray(R.array.androidcolors)
//       val randomAndroidColor = androidColors[Random().nextInt(androidColors.size)]
//       return randomAndroidColor
//   }
}