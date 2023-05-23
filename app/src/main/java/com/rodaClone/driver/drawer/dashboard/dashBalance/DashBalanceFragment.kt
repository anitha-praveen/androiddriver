package com.rodaClone.driver.drawer.dashboard.dashBalance

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.ViewPortHandler
import com.rodaClone.driver.BR
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseFragment
import com.rodaClone.driver.connection.responseModels.DashboardModel
import com.rodaClone.driver.databinding.FragmentDashBalBinding
import com.rodaClone.driver.ut.CustomMarkerView
import java.util.ArrayList
import javax.inject.Inject

class DashBalanceFragment(private val dashboardModel: DashboardModel?) :
    BaseFragment<FragmentDashBalBinding, DashBalanceVM>(),
    DashBalanceNavigator {

    lateinit var _binding: FragmentDashBalBinding
    lateinit var chart: LineChart

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    val vm by lazy {
        ViewModelProvider(this, viewModelFactory).get(DashBalanceVM::class.java)
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
        chart = _binding.chart
        initializeValues()
    }


    override fun getLayoutId() = R.layout.fragment_dash_bal

    override fun getBR() = BR.viewModel

    override fun getVMClass() = vm

    fun closeFragment() {
        findNavController().popBackStack()
    }

    var spinDataWiseAdapter: ArrayAdapter<*>? = null
    private fun initializeValues() {
        dashboardModel?.todayTrips?.totalAmount?.let { vm.todayBal.set("${vm.round(it,2)}") }
        dashboardModel?.yesterdayTrips?.totalAmount?.let { vm.yesterdayBal.set("${vm.round(it,2)}") }
        dashboardModel?.weeklyTrips?.totalAmount?.let { vm.weekBal.set("${vm.round(it,2)}") }
        dashboardModel?.monthlyTrips?.totalAmount?.let { vm.monthBal.set("${vm.round(it,2)}") }
        dashboardModel?.totalTrips?.totalAmount?.let { vm.totalAMnt.set("${vm.round(it,2)}") }
        spinDataWiseAdapter = ArrayAdapter<Any?>(
            requireActivity(), R.layout.layout_spin_text, arrayOf<String?>(
                vm.translationModel?.txt_hourly,
                vm.translationModel?.txt_daily,
                vm.translationModel?.txt_monthly
            )
        )
        (spinDataWiseAdapter as ArrayAdapter<*>).setDropDownViewResource(R.layout.layout_spin_text)
        _binding.spinDataWise.adapter = spinDataWiseAdapter
        _binding.spinDataWise.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> {
                        /* handle hour click */
                        chart.invalidate()
                        dashboardModel?.report?.hour?.horizontalKeys?.let{ horizontalKeys ->
                            dashboardModel.report.hour.values?.let { values ->
                                    chartDemo(horizontalKeys,values,4)
                            }

                        }
                    }
                    1 -> {
                        /* handle day click */
                        chart.invalidate()
                        dashboardModel?.report?.day?.horizontalKeys?.let{ horizontalKeys ->
                            dashboardModel.report.day.values?.let { values ->
                                chartDemo(horizontalKeys,values,1)
                            }

                        }
                    }
                    2 -> {
                        /* handle month click */
                        chart.invalidate()
                        dashboardModel?.report?.month?.horizontalKeys?.let{ horizontalKeys ->
                            dashboardModel.report.month.values?.let { values ->
                                chartDemo(horizontalKeys,values,2)
                            }

                        }

                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

    }


    private fun chartDemo(
        horizontalKeys: List<String>,
        values: ArrayList<String>,
        clickAction: Int
    ) {
        chart.description.isEnabled = false
        val xAxis: XAxis = chart.getXAxis()
        xAxis.textSize = 11f
        xAxis.textColor = Color.BLACK
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.axisMinimum = 0f
        xAxis.isGranularityEnabled = true
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase): String {
                return if (clickAction != 4) horizontalKeys[value.toInt() % horizontalKeys.size].subSequence(
                    0,
                    3
                ).toString() else horizontalKeys[value.toInt() % horizontalKeys.size].subSequence(
                    0,
                    2
                ).toString()
            }
        }
        val leftAxis: YAxis = chart.getAxisLeft()
        leftAxis.textColor = Color.BLACK
        leftAxis.isGranularityEnabled = true
        val rightAxis: YAxis = chart.getAxisRight()
        rightAxis.setDrawGridLines(false)
        chart.axisRight.isEnabled = false
        chart.xAxis.setDrawLabels(true)
        chart.legend.isEnabled = false
        chart.isDragEnabled = false
        chart.setTouchEnabled(false)
        chart.setTouchEnabled(true)
        val marker1 = CustomMarkerView(requireContext(),R.layout.chart_marker_layout)
        chart.marker = marker1
        val set3: LineDataSet
        val entryValues = ArrayList<Entry>()
        for (i in values.indices) {
            entryValues.add(
                Entry(
                    i.toFloat(), java.lang.Float.valueOf(
                        values[i]
                    )
                )
            )
        }
        if (chart.data != null &&
            chart.data.dataSetCount > 0
        ) {
            set3 = chart.data.getDataSetByIndex(0) as LineDataSet
            set3.values = entryValues
            chart.getData().notifyDataChanged()
            chart.notifyDataSetChanged()
        } else {
            set3 = LineDataSet(entryValues, "Sample Data")
            set3.axisDependency = YAxis.AxisDependency.RIGHT
            if (activity != null) {
                set3.color = ContextCompat.getColor(requireContext(),R.color.amnt_clr_blue)
                set3.setCircleColor(ContextCompat.getColor(requireContext(),R.color.amnt_clr_blue))
                set3.highLightColor = ContextCompat.getColor(requireContext(),R.color.amnt_clr_blue)
            }
            set3.lineWidth = 5f
            set3.circleRadius = 5f
            set3.fillAlpha = 65
            set3.fillColor = ColorTemplate.colorWithAlpha(Color.YELLOW, 200)
            set3.setDrawCircleHole(false)
            set3.mode = LineDataSet.Mode.CUBIC_BEZIER
            set3.cubicIntensity = 0.2f
            val dataSets = ArrayList<ILineDataSet>()
            dataSets.add(set3)
            val data = LineData(dataSets)
            data.setValueFormatter(MyDecimalFormat())
            chart.data = data
            chart.invalidate()
        }
    }

    class MyDecimalFormat : ValueFormatter(),
        IValueFormatter {
        override fun getFormattedValue(
            value: Float,
            entry: Entry,
            dataSetIndex: Int,
            viewPortHandler: ViewPortHandler
        ): String {
            return value.toString()
        }
    }


}