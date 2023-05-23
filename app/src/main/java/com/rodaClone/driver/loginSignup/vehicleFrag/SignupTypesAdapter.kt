package com.rodaClone.driver.loginSignup.vehicleFrag

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rodaClone.driver.R
import com.rodaClone.driver.loginSignup.vehicleFrag.model.SignupTypeData

class SignupTypesAdapter(
    var context: FragmentActivity,
    var signupTypeList: ArrayList<SignupTypeData?>,
    var mNavigator: VehicleNavigator
) : RecyclerView.Adapter<SignupTypesAdapter.ViewHolder>() {

    var clicked_position: Int = -1


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var typeName: TextView = itemView.findViewById(R.id.type_name)
        var typeImg: ImageView = itemView.findViewById(R.id.image)
        var btmView: View = itemView.findViewById(R.id.btm_view)
        var typeCard: View = itemView.findViewById(R.id.type_Card)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.types_item_lay, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.typeName.text = signupTypeList.get(position)!!.vehicleName
        Glide.with(context).load(signupTypeList.get(position)!!.image).into(holder.typeImg)

        if (clicked_position != -1) {
            if (signupTypeList.get(position)!!.id == clicked_position)
                holder.btmView.visibility = View.VISIBLE
            else holder.btmView.visibility = View.INVISIBLE
        }

        holder.typeCard.setOnClickListener {
            clicked_position = signupTypeList.get(position)!!.id!!
            mNavigator.vehicleSlug(signupTypeList.get(position)!!)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return signupTypeList.size
    }
}

