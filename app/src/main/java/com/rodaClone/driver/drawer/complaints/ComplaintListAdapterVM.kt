package com.rodaClone.driver.drawer.complaints

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.rodaClone.driver.base.BaseViewOperator
import com.rodaClone.driver.connection.responseModels.Complaint

class ComplaintListAdapterVM (
    var complaint: Complaint, adapterLister: AdapterViewModelListener
) {
    var isSelected = ObservableBoolean(complaint.isSelected)
    var complaintTitle  = ObservableField(complaint.title?:"")
    var adapterlister: AdapterViewModelListener = adapterLister

    fun onClickItem(){
        adapterlister.itemSelected(complaint)
    }
    interface AdapterViewModelListener : BaseViewOperator {
        fun itemSelected(complaint: Complaint)
    }

}
