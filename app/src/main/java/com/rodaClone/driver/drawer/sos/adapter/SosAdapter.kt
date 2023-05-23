package com.rodaClone.driver.drawer.sos.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rodaClone.driver.base.BaseViewHolder
import com.rodaClone.driver.connection.responseModels.Sos
import com.rodaClone.driver.databinding.ChildSosBinding
import com.rodaClone.driver.drawer.sos.SosNavigator

class SosAdapter(private val sosList: MutableList<Sos>, private val navigator: SosNavigator) : RecyclerView.Adapter<BaseViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildViewHolder {
        val binding = ChildSosBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChildViewHolder(binding, sosList, navigator)
    }


    override fun getItemCount(): Int {
        return sosList.size
    }

    fun addList(sosList: List<Sos>) {
        this.sosList.clear()
        this.sosList.addAll(sosList)
        notifyDataSetChanged()
    }

    class ChildViewHolder(private val mBinding: ChildSosBinding, private val sosList: MutableList<Sos>, private val navigator: SosNavigator) : BaseViewHolder(mBinding.root), ChildSosVM.ChildSosItemListener {
        private var childSosVM: ChildSosVM? = null

        override fun onBind(position: Int) {
            val sos: Sos = sosList[position]
            childSosVM = ChildSosVM(sos, this)
            mBinding.viewModel = childSosVM
            mBinding.executePendingBindings()
        }

        override fun itemSelected(sos: String) {
            navigator.onPhoneClick(sos)
        }

        override fun itemDeleted(slug: String) {
            navigator.deleteSosNav(slug)
        }


        override fun isNetworkConnected(): Boolean {
            return navigator.isNetworkConnected()
        }

        override fun showNetworkUnAvailable() {

        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

}