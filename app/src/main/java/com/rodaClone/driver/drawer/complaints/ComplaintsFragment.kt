package com.rodaClone.driver.drawer.complaints

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.rodaClone.driver.BR
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseFragment
import com.rodaClone.driver.connection.responseModels.Complaint
import com.rodaClone.driver.databinding.FragmentComplaintsBinding
import com.rodaClone.driver.drawer.DrawerActivity
import com.rodaClone.driver.ut.Config
import com.rodaClone.driver.ut.SessionMaintainence
import javax.inject.Inject

class ComplaintsFragment : BaseFragment<FragmentComplaintsBinding, ComplaintsVM>(),
    ComplaintsNavigator {

    lateinit var _binding: FragmentComplaintsBinding
    //private val args: ComplaintsFragmentArgs by navArgs()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    val vm by lazy {
        ViewModelProvider(this, viewModelFactory).get(ComplaintsVM::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                closeFragment()
            }
        })

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = getmBinding()
        vm.setNavigator(this)
        _binding.back.setOnClickListener { closeFragment() }
        arguments?.let {
            vm.mode = it.getInt(Config.mode)//args.mode
            vm.reqId = it.getString(Config.request_id).toString()// args.requestId
        }

        setAdapter()
    }


    override fun getLayoutId() = R.layout.fragment_complaints

    override fun getBR() = BR.viewModel

    override fun getVMClass() = vm

    fun closeFragment() {
        if (vm.session.getString(SessionMaintainence.ReqID)?.isEmpty() == true)
            findNavController().popBackStack()
        else
            (requireActivity() as DrawerActivity).reqProgress()
    }

    var adapter: ComplaintListAdapter? = null
    private fun setAdapter(){
        val mLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapter = ComplaintListAdapter(ArrayList(), this)
        _binding.listRecycler.layoutManager = mLayoutManager
        _binding.listRecycler.itemAnimator = DefaultItemAnimator()
        _binding.listRecycler.adapter = adapter

        if(vm.mode==1)
            vm.getComplaintsListOfTrip()
        else
            vm.getComplaintsList()

    }

    override fun addItems(data: List<Complaint>) {
        adapter?.addList(data)
    }

    override fun setSelected(data: Complaint) {
        data.slug?.let { vm.selectedSlug = it }
    }

    override fun getCtx(): Context {
        return (context?:activity?.applicationContext) as Context
    }

    override fun getBinds(): FragmentComplaintsBinding {
        return _binding
    }

    override fun hideKeyFomNav() {
        hideKeyboard(requireActivity())
    }

}