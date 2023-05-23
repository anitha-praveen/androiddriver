package com.rodaClone.driver.drawer.complaints

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rodaClone.driver.base.BaseViewHolder
import com.rodaClone.driver.connection.responseModels.Complaint
import com.rodaClone.driver.databinding.ItemComplaintsBinding


class ComplaintListAdapter(private val complaints : MutableList<Complaint>, private val navigator: ComplaintsNavigator) :
    RecyclerView.Adapter<BaseViewHolder>() {

    var selectedSlug: String? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChildViewHolder {
        val binding = ItemComplaintsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ChildViewHolder(binding, complaints, navigator,this)
    }


    override fun getItemCount(): Int {
        return complaints.size
    }

    fun addList(complaints: List<Complaint>) {
        this.complaints.clear()
        this.complaints.addAll(complaints)
        notifyDataSetChanged()
    }

    class ChildViewHolder(
        private val mBinding: ItemComplaintsBinding,
        private val complaints: MutableList<Complaint>,
        private val navigator: ComplaintsNavigator,
        private val adapter: ComplaintListAdapter
    ) :
        BaseViewHolder(mBinding.root), ComplaintListAdapterVM.AdapterViewModelListener {
        private var adapterVM: ComplaintListAdapterVM? = null
        override fun onBind(position: Int) {
            complaints [position].isSelected = (complaints[position].slug == adapter.selectedSlug)
            val complaint: Complaint = complaints[position]
            adapterVM = ComplaintListAdapterVM(complaint, this)
            mBinding.viewModel = adapterVM
            mBinding.executePendingBindings()
        }

        override fun itemSelected(complaint: Complaint) {
            navigator.setSelected(complaint)
            adapter.selectedSlug = complaint.slug
            adapter.notifyDataSetChanged()
        }

        override fun isNetworkConnected(): Boolean {
            /*
            This method is not used in this adapter
             */
            return false
        }

        override fun showNetworkUnAvailable() {

        }



    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

}