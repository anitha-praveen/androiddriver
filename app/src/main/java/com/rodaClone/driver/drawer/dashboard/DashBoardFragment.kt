package com.rodaClone.driver.drawer.dashboard

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.rodaClone.driver.BR
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseFragment
import com.rodaClone.driver.connection.responseModels.DashboardModel
import com.rodaClone.driver.databinding.FragmentDashboardBinding
import com.rodaClone.driver.drawer.DrawerActivity
import com.rodaClone.driver.drawer.dashboard.dashBalance.DashBalanceFragment
import com.rodaClone.driver.drawer.dashboard.dashFine.DashFineFragment
import com.rodaClone.driver.drawer.dashboard.dashHistory.DashHistoryFragment
import com.rodaClone.driver.ut.SessionMaintainence
import javax.inject.Inject

var dashModel : DashboardModel? = null
class DashBoardFragment : BaseFragment<FragmentDashboardBinding, DashBoardVM>(),
    DashBoardNavigator {

    lateinit var binding: FragmentDashboardBinding
    private var pagerAdapter: ViewPagerAdapter? = null
    var tabTitles : MutableList<String> = mutableListOf()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    val vm by lazy {
        ViewModelProvider(this, viewModelFactory).get(DashBoardVM::class.java)
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
        binding = getmBinding()
        vm.setNavigator(this)
        binding.backImg.setOnClickListener { closeFragment() }
        vm.getDashDetailsBase()
    }

    override fun setup(){
        if (!isAdded) return
        pagerAdapter = ViewPagerAdapter(childFragmentManager, lifecycle)
        tabTitles.add(vm.translationModel?.txt_earnings ?:"")
        tabTitles.add(vm.translationModel?.text_history ?:"")
//        tabTitles.add(vm.translationModel?.txt_benefits?:"")
        tabTitles.add(vm.translationModel?.text_balance ?:"")
        binding.viewPager.adapter =pagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }


    override fun getLayoutId() = R.layout.fragment_dashboard

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
            return 3
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> DashBalanceFragment(dashModel)
                1 -> DashHistoryFragment(dashModel)
//                2 -> DashRateRewardFragment(dashModel)
                else -> DashFineFragment(dashModel)
            }

        }
    }

}