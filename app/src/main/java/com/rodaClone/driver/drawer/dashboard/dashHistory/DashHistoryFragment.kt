package com.rodaClone.driver.drawer.dashboard.dashHistory

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.rodaClone.driver.BR
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseFragment
import com.rodaClone.driver.connection.responseModels.DashboardModel
import com.rodaClone.driver.databinding.FragmentDashHistoryBinding
import java.util.*
import javax.inject.Inject

class DashHistoryFragment(private val dashboardModel: DashboardModel?) : BaseFragment<FragmentDashHistoryBinding, DashHistoryVM>(),
    DashHistoryNavigator {

    lateinit var binding: FragmentDashHistoryBinding
    lateinit var pieChart: PieChart
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    val vm by lazy {
        ViewModelProvider(this, viewModelFactory).get(DashHistoryVM::class.java)
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
        pieChart = binding.pieChart
        setData()
    }


    override fun getLayoutId() = R.layout.fragment_dash_history

    override fun getBR() = BR.viewModel

    override fun getVMClass() = vm

    fun closeFragment(){
        findNavController().popBackStack()
    }

    private fun setData(){
        dashboardModel?.totalTrips?.isCompleted?.let {
            vm.totalTrip.set("$it")
        }
        dashboardModel?.totalTrips?.amount?.let { amt ->
            amt.cash?.let { vm.total_cash.set("${vm.round(it,2)}") }
            amt.card?.let { vm.total_card.set("${vm.round(it,2)}") }
            amt.wallet?.let { vm.total_wallet.set("${vm.round(it,2)}") }
        }
        dashboardModel?.todayTrips?.isCompleted?.let {
            vm.daily_trips.set("$it")
        }
        dashboardModel?.weeklyTrips?.isCompleted?.let {
            vm.weekly_trips.set("$it")
        }
        dashboardModel?.monthlyTrips?.isCompleted?.let {
            vm.monthly_trips.set("$it")
        }

//        var company_commision = ObservableField("")
//        var driver_commision = ObservableField("")
        dashboardModel?.totalTrips?.totalAmount?.let {
            drawChart(dashboardModel.currency ?:"",vm.round(it,2))
        }
    }


    private fun drawChart(currency: String, sum_amount: Double) {
        pieChart.description.isEnabled = false
        pieChart.legend.isEnabled = true
        pieChart.setDrawEntryLabels(true) //remove slice text
        pieChart.centerText = """$currency ${vm.round(sum_amount,2)}
     Sum Amount"""
        if (activity != null) pieChart.setCenterTextColor(ContextCompat.getColor(requireContext(),R.color.black))
        pieChart.setCenterTextSize(8f)
        pieChart.holeRadius = 50f
        pieChart.animateXY(1000, 1000)
        val yvalues = ArrayList<PieEntry>()
        val yvalues11 = ArrayList<Int>()
        val legends: MutableList<LegendEntry> = ArrayList()
        pieChart.legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        pieChart.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        pieChart.legend.orientation = Legend.LegendOrientation.HORIZONTAL
        pieChart.legend.setDrawInside(false)
        if (dashboardModel?.totalTrips?.amount?.cash != null && dashboardModel.totalTrips.amount.cash > 0) {
            yvalues.add(
                PieEntry(
                    vm.round(dashboardModel.totalTrips.amount.cash , 2).toFloat(),
                    "",
                    0
                )
            )
            yvalues11.add(ColorTemplate.rgb("#3DD6C2"))
            val cash = LegendEntry()
            cash.label = vm.translationModel?.txt_cash
            cash.formColor = ColorTemplate.rgb("#3DD6C2")
            legends.add(cash)
        }
        if (dashboardModel?.totalTrips?.amount?.card != null && dashboardModel.totalTrips.amount.card > 0) {
            yvalues.add(
                PieEntry(
                    vm.round( dashboardModel.totalTrips.amount.card,2).toFloat(),
                    "",
                    1
                )
            )
            yvalues11.add(ColorTemplate.rgb("#FEB561"))
            val card = LegendEntry()
            card.label = "Card"
            card.formColor = ColorTemplate.rgb("#FEB561")
            legends.add(card)
        }
        if (dashboardModel?.totalTrips?.amount?.wallet != null && dashboardModel.totalTrips.amount.wallet > 0) {
            yvalues.add(
                PieEntry(
                    vm.round( dashboardModel.totalTrips.amount.wallet,2).toFloat(),
                    "",
                    2
                )
            )
            yvalues11.add(ColorTemplate.rgb("#F05A50"))
            val wallet = LegendEntry()
            wallet.label = "Wallet"
            wallet.formColor = ColorTemplate.rgb("#F05A50")
            legends.add(wallet)
        }
        if (legends.size > 0) pieChart.legend.setCustom(legends)
        val dataSet = PieDataSet(yvalues, "")
        dataSet.colors = yvalues11
        val data = PieData(dataSet)
        data.setValueTextSize(12f)
        data.setValueTextColor(Color.BLACK)
        pieChart.data = data
    }

}