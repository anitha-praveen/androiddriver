package com.rodaClone.driver.drawer.refferal

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.rodaClone.driver.BR
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseFragment
import com.rodaClone.driver.databinding.FragmentReferralBinding
import com.rodaClone.driver.drawer.DrawerActivity
import com.rodaClone.driver.ut.SessionMaintainence
import javax.inject.Inject

class ReferralFragment : BaseFragment<FragmentReferralBinding, ReferralVM>(),
    ReferralNavigator {

    lateinit var _binding: FragmentReferralBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    val vm by lazy {
        ViewModelProvider(this, viewModelFactory).get(ReferralVM::class.java)
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
        _binding.backImg.setOnClickListener { closeFragment() }
        vm.getReferalApi()
    }


    override fun getLayoutId() = R.layout.fragment_referral

    override fun getBR() = BR.viewModel

    override fun getVMClass() = vm

    fun closeFragment() {
        if (vm.session.getString(SessionMaintainence.ReqID)?.isEmpty() == true)
            findNavController().popBackStack()
        else
            (requireActivity() as DrawerActivity).reqProgress()
    }

    override fun onClickShare() {
        val refText: String =
            requireActivity().getString(R.string.text_play_url) + requireActivity().packageName.toString() +
                    "\n " + requireActivity().getString(R.string.text_sharequote) +
                    "\n " + requireActivity().getString(R.string.text_note_referral) + vm.code.get()

        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, refText)
        sendIntent.type = "text/plain"

        startActivity(
            Intent.createChooser(
                sendIntent,
                requireActivity().getString(R.string.text_share_ref)
            )
        )
    }

}