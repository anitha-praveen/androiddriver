package com.rodaClone.driver.drawer.history

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.rodaClone.driver.BR
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseFragment
import com.rodaClone.driver.databinding.FragmentHistoryBinding
import com.rodaClone.driver.drawer.DrawerActivity
import com.rodaClone.driver.drawer.history.cancelledHistory.CancelledHistoryFragment
import com.rodaClone.driver.drawer.history.completedHistory.CompletedHistoryList
import com.rodaClone.driver.ut.Config
import com.rodaClone.driver.ut.SessionMaintainence
import javax.inject.Inject

class HistoryFragment : BaseFragment<FragmentHistoryBinding, HistoryVM>(),
    HistoryNavigator {

    lateinit var binding: FragmentHistoryBinding
    var tabTitles: MutableList<String> = mutableListOf()
    private var pagerAdapter: ViewPagerAdapter? = null
    private val goToInvoice: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            requireActivity().runOnUiThread {
               // val mode = 1
                val req_id = intent.getStringExtra("REQUEST_DATA_ID")
                val bundle= bundleOf(Config.mode to 1,Config.request_id to req_id)
//                val action = HistoryFragmentDirections.historyToInvoice(
//                    reqId = req_id.toString(),
//                    mode = mode
//                )
                if (findNavController().currentDestination?.label == "History")
                    findNavController().navigate(R.id.history_to_invoice,bundle)
            }
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    val vm by lazy {
        ViewModelProvider(this, viewModelFactory).get(HistoryVM::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                closeFragment()
            }
        })
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            goToInvoice,
            IntentFilter(Config.RECEIVE_DIRECTION_INVOICE)

        )

    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(goToInvoice)
        super.onDestroy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getmBinding()
        vm.setNavigator(this)
        setup()
    }

    private fun setup() {
        binding.backImg.setOnClickListener { closeFragment() }
        tabTitles.add(vm.translationModel?.txt_completed ?: "Completed")
        tabTitles.add(vm.translationModel?.txt_cancelled ?: "Cancelled")
        pagerAdapter = ViewPagerAdapter(childFragmentManager, lifecycle)
        binding.viewPager.adapter = pagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }


    override fun getLayoutId() = R.layout.fragment_history

    override fun getBR() = BR.viewModel

    override fun getVMClass() = vm

    fun closeFragment() {
        if (vm.session.getString(SessionMaintainence.ReqID)?.isEmpty() == true)
            findNavController().popBackStack()
        else
            (requireActivity() as DrawerActivity).reqProgress()
    }

    class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fragmentManager, lifecycle) {

        override fun getItemCount(): Int {
            return 2
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> CompletedHistoryList()
                else -> CancelledHistoryFragment()
            }
        }
    }

}