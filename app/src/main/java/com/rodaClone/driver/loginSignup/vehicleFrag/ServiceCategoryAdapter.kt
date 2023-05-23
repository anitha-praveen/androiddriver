package com.rodaClone.driver.loginSignup.vehicleFrag


import android.content.Context
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View

import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.rodaClone.driver.R
import com.rodaClone.driver.loginSignup.vehicleFrag.model.ServiceTypes


class ServiceCategoryAdapter(modelList: MutableList<ServiceTypes>? , val ctx : Context) :
    RecyclerView.Adapter<ServiceCategoryAdapter.MyViewHolder>() {
    val mModelList: MutableList<ServiceTypes>? = modelList
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.service_types_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = mModelList!![position]
        holder.textView.text = model.text
        holder.checkBx.background = (if (model.isSelected) ContextCompat.getDrawable(ctx, R.drawable.chkbox_tick)  else ContextCompat.getDrawable(ctx, R.drawable.rect_chkbox) )
        holder.root.setOnClickListener {
            model.isSelected = (!model.isSelected)
            holder.checkBx.background = (if (model.isSelected) ContextCompat.getDrawable(ctx, R.drawable.chkbox_tick)  else ContextCompat.getDrawable(ctx, R.drawable.rect_chkbox))
        }
    }

    override fun getItemCount(): Int {
        return mModelList?.size ?: 0
    }

    inner class MyViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val checkBx : ImageView = itemView.findViewById(R.id.chkbox)
        val textView: TextView = itemView.findViewById(R.id.text_view)
        val root: RelativeLayout = itemView.findViewById(R.id.root)
    }

}