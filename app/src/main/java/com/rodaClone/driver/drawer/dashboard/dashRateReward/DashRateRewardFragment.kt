package com.rodaClone.driver.drawer.dashboard.dashRateReward

import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.rodaClone.driver.BR
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseFragment
import com.rodaClone.driver.connection.responseModels.DashboardModel
import com.rodaClone.driver.databinding.FragmentDashRateRewardBinding
import java.util.*
import javax.inject.Inject

class DashRateRewardFragment (private val dashboardModel: DashboardModel?): BaseFragment<FragmentDashRateRewardBinding, DashRateRewardVM>(),
    DashRateRewardNavigator {

    lateinit var _binding: FragmentDashRateRewardBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    val vm by lazy {
        ViewModelProvider(this, viewModelFactory).get(DashRateRewardVM::class.java)
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
        setValues()
    }


    override fun getLayoutId() = R.layout.fragment_dash_rate_reward

    override fun getBR() = BR.viewModel

    override fun getVMClass() = vm

    fun closeFragment(){
        findNavController().popBackStack()
    }

    fun setValues(){
        dashboardModel?.acceptRatio?.let {
            vm.acceptanceRatio.set("${vm.round(it,2)}%")
            _binding.accepRateProgress.progress = it.toInt()
        }
        dashboardModel?.rating?.let {
            vm.review.set("${vm.round(it,2)}*")
        }

//        var rewardPoint = ObservableField("")

        val driverCommission: Drawable =
            _binding.accepRateProgress.progressDrawable.mutate()
        driverCommission.setColorFilter(
            ContextCompat.getColor(requireContext(),R.color.clr_00AE73),
            PorterDuff.Mode.SRC_IN
        )
        _binding.accepRateProgress.progressDrawable = driverCommission
        _binding.accepRateProgress.setScaleY(2f)
    }

}