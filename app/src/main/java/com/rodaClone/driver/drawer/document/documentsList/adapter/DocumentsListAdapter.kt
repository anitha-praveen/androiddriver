package com.rodaClone.driver.drawer.document.documentsList.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rodaClone.driver.base.BaseViewHolder
import com.rodaClone.driver.connection.TranslationModel
import com.rodaClone.driver.connection.responseModels.GroupDocument
import com.rodaClone.driver.databinding.ChildDocumentBinding
import com.rodaClone.driver.drawer.document.documentsList.DocumentsListNavigator

class DocumentsListAdapter(
    private val documentList: MutableList<GroupDocument>,
    private val navigator: DocumentsListNavigator,
    private val translationModel: TranslationModel,
) :
    RecyclerView.Adapter<BaseViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChildViewHolder {
        val binding = ChildDocumentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ChildViewHolder(binding)
    }


    override fun getItemCount() = documentList.size

    fun addList(typeList: List<GroupDocument>) {
        documentList.clear()
        documentList.addAll(typeList)
        notifyDataSetChanged()
    }

    inner class ChildViewHolder(
        private val mBinding: ChildDocumentBinding,
    ) : BaseViewHolder(mBinding.root), ChildDocsVM.ChildDocumentItemListener {

        private var childTypesVM: ChildDocsVM? = null

        override fun onBind(position: Int) {
            val types: GroupDocument = documentList[position]
            var isRequired = false
            var showExpiryDenied = false
            var expiredDenied = ""
            for (doc in types.getDocument) {
                if (doc.requried == 1) {
                    isRequired = true
                    break
                }
            }
            for(doc in types.getDocument){
                if (doc.deniedStatus == true){
                    showExpiryDenied = true
                    expiredDenied = translationModel.txt_denied ?:""
                    break
                }else if(types.expiryStatus == 1){
                    showExpiryDenied = true
                    expiredDenied = translationModel.txt_expired
                    break
                }else{
                    showExpiryDenied = false
                }
            }
            //childTypesVM = ChildDocsVM(types, this, translationModel, isRequired,showExpiryDenied,expiredDenied)
            childTypesVM = ChildDocsVM(types, this, translationModel, isRequired,false,"false")
            mBinding.viewModel = childTypesVM
            mBinding.executePendingBindings()
        }

        override fun itemSelected(document: GroupDocument) {
            navigator.onDocumentSelected(document)
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