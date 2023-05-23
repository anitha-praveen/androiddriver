package com.rodaClone.driver.drawer.support

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.rodaClone.driver.BR
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseFragment
import com.rodaClone.driver.databinding.FragmentSupportBinding
import com.rodaClone.driver.drawer.DrawerActivity
import com.rodaClone.driver.ut.SessionMaintainence
import javax.inject.Inject

class SupportFragment : BaseFragment<FragmentSupportBinding, SupportVM>(),
    SupportNavigator {

    lateinit var _binding: FragmentSupportBinding


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    val vm by lazy {
        ViewModelProvider(this, viewModelFactory).get(SupportVM::class.java)
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
        _binding.backImg.setOnClickListener {closeFragment()}

    }


    override fun getLayoutId() = R.layout.fragment_support

    override fun getBR() = BR.viewModel

    override fun getVMClass() = vm

    fun closeFragment(){
        val drawer = (requireActivity() as DrawerActivity)
        if (drawer.mDrawer?.isOpen == true)
            drawer.closeDrawer()
        else {
            if (vm.session.getString(SessionMaintainence.ReqID)?.isEmpty() == true)
                findNavController().popBackStack()
            else
                (requireActivity() as DrawerActivity).reqProgress()
        }
    }

    override fun complaints() {
        findNavController().navigate(R.id.support_to_complaints)
    }

    override fun sos() {
        findNavController().navigate(R.id.support_to_sos)
    }

    override fun faq() {
        findNavController().navigate(R.id.support_to_faq)
    }


}