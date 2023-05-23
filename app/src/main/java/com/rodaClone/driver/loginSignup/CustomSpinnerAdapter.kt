package com.rodaClone.driver.loginSignup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rodaClone.driver.R
import com.rodaClone.driver.connection.responseModels.CompanyModel
import com.rodaClone.driver.connection.responseModels.Vehicles
import com.rodaClone.driver.loginSignup.signup.SignupFragNavigator
import com.rodaClone.driver.loginSignup.vehicleFrag.VehicleNavigator
import com.rodaClone.driver.loginSignup.vehicleFrag.model.SignupServiceLocData


class CustomSpinnerAdapter : RecyclerView.Adapter<CustomSpinnerAdapter.ViewHolder> {
    private var signupServiceLocList: List<SignupServiceLocData> = ArrayList()
    private var compList: MutableList<CompanyModel.Company> = ArrayList()
    private var modelList: MutableList<Vehicles.Vehiclemodel> = ArrayList()
    var navigator: Any? = null
    var id = -1
    var mode = -1

    internal constructor(
        signupServiceLocList: ArrayList<SignupServiceLocData>,
        navigator: SignupFragNavigator?,
        previouslySelected: String
    ) {
        mode = 0
        this.signupServiceLocList = signupServiceLocList
        this.navigator = navigator
        this.signupServiceLocList.forEachIndexed { index, element ->
            if (element.slug == previouslySelected)
                id = index
        }

    }

    internal constructor(
        companyList: MutableList<CompanyModel.Company>,
        navigator: SignupFragNavigator?,
        previouslySelected: String
    ) {
        mode = 1
        compList = companyList
        this.navigator = navigator
        compList.forEachIndexed { index, element ->
            if (element.slug == previouslySelected)
                id = index
        }
    }

    internal constructor(
        modelList: MutableList<Vehicles.Vehiclemodel>,
        navigator: VehicleNavigator?,
        previouslySelected: String
    ) {
        mode = 2
        this.modelList = modelList
        this.navigator = navigator
        this.modelList.forEachIndexed { index, element ->
            if (element.slug == previouslySelected)
                id = index
        }
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val itemView: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.custom_spinner_item, viewGroup, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        if (mode == 0) {
            viewHolder.text.text = signupServiceLocList[i].zoneName
            if (id == i) {
                viewHolder.selected.visibility = View.VISIBLE
            } else {
                viewHolder.selected.visibility = View.GONE
            }
        } else if(mode == 1){
            viewHolder.text.text = compList[i].firstname
            if (id == i) {
                viewHolder.selected.visibility = View.VISIBLE
            } else {
                viewHolder.selected.visibility = View.GONE
            }
        }else{
            viewHolder.text.text = modelList[i].modelName
            if (id == i) {
                viewHolder.selected.visibility = View.VISIBLE
            } else {
                viewHolder.selected.visibility = View.GONE
            }
        }

        viewHolder.root.setOnClickListener {
            if (navigator is SignupFragNavigator) {
                if (mode == 0)
                    (navigator as SignupFragNavigator).setServiceLocation(signupServiceLocList[i])
                else if (mode == 1)
                    (navigator as SignupFragNavigator).setSelectedCompany(compList[i])
                id = i
                notifyDataSetChanged()
            }else if(navigator is VehicleNavigator)
                (navigator as VehicleNavigator).setSelectedVehicleModel(modelList[i])
        }


    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var text: TextView = itemView.findViewById(R.id.text)
        var selected: ImageView = itemView.findViewById(R.id.selected)
        var root: RelativeLayout = itemView.findViewById(R.id.root)
    }

    override fun getItemCount(): Int {
        return if (mode == 0) signupServiceLocList.size
        else if(mode == 1) compList.size
        else modelList.size
    }
}