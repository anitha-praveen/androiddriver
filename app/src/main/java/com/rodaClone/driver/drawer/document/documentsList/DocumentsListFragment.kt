package com.rodaClone.driver.drawer.document.documentsList

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.rodaClone.driver.BR
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseFragment
import com.rodaClone.driver.connection.responseModels.GroupDocument
import com.rodaClone.driver.databinding.FragmentDocumentListBinding
import com.rodaClone.driver.drawer.DrawerActivity
import com.rodaClone.driver.drawer.document.documentsList.adapter.DocumentsListAdapter
import com.rodaClone.driver.ut.Config
import com.rodaClone.driver.ut.SessionMaintainence
import java.util.ArrayList
import javax.inject.Inject

class DocumentsListFragment : BaseFragment<FragmentDocumentListBinding, DocumentsListVM>(),
    DocumentsListNavigator {

    lateinit var _binding: FragmentDocumentListBinding
    var adapter: DocumentsListAdapter? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    val vm by lazy {
        ViewModelProvider(this, viewModelFactory).get(DocumentsListVM::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                closeFragment()
            }
        })
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            getUpdatedDocList,
            IntentFilter(Config.NOTIFY_DOC_UPLOADED)
        )
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(getUpdatedDocList
        )
        super.onDestroy()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = getmBinding()
        vm.setNavigator(this)
        _binding.backImg.setOnClickListener {closeFragment()}
        setupAdapter()
    }

    private val getUpdatedDocList: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
           //vm.getDocumentList()
        }
    }


    override fun getLayoutId() = R.layout.fragment_document_list

    override fun getBR() = BR.viewModel

    override fun getVMClass() = vm

    fun closeFragment(){
        if (vm.session.getString(SessionMaintainence.ReqID)?.isEmpty() == true)
            (requireActivity() as DrawerActivity).reqProgress()
        else
            (requireActivity() as DrawerActivity).reqProgress()
    }

    private fun setupAdapter() {
        adapter = DocumentsListAdapter(ArrayList<GroupDocument>(), this,vm.translationModel!!)
        val mLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        _binding.documentListRecycler.layoutManager = mLayoutManager
        _binding.documentListRecycler.itemAnimator = DefaultItemAnimator()
        _binding.documentListRecycler.adapter = adapter
        vm.getGroupDocument()
    }

    override fun onDocumentSelected(document: GroupDocument) {
        val bundle = Bundle()
        bundle.putSerializable(Config.groupDocument_b,document)
        findNavController().navigate(R.id.uploadDocument,bundle)
    }

    override fun addList(documents: List<GroupDocument>?) {
        adapter!!.addList(documents!!)
    }

    override fun callReqProgDrawerAct() {
        ( requireActivity() as DrawerActivity).navigateFirstTabWithClearStack()
    }

}