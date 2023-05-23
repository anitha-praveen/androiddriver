package com.rodaClone.driver.drawer.notification.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.rodaClone.driver.R
import com.rodaClone.driver.connection.responseModels.NotificationData

class NotificationAdapter(private val list: ArrayList<NotificationData>): RecyclerView.Adapter<NotificationAdapter.ViewModel>() {
    class ViewModel(view: View): RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title_nh)
        val message: TextView = view.findViewById(R.id.message_nh)
        val showImage:ImageView = view.findViewById(R.id.image_view_notification)
        val date:TextView = view.findViewById(R.id.date_notification)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewModel {
        val item = LayoutInflater.from(parent.context)
            .inflate(R.layout.notification_helper, parent, false)
        return ViewModel(item)
    }

    override fun onBindViewHolder(holder: ViewModel, position: Int) {
        holder.title.text = list[position].title
        holder.message.text = list[position].message
        holder.date.text = list[position].date
        val imageUrl = list[position].images1
        if (imageUrl != null) {
            Glide.with(holder.itemView.context).load(imageUrl).apply(
                RequestOptions.circleCropTransform().error(R.drawable.ic_user_customers)
                    .placeholder(R.drawable.ic_user_customers)
            ).into(holder.showImage)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    fun addData(listItems: ArrayList<NotificationData>) {
        var size = this.list.size
        this.list.addAll(listItems)
        var sizeNew = this.list.size
        notifyItemRangeChanged(size, sizeNew)
    }
}